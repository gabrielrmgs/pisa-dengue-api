package br.com.gemsbiotec.vacinacao.dto;

import br.com.gemsbiotec.dominio.vacinacao.FaixaEtariaVacinacao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarDoseRequest(
        @NotNull(message = "A unidade de saude e obrigatoria")
        Long unidadeSaudeId,

        @NotNull(message = "A faixa etaria e obrigatoria")
        FaixaEtariaVacinacao faixaEtaria,

        @NotNull(message = "A quantidade e obrigatoria")
        @Min(value = 1, message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        @Size(max = 500, message = "As observacoes devem ter no maximo 500 caracteres")
        String observacoes) {
}
