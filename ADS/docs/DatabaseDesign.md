# Database design — ER diagram (logical)

Physical tables follow JPA `@Table` names. Relationships below match **foreign key columns** on child tables (some are plain `Long` columns without JPA `@ManyToOne` on `Appointment` / `Bill` for simplicity in this codebase).

```mermaid
erDiagram
    addresses {
        bigint address_id PK
        varchar street
        varchar city
        varchar state
        varchar zip_code
    }

    roles {
        bigint role_id PK
        varchar name UK
    }

    patients {
        bigint patient_id PK
        varchar first_name
        varchar last_name
        varchar email UK
        bigint mailing_address_id FK
        date date_of_birth
    }

    dentists {
        bigint dentist_id PK
        varchar first_name
        varchar last_name
        varchar email UK
        varchar specialization
    }

    surgeries {
        bigint surgery_id PK
        varchar name
        bigint location_address_id FK
        varchar telephone_number
    }

    appointments {
        bigint appointment_id PK
        bigint patient_id FK
        bigint dentist_id FK
        bigint surgery_id FK
        timestamp start_at
        timestamp proposed_start_at
        varchar status
        varchar channel
    }

    bills {
        bigint bill_id PK
        bigint patient_id FK
        decimal amount
        date due_date
        boolean paid
    }

    users {
        bigint user_id PK
        varchar username UK
        varchar password_hash
        bigint role_id FK
        bigint dentist_id
        bigint patient_id
    }

    addresses ||--o{ patients : "mailing_address_id"
    addresses ||--o{ surgeries : "location_address_id"
    patients ||--o{ appointments : "patient_id"
    dentists ||--o{ appointments : "dentist_id"
    surgeries ||--o{ appointments : "surgery_id"
    patients ||--o{ bills : "patient_id"
    roles ||--o{ users : "role_id"
```

## Notes

- **Account** entity maps to table **`users`** (legacy naming in JPA).
- **Appointment** stores `patient_id`, `dentist_id`, `surgery_id` as scalars (no JPA association graph on the entity).
- **Address** is shared pattern for patient mailing and surgery location.
