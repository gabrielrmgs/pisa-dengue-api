package br.com.gemsbiotec.usuario;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.dominio.usuario.Role;
import br.com.gemsbiotec.dominio.usuario.Usuario;
import br.com.gemsbiotec.repository.MunicipioRepository;
import br.com.gemsbiotec.repository.UsuarioRepository;
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

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            MunicipioRepository municipioRepository,
            TenantContext tenantContext,
            SecurityIdentity securityIdentity) {
        this.usuarioRepository = usuarioRepository;
        this.municipioRepository = municipioRepository;
        this.tenantContext = tenantContext;
        this.securityIdentity = securityIdentity;
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
