package br.com.gemsbiotec.pisa.dto;

import java.util.List;

public record FaixaEtariaDashboardResponse(
        String escopo,
        Long municipioId,
        String municipioNome,
        Long bairroId,
        String bairroNome,
        List<FaixaEtariaItem> faixas
) {
    public record FaixaEtariaItem(
            String faixa,
            long masculino,
            long feminino,
            long total
    ) {
    }
}
