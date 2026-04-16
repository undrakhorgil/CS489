## ADS — Course Project Specification

### 1) Problem statement

Advantis Dental Surgeries (ADS) operates multiple surgery locations and needs a backend system to:

- Manage surgery locations
- Register dentists and patients
- Manage appointment requests, booking, rescheduling, and cancellations
- Track patient bills
- Provide secured access for different user roles (office manager, dentist, patient)

This repository implements the backend as a Spring Boot Web API with persistent storage and token-based security.

### 2) Functional requirements (high level)

- **FR1 Surgery registration**: office manager can register a surgery (name, address, phone).
- **FR2 Dentist registration**: office manager can register a dentist (name, contact details, specialization).
- **FR3 Patient enrollment**: office manager can enroll a patient (name, contact details, DOB, address).
- **FR4 Appointment request**: patient can request an appointment with dentist/surgery and a preferred time slot.
- **FR5 Appointment booking**: office manager can book an appointment from an existing request/slot.
- **FR6 Reschedule**: patient can request reschedule; office manager can confirm reschedule.
- **FR7 Cancel**: patient can request cancel; office manager can confirm cancel.
- **FR8 Billing**: office manager can record bills for a patient; patient can view bills.
- **FR9 Access control**: endpoints are secured by role.
- **FR10 Authentication**: users login and receive a JWT access token for subsequent requests.

### 3) Non-functional requirements

- **NFR1 Stateless API**: authentication uses JWT (no HTTP session).
- **NFR2 Validation**: request payloads are validated (Bean Validation).
- **NFR3 Persistence**: data stored in PostgreSQL using Spring Data JPA.
- **NFR4 Automated tests**: unit tests (fast, isolated) and integration tests (Spring Boot).
- **NFR5 Build automation**: Maven builds, tests, packages.
- **NFR6 CI**: pipeline runs build + tests on push/PR.
- **NFR7 Containerization**: app can be built/run as a Docker image with a Postgres container.

### 4) Domain model (entities and relationships)

Core entities (implemented in `src/main/java/.../domain`):

- **Surgery**
  - Attributes: name, address, phone
  - Relationships: has many appointments
- **Dentist**
  - Attributes: firstName, lastName, phone, email, specialization
  - Relationships: has many appointments
- **Patient**
  - Attributes: firstName, lastName, phone, email, address, dateOfBirth
  - Relationships: has many appointments; has many bills
- **Appointment**
  - Attributes: startAt, proposedStartAt, status, channel
  - Relationships: references dentist, patient, and surgery (stored as IDs)
- **Bill**
  - Attributes: amount, dueDate, paid
  - Relationships: references patient (stored as patientId)
- **Account + Role**
  - Account maps a login username/passwordHash to a Role, and optionally links to a Dentist/Patient record.

### 5) Solution architecture

- **Presentation layer**: REST controllers + DTOs in `web/`
- **Business layer**: services/policies in `service/`
- **Data access layer**: Spring Data repositories in `repository/`
- **Security**: JWT filter + authentication endpoint + role rules in `security/`

