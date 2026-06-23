package br.com.gemsbiotec.ras.dto;

import br.com.gemsbiotec.dominio.ras.PerfilAssistencialDengue;
import jakarta.validation.constraints.NotNull;

public record SimularMatrizRasRequest(
        @NotNull(message = "A unidade de origem e obrigatoria")
        Long unidadeOrigemId,

        @NotNull(message = "O perfil assistencial e obrigatorio")
        PerfilAssistencialDengue perfil) {
}
