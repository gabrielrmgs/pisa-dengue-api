package br.com.gemsbiotec.encaminhamento.dto;

import java.time.LocalDateTime;

import br.com.gemsbiotec.dominio.encaminhamento.StatusEncaminhamentoSaude;
import br.com.gemsbiotec.dominio.ras.PerfilAssistencialDengue;
import br.com.gemsbiotec.triagem.dto.TriagemUnidadeResumoResponse;

public record EncaminhamentoSaudeResponse(
        Long id,
        Long triagemId,
        String pacienteIdentificacao,
        PerfilAssistencialDengue classificacao,
        StatusEncaminhamentoSaude status,
        boolean exigeRegulacao,
        TriagemUnidadeResumoResponse unidadeOrigem,
        TriagemUnidadeResumoResponse unidadeDestino,
        Long usuarioSolicitanteId,
        String usuarioSolicitanteNome,
        Long usuarioRespostaId,
        String usuarioRespostaNome,
        String justificativaRecusa,
        String observacaoResposta,
        LocalDateTime solicitadoEm,
        LocalDateTime respondidoEm,
        LocalDateTime concluidoEm,
        LocalDateTime canceladoEm,
        boolean podeAceitar,
        boolean podeRecusar,
        boolean podeConcluir,
        boolean podeCancelar) {
}
