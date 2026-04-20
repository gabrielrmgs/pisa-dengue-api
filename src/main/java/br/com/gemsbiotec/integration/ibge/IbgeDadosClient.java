package br.com.gemsbiotec.integration.ibge;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * Client para a API de dados demográficos do IBGE (SIDRA / Agregados).
 *
 * Base URL: https://servicodados.ibge.gov.br
 * Configurada em: quarkus.rest-client.ibge-dados.url
 *
 * Usada para:
 *  - População total do município (card "População Total")
 *  - Distribuição por sexo (gráfico "População por Sexo")
 *  - Distribuição por faixa etária (gráfico "População por Faixa Etária")
 */
@RegisterRestClient(configKey = "ibge-dados")
@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface IbgeDadosClient {

    /**
     * Busca informações básicas do município (nome, UF, população estimada).
     *
     * Exemplo: /api/v1/localidades/municipios/2201903
     */
    @GET
    @Path("/localidades/municipios/{codigo}")
    MunicipioInfoDTO getMunicipioInfo(
            @PathParam("codigo") String codigoMunicipio
    );

    /**
     * Retorna lista de municípios de um estado — útil para futura expansão
     * da plataforma para novos municípios.
     *
     * Exemplo: /api/v1/localidades/estados/PI/municipios
     */
    @GET
    @Path("/localidades/estados/{uf}/municipios")
    List<MunicipioInfoDTO> getMunicipiosPorEstado(
            @PathParam("uf") String siglaEstado
    );

    /**
     * Dados populacionais agregados via SIDRA.
     *
     * Tabela 9514 = Censo 2022, população por sexo e faixa etária.
     *
     * Exemplo:
     * /api/v1/pesquisas/indicadores/indicadores=93/resultados/2201903
     *
     * Para dados mais granulares usar a API SIDRA diretamente:
     * @see IbgeSidraClient
     */
    @GET
    @Path("/pesquisas/indicadores/indicadores=93/resultados/{codigo}")
    List<ResultadoIndicadorDTO> getPopulacaoMunicipio(
            @PathParam("codigo") String codigoMunicipio
    );
}
