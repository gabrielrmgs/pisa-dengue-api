package br.com.gemsbiotec.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import br.com.gemsbiotec.dominio.vacinacao.VacinacaoDados;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VacinacaoDadosRepository implements PanacheRepositoryBase<VacinacaoDados, Long> {

    public List<VacinacaoDados> listByMunicipioOrdenado(Long municipioId) {
        return find("""
                FROM VacinacaoDados v
                JOIN FETCH v.unidadeSaude u
                LEFT JOIN FETCH u.bairro
                WHERE u.municipio.id = ?1
                ORDER BY u.id, v.dataReferencia DESC
                """, municipioId).list();
    }

    public Optional<VacinacaoDados> findByUnidadeEData(Long unidadeId, LocalDate data) {
        return find("unidadeSaude.id = ?1 AND dataReferencia = ?2", unidadeId, data).firstResultOptional();
    }
}
