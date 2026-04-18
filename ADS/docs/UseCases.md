# Requirements — primary use cases

Actors: **Patient**, **Dentist**, **Office manager**, **System** (email notifications, validation).

| ID | Use case | Primary actor | Summary |
|----|-----------|---------------|---------|
| UC-01 | Register surgery | Office manager | Create a surgery site with name, address, phone. |
| UC-02 | Register dentist | Office manager | Add dentist profile (name, contact, specialization). |
| UC-03 | Enroll patient | Office manager | Add patient with demographics and mailing address. |
| UC-04 | Sign in | All | Authenticate; receive JWT for subsequent API calls. |
| UC-05 | View own appointments | Patient, Dentist | List appointments relevant to the signed-in user. |
| UC-06 | Request appointment | Patient | Choose dentist, surgery, slot; create REQUESTED visit (subject to unpaid bill rule). |
| UC-07 | Book confirmed visit | Office manager | Choose patient, dentist, surgery, slot; create BOOKED visit immediately (same scheduling rules). |
| UC-08 | View month schedule | Patient, Dentist | Calendar-oriented view of busy times (privacy rules differ by role). |
| UC-09 | View all appointments (directory) | Office manager | List and month overview of every appointment; act on rows. |
| UC-10 | Confirm / reject request | Dentist, Office manager | Move REQUESTED to BOOKED or reject. |
| UC-11 | Cancel visit / approve cancel | Dentist, Office manager | Cancel flow and patient cancel approval. |
| UC-12 | Reschedule flow | Patient, Dentist, Office manager | Propose new time; approve or reject. |
| UC-13 | Record bill | Office manager | Attach bill to patient; unpaid bill blocks new patient booking. |
| UC-14 | Health check | Operations | `GET /api/v1/health` for readiness in Docker. |

**Extensions** (non-functional, cross-cutting): input validation (Bean Validation on DTOs), structured errors, CORS configuration for separate front-end origin.
