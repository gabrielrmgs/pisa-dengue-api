package br.com.gemsbiotec.shapefile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.geo.Estado;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.repository.BairroRepository;
import br.com.gemsbiotec.repository.EstadoRepository;
import br.com.gemsbiotec.repository.MunicipioRepository;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class ShapefileService {

    @Inject
    EstadoRepository estadoRepository;

    @Inject
    MunicipioRepository municipioRepository;

    @Inject
    BairroRepository bairroRepository;

    @Transactional
    public String importarBairrosPiaui() throws Exception {

        // // 1. Pega o recurso do JAR
        // InputStream is = getClass().getClassLoader().getResourceAsStream("shapefiles/PI_bairros_CD2022.shp");

        // // 2. Cria um arquivo temporário no sistema de arquivos do Linux (Docker)
        // Path tempFile = Files.createTempFile("mapa_", ".shp");
        // Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);

        // // 3. Usa o arquivo temporário
        // File file = tempFile.toFile();

        // // Caminho do arquivo. Ajuste para onde você descompactou os arquivos do IBGE
        // // File file = new
        // // File("src\\main\\resources\\shapefiles\\PI_bairros_CD2022.shp");

        // Map<String, Object> map = new HashMap<>();
        // map.put("url", file.toURI().toURL());

        // 1. Cria um diretório temporário no Linux para hospedar todos os arquivos do
        // Shapefile
        Path tempDir = Files.createTempDirectory("shape_piaui_");

        String baseName = "PI_bairros_CD2022";
        String[] extensoes = { ".shp", ".shx", ".dbf", ".prj", ".cpg" }; // Arquivos que compõem o shapefile
        File mainShapeFile = null;

        // 2. Extrai cada arquivo do JAR para o diretório temporário
        for (String ext : extensoes) {
            String fileName = baseName + ext;
            InputStream is = getClass().getClassLoader().getResourceAsStream("shapefiles/" + fileName);

            if (is != null) {
                Path tempFile = tempDir.resolve(fileName);
                Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);

                // Guarda a referência do .shp principal para passar para o GeoTools
                if (ext.equals(".shp")) {
                    mainShapeFile = tempFile.toFile();
                }
            }
        }

        if (mainShapeFile == null) {
            throw new RuntimeException("Arquivo principal .shp não encontrado nos resources.");
        }

        // 3. Passa o arquivo .shp (que agora está junto com seus irmãos .dbf, .shx)
        // para o GeoTools
        Map<String, Object> map = new HashMap<>();
        map.put("url", mainShapeFile.toURI().toURL());

        DataStore dataStore = DataStoreFinder.getDataStore(map);

      //  DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);

        Map<String, Estado> estadoCache = new HashMap<>();
        Map<String, Municipio> municipioCache = new HashMap<>();

        // Set para guardar o nome dos municípios criados nesta execução e evitar nomes
        // duplicados na string
        Set<String> municipiosCriados = new HashSet<>();

        try (FeatureIterator<SimpleFeature> features = source.getFeatures().features()) {
            GeometryFactory geometryFactory = new GeometryFactory();

            while (features.hasNext()) {
                SimpleFeature feature = features.next();

                // 1. Estado
                String codUf = (String) feature.getAttribute("CD_UF");
                Estado estado = estadoCache.computeIfAbsent(codUf,
                        k -> estadoRepository.findByCodigoUf(codUf).orElseGet(() -> {
                            Estado novo = new Estado();
                            novo.setcodigoUf(codUf);
                            novo.setNome((String) feature.getAttribute("NM_UF"));
                            novo.setSigla("PI");
                            estadoRepository.persist(novo); // Panache usa persist()
                            return novo;
                        }));

                // 2. Município
                String codMun = (String) feature.getAttribute("CD_MUN");
                Municipio municipio = municipioCache.computeIfAbsent(codMun,
                        k -> municipioRepository.findByCodigoIbge(codMun).orElseGet(() -> {
                            Municipio novo = new Municipio();
                            novo.setCodigoIbge(codMun);
                            novo.setNome((String) feature.getAttribute("NM_MUN"));
                            novo.setEstado(estado);
                            novo.setAtivo(true);
                            municipioRepository.persist(novo);

                            // Adiciona à nossa lista de municípios criados
                            municipiosCriados.add(novo.getNome());
                            return novo;
                        }));

                // 3. Bairro e Geometria
                Bairro bairro = new Bairro();
                bairro.setCodigo((String) feature.getAttribute("CD_BAIRRO"));
                bairro.setNome((String) feature.getAttribute("NM_BAIRRO"));
                bairro.setMunicipio(municipio);

                Geometry geometry = (Geometry) feature.getDefaultGeometry();

                if (geometry != null) {

                    if (geometry instanceof Polygon poly) {
                        bairro.setGeometria(geometryFactory.createMultiPolygon(new Polygon[] { poly }));

                    } else if (geometry instanceof MultiPolygon mp) {
                        bairro.setGeometria(mp);

                    }
                }

                if (bairro.getGeometria() != null) {
                    bairro.getGeometria().setSRID(4326);
                }
                bairroRepository.persist(bairro);
            }
        } finally {
            dataStore.dispose();
        }

        // Formata o retorno
        if (municipiosCriados.isEmpty()) {
            return "Processamento concluído. Nenhum novo município foi criado (todos já existiam no banco).";
        }

        return "Municípios criados com sucesso: " + String.join(", ", municipiosCriados);
    }
}