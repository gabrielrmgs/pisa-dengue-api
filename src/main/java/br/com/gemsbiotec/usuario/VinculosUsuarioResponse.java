package br.com.gemsbiotec.usuario;

import java.util.List;

public record VinculosUsuarioResponse(
        Long usuarioId,
        String usuarioNome,
        List<UnidadeVinculadaResponse> unidades
) {
}
