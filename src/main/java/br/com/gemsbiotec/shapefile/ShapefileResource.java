package br.com.gemsbiotec.shapefile;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/admin/shapefiles")
public class ShapefileResource {

    @Inject
    ShapefileService shapefileService;

    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/extrair")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public Response acionarExtrator() {
        System.out.println("Usuário: " + identity.getPrincipal().getName());
        System.out.println("Roles: " + identity.getRoles());
        try {
            String resultado = shapefileService.importarBairrosPiaui();
            return Response.ok(resultado).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Erro ao processar shapefile: " + e.getMessage())
                    .build();
        }
    }
}