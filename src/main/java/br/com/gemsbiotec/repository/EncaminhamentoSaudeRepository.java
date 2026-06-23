package br.com.gemsbiotec.repository;

import java.util.List;
import java.util.Optional;

import br.com.gemsbiotec.dominio.encaminhamento.EncaminhamentoSaude;
import br.com.gemsbiotec.dominio.encaminhamento.StatusEncaminhamentoSaude;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EncaminhamentoSaudeRepository implements PanacheRepositoryBase<EncaminhamentoSaude, Long> {

    public Optional<EncaminhamentoSaude> findByIdETenant(Long id, Long municipioId) {
        return find("""
                FROM EncaminhamentoSaude e
                JOIN FETCH e.triagem triagem
                JOIN FETCH e.unidadeOrigem origem
                LEFT JOIN FETCH origem.bairro
                JOIN FETCH e.unidadeDestino destino
                LEFT JOIN FETCH destino.bairro
                JOIN FETCH e.usuarioSolicitante solicitante
                LEFT JOIN FETCH e.usuarioResposta resposta
                WHERE e.id = ?1
                  AND e.municipio.id = ?2
                """, id, municipioId).firstResultOptional();
    }

    public Optional<EncaminhamentoSaude> findAtivoByTriagem(Long triagemId, Long municipioId) {
        return find("""
                FROM EncaminhamentoSaude e
                JOIN FETCH e.triagem triagem
                JOIN FETCH e.unidadeOrigem origem
                LEFT JOIN FETCH origem.bairro
                JOIN FETCH e.unidadeDestino destino
                LEFT JOIN FETCH destino.bairro
                JOIN FETCH e.usuarioSolicitante solicitante
                LEFT JOIN FETCH e.usuarioResposta resposta
                WHERE triagem.id = ?1
                  AND e.municipio.id = ?2
                  AND e.status IN ?3
                """, triagemId, municipioId,
                List.of(StatusEncaminhamentoSaude.PENDENTE_ACEITE, StatusEncaminhamentoSaude.ACEITO))
                .firstResultOptional();
    }

    public List<EncaminhamentoSaude> listByTenant(Long municipioId, String caixa, int limite) {
        return find("""
                FROM EncaminhamentoSaude e
                JOIN FETCH e.triagem triagem
                JOIN FETCH e.unidadeOrigem origem
                LEFT JOIN FETCH origem.bairro
                JOIN FETCH e.unidadeDestino destino
                LEFT JOIN FETCH destino.bairro
                JOIN FETCH e.usuarioSolicitante solicitante
                LEFT JOIN FETCH e.usuarioResposta resposta
                WHERE e.municipio.id = ?1
                ORDER BY e.solicitadoEm DESC
                """, municipioId).page(0, limite).list();
    }
}
