package br.com.gemsbiotec.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Payload de entrada do endpoint POST /auth/login.
 *
 * Record imutável — sem setters, sem campos opcionais.
 * A validação do @NotBlank e @Email é feita pelo Hibernate Validator
 * antes de chegar no AuthResource (@Valid no parâmetro).
 */
public record LoginRequest(

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    String senha

) {}
