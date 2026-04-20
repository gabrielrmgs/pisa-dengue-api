package br.com.gemsbiotec.integration.ibge;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Client para as APIs públicas do IBGE.
 *
 * Duas bases distintas com URLs diferentes:
 *
 *   1. Malha Geográfica (GeoJSON de bairros / municípios)
 *      Base URL: https://servicodados.ibge.gov.br
 *      Configurada em: quarkus.rest-client.ibge-malha.url
 *
 *   2. Dados demográficos do Censo 2022
 *      Base URL: https://servicodados.ibge.gov.br
 *      Configurada em: quarkus.rest-client.ibge-dados.url
 *
 * Por usarem a mesma base URL, separamos em dois configKeys
 * para permitir timeouts e políticas de retry independentes.
 */
@RegisterRestClient(configKey = "ibge-malha")
@Path("/api/v3")
@Produces(MediaType.APPLICATION_JSON)
public interface IbgeMalhaClient {

    /**
     * GeoJSON da malha municipal — usado pelo Leaflet para renderizar o polígono
     * do município no mapa.
     *
     * Exemplo: /api/v3/malhas/municipios/2201903?formato=geojson&qualidade=minima
     *
     * @param codigoMunicipio Código IBGE de 7 dígitos (ex: 2201903)
     * @param formato         Sempre "geojson"
     * @param qualidade       "minima" | "intermediaria" | "maxima"
     *                        Use "minima" para o dashboard (menor payload).
     */
    @GET
    @Path("/malhas/municipios/{codigo}")
    @Produces("application/geo+json")
    String getMalhaMunicipio(
            @PathParam("codigo")    String codigoMunicipio,
            @QueryParam("formato")  String formato,
            @QueryParam("qualidade") String qualidade
    );

    /**
     * GeoJSON da malha de distritos/subdistritos dentro de um município.
     * Útil para subdividir o mapa por bairros quando disponível.
     */
    @GET
    @Path("/malhas/municipios/{codigo}/submalhas")
    @Produces("application/geo+json")
    String getSubMalhaMunicipio(
            @PathParam("codigo")   String codigoMunicipio,
            @QueryParam("formato") String formato
    );
}
