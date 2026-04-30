package br.com.gemsbiotec.mapa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.integration.ibge.BairroGeoJsonRepository;
import br.com.gemsbiotec.integration.ibge.BairroGeoJsonRepository.BairroMetaDTO;
import br.com.gemsbiotec.mapa.dto.Feature;
import br.com.gemsbiotec.mapa.dto.FeatureCollection;
import br.com.gemsbiotec.repository.BairroRepository;
import br.com.gemsbiotec.repository.MunicipioRepository;
import br.com.gemsbiotec.repository.UsuarioRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/mapa")
@Tag(name = "Mapa", description = "GeoJSON de bairros para visualização no Leaflet")
public class MapaResource {

    private static final Logger LOG = Logger.getLogger(MapaResource.class);

    @Inject
    BairroGeoJsonRepository geoJsonRepo;
    // @Inject CasosPorBairroService casosPorBairroService;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    MunicipioRepository municipioRepository;

    @Inject
    BairroRepository bairroRepository;

    @Inject
    JsonWebToken jwt;
    // ── GeoJSON bruto ─────────────────────────────────────────────────────────

    @GET
    @Path("/geojson")
    @Produces("application/geo+json")
    @Operation(summary = "GeoJSON com polígonos dos bairros — sem dados epidemiológicos")
    public Response getGeoJson(@PathParam("geocodigo") String geocodigo) {
        String municipioid = jwt.getClaim("municipio_id").toString();
        System.out.println("Aqui ----> " + municipioid);

        List<Bairro> bairrosMunicipioUsuario = bairroRepository.listByMunicipio(Long.valueOf(municipioid));

        FeatureCollection collection = new FeatureCollection();

        for (Bairro b : bairrosMunicipioUsuario) {
            Feature feature = new Feature();

            feature.geometry = b.getGeometria();

            Map<String, Object> props = new HashMap<>();
            props.put("codigo_ibge_bairro", b.getCodigo());
            props.put("nome_bairro", b.getNome());
            props.put("nome_municipio", b.getMunicipio().getNome());
            props.put("codigo_ibge_municipio", b.getMunicipio().getCodigoIbge());
            props.put("nome_estado", b.getMunicipio().getEstado().getNome());
            props.put("sigla_estado", b.getMunicipio().getEstado().getSigla());
            feature.properties = props;

            collection.features.add(feature);
        }

        return Response.ok(collection).build();
    }

    // ── GeoJSON enriquecido para choropleth ───────────────────────────────────

    // @GET
    // @Path("/geojson/heatmap")
    // @Produces("application/geo+json")
    // @Operation(summary = "GeoJSON com casos por bairro injetados nas properties —
    // usado pelo Leaflet choropleth")
    // public Response getGeoJsonHeatmap(
    // @PathParam("geocodigo") String geocodigo,
    // @QueryParam("ano") @DefaultValue("0") int ano,
    // @QueryParam("mes") @DefaultValue("0") int mes
    // ) {
    // // Busca contagem de casos por CD_BAIRRO no banco local
    // Map<String, Integer> casosPorBairro =
    // casosPorBairroService.getCasosPorBairro(geocodigo, ano, mes);

    // String json = geoJsonRepo.getGeoJsonEnriquecido(geocodigo, casosPorBairro);

    // return Response.ok(json)
    // .header("Cache-Control", "no-cache") // dados mudam com novos casos
    // .build();
    // }

    // ── lista de bairros ──────────────────────────────────────────────────────

    @GET
    @Path("/bairros")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista de bairros do município — para selects e filtros no dashboard")
    public List<BairroMetaDTO> listBairros(@PathParam("geocodigo") String geocodigo) {
        return geoJsonRepo.listBairros(geocodigo);
    }
}
