package br.com.gemsbiotec.repository;

import br.com.gemsbiotec.dominio.usuario.*;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Usuario.
 *
 * Queries focadas em autenticação e gestão de acesso.
 * senhaHash nunca é retornado em DTOs — essa responsabilidade
 * é do AuthService, não do repository.
 */
@ApplicationScoped
public class UsuarioRepository implements PanacheRepositoryBase<Usuario, Long> {

    // ── autenticação ──────────────────────────────────────────────────────────

    /**
     * Busca ativo por email — primeiro passo do fluxo de login.
     * Carrega o municipio junto (JOIN FETCH) para evitar lazy load
     * no JwtService que precisa do municipio_id para emitir o token.
     */
    public Optional<Usuario> findAtivoByEmailComMunicipio(String email) {
        return find("""
                FROM Usuario u
                JOIN FETCH u.municipio m
                WHERE LOWER(u.email) = LOWER(?1)
                  AND u.ativo = true
                """, email)
            .firstResultOptional();
    }

    /**
     * Busca por ID garantindo que pertence ao tenant correto.
     * Usado em toda operação autenticada para revalidar o usuário.
     */
    public Optional<Usuario> findAtivoByIdETenant(Long id, Long municipioId) {
        return find("id = ?1 AND municipio.id = ?2 AND ativo = true",
                id, municipioId)
            .firstResultOptional();
    }

    // ── gestão de usuários (ADMIN) ────────────────────────────────────────────

    /**
     * Lista todos os usuários ativos do município — painel de gestão.
     * Resultado ordenado por nome para exibição em tabela.
     */
    public List<Usuario> listAtivosByMunicipio(Long municipioId) {
        return list("""
                municipio.id = ?1
                AND ativo = true
                ORDER BY nome
                """, municipioId);
    }

    /**
     * Lista usuários ativos por role dentro do município.
     * Ex: listar todos os agentes para atribuição de triagens.
     */
    public List<Usuario> listAtivosByMunicipioERole(Long municipioId, Role role) {
        return list("municipio.id = ?1 AND role = ?2 AND ativo = true ORDER BY nome",
                municipioId, role);
    }

    public boolean existsByEmail(String email) {
        return count("LOWER(email) = LOWER(?1)", email) > 0;
    }

    public boolean existsByEmailETenant(String email, Long municipioId) {
        return count("LOWER(email) = LOWER(?1) AND municipio.id = ?2",
                email, municipioId) > 0;
    }

    // ── refresh token ─────────────────────────────────────────────────────────

    /**
     * Busca por hash do refresh token — validação no endpoint /auth/refresh.
     * O token em si não é armazenado, apenas seu hash (SHA-256).
     */
    public Optional<Usuario> findByRefreshTokenHash(String hash) {
        return find("refreshTokenHash = ?1 AND ativo = true", hash)
            .firstResultOptional();
    }

    /**
     * Invalida o refresh token — chamado no logout.
     * Retorna 1 se o token foi encontrado e invalidado, 0 caso contrário.
     */
    public int invalidarRefreshToken(Long usuarioId) {
        return update("refreshTokenHash = null WHERE id = ?1", usuarioId);
    }
}
