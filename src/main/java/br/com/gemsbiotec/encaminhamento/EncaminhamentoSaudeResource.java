package br.com.gemsbiotec.encaminhamento;

import java.util.List;

import br.com.gemsbiotec.encaminhamento.dto.EncaminhamentoSaudeResponse;
import br.com.gemsbiotec.encaminhamento.dto.ResponderEncaminhamentoRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/encaminhamentos-saude")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EncaminhamentoSaudeResource {

    private final EncaminhamentoSaudeService service;

    public EncaminhamentoSaudeResource(EncaminhamentoSaudeService service) {
        this.service = service;
    }

    @GET
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    public List<EncaminhamentoSaudeResponse> listar(
            @QueryParam("caixa") String caixa,
            @QueryParam("limite") Integer limite) {
        return service.listar(caixa, limite);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    public EncaminhamentoSaudeResponse buscar(@PathParam("id") Long id) {
        return service.buscar(id);
    }

    @PATCH
    @Path("/{id}/aceitar")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE" })
    public EncaminhamentoSaudeResponse aceitar(@PathParam("id") Long id, @Valid ResponderEncaminhamentoRequest request) {
        return service.aceitar(id, request);
    }

    @PATCH
    @Path("/{id}/recusar")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE" })
    public EncaminhamentoSaudeResponse recusar(@PathParam("id") Long id, @Valid ResponderEncaminhamentoRequest request) {
        return service.recusar(id, request);
    }

    @PATCH
    @Path("/{id}/concluir")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE" })
    public EncaminhamentoSaudeResponse concluir(@PathParam("id") Long id, @Valid ResponderEncaminhamentoRequest request) {
        return service.concluir(id, request);
    }

    @PATCH
    @Path("/{id}/cancelar")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE" })
    public EncaminhamentoSaudeResponse cancelar(@PathParam("id") Long id, @Valid ResponderEncaminhamentoRequest request) {
        return service.cancelar(id, request);
    }
}
