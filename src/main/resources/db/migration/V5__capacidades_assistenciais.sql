CREATE SEQUENCE IF NOT EXISTS capacidade_assistencial_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS unidade_capacidade_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS capacidades_assistenciais (
    id BIGINT PRIMARY KEY,
    codigo VARCHAR(60) NOT NULL,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(500),
    categoria VARCHAR(30) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_capacidades_assistenciais_codigo UNIQUE (codigo)
);

CREATE TABLE IF NOT EXISTS unidades_capacidades (
    id BIGINT PRIMARY KEY,
    unidade_saude_id BIGINT NOT NULL,
    capacidade_id BIGINT NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    horario_atendimento VARCHAR(150),
    restricoes VARCHAR(500),
    observacoes VARCHAR(500),
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_unidades_capacidades_unidade
        FOREIGN KEY (unidade_saude_id) REFERENCES unidades_saude(id) ON DELETE CASCADE,
    CONSTRAINT fk_unidades_capacidades_capacidade
        FOREIGN KEY (capacidade_id) REFERENCES capacidades_assistenciais(id),
    CONSTRAINT uq_unidades_capacidades_unidade_capacidade
        UNIQUE (unidade_saude_id, capacidade_id)
);

CREATE INDEX IF NOT EXISTS idx_unidades_capacidades_unidade
    ON unidades_capacidades(unidade_saude_id);

CREATE INDEX IF NOT EXISTS idx_unidades_capacidades_capacidade
    ON unidades_capacidades(capacidade_id);

INSERT INTO capacidades_assistenciais (id, codigo, nome, descricao, categoria)
VALUES
    (nextval('capacidade_assistencial_seq'), 'AVALIACAO_DENGUE', 'Avaliação clínica de dengue', 'Acolhimento e avaliação clínica de casos suspeitos de dengue.', 'ATENDIMENTO'),
    (nextval('capacidade_assistencial_seq'), 'HEMOGRAMA', 'Hemograma e hematócrito', 'Coleta ou execução de hemograma com avaliação de hematócrito.', 'DIAGNOSTICO'),
    (nextval('capacidade_assistencial_seq'), 'HIDRATACAO_ORAL', 'Hidratação oral', 'Estrutura para orientação e administração assistida de hidratação oral.', 'TRATAMENTO'),
    (nextval('capacidade_assistencial_seq'), 'HIDRATACAO_VENOSA', 'Hidratação venosa', 'Estrutura e equipe para administração e acompanhamento de hidratação intravenosa.', 'TRATAMENTO'),
    (nextval('capacidade_assistencial_seq'), 'OBSERVACAO', 'Leito de observação', 'Capacidade de manter paciente em observação e realizar reavaliações.', 'OBSERVACAO'),
    (nextval('capacidade_assistencial_seq'), 'ATENDIMENTO_24H', 'Atendimento 24 horas', 'Funcionamento assistencial contínuo durante vinte e quatro horas.', 'ATENDIMENTO'),
    (nextval('capacidade_assistencial_seq'), 'INTERNACAO', 'Internação', 'Disponibilidade estrutural para internação hospitalar.', 'INTERNACAO'),
    (nextval('capacidade_assistencial_seq'), 'EMERGENCIA', 'Atendimento de emergência', 'Estrutura para estabilização e atendimento imediato de pacientes graves.', 'SUPORTE_CRITICO'),
    (nextval('capacidade_assistencial_seq'), 'UTI', 'Unidade de terapia intensiva', 'Suporte intensivo para pacientes críticos.', 'SUPORTE_CRITICO'),
    (nextval('capacidade_assistencial_seq'), 'PEDIATRIA', 'Atendimento pediátrico', 'Equipe e estrutura para atendimento de crianças.', 'ESPECIALIDADE'),
    (nextval('capacidade_assistencial_seq'), 'OBSTETRICIA', 'Atendimento obstétrico', 'Equipe e estrutura para avaliação e atendimento de gestantes.', 'ESPECIALIDADE'),
    (nextval('capacidade_assistencial_seq'), 'SUPORTE_TRANSFUSIONAL', 'Suporte transfusional', 'Acesso a hemocomponentes e suporte para transfusão.', 'SUPORTE_CRITICO')
ON CONFLICT (codigo) DO NOTHING;
