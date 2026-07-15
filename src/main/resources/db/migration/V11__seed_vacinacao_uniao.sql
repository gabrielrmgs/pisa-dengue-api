DO $$
DECLARE
    v_municipio_id BIGINT;
    v_centro_lat DOUBLE PRECISION;
    v_centro_lon DOUBLE PRECISION;
BEGIN
    -- Dados extraidos dos relatorios SI-PNI/RNDS de Uniao (PI), codigo IBGE 2211100.
    -- A V10 apontava por engano para o codigo IBGE de Bom Jesus (2201903), entao os
    -- dados nunca eram inseridos para o municipio real de origem dos relatorios.
    SELECT id INTO v_municipio_id FROM municipios WHERE codigo_ibge = '2211100';
    IF v_municipio_id IS NULL THEN
        RETURN;
    END IF;

    SELECT ST_Y(ST_Centroid(ST_Union(geometria))), ST_X(ST_Centroid(ST_Union(geometria)))
      INTO v_centro_lat, v_centro_lon
      FROM bairros
     WHERE municipio_id = v_municipio_id;

    IF v_centro_lat IS NULL THEN
        RETURN;
    END IF;

    -- Coordenadas por unidade ainda nao foram informadas; usa-se o centroide do
    -- municipio como posicao provisoria ate que as coordenadas reais de cada
    -- unidade sejam cadastradas (necessarias para o calculo de doses por bairro).
    INSERT INTO unidades_saude (
        id, municipio_id, nome, tipo, icone_pin, latitude, longitude, localizacao, ativo, criado_em, atualizado_em
    )
    SELECT nextval('unidade_saude_seq'), v_municipio_id, novas.nome, 'UBS', 'UBS',
           v_centro_lat, v_centro_lon,
           ST_SetSRID(ST_MakePoint(v_centro_lon, v_centro_lat), 4326), TRUE, now(), now()
    FROM (VALUES
        ('Unidade de Saude Ana Nery'),
        ('Unidade de Saude Anfrisio Lobao'),
        ('Unidade de Saude Aniceto Sousa'),
        ('Unidade de Saude Antonio Terto Neto'),
        ('Unidade de Saude Eduvigens Goncalves Costa'),
        ('Unidade de Saude Clarice Francisca'),
        ('Unidade de Saude Elmira Irene Machado'),
        ('Unidade de Saude Isabel Ribeiro do Nascimento'),
        ('Unidade de Saude Jose Henrique Sampaio'),
        ('Unidade de Saude Laurenca Abreu da Silva'),
        ('Unidade de Saude Maria Bona Medeiros'),
        ('Unidade de Saude Nazi Barros'),
        ('Unidade de Saude Maria Costa'),
        ('Unidade de Saude Raimundo Joao Vasconcelos'),
        ('Unidade de Saude da Gamileira'),
        ('Unidade de Saude Olavo Mendes de Carvalho'),
        ('Us Luiz Pinheiro do Rego')
    ) AS novas(nome)
    WHERE NOT EXISTS (
        SELECT 1 FROM unidades_saude u
         WHERE u.municipio_id = v_municipio_id AND LOWER(u.nome) = LOWER(novas.nome)
    );

    INSERT INTO vacinacao_dados (
        id, unidade_saude_id, data_referencia, doses_10_14, doses_18_59, doses_total, origem, criado_em, atualizado_em
    )
    SELECT nextval('vacinacao_dados_seq'), u.id, DATE '2026-05-15',
           v.doses_10_14, v.doses_18_59, v.doses_10_14 + v.doses_18_59, 'SEED', now(), now()
    FROM unidades_saude u
    JOIN (VALUES
        ('Unidade de Saude Ana Nery', 55, 7),
        ('Unidade de Saude Anfrisio Lobao', 73, 4),
        ('Unidade de Saude Aniceto Sousa', 114, 7),
        ('Unidade de Saude Antonio Terto Neto', 46, 6),
        ('Unidade de Saude Eduvigens Goncalves Costa', 48, 6),
        ('Unidade de Saude Clarice Francisca', 47, 7),
        ('Unidade de Saude Elmira Irene Machado', 50, 1),
        ('Unidade de Saude Isabel Ribeiro do Nascimento', 43, 4),
        ('Unidade de Saude Jose Henrique Sampaio', 39, 0),
        ('Unidade de Saude Laurenca Abreu da Silva', 107, 7),
        ('Unidade de Saude Maria Bona Medeiros', 63, 3),
        ('Unidade de Saude Nazi Barros', 70, 10),
        ('Unidade de Saude Maria Costa', 51, 3),
        ('Unidade de Saude Raimundo Joao Vasconcelos', 66, 5),
        ('Unidade de Saude da Gamileira', 19, 7),
        ('Unidade de Saude Olavo Mendes de Carvalho', 206, 20),
        ('Us Luiz Pinheiro do Rego', 29, 7)
    ) AS v(nome, doses_10_14, doses_18_59) ON LOWER(u.nome) = LOWER(v.nome)
    WHERE u.municipio_id = v_municipio_id
      AND NOT EXISTS (
        SELECT 1 FROM vacinacao_dados vd
         WHERE vd.unidade_saude_id = u.id AND vd.data_referencia = DATE '2026-05-15'
      );

    INSERT INTO vacinacao_dados (
        id, unidade_saude_id, data_referencia, doses_10_14, doses_18_59, doses_total, origem, criado_em, atualizado_em
    )
    SELECT nextval('vacinacao_dados_seq'), u.id, DATE '2026-07-09',
           v.doses_10_14, v.doses_18_59, v.doses_10_14 + v.doses_18_59, 'SEED', now(), now()
    FROM unidades_saude u
    JOIN (VALUES
        ('Unidade de Saude Ana Nery', 55, 7),
        ('Unidade de Saude Anfrisio Lobao', 81, 5),
        ('Unidade de Saude Aniceto Sousa', 116, 7),
        ('Unidade de Saude Antonio Terto Neto', 46, 6),
        ('Unidade de Saude Eduvigens Goncalves Costa', 49, 6),
        ('Unidade de Saude Clarice Francisca', 49, 7),
        ('Unidade de Saude Elmira Irene Machado', 50, 1),
        ('Unidade de Saude Isabel Ribeiro do Nascimento', 43, 4),
        ('Unidade de Saude Jose Henrique Sampaio', 41, 1),
        ('Unidade de Saude Laurenca Abreu da Silva', 161, 10),
        ('Unidade de Saude Maria Bona Medeiros', 79, 3),
        ('Unidade de Saude Nazi Barros', 97, 20),
        ('Unidade de Saude Maria Costa', 52, 4),
        ('Unidade de Saude Raimundo Joao Vasconcelos', 66, 5),
        ('Unidade de Saude da Gamileira', 21, 7),
        ('Unidade de Saude Olavo Mendes de Carvalho', 219, 20),
        ('Us Luiz Pinheiro do Rego', 30, 8)
    ) AS v(nome, doses_10_14, doses_18_59) ON LOWER(u.nome) = LOWER(v.nome)
    WHERE u.municipio_id = v_municipio_id
      AND NOT EXISTS (
        SELECT 1 FROM vacinacao_dados vd
         WHERE vd.unidade_saude_id = u.id AND vd.data_referencia = DATE '2026-07-09'
      );

    UPDATE unidades_saude u
       SET bairro_id = (
           SELECT b.id FROM bairros b
            WHERE b.municipio_id = v_municipio_id
              AND ST_Contains(b.geometria, u.localizacao)
            LIMIT 1
       )
     WHERE u.municipio_id = v_municipio_id
       AND u.bairro_id IS NULL
       AND u.localizacao IS NOT NULL;
END $$;
