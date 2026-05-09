# Prompt Para Lovable - Frontend PISA Dengue

Crie uma aplicacao frontend profissional para o sistema PISA Dengue, consumindo exclusivamente a API REST existente. Nao use Supabase, Firebase, banco local remoto, edge functions ou qualquer backend gerado pela Lovable. Toda regra de negocio deve permanecer na API. O frontend deve ser uma camada de apresentacao, autenticacao de sessao, navegacao, visualizacao e formularios.

## Objetivo Do Produto

Construir uma aplicacao web para monitoramento epidemiologico de dengue por municipio, com login, dashboard, mapa interativo de bairros, serie historica anual e area inicial de gestao de usuarios.

A aplicacao deve parecer um produto SaaS publico/profissional, discreto e confiavel. Evite aparencia generica de app feito por IA: nada de landing page, hero gigante, cards decorativos excessivos, gradientes chamativos, textos explicando funcionalidades na tela ou layouts promocionais. A primeira tela depois do login deve ser o dashboard operacional.

## Stack Esperada

Use:

- React com TypeScript.
- React Router.
- TanStack Query ou equivalente simples para cache de requisicoes.
- Axios ou fetch encapsulado em um cliente HTTP unico.
- Leaflet para mapa interativo.
- Recharts, Tremor-like charts ou biblioteca leve semelhante para graficos.
- Componentes organizados por feature.

Nao use:

- Supabase.
- Backend gerado.
- Mock permanente como fonte de verdade.
- Regras epidemiologicas no frontend.
- Duplicacao de calculos que a API ja retorna.

## API Base

A URL base deve vir de variavel de ambiente:

```txt
VITE_API_BASE_URL=http://z8g8ocwv2xjb8j29y4j9vf4w.72.60.144.48.sslip.io
```

Todas as chamadas devem usar essa base.

## Autenticacao

### Login

Endpoint:

```http
POST /auth/login
Content-Type: application/json
```

Request:

```json
{
  "email": "admin@admin.com",
  "senha": "admin123"
}
```

Response:

```json
{
  "token": "JWT_AQUI",
  "nomeCompleto": "Gabriel Sa",
  "perfil": "ADMIN"
}
```

Comportamento esperado:

- Ao logar com sucesso, armazenar o token e os dados basicos do usuario.
- Pode usar `localStorage` para primeira versao.
- Criar um cliente HTTP centralizado que injete:

```http
Authorization: Bearer JWT_AQUI
```

- Toda rota privada deve exigir token.
- Se qualquer chamada protegida retornar `401` ou `403`, redirecionar para login ou mostrar estado de acesso negado conforme o caso.
- O frontend nao deve tentar decodificar regra sensivel do JWT para autorizar operacoes complexas. Use o perfil retornado no login apenas para controle basico de exibicao, como mostrar ou ocultar menu de usuarios.

## Rotas Da Aplicacao

Crie estas rotas:

- `/login`
- `/dashboard`
- `/mapa`
- `/dengue/historico`
- `/usuarios/novo`

Redirecionar `/` para `/dashboard` se autenticado, ou `/login` se nao autenticado.

## Design System

Use um Design System inspirado no Atlassian Design System, sem copiar marca, logos ou assets da Atlassian.

Direcao visual:

- Layout limpo, denso, operacional e profissional.
- Navegacao lateral compacta ou topo + lateral, semelhante a ferramentas corporativas.
- Superficies brancas ou quase brancas.
- Bordas sutis.
- Sombra minima.
- Raio de borda preferencial: 6px a 8px.
- Tipografia clara, com hierarquia moderada.
- Estados de foco e hover bem definidos.
- Tabelas e paineis legiveis.
- Evitar gradientes, blobs decorativos, ilustracoes genericas e efeitos chamativos.

Paleta obrigatoria:

```txt
Primaria:        #0774B3
Primaria clara:  #A7D9F7
Primaria escura: #115C8D
Suporte:         #5E99B8
Neutra fria:     #A4BCD4
```

Sugestao de tokens:

```txt
--color-primary: #0774B3
--color-primary-hover: #115C8D
--color-primary-subtle: #A7D9F7
--color-support: #5E99B8
--color-border: #A4BCD4
--color-bg: #F7F9FB
--color-surface: #FFFFFF
--color-text: #172B4D
--color-text-muted: #5E6C84
--color-danger: #DE350B
--color-warning: #FFAB00
--color-success: #36B37E
```

Componentes esperados:

- `AppShell`
- `Sidebar`
- `Topbar`
- `PageHeader`
- `MetricCard`
- `StatusBadge`
- `DataPanel`
- `LoadingState`
- `EmptyState`
- `ErrorState`
- `ProtectedRoute`
- `ApiClient`

Nao crie componentes abstratos desnecessarios. Se um componente so for usado uma vez e for simples, mantenha local.

## Dashboard

Rota:

```txt
/dashboard
```

Consumir:

```http
GET /api/v1/dashboard/resumo
Authorization: Bearer JWT_AQUI
```

Exemplo de response:

```json
{
  "municipioId": 1,
  "municipioNome": "Uniao",
  "municipioCodigoIbge": "2211100",
  "estadoNome": "Piaui",
  "estadoSigla": "PI",
  "populacao": 46119,
  "totalCasosAno": 238,
  "totalCasosMes": 31,
  "incidenciaAcumulada": 516.1,
  "semanaEpidemiologicaAtual": 19,
  "semanaUltimoAlerta": 202619,
  "nivelAlerta": "Moderado",
  "corAlerta": "#eab308"
}
```

Layout:

- Cabecalho da pagina com municipio e estado.
- Cards principais:
  - Casos no ano.
  - Casos no mes.
  - Incidencia acumulada.
  - Nivel de alerta.
  - Populacao.
  - Semana epidemiologica atual.
- Pequeno painel lateral com informacoes do municipio.
- Nao calcular estes totais no front. Apenas formatar valores retornados pela API.

## Mapa Interativo

Rota:

```txt
/mapa
```

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

Exemplo de response GeoJSON:

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
        "codigo_ibge_bairro": "2211100001",
        "nome_bairro": "Centro",
        "nome_municipio": "Uniao",
        "codigo_ibge_municipio": "2211100",
        "nome_estado": "Piaui",
        "sigla_estado": "PI",
        "populacao": 8120,
        "sexo_masculino": 3920,
        "sexo_feminino": 4200,
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

Comportamento do mapa:

- Renderizar os bairros do municipio do usuario logado.
- Ajustar o zoom automaticamente ao bounds do GeoJSON.
- Hover deve destacar bairro.
- Clique deve abrir painel lateral com dados do bairro.
- Colorir bairros por populacao quando houver demografia.
- Nao inventar casos de dengue por bairro. A API InfoDengue retorna dengue por municipio, nao por bairro.
- Se nao houver demografia, usar cor neutra e mostrar apenas nome/codigos.

## Historico De Dengue

Rota:

```txt
/dengue/historico
```

Consumir:

```http
GET /api/v1/dashboard/dengue/ano/2026
Authorization: Bearer JWT_AQUI
```

Exemplo de response:

```json
[
  {
    "dataInicioSE": "2026-01-04",
    "SE": 202601,
    "casos_est": 8.5,
    "casos_est_min": 4.0,
    "casos_est_max": 14.0,
    "casos": 7,
    "municipio_geocodigo": 2211100,
    "p_rt1": 0.74,
    "p_inc100k": 18.4,
    "Rt": 1.08,
    "nivel": 2,
    "municipio_nome": "Uniao",
    "nivel_inc": 2
  },
  {
    "dataInicioSE": "2026-01-11",
    "SE": 202602,
    "casos_est": 11.2,
    "casos_est_min": 6.0,
    "casos_est_max": 18.0,
    "casos": 9,
    "municipio_geocodigo": 2211100,
    "p_rt1": 0.81,
    "p_inc100k": 24.2,
    "Rt": 1.15,
    "nivel": 2,
    "municipio_nome": "Uniao",
    "nivel_inc": 2
  }
]
```

Tela:

- Filtro de ano simples.
- Grafico de linha ou barras para `casos`.
- Opcional: linha secundaria para `casos_est`.
- Tabela compacta com semana, casos, casos estimados, incidencia e Rt.
- Nao recalcular alerta epidemiologico alem de apresentar o campo `nivel`.

Observacao: se o backend retornar `dataInicioSE` nulo em algum registro, a interface deve continuar funcionando usando `SE` como eixo.

## Cadastro De Usuario

Rota:

```txt
/usuarios/novo
```

Mostrar no menu apenas para perfis `ADMIN` e `GESTOR`.

Consumir:

```http
POST /api/v1/usuarios
Authorization: Bearer JWT_AQUI
Content-Type: application/json
```

Request para ADMIN:

```json
{
  "nome": "Maria Oliveira",
  "email": "maria.oliveira@example.com",
  "senha": "senha123",
  "role": "GESTOR",
  "municipioId": 1
}
```

Request para GESTOR:

```json
{
  "nome": "Joao Santos",
  "email": "joao.santos@example.com",
  "senha": "senha123",
  "role": "AGENTE"
}
```

Response:

```json
{
  "id": 15,
  "nome": "Joao Santos",
  "email": "joao.santos@example.com",
  "role": "AGENTE",
  "ativo": true,
  "municipioId": 1,
  "municipioNome": "Uniao",
  "criadoEm": "2026-05-09T03:50:00"
}
```

Regras de tela:

- `ADMIN` pode selecionar/informar municipioId.
- `GESTOR` nao deve escolher municipio; o backend usa o municipio do token.
- `GESTOR` so deve ver opcoes de role `AGENTE` e `VIEWER`.
- Nunca mostrar senha depois do cadastro.
- Tratar erro `409` como e-mail ja cadastrado.

## Tratamento De Erros

Padrao da API pode retornar texto simples ou JSON de erro. O cliente HTTP deve suportar ambos.

Exemplo de erro JSON:

```json
{
  "details": "Error id 29cefa47-80dd-4308-b42f-639e864011b7-38",
  "stack": ""
}
```

Comportamento:

- `400`: mostrar mensagem de validacao.
- `401`: limpar sessao e ir para login.
- `403`: mostrar "Voce nao tem permissao para acessar este recurso."
- `404`: mostrar estado vazio ou recurso nao encontrado.
- `409`: mostrar conflito, principalmente e-mail duplicado.
- `500`: mostrar erro generico e nao expor stack trace.

## Organizacao De Codigo

Estrutura sugerida:

```txt
src/
  app/
    router.tsx
    App.tsx
  features/
    auth/
      LoginPage.tsx
      authStore.ts
      ProtectedRoute.tsx
    dashboard/
      DashboardPage.tsx
      dashboardApi.ts
      components/
    mapa/
      MapaPage.tsx
      mapaApi.ts
      components/
    dengue/
      HistoricoDenguePage.tsx
      dengueApi.ts
    usuarios/
      NovoUsuarioPage.tsx
      usuariosApi.ts
  shared/
    api/
      httpClient.ts
    components/
    layout/
    styles/
```

Evite gerar codigo inutil. Nao criar stores globais complexas se nao forem necessarias. Nao criar camada de repository no frontend. O frontend chama API, renderiza estados e envia formularios.

## Estados Obrigatorios

Toda tela com requisicao deve ter:

- loading;
- erro;
- vazio quando aplicavel;
- sucesso.

## Qualidade Visual

O resultado deve parecer uma ferramenta publica de vigilancia epidemiologica:

- clara;
- institucional;
- confiavel;
- rapida de escanear;
- sem excesso de texto explicativo;
- sem visual de template generico.

Use a paleta indicada, mas com bastante branco, cinzas frios e contraste adequado. A cor primaria deve guiar a navegacao, botoes e destaque de dados; nao dominar a tela inteira.

## Entrega Esperada

Entregar uma aplicacao funcional com:

- login integrado a API;
- armazenamento e envio automatico do JWT;
- dashboard consumindo `/api/v1/dashboard/resumo`;
- mapa consumindo GeoJSON da API;
- historico anual de dengue;
- formulario de cadastro de usuario;
- layout responsivo desktop/tablet;
- codigo organizado e enxuto;
- sem Supabase ou backend auxiliar.
