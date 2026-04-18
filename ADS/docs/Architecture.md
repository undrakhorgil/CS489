# Architecture — high-level solution

## 1. Logical view (layers)

```mermaid
flowchart TB
    subgraph client [Browser]
        UI[React SPA\nVite + TypeScript]
    end

    subgraph api [Spring Boot API]
        WEB[REST controllers\n+ DTOs + validation]
        SVC[Services\nbusiness rules]
        DOM[Domain entities\nJPA]
        SEC[Spring Security\nJWT]
        WEB --> SVC
        SVC --> DOM
        WEB --> SEC
    end

    subgraph data [Data]
        PG[(PostgreSQL)]
    end

    UI -->|HTTPS JSON\nJWT Bearer| WEB
    DOM --> PG
```

## 2. Physical view (Docker Compose)

```mermaid
flowchart LR
    subgraph host [Developer machine]
        B[Browser]
    end

    subgraph stack [Docker Compose project ADS]
        N[nginx\n:8088]
        A[ads-backend\nSpring Boot :8080]
        P[(postgresdb\nPostgres 16)]
    end

    B --> N
    N -->|proxy /api| A
    A --> P
```

- **Production-style dev**: nginx serves static `dist/` and proxies `/api/` so the browser is same-origin with the API path prefix.
- **Local dev without Docker**: Vite dev server proxies `/api` to `localhost:8080`.

## 3. API surface

- **REST** base path: `/api/v1/**` (auth, patient, dentist, office, health).

## 4. Key technology choices

| Concern | Choice |
|---------|--------|
| Runtime | Java 21, Spring Boot 3 |
| API | REST + Jackson JSON; Java `record` DTOs |
| Security | JWT, stateless filters, `@EnableMethodSecurity` |
| Persistence | Spring Data JPA, Hibernate, Flyway/Liquibase not used — schema from JPA `ddl-auto` in dev |
| Front end | React 18, TypeScript, fetch to `/api/v1` |
| Build | Maven (backend), npm (frontend) |
| Automation | GitHub Actions (see repo `.github/workflows/ads-ci.yml`) |
