package br.com.gemsbiotec.pisa.web;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import br.com.gemsbiotec.integration.ibge.IbgeService;
import br.com.gemsbiotec.integration.infodengue.AlertaSemanalDTO;
import br.com.gemsbiotec.integration.infodengue.InfoDengueService;
import br.com.gemsbiotec.mapa.MapaService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Endpoints de dados para preencher o dashboard principal")
public class DashboardResource {

    private final Logger LOG = Logger.getLogger(DashboardResource.class);

    private final IbgeService ibgeService;
    private final InfoDengueService infoDengueService;
    private final MapaService mapaService;

    public DashboardResource(IbgeService ibgeService, InfoDengueService infoDengueService, MapaService mapaService) {
        this.ibgeService = ibgeService;
        this.infoDengueService = infoDengueService;
        this.mapaService = mapaService;
    }

    @GET
    @Path("/geojson")
    @Produces("application/geo+json")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "GeoJSON com polígonos dos bairros")
    public Response geoJsonMunicipio() {
        return mapaService.geoJsonMunicipio();
    }

    @GET
    @Path("/dengue/ano/{anoConsulta}")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Casos históricos de dengue em determinado ano")
    public Response casosHistoricosDengue(@PathParam("anoConsulta") int anoConsulta) {

        List<AlertaSemanalDTO> alertas = infoDengueService.getAlertasPorAno(anoConsulta);

        return Response.status(Status.ACCEPTED).entity(alertas).build();
    }

}
