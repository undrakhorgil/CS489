## ADS — Advantis Dental Surgeries (Course Project)

Spring Boot backend for a Dental Surgery appointment management system.

### Topic coverage checklist (CS489)

- **Requirements + domain modeling**: `ADS/docs/ProjectSpecification.md`
- **App dev platform / architecture**: Spring Boot layered architecture (domain, repo, service, web)
- **Databases**: PostgreSQL (local Docker container `postgres1`)
- **Data persistence**: Spring Data JPA + Bean Validation
- **RESTful Web API**: `/api/v1/**`
- **Security**: Spring Security + JWT (stateless) + role-based authorization
- **Testing**: JUnit 5 (unit tests + Spring Boot integration tests)
- **Build automation**: Maven
- **CI/CD**: GitHub Actions workflow at `.github/workflows/ci.yml`
- **Containerization**: `Dockerfile` and `docker-compose.yml`
- **Delivery/Deployment**: `ADS/docs/DeliveryDeployment.md`

### Architecture (high level)

- **`domain/`**: JPA entities + enums (the domain model)
- **`repository/`**: Spring Data JPA repositories
- **`service/`**: business logic, policies, orchestration
- **`web/`**: REST controllers + DTOs + request validation
- **`security/`**: JWT auth + Spring Security configuration

GraphQL is enabled as an optional interface at `/graphql` (and GraphiQL at `/graphiql`).

### Default users (seeded at startup)

- **Office Manager**: `manager` / `password`
- **Dentist**: `dentist1` / `password`
- **Patient**: `patient1` / `password`

### Database (local Docker container `postgres1`)

This project is configured to use your local Postgres container named `postgres1` which publishes Postgres to the host at `localhost:5432`.

Relevant config is in `src/main/resources/application.yml` and `src/test/resources/application-test.yml`.

### Run locally

```bash
cd ADS
mvn spring-boot:run
```

### Health check

```bash
curl -i "http://localhost:8080/api/v1/health"
```

### Login (get JWT)

Request:

```bash
curl -s -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"password"}'
```

Response includes `accessToken` (JWT).

### Call protected endpoints

Use the JWT as a Bearer token:

```bash
TOKEN="PASTE_TOKEN_HERE"
curl -i "http://localhost:8080/api/v1/office/surgeries" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"name":"ADS - Test","address":"1 Main St","phoneNumber":"515-555-9999"}'
```

Role rules are configured in `SecurityConfig`:

- `/api/v1/office/**` requires role `OFFICE_MANAGER`
- `/api/v1/dentist/**` requires role `DENTIST`
- `/api/v1/patient/**` requires role `PATIENT`
- `/api/v1/auth/**` and `/api/v1/health` are public

### JWT settings

Configured in `src/main/resources/application.yml`:

- `ads.security.jwt.secret`
- `ads.security.jwt.expires-in-seconds`

### Run tests

```bash
cd ADS
mvn test
```

### Run on local Kubernetes (minikube)

See `ADS/k8s/README.md`.

