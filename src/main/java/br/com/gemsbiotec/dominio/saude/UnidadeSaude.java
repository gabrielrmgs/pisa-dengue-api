package br.com.gemsbiotec.dominio.saude;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import br.com.gemsbiotec.dominio.geo.Bairro;
import br.com.gemsbiotec.dominio.geo.Municipio;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "unidades_saude",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_unidades_saude_municipio_cnes", columnNames = { "municipio_id", "cnes" })
        },
        indexes = {
                @Index(name = "idx_unidades_saude_municipio_id", columnList = "municipio_id"),
                @Index(name = "idx_unidades_saude_bairro_id", columnList = "bairro_id"),
                @Index(name = "idx_unidades_saude_tipo", columnList = "tipo"),
                @Index(name = "idx_unidades_saude_ativo", columnList = "ativo")
        })
public class UnidadeSaude {

    @Id
    @SequenceGenerator(name = "unidade_saude_seq_gen", sequenceName = "unidade_saude_seq")
    @GeneratedValue(generator = "unidade_saude_seq_gen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_unidades_saude_municipio"))
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bairro_id", foreignKey = @ForeignKey(name = "fk_unidades_saude_bairro"))
    private Bairro bairro;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoUnidadeSaude tipo;

    @Size(max = 20)
    @Column(name = "cnes", length = 20)
    private String cnes;

    @Size(max = 255)
    @Column(name = "endereco")
    private String endereco;

    @Size(max = 30)
    @Column(name = "telefone", length = 30)
    private String telefone;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 150)
    @Column(name = "responsavel", length = 150)
    private String responsavel;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "localizacao", columnDefinition = "geometry(Point, 4326)")
    private Point localizacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Long getId() {
        return id;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoUnidadeSaude getTipo() {
        return tipo;
    }

    public void setTipo(TipoUnidadeSaude tipo) {
        this.tipo = tipo;
    }

    public String getCnes() {
        return cnes;
    }

    public void setCnes(String cnes) {
        this.cnes = cnes;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Point getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Point localizacao) {
        this.localizacao = localizacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
