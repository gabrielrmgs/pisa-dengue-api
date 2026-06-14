INSERT INTO municipios (
    id,
    nome,
    estado_id,
    codigo_ibge,
    populacao,
    ativo,
    latitude,
    longitude
) VALUES (
    1,
    'Bootstrap',
    NULL,
    NULL,
    0,
    TRUE,
    NULL,
    NULL
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO usuarios (
    id,
    municipio_id,
    nome,
    email,
    senha_hash,
    role,
    ativo,
    refresh_token_hash,
    ultimo_login,
    criado_em,
    atualizado_em
) VALUES (
    1,
    1,
    'Gabriel',
    'admin@gabriel.com',
    '$2a$10$ik9ZMVHR/iHpA7KWPQc1aOFZG/yZNwr6nIqPE6EB9SVLBRJFhJ6YG',
    'ADMIN',
    TRUE,
    NULL,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO UPDATE SET
    municipio_id = EXCLUDED.municipio_id,
    nome = EXCLUDED.nome,
    senha_hash = EXCLUDED.senha_hash,
    role = EXCLUDED.role,
    ativo = EXCLUDED.ativo,
    atualizado_em = CURRENT_TIMESTAMP;

SELECT setval('municipio_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM municipios), 1), TRUE);
SELECT setval('seq_usuario', GREATEST((SELECT COALESCE(MAX(id), 0) FROM usuarios), 1), TRUE);
