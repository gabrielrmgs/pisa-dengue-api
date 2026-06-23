package br.com.gemsbiotec.repository;

import java.util.List;

import br.com.gemsbiotec.dominio.saude.UnidadeCapacidade;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnidadeCapacidadeRepository implements PanacheRepositoryBase<UnidadeCapacidade, Long> {

    public List<UnidadeCapacidade> listByUnidadeETenant(Long unidadeId, Long municipioId) {
        return find("""
                FROM UnidadeCapacidade vinculo
                JOIN FETCH vinculo.capacidade capacidade
                WHERE vinculo.unidadeSaude.id = ?1
                  AND vinculo.unidadeSaude.municipio.id = ?2
                ORDER BY capacidade.categoria, capacidade.nome
                """, unidadeId, municipioId).list();
    }

    public List<UnidadeCapacidade> listDisponiveisByMunicipio(Long municipioId) {
        return find("""
                FROM UnidadeCapacidade vinculo
                JOIN FETCH vinculo.unidadeSaude unidade
                LEFT JOIN FETCH unidade.bairro
                JOIN FETCH vinculo.capacidade capacidade
                WHERE unidade.municipio.id = ?1
                  AND unidade.ativo = true
                  AND vinculo.disponivel = true
                  AND capacidade.ativo = true
                ORDER BY unidade.nome, capacidade.categoria, capacidade.nome
                """, municipioId).list();
    }

    public long deleteByUnidadeETenant(Long unidadeId, Long municipioId) {
        return delete("unidadeSaude.id = ?1 AND unidadeSaude.municipio.id = ?2", unidadeId, municipioId);
    }
}
