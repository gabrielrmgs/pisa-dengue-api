package br.com.gemsbiotec.integration.infodengue;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * Client para a API pública do InfoDengue (Fiocruz / EMAp).
 *
 * Base URL configurada em application.properties:
 * quarkus.rest-client.infodengue.url=https://info.dengue.mat.br
 *
 * Documentação: https://info.dengue.mat.br/services/api
 */
@RegisterRestClient(configKey = "infodengue")
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public interface InfoDengueClient {

    /**
     * Retorna alertas semanais de dengue (ou outra arbovirose) para um município,
     * dado seu geocódigo IBGE, dentro de um intervalo de semanas epidemiológicas.
     *
     * Exemplo:
     * /api/alertcity?geocode=2201903&disease=dengue&format=json&ew_start=1&ew_end=20&ey_start=2024&ey_end=2024
     *
     * @param geocode Código IBGE do município (ex: "2201903" para Bom Jesus/PI)
     * @param disease "dengue" | "chikungunya" | "zika"
     * @param format  Sempre "json"
     * @param ewStart Semana epidemiológica inicial (1–53)
     * @param ewEnd   Semana epidemiológica final (1–53)
     * @param eyStart Ano epidemiológico inicial
     * @param eyEnd   Ano epidemiológico final
     */
    @GET
    @Path("/alertcity")
    List<AlertaSemanalDTO> getAlertasPorMunicipio(
            @QueryParam("geocode") String geocode,
            @QueryParam("disease") String disease,
            @QueryParam("format") String format,
            @QueryParam("ew_start") int ewStart,
            @QueryParam("ew_end") int ewEnd,
            @QueryParam("ey_start") int eyStart,
            @QueryParam("ey_end") int eyEnd);

    /**
     * Retorna o último alerta disponível para o município — útil para o card
     * "Nível de Alerta" no dashboard (verde/amarelo/laranja/vermelho).
     */
    @GET
    @Path("/alertcity")
    List<AlertaSemanalDTO> getUltimoAlerta(
            @QueryParam("geocode") String geocode,
            @QueryParam("disease") String disease,
            @QueryParam("format") String format,
            @QueryParam("ew_start") int ewStart,
            @QueryParam("ew_end") int ewEnd,
            @QueryParam("ey_start") int eyStart,
            @QueryParam("ey_end") int eyEnd);
}
