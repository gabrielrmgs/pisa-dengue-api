package br.com.gemsbiotec.usuario;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuarios", description = "Gestao de usuarios da plataforma")
public class UsuarioResource {

    private final UsuarioService usuarioService;

    public UsuarioResource(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @POST
    @RolesAllowed("ADMIN")
    @Operation(summary = "Cadastra um novo usuario vinculado a um municipio")
    public Response criar(@Valid CriarUsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.criar(request);
        return Response.status(Response.Status.CREATED)
                .entity(usuario)
                .build();
    }

    @GET
    @RolesAllowed("ADMIN")
    @Operation(summary = "Lista os usuarios ativos do municipio logado")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Atualiza nome, perfil e status de um usuario do municipio logado")
    public UsuarioResponse atualizar(
            @PathParam("id") Long id,
            @Valid AtualizarUsuarioRequest request) {
        return usuarioService.atualizar(id, request);
    }

    @GET
    @Path("/me/unidades")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Lista as unidades de saude vinculadas ao usuario logado")
    public VinculosUsuarioResponse meusVinculos() {
        return usuarioService.meusVinculos();
    }

    @GET
    @Path("/{id}/unidades")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Lista as unidades de saude vinculadas ao usuario")
    public VinculosUsuarioResponse buscarVinculos(@PathParam("id") Long id) {
        return usuarioService.buscarVinculos(id);
    }

    @PUT
    @Path("/{id}/unidades")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Substitui as unidades de saude vinculadas ao usuario")
    public VinculosUsuarioResponse atualizarVinculos(
            @PathParam("id") Long id,
            @Valid AtualizarVinculosUnidadesRequest request) {
        return usuarioService.atualizarVinculos(id, request);
    }
}
