package br.com.gemsbiotec.pisa.dto;

import java.time.LocalDate;
import java.util.List;

public record ClimaCasosCorrelacaoResponse(
        Long municipioId,
        String municipioNome,
        String municipioCodigoIbge,
        int ano,
        int lagSemanas,
        String variavelSelecionada,
        List<VariavelClimaticaCorrelacao> variaveis,
        List<ClimaCasosSemana> pontos) {

    public record VariavelClimaticaCorrelacao(
            String codigo,
            String nome,
            String unidade,
            Double correlacaoPearson) {
    }

    public record ClimaCasosSemana(
            int semana,
            LocalDate dataInicio,
            LocalDate dataFim,
            int casos,
            double casosEstimados,
            double incidenciaPor100k,
            Double precipitacaoMm,
            Double temperaturaMediaC,
            Double umidadeMediaPct) {
    }
}
