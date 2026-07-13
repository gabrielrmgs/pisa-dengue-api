package br.com.gemsbiotec.dominio.vacinacao;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
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
        name = "vacinacao_registros",
        indexes = {
                @Index(name = "idx_vacinacao_registros_unidade", columnList = "unidade_saude_id")
        })
public class VacinacaoRegistro {

    @Id
    @SequenceGenerator(name = "vacinacao_registro_seq_gen", sequenceName = "vacinacao_registro_seq")
    @GeneratedValue(generator = "vacinacao_registro_seq_gen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_saude_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_vacinacao_registros_unidade"))
    private UnidadeSaude unidadeSaude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", updatable = false,
            foreignKey = @ForeignKey(name = "fk_vacinacao_registros_usuario"))
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "faixa_etaria", nullable = false, length = 20, updatable = false)
    private FaixaEtariaVacinacao faixaEtaria;

    @Column(name = "quantidade", nullable = false, updatable = false)
    private Integer quantidade;

    @Column(name = "observacoes", length = 500, updatable = false)
    private String observacoes;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public Long getId() { return id; }
    public UnidadeSaude getUnidadeSaude() { return unidadeSaude; }
    public void setUnidadeSaude(UnidadeSaude unidadeSaude) { this.unidadeSaude = unidadeSaude; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public FaixaEtariaVacinacao getFaixaEtaria() { return faixaEtaria; }
    public void setFaixaEtaria(FaixaEtariaVacinacao faixaEtaria) { this.faixaEtaria = faixaEtaria; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
