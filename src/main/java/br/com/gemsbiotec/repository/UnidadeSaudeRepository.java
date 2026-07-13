package br.com.gemsbiotec.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.gemsbiotec.dominio.saude.TipoUnidadeSaude;
import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@ApplicationScoped
public class UnidadeSaudeRepository implements PanacheRepositoryBase<UnidadeSaude, Long> {

    private final EntityManager em;

    public UnidadeSaudeRepository(EntityManager em) {
        this.em = em;
    }

    public Optional<UnidadeSaude> findByIdETenant(Long id, Long municipioId) {
        return find("""
                FROM UnidadeSaude u
                JOIN FETCH u.municipio
                LEFT JOIN FETCH u.bairro
                WHERE u.id = ?1
                  AND u.municipio.id = ?2
                """, id, municipioId).firstResultOptional();
    }

    public List<UnidadeSaude> listByMunicipio(
            Long municipioId,
            TipoUnidadeSaude tipo,
            Long bairroId,
            Boolean ativo) {
        StringBuilder query = new StringBuilder("""
                FROM UnidadeSaude u
                JOIN FETCH u.municipio
                LEFT JOIN FETCH u.bairro
                WHERE u.municipio.id = :municipioId
                """);
        Map<String, Object> params = new HashMap<>();
        params.put("municipioId", municipioId);

        if (tipo != null) {
            query.append(" AND u.tipo = :tipo");
            params.put("tipo", tipo);
        }
        if (bairroId != null) {
            query.append(" AND u.bairro.id = :bairroId");
            params.put("bairroId", bairroId);
        }
        if (ativo != null) {
            query.append(" AND u.ativo = :ativo");
            params.put("ativo", ativo);
        }
        query.append(" ORDER BY u.nome");

        return find(query.toString(), params).list();
    }

    public Optional<UnidadeSaude> findByNomeIgnoreCaseETenant(String nome, Long municipioId) {
        return find("LOWER(nome) = LOWER(?1) AND municipio.id = ?2", nome, municipioId).firstResultOptional();
    }

    public boolean existsByCnesETenant(String cnes, Long municipioId) {
        if (cnes == null || cnes.isBlank()) {
            return false;
        }
        return count("LOWER(cnes) = LOWER(?1) AND municipio.id = ?2", cnes, municipioId) > 0;
    }

    public boolean existsByCnesETenantExcludingId(String cnes, Long municipioId, Long id) {
        if (cnes == null || cnes.isBlank()) {
            return false;
        }
        return count("LOWER(cnes) = LOWER(?1) AND municipio.id = ?2 AND id <> ?3", cnes, municipioId, id) > 0;
    }

    public String getGeoJsonUnidades(Long municipioId, TipoUnidadeSaude tipo) {
        String filtroTipo = tipo == null ? "" : " AND u.tipo = :tipo";
        String sql = """
                SELECT json_build_object(
                    'type',     'FeatureCollection',
                    'features', COALESCE(json_agg(feat), '[]'::json)
                )::text
                FROM (
                    SELECT json_build_object(
                        'type',       'Feature',
                        'geometry',   ST_AsGeoJSON(u.localizacao)::json,
                        'properties', json_build_object(
                            'id',          u.id,
                            'nome',        u.nome,
                            'tipo',        u.tipo,
                            'iconePin',    COALESCE(u.icone_pin, 'CRUZ'),
                            'cnes',        u.cnes,
                            'endereco',    u.endereco,
                            'telefone',    u.telefone,
                            'email',       u.email,
                            'responsavel', u.responsavel,
                            'bairroId',    b.id,
                            'bairro',      b.nm_bairro,
                            'ativo',       u.ativo
                        )
                    ) AS feat
                    FROM unidades_saude u
                    LEFT JOIN bairros b ON b.id = u.bairro_id
                    WHERE u.municipio_id = :municipioId
                      AND u.ativo = true
                      AND u.localizacao IS NOT NULL
                      %s
                    ORDER BY u.nome
                ) sub
                """.formatted(filtroTipo);

        Query query = em.createNativeQuery(sql)
                .setParameter("municipioId", municipioId);
        if (tipo != null) {
            query.setParameter("tipo", tipo.name());
        }
        return (String) query.getSingleResult();
    }

}
