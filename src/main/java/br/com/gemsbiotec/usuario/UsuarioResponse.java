package br.com.gemsbiotec.usuario;

import br.com.gemsbiotec.dominio.usuario.Role;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Role role,
        Boolean ativo,
        Long municipioId,
        String municipioNome,
        LocalDateTime criadoEm
) {
}
