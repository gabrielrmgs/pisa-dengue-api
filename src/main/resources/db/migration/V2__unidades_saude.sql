CREATE SEQUENCE IF NOT EXISTS unidade_saude_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS unidades_saude (
    id BIGINT PRIMARY KEY,
    municipio_id BIGINT NOT NULL,
    bairro_id BIGINT,
    nome VARCHAR(150) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    cnes VARCHAR(20),
    endereco VARCHAR(255),
    telefone VARCHAR(30),
    email VARCHAR(255),
    responsavel VARCHAR(150),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    localizacao geometry(Point, 4326),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_unidades_saude_municipio FOREIGN KEY (municipio_id) REFERENCES municipios(id),
    CONSTRAINT fk_unidades_saude_bairro FOREIGN KEY (bairro_id) REFERENCES bairros(id),
    CONSTRAINT uq_unidades_saude_municipio_cnes UNIQUE (municipio_id, cnes)
);

CREATE INDEX IF NOT EXISTS idx_unidades_saude_municipio_id ON unidades_saude(municipio_id);
CREATE INDEX IF NOT EXISTS idx_unidades_saude_bairro_id ON unidades_saude(bairro_id);
CREATE INDEX IF NOT EXISTS idx_unidades_saude_tipo ON unidades_saude(tipo);
CREATE INDEX IF NOT EXISTS idx_unidades_saude_ativo ON unidades_saude(ativo);
CREATE INDEX IF NOT EXISTS idx_unidades_saude_localizacao ON unidades_saude USING GIST (localizacao);
