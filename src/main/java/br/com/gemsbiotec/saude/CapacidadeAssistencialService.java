package br.com.gemsbiotec.saude;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.saude.CapacidadeAssistencial;
import br.com.gemsbiotec.dominio.saude.UnidadeCapacidade;
import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
import br.com.gemsbiotec.repository.CapacidadeAssistencialRepository;
import br.com.gemsbiotec.repository.UnidadeCapacidadeRepository;
import br.com.gemsbiotec.repository.UnidadeSaudeRepository;
import br.com.gemsbiotec.saude.dto.AtualizarCapacidadesUnidadeRequest;
import br.com.gemsbiotec.saude.dto.CapacidadeAssistencialResponse;
import br.com.gemsbiotec.saude.dto.CapacidadeUnidadeRequest;
import br.com.gemsbiotec.saude.dto.CapacidadeUnidadeResponse;
import br.com.gemsbiotec.saude.dto.CapacidadesUnidadeResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class CapacidadeAssistencialService {

    private final TenantContext tenantContext;
    private final CapacidadeAssistencialRepository capacidadeRepository;
    private final UnidadeCapacidadeRepository unidadeCapacidadeRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;

    public CapacidadeAssistencialService(
            TenantContext tenantContext,
            CapacidadeAssistencialRepository capacidadeRepository,
            UnidadeCapacidadeRepository unidadeCapacidadeRepository,
            UnidadeSaudeRepository unidadeSaudeRepository) {
        this.tenantContext = tenantContext;
        this.capacidadeRepository = capacidadeRepository;
        this.unidadeCapacidadeRepository = unidadeCapacidadeRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
    }

    public List<CapacidadeAssistencialResponse> listarCatalogo() {
        return capacidadeRepository.listAtivas().stream()
                .map(this::toCatalogoResponse)
                .toList();
    }

    @Transactional
    public CapacidadesUnidadeResponse buscarPorUnidade(Long unidadeId) {
        Long municipioId = municipioIdObrigatorio();
        UnidadeSaude unidade = buscarUnidade(unidadeId, municipioId);
        return toResponse(unidade,
                unidadeCapacidadeRepository.listByUnidadeETenant(unidadeId, municipioId));
    }

    @Transactional
    public CapacidadesUnidadeResponse atualizar(
            Long unidadeId,
            AtualizarCapacidadesUnidadeRequest request) {
        Long municipioId = municipioIdObrigatorio();
        UnidadeSaude unidade = buscarUnidade(unidadeId, municipioId);

        Set<Long> ids = new HashSet<>();
        for (CapacidadeUnidadeRequest item : request.capacidades()) {
            if (!ids.add(item.capacidadeId())) {
                throw new BadRequestException("A lista possui capacidades duplicadas.");
            }
        }

        List<CapacidadeAssistencial> capacidades = request.capacidades().stream()
                .map(item -> capacidadeRepository.findByIdOptional(item.capacidadeId())
                        .filter(capacidade -> Boolean.TRUE.equals(capacidade.getAtivo()))
                        .orElseThrow(() -> new NotFoundException(
                                "Capacidade assistencial nao encontrada: " + item.capacidadeId())))
                .toList();

        unidadeCapacidadeRepository.deleteByUnidadeETenant(unidadeId, municipioId);
        unidadeCapacidadeRepository.flush();

        for (int indice = 0; indice < request.capacidades().size(); indice++) {
            CapacidadeUnidadeRequest item = request.capacidades().get(indice);
            UnidadeCapacidade vinculo = new UnidadeCapacidade();
            vinculo.setUnidadeSaude(unidade);
            vinculo.setCapacidade(capacidades.get(indice));
            vinculo.setDisponivel(item.disponivel());
            vinculo.setHorarioAtendimento(normalizar(item.horarioAtendimento()));
            vinculo.setRestricoes(normalizar(item.restricoes()));
            vinculo.setObservacoes(normalizar(item.observacoes()));
            unidadeCapacidadeRepository.persist(vinculo);
        }
        unidadeCapacidadeRepository.flush();

        return toResponse(unidade,
                unidadeCapacidadeRepository.listByUnidadeETenant(unidadeId, municipioId));
    }

    private UnidadeSaude buscarUnidade(Long unidadeId, Long municipioId) {
        return unidadeSaudeRepository.findByIdETenant(unidadeId, municipioId)
                .orElseThrow(() -> new NotFoundException("Unidade de saude nao encontrada."));
    }

    private Long municipioIdObrigatorio() {
        Long municipioId = tenantContext.getMunicipioId();
        if (municipioId == null) {
            throw new NotAuthorizedException("Municipio nao encontrado no token.");
        }
        return municipioId;
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private CapacidadeAssistencialResponse toCatalogoResponse(CapacidadeAssistencial capacidade) {
        return new CapacidadeAssistencialResponse(
                capacidade.getId(), capacidade.getCodigo(), capacidade.getNome(),
                capacidade.getDescricao(), capacidade.getCategoria());
    }

    private CapacidadesUnidadeResponse toResponse(
            UnidadeSaude unidade,
            List<UnidadeCapacidade> vinculos) {
        List<CapacidadeUnidadeResponse> capacidades = vinculos.stream()
                .map(vinculo -> {
                    CapacidadeAssistencial capacidade = vinculo.getCapacidade();
                    return new CapacidadeUnidadeResponse(
                            capacidade.getId(), capacidade.getCodigo(), capacidade.getNome(),
                            capacidade.getDescricao(), capacidade.getCategoria(), vinculo.getDisponivel(),
                            vinculo.getHorarioAtendimento(), vinculo.getRestricoes(), vinculo.getObservacoes(),
                            vinculo.getAtualizadoEm());
                })
                .toList();
        return new CapacidadesUnidadeResponse(unidade.getId(), unidade.getNome(), capacidades);
    }
}
