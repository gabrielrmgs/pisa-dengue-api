package br.com.gemsbiotec.saude.dto;

import br.com.gemsbiotec.dominio.saude.TipoUnidadeSaude;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarUnidadeSaudeRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres")
        String nome,

        @NotNull(message = "Tipo e obrigatorio")
        TipoUnidadeSaude tipo,

        @Size(max = 20, message = "CNES deve ter no maximo 20 caracteres")
        String cnes,

        @Size(max = 255, message = "Endereco deve ter no maximo 255 caracteres")
        String endereco,

        @Size(max = 30, message = "Telefone deve ter no maximo 30 caracteres")
        String telefone,

        @Email(message = "E-mail invalido")
        @Size(max = 255, message = "E-mail deve ter no maximo 255 caracteres")
        String email,

        @Size(max = 150, message = "Responsavel deve ter no maximo 150 caracteres")
        String responsavel,

        Long bairroId,

        @NotNull(message = "Latitude e obrigatoria")
        @DecimalMin(value = "-90.0", message = "Latitude deve ser maior ou igual a -90")
        @DecimalMax(value = "90.0", message = "Latitude deve ser menor ou igual a 90")
        Double latitude,

        @NotNull(message = "Longitude e obrigatoria")
        @DecimalMin(value = "-180.0", message = "Longitude deve ser maior ou igual a -180")
        @DecimalMax(value = "180.0", message = "Longitude deve ser menor ou igual a 180")
        Double longitude,

        Boolean ativo) {
}
