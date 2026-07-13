package br.com.gemsbiotec.vacinacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

class VacinacaoCsvParserTest {

    @Test
    void parseiaLinhasValidasEIgnoraLinhaComDoseNaoNumerica() throws IOException {
        String csv = """
                unidade_saude,doses_10_14,doses_18_59
                Unidade de Saude Ana Nery,55,7
                Unidade de Saude Anfrisio Lobao,73,4
                Unidade Invalida,abc,2
                """;

        VacinacaoCsvParser.ResultadoParseCsv resultado = VacinacaoCsvParser.parse(new BufferedReader(new StringReader(csv)));

        assertEquals(2, resultado.linhas().size());
        assertEquals("Unidade de Saude Ana Nery", resultado.linhas().get(0).unidadeNome());
        assertEquals(55, resultado.linhas().get(0).doses10a14());
        assertEquals(7, resultado.linhas().get(0).doses18a59());
        assertEquals(1, resultado.linhasInvalidas());
        assertEquals(1, resultado.avisos().size());
    }

    @Test
    void lancaExcecaoQuandoColunaObrigatoriaAusente() {
        String csv = "unidade,doses_10_14\nUnidade X,10\n";
        assertThrows(IOException.class, () -> VacinacaoCsvParser.parse(new BufferedReader(new StringReader(csv))));
    }
}
