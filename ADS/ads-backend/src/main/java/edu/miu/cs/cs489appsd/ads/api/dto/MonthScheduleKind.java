package edu.miu.cs.cs489appsd.ads.api.dto;

/**
 * How month schedule entries are built for a dentist's calendar.
 */
public enum MonthScheduleKind {
    /** Patient booking UI: blocking visits only, no other patients' names, excludes cancelled. */
    FOR_PATIENT_BOOKING_VIEW,
    /** Dentist's own calendar: patient names, includes cancelled and all statuses. */
    FOR_DENTIST_SELF
}
