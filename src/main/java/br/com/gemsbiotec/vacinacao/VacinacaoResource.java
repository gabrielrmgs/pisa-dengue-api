package br.com.gemsbiotec.vacinacao;

import java.util.List;

import br.com.gemsbiotec.vacinacao.dto.RegistrarDoseRequest;
import br.com.gemsbiotec.vacinacao.dto.VacinacaoResumoResponse;
import br.com.gemsbiotec.vacinacao.dto.VacinacaoUnidadeResponse;
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

@Path("/api/v1/vacinacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Vacinacao", description = "Doses aplicadas, cobertura por bairro e planejamento da campanha de vacinacao contra dengue")
public class VacinacaoResource {

    private final VacinacaoService service;

    public VacinacaoResource(VacinacaoService service) {
        this.service = service;
    }

    @GET
    @Path("/resumo")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Resumo agregado da campanha de vacinacao do municipio")
    public VacinacaoResumoResponse resumo() {
        return service.resumo();
    }

    @GET
    @Path("/unidades")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Indicadores de vacinacao por unidade de saude")
    public List<VacinacaoUnidadeResponse> unidades() {
        return service.listarUnidades();
    }

    @POST
    @Path("/registros")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE" })
    @Operation(summary = "Registra doses aplicadas manualmente para uma unidade, fora do ciclo de importacao do SI-PNI")
    public VacinacaoUnidadeResponse registrarDose(@Valid RegistrarDoseRequest request) {
        return service.registrarDose(request);
    }
}
