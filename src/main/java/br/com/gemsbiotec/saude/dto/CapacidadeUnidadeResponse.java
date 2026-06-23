package br.com.gemsbiotec.saude.dto;

import java.time.LocalDateTime;

import br.com.gemsbiotec.dominio.saude.CategoriaCapacidadeAssistencial;

public record CapacidadeUnidadeResponse(
        Long capacidadeId,
        String codigo,
        String nome,
        String descricao,
        CategoriaCapacidadeAssistencial categoria,
        Boolean disponivel,
        String horarioAtendimento,
        String restricoes,
        String observacoes,
        LocalDateTime atualizadoEm
) {
}
