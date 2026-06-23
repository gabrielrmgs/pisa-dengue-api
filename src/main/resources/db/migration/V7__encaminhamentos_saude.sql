CREATE SEQUENCE IF NOT EXISTS encaminhamento_saude_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS encaminhamentos_saude (
    id BIGINT PRIMARY KEY,
    municipio_id BIGINT NOT NULL,
    triagem_id BIGINT NOT NULL,
    unidade_origem_id BIGINT NOT NULL,
    unidade_destino_id BIGINT NOT NULL,
    usuario_solicitante_id BIGINT NOT NULL,
    usuario_resposta_id BIGINT,
    status VARCHAR(40) NOT NULL,
    exige_regulacao BOOLEAN NOT NULL DEFAULT FALSE,
    justificativa_recusa VARCHAR(1000),
    observacao_resposta VARCHAR(1000),
    solicitado_em TIMESTAMP NOT NULL DEFAULT now(),
    respondido_em TIMESTAMP,
    concluido_em TIMESTAMP,
    cancelado_em TIMESTAMP,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_encaminhamentos_municipio
        FOREIGN KEY (municipio_id) REFERENCES municipios(id),
    CONSTRAINT fk_encaminhamentos_triagem
        FOREIGN KEY (triagem_id) REFERENCES triagens_dengue(id),
    CONSTRAINT fk_encaminhamentos_unidade_origem
        FOREIGN KEY (unidade_origem_id) REFERENCES unidades_saude(id),
    CONSTRAINT fk_encaminhamentos_unidade_destino
        FOREIGN KEY (unidade_destino_id) REFERENCES unidades_saude(id),
    CONSTRAINT fk_encaminhamentos_usuario_solicitante
        FOREIGN KEY (usuario_solicitante_id) REFERENCES usuarios(id),
    CONSTRAINT fk_encaminhamentos_usuario_resposta
        FOREIGN KEY (usuario_resposta_id) REFERENCES usuarios(id)
);

CREATE INDEX IF NOT EXISTS idx_encaminhamentos_municipio_status
    ON encaminhamentos_saude(municipio_id, status);

CREATE INDEX IF NOT EXISTS idx_encaminhamentos_origem
    ON encaminhamentos_saude(unidade_origem_id, solicitado_em DESC);

CREATE INDEX IF NOT EXISTS idx_encaminhamentos_destino
    ON encaminhamentos_saude(unidade_destino_id, solicitado_em DESC);

CREATE UNIQUE INDEX IF NOT EXISTS uq_encaminhamentos_triagem_ativo
    ON encaminhamentos_saude(triagem_id)
    WHERE status IN ('PENDENTE_ACEITE', 'ACEITO');
