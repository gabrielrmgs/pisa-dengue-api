package br.com.gemsbiotec.ras.dto;

import java.util.List;

public record DestinoMatrizRasResponse(
        UnidadeMatrizRasResponse unidade,
        boolean unidadeOrigem,
        Double distanciaKm,
        int capacidadesDesejaveisAtendidas,
        int totalCapacidadesDesejaveis,
        List<CapacidadeMatrizRasResponse> capacidadesObrigatoriasAtendidas,
        List<CapacidadeMatrizRasResponse> capacidadesDesejaveisAtendidasDetalhe) {
}
