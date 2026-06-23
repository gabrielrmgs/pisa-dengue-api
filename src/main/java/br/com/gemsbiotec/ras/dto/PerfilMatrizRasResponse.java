package br.com.gemsbiotec.ras.dto;

import java.util.List;

import br.com.gemsbiotec.dominio.ras.PerfilAssistencialDengue;

public record PerfilMatrizRasResponse(
        PerfilAssistencialDengue perfil,
        String nome,
        String descricao,
        boolean exigeRegulacao,
        List<CapacidadeMatrizRasResponse> capacidadesObrigatorias,
        List<CapacidadeMatrizRasResponse> capacidadesDesejaveis) {
}
