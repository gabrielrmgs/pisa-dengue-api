CREATE SEQUENCE IF NOT EXISTS usuario_unidade_saude_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS usuarios_unidades_saude (
    id BIGINT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    unidade_saude_id BIGINT NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_usuarios_unidades_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuarios_unidades_unidade
        FOREIGN KEY (unidade_saude_id) REFERENCES unidades_saude(id) ON DELETE CASCADE,
    CONSTRAINT uq_usuarios_unidades_usuario_unidade
        UNIQUE (usuario_id, unidade_saude_id)
);

CREATE INDEX IF NOT EXISTS idx_usuarios_unidades_usuario
    ON usuarios_unidades_saude(usuario_id);

CREATE INDEX IF NOT EXISTS idx_usuarios_unidades_unidade
    ON usuarios_unidades_saude(unidade_saude_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_usuarios_unidades_principal
    ON usuarios_unidades_saude(usuario_id)
    WHERE principal = TRUE;
