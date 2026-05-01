# 🦟 Pisa Dengue API

API backend desenvolvida com **Quarkus** para monitoramento e análise de dados epidemiológicos de dengue, com suporte a **multi-tenant por município**, integração com **InfoDengue** e visualização geoespacial via **GeoJSON**.

---

## 🚀 Tecnologias utilizadas

* Java 21+
* Quarkus
* Hibernate ORM + Panache
* PostgreSQL + PostGIS
* Flyway (migrações)
* JWT (SmallRye JWT)
* REST Client (MicroProfile)
* GeoTools + JTS (processamento de shapefiles)
* Docker + Docker Compose

---

## 🧠 Funcionalidades

### 🔐 Autenticação e Autorização

* Login com JWT
* Controle de acesso por roles (ADMIN, etc)
* Multi-tenant baseado em `municipio_id`
* Filtro global (`TenantFilter`) para injeção de contexto

---

### 🗺️ Dados Geoespaciais

* Importação de shapefiles do IBGE
* Persistência de geometrias no PostGIS
* Conversão para **GeoJSON**
* Endpoint para consumo no frontend (Leaflet, etc)

---

### 🏥 Integração com InfoDengue

* Consumo da API pública da Fiocruz
* Cache de dados por município e ano
* Agregações para dashboard:

  * Casos no ano
  * Casos no mês
  * Incidência acumulada
  * Último nível de alerta

---

### 📊 Dashboard Epidemiológico

* Dados consolidados por município
* Histórico multi-ano (2023–2026)
* Preparado para gráficos e mapas interativos

---

## 📦 Como rodar o projeto

### Pré-requisitos

* Docker instalado
* Java 21+
* Maven (ou usar wrapper)

---

### 🐳 Subindo com Docker

```bash
mvn clean package
docker compose up --build -d
```

A API estará disponível em:

```
http://localhost:8081
```

---

## 🔑 Autenticação

### Criar usuário admin

```bash
curl -X POST http://localhost:8081/auth/criarAdmin \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@admin.com",
    "senha": "admin"
  }'
```

---

### Login

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@admin.com",
    "senha": "admin"
  }'
```

Resposta:

```json
{
  "token": "JWT_AQUI"
}
```

---

### Uso do token

```bash
Authorization: Bearer SEU_TOKEN
```

---

## 🧩 Multi-Tenancy

A aplicação utiliza **isolamento por município**:

* O `municipio_id` é embutido no JWT
* Um `TenantFilter` intercepta todas as requisições
* O `TenantContext` armazena o município do usuário logado
* Todas as queries são filtradas automaticamente

👉 Isso impede acesso a dados de outros municípios (ex: `/bairro/999`)

---

## 🌎 Endpoint GeoJSON

```http
GET /mapa/geojson
```

Retorna:

```json
{
  "type": "FeatureCollection",
  "features": [...]
}
```

✔ Já filtrado pelo município do usuário
✔ Pronto para uso com Leaflet

---

## 📊 Dashboard

### Endpoint principal

```http
GET /dashboard
```

Resposta:

```json
{
  "totalCasosAno": 1234,
  "totalCasosMes": 120,
  "incidencia": 456.7,
  "nivelAlerta": "Alto",
  "corAlerta": "#f97316"
}
```

---

### Histórico (2023–2026)

```http
GET /dashboard/historico
```

Retorna séries separadas por ano para gráficos.

---

## 📁 Estrutura do projeto

```
src/main/java/br/com/gemsbiotec
├── auth/                # Autenticação e JWT
├── dominio/             # Entidades (Usuario, Municipio, Bairro...)
├── repository/          # Repositórios Panache
├── security/            # TenantContext e filtros
├── shapefile/           # Importação geoespacial
├── integration/         # Integrações externas (InfoDengue)
├── resource/            # Endpoints REST
```

---

## ⚠️ Observações importantes

* O projeto usa **PostGIS**, então o banco precisa ter a extensão ativada:

```sql
CREATE EXTENSION postgis;
```

* Após `docker compose down -v`, o banco é recriado do zero

---

## 👨‍💻 Autor

Desenvolvido por **Gabriel Sá**
