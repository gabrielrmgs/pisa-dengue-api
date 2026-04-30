package br.com.gemsbiotec.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

import br.com.gemsbiotec.dominio.geo.Estado;

/**
 * Repository para Estado.
 *
 * Lookup table simples — sem PostGIS, sem queries complexas.
 * Usado principalmente pelo ShapefileImporter na carga inicial
 * e pelo MunicipioRepository para resolver a FK.
 */
@ApplicationScoped
public class EstadoRepository implements PanacheRepositoryBase<Estado, Long> {

    /**
     * Busca por código IBGE de 2 dígitos (ex: "22" para Piauí).
     * Usado pelo ShapefileImporter para evitar duplicatas.
     */
    public Optional<Estado> findByCodigoUf(String codigoUf) {
        return find("codigoUf", codigoUf).firstResultOptional();
    }

    /**
     * Busca por sigla (ex: "PI").
     * Usado pelo AuthResource para exibir o estado do município logado.
     */
    public Optional<Estado> findBySigla(String sigla) {
        return find("sigla", sigla.toUpperCase()).firstResultOptional();
    }

    /**
     * Verifica existência antes de inserir — evita query + persist separados
     * no ShapefileImporter quando o estado já existe.
     */
    public boolean existsByCodigoUf(String codigoUf) {
        return count("codigoUf", codigoUf) > 0;
    }
}
