package br.com.gemsbiotec.dominio.usuario;

/**
 * Papéis de acesso dos usuários na plataforma.
 *
 * Armazenado como String no banco (columnDefinition = "VARCHAR(20)")
 * para legibilidade nas queries e auditoria — nunca como ordinal.
 *
 * Hierarquia de permissões (maior → menor):
 *   ADMIN > GESTOR > AGENTE > VIEWER
 *
 * ADMIN    — acesso total ao município, importação de dados, gestão de usuários
 * GESTOR   — visualização e geração de relatórios, sem gestão de usuários
 * AGENTE   — registro de casos e triagens, sem acesso a configurações
 * VIEWER   — somente leitura do dashboard (futuro: link público)
 */
public enum Role {

    ADMIN,
    GESTOR,
    AGENTE,
    VIEWER;

    /** Retorna true se este role tem pelo menos o nível do role informado. */
    public boolean temPermissao(Role minimo) {
        return this.ordinal() <= minimo.ordinal();
    }
}
