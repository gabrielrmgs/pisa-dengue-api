package br.com.gemsbiotec.usuario;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record AtualizarVinculosUnidadesRequest(
        @NotNull(message = "A lista de unidades e obrigatoria")
        List<@NotNull(message = "O identificador da unidade e obrigatorio") Long> unidadeIds,
        Long unidadePrincipalId
) {
}
