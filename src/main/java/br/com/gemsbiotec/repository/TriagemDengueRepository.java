package br.com.gemsbiotec.repository;

import java.util.List;
import java.util.Optional;

import br.com.gemsbiotec.dominio.triagem.TriagemDengue;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TriagemDengueRepository implements PanacheRepositoryBase<TriagemDengue, Long> {

    public Optional<TriagemDengue> findByIdETenant(Long id, Long municipioId) {
        return find("""
                FROM TriagemDengue triagem
                JOIN FETCH triagem.municipio
                JOIN FETCH triagem.unidadeOrigem origem
                LEFT JOIN FETCH origem.bairro
                JOIN FETCH triagem.usuarioTriagem usuario
                LEFT JOIN FETCH triagem.unidadeDestinoSugerida destino
                LEFT JOIN FETCH destino.bairro
                WHERE triagem.id = ?1
                  AND triagem.municipio.id = ?2
                """, id, municipioId).firstResultOptional();
    }

    public List<TriagemDengue> listRecentesByTenant(Long municipioId, int limite) {
        return find("""
                FROM TriagemDengue triagem
                JOIN FETCH triagem.unidadeOrigem origem
                LEFT JOIN FETCH origem.bairro
                JOIN FETCH triagem.usuarioTriagem usuario
                LEFT JOIN FETCH triagem.unidadeDestinoSugerida destino
                LEFT JOIN FETCH destino.bairro
                WHERE triagem.municipio.id = ?1
                ORDER BY triagem.criadoEm DESC
                """, municipioId).page(0, limite).list();
    }
}
