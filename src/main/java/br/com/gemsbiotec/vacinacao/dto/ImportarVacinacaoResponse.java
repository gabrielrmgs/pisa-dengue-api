package br.com.gemsbiotec.vacinacao.dto;

import java.time.LocalDate;
import java.util.List;

public record ImportarVacinacaoResponse(
        LocalDate dataReferencia,
        int unidadesCasadas,
        int unidadesCriadas,
        int linhasProcessadas,
        int linhasInvalidas,
        List<String> avisos) {
}
