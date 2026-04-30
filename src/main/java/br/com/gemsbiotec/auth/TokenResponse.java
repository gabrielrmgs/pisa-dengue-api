package br.com.gemsbiotec.auth;

/**
 * Payload de saída dos endpoints /auth/login e /auth/refresh.
 *
 * accessToken  — JWT RS256, validade de 15 minutos.
 *                O frontend armazena em memória (nunca em localStorage).
 *
 * refreshToken — UUID aleatório, validade de 7 dias.
 *                O frontend armazena em cookie HttpOnly, SameSite=Strict.
 *                O servidor guarda apenas o hash SHA-256 deste valor.
 *
 * expiresIn    — segundos até o accessToken expirar (sempre 900).
 *                Permite ao frontend agendar o refresh sem decodificar o JWT.
 *
 * municipioNome / role — informações de display para a UI (header do dashboard).
 *                        Não são usadas para autorização — o JWT é a fonte de verdade.
 */
public record TokenResponse(

    String  accessToken,
    String  refreshToken,
    int     expiresIn,
    String  municipioNome,
    String  municipioIbge,
    String  role,
    String  usuarioNome

) {
    /** Tempo de expiração do accessToken em segundos. */
    public static final int ACCESS_TOKEN_EXPIRY_SECONDS = 900; // 15 minutos
}
