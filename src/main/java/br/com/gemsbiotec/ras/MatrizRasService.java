package br.com.gemsbiotec.ras;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.ras.PerfilAssistencialDengue;
import br.com.gemsbiotec.dominio.saude.CapacidadeAssistencial;
import br.com.gemsbiotec.dominio.saude.UnidadeCapacidade;
import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
import br.com.gemsbiotec.ras.dto.CapacidadeMatrizRasResponse;
import br.com.gemsbiotec.ras.dto.DestinoMatrizRasResponse;
import br.com.gemsbiotec.ras.dto.PerfilMatrizRasResponse;
import br.com.gemsbiotec.ras.dto.SimulacaoMatrizRasResponse;
import br.com.gemsbiotec.ras.dto.SimularMatrizRasRequest;
import br.com.gemsbiotec.ras.dto.UnidadeMatrizRasResponse;
import br.com.gemsbiotec.repository.UnidadeCapacidadeRepository;
import br.com.gemsbiotec.repository.UnidadeSaudeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class MatrizRasService {

    private static final Map<String, String> NOMES_CAPACIDADES = Map.ofEntries(
            Map.entry("AVALIACAO_DENGUE", "Avaliação clínica de dengue"),
            Map.entry("HEMOGRAMA", "Hemograma"),
            Map.entry("HIDRATACAO_ORAL", "Hidratação oral"),
            Map.entry("HIDRATACAO_VENOSA", "Hidratação venosa"),
            Map.entry("OBSERVACAO", "Leito de observação"),
            Map.entry("ATENDIMENTO_24H", "Atendimento 24h"),
            Map.entry("INTERNACAO", "Internação"),
            Map.entry("EMERGENCIA", "Emergência"),
            Map.entry("UTI", "UTI"),
            Map.entry("SUPORTE_TRANSFUSIONAL", "Suporte transfusional"));

    private final TenantContext tenantContext;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final UnidadeCapacidadeRepository unidadeCapacidadeRepository;
    private final Map<PerfilAssistencialDengue, PerfilConfig> matriz = new EnumMap<>(PerfilAssistencialDengue.class);

    public MatrizRasService(
            TenantContext tenantContext,
            UnidadeSaudeRepository unidadeSaudeRepository,
            UnidadeCapacidadeRepository unidadeCapacidadeRepository) {
        this.tenantContext = tenantContext;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.unidadeCapacidadeRepository = unidadeCapacidadeRepository;
        inicializarMatriz();
    }

    public List<PerfilMatrizRasResponse> listarPerfis() {
        return matriz.values().stream()
                .map(this::toPerfilResponse)
                .toList();
    }

    public SimulacaoMatrizRasResponse simular(SimularMatrizRasRequest request) {
        Long municipioId = municipioIdObrigatorio();
        PerfilConfig perfil = matriz.get(request.perfil());
        UnidadeSaude origem = unidadeSaudeRepository.findByIdETenant(request.unidadeOrigemId(), municipioId)
                .orElseThrow(() -> new NotFoundException("Unidade de origem nao encontrada."));

        List<UnidadeCapacidade> vinculos = unidadeCapacidadeRepository.listDisponiveisByMunicipio(municipioId);
        Map<Long, UnidadeCapacidadesDisponiveis> unidades = montarMapaCapacidades(vinculos);

        UnidadeCapacidadesDisponiveis origemComCapacidades = unidades.get(origem.getId());
        boolean atendeNaOrigem = origemComCapacidades != null
                && origemComCapacidades.atendeTodos(perfil.capacidadesObrigatorias());

        List<DestinoMatrizRasResponse> destinos = unidades.values().stream()
                .filter(unidade -> unidade.atendeTodos(perfil.capacidadesObrigatorias()))
                .map(unidade -> toDestino(unidade, origem, perfil))
                .sorted(Comparator
                        .comparing(DestinoMatrizRasResponse::unidadeOrigem).reversed()
                        .thenComparing(Comparator.comparing(DestinoMatrizRasResponse::capacidadesDesejaveisAtendidas).reversed())
                        .thenComparing(destino -> destino.distanciaKm() == null ? Double.MAX_VALUE : destino.distanciaKm())
                        .thenComparing(destino -> destino.unidade().nome(), String.CASE_INSENSITIVE_ORDER))
                .toList();

        boolean encaminhamentoNecessario = !atendeNaOrigem;
        return new SimulacaoMatrizRasResponse(
                toPerfilResponse(perfil),
                toUnidadeResponse(origem),
                atendeNaOrigem,
                encaminhamentoNecessario,
                perfil.exigeRegulacao(),
                recomendacao(perfil, atendeNaOrigem, destinos),
                destinos);
    }

    private Map<Long, UnidadeCapacidadesDisponiveis> montarMapaCapacidades(List<UnidadeCapacidade> vinculos) {
        Map<Long, UnidadeCapacidadesDisponiveis> unidades = new LinkedHashMap<>();
        for (UnidadeCapacidade vinculo : vinculos) {
            UnidadeSaude unidade = vinculo.getUnidadeSaude();
            CapacidadeAssistencial capacidade = vinculo.getCapacidade();
            unidades.computeIfAbsent(unidade.getId(), ignored -> new UnidadeCapacidadesDisponiveis(unidade))
                    .adicionar(capacidade);
        }
        return unidades;
    }

    private DestinoMatrizRasResponse toDestino(
            UnidadeCapacidadesDisponiveis unidade,
            UnidadeSaude origem,
            PerfilConfig perfil) {
        List<CapacidadeMatrizRasResponse> obrigatorias = unidade.capacidades(perfil.capacidadesObrigatorias());
        List<CapacidadeMatrizRasResponse> desejaveis = unidade.capacidades(perfil.capacidadesDesejaveis());
        return new DestinoMatrizRasResponse(
                toUnidadeResponse(unidade.unidade()),
                unidade.unidade().getId().equals(origem.getId()),
                distanciaKm(origem, unidade.unidade()),
                desejaveis.size(),
                perfil.capacidadesDesejaveis().size(),
                obrigatorias,
                desejaveis);
    }

    private String recomendacao(
            PerfilConfig perfil,
            boolean atendeNaOrigem,
            List<DestinoMatrizRasResponse> destinos) {
        if (atendeNaOrigem) {
            if (perfil.exigeRegulacao()) {
                return "A unidade de origem possui as capacidades obrigatorias, mas o perfil exige regulacao antes da transferencia/internacao.";
            }
            return "A unidade de origem possui as capacidades obrigatorias para este perfil.";
        }
        if (destinos.isEmpty()) {
            return "Nenhuma unidade ativa possui todas as capacidades obrigatorias. Revise o cadastro de capacidades ou acione o fluxo externo de regulacao.";
        }
        if (perfil.exigeRegulacao()) {
            return "Encaminhar para uma unidade compativel apos aceite da unidade destino e regulacao do caso.";
        }
        return "Encaminhar para uma unidade compativel apos aceite da unidade destino.";
    }

    private PerfilMatrizRasResponse toPerfilResponse(PerfilConfig perfil) {
        return new PerfilMatrizRasResponse(
                perfil.perfil(),
                perfil.nome(),
                perfil.descricao(),
                perfil.exigeRegulacao(),
                toCapacidades(perfil.capacidadesObrigatorias()),
                toCapacidades(perfil.capacidadesDesejaveis()));
    }

    private List<CapacidadeMatrizRasResponse> toCapacidades(List<String> codigos) {
        return codigos.stream()
                .map(codigo -> new CapacidadeMatrizRasResponse(codigo, NOMES_CAPACIDADES.getOrDefault(codigo, codigo)))
                .toList();
    }

    private UnidadeMatrizRasResponse toUnidadeResponse(UnidadeSaude unidade) {
        Bairro bairro = unidade.getBairro();
        return new UnidadeMatrizRasResponse(
                unidade.getId(),
                unidade.getNome(),
                unidade.getTipo(),
                bairro != null ? bairro.getId() : null,
                bairro != null ? bairro.getNome() : null,
                unidade.getEndereco(),
                unidade.getLatitude(),
                unidade.getLongitude());
    }

    private Double distanciaKm(UnidadeSaude origem, UnidadeSaude destino) {
        if (origem.getLatitude() == null || origem.getLongitude() == null
                || destino.getLatitude() == null || destino.getLongitude() == null) {
            return null;
        }
        double earthRadiusKm = 6371.0;
        double lat1 = Math.toRadians(origem.getLatitude());
        double lat2 = Math.toRadians(destino.getLatitude());
        double deltaLat = Math.toRadians(destino.getLatitude() - origem.getLatitude());
        double deltaLon = Math.toRadians(destino.getLongitude() - origem.getLongitude());
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(earthRadiusKm * c * 10.0) / 10.0;
    }

    private Long municipioIdObrigatorio() {
        Long municipioId = tenantContext.getMunicipioId();
        if (municipioId == null) {
            throw new NotAuthorizedException("Municipio nao encontrado no token.");
        }
        return municipioId;
    }

    private void inicializarMatriz() {
        adicionar(new PerfilConfig(
                PerfilAssistencialDengue.GRUPO_A,
                "Grupo A - manejo ambulatorial",
                "Dengue sem sinais de alarme, sem condicoes especiais ou risco social relevante.",
                false,
                List.of("AVALIACAO_DENGUE", "HIDRATACAO_ORAL"),
                List.of("HEMOGRAMA")));
        adicionar(new PerfilConfig(
                PerfilAssistencialDengue.GRUPO_B,
                "Grupo B - exames e observacao",
                "Dengue sem sinais de alarme, mas com condicao especial, comorbidade ou necessidade de reavaliacao.",
                false,
                List.of("AVALIACAO_DENGUE", "HEMOGRAMA", "OBSERVACAO"),
                List.of("HIDRATACAO_ORAL", "HIDRATACAO_VENOSA")));
        adicionar(new PerfilConfig(
                PerfilAssistencialDengue.GRUPO_C,
                "Grupo C - urgencia com sinais de alarme",
                "Presenca de sinais de alarme, exigindo estabilizacao, hidratacao venosa e observacao qualificada.",
                true,
                List.of("AVALIACAO_DENGUE", "HIDRATACAO_VENOSA", "OBSERVACAO", "ATENDIMENTO_24H"),
                List.of("HEMOGRAMA", "INTERNACAO")));
        adicionar(new PerfilConfig(
                PerfilAssistencialDengue.GRUPO_D,
                "Grupo D - emergencia/alta complexidade",
                "Choque, sangramento grave ou disfuncao organica, exigindo resposta emergencial e rede regulada.",
                true,
                List.of("EMERGENCIA", "HIDRATACAO_VENOSA", "ATENDIMENTO_24H"),
                List.of("INTERNACAO", "UTI", "SUPORTE_TRANSFUSIONAL")));
    }

    private void adicionar(PerfilConfig perfil) {
        matriz.put(perfil.perfil(), perfil);
    }

    private record PerfilConfig(
            PerfilAssistencialDengue perfil,
            String nome,
            String descricao,
            boolean exigeRegulacao,
            List<String> capacidadesObrigatorias,
            List<String> capacidadesDesejaveis) {
    }

    private static final class UnidadeCapacidadesDisponiveis {
        private final UnidadeSaude unidade;
        private final Map<String, CapacidadeAssistencial> capacidades = new LinkedHashMap<>();

        private UnidadeCapacidadesDisponiveis(UnidadeSaude unidade) {
            this.unidade = unidade;
        }

        private UnidadeSaude unidade() {
            return unidade;
        }

        private void adicionar(CapacidadeAssistencial capacidade) {
            capacidades.put(capacidade.getCodigo(), capacidade);
        }

        private boolean atendeTodos(List<String> codigos) {
            return capacidades.keySet().containsAll(codigos);
        }

        private List<CapacidadeMatrizRasResponse> capacidades(List<String> codigos) {
            return codigos.stream()
                    .filter(capacidades::containsKey)
                    .map(codigo -> {
                        CapacidadeAssistencial capacidade = capacidades.get(codigo);
                        return new CapacidadeMatrizRasResponse(capacidade.getCodigo(), capacidade.getNome());
                    })
                    .toList();
        }
    }
}
