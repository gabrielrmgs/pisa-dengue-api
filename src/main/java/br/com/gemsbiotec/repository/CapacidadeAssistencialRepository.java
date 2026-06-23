package br.com.gemsbiotec.repository;

import java.util.List;

import br.com.gemsbiotec.dominio.saude.CapacidadeAssistencial;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CapacidadeAssistencialRepository implements PanacheRepositoryBase<CapacidadeAssistencial, Long> {

    public List<CapacidadeAssistencial> listAtivas() {
        return list("ativo = true ORDER BY categoria, nome");
    }
}
