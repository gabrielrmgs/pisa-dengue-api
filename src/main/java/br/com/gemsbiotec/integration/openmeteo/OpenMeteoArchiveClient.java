package br.com.gemsbiotec.integration.openmeteo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "open-meteo-archive")
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface OpenMeteoArchiveClient {

    @GET
    @Path("/archive")
    OpenMeteoArchiveResponse getArchive(
            @QueryParam("latitude") double latitude,
            @QueryParam("longitude") double longitude,
            @QueryParam("start_date") String startDate,
            @QueryParam("end_date") String endDate,
            @QueryParam("hourly") String hourly,
            @QueryParam("timezone") String timezone);
}
