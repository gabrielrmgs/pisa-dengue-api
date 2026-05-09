package br.com.gemsbiotec.pisa.dto;

public record PopulacaoPorSexoDashboardResponse(
        String escopo,
        Long municipioId,
        String municipioNome,
        Long bairroId,
        String bairroNome,
        long masculino,
        long feminino,
        long total
) {
}
