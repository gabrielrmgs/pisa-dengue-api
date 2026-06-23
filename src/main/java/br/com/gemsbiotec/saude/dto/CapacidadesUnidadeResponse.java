package br.com.gemsbiotec.saude.dto;

import java.util.List;

public record CapacidadesUnidadeResponse(
        Long unidadeId,
        String unidadeNome,
        List<CapacidadeUnidadeResponse> capacidades
) {
}
