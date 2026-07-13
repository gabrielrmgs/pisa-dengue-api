package br.com.gemsbiotec.vacinacao.dto;

import java.time.LocalDate;

public record VacinacaoUnidadeResponse(
        Long unidadeSaudeId,
        String unidadeNome,
        Long bairroId,
        String bairroNome,
        LocalDate dataReferenciaAtual,
        Integer doses10a14Atual,
        Integer doses18a59Atual,
        Integer dosesTotalAtual,
        LocalDate dataReferenciaAnterior,
        Integer dosesTotalAnterior,
        Integer deltaDosesTotal,
        Double cobertura10a14Percentual,
        Double cobertura18a59Percentual,
        Integer meta10a14,
        Integer meta18a59,
        Integer faltam10a14ParaMeta,
        Integer faltam18a59ParaMeta,
        String statusCobertura) {
}
