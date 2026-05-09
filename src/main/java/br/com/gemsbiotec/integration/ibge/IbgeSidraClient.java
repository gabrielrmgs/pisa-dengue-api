package br.com.gemsbiotec.integration.ibge;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * Client para a API SIDRA do IBGE - Censo Demografico 2022.
 *
 * Tabela 9514: populacao residente por sexo, idade e forma de declaracao da idade.
 * Base URL configurada em quarkus.rest-client.ibge-sidra.url.
 */
@RegisterRestClient(configKey = "ibge-sidra")
@Produces(MediaType.APPLICATION_JSON)
public interface IbgeSidraClient {

    /**
     * Retorna populacao total, masculina e feminina do municipio.
     *
     * URL:
     * /values/t/9514/n6/{geocode}/v/allxp/p/all/c2/all/c287/100362/c286/113635
     */
    @GET
    @Path("/values/t/9514/n6/{geocode}/v/allxp/p/all/c2/all/c287/100362/c286/113635")
    List<SidraResultadoDTO> getPopulacaoPorSexo(
            @PathParam("geocode") String geocodigo
    );

    /**
     * Retorna populacao por grupos etarios quinquenais, apenas para sexo total.
     */
    @GET
    @Path("/values/t/9514/n6/{geocode}/v/93/p/2022/c2/6794/c287/100362,93070,93084,93085,93086,93087,93088,93089,93090,93091,93092,93093,93094,93095,93096,93097,93098,49108,49109,60040,60041,6653/c286/113635")
    List<SidraResultadoDTO> getPopulacaoPorFaixaEtaria(
            @PathParam("geocode") String geocodigo
    );

    /**
     * Variante com parametros dinamicos para futuras consultas SIDRA.
     *
     * Exemplo de path:
     * t/9514/n6/2201903/v/93/p/2022/c2/6794,4,5/c287/100362/c286/113635
     */
    @GET
    @Path("/values/{path:.+}")
    List<SidraResultadoDTO> consultaGenerica(
            @PathParam("path") String path
    );
}
