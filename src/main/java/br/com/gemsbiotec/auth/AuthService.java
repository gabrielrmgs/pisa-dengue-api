package br.com.gemsbiotec.auth;

import java.time.Duration;
import java.time.LocalDateTime;

import br.com.gemsbiotec.dominio.usuario.Role;
import br.com.gemsbiotec.dominio.usuario.Usuario;
import br.com.gemsbiotec.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class AuthService {

    private UsuarioRepository usuarioRepository;

    @Inject
    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository
                .find("email", request.email())
                .firstResultOptional()
                .orElseThrow(() -> new BadRequestException("Credenciais inválidas"));

        if (Boolean.FALSE.equals(usuario.ativo))
            throw new BadRequestException("Usuário inativo");

        if (!BcryptUtil.matches(request.senha(), usuario.getSenhaHash()))
            throw new BadRequestException("Credenciais inválidas");

        usuario.setUltimoLogin(LocalDateTime.now());

        String token = Jwt.issuer("pisa-dengue-api")
                .subject(usuario.getId().toString())
                .claim("nome", usuario.getNome())
                .claim("email", usuario.getEmail())
                .claim("municipio_id", usuario.getMunicipioId().toString())
                .groups(usuario.getRole().name())
                .expiresIn(Duration.ofHours(6))
                .sign();

        return new LoginResponse(token, usuario.getNome(), usuario.getRole());
    }

    @Transactional
    public LoginResponse criarUsuario(LoginRequest request) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(request.email());
        novoUsuario.setSenhaHash(BcryptUtil.bcryptHash(request.senha()));
        novoUsuario.setNome("Gabriel");
        novoUsuario.setRole(Role.ADMIN);

        usuarioRepository.persist(novoUsuario);

        return new LoginResponse("", novoUsuario.getNome(), novoUsuario.getRole());
    }
}
