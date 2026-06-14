package br.com.gemsbiotec.saude;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.gemsbiotec.dominio.saude.TipoUnidadeSaude;
import br.com.gemsbiotec.saude.dto.AtualizarUnidadeSaudeRequest;
import br.com.gemsbiotec.saude.dto.CriarUnidadeSaudeRequest;
import br.com.gemsbiotec.saude.dto.UnidadeSaudeResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/unidades-saude")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Unidades de Saude", description = "CRUD e camada de mapa das unidades de saude do municipio")
public class UnidadeSaudeResource {

    private final UnidadeSaudeService unidadeSaudeService;

    public UnidadeSaudeResource(UnidadeSaudeService unidadeSaudeService) {
        this.unidadeSaudeService = unidadeSaudeService;
    }

    @GET
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Lista unidades de saude do municipio logado")
    public List<UnidadeSaudeResponse> listar(
            @QueryParam("tipo") TipoUnidadeSaude tipo,
            @QueryParam("bairroId") Long bairroId,
            @QueryParam("ativo") Boolean ativo) {
        return unidadeSaudeService.listar(tipo, bairroId, ativo);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Busca uma unidade de saude por ID")
    public UnidadeSaudeResponse buscar(@PathParam("id") Long id) {
        return unidadeSaudeService.buscar(id);
    }

    @POST
    @RolesAllowed({ "ADMIN", "GESTOR" })
    @Operation(summary = "Cria uma unidade de saude")
    public Response criar(@Valid CriarUnidadeSaudeRequest request) {
        UnidadeSaudeResponse unidade = unidadeSaudeService.criar(request);
        return Response.status(Response.Status.CREATED).entity(unidade).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "GESTOR" })
    @Operation(summary = "Atualiza uma unidade de saude")
    public UnidadeSaudeResponse atualizar(
            @PathParam("id") Long id,
            @Valid AtualizarUnidadeSaudeRequest request) {
        return unidadeSaudeService.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "GESTOR" })
    @Operation(summary = "Desativa uma unidade de saude")
    public Response remover(@PathParam("id") Long id) {
        unidadeSaudeService.remover(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}/ativar")
    @RolesAllowed({ "ADMIN", "GESTOR" })
    @Operation(summary = "Ativa uma unidade de saude")
    public UnidadeSaudeResponse ativar(@PathParam("id") Long id) {
        return unidadeSaudeService.ativar(id);
    }

    @PATCH
    @Path("/{id}/desativar")
    @RolesAllowed({ "ADMIN", "GESTOR" })
    @Operation(summary = "Desativa uma unidade de saude")
    public UnidadeSaudeResponse desativar(@PathParam("id") Long id) {
        return unidadeSaudeService.desativar(id);
    }

    @GET
    @Path("/geojson")
    @Produces("application/geo+json")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "GeoJSON de unidades de saude para marcadores no mapa")
    public Response geoJson(@QueryParam("tipo") TipoUnidadeSaude tipo) {
        return Response.ok(unidadeSaudeService.geoJson(tipo)).build();
    }
}
