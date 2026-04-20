// package br.com.gemsbiotec.mapa;

// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.inject.Inject;
// import jakarta.persistence.EntityManager;

// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// /**
//  * Agrega casos de dengue por bairro a partir do banco de dados local.
//  *
//  * O campo `cd_bairro` na tabela `casos` deve corresponder ao campo
//  * `CD_BAIRRO` do GeoJSON (ex: "2201903001" para o bairro Centro de Bom Jesus).
//  *
//  * A query é parametrizada por ano e mês — quando zero, ignora o filtro.
//  */
// @ApplicationScoped
// public class CasosPorBairroService {

//     @Inject
//     EntityManager em;

//     /**
//      * Retorna Map<CD_BAIRRO, totalCasos> para o município.
//      *
//      * @param geocodigo Código IBGE do município (usado como tenant)
//      * @param ano       Ano de notificação (0 = todos os anos)
//      * @param mes       Mês de notificação (0 = todos os meses)
//      */
//     @SuppressWarnings("unchecked")
//     public Map<String, Integer> getCasosPorBairro(String geocodigo, int ano, int mes) {

//         StringBuilder sql = new StringBuilder("""
//             SELECT c.cd_bairro, COUNT(c.id) AS total
//             FROM casos c
//             WHERE c.municipio_geocodigo = :geocodigo
//               AND c.cd_bairro IS NOT NULL
//         """);

//         if (ano > 0) sql.append(" AND EXTRACT(YEAR  FROM c.data_notificacao) = :ano");
//         if (mes > 0) sql.append(" AND EXTRACT(MONTH FROM c.data_notificacao) = :mes");

//         sql.append(" GROUP BY c.cd_bairro");

//         var query = em.createNativeQuery(sql.toString());
//         query.setParameter("geocodigo", geocodigo);
//         if (ano > 0) query.setParameter("ano", ano);
//         if (mes > 0) query.setParameter("mes", mes);

//         List<Object[]> rows = query.getResultList();

//         return rows.stream().collect(Collectors.toMap(
//                 r -> (String)  r[0],           // CD_BAIRRO
//                 r -> ((Number) r[1]).intValue() // count
//         ));
//     }
// }
