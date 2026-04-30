package br.com.gemsbiotec.dominio.usuario;


import br.com.gemsbiotec.dominio.geo.Municipio;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Usuário da plataforma — sempre vinculado a um único município (tenant).
 *
 * Autenticação via JWT manual (SmallRye JWT).
 * O campo municipio_id do token é lido pelo TenantContext e injetado
 * como parâmetro de sessão do PostgreSQL para o RLS:
 *
 *   SET LOCAL app.tenant_id = '<municipio_id>';
 *
 * Senhas armazenadas como hash Argon2id (via quarkus-security-crypto).
 * Nunca armazenar senha em texto claro.
 *
 * email é único globalmente — não por município. Um gestor não pode
 * ter o mesmo email em dois municípios (use emails distintos).
 */
@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_usuarios_email", columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_usuarios_municipio_id", columnList = "municipio_id"),
        @Index(name = "idx_usuarios_email",        columnList = "email"),
        @Index(name = "idx_usuarios_ativo",        columnList = "ativo")
    }
)
public class Usuario {

    @Id
    @SequenceGenerator(sequenceName = "seq_usuario", name = "usuario_seq_gen")
    @GeneratedValue(generator = "usuario_seq_gen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    public Long id;

    /**
     * Tenant ao qual este usuário pertence.
     * Este campo é a base do isolamento multi-tenant.
     * Não é possível alterar o município de um usuário após a criação.
     */
    //@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id", nullable = true, updatable = true,
                foreignKey = @ForeignKey(name = "fk_usuarios_municipio"))
    public Municipio municipio;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nome", nullable = false, length = 150)
    public String nome;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(name = "email", nullable = false, length = 255)
    public String email;

    /**
     * Hash Argon2id da senha.
     * Gerado via io.quarkus.elytron.security.common.BcryptUtil
     * ou BcryptPassword.encode() do quarkus-security-crypto.
     *
     * Nunca exposto em DTOs de resposta — @JsonIgnore em todos os mappers.
     */
    @NotBlank
    @Column(name = "senha_hash", nullable = false)
    public String senhaHash;

    /**
     * Papel do usuário — armazenado como String para legibilidade no banco.
     * @see Role
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    public Role role = Role.AGENTE;

    @Column(name = "ativo", nullable = false)
    public Boolean ativo = true;

    /**
     * Token de refresh armazenado como hash.
     * Nullable — nulo quando não há sessão ativa.
     * Invalidado no logout ou ao emitir novo refresh token.
     */
    @Column(name = "refresh_token_hash")
    public String refreshTokenHash;

    @Column(name = "ultimo_login")
    public LocalDateTime ultimoLogin;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    public LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    public LocalDateTime atualizadoEm;

    // ── construtores ────────────────────────────────────────────────────────

    public Usuario() {}

    public Usuario(Municipio municipio, String nome, String email,
                   String senhaHash, Role role) {
        this.municipio  = municipio;
        this.nome       = nome;
        this.email      = email;
        this.senhaHash  = senhaHash;
        this.role       = role;
    }

    // ── helpers de domínio ──────────────────────────────────────────────────

    public boolean isAdmin()   { return Role.ADMIN  == this.role; }
    public boolean isGestor()  { return Role.GESTOR == this.role; }
    public boolean isAgente()  { return Role.AGENTE == this.role; }

    public boolean temPermissao(Role minimo) {
        return this.role.temPermissao(minimo);
    }

    public Long getMunicipioId() {
        return municipio != null ? municipio.getId() : null;
    }

    @Override
    public String toString() {
        return "Usuario{email='" + email + "', role=" + role + "}";
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Municipio getMunicipio() {
        return this.municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return this.senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean isAtivo() {
        return this.ativo;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getRefreshTokenHash() {
        return this.refreshTokenHash;
    }

    public void setRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public LocalDateTime getUltimoLogin() {
        return this.ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public LocalDateTime getCriadoEm() {
        return this.criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return this.atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

}
