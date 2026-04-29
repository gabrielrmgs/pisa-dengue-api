package br.com.gemsbiotec.dominio.geo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.postgis.jdbc.geometry.MultiPolygon;
import java.util.Objects;

@Entity
@Table
public class Bairro {

    private Long id;
    private String nome;
    private String codigo;
    private Municipio municipio;
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
