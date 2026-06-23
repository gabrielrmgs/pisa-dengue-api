package br.com.gemsbiotec.saude.dto;

import br.com.gemsbiotec.dominio.saude.CategoriaCapacidadeAssistencial;

public record CapacidadeAssistencialResponse(
        Long id,
        String codigo,
        String nome,
        String descricao,
        CategoriaCapacidadeAssistencial categoria
) {
}
