package br.com.gemsbiotec.triagem;

import java.util.List;

import br.com.gemsbiotec.triagem.dto.CriarTriagemDengueRequest;
import br.com.gemsbiotec.triagem.dto.TriagemDengueResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/triagens-dengue")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Triagem de Dengue", description = "Classificacao simples e encaminhamento assistencial para dengue")
public class TriagemDengueResource {

    private final TriagemDengueService service;

    public TriagemDengueResource(TriagemDengueService service) {
        this.service = service;
    }

    @GET
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Lista triagens recentes do municipio")
    public List<TriagemDengueResponse> listar(@QueryParam("limite") Integer limite) {
        return service.listarRecentes(limite);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Busca uma triagem de dengue")
    public TriagemDengueResponse buscar(@PathParam("id") Long id) {
        return service.buscar(id);
    }

    @POST
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE" })
    @Operation(summary = "Registra uma triagem de dengue e sugere destino pela Matriz RAS")
    public TriagemDengueResponse criar(@Valid CriarTriagemDengueRequest request) {
        return service.criar(request);
    }
}
