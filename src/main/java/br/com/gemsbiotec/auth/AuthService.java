package br.com.gemsbiotec.auth;

import java.time.Duration;
import java.time.LocalDateTime;

import br.com.gemsbiotec.dominio.usuario.Usuario;
import br.com.gemsbiotec.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        Usuario usuario = usuarioRepository
                .findAtivoByEmailComMunicipio(email)
                .orElseThrow(() -> new BadRequestException("Credenciais invalidas"));

        if (!BcryptUtil.matches(request.senha(), usuario.getSenhaHash())) {
            throw new BadRequestException("Credenciais invalidas");
        }

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
