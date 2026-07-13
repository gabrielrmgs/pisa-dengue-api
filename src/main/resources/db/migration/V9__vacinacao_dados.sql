CREATE SEQUENCE IF NOT EXISTS vacinacao_dados_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS vacinacao_dados (
    id BIGINT PRIMARY KEY,
    unidade_saude_id BIGINT NOT NULL,
    data_referencia DATE NOT NULL,
    doses_10_14 INTEGER NOT NULL DEFAULT 0,
    doses_18_59 INTEGER NOT NULL DEFAULT 0,
    doses_total INTEGER NOT NULL DEFAULT 0,
    origem VARCHAR(20) NOT NULL DEFAULT 'IMPORTACAO',
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_vacinacao_dados_unidade
        FOREIGN KEY (unidade_saude_id) REFERENCES unidades_saude(id),
    CONSTRAINT uq_vacinacao_dados_unidade_data
        UNIQUE (unidade_saude_id, data_referencia)
);

CREATE INDEX IF NOT EXISTS idx_vacinacao_dados_unidade ON vacinacao_dados(unidade_saude_id);
CREATE INDEX IF NOT EXISTS idx_vacinacao_dados_data ON vacinacao_dados(data_referencia);

CREATE SEQUENCE IF NOT EXISTS vacinacao_registro_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS vacinacao_registros (
    id BIGINT PRIMARY KEY,
    unidade_saude_id BIGINT NOT NULL,
    usuario_id BIGINT,
    faixa_etaria VARCHAR(20) NOT NULL,
    quantidade INTEGER NOT NULL,
    observacoes VARCHAR(500),
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_vacinacao_registros_unidade
        FOREIGN KEY (unidade_saude_id) REFERENCES unidades_saude(id),
    CONSTRAINT fk_vacinacao_registros_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE INDEX IF NOT EXISTS idx_vacinacao_registros_unidade ON vacinacao_registros(unidade_saude_id);
