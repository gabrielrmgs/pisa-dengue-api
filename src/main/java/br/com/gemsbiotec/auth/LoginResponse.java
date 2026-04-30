package br.com.gemsbiotec.auth;

import br.com.gemsbiotec.dominio.usuario.Role;

public record LoginResponse(
                String token,
                String nomeCompleto,
                Role perfil) {
}
