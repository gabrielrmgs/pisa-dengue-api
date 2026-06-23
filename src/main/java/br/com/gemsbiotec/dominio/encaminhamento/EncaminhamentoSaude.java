package br.com.gemsbiotec.dominio.encaminhamento;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
import br.com.gemsbiotec.dominio.triagem.TriagemDengue;
import br.com.gemsbiotec.dominio.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "encaminhamentos_saude",
        indexes = {
                @Index(name = "idx_encaminhamentos_municipio_status", columnList = "municipio_id, status"),
                @Index(name = "idx_encaminhamentos_origem", columnList = "unidade_origem_id, solicitado_em"),
                @Index(name = "idx_encaminhamentos_destino", columnList = "unidade_destino_id, solicitado_em")
        })
public class EncaminhamentoSaude {

    @Id
    @SequenceGenerator(name = "encaminhamento_saude_seq_gen", sequenceName = "encaminhamento_saude_seq")
    @GeneratedValue(generator = "encaminhamento_saude_seq_gen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipio_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_encaminhamentos_municipio"))
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "triagem_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_encaminhamentos_triagem"))
    private TriagemDengue triagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_origem_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_encaminhamentos_unidade_origem"))
    private UnidadeSaude unidadeOrigem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_destino_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_encaminhamentos_unidade_destino"))
    private UnidadeSaude unidadeDestino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_solicitante_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_encaminhamentos_usuario_solicitante"))
    private Usuario usuarioSolicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_resposta_id",
            foreignKey = @ForeignKey(name = "fk_encaminhamentos_usuario_resposta"))
    private Usuario usuarioResposta;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private StatusEncaminhamentoSaude status = StatusEncaminhamentoSaude.PENDENTE_ACEITE;

    @Column(name = "exige_regulacao", nullable = false)
    private Boolean exigeRegulacao = false;

    @Column(name = "justificativa_recusa", length = 1000)
    private String justificativaRecusa;

    @Column(name = "observacao_resposta", length = 1000)
    private String observacaoResposta;

    @Column(name = "solicitado_em", nullable = false)
    private LocalDateTime solicitadoEm;

    @Column(name = "respondido_em")
    private LocalDateTime respondidoEm;

    @Column(name = "concluido_em")
    private LocalDateTime concluidoEm;

    @Column(name = "cancelado_em")
    private LocalDateTime canceladoEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Long getId() { return id; }
    public Municipio getMunicipio() { return municipio; }
    public void setMunicipio(Municipio municipio) { this.municipio = municipio; }
    public TriagemDengue getTriagem() { return triagem; }
    public void setTriagem(TriagemDengue triagem) { this.triagem = triagem; }
    public UnidadeSaude getUnidadeOrigem() { return unidadeOrigem; }
    public void setUnidadeOrigem(UnidadeSaude unidadeOrigem) { this.unidadeOrigem = unidadeOrigem; }
    public UnidadeSaude getUnidadeDestino() { return unidadeDestino; }
    public void setUnidadeDestino(UnidadeSaude unidadeDestino) { this.unidadeDestino = unidadeDestino; }
    public Usuario getUsuarioSolicitante() { return usuarioSolicitante; }
    public void setUsuarioSolicitante(Usuario usuarioSolicitante) { this.usuarioSolicitante = usuarioSolicitante; }
    public Usuario getUsuarioResposta() { return usuarioResposta; }
    public void setUsuarioResposta(Usuario usuarioResposta) { this.usuarioResposta = usuarioResposta; }
    public StatusEncaminhamentoSaude getStatus() { return status; }
    public void setStatus(StatusEncaminhamentoSaude status) { this.status = status; }
    public Boolean getExigeRegulacao() { return exigeRegulacao; }
    public void setExigeRegulacao(Boolean exigeRegulacao) { this.exigeRegulacao = exigeRegulacao; }
    public String getJustificativaRecusa() { return justificativaRecusa; }
    public void setJustificativaRecusa(String justificativaRecusa) { this.justificativaRecusa = justificativaRecusa; }
    public String getObservacaoResposta() { return observacaoResposta; }
    public void setObservacaoResposta(String observacaoResposta) { this.observacaoResposta = observacaoResposta; }
    public LocalDateTime getSolicitadoEm() { return solicitadoEm; }
    public void setSolicitadoEm(LocalDateTime solicitadoEm) { this.solicitadoEm = solicitadoEm; }
    public LocalDateTime getRespondidoEm() { return respondidoEm; }
    public void setRespondidoEm(LocalDateTime respondidoEm) { this.respondidoEm = respondidoEm; }
    public LocalDateTime getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(LocalDateTime concluidoEm) { this.concluidoEm = concluidoEm; }
    public LocalDateTime getCanceladoEm() { return canceladoEm; }
    public void setCanceladoEm(LocalDateTime canceladoEm) { this.canceladoEm = canceladoEm; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
