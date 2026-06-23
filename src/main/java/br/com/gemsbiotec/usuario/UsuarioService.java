package br.com.gemsbiotec.usuario;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.dominio.usuario.Role;
import br.com.gemsbiotec.dominio.usuario.Usuario;
import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
import br.com.gemsbiotec.dominio.saude.UsuarioUnidadeSaude;
import br.com.gemsbiotec.repository.MunicipioRepository;
import br.com.gemsbiotec.repository.UnidadeSaudeRepository;
import br.com.gemsbiotec.repository.UsuarioRepository;
import br.com.gemsbiotec.repository.UsuarioUnidadeSaudeRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final MunicipioRepository municipioRepository;
    private final TenantContext tenantContext;
    private final SecurityIdentity securityIdentity;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final UsuarioUnidadeSaudeRepository usuarioUnidadeSaudeRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            MunicipioRepository municipioRepository,
            TenantContext tenantContext,
            SecurityIdentity securityIdentity,
            UnidadeSaudeRepository unidadeSaudeRepository,
            UsuarioUnidadeSaudeRepository usuarioUnidadeSaudeRepository) {
        this.usuarioRepository = usuarioRepository;
        this.municipioRepository = municipioRepository;
        this.tenantContext = tenantContext;
        this.securityIdentity = securityIdentity;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.usuarioUnidadeSaudeRepository = usuarioUnidadeSaudeRepository;
    }

    @Transactional
    public List<UsuarioResponse> listar() {
        Long municipioId = exigirMunicipioLogado();
        return usuarioRepository.listAtivosByMunicipio(municipioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public VinculosUsuarioResponse buscarVinculos(Long usuarioId) {
        Long municipioId = exigirMunicipioLogado();
        Usuario usuario = buscarUsuarioDoTenant(usuarioId, municipioId);
        return toVinculosResponse(usuario,
                usuarioUnidadeSaudeRepository.listByUsuarioETenant(usuarioId, municipioId));
    }

    @Transactional
    public VinculosUsuarioResponse meusVinculos() {
        Long municipioId = exigirMunicipioLogado();
        Long usuarioId = tenantContext.getUsuarioId();
        if (usuarioId == null) {
            throw new ForbiddenException("Usuario logado nao encontrado.");
        }
        Usuario usuario = buscarUsuarioDoTenant(usuarioId, municipioId);
        return toVinculosResponse(usuario,
                usuarioUnidadeSaudeRepository.listByUsuarioETenant(usuarioId, municipioId));
    }

    @Transactional
    public VinculosUsuarioResponse atualizarVinculos(
            Long usuarioId,
            AtualizarVinculosUnidadesRequest request) {
        Long municipioId = exigirMunicipioLogado();
        Usuario usuario = buscarUsuarioDoTenant(usuarioId, municipioId);

        Set<Long> unidadeIds = new HashSet<>(request.unidadeIds());
        if (unidadeIds.size() != request.unidadeIds().size()) {
            throw new BadRequestException("A lista de unidades possui identificadores duplicados.");
        }
        if (!unidadeIds.isEmpty() && request.unidadePrincipalId() == null) {
            throw new BadRequestException("A unidade principal e obrigatoria quando existem unidades vinculadas.");
        }
        if (request.unidadePrincipalId() != null && !unidadeIds.contains(request.unidadePrincipalId())) {
            throw new BadRequestException("A unidade principal deve estar entre as unidades vinculadas.");
        }

        List<UnidadeSaude> unidades = unidadeIds.stream()
                .map(id -> unidadeSaudeRepository.findByIdETenant(id, municipioId)
                        .orElseThrow(() -> new NotFoundException("Unidade de saude nao encontrada: " + id)))
                .toList();

        if (unidades.stream().anyMatch(unidade -> !Boolean.TRUE.equals(unidade.getAtivo()))) {
            throw new BadRequestException("Somente unidades ativas podem receber novos vinculos.");
        }

        usuarioUnidadeSaudeRepository.deleteByUsuarioETenant(usuarioId, municipioId);
        usuarioUnidadeSaudeRepository.flush();

        for (UnidadeSaude unidade : unidades) {
            UsuarioUnidadeSaude vinculo = new UsuarioUnidadeSaude();
            vinculo.setUsuario(usuario);
            vinculo.setUnidadeSaude(unidade);
            vinculo.setPrincipal(unidade.getId().equals(request.unidadePrincipalId()));
            usuarioUnidadeSaudeRepository.persist(vinculo);
        }
        usuarioUnidadeSaudeRepository.flush();

        return toVinculosResponse(usuario,
                usuarioUnidadeSaudeRepository.listByUsuarioETenant(usuarioId, municipioId));
    }

    @Transactional
    public UsuarioResponse criar(CriarUsuarioRequest request) {
        Long municipioId = resolverMunicipioId(request);
        validarRolePermitida(request.role());

        Municipio municipio = municipioRepository.findAtivoById(municipioId)
                .orElseThrow(() -> new NotFoundException("Municipio ativo nao encontrado."));

        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new WebApplicationException("E-mail ja cadastrado.", Response.Status.CONFLICT);
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(BcryptUtil.bcryptHash(request.senha()));
        usuario.setRole(request.role());
        usuario.setMunicipio(municipio);
        usuario.setAtivo(true);

        usuarioRepository.persist(usuario);
        usuarioRepository.flush();

        return toResponse(usuario);
    }

    private Long resolverMunicipioId(CriarUsuarioRequest request) {
        if (securityIdentity.hasRole(Role.ADMIN.name())) {
            if (request.municipioId() == null) {
                throw new BadRequestException("municipioId e obrigatorio para ADMIN.");
            }
            return request.municipioId();
        }

        if (securityIdentity.hasRole(Role.GESTOR.name())) {
            Long municipioIdLogado = tenantContext.getMunicipioId();
            if (municipioIdLogado == null) {
                throw new ForbiddenException("Municipio do usuario logado nao encontrado.");
            }

            if (request.municipioId() != null && !request.municipioId().equals(municipioIdLogado)) {
                throw new ForbiddenException("GESTOR so pode criar usuarios no proprio municipio.");
            }

            return municipioIdLogado;
        }

        throw new ForbiddenException("Usuario sem permissao para criar usuarios.");
    }

    private void validarRolePermitida(Role role) {
        if (securityIdentity.hasRole(Role.ADMIN.name())) {
            return;
        }

        if (securityIdentity.hasRole(Role.GESTOR.name())
                && (Role.AGENTE.equals(role) || Role.VIEWER.equals(role))) {
            return;
        }

        throw new ForbiddenException("Role nao permitida para o usuario logado.");
    }

    private Long exigirMunicipioLogado() {
        Long municipioId = tenantContext.getMunicipioId();
        if (municipioId == null) {
            throw new ForbiddenException("Municipio do usuario logado nao encontrado.");
        }
        return municipioId;
    }

    private Usuario buscarUsuarioDoTenant(Long usuarioId, Long municipioId) {
        return usuarioRepository.findAtivoByIdETenant(usuarioId, municipioId)
                .orElseThrow(() -> new NotFoundException("Usuario ativo nao encontrado."));
    }

    private VinculosUsuarioResponse toVinculosResponse(
            Usuario usuario,
            List<UsuarioUnidadeSaude> vinculos) {
        List<UnidadeVinculadaResponse> unidades = vinculos.stream()
                .map(vinculo -> {
                    UnidadeSaude unidade = vinculo.getUnidadeSaude();
                    return new UnidadeVinculadaResponse(
                            unidade.getId(),
                            unidade.getNome(),
                            unidade.getTipo(),
                            unidade.getAtivo(),
                            vinculo.getPrincipal());
                })
                .toList();
        return new VinculosUsuarioResponse(usuario.getId(), usuario.getNome(), unidades);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        Municipio municipio = usuario.getMunicipio();
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getAtivo(),
                municipio != null ? municipio.getId() : null,
                municipio != null ? municipio.getNome() : null,
                usuario.getCriadoEm());
    }
}
