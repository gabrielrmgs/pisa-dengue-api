package br.com.gemsbiotec.encaminhamento;

import java.time.LocalDateTime;
import java.util.List;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.encaminhamento.EncaminhamentoSaude;
import br.com.gemsbiotec.dominio.encaminhamento.StatusEncaminhamentoSaude;
import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
import br.com.gemsbiotec.dominio.triagem.TriagemDengue;
import br.com.gemsbiotec.dominio.usuario.Role;
import br.com.gemsbiotec.dominio.usuario.Usuario;
import br.com.gemsbiotec.encaminhamento.dto.EncaminhamentoSaudeResponse;
import br.com.gemsbiotec.encaminhamento.dto.ResponderEncaminhamentoRequest;
import br.com.gemsbiotec.repository.EncaminhamentoSaudeRepository;
import br.com.gemsbiotec.repository.UsuarioRepository;
import br.com.gemsbiotec.repository.UsuarioUnidadeSaudeRepository;
import br.com.gemsbiotec.triagem.dto.TriagemUnidadeResumoResponse;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class EncaminhamentoSaudeService {

    private final TenantContext tenantContext;
    private final SecurityIdentity securityIdentity;
    private final EncaminhamentoSaudeRepository encaminhamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioUnidadeSaudeRepository usuarioUnidadeRepository;

    public EncaminhamentoSaudeService(
            TenantContext tenantContext,
            SecurityIdentity securityIdentity,
            EncaminhamentoSaudeRepository encaminhamentoRepository,
            UsuarioRepository usuarioRepository,
            UsuarioUnidadeSaudeRepository usuarioUnidadeRepository) {
        this.tenantContext = tenantContext;
        this.securityIdentity = securityIdentity;
        this.encaminhamentoRepository = encaminhamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioUnidadeRepository = usuarioUnidadeRepository;
    }

    public List<EncaminhamentoSaudeResponse> listar(String caixa, Integer limite) {
        int limiteSeguro = limite == null ? 80 : Math.max(1, Math.min(limite, 150));
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = usuarioIdObrigatorio();
        List<EncaminhamentoSaude> todos = encaminhamentoRepository.listByTenant(municipioId, caixa, limiteSeguro);
        return todos.stream()
                .filter(e -> podeVisualizar(e, usuarioId, municipioId))
                .filter(e -> filtrarCaixa(e, caixa, usuarioId, municipioId))
                .map(this::toResponse)
                .toList();
    }

    public EncaminhamentoSaudeResponse buscar(Long id) {
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = usuarioIdObrigatorio();
        EncaminhamentoSaude encaminhamento = buscarEntidade(id, municipioId);
        if (!podeVisualizar(encaminhamento, usuarioId, municipioId)) {
            throw new ForbiddenException("Usuario sem acesso a este encaminhamento.");
        }
        return toResponse(encaminhamento);
    }

    @Transactional
    public EncaminhamentoSaude criarAutomatico(TriagemDengue triagem, UnidadeSaude destino) {
        if (destino == null || destino.getId().equals(triagem.getUnidadeOrigem().getId())
                || !Boolean.TRUE.equals(triagem.getEncaminhamentoNecessario())) {
            return null;
        }
        if (encaminhamentoRepository.findAtivoByTriagem(triagem.getId(), triagem.getMunicipio().getId()).isPresent()) {
            return null;
        }

        EncaminhamentoSaude encaminhamento = new EncaminhamentoSaude();
        encaminhamento.setMunicipio(triagem.getMunicipio());
        encaminhamento.setTriagem(triagem);
        encaminhamento.setUnidadeOrigem(triagem.getUnidadeOrigem());
        encaminhamento.setUnidadeDestino(destino);
        encaminhamento.setUsuarioSolicitante(triagem.getUsuarioTriagem());
        encaminhamento.setStatus(StatusEncaminhamentoSaude.PENDENTE_ACEITE);
        encaminhamento.setExigeRegulacao(Boolean.TRUE.equals(triagem.getExigeRegulacao()));
        encaminhamento.setSolicitadoEm(LocalDateTime.now());
        encaminhamentoRepository.persist(encaminhamento);
        return encaminhamento;
    }

    @Transactional
    public EncaminhamentoSaudeResponse aceitar(Long id, ResponderEncaminhamentoRequest request) {
        EncaminhamentoSaude encaminhamento = buscarParaResposta(id);
        if (!StatusEncaminhamentoSaude.PENDENTE_ACEITE.equals(encaminhamento.getStatus())) {
            throw new BadRequestException("Somente encaminhamentos pendentes podem ser aceitos.");
        }
        encaminhamento.setStatus(StatusEncaminhamentoSaude.ACEITO);
        preencherResposta(encaminhamento, request != null ? request.observacao() : null, null);
        return toResponse(encaminhamento);
    }

    @Transactional
    public EncaminhamentoSaudeResponse recusar(Long id, ResponderEncaminhamentoRequest request) {
        EncaminhamentoSaude encaminhamento = buscarParaResposta(id);
        if (!StatusEncaminhamentoSaude.PENDENTE_ACEITE.equals(encaminhamento.getStatus())) {
            throw new BadRequestException("Somente encaminhamentos pendentes podem ser recusados.");
        }
        String justificativa = normalizar(request != null ? request.justificativa() : null);
        if (justificativa == null) {
            throw new BadRequestException("A justificativa da recusa e obrigatoria.");
        }
        encaminhamento.setStatus(StatusEncaminhamentoSaude.RECUSADO);
        preencherResposta(encaminhamento, request != null ? request.observacao() : null, justificativa);
        return toResponse(encaminhamento);
    }

    @Transactional
    public EncaminhamentoSaudeResponse concluir(Long id, ResponderEncaminhamentoRequest request) {
        EncaminhamentoSaude encaminhamento = buscarParaResposta(id);
        if (!StatusEncaminhamentoSaude.ACEITO.equals(encaminhamento.getStatus())) {
            throw new BadRequestException("Somente encaminhamentos aceitos podem ser concluidos.");
        }
        encaminhamento.setStatus(StatusEncaminhamentoSaude.CONCLUIDO);
        encaminhamento.setConcluidoEm(LocalDateTime.now());
        if (request != null && normalizar(request.observacao()) != null) {
            encaminhamento.setObservacaoResposta(normalizar(request.observacao()));
        }
        return toResponse(encaminhamento);
    }

    @Transactional
    public EncaminhamentoSaudeResponse cancelar(Long id, ResponderEncaminhamentoRequest request) {
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = usuarioIdObrigatorio();
        EncaminhamentoSaude encaminhamento = buscarEntidade(id, municipioId);
        if (!podeAtuarNaUnidade(encaminhamento.getUnidadeOrigem().getId(), usuarioId, municipioId)) {
            throw new ForbiddenException("Usuario sem permissao para cancelar pela unidade de origem.");
        }
        if (!StatusEncaminhamentoSaude.PENDENTE_ACEITE.equals(encaminhamento.getStatus())
                && !StatusEncaminhamentoSaude.ACEITO.equals(encaminhamento.getStatus())) {
            throw new BadRequestException("Somente encaminhamentos pendentes ou aceitos podem ser cancelados.");
        }
        encaminhamento.setStatus(StatusEncaminhamentoSaude.CANCELADO);
        encaminhamento.setCanceladoEm(LocalDateTime.now());
        if (request != null && normalizar(request.observacao()) != null) {
            encaminhamento.setObservacaoResposta(normalizar(request.observacao()));
        }
        return toResponse(encaminhamento);
    }

    private EncaminhamentoSaude buscarParaResposta(Long id) {
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = usuarioIdObrigatorio();
        EncaminhamentoSaude encaminhamento = buscarEntidade(id, municipioId);
        if (!podeAtuarNaUnidade(encaminhamento.getUnidadeDestino().getId(), usuarioId, municipioId)) {
            throw new ForbiddenException("Usuario sem permissao para responder pela unidade destino.");
        }
        return encaminhamento;
    }

    private void preencherResposta(EncaminhamentoSaude encaminhamento, String observacao, String justificativa) {
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = usuarioIdObrigatorio();
        Usuario usuario = usuarioRepository.findAtivoByIdETenant(usuarioId, municipioId)
                .orElseThrow(() -> new NotAuthorizedException("Usuario nao encontrado no municipio logado."));
        encaminhamento.setUsuarioResposta(usuario);
        encaminhamento.setRespondidoEm(LocalDateTime.now());
        encaminhamento.setObservacaoResposta(normalizar(observacao));
        encaminhamento.setJustificativaRecusa(justificativa);
    }

    private EncaminhamentoSaude buscarEntidade(Long id, Long municipioId) {
        return encaminhamentoRepository.findByIdETenant(id, municipioId)
                .orElseThrow(() -> new NotFoundException("Encaminhamento nao encontrado."));
    }

    private boolean filtrarCaixa(EncaminhamentoSaude e, String caixa, Long usuarioId, Long municipioId) {
        if (isAdminOuGestor()) {
            return true;
        }
        if ("recebidos".equals(caixa)) {
            return usuarioUnidadeRepository.existsByUsuarioUnidadeETenant(usuarioId, e.getUnidadeDestino().getId(), municipioId);
        }
        if ("enviados".equals(caixa)) {
            return usuarioUnidadeRepository.existsByUsuarioUnidadeETenant(usuarioId, e.getUnidadeOrigem().getId(), municipioId);
        }
        return podeVisualizar(e, usuarioId, municipioId);
    }

    private boolean podeVisualizar(EncaminhamentoSaude e, Long usuarioId, Long municipioId) {
        return isAdminOuGestor()
                || usuarioUnidadeRepository.existsByUsuarioUnidadeETenant(usuarioId, e.getUnidadeOrigem().getId(), municipioId)
                || usuarioUnidadeRepository.existsByUsuarioUnidadeETenant(usuarioId, e.getUnidadeDestino().getId(), municipioId);
    }

    private boolean podeAtuarNaUnidade(Long unidadeId, Long usuarioId, Long municipioId) {
        return isAdminOuGestor()
                || usuarioUnidadeRepository.existsByUsuarioUnidadeETenant(usuarioId, unidadeId, municipioId);
    }

    private boolean isAdminOuGestor() {
        return securityIdentity.hasRole(Role.ADMIN.name()) || securityIdentity.hasRole(Role.GESTOR.name());
    }

    private EncaminhamentoSaudeResponse toResponse(EncaminhamentoSaude e) {
        Long municipioId = municipioIdObrigatorio();
        Long usuarioId = usuarioIdObrigatorio();
        boolean destino = podeAtuarNaUnidade(e.getUnidadeDestino().getId(), usuarioId, municipioId);
        boolean origem = podeAtuarNaUnidade(e.getUnidadeOrigem().getId(), usuarioId, municipioId);
        TriagemDengue triagem = e.getTriagem();
        Usuario solicitante = e.getUsuarioSolicitante();
        Usuario resposta = e.getUsuarioResposta();
        return new EncaminhamentoSaudeResponse(
                e.getId(),
                triagem.getId(),
                triagem.getPacienteIdentificacao(),
                triagem.getClassificacao(),
                e.getStatus(),
                Boolean.TRUE.equals(e.getExigeRegulacao()),
                toUnidade(e.getUnidadeOrigem()),
                toUnidade(e.getUnidadeDestino()),
                solicitante != null ? solicitante.getId() : null,
                solicitante != null ? solicitante.getNome() : null,
                resposta != null ? resposta.getId() : null,
                resposta != null ? resposta.getNome() : null,
                e.getJustificativaRecusa(),
                e.getObservacaoResposta(),
                e.getSolicitadoEm(),
                e.getRespondidoEm(),
                e.getConcluidoEm(),
                e.getCanceladoEm(),
                destino && StatusEncaminhamentoSaude.PENDENTE_ACEITE.equals(e.getStatus()),
                destino && StatusEncaminhamentoSaude.PENDENTE_ACEITE.equals(e.getStatus()),
                destino && StatusEncaminhamentoSaude.ACEITO.equals(e.getStatus()),
                origem && (StatusEncaminhamentoSaude.PENDENTE_ACEITE.equals(e.getStatus())
                        || StatusEncaminhamentoSaude.ACEITO.equals(e.getStatus())));
    }

    private TriagemUnidadeResumoResponse toUnidade(UnidadeSaude unidade) {
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

    private String normalizar(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
