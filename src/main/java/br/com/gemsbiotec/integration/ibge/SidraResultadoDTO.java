package br.com.gemsbiotec.integration.ibge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * DTO para resultados da API SIDRA do IBGE.
 *
 * Na tabela 9514, com a URL usada pelo projeto, a ordem dos descritores e:
 * D1 = Municipio
 * D2 = Variavel
 * D3 = Ano
 * D4 = Sexo
 * D5 = Idade
 * D6 = Forma de declaracao da idade
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SidraResultadoDTO {

    private static final String SEXO_TOTAL = "6794";
    private static final String SEXO_HOMENS = "4";
    private static final String SEXO_MULHERES = "5";
    private static final String IDADE_TOTAL = "100362";
    private static final String FORMA_TOTAL = "113635";

    @JsonProperty("V")
    public String valor;

    @JsonProperty("D1N")
    public String municipioNome;

    @JsonProperty("D1C")
    public String municipioCodigo;

    @JsonProperty("D2N")
    public String variavel;

    @JsonProperty("D2C")
    public String variavelCodigo;

    @JsonProperty("D3N")
    public String periodo;

    @JsonProperty("D3C")
    public String periodoCodigo;

    @JsonProperty("D4N")
    public String sexo;

    @JsonProperty("D4C")
    public String sexoCodigo;

    @JsonProperty("D5N")
    public String faixaEtaria;

    @JsonProperty("D5C")
    public String faixaEtariaCodigo;

    @JsonProperty("D6N")
    public String formaDeclaracaoIdade;

    @JsonProperty("D6C")
    public String formaDeclaracaoIdadeCodigo;

    public long getValorLong() {
        if (valor == null || "-".equals(valor) || "X".equalsIgnoreCase(valor)
                || "..".equals(valor) || "...".equals(valor) || "Valor".equalsIgnoreCase(valor)) {
            return 0L;
        }

        try {
            return Long.parseLong(valor.replace(".", "").trim());
        } catch (NumberFormatException | NullPointerException e) {
            return 0L;
        }
    }

    public boolean isTotalGeral() {
        return isSexoTotal() && isIdadeTotal() && isFormaDeclaracaoTotal();
    }

    public boolean isSexoTotal() {
        return SEXO_TOTAL.equals(sexoCodigo) || "Total".equalsIgnoreCase(sexo);
    }

    public boolean isSexoMasculino() {
        return SEXO_HOMENS.equals(sexoCodigo)
                || "Homens".equalsIgnoreCase(sexo)
                || "Masculino".equalsIgnoreCase(sexo);
    }

    public boolean isSexoFeminino() {
        return SEXO_MULHERES.equals(sexoCodigo)
                || "Mulheres".equalsIgnoreCase(sexo)
                || "Feminino".equalsIgnoreCase(sexo);
    }

    public boolean isIdadeTotal() {
        return IDADE_TOTAL.equals(faixaEtariaCodigo) || "Total".equalsIgnoreCase(faixaEtaria);
    }

    public boolean isFormaDeclaracaoTotal() {
        return FORMA_TOTAL.equals(formaDeclaracaoIdadeCodigo)
                || "Total".equalsIgnoreCase(formaDeclaracaoIdade)
                || formaDeclaracaoIdade == null;
    }
}

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
