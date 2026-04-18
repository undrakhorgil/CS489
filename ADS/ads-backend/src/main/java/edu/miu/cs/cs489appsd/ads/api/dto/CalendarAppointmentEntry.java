package edu.miu.cs.cs489appsd.ads.api.dto;

import edu.miu.cs.cs489appsd.ads.domain.AppointmentStatus;

import java.time.LocalDateTime;

public record CalendarAppointmentEntry(
        LocalDateTime startAt,
        AppointmentStatus status,
        Long surgeryId,
        String surgeryName,
        /** Non-null when {@link MonthScheduleKind#FOR_DENTIST_SELF}; otherwise null for privacy. */
        String patientName
) {
}
