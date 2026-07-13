package br.com.gemsbiotec.repository;

import br.com.gemsbiotec.dominio.vacinacao.VacinacaoRegistro;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VacinacaoRegistroRepository implements PanacheRepositoryBase<VacinacaoRegistro, Long> {
}
