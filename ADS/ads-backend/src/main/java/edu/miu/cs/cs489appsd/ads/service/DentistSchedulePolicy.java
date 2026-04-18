package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Appointment;
import edu.miu.cs.cs489appsd.ads.domain.AppointmentStatus;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

/**
 * Enforces: a dentist cannot have more than 5 booked appointments in any ISO calendar week.
 */
public final class DentistSchedulePolicy {

    private static final WeekFields ISO_WEEK = WeekFields.of(Locale.getDefault());
    public static final int MAX_APPOINTMENTS_PER_WEEK = 5;

    private DentistSchedulePolicy() {
    }

    public static boolean sameWeek(LocalDateTime a, LocalDateTime b) {
        int y1 = a.get(ISO_WEEK.weekBasedYear());
        int w1 = a.get(ISO_WEEK.weekOfWeekBasedYear());
        int y2 = b.get(ISO_WEEK.weekBasedYear());
        int w2 = b.get(ISO_WEEK.weekOfWeekBasedYear());
        return y1 == y2 && w1 == w2;
    }

    public static long countBookedInSameWeek(List<Appointment> appointments, Long dentistId, LocalDateTime slot) {
        return appointments.stream()
                .filter(a -> dentistId.equals(a.getDentistId()))
                .filter(a -> a.getStatus() == AppointmentStatus.BOOKED)
                .filter(a -> sameWeek(a.getStartAt(), slot))
                .count();
    }

    /**
     * @param excludeAppointmentId when confirming reschedule, exclude this id from count (old slot freed)
     */
    public static long countBookedInSameWeekExcluding(
            List<Appointment> appointments,
            Long dentistId,
            LocalDateTime slot,
            Long excludeAppointmentId
    ) {
        return appointments.stream()
                .filter(a -> !a.getAppointmentId().equals(excludeAppointmentId))
                .filter(a -> dentistId.equals(a.getDentistId()))
                .filter(a -> a.getStatus() == AppointmentStatus.BOOKED)
                .filter(a -> sameWeek(a.getStartAt(), slot))
                .count();
    }
}
