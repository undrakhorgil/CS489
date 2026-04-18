# CS489 evaluation checklist — mapping to this project (ADS)

This file maps the **180-point** rubric items to **evidence in the repository** so graders can find them quickly.

| Item | Points | Where to find it in this repo |
|------|--------|--------------------------------|
| Creativity and originality | 10 | Product scope + office manager tabbed UX + direct BOOKED booking; see `ADS/README.md` and `ads-frontend/src/App.tsx`. |
| Enterprise solution design and technology | 10 | Spring Boot layered architecture, PostgreSQL, JWT, React SPA — `ADS/docs/Architecture.md`, `ads-backend/README.md`. |
| Functionality and user experience | 10 | Role-based portals (patient / dentist / office); run `docker compose` per `ADS/README.md`. |
| Communication and project / time management | 10 | **Instructor-facing:** this checklist + dated docs under `ADS/docs/`; Git history on GitHub. |
| Problem statement | 10 | `ADS/docs/ProblemStatement.md` |
| Requirements — use-cases | 10 | `ADS/docs/UseCases.md` (+ summary in `ADS/docs/ProjectSpecification.md`) |
| Analysis / design — domain model class diagram | 10 | `ADS/docs/DomainModel.md` (Mermaid) |
| Architecture — high-level architecture diagram | 10 | `ADS/docs/Architecture.md` (Mermaid) |
| Database design — ER diagram | 10 | `ADS/docs/DatabaseDesign.md` (Mermaid) |
| Use of Git / GitHub repository | 10 | Remote repository (push this `CS489` tree); `.gitignore` for build artifacts. |
| Setup CI/CD automation | 10 | `.github/workflows/ads-ci.yml` (backend tests + frontend build). |
| DTOs (or similar) | 10 | `ads-backend/src/main/java/.../api/dto/` — Java `record` request/response types; validation with Jakarta Validation. |
| Security — user authentication | 10 | JWT login `AuthController`, `security/SecurityConfig`, `JwtAuthenticationFilter`; seeded users in `ADS/README.md`. |
| Testing — unit test(s) | 10 | `ads-backend/src/test/java/.../service/*Test.java` (e.g. `RegistrationServiceTest`, `DentistSchedulePolicyTest`). |
| Testing — integration test(s) | 10 | `ads-backend/src/test/java/.../api/AuthIntegrationTest.java` (MockMvc + security). |
| Project deployment — Docker / cloud | 20 | `ADS/docker-compose.yml`, `ADS/DOCKER.md`, `ADS/ads-backend/Dockerfile`, `ADS/ads-frontend/Dockerfile`; `ADS/docs/DeliveryDeployment.md`. |
| Project presentation / demo | 10 | Use **Quick start** in `ADS/README.md` + default logins; optional slide outline from `ADS/docs/UseCases.md`. |

**Total:** 180 points (as provided on the course checklist).

## Quick links

- Root project readme: `ADS/README.md`
- Backend readme (API, tests): `ADS/ads-backend/README.md`
- All specification / design PDF substitutes: `ADS/docs/*.md`
