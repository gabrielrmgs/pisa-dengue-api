package br.com.gemsbiotec.dominio.geo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;

import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table
public class Bairro {

    @Id
    @SequenceGenerator(name = "bairro_seq_gen", sequenceName = "bairro_seq")
    @GeneratedValue(generator = "bairro_seq_gen", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String nome;
    private String codigo;
    @ManyToOne
    private Municipio municipio;

    @Column(columnDefinition = "geometry(MultiPolygon, 4326)")
    private MultiPolygon geometria;


    public Bairro() {
    }

    public Bairro(Long id, String nome, String codigo, Municipio municipio, MultiPolygon geometria) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.municipio = municipio;
        this.geometria = geometria;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Municipio getMunicipio() {
        return this.municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public MultiPolygon getGeometria() {
        return this.geometria;
    }

    public void setGeometria(MultiPolygon geometria) {
        this.geometria = geometria;
    }

    public Bairro id(Long id) {
        setId(id);
        return this;
    }

    public Bairro nome(String nome) {
        setNome(nome);
        return this;
    }

    public Bairro codigo(String codigo) {
        setCodigo(codigo);
        return this;
    }

    public Bairro municipio(Municipio municipio) {
        setMunicipio(municipio);
        return this;
    }

    public Bairro geometria(MultiPolygon geometria) {
        setGeometria(geometria);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Bairro)) {
            return false;
        }
        Bairro bairro = (Bairro) o;
        return Objects.equals(id, bairro.id) && Objects.equals(nome, bairro.nome) && Objects.equals(codigo, bairro.codigo) && Objects.equals(municipio, bairro.municipio) && Objects.equals(geometria, bairro.geometria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, codigo, municipio, geometria);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", nome='" + getNome() + "'" +
            ", codigo='" + getCodigo() + "'" +
            ", municipio='" + getMunicipio() + "'" +
            ", geometria='" + getGeometria() + "'" +
            "}";
    }
    
}
