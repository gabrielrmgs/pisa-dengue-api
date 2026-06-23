CREATE SEQUENCE IF NOT EXISTS triagem_dengue_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS triagens_dengue (
    id BIGINT PRIMARY KEY,
    municipio_id BIGINT NOT NULL,
    unidade_origem_id BIGINT NOT NULL,
    usuario_triagem_id BIGINT NOT NULL,
    unidade_destino_sugerida_id BIGINT,
    paciente_identificacao VARCHAR(120) NOT NULL,
    paciente_idade INTEGER,
    paciente_sexo VARCHAR(20),
    dias_sintomas INTEGER,
    febre BOOLEAN NOT NULL DEFAULT FALSE,
    cefaleia BOOLEAN NOT NULL DEFAULT FALSE,
    dor_retroorbital BOOLEAN NOT NULL DEFAULT FALSE,
    mialgia BOOLEAN NOT NULL DEFAULT FALSE,
    artralgia BOOLEAN NOT NULL DEFAULT FALSE,
    exantema BOOLEAN NOT NULL DEFAULT FALSE,
    nausea_vomito BOOLEAN NOT NULL DEFAULT FALSE,
    prova_laco_positiva BOOLEAN NOT NULL DEFAULT FALSE,
    dor_abdominal_intensa BOOLEAN NOT NULL DEFAULT FALSE,
    vomitos_persistentes BOOLEAN NOT NULL DEFAULT FALSE,
    acumulo_liquidos BOOLEAN NOT NULL DEFAULT FALSE,
    hipotensao_postural_lipotimia BOOLEAN NOT NULL DEFAULT FALSE,
    hepatomegalia BOOLEAN NOT NULL DEFAULT FALSE,
    sangramento_mucosa BOOLEAN NOT NULL DEFAULT FALSE,
    letargia_irritabilidade BOOLEAN NOT NULL DEFAULT FALSE,
    aumento_hematocrito BOOLEAN NOT NULL DEFAULT FALSE,
    choque BOOLEAN NOT NULL DEFAULT FALSE,
    sangramento_grave BOOLEAN NOT NULL DEFAULT FALSE,
    disfuncao_organica BOOLEAN NOT NULL DEFAULT FALSE,
    lactente BOOLEAN NOT NULL DEFAULT FALSE,
    gestante BOOLEAN NOT NULL DEFAULT FALSE,
    idoso BOOLEAN NOT NULL DEFAULT FALSE,
    comorbidade BOOLEAN NOT NULL DEFAULT FALSE,
    risco_social BOOLEAN NOT NULL DEFAULT FALSE,
    sangramento_pele BOOLEAN NOT NULL DEFAULT FALSE,
    observacoes VARCHAR(1000),
    classificacao VARCHAR(20) NOT NULL,
    suspeita_consistente BOOLEAN NOT NULL DEFAULT FALSE,
    exige_regulacao BOOLEAN NOT NULL DEFAULT FALSE,
    atende_na_origem BOOLEAN NOT NULL DEFAULT FALSE,
    encaminhamento_necessario BOOLEAN NOT NULL DEFAULT FALSE,
    recomendacao VARCHAR(1000) NOT NULL,
    status VARCHAR(40) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_triagens_dengue_municipio
        FOREIGN KEY (municipio_id) REFERENCES municipios(id),
    CONSTRAINT fk_triagens_dengue_unidade_origem
        FOREIGN KEY (unidade_origem_id) REFERENCES unidades_saude(id),
    CONSTRAINT fk_triagens_dengue_usuario
        FOREIGN KEY (usuario_triagem_id) REFERENCES usuarios(id),
    CONSTRAINT fk_triagens_dengue_unidade_destino
        FOREIGN KEY (unidade_destino_sugerida_id) REFERENCES unidades_saude(id)
);

CREATE INDEX IF NOT EXISTS idx_triagens_dengue_municipio_criado
    ON triagens_dengue(municipio_id, criado_em DESC);

CREATE INDEX IF NOT EXISTS idx_triagens_dengue_unidade_origem
    ON triagens_dengue(unidade_origem_id);

CREATE INDEX IF NOT EXISTS idx_triagens_dengue_classificacao
    ON triagens_dengue(classificacao);
