package br.com.gemsbiotec.usuario;

import br.com.gemsbiotec.dominio.saude.TipoUnidadeSaude;

public record UnidadeVinculadaResponse(
        Long id,
        String nome,
        TipoUnidadeSaude tipo,
        Boolean ativo,
        Boolean principal
) {
}
