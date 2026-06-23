package br.com.gemsbiotec.dominio.saude;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import br.com.gemsbiotec.dominio.usuario.Usuario;
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
        name = "usuarios_unidades_saude",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_usuarios_unidades_usuario_unidade",
                columnNames = { "usuario_id", "unidade_saude_id" }),
        indexes = {
                @Index(name = "idx_usuarios_unidades_usuario", columnList = "usuario_id"),
                @Index(name = "idx_usuarios_unidades_unidade", columnList = "unidade_saude_id")
        })
public class UsuarioUnidadeSaude {

    @Id
    @SequenceGenerator(
            name = "usuario_unidade_saude_seq_gen",
            sequenceName = "usuario_unidade_saude_seq")
    @GeneratedValue(generator = "usuario_unidade_saude_seq_gen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_usuarios_unidades_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_saude_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_usuarios_unidades_unidade"))
    private UnidadeSaude unidadeSaude;

    @Column(name = "principal", nullable = false)
    private Boolean principal = false;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
