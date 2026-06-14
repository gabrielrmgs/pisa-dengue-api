package br.com.gemsbiotec.pisa;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.integration.infodengue.AlertaSemanalDTO;
import br.com.gemsbiotec.integration.infodengue.InfoDengueService;
import br.com.gemsbiotec.integration.openmeteo.OpenMeteoService;
import br.com.gemsbiotec.integration.openmeteo.OpenMeteoService.ClimaSemana;
import br.com.gemsbiotec.pisa.dto.ClimaCasosCorrelacaoResponse;
import br.com.gemsbiotec.pisa.dto.ClimaCasosCorrelacaoResponse.ClimaCasosSemana;
import br.com.gemsbiotec.pisa.dto.ClimaCasosCorrelacaoResponse.VariavelClimaticaCorrelacao;
import br.com.gemsbiotec.repository.BairroRepository;
import br.com.gemsbiotec.repository.BairroRepository.Coordenada;
import br.com.gemsbiotec.repository.MunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotAuthorizedException;

@ApplicationScoped
public class ClimaCasosCorrelacaoService {

    private static final WeekFields WEEK_FIELDS = WeekFields.of(Locale.forLanguageTag("pt-BR"));

    private final TenantContext tenantContext;
    private final MunicipioRepository municipioRepository;
    private final BairroRepository bairroRepository;
    private final InfoDengueService infoDengueService;
    private final OpenMeteoService openMeteoService;

    public ClimaCasosCorrelacaoService(
            TenantContext tenantContext,
            MunicipioRepository municipioRepository,
            BairroRepository bairroRepository,
            InfoDengueService infoDengueService,
            OpenMeteoService openMeteoService) {
        this.tenantContext = tenantContext;
        this.municipioRepository = municipioRepository;
        this.bairroRepository = bairroRepository;
        this.infoDengueService = infoDengueService;
        this.openMeteoService = openMeteoService;
    }

    public ClimaCasosCorrelacaoResponse correlacao(int ano, int lagSemanas, String variavelSelecionada) {
        Long municipioId = tenantContext.getMunicipioId();
        if (municipioId == null) {
            throw new NotAuthorizedException("Municipio nao encontrado no token.");
        }

        Municipio municipio = municipioRepository.findAtivoById(municipioId)
                .orElseThrow(() -> new NotFoundException("Municipio ativo nao encontrado."));

        Coordenada centroide = bairroRepository.getCentroideMunicipio(municipioId)
                .orElseThrow(() -> new NotFoundException("Nao ha geometria de bairros para calcular o centroide do municipio."));

        LocalDate inicio = LocalDate.of(ano, 1, 1);
        LocalDate fim = ano == LocalDate.now().getYear()
                ? LocalDate.now().minusDays(5)
                : LocalDate.of(ano, 12, 31);
        if (fim.isBefore(inicio)) {
            fim = inicio;
        }

        Map<Integer, AlertaSemanalDTO> alertasPorSemana = infoDengueService
                .getAlertasPorAno(municipio.getCodigoIbge(), ano)
                .stream()
                .filter(alerta -> alerta.semanaEpidemiologica != null)
                .collect(Collectors.toMap(
                        alerta -> semanaDoAno(alerta.semanaEpidemiologica),
                        Function.identity(),
                        (atual, substituto) -> substituto,
                        LinkedHashMap::new));

        Map<Integer, ClimaSemana> climaPorSemana = openMeteoService
                .getClimaSemanalHistorico(centroide.latitude(), centroide.longitude(), inicio, fim)
                .stream()
                .collect(Collectors.toMap(
                        ClimaSemana::semana,
                        Function.identity(),
                        (atual, substituto) -> substituto,
                        LinkedHashMap::new));

        List<ClimaCasosSemana> pontos = montarPontos(alertasPorSemana, climaPorSemana);

        return new ClimaCasosCorrelacaoResponse(
                municipio.getId(),
                municipio.getNome(),
                municipio.getCodigoIbge(),
                ano,
                lagSemanas,
                normalizarVariavel(variavelSelecionada),
                List.of(
                        new VariavelClimaticaCorrelacao(
                                "precipitacao",
                                "Precipitacao",
                                "mm",
                                pearsonComLag(pontos, lagSemanas, ClimaCasosSemana::precipitacaoMm)),
                        new VariavelClimaticaCorrelacao(
                                "temperatura",
                                "Temperatura media",
                                "C",
                                pearsonComLag(pontos, lagSemanas, ClimaCasosSemana::temperaturaMediaC)),
                        new VariavelClimaticaCorrelacao(
                                "umidade",
                                "Umidade media",
                                "%",
                                pearsonComLag(pontos, lagSemanas, ClimaCasosSemana::umidadeMediaPct))),
                pontos);
    }

    private List<ClimaCasosSemana> montarPontos(
            Map<Integer, AlertaSemanalDTO> alertasPorSemana,
            Map<Integer, ClimaSemana> climaPorSemana) {
        int ultimaSemana = Math.max(
                alertasPorSemana.keySet().stream().mapToInt(Integer::intValue).max().orElse(0),
                climaPorSemana.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));

        List<ClimaCasosSemana> pontos = new ArrayList<>();
        for (int semana = 1; semana <= ultimaSemana; semana++) {
            int semanaAtual = semana;
            AlertaSemanalDTO alerta = alertasPorSemana.get(semana);
            ClimaSemana clima = climaPorSemana.get(semana);
            LocalDate dataInicio = primeiroDiaDisponivel(alerta, clima)
                    .orElseGet(() -> LocalDate.now()
                            .with(WEEK_FIELDS.weekOfWeekBasedYear(), semanaAtual)
                            .with(WEEK_FIELDS.dayOfWeek(), 1));

            pontos.add(new ClimaCasosSemana(
                    semana,
                    dataInicio,
                    clima != null && clima.dataFim() != null ? clima.dataFim() : dataInicio.plusDays(6),
                    alerta != null && alerta.casosNotificados != null ? alerta.casosNotificados : 0,
                    alerta != null && alerta.casosEstimados != null ? alerta.casosEstimados : 0.0,
                    alerta != null && alerta.incidenciaPor100k != null ? alerta.incidenciaPor100k : 0.0,
                    arredondar(clima != null ? clima.precipitacaoMm() : null, 1),
                    arredondar(clima != null ? clima.temperaturaMediaC() : null, 1),
                    arredondar(clima != null ? clima.umidadeMediaPct() : null, 1)));
        }
        return pontos;
    }

    private Optional<LocalDate> primeiroDiaDisponivel(AlertaSemanalDTO alerta, ClimaSemana clima) {
        if (alerta != null && alerta.getDataInicioSE() != null) {
            return Optional.of(alerta.getDataInicioSE());
        }
        if (clima != null && clima.dataInicio() != null) {
            return Optional.of(clima.dataInicio());
        }
        return Optional.empty();
    }

    private Double pearsonComLag(
            List<ClimaCasosSemana> pontos,
            int lagSemanas,
            Function<ClimaCasosSemana, Double> seletorClima) {
        List<Double> casos = new ArrayList<>();
        List<Double> clima = new ArrayList<>();
        Map<Integer, ClimaCasosSemana> porSemana = pontos.stream()
                .collect(Collectors.toMap(ClimaCasosSemana::semana, Function.identity()));

        for (ClimaCasosSemana pontoCasos : pontos) {
            ClimaCasosSemana pontoClima = porSemana.get(pontoCasos.semana() - lagSemanas);
            if (pontoClima == null) {
                continue;
            }
            Double valorClima = seletorClima.apply(pontoClima);
            if (valorClima == null) {
                continue;
            }
            casos.add((double) pontoCasos.casos());
            clima.add(valorClima);
        }

        return arredondar(pearson(casos, clima), 3);
    }

    private Double pearson(List<Double> x, List<Double> y) {
        if (x.size() < 2 || y.size() < 2 || x.size() != y.size()) {
            return null;
        }

        double mediaX = x.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double mediaY = y.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double numerador = 0.0;
        double somaX = 0.0;
        double somaY = 0.0;

        for (int i = 0; i < x.size(); i++) {
            double dx = x.get(i) - mediaX;
            double dy = y.get(i) - mediaY;
            numerador += dx * dy;
            somaX += dx * dx;
            somaY += dy * dy;
        }

        double denominador = Math.sqrt(somaX * somaY);
        if (denominador == 0.0) {
            return null;
        }
        return numerador / denominador;
    }

    private Double arredondar(Double valor, int casas) {
        if (valor == null) {
            return null;
        }
        double fator = Math.pow(10, casas);
        return Math.round(valor * fator) / fator;
    }

    private int semanaDoAno(Integer semanaEpidemiologica) {
        return Math.floorMod(semanaEpidemiologica, 100);
    }

    private String normalizarVariavel(String variavelSelecionada) {
        if (variavelSelecionada == null || variavelSelecionada.isBlank()) {
            return "precipitacao";
        }
        return switch (variavelSelecionada.toLowerCase(Locale.ROOT)) {
            case "temperatura" -> "temperatura";
            case "umidade" -> "umidade";
            default -> "precipitacao";
        };
    }
}
