package br.com.gemsbiotec.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

import br.com.gemsbiotec.dominio.geo.Municipio;

/**
 * Repository para Municipio.
 *
 * Sem PostGIS — geometria fica nos bairros.
 * As queries aqui servem principalmente auth, admin e o ShapefileImporter.
 */
@ApplicationScoped
public class MunicipioRepository implements PanacheRepositoryBase<Municipio, Long> {

    @Inject
    EntityManager em;

    // ── lookups básicos ───────────────────────────────────────────────────────

    /**
     * Busca pelo código IBGE de 7 dígitos.
     * Usado pelo AuthResource (token JWT embute municipio_id, não codigoIbge)
     * e pelo InfoDengueService (geocode = codigoIbge).
     */
    public Optional<Municipio> findByCodigoIbge(String codigoIbge) {
        return find("codigoIbge", codigoIbge).firstResultOptional();
    }

    /**
     * Busca município ativo por ID — validação padrão em todos endpoint autenticado.
     * O token JWT traz o municipio_id; este método confirma que ainda está ativo.
     */
    public Optional<Municipio> findAtivoById(Long id) {
        return find("id = ?1 AND ativo = true", id).firstResultOptional();
    }

    /**
     * Lista todos os municípios ativos — usado pelo painel administrativo
     * para popular selects e relatórios multi-município.
     */
    public List<Municipio> listAtivos() {
        return list("ativo = true ORDER BY nome");
    }

    /**
     * Lista municípios ativos de um estado específico.
     */
    public List<Municipio> listAtivosByEstado(Long estadoId) {
        return list("estado.id = ?1 AND ativo = true ORDER BY nome", estadoId);
    }

    // ── operações do ShapefileImporter ────────────────────────────────────────

    public boolean existsByCodigoIbge(String codigoIbge) {
        return count("codigoIbge", codigoIbge) > 0;
    }

    /**
     * Atualiza centróide calculado via PostGIS após importação dos bairros.
     * Chamado pelo ShapefileImporter após persistir todos os bairros do município.
     *
     * ST_Union agrega todos os polígonos dos bairros em uma geometria única,
     * ST_Centroid calcula o centro geométrico, ST_X/ST_Y extraem as coordenadas.
     */
    public int recalcularCentroidesPorMunicipio(Long municipioId) {
        return em.createNativeQuery("""
                UPDATE municipios m
                SET    latitude  = ST_Y(ST_Centroid(ST_Union(b.geometria))),
                       longitude = ST_X(ST_Centroid(ST_Union(b.geometria)))
                FROM   bairros b
                WHERE  b.municipio_id = m.id
                  AND  m.id = :municipioId
                """)
            .setParameter("municipioId", municipioId)
            .executeUpdate();
    }
}
