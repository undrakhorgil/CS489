-- CS489 - Applied Software Development
-- Lab5a (April 2026) - ADS Dental Surgeries database script
--
-- RDBMS: SQLite (portable single-file relational database)
-- This script:
--  - creates tables (ER model -> relational schema)
--  - inserts dummy data
--  - contains the required SQL queries

PRAGMA foreign_keys = ON;

-- ============================================================
-- 1) Drop existing objects (safe re-run)
-- ============================================================
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS surgeries;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS dentists;

-- ============================================================
-- 2) Create schema (ER model implementation)
-- ============================================================

-- Dentist
CREATE TABLE dentists (
  dentist_id            INTEGER PRIMARY KEY,
  first_name            TEXT NOT NULL,
  last_name             TEXT NOT NULL,
  contact_phone_number  TEXT NOT NULL,
  email                 TEXT NOT NULL UNIQUE,
  specialization        TEXT NOT NULL
);

-- Patient
CREATE TABLE patients (
  patient_id            INTEGER PRIMARY KEY,
  first_name            TEXT NOT NULL,
  last_name             TEXT NOT NULL,
  contact_phone_number  TEXT NOT NULL,
  email                 TEXT NOT NULL UNIQUE,
  mailing_address       TEXT NOT NULL,
  date_of_birth         TEXT NOT NULL -- ISO date: YYYY-MM-DD
);

-- Surgery location
CREATE TABLE surgeries (
  surgery_id            INTEGER PRIMARY KEY,
  name                  TEXT NOT NULL,
  location_address      TEXT NOT NULL,
  telephone_number      TEXT NOT NULL
);

-- Appointment
CREATE TABLE appointments (
  appointment_id        INTEGER PRIMARY KEY,
  patient_id            INTEGER NOT NULL,
  dentist_id            INTEGER NOT NULL,
  surgery_id            INTEGER NOT NULL,
  start_at              TEXT NOT NULL, -- ISO datetime: YYYY-MM-DD HH:MM:SS
  proposed_start_at     TEXT NULL,     -- optional
  status                TEXT NOT NULL CHECK (status IN ('REQUESTED','SCHEDULED','CANCELLED','RESCHEDULE_REQUESTED','COMPLETED')),
  channel               TEXT NOT NULL CHECK (channel IN ('PHONE','ONLINE')),
  CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
  CONSTRAINT fk_appt_dentist  FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id),
  CONSTRAINT fk_appt_surgery  FOREIGN KEY (surgery_id) REFERENCES surgeries(surgery_id)
);

-- Bill (used by business rule "unpaid bill gate")
CREATE TABLE bills (
  bill_id               INTEGER PRIMARY KEY,
  patient_id            INTEGER NOT NULL,
  amount                NUMERIC NOT NULL,
  due_date              TEXT NOT NULL, -- ISO date: YYYY-MM-DD
  paid                  INTEGER NOT NULL CHECK (paid IN (0,1)),
  CONSTRAINT fk_bill_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
);

CREATE INDEX idx_appointments_dentist_id ON appointments(dentist_id);
CREATE INDEX idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX idx_appointments_surgery_id ON appointments(surgery_id);
CREATE INDEX idx_appointments_start_at   ON appointments(start_at);
CREATE INDEX idx_bills_patient_id        ON bills(patient_id);

-- ============================================================
-- 3) Dummy data
-- ============================================================

INSERT INTO dentists (dentist_id, first_name, last_name, contact_phone_number, email, specialization) VALUES
  (101, 'Amelia', 'Brown',   '515-555-0101', 'amelia.brown@ads.com',   'Orthodontics'),
  (102, 'Noah',   'Carter',  '515-555-0102', 'noah.carter@ads.com',   'Endodontics'),
  (103, 'Olivia', 'Davis',   '515-555-0103', 'olivia.davis@ads.com',  'Pediatric Dentistry'),
  (104, 'Liam',   'Evans',   '515-555-0104', 'liam.evans@ads.com',    'Periodontics'),
  (105, 'Sophia', 'Nguyen',  '515-555-0105', 'sophia.nguyen@ads.com', 'General Dentistry');

INSERT INTO patients (patient_id, first_name, last_name, contact_phone_number, email, mailing_address, date_of_birth) VALUES
  (201, 'Ethan',  'Miller',  '515-555-0201', 'ethan.miller@gmail.com',  '1000 N 4th St, Fairfield, IA', '1997-06-11'),
  (202, 'Ava',    'Wilson',  '515-555-0202', 'ava.wilson@gmail.com',    '12 S Main St, Ottumwa, IA',    '1990-02-19'),
  (203, 'Mason',  'Taylor',  '515-555-0203', 'mason.taylor@gmail.com',  '55 W Jefferson, Des Moines, IA','1985-11-03'),
  (204, 'Isla',   'Anderson','515-555-0204', 'isla.anderson@gmail.com', '9 Cherry Ln, Ames, IA',        '2001-09-27');

INSERT INTO surgeries (surgery_id, name, location_address, telephone_number) VALUES
  (301, 'ADS - Fairfield',  '2000 W Burlington Ave, Fairfield, IA', '515-555-0301'),
  (302, 'ADS - Ottumwa',    '22 S Market St, Ottumwa, IA',         '515-555-0302'),
  (303, 'ADS - Des Moines', '77 E Grand Ave, Des Moines, IA',      '515-555-0303');

-- A mix of scheduled/cancelled/reschedule requested appointments
INSERT INTO appointments (appointment_id, patient_id, dentist_id, surgery_id, start_at, proposed_start_at, status, channel) VALUES
  (401, 201, 101, 301, '2026-04-10 09:00:00', NULL,                 'SCHEDULED',            'PHONE'),
  (402, 202, 101, 302, '2026-04-11 10:30:00', NULL,                 'SCHEDULED',            'ONLINE'),
  (403, 203, 102, 303, '2026-04-10 13:00:00', NULL,                 'CANCELLED',            'PHONE'),
  (404, 204, 103, 301, '2026-04-12 08:15:00', '2026-04-13 08:15:00', 'RESCHEDULE_REQUESTED','ONLINE'),
  (405, 201, 104, 303, '2026-04-15 11:00:00', NULL,                 'SCHEDULED',            'ONLINE'),
  (406, 202, 105, 302, '2026-04-15 14:00:00', NULL,                 'SCHEDULED',            'PHONE');

-- Bills (patient 203 has an unpaid bill)
INSERT INTO bills (bill_id, patient_id, amount, due_date, paid) VALUES
  (501, 201,  95.00, '2026-03-20', 1),
  (502, 202, 120.00, '2026-03-25', 1),
  (503, 203, 250.00, '2026-03-15', 0),
  (504, 204,  80.00, '2026-03-30', 1);

-- ============================================================
-- 4) Required queries
-- ============================================================

-- Q1) Display the list of ALL Dentists registered in the system,
--     sorted in ascending order of their lastNames
SELECT dentist_id, first_name, last_name, contact_phone_number, email, specialization
FROM dentists
ORDER BY last_name ASC, first_name ASC;

-- Q2) Display the list of ALL Appointments for a given Dentist by their dentist_Id number.
--     Include in the result, the Patient information.
-- Example input: dentist_id = 101
SELECT
  a.appointment_id,
  a.start_at,
  a.status,
  a.channel,
  p.patient_id,
  p.first_name  AS patient_first_name,
  p.last_name   AS patient_last_name,
  p.contact_phone_number AS patient_phone,
  p.email       AS patient_email
FROM appointments a
JOIN patients p ON p.patient_id = a.patient_id
WHERE a.dentist_id = 101
ORDER BY a.start_at;

-- Q3) Display the list of ALL Appointments that have been scheduled at a Surgery Location
SELECT
  a.appointment_id,
  a.start_at,
  a.status,
  s.surgery_id,
  s.name AS surgery_name,
  s.location_address,
  d.dentist_id,
  d.first_name AS dentist_first_name,
  d.last_name  AS dentist_last_name,
  p.patient_id,
  p.first_name AS patient_first_name,
  p.last_name  AS patient_last_name
FROM appointments a
JOIN surgeries s ON s.surgery_id = a.surgery_id
JOIN dentists  d ON d.dentist_id = a.dentist_id
JOIN patients  p ON p.patient_id = a.patient_id
WHERE a.status = 'SCHEDULED'
ORDER BY a.start_at;

-- Q4) Display the list of the Appointments booked for a given Patient on a given Date.
-- Example inputs: patient_id = 202, date = '2026-04-15'
SELECT
  a.appointment_id,
  a.start_at,
  a.status,
  d.dentist_id,
  d.first_name AS dentist_first_name,
  d.last_name  AS dentist_last_name,
  s.surgery_id,
  s.name AS surgery_name
FROM appointments a
JOIN dentists d  ON d.dentist_id = a.dentist_id
JOIN surgeries s ON s.surgery_id = a.surgery_id
WHERE a.patient_id = 202
  AND date(a.start_at) = '2026-04-15'
ORDER BY a.start_at;

