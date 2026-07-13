package br.com.gemsbiotec.vacinacao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
import br.com.gemsbiotec.dominio.vacinacao.VacinacaoDados;
import br.com.gemsbiotec.dominio.vacinacao.VacinacaoRegistro;
import br.com.gemsbiotec.repository.MunicipioRepository;
import br.com.gemsbiotec.repository.UnidadeSaudeRepository;
import br.com.gemsbiotec.repository.UsuarioRepository;
import br.com.gemsbiotec.repository.VacinacaoDadosRepository;
import br.com.gemsbiotec.repository.VacinacaoRegistroRepository;
import br.com.gemsbiotec.saude.UnidadeSaudeService;
import br.com.gemsbiotec.vacinacao.dto.VacinacaoResumoResponse;
import br.com.gemsbiotec.vacinacao.dto.VacinacaoUnidadeResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class VacinacaoService {

    private final TenantContext tenantContext;
    private final MunicipioRepository municipioRepository;
    private final VacinacaoDadosRepository vacinacaoDadosRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final UsuarioRepository usuarioRepository;
    private final VacinacaoRegistroRepository vacinacaoRegistroRepository;
    private final UnidadeSaudeService unidadeSaudeService;

    public VacinacaoService(
            TenantContext tenantContext,
            MunicipioRepository municipioRepository,
            VacinacaoDadosRepository vacinacaoDadosRepository,
            UnidadeSaudeRepository unidadeSaudeRepository,
            UsuarioRepository usuarioRepository,
            VacinacaoRegistroRepository vacinacaoRegistroRepository,
            UnidadeSaudeService unidadeSaudeService) {
        this.tenantContext = tenantContext;
        this.municipioRepository = municipioRepository;
        this.vacinacaoDadosRepository = vacinacaoDadosRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.usuarioRepository = usuarioRepository;
        this.vacinacaoRegistroRepository = vacinacaoRegistroRepository;
        this.unidadeSaudeService = unidadeSaudeService;
    }

    public List<VacinacaoUnidadeResponse> listarUnidades() {
        Long municipioId = municipioIdObrigatorio();
        return agruparPorUnidade(vacinacaoDadosRepository.listByMunicipioOrdenado(municipioId))
                .values()
                .stream()
                .map(this::toUnidadeResponse)
                .toList();
    }

    public VacinacaoResumoResponse resumo() {
        Long municipioId = municipioIdObrigatorio();
        Municipio municipio = municipioRepository.findAtivoById(municipioId)
                .orElseThrow(() -> new NotFoundException("Municipio ativo nao encontrado."));

        List<VacinacaoUnidadeResponse> unidades = listarUnidades();

        LocalDate dataAtual = unidades.stream()
                .map(VacinacaoUnidadeResponse::dataReferenciaAtual)
                .filter(java.util.Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);
        LocalDate dataAnterior = unidades.stream()
                .map(VacinacaoUnidadeResponse::dataReferenciaAnterior)
                .filter(java.util.Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        int doses10a14 = unidades.stream().mapToInt(u -> nz(u.doses10a14Atual())).sum();
        int doses18a59 = unidades.stream().mapToInt(u -> nz(u.doses18a59Atual())).sum();
        int dosesTotal = unidades.stream().mapToInt(u -> nz(u.dosesTotalAtual())).sum();
        int dosesTotalAnterior = unidades.stream().mapToInt(u -> nz(u.dosesTotalAnterior())).sum();

        List<Double> coberturasDisponiveis = new ArrayList<>();
        int comCoberturaIndisponivel = 0;
        int comMetaAtingida = 0;
        for (VacinacaoUnidadeResponse unidade : unidades) {
            if (unidade.cobertura10a14Percentual() == null && unidade.cobertura18a59Percentual() == null) {
                comCoberturaIndisponivel++;
                continue;
            }
            if (unidade.cobertura10a14Percentual() != null) {
                coberturasDisponiveis.add(unidade.cobertura10a14Percentual());
            }
            if (unidade.cobertura18a59Percentual() != null) {
                coberturasDisponiveis.add(unidade.cobertura18a59Percentual());
            }
            if ("META_ATINGIDA".equals(unidade.statusCobertura())) {
                comMetaAtingida++;
            }
        }
        Double coberturaMedia = coberturasDisponiveis.isEmpty()
                ? null
                : Math.round(coberturasDisponiveis.stream().mapToDouble(Double::doubleValue).average().orElse(0) * 100) / 100.0;

        return new VacinacaoResumoResponse(
                municipio.getId(),
                municipio.getNome(),
                dataAtual,
                dataAnterior,
                doses10a14,
                doses18a59,
                dosesTotal,
                dosesTotal - dosesTotalAnterior,
                coberturaMedia,
                comMetaAtingida,
                unidades.size(),
                comCoberturaIndisponivel);
    }

    @jakarta.transaction.Transactional
    public VacinacaoUnidadeResponse registrarDose(br.com.gemsbiotec.vacinacao.dto.RegistrarDoseRequest request) {
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = tenantContext.getUsuarioId();

        UnidadeSaude unidade = unidadeSaudeRepository.findByIdETenant(request.unidadeSaudeId(), municipioId)
                .orElseThrow(() -> new NotFoundException("Unidade de saude nao encontrada."));

        LocalDate hoje = LocalDate.now();
        VacinacaoDados snapshot = vacinacaoDadosRepository.findByUnidadeEData(unidade.getId(), hoje)
                .orElseGet(() -> {
                    VacinacaoDados novo = new VacinacaoDados();
                    novo.setUnidadeSaude(unidade);
                    novo.setDataReferencia(hoje);
                    novo.setOrigem(br.com.gemsbiotec.dominio.vacinacao.OrigemVacinacao.MANUAL);
                    vacinacaoDadosRepository.persist(novo);
                    return novo;
                });

        if (request.faixaEtaria() == br.com.gemsbiotec.dominio.vacinacao.FaixaEtariaVacinacao.FAIXA_10_14) {
            snapshot.setDoses10a14(snapshot.getDoses10a14() + request.quantidade());
        } else {
            snapshot.setDoses18a59(snapshot.getDoses18a59() + request.quantidade());
        }
        snapshot.setDosesTotal(snapshot.getDoses10a14() + snapshot.getDoses18a59());

        VacinacaoRegistro registro = new VacinacaoRegistro();
        registro.setUnidadeSaude(unidade);
        registro.setFaixaEtaria(request.faixaEtaria());
        registro.setQuantidade(request.quantidade());
        registro.setObservacoes(request.observacoes());
        if (usuarioId != null) {
            usuarioRepository.findAtivoByIdETenant(usuarioId, municipioId).ifPresent(registro::setUsuario);
        }
        vacinacaoRegistroRepository.persist(registro);
        vacinacaoDadosRepository.flush();

        return listarUnidades().stream()
                .filter(u -> u.unidadeSaudeId().equals(unidade.getId()))
                .findFirst()
                .orElseThrow();
    }

    @jakarta.transaction.Transactional
    public br.com.gemsbiotec.vacinacao.dto.ImportarVacinacaoResponse importar(
            java.io.InputStream inputStream, LocalDate dataReferencia) throws java.io.IOException {
        Long municipioId = municipioIdObrigatorio();
        Municipio municipio = municipioRepository.findAtivoById(municipioId)
                .orElseThrow(() -> new NotFoundException("Municipio ativo nao encontrado."));

        VacinacaoCsvParser.ResultadoParseCsv resultado;
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8))) {
            resultado = VacinacaoCsvParser.parse(reader);
        }

        List<String> avisos = new ArrayList<>(resultado.avisos());
        int unidadesCasadas = 0;
        int unidadesCriadas = 0;

        for (VacinacaoCsvParser.LinhaVacinacaoCsv linha : resultado.linhas()) {
            boolean existiaAntes = unidadeSaudeRepository.findByNomeIgnoreCaseETenant(linha.unidadeNome(), municipioId).isPresent();
            UnidadeSaude unidade = obterOuCriarUnidade(linha.unidadeNome(), municipioId, municipio, avisos);
            if (unidade == null) {
                continue;
            }
            if (existiaAntes) {
                unidadesCasadas++;
            } else {
                unidadesCriadas++;
            }

            VacinacaoDados snapshot = vacinacaoDadosRepository.findByUnidadeEData(unidade.getId(), dataReferencia)
                    .orElseGet(VacinacaoDados::new);
            snapshot.setUnidadeSaude(unidade);
            snapshot.setDataReferencia(dataReferencia);
            snapshot.setDoses10a14(linha.doses10a14());
            snapshot.setDoses18a59(linha.doses18a59());
            snapshot.setDosesTotal(linha.doses10a14() + linha.doses18a59());
            snapshot.setOrigem(br.com.gemsbiotec.dominio.vacinacao.OrigemVacinacao.IMPORTACAO);
            if (snapshot.getId() == null) {
                vacinacaoDadosRepository.persist(snapshot);
            }
        }
        vacinacaoDadosRepository.flush();

        return new br.com.gemsbiotec.vacinacao.dto.ImportarVacinacaoResponse(
                dataReferencia, unidadesCasadas, unidadesCriadas,
                resultado.linhas().size(), resultado.linhasInvalidas(), avisos);
    }

    private UnidadeSaude obterOuCriarUnidade(String nome, Long municipioId, Municipio municipio, List<String> avisos) {
        return unidadeSaudeRepository.findByNomeIgnoreCaseETenant(nome, municipioId)
                .orElseGet(() -> {
                    if (municipio.getLatitude() == null || municipio.getLongitude() == null) {
                        avisos.add("Unidade '" + nome + "' nao encontrada e nao pode ser criada: "
                                + "municipio sem coordenadas de referencia (recalcule o centroide antes de importar).");
                        return null;
                    }
                    br.com.gemsbiotec.saude.dto.CriarUnidadeSaudeRequest request =
                            new br.com.gemsbiotec.saude.dto.CriarUnidadeSaudeRequest(
                                    nome, br.com.gemsbiotec.dominio.saude.TipoUnidadeSaude.UBS,
                                    null, null, null, null, null, null, null,
                                    municipio.getLatitude(), municipio.getLongitude());
                    br.com.gemsbiotec.saude.dto.UnidadeSaudeResponse criada = unidadeSaudeService.criar(request);
                    return unidadeSaudeRepository.findById(criada.id());
                });
    }

    private Map<Long, List<VacinacaoDados>> agruparPorUnidade(List<VacinacaoDados> registros) {
        Map<Long, List<VacinacaoDados>> porUnidade = new LinkedHashMap<>();
        for (VacinacaoDados registro : registros) {
            porUnidade.computeIfAbsent(registro.getUnidadeSaude().getId(), key -> new ArrayList<>()).add(registro);
        }
        return porUnidade;
    }

    private VacinacaoUnidadeResponse toUnidadeResponse(List<VacinacaoDados> snapshotsOrdenados) {
        VacinacaoDados atual = snapshotsOrdenados.get(0);
        VacinacaoDados anterior = snapshotsOrdenados.size() > 1 ? snapshotsOrdenados.get(1) : null;
        UnidadeSaude unidade = atual.getUnidadeSaude();
        Bairro bairro = unidade.getBairro();

        Integer populacaoAlvo10a14 = bairro != null ? bairro.getMoradores10a14Anos() : null;
        Integer populacaoAlvo18a59 = bairro != null
                ? VacinacaoCoberturaCalculator.populacaoAlvo18a59(
                        bairro.getMoradores20a24Anos(),
                        bairro.getMoradores25a29Anos(),
                        bairro.getMoradores30a39Anos(),
                        bairro.getMoradores40a49Anos(),
                        bairro.getMoradores50a59Anos())
                : null;

        Double cobertura10a14 = VacinacaoCoberturaCalculator.coberturaPercentual(atual.getDoses10a14(), populacaoAlvo10a14);
        Double cobertura18a59 = VacinacaoCoberturaCalculator.coberturaPercentual(atual.getDoses18a59(), populacaoAlvo18a59);
        Integer meta10a14 = VacinacaoCoberturaCalculator.meta90(populacaoAlvo10a14);
        Integer meta18a59 = VacinacaoCoberturaCalculator.meta90(populacaoAlvo18a59);

        Double coberturaRepresentativa = cobertura10a14 != null && cobertura18a59 != null
                ? Math.min(cobertura10a14, cobertura18a59)
                : (cobertura10a14 != null ? cobertura10a14 : cobertura18a59);

        return new VacinacaoUnidadeResponse(
                unidade.getId(),
                unidade.getNome(),
                bairro != null ? bairro.getId() : null,
                bairro != null ? bairro.getNome() : null,
                atual.getDataReferencia(),
                atual.getDoses10a14(),
                atual.getDoses18a59(),
                atual.getDosesTotal(),
                anterior != null ? anterior.getDataReferencia() : null,
                anterior != null ? anterior.getDosesTotal() : null,
                anterior != null ? atual.getDosesTotal() - anterior.getDosesTotal() : null,
                cobertura10a14,
                cobertura18a59,
                meta10a14,
                meta18a59,
                VacinacaoCoberturaCalculator.faltamParaMeta(atual.getDoses10a14(), meta10a14),
                VacinacaoCoberturaCalculator.faltamParaMeta(atual.getDoses18a59(), meta18a59),
                VacinacaoCoberturaCalculator.statusCampanha(coberturaRepresentativa));
    }

    private int nz(Integer value) {
        return value == null ? 0 : value;
    }

    private Long municipioIdObrigatorio() {
        Long municipioId = tenantContext.getMunicipioId();
        if (municipioId == null) {
            throw new NotAuthorizedException("Municipio nao encontrado no token.");
        }
        return municipioId;
    }
}
