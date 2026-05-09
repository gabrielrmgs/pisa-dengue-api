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

    public Response geoJsonMunicipioComDemografia() {

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
            props.put("populacao", b.getPopulacao());
            props.put("sexo_masculino", b.getSexoMasculino());
            props.put("sexo_feminino", b.getSexoFeminino());
            props.put("masculino_0_a_4_anos", b.getMasculino0a4Anos());
            props.put("masculino_5_a_9_anos", b.getMasculino5a9Anos());
            props.put("masculino_10_a_14_anos", b.getMasculino10a14Anos());
            props.put("masculino_15_a_19_anos", b.getMasculino15a19Anos());
            props.put("masculino_20_a_24_anos", b.getMasculino20a24Anos());
            props.put("masculino_25_a_29_anos", b.getMasculino25a29Anos());
            props.put("masculino_30_a_39_anos", b.getMasculino30a39Anos());
            props.put("masculino_40_a_49_anos", b.getMasculino40a49Anos());
            props.put("masculino_50_a_59_anos", b.getMasculino50a59Anos());
            props.put("masculino_60_a_69_anos", b.getMasculino60a69Anos());
            props.put("masculino_70_anos_ou_mais", b.getMasculino70AnosOuMais());
            props.put("feminino_0_a_4_anos", b.getFeminino0a4Anos());
            props.put("feminino_5_a_9_anos", b.getFeminino5a9Anos());
            props.put("feminino_10_a_14_anos", b.getFeminino10a14Anos());
            props.put("feminino_15_a_19_anos", b.getFeminino15a19Anos());
            props.put("feminino_20_a_24_anos", b.getFeminino20a24Anos());
            props.put("feminino_25_a_29_anos", b.getFeminino25a29Anos());
            props.put("feminino_30_a_39_anos", b.getFeminino30a39Anos());
            props.put("feminino_40_a_49_anos", b.getFeminino40a49Anos());
            props.put("feminino_50_a_59_anos", b.getFeminino50a59Anos());
            props.put("feminino_60_a_69_anos", b.getFeminino60a69Anos());
            props.put("feminino_70_anos_ou_mais", b.getFeminino70AnosOuMais());
            props.put("moradores_0_a_4_anos", b.getMoradores0a4Anos());
            props.put("moradores_5_a_9_anos", b.getMoradores5a9Anos());
            props.put("moradores_10_a_14_anos", b.getMoradores10a14Anos());
            props.put("moradores_15_a_19_anos", b.getMoradores15a19Anos());
            props.put("moradores_20_a_24_anos", b.getMoradores20a24Anos());
            props.put("moradores_25_a_29_anos", b.getMoradores25a29Anos());
            props.put("moradores_30_a_39_anos", b.getMoradores30a39Anos());
            props.put("moradores_40_a_49_anos", b.getMoradores40a49Anos());
            props.put("moradores_50_a_59_anos", b.getMoradores50a59Anos());
            props.put("moradores_60_a_69_anos", b.getMoradores60a69Anos());
            props.put("moradores_70_anos_ou_mais", b.getMoradores70AnosOuMais());
            feature.properties = props;

            collection.features.add(feature);
        }

        return Response.ok(collection).build();
    }
}
