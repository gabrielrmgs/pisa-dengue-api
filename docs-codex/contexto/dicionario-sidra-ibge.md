# IBGE – Tabela 9514: População Residente por Sexo, Idade e Forma de Declaração da Idade

## Metadados

| Campo                  | Valor                             |
|------------------------|-----------------------------------|
| **Tabela**             | 9514                              |
| **Pesquisa**           | Censo Demográfico                 |
| **Assunto**            | Pessoas                           |
| **Última atualização** | 2023-12-22                        |
| **Período disponível** | 2022                              |
| **Fonte**              | IBGE – Censo Demográfico          |

---

## Endpoint Correto da API SIDRA

```
https://apisidra.ibge.gov.br/values/t/9514/<parâmetros>
```

A URL é construída com pares de identificador e valor separados por `/`:

```
https://apisidra.ibge.gov.br/values/t/9514/n<nivel>/<unidades>/v/<variaveis>/p/<periodo>/c2/<sexo>/c287/<idades>/c286/<forma>/f/<formato>/h/<cabecalho>/d/<decimais>
```

> **Limite:** A consulta é limitada a **100.000 valores**. Multiplique a quantidade de elementos em cada dimensão para estimar o total.

---

## Parâmetros

### `/t/` — Tabela (obrigatório)
```
/t/9514
```

### `/p/` — Período
```
/p/2022         → ano específico
/p/last         → último período disponível
/p/all          → todos os períodos
```

### `/v/` — Variáveis

| Código     | Descrição                                           | Decimais (padrão/máx) |
|------------|-----------------------------------------------------|------------------------|
| `93`       | População residente (Pessoas)                       | 0 / 0                  |
| `1000093`  | População residente – percentual do total geral (%) | 2 / 5                  |
| `all`      | Todas as variáveis                                  | —                      |
| `allxp`    | Todas exceto percentuais automáticos                | —                      |

Exemplo: `/v/93` ou `/v/93,1000093`

### `/n<i>/` — Nível Territorial

| Parâmetro | Nível                              | Qtd. Unidades |
|-----------|------------------------------------|---------------|
| `/n1/`    | Brasil                             | 1             |
| `/n2/`    | Grande Região                      | 5             |
| `/n3/`    | Unidade da Federação               | 27            |
| `/n24/`   | Região Geográfica Intermediária    | 133           |
| `/n25/`   | Região Geográfica Imediata         | 510           |
| `/n33/`   | Concentração Urbana                | 185           |
| `/n6/`    | Município                          | 5.570         |

Exemplos:
```
/n1/1               → Brasil
/n2/all             → todas as Grandes Regiões
/n3/22              → Piauí
/n6/2207702         → Parnaíba/PI
/n6/in n3/22        → todos os municípios do Piauí
```

### `/c2/` — Sexo (Classificação 2)

| Código | Categoria |
|--------|-----------|
| `6794` | Total     |
| `4`    | Homens    |
| `5`    | Mulheres  |
| `all`  | Todas     |

Exemplo: `/c2/6794,4,5`

### `/c286/` — Forma de Declaração da Idade (Classificação 286)

| Código    | Categoria           |
|-----------|---------------------|
| `113635`  | Total               |
| `6555`    | Data de nascimento  |
| `6556`    | Idade presumida     |

Exemplo: `/c286/113635`

### `/c287/` — Idade (Classificação 287)

#### Grupos etários (resumidos)
| Código    | Descrição        |
|-----------|------------------|
| `100362`  | Total            |
| `93070`   | 0 a 4 anos       |
| `93084`   | 5 a 9 anos       |
| `93085`   | 10 a 14 anos     |
| `93086`   | 15 a 19 anos     |
| `93087`   | 20 a 24 anos     |
| `93088`   | 25 a 29 anos     |
| `93089`   | 30 a 34 anos     |
| `93090`   | 35 a 39 anos     |
| `93091`   | 40 a 44 anos     |
| `93092`   | 45 a 49 anos     |
| `93093`   | 50 a 54 anos     |
| `93094`   | 55 a 59 anos     |
| `93095`   | 60 a 64 anos     |
| `93096`   | 65 a 69 anos     |
| `93097`   | 70 a 74 anos     |
| `93098`   | 75 a 79 anos     |
| `49108`   | 80 a 84 anos     |
| `49109`   | 85 a 89 anos     |
| `60040`   | 90 a 94 anos     |
| `60041`   | 95 a 99 anos     |
| `6653`    | 100 anos ou mais |

#### Idades simples — 0 a 4 anos
| Código  | Descrição      | Código  | Descrição |
|---------|----------------|---------|-----------|
| `6557`  | Menos de 1 ano | `93071` | Menos de 1 mês |
| `93072` | 1 mês          | `93073` | 2 meses   |
| `93074` | 3 meses        | `93075` | 4 meses   |
| `93076` | 5 meses        | `93077` | 6 meses   |
| `93078` | 7 meses        | `93079` | 8 meses   |
| `93080` | 9 meses        | `93081` | 10 meses  |
| `93082` | 11 meses       | `6558`  | 1 ano     |
| `6559`  | 2 anos         | `6560`  | 3 anos    |
| `6561`  | 4 anos         |         |           |

#### Idades simples — 5 a 9 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6562` | 5 anos    | `6563` | 6 anos    |
| `6564` | 7 anos    | `6565` | 8 anos    |
| `6566` | 9 anos    |        |           |

#### Idades simples — 10 a 14 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6567` | 10 anos   | `6568` | 11 anos   |
| `6569` | 12 anos   | `6570` | 13 anos   |
| `6571` | 14 anos   |        |           |

#### Idades simples — 15 a 19 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6572` | 15 anos   | `6573` | 16 anos   |
| `6574` | 17 anos   | `6575` | 18 anos   |
| `6576` | 19 anos   |        |           |

#### Idades simples — 20 a 24 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6577` | 20 anos   | `6578` | 21 anos   |
| `6579` | 22 anos   | `6580` | 23 anos   |
| `6581` | 24 anos   |        |           |

#### Idades simples — 25 a 29 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6582` | 25 anos   | `6656` | 26 anos   |
| `6657` | 27 anos   | `6658` | 28 anos   |
| `6659` | 29 anos   |        |           |

#### Idades simples — 30 a 34 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6583` | 30 anos   | `6584` | 31 anos   |
| `6585` | 32 anos   | `6586` | 33 anos   |
| `6587` | 34 anos   |        |           |

#### Idades simples — 35 a 39 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6588` | 35 anos   | `6589` | 36 anos   |
| `6590` | 37 anos   | `6591` | 38 anos   |
| `6592` | 39 anos   |        |           |

#### Idades simples — 40 a 44 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6593` | 40 anos   | `6594` | 41 anos   |
| `6595` | 42 anos   | `6596` | 43 anos   |
| `6597` | 44 anos   |        |           |

#### Idades simples — 45 a 49 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6598` | 45 anos   | `6599` | 46 anos   |
| `6600` | 47 anos   | `6601` | 48 anos   |
| `6602` | 49 anos   |        |           |

#### Idades simples — 50 a 54 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6603` | 50 anos   | `6604` | 51 anos   |
| `6605` | 52 anos   | `6606` | 53 anos   |
| `6607` | 54 anos   |        |           |

#### Idades simples — 55 a 59 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6608` | 55 anos   | `6609` | 56 anos   |
| `6610` | 57 anos   | `6611` | 58 anos   |
| `6612` | 59 anos   |        |           |

#### Idades simples — 60 a 64 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6613` | 60 anos   | `6614` | 61 anos   |
| `6615` | 62 anos   | `6616` | 63 anos   |
| `6617` | 64 anos   |        |           |

#### Idades simples — 65 a 69 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6618` | 65 anos   | `6619` | 66 anos   |
| `6620` | 67 anos   | `6621` | 68 anos   |
| `6622` | 69 anos   |        |           |

#### Idades simples — 70 a 74 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6623` | 70 anos   | `6624` | 71 anos   |
| `6625` | 72 anos   | `6626` | 73 anos   |
| `6627` | 74 anos   |        |           |

#### Idades simples — 75 a 79 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6628` | 75 anos   | `6629` | 76 anos   |
| `6630` | 77 anos   | `6631` | 78 anos   |
| `6632` | 79 anos   |        |           |

#### Idades simples — 80 a 84 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6633` | 80 anos   | `6634` | 81 anos   |
| `6635` | 82 anos   | `6636` | 83 anos   |
| `6637` | 84 anos   |        |           |

#### Idades simples — 85 a 89 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6638` | 85 anos   | `6639` | 86 anos   |
| `6640` | 87 anos   | `6641` | 88 anos   |
| `6642` | 89 anos   |        |           |

#### Idades simples — 90 a 94 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6643` | 90 anos   | `6644` | 91 anos   |
| `6645` | 92 anos   | `6646` | 93 anos   |
| `6647` | 94 anos   |        |           |

#### Idades simples — 95 a 99 anos
| Código | Descrição | Código | Descrição |
|--------|-----------|--------|-----------|
| `6648` | 95 anos   | `6649` | 96 anos   |
| `6650` | 97 anos   | `6651` | 98 anos   |
| `6652` | 99 anos   |        |           |

---

## Parâmetros de Formatação

| Parâmetro | Opções       | Descrição |
|-----------|--------------|-----------|
| `/f/`     | `a` (padrão) | Código + nome dos descritores |
|           | `c`          | Apenas códigos |
|           | `n`          | Apenas nomes |
|           | `u`          | Código + nome da unidade territorial; nome dos demais |
| `/h/`     | `y` (padrão) | Com cabeçalho |
|           | `n`          | Sem cabeçalho |
| `/d/`     | `s` (padrão) | Casas decimais padrão de cada variável |
|           | `m`          | Casas decimais máximas (maior precisão) |
|           | `0`–`9`      | Número fixo de casas decimais |

---

## Exemplos de Consultas

### 1. Brasil — população total por grandes grupos etários e sexo
```
https://apisidra.ibge.gov.br/values/t/9514/n1/1/v/93/p/2022/c2/6794,4,5/c287/100362,93070,93084,93085,93086,93087,93088,93089,93090,93091,93092,93093,93094,93095,93096,93097,93098,49108,49109,60040,60041,6653/c286/113635/f/n/h/y
```

### 2. Piauí (N3 código 22) — população total por sexo
```
https://apisidra.ibge.gov.br/values/t/9514/n3/22/v/93/p/2022/c2/6794,4,5/c287/100362/c286/113635/f/n/h/y
```

### 3. Parnaíba/PI (município código 2207702) — população total
```
https://apisidra.ibge.gov.br/values/t/9514/n6/2207702/v/93/p/2022/c2/6794/c287/100362/c286/113635/f/n/h/y
```

### 4. Todos os municípios do Piauí — população total (sem cabeçalho)
```
https://apisidra.ibge.gov.br/values/t/9514/n6/in n3/22/v/93/p/2022/c2/6794/c287/100362/c286/113635/f/n/h/n
```

### 5. Todas as UFs — grupos quinquenais, total geral
```
https://apisidra.ibge.gov.br/values/t/9514/n3/all/v/93/p/2022/c2/6794/c287/100362,93070,93084,93085,93086,93087,93088,93089,93090,93091,93092,93093,93094,93095,93096,93097,93098,49108,49109,60040,60041,6653/c286/113635/f/n/h/n
```

---

## Formato de Resposta JSON (exemplo)

A API retorna um array JSON. O primeiro objeto é o cabeçalho (quando `/h/y`), os demais são os dados:

```json
[
  {
    "D1N": "Unidade Territorial",
    "D2N": "Variável",
    "D3N": "Ano",
    "D4N": "Sexo",
    "D5N": "Idade",
    "D6N": "Forma de declaração da idade",
    "MN": "Unidade de Medida",
    "V": "Valor"
  },
  {
    "D1N": "Brasil",
    "D2N": "População residente",
    "D3N": "2022",
    "D4N": "Total",
    "D5N": "Total",
    "D6N": "Total",
    "MN": "Pessoas",
    "V": "203062512"
  }
]
```

### Campos dos registros

| Campo        | Descrição |
|--------------|-----------|
| `V`          | Valor numérico do registro |
| `MN` / `MC`  | Nome / código da unidade de medida |
| `D1N`–`D9N`  | Nome do descritor na dimensão 1–9 (ordem = ordem dos parâmetros na URL) |
| `D1C`–`D9C`  | Código do descritor na dimensão 1–9 |
| `NN` / `NC`  | Nome / código do nível territorial (quando múltiplos níveis) |

### Símbolos especiais no campo `V`

| Símbolo | Significado |
|---------|-------------|
| `-`     | Zero absoluto (não há ocorrências) |
| `0`     | Zero resultante de arredondamento |
| `X`     | Valor inibido (sigilo estatístico) |
| `..`    | Não se aplica |
| `...`   | Não disponível |

---

## Notas Importantes

1. Dados do **Universo** (não amostra).
2. Os dados dos municípios **Abel Figueiredo (PA)** e **São Pedro da Água Branca (MA)** foram corrigidos em 22/12/2023, impactando os totais do Pará, Maranhão e das Grandes Regiões Norte e Nordeste.
3. Para retornar dados em XML, adicionar `?formato=xml` ao final da URL.
4. O descritor completo da tabela em JSON está disponível em:
   `https://apisidra.ibge.gov.br/DescritoresTabela/t/9514`
