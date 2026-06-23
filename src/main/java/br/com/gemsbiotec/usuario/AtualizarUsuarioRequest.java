package br.com.gemsbiotec.usuario;

import br.com.gemsbiotec.dominio.usuario.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres")
        String nome,

        @NotNull(message = "Role e obrigatoria")
        Role role,

        @NotNull(message = "Status ativo e obrigatorio")
        Boolean ativo
) {
}
