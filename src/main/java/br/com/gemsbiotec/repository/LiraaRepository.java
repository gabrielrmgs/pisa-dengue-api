// package br.uespi.pisa.repository;

// import br.uespi.pisa.dominio.caso.LiraaAnalise;
// import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.inject.Inject;
// import jakarta.persistence.EntityManager;

// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// /**
//  * Repository para LiraaAnalise.
//  *
//  * Queries para o gráfico LIRAa do dashboard e para o heatmap de
//  * risco entomológico por bairro.
//  */
// @ApplicationScoped
// public class LiraaRepository implements PanacheRepositoryBase<LiraaAnalise, UUID> {

//     @Inject
//     EntityManager em;

//     // ── lookups ───────────────────────────────────────────────────────────────

//     /**
//      * Busca o LIRAa de um bairro em uma SE específica.
//      * Usado para validar duplicata antes de inserir novo registro.
//      */
//     public Optional<LiraaAnalise> findByBairroESemana(UUID bairroId,
//                                                        int semana, int ano) {
//         return find("bairro.id = ?1 AND semanaEpidemiologica = ?2 AND ano = ?3",
//                 bairroId, semana, ano)
//             .firstResultOptional();
//     }

//     /**
//      * Último LIRAa registrado por bairro no município.
//      * Usado pelo heatmap de risco para mostrar o estado atual de infestação.
//      */
//     @SuppressWarnings("unchecked")
//     public List<LiraaAnalise> findUltimosPorBairro(UUID municipioId) {
//         return em.createQuery("""
//                 SELECT l FROM LiraaAnalise l
//                 WHERE l.municipio.id = :municipioId
//                   AND l.semanaEpidemiologica = (
//                       SELECT MAX(l2.semanaEpidemiologica)
//                       FROM LiraaAnalise l2
//                       WHERE l2.bairro.id = l.bairro.id
//                         AND l2.ano = l.ano
//                   )
//                 ORDER BY l.bairro.nmBairro
//                 """, LiraaAnalise.class)
//             .setParameter("municipioId", municipioId)
//             .getResultList();
//     }

//     // ── dashboard — gráfico LIRAa ─────────────────────────────────────────────

//     /**
//      * Série histórica do IP por município (média dos bairros) por SE.
//      * Resultado do gráfico de linha LIRAa do dashboard.
//      *
//      * Retorna List<Object[]>: [semana(int), ano(int), ipMedio(double), nivel(String)]
//      */
//     @SuppressWarnings("unchecked")
//     public List<Object[]> seriePorSemana(UUID municipioId, int ano) {
//         return em.createQuery("""
//                 SELECT l.semanaEpidemiologica,
//                        l.ano,
//                        AVG(l.indicePredial),
//                        MAX(l.nivelRisco)
//                 FROM LiraaAnalise l
//                 WHERE l.municipio.id = :municipioId
//                   AND l.ano = :ano
//                 GROUP BY l.semanaEpidemiologica, l.ano
//                 ORDER BY l.ano, l.semanaEpidemiologica
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("ano", ano)
//             .getResultList();
//     }

//     /**
//      * Distribuição de bairros por nível de risco no levantamento mais recente.
//      * Usado pelo card de resumo LIRAa: "X bairros em risco, Y em alerta...".
//      *
//      * Retorna List<Object[]>: [nivelRisco(String), totalBairros(long)]
//      */
//     @SuppressWarnings("unchecked")
//     public List<Object[]> distribuicaoPorNivelRisco(UUID municipioId, int ano) {
//         return em.createQuery("""
//                 SELECT l.nivelRisco, COUNT(DISTINCT l.bairro.id)
//                 FROM LiraaAnalise l
//                 WHERE l.municipio.id = :municipioId
//                   AND l.ano = :ano
//                 GROUP BY l.nivelRisco
//                 ORDER BY l.nivelRisco
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("ano", ano)
//             .getResultList();
//     }

//     /**
//      * GeoJSON com IP por bairro para o heatmap de risco entomológico.
//      * Complementa o heatmap de casos — mostra onde há focos de Aedes,
//      * não apenas onde há doentes.
//      *
//      * Usa DISTINCT ON (PostgreSQL) para pegar o último LIRAa por bairro.
//      */
//     public String getGeoJsonLiraa(UUID municipioId, int ano) {
//         return (String) em.createNativeQuery("""
//                 SELECT json_build_object(
//                     'type',     'FeatureCollection',
//                     'features', COALESCE(json_agg(feat), '[]'::json)
//                 )::text
//                 FROM (
//                     SELECT json_build_object(
//                         'type',       'Feature',
//                         'geometry',   ST_AsGeoJSON(b.geometria)::json,
//                         'properties', json_build_object(
//                             'cdBairro',           b.cd_bairro,
//                             'nmBairro',           b.nm_bairro,
//                             'indicePredial',      l.indice_predial,
//                             'indiceBreteau',      l.indice_breteau,
//                             'nivelRisco',         l.nivel_risco,
//                             'semana',             l.semana_epidemiologica,
//                             'imoveisInspecionados', l.imoveis_inspecionados,
//                             'imoveisPositivos',   l.imoveis_positivos
//                         )
//                     ) AS feat
//                     FROM (
//                         SELECT DISTINCT ON (bairro_id) *
//                         FROM liraa_analises
//                         WHERE municipio_id = :municipioId
//                           AND ano = :ano
//                         ORDER BY bairro_id, semana_epidemiologica DESC
//                     ) l
//                     JOIN bairros b ON b.id = l.bairro_id
//                     ORDER BY b.nm_bairro
//                 ) sub
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("ano", ano)
//             .getSingleResult();
//     }
// }
