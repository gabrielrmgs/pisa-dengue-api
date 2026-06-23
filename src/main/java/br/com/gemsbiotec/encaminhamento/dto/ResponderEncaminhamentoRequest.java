package br.com.gemsbiotec.encaminhamento.dto;

import jakarta.validation.constraints.Size;

public record ResponderEncaminhamentoRequest(
        @Size(max = 1000, message = "A observacao deve ter no maximo 1000 caracteres")
        String observacao,

        @Size(max = 1000, message = "A justificativa deve ter no maximo 1000 caracteres")
        String justificativa) {
}
