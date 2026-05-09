# Prompt Para Lovable - Ajuste Do Dashboard Principal

Atualize a aplicacao frontend PISA Dengue ja iniciada. Nao recrie o projeto, nao troque a arquitetura atual e nao adicione backend, Supabase, Firebase ou mocks permanentes. Apenas adapte o dashboard principal para ficar mais completo, consumindo a API existente.

## Objetivo

Adicionar ao dashboard principal todos estes blocos:

1. Cards de resumo do municipio.
2. Grafico comparativo de casos de dengue dos 3 ultimos anos.
3. Grafico de barras de populacao por faixa etaria e sexo.
4. Grafico de torta de populacao por sexo.
5. Mapa-resumo dos bairros do municipio.

O dashboard deve continuar sendo uma tela operacional, objetiva e profissional, com design inspirado no Atlassian Design System e usando a paleta do projeto.

## Paleta

Use a paleta ja definida:

```txt
Primaria:        #0774B3
Primaria clara:  #A7D9F7
Primaria escura: #115C8D
Suporte:         #5E99B8
Neutra fria:     #A4BCD4
```

Sugestoes:

```txt
Ano atual dengue:       #0774B3
Ano anterior dengue:    #5E99B8
Dois anos atras dengue: #A4BCD4
Masculino:              #0774B3
Feminino:               #A7D9F7
```

## Layout Esperado Do Dashboard

Organize o dashboard principal nesta ordem:

1. Cabecalho da pagina:
   - nome do municipio;
   - estado;
   - nivel de alerta atual.

2. Cards de resumo:
   - casos no ano;
   - casos no mes;
   - incidencia acumulada;
   - populacao;
   - semana epidemiologica atual;
   - ultimo alerta.

3. Grafico comparativo dos 3 ultimos anos:
   - logo abaixo dos cards;
   - ocupar largura principal;
   - altura aproximada entre 300px e 380px.

4. Area demografica:
   - grafico de barras por faixa etaria e sexo;
   - grafico de torta por sexo;
   - ambos iniciam com dados do municipio.

5. Mapa-resumo dos bairros:
   - abaixo dos graficos ou em composicao lateral se houver espaco;
   - ao clicar em um bairro, atualizar os graficos demograficos para aquele bairro.

Manter a pagina `/mapa` como experiencia detalhada. O mapa no dashboard e apenas uma visao resumida.

## Autenticacao

Manter o comportamento existente:

- token vem do login;
- token deve ser enviado em todas as requisicoes privadas;
- header:

```http
Authorization: Bearer JWT_AQUI
```

- se retornar `401`, limpar sessao e voltar para `/login`;
- se retornar `403`, mostrar mensagem de acesso negado.

## Dashboard Resumo

Consumir:

```http
GET /api/v1/dashboard/resumo
Authorization: Bearer JWT_AQUI
```

Exemplo:

```json
{
  "municipioId": 1,
  "municipioNome": "Uniao",
  "municipioCodigoIbge": "2211100",
  "estadoNome": "Piaui",
  "estadoSigla": "PI",
  "populacao": 46119,
  "populacaoMasculina": 22921,
  "populacaoFeminina": 23198,
  "totalCasosAno": 238,
  "totalCasosMes": 31,
  "incidenciaAcumulada": 516.1,
  "semanaEpidemiologicaAtual": 19,
  "semanaUltimoAlerta": 202619,
  "nivelAlerta": "Moderado",
  "corAlerta": "#eab308"
}
```

Usar `populacaoMasculina` e `populacaoFeminina` para preencher o grafico de torta inicial do municipio.

## Grafico Comparativo Dos 3 Ultimos Anos

Consumir o endpoint consolidado da API:

```http
GET /api/v1/dashboard/dengue/comparativo
Authorization: Bearer JWT_AQUI
```

O frontend nao deve chamar os anos separadamente. A API ja retorna os 3 ultimos anos organizados por semana epidemiologica.

Nao calcular incidencia, alerta ou risco no frontend.

### Exemplo De Response

```json
{
  "anos": [2026, 2025, 2024],
  "semanas": [
    {
      "semana": 1,
      "valores": [
        {
          "ano": 2026,
          "casos": 7,
          "casosEstimados": 8.5,
          "incidenciaPor100k": 18.4
        },
        {
          "ano": 2025,
          "casos": 3,
          "casosEstimados": 4.2,
          "incidenciaPor100k": 9.1
        },
        {
          "ano": 2024,
          "casos": 5,
          "casosEstimados": 6.0,
          "incidenciaPor100k": 12.8
        }
      ]
    },
    {
      "semana": 2,
      "valores": [
        {
          "ano": 2026,
          "casos": 9,
          "casosEstimados": 11.2,
          "incidenciaPor100k": 24.2
        },
        {
          "ano": 2025,
          "casos": 4,
          "casosEstimados": 5.1,
          "incidenciaPor100k": 10.6
        },
        {
          "ano": 2024,
          "casos": 6,
          "casosEstimados": 7.0,
          "incidenciaPor100k": 14.0
        }
      ]
    }
  ]
}
```

Para renderizar em bibliotecas como Recharts, o frontend pode transformar visualmente para um formato tabular, por exemplo:

```json
[
  {
    "semana": 1,
    "2026": 7,
    "2025": 3,
    "2024": 5
  },
  {
    "semana": 2,
    "2026": 9,
    "2025": 4,
    "2024": 6
  }
]
```

Essa transformacao e apenas de apresentacao. Nao enviar de volta para a API.

### Requisitos Do Grafico

- Titulo: `Casos de dengue por semana epidemiologica`
- Subtitulo: `Comparativo dos ultimos 3 anos`
- Eixo X: semana epidemiologica.
- Eixo Y: casos notificados.
- Linha do ano atual mais forte.
- Anos anteriores mais discretos.
- Tooltip com semana, ano e casos.
- Loading enquanto o comparativo carrega.
- Estado vazio se nao houver dados.

## Grafico De Barras Por Faixa Etaria E Sexo

Este grafico deve iniciar com dados do municipio.

Consumir:

```http
GET /api/v1/dashboard/demografia/faixa-etaria
Authorization: Bearer JWT_AQUI
```

Exemplo:

```json
{
  "escopo": "MUNICIPIO",
  "municipioId": 1,
  "municipioNome": "Uniao",
  "bairroId": null,
  "bairroNome": null,
  "faixas": [
    {
      "faixa": "0 a 4 anos",
      "masculino": 1210,
      "feminino": 1168,
      "total": 2378
    },
    {
      "faixa": "5 a 9 anos",
      "masculino": 1322,
      "feminino": 1280,
      "total": 2602
    }
  ]
}
```

Requisitos:

- Tipo: barras agrupadas.
- Uma barra para masculino e uma barra para feminino em cada faixa.
- Eixo X: faixa etaria.
- Eixo Y: populacao.
- Tooltip com masculino, feminino e total.
- Legenda clara.
- Titulo inicial: `Populacao por faixa etaria`
- Subtitulo inicial: `Municipio de {municipioNome}`

Quando o usuario clicar em um bairro no mapa, atualizar este grafico usando os dados do proprio GeoJSON, sem precisar chamar outro endpoint.

## Grafico De Torta Por Sexo

Este grafico deve iniciar com dados do municipio usando o response de `/api/v1/dashboard/resumo`:

```json
{
  "populacaoMasculina": 22921,
  "populacaoFeminina": 23198
}
```

Formato visual sugerido:

```json
[
  {
    "sexo": "Masculino",
    "populacao": 22921
  },
  {
    "sexo": "Feminino",
    "populacao": 23198
  }
]
```

Requisitos:

- Titulo inicial: `Populacao por sexo`
- Subtitulo inicial: `Municipio de {municipioNome}`
- Cores:
  - Masculino: `#0774B3`
  - Feminino: `#A7D9F7`
- Tooltip com valor absoluto e percentual.

Quando o usuario clicar em um bairro no mapa, atualizar este grafico usando `sexo_masculino` e `sexo_feminino` do GeoJSON.

## Mapa-Resumo No Dashboard

Adicionar uma secao de mapa no dashboard principal.

Consumir preferencialmente:

```http
GET /api/v1/dashboard/geojson/bairros-demografia
Authorization: Bearer JWT_AQUI
Accept: application/geo+json
```

Fallback:

```http
GET /api/v1/dashboard/geojson
Authorization: Bearer JWT_AQUI
Accept: application/geo+json
```

### Exemplo De GeoJSON

```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "geometry": {
        "type": "MultiPolygon",
        "coordinates": [
          [
            [
              [-42.8612, -4.5851],
              [-42.8581, -4.5857],
              [-42.8574, -4.5899],
              [-42.8612, -4.5851]
            ]
          ]
        ]
      },
      "properties": {
        "bairro_id": 10,
        "codigo_ibge_bairro": "2211100001",
        "nome_bairro": "Centro",
        "nome_municipio": "Uniao",
        "codigo_ibge_municipio": "2211100",
        "nome_estado": "Piaui",
        "sigla_estado": "PI",
        "populacao": 8120,
        "sexo_masculino": 3920,
        "sexo_feminino": 4200,
        "masculino_0_a_4_anos": 260,
        "feminino_0_a_4_anos": 250,
        "masculino_5_a_9_anos": 300,
        "feminino_5_a_9_anos": 290,
        "masculino_10_a_14_anos": 315,
        "feminino_10_a_14_anos": 305,
        "masculino_15_a_19_anos": 330,
        "feminino_15_a_19_anos": 310,
        "masculino_20_a_24_anos": 350,
        "feminino_20_a_24_anos": 330,
        "masculino_25_a_29_anos": 360,
        "feminino_25_a_29_anos": 350,
        "masculino_30_a_39_anos": 650,
        "feminino_30_a_39_anos": 670,
        "masculino_40_a_49_anos": 570,
        "feminino_40_a_49_anos": 610,
        "masculino_50_a_59_anos": 440,
        "feminino_50_a_59_anos": 470,
        "masculino_60_a_69_anos": 290,
        "feminino_60_a_69_anos": 320,
        "masculino_70_anos_ou_mais": 150,
        "feminino_70_anos_ou_mais": 200,
        "moradores_0_a_4_anos": 510,
        "moradores_5_a_9_anos": 590,
        "moradores_10_a_14_anos": 620,
        "moradores_15_a_19_anos": 640,
        "moradores_20_a_24_anos": 680,
        "moradores_25_a_29_anos": 710,
        "moradores_30_a_39_anos": 1320,
        "moradores_40_a_49_anos": 1180,
        "moradores_50_a_59_anos": 910,
        "moradores_60_a_69_anos": 610,
        "moradores_70_anos_ou_mais": 350
      }
    }
  ]
}
```

### Requisitos Do Mapa

- Titulo: `Mapa territorial do municipio`
- Subtitulo: `Bairros e distribuicao demografica`
- Altura entre 360px e 460px.
- Ajustar zoom automaticamente aos bairros.
- Hover destaca o bairro.
- Clique seleciona o bairro e atualiza:
  - grafico de barras por faixa etaria e sexo;
  - grafico de torta por sexo;
  - painel compacto do bairro.
- Colorir bairros por populacao quando o campo existir.
- Nao mostrar casos de dengue por bairro.
- Incluir botao discreto: `Abrir mapa completo`, apontando para `/mapa`.

## Como Atualizar Graficos Ao Clicar No Bairro

Ao clicar em uma feature do GeoJSON:

1. Ler `feature.properties`.
2. Atualizar titulo/subtitulo dos graficos para `Bairro {nome_bairro}`.
3. Atualizar grafico de torta usando:

```txt
sexo_masculino
sexo_feminino
```

4. Atualizar grafico de barras usando:

```txt
masculino_0_a_4_anos / feminino_0_a_4_anos
masculino_5_a_9_anos / feminino_5_a_9_anos
masculino_10_a_14_anos / feminino_10_a_14_anos
masculino_15_a_19_anos / feminino_15_a_19_anos
masculino_20_a_24_anos / feminino_20_a_24_anos
masculino_25_a_29_anos / feminino_25_a_29_anos
masculino_30_a_39_anos / feminino_30_a_39_anos
masculino_40_a_49_anos / feminino_40_a_49_anos
masculino_50_a_59_anos / feminino_50_a_59_anos
masculino_60_a_69_anos / feminino_60_a_69_anos
masculino_70_anos_ou_mais / feminino_70_anos_ou_mais
```

Nao chamar endpoint adicional para dados do bairro se essas propriedades existirem no GeoJSON. O objetivo e evitar codigo e requisicoes desnecessarias.

Adicionar uma acao discreta `Voltar para municipio` para restaurar os graficos com dados municipais:

- torta: usar novamente `populacaoMasculina` e `populacaoFeminina` do resumo;
- barras: usar novamente `/api/v1/dashboard/demografia/faixa-etaria`.

## Estados Obrigatorios

Cada bloco deve ter:

- loading;
- erro;
- vazio quando aplicavel;
- sucesso.

Se uma API falhar, nao quebrar o dashboard inteiro. Mostre erro apenas no bloco afetado.

## Importante

Nao transformar o dashboard em tela promocional. Ele deve parecer uma ferramenta de trabalho.

Nao criar cards ou graficos com dados inventados. Se a API nao retornar dados, mostre estado vazio.

Nao implementar regra epidemiologica no frontend. O frontend pode apenas:

- formatar numeros;
- ordenar semanas;
- montar series visuais;
- renderizar mapas e graficos;
- trocar dados dos graficos quando um bairro for selecionado.

Nao remover a pagina `/mapa`. O dashboard deve ter uma versao resumida do mapa, e `/mapa` deve continuar sendo a tela detalhada.
