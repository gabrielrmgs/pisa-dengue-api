package br.com.gemsbiotec.mapa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.mapa.dto.Feature;
import br.com.gemsbiotec.mapa.dto.FeatureCollection;
import br.com.gemsbiotec.repository.BairroRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class MapaService {

    private final BairroRepository bairroRepository;

    public MapaService(BairroRepository bairroRepository) {
        this.bairroRepository = bairroRepository;
    }

    public Response geoJsonMunicipio() {

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
