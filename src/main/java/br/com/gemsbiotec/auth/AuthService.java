package br.com.gemsbiotec.auth;

import java.time.Duration;
import java.time.LocalDateTime;

import br.com.gemsbiotec.dominio.usuario.Usuario;
import br.com.gemsbiotec.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final LoginAttemptService loginAttemptService;
    private static final String DUMMY_HASH = "$2a$10$ik9ZMVHR/iHpA7KWPQc1aOFZG/yZNwr6nIqPE6EB9SVLBRJFhJ6YG";

    public AuthService(UsuarioRepository usuarioRepository, LoginAttemptService loginAttemptService) {
        this.usuarioRepository = usuarioRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        long retryAfter = loginAttemptService.segundosAteLiberar(email);
        if (retryAfter > 0) {
            Response response = Response.status(429).header("Retry-After", retryAfter).build();
            throw new WebApplicationException("Muitas tentativas. Tente novamente mais tarde.", response);
        }

        Usuario usuario = usuarioRepository.findAtivoByEmailComMunicipio(email).orElse(null);
        String hash = usuario != null ? usuario.getSenhaHash() : DUMMY_HASH;

        if (!BcryptUtil.matches(request.senha(), hash) || usuario == null) {
            loginAttemptService.registrarFalha(email);
            throw new WebApplicationException("Credenciais invalidas", Response.Status.UNAUTHORIZED);
        }

        loginAttemptService.registrarSucesso(email);

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
}
