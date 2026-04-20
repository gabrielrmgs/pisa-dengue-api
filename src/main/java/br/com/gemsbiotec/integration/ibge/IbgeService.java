package br.com.gemsbiotec.integration.ibge;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de negócio para dados do IBGE.
 *
 * Cache agressivo (24h) porque dados do Censo 2022 não mudam —
 * configurado via quarkus.cache.caffeine.ibge-*.expire-after-write=86400S
 */
@ApplicationScoped
public class IbgeService {

    private static final Logger LOG = Logger.getLogger(IbgeService.class);

    @Inject @RestClient IbgeDadosClient dadosClient;
    @Inject @RestClient IbgeSidraClient sidraClient;

    // Nota: malha GeoJSON de bairros é servida por BairroGeoJsonRepository,
    // que usa arquivo local (resources/geojson/bairros_{geocodigo}.json)
    // com fallback para IbgeMalhaClient quando o arquivo não existe.

    // ── informações do município ──────────────────────────────────────────────

    @CacheResult(cacheName = "ibge-municipio")
    public Optional<MunicipioInfoDTO> getMunicipioInfo(String geocodigo) {
        try {
            return Optional.ofNullable(dadosClient.getMunicipioInfo(geocodigo));
        } catch (Exception e) {
            LOG.errorf("Falha ao buscar info município IBGE [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return Optional.empty();
        }
    }

    // ── demografia (Censo 2022) ───────────────────────────────────────────────

    /**
     * Retorna a população total do município.
     * Busca no SIDRA, filtra a linha "Total / Total".
     */
    @CacheResult(cacheName = "ibge-populacao")
    public long getPopulacaoTotal(String geocodigo) {
        try {
            List<SidraResultadoDTO> dados = sidraClient.getPopulacaoPorSexo(geocodigo);
            // O primeiro elemento do array SIDRA é o cabeçalho — ignorar
            return dados.stream()
                    .skip(1)
                    .filter(SidraResultadoDTO::isTotalGeral)
                    .mapToLong(SidraResultadoDTO::getValorLong)
                    .findFirst()
                    .orElse(0L);
        } catch (Exception e) {
            LOG.errorf("Falha ao buscar população IBGE [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return 0L;
        }
    }

    /**
     * Distribuição por sexo — retorna [masculino, feminino].
     * Formato esperado pelo gráfico de pizza do dashboard.
     */
    @CacheResult(cacheName = "ibge-sexo")
    public PopulacaoPorSexoDTO getPopulacaoPorSexo(String geocodigo) {
        try {
            List<SidraResultadoDTO> dados = sidraClient.getPopulacaoPorSexo(geocodigo);
            long masculino = 0L, feminino = 0L;
            for (SidraResultadoDTO linha : dados.stream().skip(1).toList()) {
                if ("Total".equalsIgnoreCase(linha.faixaEtaria)) {
                    if ("Masculino".equalsIgnoreCase(linha.sexo)) masculino = linha.getValorLong();
                    if ("Feminino".equalsIgnoreCase(linha.sexo))  feminino  = linha.getValorLong();
                }
            }
            return new PopulacaoPorSexoDTO(masculino, feminino);
        } catch (Exception e) {
            LOG.errorf("Falha ao buscar pop. por sexo IBGE [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return new PopulacaoPorSexoDTO(0L, 0L);
        }
    }

    /**
     * Distribuição por faixa etária — retorna lista ordenada para o gráfico de barras.
     */
    @CacheResult(cacheName = "ibge-faixa-etaria")
    public List<FaixaEtariaDTO> getPopulacaoPorFaixaEtaria(String geocodigo) {
        try {
            List<SidraResultadoDTO> dados = sidraClient.getPopulacaoPorFaixaEtaria(geocodigo);
            return dados.stream()
                    .skip(1)
                    .filter(d -> !"Total".equalsIgnoreCase(d.faixaEtaria))
                    .map(d -> new FaixaEtariaDTO(d.faixaEtaria, d.getValorLong()))
                    .toList();
        } catch (Exception e) {
            LOG.errorf("Falha ao buscar faixa etária IBGE [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return Collections.emptyList();
        }
    }

    // ── projeção de records para o dashboard ──────────────────────────────────

    public record PopulacaoPorSexoDTO(long masculino, long feminino) {
        public long total() { return masculino + feminino; }
        public double percMasculino() { return total() == 0 ? 0 : (masculino * 100.0 / total()); }
        public double percFeminino()  { return total() == 0 ? 0 : (feminino  * 100.0 / total()); }
    }

    public record FaixaEtariaDTO(String faixa, long populacao) {}
}
