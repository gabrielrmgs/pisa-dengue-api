package br.com.gemsbiotec.repository;

import java.util.List;

import br.com.gemsbiotec.dominio.saude.UsuarioUnidadeSaude;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioUnidadeSaudeRepository implements PanacheRepositoryBase<UsuarioUnidadeSaude, Long> {

    public List<UsuarioUnidadeSaude> listByUsuarioETenant(Long usuarioId, Long municipioId) {
        return find("""
                FROM UsuarioUnidadeSaude vinculo
                JOIN FETCH vinculo.unidadeSaude unidade
                WHERE vinculo.usuario.id = ?1
                  AND vinculo.usuario.municipio.id = ?2
                  AND unidade.municipio.id = ?2
                ORDER BY vinculo.principal DESC, unidade.nome
                """, usuarioId, municipioId).list();
    }

    public long deleteByUsuarioETenant(Long usuarioId, Long municipioId) {
        return delete("usuario.id = ?1 AND usuario.municipio.id = ?2", usuarioId, municipioId);
    }

    public boolean existsByUsuarioUnidadeETenant(Long usuarioId, Long unidadeId, Long municipioId) {
        return count("""
                usuario.id = ?1
                AND usuario.municipio.id = ?3
                AND unidadeSaude.id = ?2
                AND unidadeSaude.municipio.id = ?3
                """, usuarioId, unidadeId, municipioId) > 0;
    }
}
