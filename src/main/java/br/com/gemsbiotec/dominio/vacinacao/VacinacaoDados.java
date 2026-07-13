package br.com.gemsbiotec.dominio.vacinacao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.gemsbiotec.dominio.saude.UnidadeSaude;
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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "vacinacao_dados",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_vacinacao_dados_unidade_data", columnNames = { "unidade_saude_id", "data_referencia" })
        },
        indexes = {
                @Index(name = "idx_vacinacao_dados_unidade", columnList = "unidade_saude_id"),
                @Index(name = "idx_vacinacao_dados_data", columnList = "data_referencia")
        })
public class VacinacaoDados {

    @Id
    @SequenceGenerator(name = "vacinacao_dados_seq_gen", sequenceName = "vacinacao_dados_seq")
    @GeneratedValue(generator = "vacinacao_dados_seq_gen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_saude_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_vacinacao_dados_unidade"))
    private UnidadeSaude unidadeSaude;

    @Column(name = "data_referencia", nullable = false, updatable = false)
    private LocalDate dataReferencia;

    @Column(name = "doses_10_14", nullable = false)
    private Integer doses10a14 = 0;

    @Column(name = "doses_18_59", nullable = false)
    private Integer doses18a59 = 0;

    @Column(name = "doses_total", nullable = false)
    private Integer dosesTotal = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem", nullable = false, length = 20)
    private OrigemVacinacao origem = OrigemVacinacao.IMPORTACAO;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Long getId() { return id; }
    public UnidadeSaude getUnidadeSaude() { return unidadeSaude; }
    public void setUnidadeSaude(UnidadeSaude unidadeSaude) { this.unidadeSaude = unidadeSaude; }
    public LocalDate getDataReferencia() { return dataReferencia; }
    public void setDataReferencia(LocalDate dataReferencia) { this.dataReferencia = dataReferencia; }
    public Integer getDoses10a14() { return doses10a14; }
    public void setDoses10a14(Integer doses10a14) { this.doses10a14 = doses10a14; }
    public Integer getDoses18a59() { return doses18a59; }
    public void setDoses18a59(Integer doses18a59) { this.doses18a59 = doses18a59; }
    public Integer getDosesTotal() { return dosesTotal; }
    public void setDosesTotal(Integer dosesTotal) { this.dosesTotal = dosesTotal; }
    public OrigemVacinacao getOrigem() { return origem; }
    public void setOrigem(OrigemVacinacao origem) { this.origem = origem; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
