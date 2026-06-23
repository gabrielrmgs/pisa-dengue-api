package br.com.gemsbiotec.triagem.dto;

import br.com.gemsbiotec.dominio.saude.TipoUnidadeSaude;

public record TriagemUnidadeResumoResponse(
        Long id,
        String nome,
        TipoUnidadeSaude tipo,
        Long bairroId,
        String bairroNome) {
}
