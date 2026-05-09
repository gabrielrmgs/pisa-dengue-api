package br.com.gemsbiotec.pisa.dto;

public record DashboardResumoResponse(
            Long municipioId,
            String municipioNome,
            String municipioCodigoIbge,
            String estadoNome,
            String estadoSigla,
            long populacao,
            int totalCasosAno,
            int totalCasosMes,
            double incidenciaAcumulada,
            int semanaEpidemiologicaAtual,
            Integer semanaUltimoAlerta,
            String nivelAlerta,
            String corAlerta) {
    }
