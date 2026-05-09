package br.com.gemsbiotec.pisa.dto;

import java.util.List;

public record DengueComparativoAnualResponse(
        List<Integer> anos,
        List<DengueSemanaComparativo> semanas
) {
    public record DengueSemanaComparativo(
            int semana,
            List<DengueAnoValor> valores
    ) {
    }

    public record DengueAnoValor(
            int ano,
            int casos,
            double casosEstimados,
            double incidenciaPor100k
    ) {
    }
}
