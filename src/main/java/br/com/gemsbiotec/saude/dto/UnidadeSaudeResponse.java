package br.com.gemsbiotec.saude.dto;

import java.time.LocalDateTime;

import br.com.gemsbiotec.dominio.saude.IconePinUnidadeSaude;
import br.com.gemsbiotec.dominio.saude.TipoUnidadeSaude;

public record UnidadeSaudeResponse(
        Long id,
        Long municipioId,
        String municipioNome,
        Long bairroId,
        String bairroNome,
        String nome,
        TipoUnidadeSaude tipo,
        IconePinUnidadeSaude iconePin,
        String cnes,
        String endereco,
        String telefone,
        String email,
        String responsavel,
        Double latitude,
        Double longitude,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {
}
