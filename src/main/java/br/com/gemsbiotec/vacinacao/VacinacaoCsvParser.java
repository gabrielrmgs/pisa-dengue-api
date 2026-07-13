package br.com.gemsbiotec.vacinacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VacinacaoCsvParser {

    private VacinacaoCsvParser() {
    }

    public record LinhaVacinacaoCsv(String unidadeNome, int doses10a14, int doses18a59, int numeroLinha) {
    }

    public record ResultadoParseCsv(List<LinhaVacinacaoCsv> linhas, List<String> avisos, int linhasInvalidas) {
    }

    public static ResultadoParseCsv parse(BufferedReader reader) throws IOException {
        String headerLine = reader.readLine();
        if (headerLine == null || headerLine.isBlank()) {
            throw new IOException("CSV vazio ou sem cabecalho.");
        }

        Map<String, Integer> header = new HashMap<>();
        List<String> colunas = splitLine(headerLine);
        for (int i = 0; i < colunas.size(); i++) {
            header.put(colunas.get(i).trim().toLowerCase(), i);
        }
        for (String obrigatoria : List.of("unidade_saude", "doses_10_14", "doses_18_59")) {
            if (!header.containsKey(obrigatoria)) {
                throw new IOException("Coluna obrigatoria ausente no CSV: " + obrigatoria);
            }
        }

        List<LinhaVacinacaoCsv> linhas = new ArrayList<>();
        List<String> avisos = new ArrayList<>();
        int linhasInvalidas = 0;

        String line;
        int numeroLinha = 1;
        while ((line = reader.readLine()) != null) {
            numeroLinha++;
            if (line.isBlank()) {
                continue;
            }
            List<String> valores = splitLine(line);
            String unidadeNome = get(valores, header, "unidade_saude");
            String doses10a14Raw = get(valores, header, "doses_10_14");
            String doses18a59Raw = get(valores, header, "doses_18_59");

            if (unidadeNome == null || unidadeNome.isBlank()) {
                linhasInvalidas++;
                avisos.add("Linha " + numeroLinha + ": unidade_saude vazio.");
                continue;
            }
            Integer doses10a14 = parseInteiro(doses10a14Raw);
            Integer doses18a59 = parseInteiro(doses18a59Raw);
            if (doses10a14 == null || doses18a59 == null) {
                linhasInvalidas++;
                avisos.add("Linha " + numeroLinha + ": doses_10_14/doses_18_59 devem ser numeros inteiros.");
                continue;
            }

            linhas.add(new LinhaVacinacaoCsv(unidadeNome.trim(), doses10a14, doses18a59, numeroLinha));
        }

        return new ResultadoParseCsv(linhas, avisos, linhasInvalidas);
    }

    private static Integer parseInteiro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String get(List<String> row, Map<String, Integer> header, String coluna) {
        Integer index = header.get(coluna);
        if (index == null || index >= row.size()) {
            return null;
        }
        return row.get(index);
    }

    private static List<String> splitLine(String line) {
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
}
