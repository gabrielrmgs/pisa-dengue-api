package br.com.gemsbiotec.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Bairro;

/**
 * Repository para Bairro.
 *
 * Concentra todas as queries PostGIS do sistema.
 * É aqui que ST_AsGeoJSON, ST_Within e ST_Distance vivem —
 * nunca espalhados por Services ou Resources.
 */
@ApplicationScoped
public class BairroRepository implements PanacheRepositoryBase<Bairro, Long> {

    private EntityManager em;
    private TenantContext tenantContext;

    @Inject
    public BairroRepository(EntityManager em, TenantContext tenantContext) {
        this.em = em;
        this.tenantContext = tenantContext;
    }

    public List<Bairro> listar() {
        return list("municipio.id = ?1 ORDER BY nome", tenantContext.getMunicipioId());
    }

    public Optional<Bairro> findByCdBairro(String cdBairro) {
        return find("cdBairro", cdBairro).firstResultOptional();
    }

    public List<Bairro> listByMunicipio(Long municipioId) {
        return list("municipio.id = ?1 ORDER BY nome", municipioId);
    }

    public boolean existsByCdBairro(String cdBairro) {
        return count("cdBairro", cdBairro) > 0;
    }

    // ── GeoJSON para o Leaflet ────────────────────────────────────────────────

    /**
     * Retorna o GeoJSON puro de todos os bairros do município.
     * Usado pelo endpoint GET /api/v1/mapa/{geocodigo}/geojson.
     *
     * ST_AsGeoJSON converte o MultiPolygon PostGIS diretamente para o
     * formato GeoJSON que o Leaflet consome — sem processamento no Java.
     *
     * json_build_object e json_agg montam o FeatureCollection completo
     * em uma única query, eliminando N+1 e serialização manual.
     *
     * @param municipioId Long do tenant
     * @return String com o FeatureCollection GeoJSON completo
     */
    public String getGeoJsonPorMunicipio(Long municipioId) {
        return (String) em.createNativeQuery("""
                SELECT json_build_object(
                    'type',     'FeatureCollection',
                    'features', COALESCE(json_agg(feat), '[]'::json)
                )::text
                FROM (
                    SELECT json_build_object(
                        'type',       'Feature',
                        'geometry',   ST_AsGeoJSON(b.geometria)::json,
                        'properties', json_build_object(
                            'id',        b.id,
                            'cdBairro',  b.cd_bairro,
                            'nmBairro',  b.nm_bairro,
                            'populacao', b.populacao
                        )
                    ) AS feat
                    FROM bairros b
                    WHERE b.municipio_id = :municipioId
                    ORDER BY b.nm_bairro
                ) sub
                """)
                .setParameter("municipioId", municipioId)
                .getSingleResult();
    }

    /**
     * Retorna o GeoJSON enriquecido com contagem de casos por bairro.
     * Usado pelo endpoint GET /api/v1/mapa/{geocodigo}/geojson/heatmap.
     *
     * O LEFT JOIN com casos é filtrado por ano e mês quando informados
     * (0 = sem filtro). COUNT(c.id) é seguro com LEFT JOIN — retorna 0
     * quando não há casos, nunca NULL.
     *
     * A propriedade "casos" é o que o Leaflet usa para colorir o choropleth:
     * function getColor(d) {
     * return d > 50 ? '#d73027' : d > 20 ? '#fc8d59' : '#fee08b';
     * }
     *
     * @param municipioId Long do tenant
     * @param ano         Ano de notificação (0 = todos)
     * @param mes         Mês de notificação (0 = todos)
     */
    public String getGeoJsonHeatmapPorMunicipio(Long municipioId, int ano, int mes) {
        return (String) em.createNativeQuery("""
                SELECT json_build_object(
                    'type',     'FeatureCollection',
                    'features', COALESCE(json_agg(feat), '[]'::json)
                )::text
                FROM (
                    SELECT json_build_object(
                        'type',       'Feature',
                        'geometry',   ST_AsGeoJSON(b.geometria)::json,
                        'properties', json_build_object(
                            'id',           b.id,
                            'cdBairro',     b.cd_bairro,
                            'nmBairro',     b.nm_bairro,
                            'populacao',    b.populacao,
                            'casos',        COUNT(c.id),
                            'confirmados',  COUNT(c.id) FILTER (WHERE c.classificacao = 'CONFIRMADO'),
                            'suspeitos',    COUNT(c.id) FILTER (WHERE c.classificacao = 'SUSPEITO'),
                            'incidencia',   CASE
                                              WHEN b.populacao > 0
                                              THEN ROUND((COUNT(c.id)::numeric / b.populacao) * 100000, 1)
                                              ELSE 0
                                            END
                        )
                    ) AS feat
                    FROM bairros b
                    LEFT JOIN casos c
                           ON c.bairro_id = b.id
                          AND c.municipio_id = :municipioId
                          AND (:ano  = 0 OR EXTRACT(YEAR  FROM c.data_notificacao) = :ano)
                          AND (:mes  = 0 OR EXTRACT(MONTH FROM c.data_notificacao) = :mes)
                    WHERE b.municipio_id = :municipioId
                    GROUP BY b.id, b.cd_bairro, b.nm_bairro, b.geometria, b.populacao
                    ORDER BY b.nm_bairro
                ) sub
                """)
                .setParameter("municipioId", municipioId)
                .setParameter("ano", ano)
                .setParameter("mes", mes)
                .getSingleResult();
    }

    // ── resolução geoespacial de bairro a partir de ponto ────────────────────

    /**
     * Retorna o bairro que contém o ponto geográfico informado (lat/lon WGS84).
     * Usado ao registrar um Caso com coordenada GPS para preencher bairro_id
     * automaticamente, sem que o agente precise selecionar o bairro no formulário.
     *
     * ST_Within(ponto, polígono) usa o índice GIST em bairros.geometria —
     * a query é O(log n), não um full scan.
     *
     * @param longitude   Longitude WGS84 do ponto (ex: -44.3577)
     * @param latitude    Latitude WGS84 do ponto (ex: -9.0713)
     * @param municipioId Tenant — restringe a busca ao município correto
     */
    public Optional<Bairro> findBairroQueContemPonto(
            double longitude, double latitude, Long municipioId) {

        return em.createQuery("""
                FROM Bairro b
                WHERE b.municipio.id = :municipioId
                  AND ST_Within(
                        ST_SetSRID(ST_MakePoint(:lon, :lat), 4326),
                        b.geometria
                      ) = true
                """, Bairro.class)
                .setParameter("municipioId", municipioId)
                .setParameter("lon", longitude)
                .setParameter("lat", latitude)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    /**
     * Retorna o bairro mais próximo ao ponto informado quando o ponto cai
     * fora de qualquer polígono (ex: ponto na beira de estrada, imprecisão de GPS).
     * Fallback do método findBairroQueContemPonto.
     *
     * ST_Distance com índice GIST usa KNN (K-Nearest Neighbor) — eficiente
     * mesmo com centenas de bairros.
     *
     * @param longitude   Longitude WGS84
     * @param latitude    Latitude WGS84
     * @param municipioId Tenant
     */
    public Optional<Bairro> findBairroMaisProximo(
            double longitude, double latitude, Long municipioId) {

        return em.createQuery("""
                FROM Bairro b
                WHERE b.municipio.id = :municipioId
                ORDER BY ST_Distance(
                    b.geometria,
                    ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)
                )
                """, Bairro.class)
                .setParameter("municipioId", municipioId)
                .setParameter("lon", longitude)
                .setParameter("lat", latitude)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
