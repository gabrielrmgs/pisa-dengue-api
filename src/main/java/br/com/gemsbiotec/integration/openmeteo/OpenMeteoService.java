package br.com.gemsbiotec.integration.openmeteo;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ServiceUnavailableException;

@ApplicationScoped
public class OpenMeteoService {

    private static final Logger LOG = Logger.getLogger(OpenMeteoService.class);
    private static final String HOURLY_VARIABLES = "temperature_2m,relative_humidity_2m,precipitation";
    private static final String TIMEZONE = "America/Sao_Paulo";
    private static final WeekFields WEEK_FIELDS = WeekFields.of(Locale.forLanguageTag("pt-BR"));

    @Inject
    @RestClient
    OpenMeteoArchiveClient archiveClient;

    @CacheResult(cacheName = "clima-historico")
    public List<ClimaSemana> getClimaSemanalHistorico(
            double latitude,
            double longitude,
            LocalDate inicio,
            LocalDate fim) {
        try {
            OpenMeteoArchiveResponse response = archiveClient.getArchive(
                    latitude,
                    longitude,
                    inicio.toString(),
                    fim.toString(),
                    HOURLY_VARIABLES,
                    TIMEZONE);

            return agregarPorSemana(response);
        } catch (Exception e) {
            LOG.errorf("Falha ao consultar Open-Meteo Archive [lat=%f lon=%f inicio=%s fim=%s]: %s",
                    latitude, longitude, inicio, fim, e.getMessage());
            throw new ServiceUnavailableException("Open-Meteo temporariamente indisponivel.");
        }
    }

    private List<ClimaSemana> agregarPorSemana(OpenMeteoArchiveResponse response) {
        if (response == null || response.hourly == null || response.hourly.time == null) {
            throw new ServiceUnavailableException("Open-Meteo retornou uma resposta incompleta.");
        }

        Map<Integer, AcumuladorSemana> acumuladores = new LinkedHashMap<>();
        for (int i = 0; i < response.hourly.time.size(); i++) {
            LocalDate data = LocalDate.parse(response.hourly.time.get(i).substring(0, 10));
            int semana = data.get(WEEK_FIELDS.weekOfWeekBasedYear());

            AcumuladorSemana acumulador = acumuladores.computeIfAbsent(semana, ignored -> new AcumuladorSemana());
            acumulador.adicionar(
                    data,
                    valorNaPosicao(response.hourly.precipitacao, i),
                    valorNaPosicao(response.hourly.temperatura, i),
                    valorNaPosicao(response.hourly.umidade, i));
        }

        List<ClimaSemana> semanas = new ArrayList<>();
        acumuladores.forEach((semana, acumulador) -> semanas.add(acumulador.toClimaSemana(semana)));
        return semanas;
    }

    private Double valorNaPosicao(List<Double> valores, int index) {
        if (valores == null || index >= valores.size()) {
            return null;
        }
        return valores.get(index);
    }

    public record ClimaSemana(
            int semana,
            LocalDate dataInicio,
            LocalDate dataFim,
            Double precipitacaoMm,
            Double temperaturaMediaC,
            Double umidadeMediaPct) {
    }

    private static class AcumuladorSemana {
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private double precipitacao;
        private double temperatura;
        private int leiturasTemperatura;
        private double umidade;
        private int leiturasUmidade;

        void adicionar(LocalDate data, Double precipitacaoDia, Double temperaturaDia, Double umidadeDia) {
            dataInicio = dataInicio == null || data.isBefore(dataInicio) ? data : dataInicio;
            dataFim = dataFim == null || data.isAfter(dataFim) ? data : dataFim;

            precipitacao += Optional.ofNullable(precipitacaoDia).orElse(0.0);

            if (temperaturaDia != null) {
                temperatura += temperaturaDia;
                leiturasTemperatura++;
            }

            if (umidadeDia != null) {
                umidade += umidadeDia;
                leiturasUmidade++;
            }
        }

        ClimaSemana toClimaSemana(int semana) {
            return new ClimaSemana(
                    semana,
                    dataInicio,
                    dataFim,
                    precipitacao,
                    leiturasTemperatura == 0 ? null : temperatura / leiturasTemperatura,
                    leiturasUmidade == 0 ? null : umidade / leiturasUmidade);
        }
    }
}
