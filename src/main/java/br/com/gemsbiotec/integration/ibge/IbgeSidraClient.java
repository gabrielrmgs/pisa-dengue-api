package br.com.gemsbiotec.integration.ibge;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * Client para a API SIDRA do IBGE — Censo Demográfico 2022.
 *
 * Base URL: https://apisidra.ibge.gov.br
 * Configurada em: quarkus.rest-client.ibge-sidra.url
 *
 * Tabelas usadas no MVP:
 *   9514 — Pessoas por sexo e grupo de idade (Censo 2022)
 *   9515 — Domicílios por situação (urbano/rural)
 *
 * Formato de URL do SIDRA:
 *   /values/t/{tabela}/n6/{geocodigo}/v/{variavel}/p/2022/c2/{classificacao_sexo}/c287/{classificacao_idade}
 *
 * Onde:
 *   n6  = nível geográfico "município"
 *   v   = variável (93 = pessoas)
 *   c2  = classificação sexo (4=masculino, 5=feminino, 6=total)
 *   c287= classificação por grupos de idade
 */
@RegisterRestClient(configKey = "ibge-sidra")
@Produces(MediaType.APPLICATION_JSON)
public interface IbgeSidraClient {

    /**
     * Retorna população total, masculina e feminina do município.
     *
     * URL gerada:
     * /values/t/9514/n6/{geocodigo}/v/93/p/2022/c2/4,5,6/c287/allxt
     *
     * @param geocodigo Código IBGE de 7 dígitos sem traço
     */
    @GET
    @Path("/values/t/9514/n6/{geocodigo}/v/93/p/2022/c2/4,5,6/c287/allxt")
    List<SidraResultadoDTO> getPopulacaoPorSexo(
            @PathParam("geocodigo") String geocodigo
    );

    /**
     * Retorna população por faixa etária detalhada (17 grupos).
     * Usado no gráfico "População por Faixa Etária" do dashboard.
     *
     * URL gerada:
     * /values/t/9514/n6/{geocodigo}/v/93/p/2022/c2/6/c287/allxt
     */
    @GET
    @Path("/values/t/9514/n6/{geocodigo}/v/93/p/2022/c2/6/c287/allxt")
    List<SidraResultadoDTO> getPopulacaoPorFaixaEtaria(
            @PathParam("geocodigo") String geocodigo
    );

    /**
     * Variante com parâmetros dinâmicos — útil para consultas ad-hoc
     * ou futuras expansões sem criar um método por combinação.
     *
     * Exemplo de path completo:
     * /values/t/9514/n6/2201903/v/93/p/2022/c2/4,5,6/c287/allxt
     */
    @GET
    @Path("/values/{path}")
    List<SidraResultadoDTO> consultaGenerica(
            @PathParam("path") String path
    );
}
