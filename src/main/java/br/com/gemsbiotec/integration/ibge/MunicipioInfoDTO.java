package br.com.gemsbiotec.integration.ibge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para o endpoint /api/v1/localidades/municipios/{codigo}
 *
 * Exemplo de resposta:
 * {
 *   "id": 2201903,
 *   "nome": "Bom Jesus",
 *   "microrregiao": { "id": 22008, "nome": "Alto Parnaíba Piauiense", ... },
 *   "regiao-imediata": { ... }
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MunicipioInfoDTO {

    public Long id;
    public String nome;

    @JsonProperty("microrregiao")
    public MicroRegiaoDTO microRegiao;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MicroRegiaoDTO {
        public Long id;
        public String nome;

        @JsonProperty("mesorregiao")
        public MesoRegiaoDTO mesoRegiao;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MesoRegiaoDTO {
        public Long id;
        public String nome;

        @JsonProperty("UF")
        public UfDTO uf;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UfDTO {
        public Integer id;
        public String sigla;
        public String nome;
    }
}
