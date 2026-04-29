package br.com.gemsbiotec.dominio.geo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table
public class Municipio {

    @Id
    @SequenceGenerator(sequenceName = "municipio_seq", name = "municipio_seq_gen")
    @GeneratedValue(generator = "municipio_seq_gen", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String nome;
    @ManyToOne
    private Estado estado;
    private String codigoIbge;
    private int populacao;
    private boolean ativo;

    public Municipio() {
    }

    public Municipio(Long id, String nome, Estado estado, String codigoIbge, int populacao, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
        this.codigoIbge = codigoIbge;
        this.populacao = populacao;
        this.ativo = ativo;
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

    public Estado getEstado() {
        return this.estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getCodigoIbge() {
        return this.codigoIbge;
    }

    public void setCodigoIbge(String codigoIbge) {
        this.codigoIbge = codigoIbge;
    }

    public int getPopulacao() {
        return this.populacao;
    }

    public void setPopulacao(int populacao) {
        this.populacao = populacao;
    }

    public boolean isAtivo() {
        return this.ativo;
    }

    public boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Municipio id(Long id) {
        setId(id);
        return this;
    }

    public Municipio nome(String nome) {
        setNome(nome);
        return this;
    }

    public Municipio estado(Estado estado) {
        setEstado(estado);
        return this;
    }

    public Municipio codigoIbge(String codigoIbge) {
        setCodigoIbge(codigoIbge);
        return this;
    }

    public Municipio populacao(int populacao) {
        setPopulacao(populacao);
        return this;
    }

    public Municipio ativo(boolean ativo) {
        setAtivo(ativo);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Municipio)) {
            return false;
        }
        Municipio municipio = (Municipio) o;
        return Objects.equals(id, municipio.id) && Objects.equals(nome, municipio.nome) && Objects.equals(estado, municipio.estado) && Objects.equals(codigoIbge, municipio.codigoIbge) && populacao == municipio.populacao && ativo == municipio.ativo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, estado, codigoIbge, populacao, ativo);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", nome='" + getNome() + "'" +
            ", estado='" + getEstado() + "'" +
            ", codigoIbge='" + getCodigoIbge() + "'" +
            ", populacao='" + getPopulacao() + "'" +
            ", ativo='" + isAtivo() + "'" +
            "}";
    }    


}
