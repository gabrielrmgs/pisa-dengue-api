package br.com.gemsbiotec.saude.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarCapacidadesUnidadeRequest(
        @NotNull(message = "A lista de capacidades e obrigatoria")
        @Size(max = 100, message = "A lista deve ter no maximo 100 capacidades")
        List<@NotNull(message = "A capacidade informada e invalida") @Valid CapacidadeUnidadeRequest> capacidades
) {
}
