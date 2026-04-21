package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Appointment;
import edu.miu.cs.cs489appsd.ads.domain.AppointmentStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DentistSchedulePolicyTest {

    @Nested
    class SameWeek {
        @Test
        void returnsTrue_whenDatesAreInSameIsoWeek() {
            // Arrange
            LocalDateTime mon = LocalDateTime.of(2026, 3, 30, 9, 0);
            LocalDateTime wed = LocalDateTime.of(2026, 4, 1, 10, 0);

            // Act
            boolean sameWeek = DentistSchedulePolicy.sameWeek(mon, wed);

            // Assert
            assertThat(sameWeek).isTrue();
        }
    }

    @Nested
    class CountBookedInSameWeek {
        @Test
        void returnsBookedCount_forSameWeek() {
            // Arrange
            long dentistId = 1L;
            LocalDateTime slot = LocalDateTime.of(2026, 4, 2, 14, 0);
            List<Appointment> list = List.of(
                    ap(1L, dentistId, AppointmentStatus.BOOKED, LocalDateTime.of(2026, 3, 30, 9, 0)),
                    ap(2L, dentistId, AppointmentStatus.BOOKED, LocalDateTime.of(2026, 4, 1, 9, 0)),
                    ap(3L, dentistId, AppointmentStatus.BOOKED, LocalDateTime.of(2026, 4, 2, 9, 0)),
                    ap(4L, dentistId, AppointmentStatus.BOOKED, LocalDateTime.of(2026, 4, 3, 9, 0)),
                    ap(5L, dentistId, AppointmentStatus.BOOKED, LocalDateTime.of(2026, 4, 4, 9, 0))
            );

            // Act
            long count = DentistSchedulePolicy.countBookedInSameWeek(list, dentistId, slot);

            // Assert
            assertThat(count).isEqualTo(5);
        }
    }

    private static Appointment ap(long id, long dentistId, AppointmentStatus status, LocalDateTime start) {
        Appointment a = new Appointment();
        a.setAppointmentId(id);
        a.setDentistId(dentistId);
        a.setPatientId(99L);
        a.setSurgeryId(1L);
        a.setStatus(status);
        a.setStartAt(start);
        return a;
    }
}
