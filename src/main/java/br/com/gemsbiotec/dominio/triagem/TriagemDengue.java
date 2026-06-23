package br.com.gemsbiotec.dominio.triagem;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.gemsbiotec.dominio.geo.Municipio;
import br.com.gemsbiotec.dominio.ras.PerfilAssistencialDengue;
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
        name = "triagens_dengue",
        indexes = {
                @Index(name = "idx_triagens_dengue_municipio_criado", columnList = "municipio_id, criado_em"),
                @Index(name = "idx_triagens_dengue_unidade_origem", columnList = "unidade_origem_id"),
                @Index(name = "idx_triagens_dengue_classificacao", columnList = "classificacao")
        })
public class TriagemDengue {

    @Id
    @SequenceGenerator(name = "triagem_dengue_seq_gen", sequenceName = "triagem_dengue_seq")
    @GeneratedValue(generator = "triagem_dengue_seq_gen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipio_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_triagens_dengue_municipio"))
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_origem_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_triagens_dengue_unidade_origem"))
    private UnidadeSaude unidadeOrigem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_triagem_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_triagens_dengue_usuario"))
    private Usuario usuarioTriagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_destino_sugerida_id",
            foreignKey = @ForeignKey(name = "fk_triagens_dengue_unidade_destino"))
    private UnidadeSaude unidadeDestinoSugerida;

    @Column(name = "paciente_identificacao", nullable = false, length = 120)
    private String pacienteIdentificacao;

    @Column(name = "paciente_idade")
    private Integer pacienteIdade;

    @Column(name = "paciente_sexo", length = 20)
    private String pacienteSexo;

    @Column(name = "dias_sintomas")
    private Integer diasSintomas;

    @Column(name = "febre", nullable = false)
    private Boolean febre = false;
    @Column(name = "cefaleia", nullable = false)
    private Boolean cefaleia = false;
    @Column(name = "dor_retroorbital", nullable = false)
    private Boolean dorRetroorbital = false;
    @Column(name = "mialgia", nullable = false)
    private Boolean mialgia = false;
    @Column(name = "artralgia", nullable = false)
    private Boolean artralgia = false;
    @Column(name = "exantema", nullable = false)
    private Boolean exantema = false;
    @Column(name = "nausea_vomito", nullable = false)
    private Boolean nauseaVomito = false;
    @Column(name = "prova_laco_positiva", nullable = false)
    private Boolean provaLacoPositiva = false;

    @Column(name = "dor_abdominal_intensa", nullable = false)
    private Boolean dorAbdominalIntensa = false;
    @Column(name = "vomitos_persistentes", nullable = false)
    private Boolean vomitosPersistentes = false;
    @Column(name = "acumulo_liquidos", nullable = false)
    private Boolean acumuloLiquidos = false;
    @Column(name = "hipotensao_postural_lipotimia", nullable = false)
    private Boolean hipotensaoPosturalLipotimia = false;
    @Column(name = "hepatomegalia", nullable = false)
    private Boolean hepatomegalia = false;
    @Column(name = "sangramento_mucosa", nullable = false)
    private Boolean sangramentoMucosa = false;
    @Column(name = "letargia_irritabilidade", nullable = false)
    private Boolean letargiaIrritabilidade = false;
    @Column(name = "aumento_hematocrito", nullable = false)
    private Boolean aumentoHematocrito = false;

    @Column(name = "choque", nullable = false)
    private Boolean choque = false;
    @Column(name = "sangramento_grave", nullable = false)
    private Boolean sangramentoGrave = false;
    @Column(name = "disfuncao_organica", nullable = false)
    private Boolean disfuncaoOrganica = false;

    @Column(name = "lactente", nullable = false)
    private Boolean lactente = false;
    @Column(name = "gestante", nullable = false)
    private Boolean gestante = false;
    @Column(name = "idoso", nullable = false)
    private Boolean idoso = false;
    @Column(name = "comorbidade", nullable = false)
    private Boolean comorbidade = false;
    @Column(name = "risco_social", nullable = false)
    private Boolean riscoSocial = false;
    @Column(name = "sangramento_pele", nullable = false)
    private Boolean sangramentoPele = false;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "classificacao", nullable = false, length = 20)
    private PerfilAssistencialDengue classificacao;

    @Column(name = "suspeita_consistente", nullable = false)
    private Boolean suspeitaConsistente = false;

    @Column(name = "exige_regulacao", nullable = false)
    private Boolean exigeRegulacao = false;

    @Column(name = "atende_na_origem", nullable = false)
    private Boolean atendeNaOrigem = false;

    @Column(name = "encaminhamento_necessario", nullable = false)
    private Boolean encaminhamentoNecessario = false;

    @Column(name = "recomendacao", nullable = false, length = 1000)
    private String recomendacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private StatusTriagemDengue status;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Long getId() { return id; }
    public Municipio getMunicipio() { return municipio; }
    public void setMunicipio(Municipio municipio) { this.municipio = municipio; }
    public UnidadeSaude getUnidadeOrigem() { return unidadeOrigem; }
    public void setUnidadeOrigem(UnidadeSaude unidadeOrigem) { this.unidadeOrigem = unidadeOrigem; }
    public Usuario getUsuarioTriagem() { return usuarioTriagem; }
    public void setUsuarioTriagem(Usuario usuarioTriagem) { this.usuarioTriagem = usuarioTriagem; }
    public UnidadeSaude getUnidadeDestinoSugerida() { return unidadeDestinoSugerida; }
    public void setUnidadeDestinoSugerida(UnidadeSaude unidadeDestinoSugerida) { this.unidadeDestinoSugerida = unidadeDestinoSugerida; }
    public String getPacienteIdentificacao() { return pacienteIdentificacao; }
    public void setPacienteIdentificacao(String pacienteIdentificacao) { this.pacienteIdentificacao = pacienteIdentificacao; }
    public Integer getPacienteIdade() { return pacienteIdade; }
    public void setPacienteIdade(Integer pacienteIdade) { this.pacienteIdade = pacienteIdade; }
    public String getPacienteSexo() { return pacienteSexo; }
    public void setPacienteSexo(String pacienteSexo) { this.pacienteSexo = pacienteSexo; }
    public Integer getDiasSintomas() { return diasSintomas; }
    public void setDiasSintomas(Integer diasSintomas) { this.diasSintomas = diasSintomas; }
    public Boolean getFebre() { return febre; }
    public void setFebre(Boolean febre) { this.febre = febre; }
    public Boolean getCefaleia() { return cefaleia; }
    public void setCefaleia(Boolean cefaleia) { this.cefaleia = cefaleia; }
    public Boolean getDorRetroorbital() { return dorRetroorbital; }
    public void setDorRetroorbital(Boolean dorRetroorbital) { this.dorRetroorbital = dorRetroorbital; }
    public Boolean getMialgia() { return mialgia; }
    public void setMialgia(Boolean mialgia) { this.mialgia = mialgia; }
    public Boolean getArtralgia() { return artralgia; }
    public void setArtralgia(Boolean artralgia) { this.artralgia = artralgia; }
    public Boolean getExantema() { return exantema; }
    public void setExantema(Boolean exantema) { this.exantema = exantema; }
    public Boolean getNauseaVomito() { return nauseaVomito; }
    public void setNauseaVomito(Boolean nauseaVomito) { this.nauseaVomito = nauseaVomito; }
    public Boolean getProvaLacoPositiva() { return provaLacoPositiva; }
    public void setProvaLacoPositiva(Boolean provaLacoPositiva) { this.provaLacoPositiva = provaLacoPositiva; }
    public Boolean getDorAbdominalIntensa() { return dorAbdominalIntensa; }
    public void setDorAbdominalIntensa(Boolean dorAbdominalIntensa) { this.dorAbdominalIntensa = dorAbdominalIntensa; }
    public Boolean getVomitosPersistentes() { return vomitosPersistentes; }
    public void setVomitosPersistentes(Boolean vomitosPersistentes) { this.vomitosPersistentes = vomitosPersistentes; }
    public Boolean getAcumuloLiquidos() { return acumuloLiquidos; }
    public void setAcumuloLiquidos(Boolean acumuloLiquidos) { this.acumuloLiquidos = acumuloLiquidos; }
    public Boolean getHipotensaoPosturalLipotimia() { return hipotensaoPosturalLipotimia; }
    public void setHipotensaoPosturalLipotimia(Boolean hipotensaoPosturalLipotimia) { this.hipotensaoPosturalLipotimia = hipotensaoPosturalLipotimia; }
    public Boolean getHepatomegalia() { return hepatomegalia; }
    public void setHepatomegalia(Boolean hepatomegalia) { this.hepatomegalia = hepatomegalia; }
    public Boolean getSangramentoMucosa() { return sangramentoMucosa; }
    public void setSangramentoMucosa(Boolean sangramentoMucosa) { this.sangramentoMucosa = sangramentoMucosa; }
    public Boolean getLetargiaIrritabilidade() { return letargiaIrritabilidade; }
    public void setLetargiaIrritabilidade(Boolean letargiaIrritabilidade) { this.letargiaIrritabilidade = letargiaIrritabilidade; }
    public Boolean getAumentoHematocrito() { return aumentoHematocrito; }
    public void setAumentoHematocrito(Boolean aumentoHematocrito) { this.aumentoHematocrito = aumentoHematocrito; }
    public Boolean getChoque() { return choque; }
    public void setChoque(Boolean choque) { this.choque = choque; }
    public Boolean getSangramentoGrave() { return sangramentoGrave; }
    public void setSangramentoGrave(Boolean sangramentoGrave) { this.sangramentoGrave = sangramentoGrave; }
    public Boolean getDisfuncaoOrganica() { return disfuncaoOrganica; }
    public void setDisfuncaoOrganica(Boolean disfuncaoOrganica) { this.disfuncaoOrganica = disfuncaoOrganica; }
    public Boolean getLactente() { return lactente; }
    public void setLactente(Boolean lactente) { this.lactente = lactente; }
    public Boolean getGestante() { return gestante; }
    public void setGestante(Boolean gestante) { this.gestante = gestante; }
    public Boolean getIdoso() { return idoso; }
    public void setIdoso(Boolean idoso) { this.idoso = idoso; }
    public Boolean getComorbidade() { return comorbidade; }
    public void setComorbidade(Boolean comorbidade) { this.comorbidade = comorbidade; }
    public Boolean getRiscoSocial() { return riscoSocial; }
    public void setRiscoSocial(Boolean riscoSocial) { this.riscoSocial = riscoSocial; }
    public Boolean getSangramentoPele() { return sangramentoPele; }
    public void setSangramentoPele(Boolean sangramentoPele) { this.sangramentoPele = sangramentoPele; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public PerfilAssistencialDengue getClassificacao() { return classificacao; }
    public void setClassificacao(PerfilAssistencialDengue classificacao) { this.classificacao = classificacao; }
    public Boolean getSuspeitaConsistente() { return suspeitaConsistente; }
    public void setSuspeitaConsistente(Boolean suspeitaConsistente) { this.suspeitaConsistente = suspeitaConsistente; }
    public Boolean getExigeRegulacao() { return exigeRegulacao; }
    public void setExigeRegulacao(Boolean exigeRegulacao) { this.exigeRegulacao = exigeRegulacao; }
    public Boolean getAtendeNaOrigem() { return atendeNaOrigem; }
    public void setAtendeNaOrigem(Boolean atendeNaOrigem) { this.atendeNaOrigem = atendeNaOrigem; }
    public Boolean getEncaminhamentoNecessario() { return encaminhamentoNecessario; }
    public void setEncaminhamentoNecessario(Boolean encaminhamentoNecessario) { this.encaminhamentoNecessario = encaminhamentoNecessario; }
    public String getRecomendacao() { return recomendacao; }
    public void setRecomendacao(String recomendacao) { this.recomendacao = recomendacao; }
    public StatusTriagemDengue getStatus() { return status; }
    public void setStatus(StatusTriagemDengue status) { this.status = status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
