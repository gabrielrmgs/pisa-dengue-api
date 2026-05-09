package br.com.gemsbiotec.shapefile;

import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.repository.BairroRepository;
import br.com.gemsbiotec.repository.MunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class BairroDemografiaCsvService {

    private static final String CSV_PADRAO_RESOURCES = "csv/Agregados_por_bairros_demografia_BR.csv";
    private static final Path CSV_PADRAO_DESENVOLVIMENTO = Path.of(
            "docs-codex/contexto/extrator-bairros/Agregados_por_bairros_demografia_BR_Apenas_Municipio_Uniao.csv");

    @Inject
    BairroRepository bairroRepository;

    @Inject
    MunicipioRepository municipioRepository;

    @Transactional
    public ResultadoImportacao importar(String caminhoArquivo) throws IOException {
        ResultadoImportacao resultado;

        if (caminhoArquivo != null && !caminhoArquivo.isBlank()) {
            try (BufferedReader reader = Files.newBufferedReader(Path.of(caminhoArquivo), StandardCharsets.UTF_8)) {
                resultado = importar(reader);
            }
        } else {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PADRAO_RESOURCES);
            if (inputStream == null) {
                throw new IOException("CSV nao encontrado. Informe o parametro 'arquivo' ou adicione o arquivo em "
                        + CSV_PADRAO_RESOURCES + ".");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                resultado = importar(reader);
            }
        }

        return resultado;
    }

    private ResultadoImportacao importar(BufferedReader reader) throws IOException {
        String headerLine = reader.readLine();
        if (headerLine == null || headerLine.isBlank()) {
            throw new IOException("CSV vazio ou sem cabecalho.");
        }

        Map<String, Integer> header = montarIndiceCabecalho(parseCsvLine(headerLine));
        validarCabecalho(header);

        ResultadoImportacao resultado = new ResultadoImportacao();
        Map<String, Optional<Municipio>> municipioCache = new HashMap<>();

        String line;
        int numeroLinha = 1;
        while ((line = reader.readLine()) != null) {
            numeroLinha++;
            if (line.isBlank()) {
                continue;
            }

            List<String> row = parseCsvLine(line);
            String codigoBairro = get(row, header, "CD_BAIRRO");
            String nomeBairro = get(row, header, "NM_BAIRRO");

            if (codigoBairro == null || codigoBairro.length() < 10) {
                resultado.linhasInvalidas++;
                resultado.adicionarAviso("Linha " + numeroLinha + ": CD_BAIRRO invalido.");
                continue;
            }

            String codigoMunicipio = codigoBairro.substring(0, 7);
            Optional<Municipio> municipio = municipioCache.computeIfAbsent(codigoMunicipio,
                    municipioRepository::findByCodigoIbge);

            if (municipio.isEmpty()) {
                resultado.municipiosNaoEncontrados++;
                resultado.adicionarAviso("Linha " + numeroLinha + ": municipio " + codigoMunicipio
                        + " nao encontrado para o bairro " + codigoBairro + ".");
                continue;
            }

            Optional<Bairro> bairroExistente = bairroRepository.findByCodigo(codigoBairro);
            Bairro bairro = bairroExistente.orElseGet(() -> {
                Bairro novo = new Bairro();
                novo.setCodigo(codigoBairro);
                novo.setMunicipio(municipio.get());
                bairroRepository.persist(novo);
                return novo;
            });

            if (bairroExistente.isPresent()) {
                resultado.bairrosAtualizados++;
            } else {
                resultado.bairrosCriados++;
            }

            bairro.setNome(nomeBairro);
            bairro.setMunicipio(municipio.get());
            preencherDemografia(bairro, row, header);
            resultado.linhasProcessadas++;
        }

        return resultado;
    }

    private void preencherDemografia(Bairro bairro, List<String> row, Map<String, Integer> header) {
        bairro.setPopulacao(getInt(row, header, "V01006"));
        bairro.setSexoMasculino(getInt(row, header, "V01007"));
        bairro.setSexoFeminino(getInt(row, header, "V01008"));
        bairro.setMasculino0a4Anos(getInt(row, header, "V01009"));
        bairro.setMasculino5a9Anos(getInt(row, header, "V01010"));
        bairro.setMasculino10a14Anos(getInt(row, header, "V01011"));
        bairro.setMasculino15a19Anos(getInt(row, header, "V01012"));
        bairro.setMasculino20a24Anos(getInt(row, header, "V01013"));
        bairro.setMasculino25a29Anos(getInt(row, header, "V01014"));
        bairro.setMasculino30a39Anos(getInt(row, header, "V01015"));
        bairro.setMasculino40a49Anos(getInt(row, header, "V01016"));
        bairro.setMasculino50a59Anos(getInt(row, header, "V01017"));
        bairro.setMasculino60a69Anos(getInt(row, header, "V01018"));
        bairro.setMasculino70AnosOuMais(getInt(row, header, "V01019"));
        bairro.setFeminino0a4Anos(getInt(row, header, "V01020"));
        bairro.setFeminino5a9Anos(getInt(row, header, "V01021"));
        bairro.setFeminino10a14Anos(getInt(row, header, "V01022"));
        bairro.setFeminino15a19Anos(getInt(row, header, "V01023"));
        bairro.setFeminino20a24Anos(getInt(row, header, "V01024"));
        bairro.setFeminino25a29Anos(getInt(row, header, "V01025"));
        bairro.setFeminino30a39Anos(getInt(row, header, "V01026"));
        bairro.setFeminino40a49Anos(getInt(row, header, "V01027"));
        bairro.setFeminino50a59Anos(getInt(row, header, "V01028"));
        bairro.setFeminino60a69Anos(getInt(row, header, "V01029"));
        bairro.setFeminino70AnosOuMais(getInt(row, header, "V01030"));
        bairro.setMoradores0a4Anos(getInt(row, header, "V01031"));
        bairro.setMoradores5a9Anos(getInt(row, header, "V01032"));
        bairro.setMoradores10a14Anos(getInt(row, header, "V01033"));
        bairro.setMoradores15a19Anos(getInt(row, header, "V01034"));
        bairro.setMoradores20a24Anos(getInt(row, header, "V01035"));
        bairro.setMoradores25a29Anos(getInt(row, header, "V01036"));
        bairro.setMoradores30a39Anos(getInt(row, header, "V01037"));
        bairro.setMoradores40a49Anos(getInt(row, header, "V01038"));
        bairro.setMoradores50a59Anos(getInt(row, header, "V01039"));
        bairro.setMoradores60a69Anos(getInt(row, header, "V01040"));
        bairro.setMoradores70AnosOuMais(getInt(row, header, "V01041"));
    }

    private Map<String, Integer> montarIndiceCabecalho(List<String> colunas) {
        Map<String, Integer> header = new HashMap<>();
        for (int i = 0; i < colunas.size(); i++) {
            header.put(colunas.get(i).trim(), i);
        }
        return header;
    }

    private void validarCabecalho(Map<String, Integer> header) throws IOException {
        for (String coluna : List.of("CD_BAIRRO", "NM_BAIRRO", "V01006")) {
            if (!header.containsKey(coluna)) {
                throw new IOException("Coluna obrigatoria ausente no CSV: " + coluna);
            }
        }
    }

    private String get(List<String> row, Map<String, Integer> header, String coluna) {
        Integer index = header.get(coluna);
        if (index == null || index >= row.size()) {
            return null;
        }
        return row.get(index).trim();
    }

    private Integer getInt(List<String> row, Map<String, Integer> header, String coluna) {
        String value = get(row, header, coluna);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        values.add(current.toString());
        return values;
    }

    public static class ResultadoImportacao {
        public int linhasProcessadas;
        public int bairrosCriados;
        public int bairrosAtualizados;
        public int municipiosNaoEncontrados;
        public int linhasInvalidas;
        public List<String> avisos = new ArrayList<>();

        private void adicionarAviso(String aviso) {
            if (avisos.size() < 20) {
                avisos.add(aviso);
            }
        }
    }
}
