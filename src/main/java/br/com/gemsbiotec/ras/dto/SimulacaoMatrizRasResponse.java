package br.com.gemsbiotec.ras.dto;

import java.util.List;

public record SimulacaoMatrizRasResponse(
        PerfilMatrizRasResponse perfil,
        UnidadeMatrizRasResponse origem,
        boolean atendeNaOrigem,
        boolean encaminhamentoNecessario,
        boolean exigeRegulacao,
        String recomendacao,
        List<DestinoMatrizRasResponse> destinosCompativeis) {
}
