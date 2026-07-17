package br.com.gemsbiotec.vacinacao.dto;

import java.time.LocalDate;

public record VacinacaoResumoResponse(
        Long municipioId,
        String municipioNome,
        LocalDate dataReferenciaAtual,
        LocalDate dataReferenciaAnterior,
        Integer doses10a14Total,
        Integer doses18a59Total,
        Integer dosesTotal,
        Integer deltaDosesTotal,
        Double coberturaMediaPercentual,
        Integer unidadesComMetaAtingida,
        Integer totalUnidades,
        Integer unidadesComCoberturaIndisponivel,
        Integer unidadesComDadoInconsistente) {
}
