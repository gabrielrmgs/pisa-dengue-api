package br.com.gemsbiotec.triagem.dto;

import java.time.LocalDateTime;

import br.com.gemsbiotec.dominio.ras.PerfilAssistencialDengue;
import br.com.gemsbiotec.dominio.triagem.StatusTriagemDengue;
import br.com.gemsbiotec.ras.dto.SimulacaoMatrizRasResponse;

public record TriagemDengueResponse(
        Long id,
        String pacienteIdentificacao,
        Integer pacienteIdade,
        String pacienteSexo,
        Integer diasSintomas,
        PerfilAssistencialDengue classificacao,
        StatusTriagemDengue status,
        boolean suspeitaConsistente,
        boolean atendeNaOrigem,
        boolean encaminhamentoNecessario,
        boolean exigeRegulacao,
        String recomendacao,
        TriagemUnidadeResumoResponse unidadeOrigem,
        TriagemUnidadeResumoResponse unidadeDestinoSugerida,
        Long usuarioTriagemId,
        String usuarioTriagemNome,
        LocalDateTime criadoEm,
        SimulacaoMatrizRasResponse simulacao) {
}
