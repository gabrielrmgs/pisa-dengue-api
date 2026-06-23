package br.com.gemsbiotec.triagem;

import java.util.List;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.ras.PerfilAssistencialDengue;
import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
import br.com.gemsbiotec.dominio.triagem.StatusTriagemDengue;
import br.com.gemsbiotec.dominio.triagem.TriagemDengue;
import br.com.gemsbiotec.dominio.usuario.Role;
import br.com.gemsbiotec.dominio.usuario.Usuario;
import br.com.gemsbiotec.encaminhamento.EncaminhamentoSaudeService;
import br.com.gemsbiotec.ras.MatrizRasService;
import br.com.gemsbiotec.ras.dto.DestinoMatrizRasResponse;
import br.com.gemsbiotec.ras.dto.SimulacaoMatrizRasResponse;
import br.com.gemsbiotec.ras.dto.SimularMatrizRasRequest;
import br.com.gemsbiotec.repository.TriagemDengueRepository;
import br.com.gemsbiotec.repository.UnidadeSaudeRepository;
import br.com.gemsbiotec.repository.UsuarioRepository;
import br.com.gemsbiotec.repository.UsuarioUnidadeSaudeRepository;
import br.com.gemsbiotec.triagem.dto.CriarTriagemDengueRequest;
import br.com.gemsbiotec.triagem.dto.TriagemDengueResponse;
import br.com.gemsbiotec.triagem.dto.TriagemUnidadeResumoResponse;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class TriagemDengueService {

    private final TenantContext tenantContext;
    private final TriagemDengueRepository triagemRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatrizRasService matrizRasService;
    private final EncaminhamentoSaudeService encaminhamentoService;
    private final UsuarioUnidadeSaudeRepository usuarioUnidadeRepository;
    private final SecurityIdentity securityIdentity;

    public TriagemDengueService(
            TenantContext tenantContext,
            TriagemDengueRepository triagemRepository,
            UnidadeSaudeRepository unidadeSaudeRepository,
            UsuarioRepository usuarioRepository,
            MatrizRasService matrizRasService,
            EncaminhamentoSaudeService encaminhamentoService,
            UsuarioUnidadeSaudeRepository usuarioUnidadeRepository,
            SecurityIdentity securityIdentity) {
        this.tenantContext = tenantContext;
        this.triagemRepository = triagemRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.usuarioRepository = usuarioRepository;
        this.matrizRasService = matrizRasService;
        this.encaminhamentoService = encaminhamentoService;
        this.usuarioUnidadeRepository = usuarioUnidadeRepository;
        this.securityIdentity = securityIdentity;
    }

    public List<TriagemDengueResponse> listarRecentes(Integer limite) {
        int limiteSeguro = limite == null ? 50 : Math.max(1, Math.min(limite, 100));
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = usuarioIdObrigatorio();
        return triagemRepository.listRecentesByTenant(municipioId, limiteSeguro).stream()
                .filter(triagem -> podeVisualizar(triagem, usuarioId, municipioId))
                .map(triagem -> toResponse(triagem, null))
                .toList();
    }

    public TriagemDengueResponse buscar(Long id) {
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = usuarioIdObrigatorio();
        TriagemDengue triagem = triagemRepository.findByIdETenant(id, municipioId)
                .orElseThrow(() -> new NotFoundException("Triagem de dengue nao encontrada."));
        if (!podeVisualizar(triagem, usuarioId, municipioId)) {
            throw new ForbiddenException("Usuario sem acesso a esta triagem.");
        }
        return toResponse(triagem, null);
    }

    @Transactional
    public TriagemDengueResponse criar(CriarTriagemDengueRequest request) {
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = usuarioIdObrigatorio();
        UnidadeSaude unidadeOrigem = unidadeSaudeRepository.findByIdETenant(request.unidadeOrigemId(), municipioId)
                .orElseThrow(() -> new NotFoundException("Unidade de origem nao encontrada."));
        exigirAcessoUnidade(unidadeOrigem.getId(), usuarioId, municipioId);
        Usuario usuario = usuarioRepository.findAtivoByIdETenant(usuarioId, municipioId)
                .orElseThrow(() -> new NotAuthorizedException("Usuario nao encontrado no municipio logado."));

        PerfilAssistencialDengue classificacao = classificar(request);
        SimulacaoMatrizRasResponse simulacao = matrizRasService.simular(
                new SimularMatrizRasRequest(unidadeOrigem.getId(), classificacao));
        UnidadeSaude destinoSugerido = resolverDestinoSugerido(simulacao, unidadeOrigem, municipioId);

        TriagemDengue triagem = new TriagemDengue();
        triagem.setMunicipio(unidadeOrigem.getMunicipio());
        triagem.setUnidadeOrigem(unidadeOrigem);
        triagem.setUsuarioTriagem(usuario);
        triagem.setUnidadeDestinoSugerida(destinoSugerido);
        triagem.setPacienteIdentificacao(normalizarTextoObrigatorio(request.pacienteIdentificacao()));
        triagem.setPacienteIdade(request.pacienteIdade());
        triagem.setPacienteSexo(normalizarTexto(request.pacienteSexo()));
        triagem.setDiasSintomas(request.diasSintomas());
        aplicarSinais(triagem, request);
        triagem.setObservacoes(normalizarTexto(request.observacoes()));
        triagem.setClassificacao(classificacao);
        triagem.setSuspeitaConsistente(suspeitaConsistente(request));
        triagem.setAtendeNaOrigem(simulacao.atendeNaOrigem());
        triagem.setEncaminhamentoNecessario(simulacao.encaminhamentoNecessario());
        triagem.setExigeRegulacao(simulacao.exigeRegulacao());
        triagem.setRecomendacao(simulacao.recomendacao());
        triagem.setStatus(status(simulacao));

        triagemRepository.persist(triagem);
        triagemRepository.flush();
        encaminhamentoService.criarAutomatico(triagem, destinoSugerido);
        return toResponse(triagem, simulacao);
    }

    private void exigirAcessoUnidade(Long unidadeId, Long usuarioId, Long municipioId) {
        if (isAdminOuGestor()) {
            return;
        }
        if (!usuarioUnidadeRepository.existsByUsuarioUnidadeETenant(usuarioId, unidadeId, municipioId)) {
            throw new ForbiddenException("Usuario sem vinculo com a unidade de origem.");
        }
    }

    private boolean podeVisualizar(TriagemDengue triagem, Long usuarioId, Long municipioId) {
        return isAdminOuGestor()
                || usuarioUnidadeRepository.existsByUsuarioUnidadeETenant(
                        usuarioId, triagem.getUnidadeOrigem().getId(), municipioId)
                || (triagem.getUnidadeDestinoSugerida() != null
                        && usuarioUnidadeRepository.existsByUsuarioUnidadeETenant(
                                usuarioId, triagem.getUnidadeDestinoSugerida().getId(), municipioId));
    }

    private boolean isAdminOuGestor() {
        return securityIdentity.hasRole(Role.ADMIN.name()) || securityIdentity.hasRole(Role.GESTOR.name());
    }

    private PerfilAssistencialDengue classificar(CriarTriagemDengueRequest request) {
        if (verdadeiro(request.choque()) || verdadeiro(request.sangramentoGrave()) || verdadeiro(request.disfuncaoOrganica())) {
            return PerfilAssistencialDengue.GRUPO_D;
        }
        if (possuiSinalAlarme(request)) {
            return PerfilAssistencialDengue.GRUPO_C;
        }
        if (verdadeiro(request.lactente()) || verdadeiro(request.gestante()) || verdadeiro(request.idoso())
                || verdadeiro(request.comorbidade()) || verdadeiro(request.riscoSocial()) || verdadeiro(request.sangramentoPele())) {
            return PerfilAssistencialDengue.GRUPO_B;
        }
        return PerfilAssistencialDengue.GRUPO_A;
    }

    private boolean possuiSinalAlarme(CriarTriagemDengueRequest request) {
        return verdadeiro(request.dorAbdominalIntensa())
                || verdadeiro(request.vomitosPersistentes())
                || verdadeiro(request.acumuloLiquidos())
                || verdadeiro(request.hipotensaoPosturalLipotimia())
                || verdadeiro(request.hepatomegalia())
                || verdadeiro(request.sangramentoMucosa())
                || verdadeiro(request.letargiaIrritabilidade())
                || verdadeiro(request.aumentoHematocrito());
    }

    private boolean suspeitaConsistente(CriarTriagemDengueRequest request) {
        int sintomas = 0;
        sintomas += verdadeiro(request.cefaleia()) ? 1 : 0;
        sintomas += verdadeiro(request.dorRetroorbital()) ? 1 : 0;
        sintomas += verdadeiro(request.mialgia()) ? 1 : 0;
        sintomas += verdadeiro(request.artralgia()) ? 1 : 0;
        sintomas += verdadeiro(request.exantema()) ? 1 : 0;
        sintomas += verdadeiro(request.nauseaVomito()) ? 1 : 0;
        sintomas += verdadeiro(request.provaLacoPositiva()) ? 1 : 0;
        return verdadeiro(request.febre()) && sintomas >= 2;
    }

    private UnidadeSaude resolverDestinoSugerido(
            SimulacaoMatrizRasResponse simulacao,
            UnidadeSaude unidadeOrigem,
            Long municipioId) {
        if (simulacao.destinosCompativeis().isEmpty()) {
            return null;
        }
        DestinoMatrizRasResponse escolhido = simulacao.encaminhamentoNecessario()
                ? simulacao.destinosCompativeis().stream()
                        .filter(destino -> !destino.unidadeOrigem())
                        .findFirst()
                        .orElse(simulacao.destinosCompativeis().getFirst())
                : simulacao.destinosCompativeis().stream()
                        .filter(DestinoMatrizRasResponse::unidadeOrigem)
                        .findFirst()
                        .orElse(simulacao.destinosCompativeis().getFirst());
        if (escolhido.unidade().id().equals(unidadeOrigem.getId())) {
            return unidadeOrigem;
        }
        return unidadeSaudeRepository.findByIdETenant(escolhido.unidade().id(), municipioId).orElse(null);
    }

    private StatusTriagemDengue status(SimulacaoMatrizRasResponse simulacao) {
        if (simulacao.destinosCompativeis().isEmpty()) {
            return StatusTriagemDengue.SEM_DESTINO_COMPATIVEL;
        }
        if (simulacao.encaminhamentoNecessario()) {
            return StatusTriagemDengue.ENCAMINHAMENTO_SUGERIDO;
        }
        return StatusTriagemDengue.ATENDIMENTO_LOCAL_RECOMENDADO;
    }

    private void aplicarSinais(TriagemDengue triagem, CriarTriagemDengueRequest request) {
        triagem.setFebre(verdadeiro(request.febre()));
        triagem.setCefaleia(verdadeiro(request.cefaleia()));
        triagem.setDorRetroorbital(verdadeiro(request.dorRetroorbital()));
        triagem.setMialgia(verdadeiro(request.mialgia()));
        triagem.setArtralgia(verdadeiro(request.artralgia()));
        triagem.setExantema(verdadeiro(request.exantema()));
        triagem.setNauseaVomito(verdadeiro(request.nauseaVomito()));
        triagem.setProvaLacoPositiva(verdadeiro(request.provaLacoPositiva()));
        triagem.setDorAbdominalIntensa(verdadeiro(request.dorAbdominalIntensa()));
        triagem.setVomitosPersistentes(verdadeiro(request.vomitosPersistentes()));
        triagem.setAcumuloLiquidos(verdadeiro(request.acumuloLiquidos()));
        triagem.setHipotensaoPosturalLipotimia(verdadeiro(request.hipotensaoPosturalLipotimia()));
        triagem.setHepatomegalia(verdadeiro(request.hepatomegalia()));
        triagem.setSangramentoMucosa(verdadeiro(request.sangramentoMucosa()));
        triagem.setLetargiaIrritabilidade(verdadeiro(request.letargiaIrritabilidade()));
        triagem.setAumentoHematocrito(verdadeiro(request.aumentoHematocrito()));
        triagem.setChoque(verdadeiro(request.choque()));
        triagem.setSangramentoGrave(verdadeiro(request.sangramentoGrave()));
        triagem.setDisfuncaoOrganica(verdadeiro(request.disfuncaoOrganica()));
        triagem.setLactente(verdadeiro(request.lactente()));
        triagem.setGestante(verdadeiro(request.gestante()));
        triagem.setIdoso(verdadeiro(request.idoso()));
        triagem.setComorbidade(verdadeiro(request.comorbidade()));
        triagem.setRiscoSocial(verdadeiro(request.riscoSocial()));
        triagem.setSangramentoPele(verdadeiro(request.sangramentoPele()));
    }

    private TriagemDengueResponse toResponse(TriagemDengue triagem, SimulacaoMatrizRasResponse simulacao) {
        Usuario usuario = triagem.getUsuarioTriagem();
        return new TriagemDengueResponse(
                triagem.getId(),
                triagem.getPacienteIdentificacao(),
                triagem.getPacienteIdade(),
                triagem.getPacienteSexo(),
                triagem.getDiasSintomas(),
                triagem.getClassificacao(),
                triagem.getStatus(),
                verdadeiro(triagem.getSuspeitaConsistente()),
                verdadeiro(triagem.getAtendeNaOrigem()),
                verdadeiro(triagem.getEncaminhamentoNecessario()),
                verdadeiro(triagem.getExigeRegulacao()),
                triagem.getRecomendacao(),
                toUnidadeResumo(triagem.getUnidadeOrigem()),
                toUnidadeResumo(triagem.getUnidadeDestinoSugerida()),
                usuario != null ? usuario.getId() : null,
                usuario != null ? usuario.getNome() : null,
                triagem.getCriadoEm(),
                simulacao);
    }

    private TriagemUnidadeResumoResponse toUnidadeResumo(UnidadeSaude unidade) {
        if (unidade == null) {
            return null;
        }
        Bairro bairro = unidade.getBairro();
        return new TriagemUnidadeResumoResponse(
                unidade.getId(),
                unidade.getNome(),
                unidade.getTipo(),
                bairro != null ? bairro.getId() : null,
                bairro != null ? bairro.getNome() : null);
    }

    private Long municipioIdObrigatorio() {
        Long municipioId = tenantContext.getMunicipioId();
        if (municipioId == null) {
            throw new NotAuthorizedException("Municipio nao encontrado no token.");
        }
        return municipioId;
    }

    private Long usuarioIdObrigatorio() {
        Long usuarioId = tenantContext.getUsuarioId();
        if (usuarioId == null) {
            throw new NotAuthorizedException("Usuario nao encontrado no token.");
        }
        return usuarioId;
    }

    private boolean verdadeiro(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    private String normalizarTextoObrigatorio(String value) {
        return value.trim();
    }

    private String normalizarTexto(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
