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
 * Servico de negocio para dados do IBGE.
 */
@ApplicationScoped
public class IbgeService {

    private static final Logger LOG = Logger.getLogger(IbgeService.class);

    @Inject
    @RestClient
    IbgeDadosClient dadosClient;

    @Inject
    @RestClient
    IbgeSidraClient sidraClient;

    @CacheResult(cacheName = "ibge-municipio")
    public Optional<MunicipioInfoDTO> getMunicipioInfo(String geocodigo) {
        try {
            return Optional.ofNullable(dadosClient.getMunicipioInfo(geocodigo));
        } catch (Exception e) {
            LOG.errorf("Falha ao buscar info municipio IBGE [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return Optional.empty();
        }
    }

    @CacheResult(cacheName = "ibge-populacao")
    public long getPopulacaoTotal(String geocodigo) {
        try {
            List<SidraResultadoDTO> dados = sidraClient.getPopulacaoPorSexo(geocodigo);
            return dados.stream()
                    .filter(SidraResultadoDTO::isTotalGeral)
                    .mapToLong(SidraResultadoDTO::getValorLong)
                    .findFirst()
                    .orElse(0L);
        } catch (Exception e) {
            LOG.errorf("Falha ao buscar populacao IBGE [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return 0L;
        }
    }

    @CacheResult(cacheName = "ibge-sexo")
    public PopulacaoPorSexoDTO getPopulacaoPorSexo(String geocodigo) {
        try {
            List<SidraResultadoDTO> dados = sidraClient.getPopulacaoPorSexo(geocodigo);
            long total = 0L;
            long masculino = 0L;
            long feminino = 0L;

            for (SidraResultadoDTO linha : dados) {
                if (linha.isIdadeTotal() && linha.isFormaDeclaracaoTotal()) {
                    if (linha.isSexoTotal()) total = linha.getValorLong();
                    if (linha.isSexoMasculino()) masculino = linha.getValorLong();
                    if (linha.isSexoFeminino()) feminino = linha.getValorLong();
                }
            }

            if (total == 0L) {
                total = masculino + feminino;
            }

            return new PopulacaoPorSexoDTO(total, masculino, feminino);
        } catch (Exception e) {
            LOG.errorf("Falha ao buscar pop. por sexo IBGE [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return new PopulacaoPorSexoDTO(0L, 0L, 0L);
        }
    }

    @CacheResult(cacheName = "ibge-faixa-etaria")
    public List<FaixaEtariaDTO> getPopulacaoPorFaixaEtaria(String geocodigo) {
        try {
            List<SidraResultadoDTO> dados = sidraClient.getPopulacaoPorFaixaEtaria(geocodigo);
            return dados.stream()
                    .filter(SidraResultadoDTO::isSexoTotal)
                    .filter(SidraResultadoDTO::isFormaDeclaracaoTotal)
                    .filter(d -> !d.isIdadeTotal())
                    .map(d -> new FaixaEtariaDTO(d.faixaEtaria, d.getValorLong()))
                    .toList();
        } catch (Exception e) {
            LOG.errorf("Falha ao buscar faixa etaria IBGE [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return Collections.emptyList();
        }
    }

    @CacheResult(cacheName = "ibge-faixa-etaria-sexo")
    public List<FaixaEtariaSexoDTO> getPopulacaoPorFaixaEtariaESexo(String geocodigo) {
        try {
            List<SidraResultadoDTO> dados = sidraClient.getPopulacaoPorFaixaEtaria(geocodigo);
            return dados.stream()
                    .filter(SidraResultadoDTO::isFormaDeclaracaoTotal)
                    .filter(d -> !d.isIdadeTotal())
                    .map(d -> new FaixaEtariaSexoDTO(
                            d.faixaEtaria,
                            d.isSexoMasculino() ? "MASCULINO" : d.isSexoFeminino() ? "FEMININO" : "TOTAL",
                            d.getValorLong()))
                    .toList();
        } catch (Exception e) {
            LOG.errorf("Falha ao buscar faixa etaria por sexo IBGE [geocodigo=%s]: %s", geocodigo, e.getMessage());
            return Collections.emptyList();
        }
    }

    public record PopulacaoPorSexoDTO(long total, long masculino, long feminino) {
        public double percMasculino() {
            return total == 0 ? 0 : (masculino * 100.0 / total);
        }

        public double percFeminino() {
            return total == 0 ? 0 : (feminino * 100.0 / total);
        }
    }

    public record FaixaEtariaDTO(String faixa, long populacao) {
    }

    public record FaixaEtariaSexoDTO(String faixa, String sexo, long populacao) {
    }
}
