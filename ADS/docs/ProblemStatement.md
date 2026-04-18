# Problem statement — Advantis Dental Surgeries (ADS)

## Context

Dental practices need to coordinate **patients**, **dentists**, **surgery locations**, and **appointments** while keeping schedules consistent, enforcing simple business rules (for example weekly limits), and giving each stakeholder a clear view of their work.

## Problem

Without a dedicated system, staff rely on spreadsheets or ad hoc communication. That leads to:

- Double-booking or overlapping dentist time.
- Unclear status of requests versus confirmed visits.
- Patients who cannot easily see their own schedule or request changes online.
- Weak traceability of who approved or rejected a change.

## Goal of this project

Deliver a **small enterprise-style** solution: a **Spring Boot** API with **PostgreSQL**, **JWT role-based security**, and a **React** web client. The system should support:

- Registration of surgeries, dentists, and patients (office manager).
- Patient self-service booking and viewing appointments.
- Dentist and office workflows to confirm, reject, cancel, or reschedule visits.
- Bills that can block new booking until resolved (simple rule).

This aligns with course outcomes: requirements, domain modeling, persistence, REST, security, testing, CI/CD, and containerized deployment.
