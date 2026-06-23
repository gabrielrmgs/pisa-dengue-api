package br.com.gemsbiotec.saude.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CapacidadeUnidadeRequest(
        @NotNull(message = "A capacidade e obrigatoria")
        Long capacidadeId,

        @NotNull(message = "A disponibilidade e obrigatoria")
        Boolean disponivel,

        @Size(max = 150, message = "O horario deve ter no maximo 150 caracteres")
        String horarioAtendimento,

        @Size(max = 500, message = "As restricoes devem ter no maximo 500 caracteres")
        String restricoes,

        @Size(max = 500, message = "As observacoes devem ter no maximo 500 caracteres")
        String observacoes
) {
}
