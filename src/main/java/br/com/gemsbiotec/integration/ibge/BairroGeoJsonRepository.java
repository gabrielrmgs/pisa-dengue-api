package br.com.gemsbiotec.integration.ibge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.util.Map;

/**
 * Repositório de malhas GeoJSON de bairros.
 *
 * Estratégia por município:
 * - Bom Jesus (2201903): usa arquivo local em classpath
 * (resources/geojson/bairros_2201903.json) — contém os 9 bairros com
 * NM_BAIRRO, CD_BAIRRO e geometrias precisas que a API do IBGE não fornece.
 * - Demais municípios: fallback para a API do IBGE (malha municipal sem
 * bairros).
 *
 * Expõe o GeoJSON enriquecido com dados de incidência para colorir
 * o heatmap do Leaflet via choropleth.
 */
@ApplicationScoped
public class BairroGeoJsonRepository {

    private static final Logger LOG = Logger.getLogger(BairroGeoJsonRepository.class);

    @Inject
    ObjectMapper mapper;

    @RestClient
    IbgeMalhaClient ibgeMalhaClient;

    // ── leitura do arquivo ────────────────────────────────────────────────────

    /**
     * Retorna o GeoJSON bruto do município.
     * Cache longo — geometria não muda.
     */
    @CacheResult(cacheName = "geojson-bairros")
    public String getGeoJsonBruto(String geocodigo) {
        // Tenta arquivo local primeiro
        String resourcePath = "/bairros_" + geocodigo + ".geojson";
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                String json = new String(is.readAllBytes());
                LOG.infof("GeoJSON carregado do classpath para geocodigo=%s (%d bytes)",
                        geocodigo, json.length());
                return json;
            }
            return "Arquivo geoJson não encontrado";
        } catch (Exception e) {
            LOG.warnf("Falha ao ler GeoJSON local [%s]: %s", resourcePath, e.getMessage());
            return "{ \"type\": \"FeatureCollection\", \"features\": [] }";
        }

        // Fallback: API IBGE (malha municipal sem bairros)
        // comentado devido erro de tratamento, olharr esse fallback depois
        // LOG.warnf("GeoJSON local não encontrado para geocodigo=%s — usando malha IBGE
        // sem bairros", geocodigo);
        // try {
        // return ibgeMalhaClient.getMalhaMunicipio(geocodigo, "geojson", "minima");
        // } catch (Exception e) {
        // LOG.errorf("Fallback IBGE também falhou para geocodigo=%s: %s", geocodigo,
        // e.getMessage());
        // return "{ \"type\": \"FeatureCollection\", \"features\": [] }";
        // }
    }

    // ── enriquecimento com dados de incidência ────────────────────────────────

    /**
     * Retorna o GeoJSON com a propriedade "casos" injetada em cada Feature,
     * pronto para o Leaflet usar como choropleth.
     *
     * @param geocodigo      Código IBGE do município
     * @param casosPorBairro Map<CD_BAIRRO, totalCasos> — vindo da tabela de casos
     *                       do BD
     *
     *                       Exemplo de feature enriquecida:
     *                       {
     *                       "type": "Feature",
     *                       "properties": {
     *                       "CD_BAIRRO": "2201903001",
     *                       "NM_BAIRRO": "Centro",
     *                       "casos": 42,
     *                       "incidencia": 312.5 ← calculada se populacao_bairro
     *                       disponível
     *                       },
     *                       "geometry": { ... }
     *                       }
     */
    public String getGeoJsonEnriquecido(String geocodigo, Map<String, Integer> casosPorBairro) {
        try {
            JsonNode root = mapper.readTree(getGeoJsonBruto(geocodigo));
            ArrayNode features = (ArrayNode) root.get("features");

            for (JsonNode feature : features) {
                ObjectNode props = (ObjectNode) feature.get("properties");
                String cdBairro = props.path("CD_BAIRRO").asText("");

                int casos = casosPorBairro.getOrDefault(cdBairro, 0);
                props.put("casos", casos);
            }

            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            LOG.errorf("Falha ao enriquecer GeoJSON [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return getGeoJsonBruto(geocodigo);
        }
    }

    // ── metadados dos bairros ─────────────────────────────────────────────────

    /**
     * Retorna lista dos bairros disponíveis no GeoJSON local.
     * Útil para popular selects de filtro no dashboard.
     */
    @CacheResult(cacheName = "geojson-bairros-meta")
    public java.util.List<BairroMetaDTO> listBairros(String geocodigo) {
        try {
            JsonNode root = mapper.readTree(getGeoJsonBruto(geocodigo));
            ArrayNode features = (ArrayNode) root.get("features");
            var bairros = new java.util.ArrayList<BairroMetaDTO>();

            for (JsonNode feature : features) {
                JsonNode props = feature.get("properties");
                bairros.add(new BairroMetaDTO(
                        props.path("CD_BAIRRO").asText(),
                        props.path("NM_BAIRRO").asText(),
                        props.path("CD_MUN").asText()));
            }

            bairros.sort(java.util.Comparator.comparing(b -> b.nome()));
            return bairros;
        } catch (Exception e) {
            LOG.errorf("Falha ao listar bairros [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    /** Metadados mínimos de um bairro para uso em selects e labels */
    public record BairroMetaDTO(String codigo, String nome, String codigoMunicipio) {
    }
}
