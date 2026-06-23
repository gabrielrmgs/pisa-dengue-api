package br.com.gemsbiotec.triagem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarTriagemDengueRequest(
        @NotNull(message = "A unidade de origem e obrigatoria")
        Long unidadeOrigemId,

        @NotBlank(message = "A identificacao do paciente e obrigatoria")
        @Size(max = 120, message = "A identificacao deve ter no maximo 120 caracteres")
        String pacienteIdentificacao,

        @Min(value = 0, message = "A idade nao pode ser negativa")
        @Max(value = 130, message = "A idade informada parece invalida")
        Integer pacienteIdade,

        @Size(max = 20, message = "O sexo deve ter no maximo 20 caracteres")
        String pacienteSexo,

        @Min(value = 0, message = "Os dias de sintomas nao podem ser negativos")
        @Max(value = 30, message = "Os dias de sintomas devem ser revisados")
        Integer diasSintomas,

        Boolean febre,
        Boolean cefaleia,
        Boolean dorRetroorbital,
        Boolean mialgia,
        Boolean artralgia,
        Boolean exantema,
        Boolean nauseaVomito,
        Boolean provaLacoPositiva,

        Boolean dorAbdominalIntensa,
        Boolean vomitosPersistentes,
        Boolean acumuloLiquidos,
        Boolean hipotensaoPosturalLipotimia,
        Boolean hepatomegalia,
        Boolean sangramentoMucosa,
        Boolean letargiaIrritabilidade,
        Boolean aumentoHematocrito,

        Boolean choque,
        Boolean sangramentoGrave,
        Boolean disfuncaoOrganica,

        Boolean lactente,
        Boolean gestante,
        Boolean idoso,
        Boolean comorbidade,
        Boolean riscoSocial,
        Boolean sangramentoPele,

        @Size(max = 1000, message = "As observacoes devem ter no maximo 1000 caracteres")
        String observacoes) {
}
