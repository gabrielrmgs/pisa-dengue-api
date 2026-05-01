package br.com.gemsbiotec.mapa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.mapa.dto.Feature;
import br.com.gemsbiotec.mapa.dto.FeatureCollection;
import br.com.gemsbiotec.repository.BairroRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/mapa")
@Tag(name = "Mapa", description = "GeoJSON de bairros para visualização no Leaflet")
public class MapaResource {

    private static final Logger LOG = Logger.getLogger(MapaResource.class);

    private BairroRepository bairroRepository;

    @Inject
    public MapaResource(BairroRepository bairroRepository) {
        this.bairroRepository = bairroRepository;
    }

    @GET
    @Path("/geojson")
    @Produces("application/geo+json")
    @PermitAll
    @Operation(summary = "GeoJSON com polígonos dos bairros")
    public Response getGeoJson() {

        LOG.info("Buscando bairros");

        List<Bairro> bairrosMunicipioUsuario = bairroRepository.listar();

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

}
