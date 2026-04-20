package br.com.gemsbiotec.integration.infodengue;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Serviço de negócio para dados do InfoDengue.
 *
 * Responsabilidades:
 * - Encapsular chamadas ao InfoDengueClient
 * - Cache por geocódigo + ano para evitar sobrecarga da API pública
 * - Prover dados agregados prontos para o dashboard
 */
@ApplicationScoped
public class InfoDengueService {

    private static final Logger LOG = Logger.getLogger(InfoDengueService.class);
    private static final String DOENCA_DENGUE = "dengue";
    private static final String FORMAT_JSON = "json";

    @Inject
    @RestClient
    InfoDengueClient client;

    // ── consultas principais ──────────────────────────────────────────────────

    /**
     * Retorna todos os alertas do ano corrente para o município.
     * Cache de 1 hora (configurado em application.properties via caffeine).
     */
    @CacheResult(cacheName = "infodengue-ano")
    public List<AlertaSemanalDTO> getAlertasAnoCorrente(String geocode) {
        int ano = LocalDate.now().getYear();
        return getAlertasPorAno(geocode, ano);
    }

    /**
     * Retorna alertas de um ano específico (SE 1 a 53).
     * Útil para o gráfico comparativo 2024 vs 2025 vs 2026.
     */
    @CacheResult(cacheName = "infodengue-historico")
    public List<AlertaSemanalDTO> getAlertasPorAno(String geocode, int ano) {
        try {
            List<AlertaSemanalDTO> resultado = client.getAlertasPorMunicipio(
                    geocode, DOENCA_DENGUE, FORMAT_JSON,
                    1, 53, ano, ano);
            LOG.infof("InfoDengue: %d registros carregados para geocode=%s ano=%d",
                    resultado.size(), geocode, ano);
            return resultado;
        } catch (Exception e) {
            LOG.errorf("Falha ao consultar InfoDengue [geocode=%s ano=%d]: %s",
                    geocode, ano, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Retorna alertas de múltiplos anos — usado pelo gráfico histórico do
     * dashboard.
     */
    public List<List<AlertaSemanalDTO>> getHistoricoMultiAnos(String geocode, int... anos) {
        return java.util.Arrays.stream(anos)
                .mapToObj(ano -> getAlertasPorAno(geocode, ano))
                .toList();
    }

    // ── agregações para os cards do dashboard ─────────────────────────────────

    /**
     * Total de casos notificados no ano corrente.
     */
    public int getTotalCasosAno(String geocode) {
        return getAlertasAnoCorrente(geocode).stream()
                .mapToInt(a -> a.casosNotificados != null ? a.casosNotificados : 0)
                .sum();
    }

    /**
     * Total de casos no mês corrente (pelo campo data_iniSE).
     */
    public int getTotalCasosMesCorrente(String geocode) {
        int mesAtual = LocalDate.now().getMonthValue();
        int anoAtual = LocalDate.now().getYear();
        return getAlertasAnoCorrente(geocode).stream()
                .filter(a -> a.dataInicioSE != null
                        && a.dataInicioSE.getYear() == anoAtual
                        && a.dataInicioSE.getMonthValue() == mesAtual)
                .mapToInt(a -> a.casosNotificados != null ? a.casosNotificados : 0)
                .sum();
    }

    /**
     * Nível de alerta da semana epidemiológica mais recente disponível.
     * Retorna Optional.empty() se não houver dados.
     */
    public Optional<AlertaSemanalDTO> getUltimoAlerta(String geocode) {
        return getAlertasAnoCorrente(geocode).stream()
                .filter(a -> a.semanaEpidemiologica != null)
                .max(java.util.Comparator.comparingInt(a -> a.semanaEpidemiologica));
    }

    /**
     * Incidência acumulada por 100 mil hab. no ano corrente.
     * Usa o valor estimado pelo modelo quando notificados é zero.
     */
    public double getIncidenciaAcumulada(String geocode) {
        return getAlertasAnoCorrente(geocode).stream()
                .mapToDouble(a -> a.incidenciaPor100k != null ? a.incidenciaPor100k : 0.0)
                .sum();
    }

    /**
     * Número da semana epidemiológica atual usando o calendário epidemiológico
     * brasileiro (primeira semana começa no domingo).
     */
    public int getSemanaEpidemiologicaAtual() {
        return LocalDate.now()
                .get(WeekFields.of(Locale.forLanguageTag("pt-BR")).weekOfWeekBasedYear());
    }
}
