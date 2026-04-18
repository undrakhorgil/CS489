# ADS — Advantis Dental Surgeries

**Containerized stack:** Postgres + Spring Boot + nginx/React — see **`DOCKER.md`** for compose commands, ports, and image layout.

This folder contains the course project split into:

- **`ads-backend/`** — Spring Boot REST API (PostgreSQL, JWT security)
- **`ads-frontend/`** — Vite + React + TypeScript web UI

---

## Quick start: everything in Docker (recommended)

**Requirements:** [Docker](https://docs.docker.com/get-docker/) and Docker Compose v2.

**Docker Desktop:** use **`ADS/docker-compose.yml`** only. The Compose project name is **`ADS`**, so you get one stack with **postgresdb**, **ads-backend**, and **ads-frontend**. Run it from **`ADS/`** (or `-f ADS/docker-compose.yml` from the repo root).

From the **repository root** (the folder that contains `ADS/`):

```bash
docker compose -f ADS/docker-compose.yml up --build
```

Or from **inside `ADS/`**:

```bash
cd ADS
docker compose up --build
```

- **UI:** [http://localhost:8088/](http://localhost:8088/) — nginx serves the app and proxies **`/api/`** to **`ads-backend:8080`** on the Docker network (browser talks only to nginx; same origin, no extra CORS).
- **API (host):** [http://localhost:8080/](http://localhost:8080/) — Spring Boot, same as nginx uses internally via the service name **`ads-backend`**.
- **Postgres (host):** **`localhost:5432`** — same DB the API uses via **`postgresdb:5432`** inside the stack.

Inside Compose, containers reach each other by **service name** and the **container port** (not by `localhost` on your Mac). **`ports:`** only publishes those services to your machine for debugging or local tools.

The **ads-frontend** container waits until **ads-backend** passes its health check, so nginx does not proxy to a JVM that is still starting (that situation shows up as **502 Bad Gateway** on login).

Run in the background:

```bash
docker compose -f ADS/docker-compose.yml up --build -d
```

Stop containers (keep database volume):

```bash
docker compose -f ADS/docker-compose.yml down
```

Stop and **delete** the Postgres data volume:

```bash
docker compose -f ADS/docker-compose.yml down -v
```

---

## Kubernetes (Minikube)

The same three-tier app can run on **local Minikube**: manifests live under **`ADS/k8s/`** (Kustomize). Build images into Minikube’s Docker (or `minikube image load`), then:

```bash
kubectl apply -k ADS/k8s
```

Full steps (build tags, port-forward vs NodePort, CORS, teardown) are in **`ADS/k8s/README.md`**.

---

## Local development (without Docker for the app)

Use this when you want hot reload and your own Postgres on **`localhost:5432`** (install Postgres locally, or e.g. `docker run -d --name postgresdb -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:16`).

### 1) Database

Start PostgreSQL so it is reachable at **`localhost:5432`** (credentials should match `ads-backend/src/main/resources/application.yml`, typically user/password **`postgres`** / **`postgres`**, database **`postgres`**).

### 2) Backend

```bash
cd ADS/ads-backend
mvn spring-boot:run
```

API base URL: **http://localhost:8080**

### 3) Frontend

In a second terminal:

```bash
cd ADS/ads-frontend
npm install
npm run dev
```

Open **http://localhost:5173/** — Vite proxies **`/api`** to **http://localhost:8080** (see `vite.config.ts`).

---

## Default login users (seeded in the backend)

| Role            | Username   | Password   |
|-----------------|------------|------------|
| Office Manager  | `manager`  | `password` |
| Dentist         | `dentist1` | `password` |
| Patient         | `patient1` | `password` |

---

## More documentation

- **Course evaluation mapping (180-point checklist):** **`EVALUATION_CHECKLIST.md`**
- Backend details, tests, API examples: **`ads-backend/README.md`**
- **Problem statement:** **`docs/ProblemStatement.md`**
- **Requirements + use cases + traceability:** **`docs/ProjectSpecification.md`**, **`docs/UseCases.md`**
- **Domain model (class diagram):** **`docs/DomainModel.md`**
- **Architecture diagrams:** **`docs/Architecture.md`**
- **Database ER diagram:** **`docs/DatabaseDesign.md`**
- **Deployment / cloud notes:** **`docs/DeliveryDeployment.md`**
