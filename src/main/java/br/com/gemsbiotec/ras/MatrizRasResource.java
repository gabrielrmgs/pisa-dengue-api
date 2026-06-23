package br.com.gemsbiotec.ras;

import java.util.List;

import br.com.gemsbiotec.ras.dto.PerfilMatrizRasResponse;
import br.com.gemsbiotec.ras.dto.SimulacaoMatrizRasResponse;
import br.com.gemsbiotec.ras.dto.SimularMatrizRasRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/matriz-ras")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Matriz RAS", description = "Simulacao de referencia assistencial para dengue")
public class MatrizRasResource {

    private final MatrizRasService service;

    public MatrizRasResource(MatrizRasService service) {
        this.service = service;
    }

    @GET
    @Path("/perfis")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Lista os perfis assistenciais usados pela Matriz RAS")
    public List<PerfilMatrizRasResponse> perfis() {
        return service.listarPerfis();
    }

    @POST
    @Path("/simular")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE" })
    @Operation(summary = "Simula destinos compativeis para encaminhamento na rede de saude")
    public SimulacaoMatrizRasResponse simular(@Valid SimularMatrizRasRequest request) {
        return service.simular(request);
    }
}
