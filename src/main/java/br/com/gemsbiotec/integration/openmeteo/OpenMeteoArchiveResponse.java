package br.com.gemsbiotec.integration.openmeteo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoArchiveResponse {

    public Hourly hourly;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hourly {
        public List<String> time;

        @JsonProperty("temperature_2m")
        public List<Double> temperatura;

        @JsonProperty("precipitation")
        public List<Double> precipitacao;

        @JsonProperty("relative_humidity_2m")
        public List<Double> umidade;
    }
}
