package br.com.gemsbiotec.ras.dto;

import br.com.gemsbiotec.dominio.saude.TipoUnidadeSaude;

public record UnidadeMatrizRasResponse(
        Long id,
        String nome,
        TipoUnidadeSaude tipo,
        Long bairroId,
        String bairroNome,
        String endereco,
        Double latitude,
        Double longitude) {
}
