## Lab 9 — ADS Dental Surgery Appointment Web API (JWT Security)

This project (`lab9/adsDentalSurgeryWebApi`) is a new version of the ADS Web API with:

- **Token-based Authentication** using **JWT**
- **Role-based Authorization** using **Spring Security**

### Roles and protected routes

- **OFFICE_MANAGER** → `/api/v1/office/**`
- **DENTIST** → `/api/v1/dentist/**`
- **PATIENT** → `/api/v1/patient/**`
- Public: `/api/v1/health`, `/api/v1/auth/login`

### Default users (seeded on startup)

- `manager` / `password` (OFFICE_MANAGER)
- `dentist1` / `password` (DENTIST)
- `patient1` / `password` (PATIENT)

### Run with PostgreSQL (Docker)

Create/start a Postgres container and database `ads_lab9_db`, then run:

```bash
cd lab9/adsDentalSurgeryWebApi
mvn spring-boot:run
```

Connection settings are in `src/main/resources/application.yml`.

### Get a JWT

```bash
curl -s -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"password"}'
```

### Call a protected endpoint

```bash
TOKEN="PASTE_ACCESS_TOKEN_HERE"
curl -s "http://localhost:8080/api/v1/office/dashboard" \
  -H "Authorization: Bearer ${TOKEN}"
```

