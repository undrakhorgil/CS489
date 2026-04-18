# Project specification — ADS

## 1. Scope

In scope: REST API (`/api/v1/**`), relational persistence, JWT authentication, role-based authorization, React SPA, Docker Compose stack, GitHub Actions CI, unit and integration tests.

Out of scope (for this course version): payment processing, multi-tenant SaaS, native mobile apps, full HL7 integration.

## 2. Stakeholders and roles

| Role | Typical user | System access |
|------|----------------|----------------|
| PATIENT | Registered patient | Own appointments; booking; cancel/reschedule requests. |
| DENTIST | Provider | Own schedule; confirm/reject; cancel/approve actions on own patients’ visits. |
| OFFICE_MANAGER | Front desk / admin | Directory (patients, dentists, surgeries); all appointments; billing; staff booking. |

## 3. Functional requirements (summary)

- **FR-1** Persist patients, dentists, surgeries, addresses, appointments, bills, user accounts, roles.
- **FR-2** Enforce appointment status transitions and scheduling policies in the service layer.
- **FR-3** Expose REST resources for auth, patient portal, dentist portal, office portal, and health.
- **FR-4** Web UI for login and role-specific portals (patient calendar booking, dentist table actions, office tabs).
- **FR-5** Seed demo users for grading and demos.

Detailed narratives: **`UseCases.md`**. Problem framing: **`ProblemStatement.md`**.

## 4. Non-functional requirements

- **NFR-1 Security**: Passwords hashed; JWT for API; method-level role checks.
- **NFR-2 Reliability**: Health endpoint; Compose health checks for startup ordering.
- **NFR-3 Maintainability**: Layered packages; DTOs for API boundaries; mapper for responses.
- **NFR-4 Quality**: Automated tests on every push (CI).

## 5. Traceability

| FR / NFR | Primary implementation |
|----------|-------------------------|
| FR-1, persistence | JPA entities under `domain/`, repositories under `repository/` |
| FR-2 | `service/*`, e.g. `AppointmentService`, `DentistSchedulePolicy` |
| FR-3 | `api/*Controller`, `api/dto/*` |
| FR-4 | `ADS/ads-frontend/` |
| NFR-1 | `security/`, JWT filter, `SecurityConfig` |
| NFR-4 | `.github/workflows/ads-ci.yml`, `mvn test` |

## 6. Related design documents

| Topic | Document |
|-------|-----------|
| Use cases (table) | `UseCases.md` |
| Domain classes | `DomainModel.md` |
| Solution architecture | `Architecture.md` |
| Database ER | `DatabaseDesign.md` |
| Deployment | `DeliveryDeployment.md` |
