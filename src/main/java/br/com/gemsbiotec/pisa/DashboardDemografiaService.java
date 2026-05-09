package br.com.gemsbiotec.pisa;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.integration.ibge.IbgeService;
import br.com.gemsbiotec.pisa.dto.FaixaEtariaDashboardResponse;
import br.com.gemsbiotec.pisa.dto.FaixaEtariaDashboardResponse.FaixaEtariaItem;
import br.com.gemsbiotec.pisa.dto.PopulacaoPorSexoDashboardResponse;
import br.com.gemsbiotec.repository.BairroRepository;
import br.com.gemsbiotec.repository.MunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DashboardDemografiaService {

    private final IbgeService ibgeService;
    private final MunicipioRepository municipioRepository;
    private final BairroRepository bairroRepository;
    private final TenantContext tenantContext;

    public DashboardDemografiaService(
            IbgeService ibgeService,
            MunicipioRepository municipioRepository,
            BairroRepository bairroRepository,
            TenantContext tenantContext) {
        this.ibgeService = ibgeService;
        this.municipioRepository = municipioRepository;
        this.bairroRepository = bairroRepository;
        this.tenantContext = tenantContext;
    }

    public FaixaEtariaDashboardResponse faixaEtariaMunicipio() {
        Municipio municipio = municipioRepository.findAtivoById(tenantContext.getMunicipioId())
                .orElseThrow(() -> new NotFoundException("Municipio ativo nao encontrado."));

        Map<String, FaixaSexo> faixas = faixasVazias();
        ibgeService.getPopulacaoPorFaixaEtariaESexo(municipio.getCodigoIbge())
                .forEach(item -> somarFaixaMunicipio(faixas, item.faixa(), item.sexo(), item.populacao()));

        return new FaixaEtariaDashboardResponse(
                "MUNICIPIO",
                municipio.getId(),
                municipio.getNome(),
                null,
                null,
                toItems(faixas));
    }

    public FaixaEtariaDashboardResponse faixaEtariaBairro(Long bairroId) {
        Municipio municipio = municipioRepository.findAtivoById(tenantContext.getMunicipioId())
                .orElseThrow(() -> new NotFoundException("Municipio ativo nao encontrado."));

        Bairro bairro = bairroRepository.findByIdETenant(bairroId, municipio.getId())
                .orElseThrow(() -> new NotFoundException("Bairro nao encontrado para o municipio logado."));

        Map<String, FaixaSexo> faixas = faixasVazias();
        definirFaixaBairro(faixas, "0 a 4 anos", bairro.getMasculino0a4Anos(), bairro.getFeminino0a4Anos());
        definirFaixaBairro(faixas, "5 a 9 anos", bairro.getMasculino5a9Anos(), bairro.getFeminino5a9Anos());
        definirFaixaBairro(faixas, "10 a 14 anos", bairro.getMasculino10a14Anos(), bairro.getFeminino10a14Anos());
        definirFaixaBairro(faixas, "15 a 19 anos", bairro.getMasculino15a19Anos(), bairro.getFeminino15a19Anos());
        definirFaixaBairro(faixas, "20 a 24 anos", bairro.getMasculino20a24Anos(), bairro.getFeminino20a24Anos());
        definirFaixaBairro(faixas, "25 a 29 anos", bairro.getMasculino25a29Anos(), bairro.getFeminino25a29Anos());
        definirFaixaBairro(faixas, "30 a 39 anos", bairro.getMasculino30a39Anos(), bairro.getFeminino30a39Anos());
        definirFaixaBairro(faixas, "40 a 49 anos", bairro.getMasculino40a49Anos(), bairro.getFeminino40a49Anos());
        definirFaixaBairro(faixas, "50 a 59 anos", bairro.getMasculino50a59Anos(), bairro.getFeminino50a59Anos());
        definirFaixaBairro(faixas, "60 a 69 anos", bairro.getMasculino60a69Anos(), bairro.getFeminino60a69Anos());
        definirFaixaBairro(faixas, "70 anos ou mais", bairro.getMasculino70AnosOuMais(), bairro.getFeminino70AnosOuMais());

        return new FaixaEtariaDashboardResponse(
                "BAIRRO",
                municipio.getId(),
                municipio.getNome(),
                bairro.getId(),
                bairro.getNome(),
                toItems(faixas));
    }

    public PopulacaoPorSexoDashboardResponse sexoBairro(Long bairroId) {
        Municipio municipio = municipioRepository.findAtivoById(tenantContext.getMunicipioId())
                .orElseThrow(() -> new NotFoundException("Municipio ativo nao encontrado."));

        Bairro bairro = bairroRepository.findByIdETenant(bairroId, municipio.getId())
                .orElseThrow(() -> new NotFoundException("Bairro nao encontrado para o municipio logado."));

        long masculino = valor(bairro.getSexoMasculino());
        long feminino = valor(bairro.getSexoFeminino());

        return new PopulacaoPorSexoDashboardResponse(
                "BAIRRO",
                municipio.getId(),
                municipio.getNome(),
                bairro.getId(),
                bairro.getNome(),
                masculino,
                feminino,
                masculino + feminino);
    }

    private Map<String, FaixaSexo> faixasVazias() {
        Map<String, FaixaSexo> faixas = new LinkedHashMap<>();
        faixas.put("0 a 4 anos", new FaixaSexo());
        faixas.put("5 a 9 anos", new FaixaSexo());
        faixas.put("10 a 14 anos", new FaixaSexo());
        faixas.put("15 a 19 anos", new FaixaSexo());
        faixas.put("20 a 24 anos", new FaixaSexo());
        faixas.put("25 a 29 anos", new FaixaSexo());
        faixas.put("30 a 39 anos", new FaixaSexo());
        faixas.put("40 a 49 anos", new FaixaSexo());
        faixas.put("50 a 59 anos", new FaixaSexo());
        faixas.put("60 a 69 anos", new FaixaSexo());
        faixas.put("70 anos ou mais", new FaixaSexo());
        return faixas;
    }

    private void somarFaixaMunicipio(Map<String, FaixaSexo> faixas, String faixaIbge, String sexo, long populacao) {
        String faixa = normalizarFaixaMunicipio(faixaIbge);
        if (faixa != null) {
            FaixaSexo valores = faixas.get(faixa);
            if (valores != null) {
                valores.somar(sexo, populacao);
            }
        }
    }

    private String normalizarFaixaMunicipio(String faixaIbge) {
        if (faixaIbge == null) {
            return null;
        }

        return switch (faixaIbge.trim()) {
            case "0 a 4 anos" -> "0 a 4 anos";
            case "5 a 9 anos" -> "5 a 9 anos";
            case "10 a 14 anos" -> "10 a 14 anos";
            case "15 a 19 anos" -> "15 a 19 anos";
            case "20 a 24 anos" -> "20 a 24 anos";
            case "25 a 29 anos" -> "25 a 29 anos";
            case "30 a 34 anos", "35 a 39 anos" -> "30 a 39 anos";
            case "40 a 44 anos", "45 a 49 anos" -> "40 a 49 anos";
            case "50 a 54 anos", "55 a 59 anos" -> "50 a 59 anos";
            case "60 a 64 anos", "65 a 69 anos" -> "60 a 69 anos";
            case "70 a 74 anos", "75 a 79 anos", "80 a 84 anos", "85 a 89 anos",
                    "90 a 94 anos", "95 a 99 anos", "100 anos ou mais" -> "70 anos ou mais";
            default -> null;
        };
    }

    private void definirFaixaBairro(
            Map<String, FaixaSexo> faixas,
            String faixa,
            Integer masculino,
            Integer feminino) {
        FaixaSexo valores = faixas.get(faixa);
        if (valores != null) {
            valores.masculino = valor(masculino);
            valores.feminino = valor(feminino);
        }
    }

    private List<FaixaEtariaItem> toItems(Map<String, FaixaSexo> faixas) {
        List<FaixaEtariaItem> items = new ArrayList<>();
        faixas.forEach((faixa, valores) -> items.add(new FaixaEtariaItem(
                faixa,
                valores.masculino,
                valores.feminino,
                valores.total())));
        return items;
    }

    private long valor(Integer valor) {
        return valor != null ? valor.longValue() : 0L;
    }

    private static class FaixaSexo {
        private long masculino;
        private long feminino;

        private void somar(String sexo, long populacao) {
            if ("MASCULINO".equals(sexo)) {
                masculino += populacao;
            } else if ("FEMININO".equals(sexo)) {
                feminino += populacao;
            }
        }

        private long total() {
            return masculino + feminino;
        }
    }
}
