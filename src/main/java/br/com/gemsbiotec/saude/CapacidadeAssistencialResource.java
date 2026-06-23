package br.com.gemsbiotec.saude;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.gemsbiotec.saude.dto.CapacidadeAssistencialResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/capacidades-assistenciais")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Capacidades Assistenciais", description = "Catalogo de capacidades da rede de saude")
public class CapacidadeAssistencialResource {

    private final CapacidadeAssistencialService service;

    public CapacidadeAssistencialResource(CapacidadeAssistencialService service) {
        this.service = service;
    }

    @GET
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Lista o catalogo ativo de capacidades assistenciais")
    public List<CapacidadeAssistencialResponse> listar() {
        return service.listarCatalogo();
    }
}
