package br.com.gemsbiotec.dominio.saude;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "unidades_capacidades",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_unidades_capacidades_unidade_capacidade",
                columnNames = { "unidade_saude_id", "capacidade_id" }),
        indexes = {
                @Index(name = "idx_unidades_capacidades_unidade", columnList = "unidade_saude_id"),
                @Index(name = "idx_unidades_capacidades_capacidade", columnList = "capacidade_id")
        })
public class UnidadeCapacidade {

    @Id
    @SequenceGenerator(name = "unidade_capacidade_seq_gen", sequenceName = "unidade_capacidade_seq")
    @GeneratedValue(generator = "unidade_capacidade_seq_gen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_saude_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_unidades_capacidades_unidade"))
    private UnidadeSaude unidadeSaude;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "capacidade_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_unidades_capacidades_capacidade"))
    private CapacidadeAssistencial capacidade;

    @Column(name = "disponivel", nullable = false)
    private Boolean disponivel = true;

    @Column(name = "horario_atendimento", length = 150)
    private String horarioAtendimento;

    @Column(name = "restricoes", length = 500)
    private String restricoes;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Long getId() { return id; }
    public UnidadeSaude getUnidadeSaude() { return unidadeSaude; }
    public void setUnidadeSaude(UnidadeSaude unidadeSaude) { this.unidadeSaude = unidadeSaude; }
    public CapacidadeAssistencial getCapacidade() { return capacidade; }
    public void setCapacidade(CapacidadeAssistencial capacidade) { this.capacidade = capacidade; }
    public Boolean getDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }
    public String getHorarioAtendimento() { return horarioAtendimento; }
    public void setHorarioAtendimento(String horarioAtendimento) { this.horarioAtendimento = horarioAtendimento; }
    public String getRestricoes() { return restricoes; }
    public void setRestricoes(String restricoes) { this.restricoes = restricoes; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
