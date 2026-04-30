// package br.uespi.pisa.repository;

// import br.uespi.pisa.dominio.caso.UnidadeSaude;
// import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.inject.Inject;
// import jakarta.persistence.EntityManager;

// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// /**
//  * Repository para UnidadeSaude.
//  *
//  * Queries voltadas para o mapa (markers) e para o módulo de triagem
//  * (encontrar unidade mais próxima do paciente).
//  */
// @ApplicationScoped
// public class UnidadeSaudeRepository implements PanacheRepositoryBase<UnidadeSaude, UUID> {

//     @Inject
//     EntityManager em;

//     // ── listagens para o mapa ─────────────────────────────────────────────────

//     /**
//      * Lista todas as unidades ativas do município.
//      * Retorna com bairro carregado para exibir nome no tooltip do marker.
//      */
//     public List<UnidadeSaude> listAtivasByMunicipio(UUID municipioId) {
//         return list("""
//                 FROM UnidadeSaude u
//                 LEFT JOIN FETCH u.bairro b
//                 WHERE u.municipio.id = ?1
//                   AND u.ativo = true
//                 ORDER BY u.nome
//                 """, municipioId);
//     }

//     /**
//      * Lista unidades ativas por tipo e município.
//      * Usado pelos filtros do mapa (ex: mostrar apenas UBS).
//      */
//     public List<UnidadeSaude> listAtivasByMunicipioETipo(UUID municipioId, String tipo) {
//         return list("municipio.id = ?1 AND tipo = ?2 AND ativo = true ORDER BY nome",
//                 municipioId, tipo);
//     }

//     /**
//      * Lista unidades de um bairro específico — drill-down do mapa.
//      */
//     public List<UnidadeSaude> listByBairro(UUID bairroId, UUID municipioId) {
//         return list("municipio.id = ?1 AND bairro.id = ?2 AND ativo = true",
//                 municipioId, bairroId);
//     }

//     public long countAtivasByMunicipio(UUID municipioId) {
//         return count("municipio.id = ?1 AND ativo = true", municipioId);
//     }

//     public Optional<UnidadeSaude> findByCnes(String cnes) {
//         return find("cnes", cnes).firstResultOptional();
//     }

//     // ── GeoJSON para o Leaflet ────────────────────────────────────────────────

//     /**
//      * Retorna unidades ativas como GeoJSON FeatureCollection de pontos.
//      * Entregue diretamente ao Leaflet para renderizar os markers de unidades.
//      *
//      * Cada Feature inclui as propriedades necessárias para o popup do marker:
//      * nome, tipo, cnes, endereço e telefone.
//      */
//     public String getGeoJsonUnidades(UUID municipioId) {
//         return (String) em.createNativeQuery("""
//                 SELECT json_build_object(
//                     'type',     'FeatureCollection',
//                     'features', COALESCE(json_agg(feat), '[]'::json)
//                 )::text
//                 FROM (
//                     SELECT json_build_object(
//                         'type',       'Feature',
//                         'geometry',   ST_AsGeoJSON(u.localizacao)::json,
//                         'properties', json_build_object(
//                             'id',        u.id,
//                             'nome',      u.nome,
//                             'tipo',      u.tipo,
//                             'cnes',      u.cnes,
//                             'endereco',  u.endereco,
//                             'telefone',  u.telefone,
//                             'bairro',    b.nm_bairro
//                         )
//                     ) AS feat
//                     FROM unidades_saude u
//                     LEFT JOIN bairros b ON b.id = u.bairro_id
//                     WHERE u.municipio_id = :municipioId
//                       AND u.ativo = true
//                       AND u.localizacao IS NOT NULL
//                     ORDER BY u.nome
//                 ) sub
//                 """)
//             .setParameter("municipioId", municipioId)
//             .getSingleResult();
//     }

//     // ── proximidade ───────────────────────────────────────────────────────────

//     /**
//      * Retorna as N unidades mais próximas de um ponto geográfico.
//      * Usado na triagem para sugerir para onde encaminhar o paciente.
//      *
//      * ST_Distance com índice GIST usa busca KNN — eficiente para
//      * um número pequeno de unidades por município.
//      *
//      * Retorna List<Object[]>: [UnidadeSaude, distanciaMetros(double)]
//      *
//      * @param longitude Longitude WGS84 do ponto de origem
//      * @param latitude  Latitude  WGS84 do ponto de origem
//      * @param municipioId Tenant
//      * @param limit     Número máximo de resultados (ex: 3)
//      */
//     @SuppressWarnings("unchecked")
//     public List<Object[]> findMaisProximas(double longitude, double latitude,
//                                             UUID municipioId, int limit) {
//         return em.createQuery("""
//                 SELECT u,
//                        ST_Distance(
//                            u.localizacao,
//                            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)
//                        ) AS distancia
//                 FROM UnidadeSaude u
//                 WHERE u.municipio.id = :municipioId
//                   AND u.ativo = true
//                   AND u.localizacao IS NOT NULL
//                 ORDER BY distancia
//                 """)
//             .setParameter("municipioId", municipioId)
//             .setParameter("lon", longitude)
//             .setParameter("lat", latitude)
//             .setMaxResults(limit)
//             .getResultList();
//     }
// }
