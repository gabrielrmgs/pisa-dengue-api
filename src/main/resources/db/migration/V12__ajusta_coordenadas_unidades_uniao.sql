DO $$
DECLARE
    v_municipio_id BIGINT;
    v_vnc_lat DOUBLE PRECISION;
    v_vnc_lon DOUBLE PRECISION;
BEGIN
    SELECT id INTO v_municipio_id FROM municipios WHERE codigo_ibge = '2211100';
    IF v_municipio_id IS NULL THEN
        RETURN;
    END IF;

    SELECT ST_Y(ST_Centroid(geometria)), ST_X(ST_Centroid(geometria))
      INTO v_vnc_lat, v_vnc_lon
      FROM bairros
     WHERE municipio_id = v_municipio_id AND nm_bairro ILIKE 'Vila Nova Conquista'
     LIMIT 1;

    -- Corrige a posicao das unidades de Uniao inseridas pela V11 (que usou o
    -- centroide do municipio como placeholder para todas). Coordenadas obtidas
    -- via geocodificacao OpenStreetMap/Nominatim da localidade/bairro que a
    -- Prefeitura de Uniao atribui a cada unidade, conferido como dentro do
    -- municipio de Uniao-PI. As unidades cuja localidade nao foi encontrada com
    -- confianca (Bandeira, Mussum, Buriti Alegre, Cajueiro, Santa Rita, Baixa
    -- Grande) permanecem no centroide ate as coordenadas reais serem levantadas
    -- em campo.
    UPDATE unidades_saude u
       SET latitude = novas.lat,
           longitude = novas.lon,
           localizacao = ST_SetSRID(ST_MakePoint(novas.lon, novas.lat), 4326),
           bairro_id = (
               SELECT b.id FROM bairros b
                WHERE b.municipio_id = v_municipio_id
                  AND ST_Contains(b.geometria, ST_SetSRID(ST_MakePoint(novas.lon, novas.lat), 4326))
                LIMIT 1
           )
    FROM (VALUES
        ('Unidade de Saude Ana Nery', -4.7423781::double precision, -42.9109835::double precision),         -- David Caldas
        ('Unidade de Saude Anfrisio Lobao', -4.5849200::double precision, -42.8697595::double precision),   -- Beira Rio
        ('Unidade de Saude Aniceto Sousa', -4.6621693::double precision, -42.8323031::double precision),    -- Divinopolis
        ('Unidade de Saude Antonio Terto Neto', -4.5884296::double precision, -42.8674020::double precision), -- Sao Joao
        ('Unidade de Saude Eduvigens Goncalves Costa', -4.3976752::double precision, -42.8877027::double precision), -- Novo Nilo
        ('Unidade de Saude Isabel Ribeiro do Nascimento', -4.5917912::double precision, -42.8415253::double precision), -- Santa Helena / Dos Cocos
        ('Unidade de Saude Jose Henrique Sampaio', -4.5836992::double precision, -42.8522910::double precision), -- Rua Jose Moita, Sao Sebastiao
        ('Unidade de Saude Nazi Barros', -4.5960016::double precision, -42.8526453::double precision),      -- Cruzeiro
        ('Unidade de Saude da Gamileira', -4.7790540::double precision, -42.8876229::double precision),     -- Gameleira
        ('Us Luiz Pinheiro do Rego', -4.5933111::double precision, -42.8655217::double precision)           -- Nossa Senhora das Gracas
    ) AS novas(nome, lat, lon)
    WHERE u.municipio_id = v_municipio_id
      AND LOWER(u.nome) = LOWER(novas.nome);

    -- "Olavo Mendes de Carvalho" fica no bairro oficial Vila Nova Conquista;
    -- usa-se o centroide do proprio poligono do bairro em vez de geocodificacao.
    IF v_vnc_lat IS NOT NULL THEN
        UPDATE unidades_saude u
           SET latitude = v_vnc_lat,
               longitude = v_vnc_lon,
               localizacao = ST_SetSRID(ST_MakePoint(v_vnc_lon, v_vnc_lat), 4326),
               bairro_id = (
                   SELECT b.id FROM bairros b
                    WHERE b.municipio_id = v_municipio_id AND b.nm_bairro ILIKE 'Vila Nova Conquista'
                    LIMIT 1
               )
         WHERE u.municipio_id = v_municipio_id
           AND LOWER(u.nome) = LOWER('Unidade de Saude Olavo Mendes de Carvalho');
    END IF;
END $$;
