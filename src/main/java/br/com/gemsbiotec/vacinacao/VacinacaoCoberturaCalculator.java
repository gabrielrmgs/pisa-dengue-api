package br.com.gemsbiotec.vacinacao;

public final class VacinacaoCoberturaCalculator {

    private VacinacaoCoberturaCalculator() {
    }

    public static Integer populacaoAlvo18a59(
            Integer m20a24, Integer m25a29, Integer m30a39, Integer m40a49, Integer m50a59) {
        if (m20a24 == null && m25a29 == null && m30a39 == null && m40a49 == null && m50a59 == null) {
            return null;
        }
        return nz(m20a24) + nz(m25a29) + nz(m30a39) + nz(m40a49) + nz(m50a59);
    }

    public static Double coberturaPercentual(int doses, Integer populacaoAlvo) {
        if (populacaoAlvo == null || populacaoAlvo <= 0) {
            return null;
        }
        return Math.round(doses * 10000.0 / populacaoAlvo) / 100.0;
    }

    /**
     * Cobertura acima de 100% e matematicamente impossivel e indica que a populacao-alvo
     * do bairro (censo IBGE do poligono) nao corresponde a real area atendida pela unidade
     * (comum em UBS rurais cujo ponto geografico cai num bairro censitario pequeno).
     */
    public static boolean isInconsistente(Double coberturaPercentual) {
        return coberturaPercentual != null && coberturaPercentual > 100.0;
    }

    public static Integer meta90(Integer populacaoAlvo) {
        if (populacaoAlvo == null) {
            return null;
        }
        return (int) Math.ceil(populacaoAlvo * 0.9);
    }

    public static Integer faltamParaMeta(int doses, Integer meta) {
        if (meta == null) {
            return null;
        }
        return Math.max(0, meta - doses);
    }

    public static String statusCampanha(Double coberturaMediaPercentual) {
        if (coberturaMediaPercentual == null) {
            return "INDISPONIVEL";
        }
        if (coberturaMediaPercentual >= 90) {
            return "META_ATINGIDA";
        }
        if (coberturaMediaPercentual >= 70) {
            return "BOM_PROGRESSO";
        }
        if (coberturaMediaPercentual >= 50) {
            return "EM_ANDAMENTO";
        }
        return "ATENCAO_NECESSARIA";
    }

    private static int nz(Integer value) {
        return value == null ? 0 : value;
    }
}
