package br.com.gemsbiotec.auth;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private AuthService authService;

    @Inject
    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(LoginRequest request) {
        return Response.ok(authService.login(request)).build();
    }

    @POST
    @Path("/criarAdmin")
    @PermitAll
    public Response criarAdmin(LoginRequest request) {
        return Response.ok(authService.criarUsuario(request)).build();
    }
}