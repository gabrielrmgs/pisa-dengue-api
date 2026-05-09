package br.com.gemsbiotec.usuario;

import br.com.gemsbiotec.dominio.usuario.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarUsuarioRequest(

        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres")
        String nome,

        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        @Size(max = 255, message = "E-mail deve ter no maximo 255 caracteres")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        String senha,

        @NotNull(message = "Role e obrigatoria")
        Role role,

        Long municipioId
) {
}
