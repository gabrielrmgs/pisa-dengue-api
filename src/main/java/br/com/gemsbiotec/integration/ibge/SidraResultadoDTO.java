package br.com.gemsbiotec.integration.ibge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * DTO para resultados da API SIDRA do IBGE.
 *
 * O SIDRA retorna um array onde o primeiro elemento é o cabeçalho
 * e os demais são as linhas de dados. Cada elemento é um Map<String, String>.
 *
 * Exemplo de linha de dados:
 * {
 *   "NC":  "Município",
 *   "NN":  "Bom Jesus",
 *   "MC":  "N6",
 *   "MN":  "Município",
 *   "V":   "23114",
 *   "D1C": "2201903",
 *   "D1N": "Bom Jesus",
 *   "D2C": "2022",
 *   "D2N": "2022",
 *   "D3C": "6",
 *   "D3N": "Total",
 *   "D4C": "100362",
 *   "D4N": "Total"
 * }
 *
 * Campos relevantes:
 *   V    = valor (população, domicílios, etc.)
 *   D3N  = sexo (Total / Masculino / Feminino)
 *   D4N  = faixa etária (Total, 0 a 4 anos, 5 a 9 anos, ...)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SidraResultadoDTO {

    /** Valor da variável consultada (ex: número de pessoas) */
    @JsonProperty("V")
    public String valor;

    /** Nome da localidade (município) */
    @JsonProperty("D1N")
    public String municipioNome;

    /** Código da localidade */
    @JsonProperty("D1C")
    public String municipioCodigo;

    /** Período (ano) */
    @JsonProperty("D2N")
    public String periodo;

    /** Classificação de sexo: "Total", "Masculino", "Feminino" */
    @JsonProperty("D3N")
    public String sexo;

    /** Faixa etária: "Total", "0 a 4 anos", "5 a 9 anos", etc. */
    @JsonProperty("D4N")
    public String faixaEtaria;

    // ── helpers ──────────────────────────────────────────────────────────────

    /** Converte o valor string para Long, retornando 0 em caso de erro. */
    public long getValorLong() {
        try {
            return Long.parseLong(valor.replace(".", "").trim());
        } catch (NumberFormatException | NullPointerException e) {
            return 0L;
        }
    }

    /** Retorna true se esta linha representa o total geral (sem corte por sexo/idade). */
    public boolean isTotalGeral() {
        return "Total".equalsIgnoreCase(sexo) && "Total".equalsIgnoreCase(faixaEtaria);
    }
}

// ── DTO para /api/v1/pesquisas/indicadores ────────────────────────────────────

class ResultadoIndicadorDTO {

    @JsonProperty("localidade")
    public LocalidadeDTO localidade;

    @JsonProperty("res")
    public List<Map<String, String>> resultados;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocalidadeDTO {
        public String id;
        public String nivel;
        public String nome;
    }
}
