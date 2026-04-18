# Analysis / design — domain model (conceptual class diagram)

The **implementation** lives in Java under `ads-backend/src/main/java/edu/miu/cs/cs489appsd/ads/domain/`. The diagram below is a **conceptual** view aligned with those entities (attributes abbreviated).

```mermaid
classDiagram
    direction TB

    class Address {
        +Long addressId
        +String street city state zipCode
    }

    class Patient {
        +Long patientId
        +String firstName lastName
        +String contactPhoneNumber email
        +LocalDate dateOfBirth
    }

    class Dentist {
        +Long dentistId
        +String firstName lastName
        +String contactPhoneNumber email
        +String specialization
    }

    class Surgery {
        +Long surgeryId
        +String name telephoneNumber
    }

    class Appointment {
        +Long appointmentId
        +Long patientId dentistId surgeryId
        +LocalDateTime startAt proposedStartAt
        +AppointmentStatus status
        +AppointmentRequestChannel channel
    }

    class Bill {
        +Long billId
        +Long patientId
        +BigDecimal amount
        +LocalDate dueDate
        +boolean paid
    }

    class RoleEntity {
        +Long roleId
        +String name
    }

    class Account {
        +Long accountId
        +String username passwordHash
        +Long dentistId patientId
    }

    Patient "1" --> "1" Address : mailing address
    Surgery "1" --> "1" Address : location address
    Patient "1" --> "*" Appointment : scheduled for
    Dentist "1" --> "*" Appointment : performs
    Surgery "1" --> "*" Appointment : hosted at
    Patient "1" --> "*" Bill : billed
    RoleEntity "1" --> "*" Account : authorizes
    Account ..> Dentist : optional profile link
    Account ..> Patient : optional profile link
```

**Enumerations** (see `AppointmentStatus`, `AppointmentRequestChannel`, `Role` in code): drive valid status transitions and API behavior.
