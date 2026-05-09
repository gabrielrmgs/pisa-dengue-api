package br.com.gemsbiotec.pisa.web;

import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.integration.ibge.IbgeService;
import br.com.gemsbiotec.integration.infodengue.AlertaSemanalDTO;
import br.com.gemsbiotec.integration.infodengue.InfoDengueService;
import br.com.gemsbiotec.mapa.MapaService;
import br.com.gemsbiotec.pisa.dto.DashboardResumoResponse;
import br.com.gemsbiotec.repository.MunicipioRepository;
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
    private final MunicipioRepository municipioRepository;
    private final TenantContext tenantContext;

    public DashboardResource(
            IbgeService ibgeService,
            InfoDengueService infoDengueService,
            MapaService mapaService,
            MunicipioRepository municipioRepository,
            TenantContext tenantContext) {
        this.ibgeService = ibgeService;
        this.infoDengueService = infoDengueService;
        this.mapaService = mapaService;
        this.municipioRepository = municipioRepository;
        this.tenantContext = tenantContext;
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

        if (anoConsulta < 2000 || anoConsulta > 2100) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Ano de consulta inválido.")
                    .build();
        }
        List<AlertaSemanalDTO> alertas = infoDengueService.getAlertasPorAno(anoConsulta);

        return Response.ok(alertas).build();
    }

    @GET
    @Path("/resumo")
    @RolesAllowed({ "ADMIN", "GESTOR", "AGENTE", "VIEWER" })
    @Operation(summary = "Resumo epidemiologico do municipio do usuario logado")
    public Response dashboardResumo() {

        Long municipioId = tenantContext.getMunicipioId();
        if (municipioId == null) {
            return Response.status(Status.UNAUTHORIZED)
                    .entity("Municipio nao encontrado no token.")
                    .build();
        }

        Optional<Municipio> municipioOptional = municipioRepository.findAtivoById(municipioId);
        if (municipioOptional.isEmpty()) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Municipio ativo nao encontrado.")
                    .build();
        }

        Municipio municipio = municipioOptional.get();
        String geocode = municipio.getCodigoIbge();

        int totalCasosAno = infoDengueService.getTotalCasosAno(geocode);
        int totalCasosMes = infoDengueService.getTotalCasosMesCorrente(geocode);
        double incidenciaAcumulada = infoDengueService.getIncidenciaAcumulada(geocode);
        int semanaEpidemiologicaAtual = infoDengueService.getSemanaEpidemiologicaAtual();

        Optional<AlertaSemanalDTO> ultimoAlerta = infoDengueService.getUltimoAlerta(geocode);
        String nivelAlerta = ultimoAlerta
                .map(AlertaSemanalDTO::nivelComoTexto)
                .orElse("Indeterminado");
        String corAlerta = ultimoAlerta
                .map(AlertaSemanalDTO::nivelComoCor)
                .orElse("#6b7280");
        Integer semanaUltimoAlerta = ultimoAlerta
                .map(alerta -> alerta.semanaEpidemiologica)
                .orElse(null);

        long populacao = municipio.getPopulacao() > 0
                ? municipio.getPopulacao()
                : ibgeService.getPopulacaoTotal(geocode);

        DashboardResumoResponse resumo = new DashboardResumoResponse(
                municipio.getId(),
                municipio.getNome(),
                geocode,
                municipio.getEstado() != null ? municipio.getEstado().getNome() : null,
                municipio.getEstado() != null ? municipio.getEstado().getSigla() : null,
                populacao,
                totalCasosAno,
                totalCasosMes,
                incidenciaAcumulada,
                semanaEpidemiologicaAtual,
                semanaUltimoAlerta,
                nivelAlerta,
                corAlerta);

        LOG.infof("Resumo do dashboard gerado para municipioId=%d geocode=%s", municipio.getId(), geocode);

        return Response.ok(resumo).build();

    }

}
