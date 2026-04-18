# Delivery and deployment

## 1. Containerized local deployment (primary)

From the repository root:

```bash
docker compose -f ADS/docker-compose.yml up --build
```

- **UI:** http://localhost:8088/ (nginx + static React build, proxies `/api/` to the API container).
- **API (host port):** http://localhost:8080/ (optional direct access while debugging).
- **Postgres:** internal to the Compose network (`postgresdb:5432`); see `ADS/docker-compose.yml` for credentials and volume name.

Stop without deleting data:

```bash
docker compose -f ADS/docker-compose.yml down
```

## 2. Environment variables (examples)

Spring Boot reads standard properties; override with env vars where needed:

| Property / env | Purpose |
|----------------|---------|
| `SPRING_DATASOURCE_URL` | JDBC URL when not using embedded defaults |
| `SPRING_DATASOURCE_USERNAME` / `PASSWORD` | DB credentials |
| `ads.cors.allowed-origins` | Comma-separated origins if the SPA is not same-origin |

Front end: **`VITE_API_BASE`** — empty when using Vite proxy or nginx same-origin; set to full API origin if the static site is hosted separately.

## 3. CI/CD

- **Workflow:** `.github/workflows/ads-ci.yml` at repo root.
- **Jobs:** Maven `clean test` on `ADS/ads-backend`; `npm ci && npm run build` on `ADS/ads-frontend`.
- **Triggers:** pushes and pull requests that touch `ADS/**` or the workflow file.

## 4. Cloud (example: Azure)

Typical mapping:

| Local concept | Azure-style service |
|---------------|---------------------|
| `postgresdb` | Azure Database for PostgreSQL Flexible Server |
| `ads-backend` | Azure App Service (Java) or Azure Container Apps |
| `nginx` + static UI | Azure Static Web Apps, or App Service serving `dist/`, or Container Apps front door |

Store secrets (DB password, JWT signing key) in **Key Vault** or App Service application settings — never commit production secrets to Git.

## 5. Demo checklist

1. `docker compose -f ADS/docker-compose.yml up --build`
2. Open http://localhost:8088/
3. Log in as `manager` / `password`, `dentist1` / `password`, `patient1` / `password`
4. Walk through booking and office confirmation flows
