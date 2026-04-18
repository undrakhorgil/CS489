# ADS — containerization

The **ADS** stack is fully containerized with **Docker Compose**: PostgreSQL, Spring Boot API, and nginx serving the React production build (with `/api` proxied to the API).

## Files

| File | Role |
|------|------|
| `docker-compose.yml` | Orchestrates `postgresdb`, `ads-backend`, `ads-frontend`; named project **ADS**. |
| `ads-backend/Dockerfile` | Multi-stage: Maven builds the JAR; JRE image runs it + health check. |
| `ads-frontend/Dockerfile` | Multi-stage: `npm ci` + `vite build`; nginx serves `dist/` + `nginx/default.conf`. |
| `ads-backend/.dockerignore` | Keeps build context small (excludes `target/`, etc.). |
| `ads-frontend/.dockerignore` | Excludes `node_modules`, `dist`, local `.env`. |

## Run the whole stack

From **`ADS/`**:

```bash
docker compose up --build
```

From the **repository root** (parent of `ADS/`):

```bash
docker compose -f ADS/docker-compose.yml up --build
```

Detached:

```bash
docker compose -f ADS/docker-compose.yml up --build -d
```

## URLs (default ports)

| Service | URL / port |
|---------|------------|
| **Web UI** | http://localhost:8088/ |
| **REST API** (host) | http://localhost:8080/ (e.g. `GET /api/v1/health`) |
| **Postgres** | `localhost:5432` (user/password/db: `postgres` / `postgres` / `postgres`) |

If port **5432** is already taken, edit `docker-compose.yml` under `postgresdb.ports` (e.g. `"5433:5432"`).

## Stop and data

```bash
docker compose -f ADS/docker-compose.yml down
```

Remove the database volume as well:

```bash
docker compose -f ADS/docker-compose.yml down -v
```

## Build images only

```bash
cd ADS
docker compose build
```

## Environment (Compose)

`docker-compose.yml` sets `SPRING_DATASOURCE_*`, `ADS_SECURITY_JWT_SECRET`, and `ADS_CORS_ALLOWED_ORIGINS` for local use. For real deployment, override with secrets and a strong JWT secret (see `docs/DeliveryDeployment.md`).
