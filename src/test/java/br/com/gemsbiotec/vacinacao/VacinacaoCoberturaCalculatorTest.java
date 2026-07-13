package br.com.gemsbiotec.vacinacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class VacinacaoCoberturaCalculatorTest {

    @Test
    void populacaoAlvo18a59SomaFaixasEIgnoraNulos() {
        assertEquals(150, VacinacaoCoberturaCalculator.populacaoAlvo18a59(30, null, 50, 40, 30));
        assertNull(VacinacaoCoberturaCalculator.populacaoAlvo18a59(null, null, null, null, null));
    }

    @Test
    void coberturaPercentualCalculaComDuasCasas() {
        assertEquals(50.0, VacinacaoCoberturaCalculator.coberturaPercentual(50, 100));
        assertEquals(33.33, VacinacaoCoberturaCalculator.coberturaPercentual(1, 3));
        assertNull(VacinacaoCoberturaCalculator.coberturaPercentual(10, null));
        assertNull(VacinacaoCoberturaCalculator.coberturaPercentual(10, 0));
    }

    @Test
    void meta90ArredondaParaCima() {
        assertEquals(90, VacinacaoCoberturaCalculator.meta90(100));
        assertEquals(9, VacinacaoCoberturaCalculator.meta90(10));
        assertNull(VacinacaoCoberturaCalculator.meta90(null));
    }

    @Test
    void faltamParaMetaNuncaFicaNegativo() {
        assertEquals(0, VacinacaoCoberturaCalculator.faltamParaMeta(100, 90));
        assertEquals(40, VacinacaoCoberturaCalculator.faltamParaMeta(50, 90));
        assertNull(VacinacaoCoberturaCalculator.faltamParaMeta(50, null));
    }

    @Test
    void statusCampanhaSeguePorFaixasDeCobertura() {
        assertEquals("INDISPONIVEL", VacinacaoCoberturaCalculator.statusCampanha(null));
        assertEquals("META_ATINGIDA", VacinacaoCoberturaCalculator.statusCampanha(90.0));
        assertEquals("BOM_PROGRESSO", VacinacaoCoberturaCalculator.statusCampanha(75.0));
        assertEquals("EM_ANDAMENTO", VacinacaoCoberturaCalculator.statusCampanha(55.0));
        assertEquals("ATENCAO_NECESSARIA", VacinacaoCoberturaCalculator.statusCampanha(20.0));
    }
}
