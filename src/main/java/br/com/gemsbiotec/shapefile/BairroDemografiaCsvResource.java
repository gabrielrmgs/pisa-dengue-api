package br.com.gemsbiotec.shapefile;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/admin/shapefiles/bairros-demografia")
public class BairroDemografiaCsvResource {

    @Inject
    BairroDemografiaCsvService bairroDemografiaCsvService;

    @GET
    @Path("/extrair")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acionarExtrator() {
        try {
            BairroDemografiaCsvService.ResultadoImportacao resultado = bairroDemografiaCsvService.importar(null);
            return Response.ok(resultado).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Erro ao processar CSV de demografia por bairros: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
