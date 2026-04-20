package br.com.gemsbiotec.integration.infodengue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Representa um registro semanal retornado pela API do InfoDengue.
 *
 * Campos mapeados do JSON de resposta:
 * {
 *   "data_iniSE": "2024-01-07",
 *   "SE": 202401,
 *   "casos_est": 3.5,
 *   "casos_est_min": 1.0,
 *   "casos_est_max": 8.0,
 *   "casos": 2,
 *   "municipio_geocodigo": 2201903,
 *   "p_rt1": 0.87,
 *   "p_inc100k": 5.2,
 *   "Rt": 1.12,
 *   "versao_modelo": "2024-01-14",
 *   "Localidade_id": 0,
 *   "nivel": 2,
 *   "id": 123456,
 *   "versao_modelo_id": 7,
 *   "municipio_nome": "Bom Jesus",
 *   "nivel_inc": 3
 * }
 *
 * nivel: 1=verde, 2=amarelo, 3=laranja, 4=vermelho
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertaSemanalDTO {

    /** Data de início da Semana Epidemiológica (formato AAAA-MM-DD) */
    @JsonProperty("data_iniSE")
    public LocalDate dataInicioSE;

    /** Código numérico da SE no formato AAAAMM (ex: 202401) */
    @JsonProperty("SE")
    public Integer semanaEpidemiologica;

    /** Casos estimados pelo modelo */
    @JsonProperty("casos_est")
    public Double casosEstimados;

    /** Limite inferior do intervalo de confiança */
    @JsonProperty("casos_est_min")
    public Double casosEstimadosMin;

    /** Limite superior do intervalo de confiança */
    @JsonProperty("casos_est_max")
    public Double casosEstimadosMax;

    /** Casos notificados confirmados */
    @JsonProperty("casos")
    public Integer casosNotificados;

    /** Geocódigo IBGE do município */
    @JsonProperty("municipio_geocodigo")
    public Long municipioGeocodigo;

    /** Probabilidade de estar em fase de crescimento (Rt > 1) */
    @JsonProperty("p_rt1")
    public Double probRtMaiorUm;

    /** Incidência estimada por 100 mil habitantes */
    @JsonProperty("p_inc100k")
    public Double incidenciaPor100k;

    /** Número reprodutivo efetivo */
    @JsonProperty("Rt")
    public Double rt;

    /**
     * Nível de alerta epidemiológico:
     * 1 = verde  (baixo risco)
     * 2 = amarelo (risco moderado)
     * 3 = laranja (alto risco)
     * 4 = vermelho (muito alto risco)
     */
    @JsonProperty("nivel")
    public Integer nivel;

    /** Nome do município */
    @JsonProperty("municipio_nome")
    public String municipioNome;

    /** Nível de incidência histórica */
    @JsonProperty("nivel_inc")
    public Integer nivelIncidencia;

    // ── helpers ─────────────────────────────────────────────────────────────

    public String nivelComoTexto() {
        return switch (nivel != null ? nivel : 0) {
            case 1  -> "Baixo";
            case 2  -> "Moderado";
            case 3  -> "Alto";
            case 4  -> "Muito Alto";
            default -> "Indeterminado";
        };
    }

    public String nivelComoCor() {
        return switch (nivel != null ? nivel : 0) {
            case 1  -> "#22c55e";
            case 2  -> "#eab308";
            case 3  -> "#f97316";
            case 4  -> "#ef4444";
            default -> "#6b7280";
        };
    }
}

