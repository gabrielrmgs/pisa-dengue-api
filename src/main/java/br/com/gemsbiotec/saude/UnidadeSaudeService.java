package br.com.gemsbiotec.saude;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import br.com.gemsbiotec.auth.TenantContext;
import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.dominio.saude.IconePinUnidadeSaude;
import br.com.gemsbiotec.dominio.saude.TipoUnidadeSaude;
import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
import br.com.gemsbiotec.repository.BairroRepository;
import br.com.gemsbiotec.repository.MunicipioRepository;
import br.com.gemsbiotec.repository.UnidadeSaudeRepository;
import br.com.gemsbiotec.saude.dto.AtualizarUnidadeSaudeRequest;
import br.com.gemsbiotec.saude.dto.CriarUnidadeSaudeRequest;
import br.com.gemsbiotec.saude.dto.UnidadeSaudeResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UnidadeSaudeService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private final TenantContext tenantContext;
    private final MunicipioRepository municipioRepository;
    private final BairroRepository bairroRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;

    public UnidadeSaudeService(
            TenantContext tenantContext,
            MunicipioRepository municipioRepository,
            BairroRepository bairroRepository,
            UnidadeSaudeRepository unidadeSaudeRepository) {
        this.tenantContext = tenantContext;
        this.municipioRepository = municipioRepository;
        this.bairroRepository = bairroRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
    }

    public List<UnidadeSaudeResponse> listar(TipoUnidadeSaude tipo, Long bairroId, Boolean ativo) {
        Long municipioId = municipioIdObrigatorio();
        return unidadeSaudeRepository.listByMunicipio(municipioId, tipo, bairroId, ativo)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UnidadeSaudeResponse buscar(Long id) {
        Long municipioId = municipioIdObrigatorio();
        UnidadeSaude unidade = unidadeSaudeRepository.findByIdETenant(id, municipioId)
                .orElseThrow(() -> new NotFoundException("Unidade de saude nao encontrada."));
        return toResponse(unidade);
    }

    @Transactional
    public UnidadeSaudeResponse criar(CriarUnidadeSaudeRequest request) {
        Long municipioId = municipioIdObrigatorio();
        Municipio municipio = municipioRepository.findAtivoById(municipioId)
                .orElseThrow(() -> new NotFoundException("Municipio ativo nao encontrado."));

        String cnes = normalizarTexto(request.cnes());
        if (unidadeSaudeRepository.existsByCnesETenant(cnes, municipioId)) {
            throw new WebApplicationException("CNES ja cadastrado para este municipio.", Response.Status.CONFLICT);
        }

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setMunicipio(municipio);
        aplicarDados(unidade, request.nome(), request.tipo(), request.iconePin(), cnes, request.endereco(), request.telefone(),
                request.email(), request.responsavel(), request.bairroId(), request.latitude(), request.longitude(),
                null);
        unidade.setAtivo(true);

        unidadeSaudeRepository.persist(unidade);
        unidadeSaudeRepository.flush();
        return toResponse(unidade);
    }

    @Transactional
    public UnidadeSaudeResponse atualizar(Long id, AtualizarUnidadeSaudeRequest request) {
        Long municipioId = municipioIdObrigatorio();
        UnidadeSaude unidade = unidadeSaudeRepository.findByIdETenant(id, municipioId)
                .orElseThrow(() -> new NotFoundException("Unidade de saude nao encontrada."));

        String cnes = normalizarTexto(request.cnes());
        if (unidadeSaudeRepository.existsByCnesETenantExcludingId(cnes, municipioId, id)) {
            throw new WebApplicationException("CNES ja cadastrado para este municipio.", Response.Status.CONFLICT);
        }

        aplicarDados(unidade, request.nome(), request.tipo(), request.iconePin(), cnes, request.endereco(), request.telefone(),
                request.email(), request.responsavel(), request.bairroId(), request.latitude(), request.longitude(),
                request.ativo());
        return toResponse(unidade);
    }

    @Transactional
    public UnidadeSaudeResponse ativar(Long id) {
        UnidadeSaude unidade = buscarEntidade(id);
        unidade.setAtivo(true);
        return toResponse(unidade);
    }

    @Transactional
    public UnidadeSaudeResponse desativar(Long id) {
        UnidadeSaude unidade = buscarEntidade(id);
        unidade.setAtivo(false);
        return toResponse(unidade);
    }

    @Transactional
    public void remover(Long id) {
        UnidadeSaude unidade = buscarEntidade(id);
        unidade.setAtivo(false);
    }

    public String geoJson(TipoUnidadeSaude tipo) {
        return unidadeSaudeRepository.getGeoJsonUnidades(municipioIdObrigatorio(), tipo);
    }

    private void aplicarDados(
            UnidadeSaude unidade,
            String nome,
            TipoUnidadeSaude tipo,
            IconePinUnidadeSaude iconePin,
            String cnes,
            String endereco,
            String telefone,
            String email,
            String responsavel,
            Long bairroId,
            Double latitude,
            Double longitude,
            Boolean ativo) {
        unidade.setNome(nome.trim());
        unidade.setTipo(tipo);
        unidade.setIconePin(iconePin != null ? iconePin : IconePinUnidadeSaude.CRUZ);
        unidade.setCnes(cnes);
        unidade.setEndereco(normalizarTexto(endereco));
        unidade.setTelefone(normalizarTexto(telefone));
        unidade.setEmail(normalizarEmail(email));
        unidade.setResponsavel(normalizarTexto(responsavel));
        unidade.setBairro(resolverBairro(bairroId));
        unidade.setLatitude(latitude);
        unidade.setLongitude(longitude);
        unidade.setLocalizacao(criarPonto(longitude, latitude));
        if (ativo != null) {
            unidade.setAtivo(ativo);
        }
    }

    private UnidadeSaude buscarEntidade(Long id) {
        Long municipioId = municipioIdObrigatorio();
        return unidadeSaudeRepository.findByIdETenant(id, municipioId)
                .orElseThrow(() -> new NotFoundException("Unidade de saude nao encontrada."));
    }

    private Bairro resolverBairro(Long bairroId) {
        if (bairroId == null) {
            return null;
        }
        return bairroRepository.findByIdETenant(bairroId, municipioIdObrigatorio())
                .orElseThrow(() -> new BadRequestException("Bairro nao pertence ao municipio logado."));
    }

    private Long municipioIdObrigatorio() {
        Long municipioId = tenantContext.getMunicipioId();
        if (municipioId == null) {
            throw new NotAuthorizedException("Municipio nao encontrado no token.");
        }
        return municipioId;
    }

    private Point criarPonto(Double longitude, Double latitude) {
        Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(4326);
        return point;
    }

    private String normalizarTexto(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizarEmail(String value) {
        String email = normalizarTexto(value);
        return email != null ? email.toLowerCase() : null;
    }

    private UnidadeSaudeResponse toResponse(UnidadeSaude unidade) {
        Municipio municipio = unidade.getMunicipio();
        Bairro bairro = unidade.getBairro();
        return new UnidadeSaudeResponse(
                unidade.getId(),
                municipio != null ? municipio.getId() : null,
                municipio != null ? municipio.getNome() : null,
                bairro != null ? bairro.getId() : null,
                bairro != null ? bairro.getNome() : null,
                unidade.getNome(),
                unidade.getTipo(),
                unidade.getIconePin(),
                unidade.getCnes(),
                unidade.getEndereco(),
                unidade.getTelefone(),
                unidade.getEmail(),
                unidade.getResponsavel(),
                unidade.getLatitude(),
                unidade.getLongitude(),
                unidade.getAtivo(),
                unidade.getCriadoEm(),
                unidade.getAtualizadoEm());
    }
}
