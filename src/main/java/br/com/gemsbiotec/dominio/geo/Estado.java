package br.com.gemsbiotec.dominio.geo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table
public class Estado {

    @Id
    @SequenceGenerator(name = "seq_estado_gen", sequenceName = "seq_estado")
    @GeneratedValue(generator = "seq_estado_gen", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String codigoUf;
    private String nome;
    private String sigla;
    /**
     * Relacionamento bidirecional declarado aqui apenas para navegação JPA.
     * Nunca carregue esta coleção diretamente — use MunicipioRepository.
     */
    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Municipio> municipios = new ArrayList<>();


    public Estado() {
    }

    public Estado(Long id, String codigoUf, String nome, String sigla) {
        this.id = id;
        this.codigoUf = codigoUf;
        this.nome = nome;
        this.sigla = sigla;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getcodigoUf() {
        return this.codigoUf;
    }

    public void setcodigoUf(String codigoUf) {
        this.codigoUf = codigoUf;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return this.sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public List<Municipio> getMunicipios() {
        return this.municipios;
    }

    public void setMunicipios(List<Municipio> municipios) {
        this.municipios = municipios;
    }

    public Estado id(Long id) {
        setId(id);
        return this;
    }

    public Estado codigoUf(String codigoUf) {
        setcodigoUf(codigoUf);
        return this;
    }

    public Estado nome(String nome) {
        setNome(nome);
        return this;
    }

    public Estado sigla(String sigla) {
        setSigla(sigla);
        return this;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", codigoUf='" + getcodigoUf() + "'" +
            ", nome='" + getNome() + "'" +
            ", sigla='" + getSigla() + "'" +
            "}";
    }

}
