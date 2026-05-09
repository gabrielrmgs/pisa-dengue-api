package br.com.gemsbiotec.usuario;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
    @RolesAllowed({ "ADMIN", "GESTOR" })
    @Operation(summary = "Cadastra um novo usuario vinculado a um municipio")
    public Response criar(@Valid CriarUsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.criar(request);
        return Response.status(Response.Status.CREATED)
                .entity(usuario)
                .build();
    }
}
