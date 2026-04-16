## Delivery & Deployment (Course Topic: Software Delivery)

This document captures a practical path to deliver ADS beyond local development.

### 1) Configuration strategy

Use environment-specific configuration via Spring profiles:

- **Local dev**: `application.yml` (connects to local Postgres)
- **Test**: `src/test/resources/application-test.yml`
- **Cloud**: use environment variables (preferred) or `application-prod.yml` (not committed with secrets)

Recommended sensitive values to supply via environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `ADS_SECURITY_JWT_SECRET`

Example:

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://<host>:5432/<db>"
export SPRING_DATASOURCE_USERNAME="..."
export SPRING_DATASOURCE_PASSWORD="..."
export ADS_SECURITY_JWT_SECRET="a-long-random-secret-at-least-32-chars"
```

### 2) Build artifact

Produce a runnable jar:

```bash
cd ADS
mvn clean package
```

The jar will be in `target/`.

### 3) Deploying to Azure (typical approach)

You can deploy a Spring Boot app to Azure using:

- **Azure App Service** (Java runtime) for the application
- **Azure Database for PostgreSQL** (managed Postgres) for data

High-level steps:

- Create a managed Postgres instance and database
- Configure networking/firewall rules to allow your App Service to reach the database
- Set App Service application settings (environment variables) for datasource + JWT secret
- Deploy the jar (or deploy a Docker image if using container deployment)

### 4) Notes on database migrations

For a production-grade deployment, prefer database migrations (Flyway/Liquibase) instead of `ddl-auto=create-drop`.
This repository keeps `create-drop` for course/lab-style iteration; for production switch to `validate` or `update` plus migrations.

