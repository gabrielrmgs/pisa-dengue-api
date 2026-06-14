CREATE EXTENSION IF NOT EXISTS postgis;

CREATE SEQUENCE IF NOT EXISTS seq_estado START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS municipio_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS bairro_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS seq_usuario START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS estados (
    id BIGINT PRIMARY KEY,
    codigo_uf VARCHAR(2),
    nome VARCHAR(255),
    sigla VARCHAR(2)
);

CREATE TABLE IF NOT EXISTS municipios (
    id BIGINT PRIMARY KEY,
    nome VARCHAR(255),
    estado_id BIGINT REFERENCES estados(id),
    codigo_ibge VARCHAR(7),
    populacao INTEGER NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS bairros (
    id BIGINT PRIMARY KEY,
    nm_bairro VARCHAR(255),
    cd_bairro VARCHAR(32),
    populacao INTEGER,
    sexo_masculino INTEGER,
    sexo_feminino INTEGER,
    masculino_0_a_4_anos INTEGER,
    masculino_5_a_9_anos INTEGER,
    masculino_10_a_14_anos INTEGER,
    masculino_15_a_19_anos INTEGER,
    masculino_20_a_24_anos INTEGER,
    masculino_25_a_29_anos INTEGER,
    masculino_30_a_39_anos INTEGER,
    masculino_40_a_49_anos INTEGER,
    masculino_50_a_59_anos INTEGER,
    masculino_60_a_69_anos INTEGER,
    masculino_70_anos_ou_mais INTEGER,
    feminino_0_a_4_anos INTEGER,
    feminino_5_a_9_anos INTEGER,
    feminino_10_a_14_anos INTEGER,
    feminino_15_a_19_anos INTEGER,
    feminino_20_a_24_anos INTEGER,
    feminino_25_a_29_anos INTEGER,
    feminino_30_a_39_anos INTEGER,
    feminino_40_a_49_anos INTEGER,
    feminino_50_a_59_anos INTEGER,
    feminino_60_a_69_anos INTEGER,
    feminino_70_anos_ou_mais INTEGER,
    moradores_0_a_4_anos INTEGER,
    moradores_5_a_9_anos INTEGER,
    moradores_10_a_14_anos INTEGER,
    moradores_15_a_19_anos INTEGER,
    moradores_20_a_24_anos INTEGER,
    moradores_25_a_29_anos INTEGER,
    moradores_30_a_39_anos INTEGER,
    moradores_40_a_49_anos INTEGER,
    moradores_50_a_59_anos INTEGER,
    moradores_60_a_69_anos INTEGER,
    moradores_70_anos_ou_mais INTEGER,
    municipio_id BIGINT REFERENCES municipios(id),
    geometria geometry(MultiPolygon, 4326)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT PRIMARY KEY,
    municipio_id BIGINT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    refresh_token_hash VARCHAR(255),
    ultimo_login TIMESTAMP,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT uq_usuarios_email UNIQUE (email),
    CONSTRAINT fk_usuarios_municipio FOREIGN KEY (municipio_id) REFERENCES municipios(id)
);

CREATE INDEX IF NOT EXISTS idx_estados_codigo_uf ON estados(codigo_uf);
CREATE INDEX IF NOT EXISTS idx_municipios_estado_id ON municipios(estado_id);
CREATE INDEX IF NOT EXISTS idx_municipios_codigo_ibge ON municipios(codigo_ibge);
CREATE INDEX IF NOT EXISTS idx_municipios_ativo ON municipios(ativo);
CREATE INDEX IF NOT EXISTS idx_bairros_municipio_id ON bairros(municipio_id);
CREATE INDEX IF NOT EXISTS idx_bairros_cd_bairro ON bairros(cd_bairro);
CREATE INDEX IF NOT EXISTS idx_bairros_geometria ON bairros USING GIST (geometria);
CREATE INDEX IF NOT EXISTS idx_usuarios_municipio_id ON usuarios(municipio_id);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_ativo ON usuarios(ativo);
