package br.com.gemsbiotec.dominio.geo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;

import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "bairros")
public class Bairro {

    @Id
    @SequenceGenerator(name = "bairro_seq_gen", sequenceName = "bairro_seq")
    @GeneratedValue(generator = "bairro_seq_gen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "nm_bairro")
    private String nome;

    @Column(name = "cd_bairro")
    private String codigo;

    @Column(name = "populacao")
    private Integer populacao;

    @Column(name = "sexo_masculino")
    private Integer sexoMasculino;

    @Column(name = "sexo_feminino")
    private Integer sexoFeminino;

    @Column(name = "masculino_0_a_4_anos")
    private Integer masculino0a4Anos;

    @Column(name = "masculino_5_a_9_anos")
    private Integer masculino5a9Anos;

    @Column(name = "masculino_10_a_14_anos")
    private Integer masculino10a14Anos;

    @Column(name = "masculino_15_a_19_anos")
    private Integer masculino15a19Anos;

    @Column(name = "masculino_20_a_24_anos")
    private Integer masculino20a24Anos;

    @Column(name = "masculino_25_a_29_anos")
    private Integer masculino25a29Anos;

    @Column(name = "masculino_30_a_39_anos")
    private Integer masculino30a39Anos;

    @Column(name = "masculino_40_a_49_anos")
    private Integer masculino40a49Anos;

    @Column(name = "masculino_50_a_59_anos")
    private Integer masculino50a59Anos;

    @Column(name = "masculino_60_a_69_anos")
    private Integer masculino60a69Anos;

    @Column(name = "masculino_70_anos_ou_mais")
    private Integer masculino70AnosOuMais;

    @Column(name = "feminino_0_a_4_anos")
    private Integer feminino0a4Anos;

    @Column(name = "feminino_5_a_9_anos")
    private Integer feminino5a9Anos;

    @Column(name = "feminino_10_a_14_anos")
    private Integer feminino10a14Anos;

    @Column(name = "feminino_15_a_19_anos")
    private Integer feminino15a19Anos;

    @Column(name = "feminino_20_a_24_anos")
    private Integer feminino20a24Anos;

    @Column(name = "feminino_25_a_29_anos")
    private Integer feminino25a29Anos;

    @Column(name = "feminino_30_a_39_anos")
    private Integer feminino30a39Anos;

    @Column(name = "feminino_40_a_49_anos")
    private Integer feminino40a49Anos;

    @Column(name = "feminino_50_a_59_anos")
    private Integer feminino50a59Anos;

    @Column(name = "feminino_60_a_69_anos")
    private Integer feminino60a69Anos;

    @Column(name = "feminino_70_anos_ou_mais")
    private Integer feminino70AnosOuMais;

    @Column(name = "moradores_0_a_4_anos")
    private Integer moradores0a4Anos;

    @Column(name = "moradores_5_a_9_anos")
    private Integer moradores5a9Anos;

    @Column(name = "moradores_10_a_14_anos")
    private Integer moradores10a14Anos;

    @Column(name = "moradores_15_a_19_anos")
    private Integer moradores15a19Anos;

    @Column(name = "moradores_20_a_24_anos")
    private Integer moradores20a24Anos;

    @Column(name = "moradores_25_a_29_anos")
    private Integer moradores25a29Anos;

    @Column(name = "moradores_30_a_39_anos")
    private Integer moradores30a39Anos;

    @Column(name = "moradores_40_a_49_anos")
    private Integer moradores40a49Anos;

    @Column(name = "moradores_50_a_59_anos")
    private Integer moradores50a59Anos;

    @Column(name = "moradores_60_a_69_anos")
    private Integer moradores60a69Anos;

    @Column(name = "moradores_70_anos_ou_mais")
    private Integer moradores70AnosOuMais;

    @ManyToOne
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    @Column(name = "geometria", columnDefinition = "geometry(MultiPolygon, 4326)")
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

    public Integer getPopulacao() {
        return this.populacao;
    }

    public void setPopulacao(Integer populacao) {
        this.populacao = populacao;
    }

    public Integer getSexoMasculino() {
        return sexoMasculino;
    }

    public void setSexoMasculino(Integer sexoMasculino) {
        this.sexoMasculino = sexoMasculino;
    }

    public Integer getSexoFeminino() {
        return sexoFeminino;
    }

    public void setSexoFeminino(Integer sexoFeminino) {
        this.sexoFeminino = sexoFeminino;
    }

    public Integer getMasculino0a4Anos() {
        return masculino0a4Anos;
    }

    public void setMasculino0a4Anos(Integer masculino0a4Anos) {
        this.masculino0a4Anos = masculino0a4Anos;
    }

    public Integer getMasculino5a9Anos() {
        return masculino5a9Anos;
    }

    public void setMasculino5a9Anos(Integer masculino5a9Anos) {
        this.masculino5a9Anos = masculino5a9Anos;
    }

    public Integer getMasculino10a14Anos() {
        return masculino10a14Anos;
    }

    public void setMasculino10a14Anos(Integer masculino10a14Anos) {
        this.masculino10a14Anos = masculino10a14Anos;
    }

    public Integer getMasculino15a19Anos() {
        return masculino15a19Anos;
    }

    public void setMasculino15a19Anos(Integer masculino15a19Anos) {
        this.masculino15a19Anos = masculino15a19Anos;
    }

    public Integer getMasculino20a24Anos() {
        return masculino20a24Anos;
    }

    public void setMasculino20a24Anos(Integer masculino20a24Anos) {
        this.masculino20a24Anos = masculino20a24Anos;
    }

    public Integer getMasculino25a29Anos() {
        return masculino25a29Anos;
    }

    public void setMasculino25a29Anos(Integer masculino25a29Anos) {
        this.masculino25a29Anos = masculino25a29Anos;
    }

    public Integer getMasculino30a39Anos() {
        return masculino30a39Anos;
    }

    public void setMasculino30a39Anos(Integer masculino30a39Anos) {
        this.masculino30a39Anos = masculino30a39Anos;
    }

    public Integer getMasculino40a49Anos() {
        return masculino40a49Anos;
    }

    public void setMasculino40a49Anos(Integer masculino40a49Anos) {
        this.masculino40a49Anos = masculino40a49Anos;
    }

    public Integer getMasculino50a59Anos() {
        return masculino50a59Anos;
    }

    public void setMasculino50a59Anos(Integer masculino50a59Anos) {
        this.masculino50a59Anos = masculino50a59Anos;
    }

    public Integer getMasculino60a69Anos() {
        return masculino60a69Anos;
    }

    public void setMasculino60a69Anos(Integer masculino60a69Anos) {
        this.masculino60a69Anos = masculino60a69Anos;
    }

    public Integer getMasculino70AnosOuMais() {
        return masculino70AnosOuMais;
    }

    public void setMasculino70AnosOuMais(Integer masculino70AnosOuMais) {
        this.masculino70AnosOuMais = masculino70AnosOuMais;
    }

    public Integer getFeminino0a4Anos() {
        return feminino0a4Anos;
    }

    public void setFeminino0a4Anos(Integer feminino0a4Anos) {
        this.feminino0a4Anos = feminino0a4Anos;
    }

    public Integer getFeminino5a9Anos() {
        return feminino5a9Anos;
    }

    public void setFeminino5a9Anos(Integer feminino5a9Anos) {
        this.feminino5a9Anos = feminino5a9Anos;
    }

    public Integer getFeminino10a14Anos() {
        return feminino10a14Anos;
    }

    public void setFeminino10a14Anos(Integer feminino10a14Anos) {
        this.feminino10a14Anos = feminino10a14Anos;
    }

    public Integer getFeminino15a19Anos() {
        return feminino15a19Anos;
    }

    public void setFeminino15a19Anos(Integer feminino15a19Anos) {
        this.feminino15a19Anos = feminino15a19Anos;
    }

    public Integer getFeminino20a24Anos() {
        return feminino20a24Anos;
    }

    public void setFeminino20a24Anos(Integer feminino20a24Anos) {
        this.feminino20a24Anos = feminino20a24Anos;
    }

    public Integer getFeminino25a29Anos() {
        return feminino25a29Anos;
    }

    public void setFeminino25a29Anos(Integer feminino25a29Anos) {
        this.feminino25a29Anos = feminino25a29Anos;
    }

    public Integer getFeminino30a39Anos() {
        return feminino30a39Anos;
    }

    public void setFeminino30a39Anos(Integer feminino30a39Anos) {
        this.feminino30a39Anos = feminino30a39Anos;
    }

    public Integer getFeminino40a49Anos() {
        return feminino40a49Anos;
    }

    public void setFeminino40a49Anos(Integer feminino40a49Anos) {
        this.feminino40a49Anos = feminino40a49Anos;
    }

    public Integer getFeminino50a59Anos() {
        return feminino50a59Anos;
    }

    public void setFeminino50a59Anos(Integer feminino50a59Anos) {
        this.feminino50a59Anos = feminino50a59Anos;
    }

    public Integer getFeminino60a69Anos() {
        return feminino60a69Anos;
    }

    public void setFeminino60a69Anos(Integer feminino60a69Anos) {
        this.feminino60a69Anos = feminino60a69Anos;
    }

    public Integer getFeminino70AnosOuMais() {
        return feminino70AnosOuMais;
    }

    public void setFeminino70AnosOuMais(Integer feminino70AnosOuMais) {
        this.feminino70AnosOuMais = feminino70AnosOuMais;
    }

    public Integer getMoradores0a4Anos() {
        return moradores0a4Anos;
    }

    public void setMoradores0a4Anos(Integer moradores0a4Anos) {
        this.moradores0a4Anos = moradores0a4Anos;
    }

    public Integer getMoradores5a9Anos() {
        return moradores5a9Anos;
    }

    public void setMoradores5a9Anos(Integer moradores5a9Anos) {
        this.moradores5a9Anos = moradores5a9Anos;
    }

    public Integer getMoradores10a14Anos() {
        return moradores10a14Anos;
    }

    public void setMoradores10a14Anos(Integer moradores10a14Anos) {
        this.moradores10a14Anos = moradores10a14Anos;
    }

    public Integer getMoradores15a19Anos() {
        return moradores15a19Anos;
    }

    public void setMoradores15a19Anos(Integer moradores15a19Anos) {
        this.moradores15a19Anos = moradores15a19Anos;
    }

    public Integer getMoradores20a24Anos() {
        return moradores20a24Anos;
    }

    public void setMoradores20a24Anos(Integer moradores20a24Anos) {
        this.moradores20a24Anos = moradores20a24Anos;
    }

    public Integer getMoradores25a29Anos() {
        return moradores25a29Anos;
    }

    public void setMoradores25a29Anos(Integer moradores25a29Anos) {
        this.moradores25a29Anos = moradores25a29Anos;
    }

    public Integer getMoradores30a39Anos() {
        return moradores30a39Anos;
    }

    public void setMoradores30a39Anos(Integer moradores30a39Anos) {
        this.moradores30a39Anos = moradores30a39Anos;
    }

    public Integer getMoradores40a49Anos() {
        return moradores40a49Anos;
    }

    public void setMoradores40a49Anos(Integer moradores40a49Anos) {
        this.moradores40a49Anos = moradores40a49Anos;
    }

    public Integer getMoradores50a59Anos() {
        return moradores50a59Anos;
    }

    public void setMoradores50a59Anos(Integer moradores50a59Anos) {
        this.moradores50a59Anos = moradores50a59Anos;
    }

    public Integer getMoradores60a69Anos() {
        return moradores60a69Anos;
    }

    public void setMoradores60a69Anos(Integer moradores60a69Anos) {
        this.moradores60a69Anos = moradores60a69Anos;
    }

    public Integer getMoradores70AnosOuMais() {
        return moradores70AnosOuMais;
    }

    public void setMoradores70AnosOuMais(Integer moradores70AnosOuMais) {
        this.moradores70AnosOuMais = moradores70AnosOuMais;
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
            ", populacao='" + getPopulacao() + "'" +
            ", municipio='" + getMunicipio() + "'" +
            ", geometria='" + getGeometria() + "'" +
            "}";
    }
    
}
