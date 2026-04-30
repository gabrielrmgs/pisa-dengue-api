// package br.com.gemsbiotec.repository;

// import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.inject.Inject;
// import jakarta.persistence.EntityManager;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// /**
//  * Repository para Caso.
//  *
//  * Divide-se em três grupos de queries:
//  *
//  *   1. CRUD básico   — registro e busca de casos individuais
//  *   2. Dashboard     — agregações para os cards e gráficos
//  *   3. Geoespacial   — atribuição de bairro via PostGIS
//  */
// @ApplicationScoped
// public class CasoRepository implements PanacheRepositoryBase<Caso, UUID> {

//     @Inject
//     EntityManager em;

//     // ══════════════════════════════════════════════════════════════════════════
//     // 1. CRUD
//     // ══════════════════════════════════════════════════════════════════════════

//     /**
//      * Busca caso por ID dentro do tenant — nunca retorna casos de outro município.
//      */
//     public Optional<Caso> findByIdETenant(UUID id, UUID municipioId) {
//         return find("id = ?1 AND municipio.id = ?2", id, municipioId)
//             .firstResultOptional();
//     }

//     /**
//      * Lista casos do município com paginação — usado pela tela de listagem.
//      * Ordenado por data de notificação decrescente (mais recentes primeiro).
//      */
//     public List<Caso> listByMunicipio(UUID municipioId, int page, int size) {
//         return find("municipio.id = ?1 ORDER BY dataNotificacao DESC",
//                 municipioId)
//             .page(page, size)
//             .list();
//     }

//     /**
//      * Lista casos por bairro — usado no drill-down do mapa.
//      */
//     public List<Caso> listByBairro(UUID bairroId, UUID municipioId) {
//         return list("""
//                 municipio.id = ?1
//                 AND bairro.id = ?2
//                 ORDER BY dataNotificacao DESC
//                 """, municipioId, bairroId);
//     }

//     /**
//      * Conta casos sem bairro definido — jobs de geocodificação usam isso
//      * para saber quantos casos ainda precisam de atribuição via PostGIS.
//      */
//     public long countSemBairro(UUID municipioId) {
//         return count("municipio.id = ?1 AND bairro IS NULL", municipioId);
//     }

//     /**
//      * Retorna casos sem bairro e com localização GPS — candidatos para
//      * geocodificação automática via ST_Within.
//      */
//     public List<Caso> listSemBairroComLocalizacao(UUID municipioId) {
//         return list("""
//                 municipio.id = ?1
//                 AND bairro IS NULL
//                 AND localizacao IS NOT NULL
//                 """, municipioId);
//     }

//     // ══════════════════════════════════════════════════════════════════════════
//     // 2. DASHBOARD — cards
//     // ══════════════════════════════════════════════════════════════════════════

//     /**
//      * Total de casos notificados no ano — card "Total de Casos".
//      */
//     public long countByAno(UUID municipioId, int ano) {
//         return count("municipio.id = ?1 AND anoEpidemiologico = ?2",
//                 municipioId, ano);
//     }

//     /**
//      * Total de casos no mês corrente — card "Casos Este Mês".
//      */
//     public long countByAnoEMes(UUID municipioId, int ano, int mes) {
//         return (long) em.createNativeQuery("""
//                 SELECT COUNT(*)
//                 FROM casos
//                 WHERE municipio_id = :municipioId
//                   AND EXTRACT(YEAR  FROM data_notificacao) = :ano
//                   AND EXTRACT(MONTH FROM data_notificacao) = :mes
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("ano", ano)
//             .setParameter("mes", mes)
//             .getSingleResult();
//     }

//     /**
//      * Total por classificação no ano — usado pelo card de nível de alerta
//      * e pela distribuição confirmados/suspeitos/descartados.
//      */
//     public long countByAnoEClassificacao(UUID municipioId, int ano,
//                                           Classificacao classificacao) {
//         return count("""
//                 municipio.id = ?1
//                 AND anoEpidemiologico = ?2
//                 AND classificacao = ?3
//                 """, municipioId, ano, classificacao);
//     }

//     // ══════════════════════════════════════════════════════════════════════════
//     // 2. DASHBOARD — gráficos
//     // ══════════════════════════════════════════════════════════════════════════

//     /**
//      * Série temporal de casos por semana epidemiológica em um ano.
//      * Resultado do gráfico histórico de linhas do dashboard.
//      *
//      * Retorna List<Object[]> onde cada linha é [semana(int), total(long)].
//      * O frontend recebe isso como array de pontos {se: N, casos: M}.
//      */
//     @SuppressWarnings("unchecked")
//     public List<Object[]> countPorSemanaEpidemiologica(UUID municipioId, int ano) {
//         return em.createQuery("""
//                 SELECT c.semanaEpidemiologica, COUNT(c.id)
//                 FROM Caso c
//                 WHERE c.municipio.id = :municipioId
//                   AND c.anoEpidemiologico = :ano
//                 GROUP BY c.semanaEpidemiologica
//                 ORDER BY c.semanaEpidemiologica
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("ano", ano)
//             .getResultList();
//     }

//     /**
//      * Distribuição de casos por faixa etária no ano.
//      * Resultado do gráfico demográfico do dashboard.
//      *
//      * Retorna List<Object[]> onde cada linha é [faixaEtaria(String), total(long)].
//      */
//     @SuppressWarnings("unchecked")
//     public List<Object[]> countPorFaixaEtaria(UUID municipioId, int ano) {
//         return em.createQuery("""
//                 SELECT c.faixaEtaria, COUNT(c.id)
//                 FROM Caso c
//                 WHERE c.municipio.id = :municipioId
//                   AND c.anoEpidemiologico = :ano
//                   AND c.faixaEtaria IS NOT NULL
//                 GROUP BY c.faixaEtaria
//                 ORDER BY c.faixaEtaria
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("ano", ano)
//             .getResultList();
//     }

//     /**
//      * Distribuição de casos por sexo no ano.
//      * Retorna List<Object[]> onde cada linha é [sexo(String), total(long)].
//      */
//     @SuppressWarnings("unchecked")
//     public List<Object[]> countPorSexo(UUID municipioId, int ano) {
//         return em.createQuery("""
//                 SELECT c.sexo, COUNT(c.id)
//                 FROM Caso c
//                 WHERE c.municipio.id = :municipioId
//                   AND c.anoEpidemiologico = :ano
//                 GROUP BY c.sexo
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("ano", ano)
//             .getResultList();
//     }

//     /**
//      * Série diária de casos em um intervalo de datas.
//      * Usado para o gráfico de correlação clima × casos (eixo X = data).
//      *
//      * Retorna List<Object[]> onde cada linha é [data(LocalDate), total(long)].
//      */
//     @SuppressWarnings("unchecked")
//     public List<Object[]> countPorDia(UUID municipioId,
//                                        LocalDate inicio, LocalDate fim) {
//         return em.createQuery("""
//                 SELECT c.dataNotificacao, COUNT(c.id)
//                 FROM Caso c
//                 WHERE c.municipio.id = :municipioId
//                   AND c.dataNotificacao BETWEEN :inicio AND :fim
//                 GROUP BY c.dataNotificacao
//                 ORDER BY c.dataNotificacao
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("inicio", inicio)
//             .setParameter("fim", fim)
//             .getResultList();
//     }

//     /**
//      * Ranking dos bairros com mais casos no ano.
//      * Retorna List<Object[]>: [nmBairro(String), cdBairro(String), total(long)].
//      * Usado pelo card "Bairros mais afetados" e pelo tooltip do mapa.
//      */
//     @SuppressWarnings("unchecked")
//     public List<Object[]> rankingBairrosPorCasos(UUID municipioId, int ano, int limit) {
//         return em.createQuery("""
//                 SELECT b.nmBairro, b.cdBairro, COUNT(c.id)
//                 FROM Caso c
//                 JOIN c.bairro b
//                 WHERE c.municipio.id = :municipioId
//                   AND c.anoEpidemiologico = :ano
//                 GROUP BY b.id, b.nmBairro, b.cdBairro
//                 ORDER BY COUNT(c.id) DESC
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("ano", ano)
//             .setMaxResults(limit)
//             .getResultList();
//     }

//     // ══════════════════════════════════════════════════════════════════════════
//     // 3. GEOESPACIAL — atribuição de bairro
//     // ══════════════════════════════════════════════════════════════════════════

//     /**
//      * Atribui bairro a casos que têm localização GPS mas bairro nulo.
//      * Usado pelo job de geocodificação — executa ST_Within em lote.
//      *
//      * A subquery correlacionada usa o índice GIST de bairros.geometria,
//      * tornando a operação eficiente mesmo com muitos casos pendentes.
//      *
//      * @return número de casos atualizados
//      */
//     public int atribuirBairroViaGps(UUID municipioId) {
//         return em.createNativeQuery("""
//                 UPDATE casos c
//                 SET bairro_id = (
//                     SELECT b.id
//                     FROM bairros b
//                     WHERE b.municipio_id = :municipioId
//                       AND ST_Within(c.localizacao, b.geometria)
//                     LIMIT 1
//                 )
//                 WHERE c.municipio_id = :municipioId
//                   AND c.bairro_id IS NULL
//                   AND c.localizacao IS NOT NULL
//                 """)
//             .setParameter("municipioId", municipioId)
//             .executeUpdate();
//     }
// }
