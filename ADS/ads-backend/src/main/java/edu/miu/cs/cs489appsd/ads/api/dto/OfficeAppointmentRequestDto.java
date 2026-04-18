package edu.miu.cs.cs489appsd.ads.api.dto;

import edu.miu.cs.cs489appsd.ads.domain.AppointmentRequestChannel;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/** Office manager creates an appointment request on behalf of a patient. */
public record OfficeAppointmentRequestDto(
        @NotNull Long patientId,
        @NotNull Long dentistId,
        @NotNull Long surgeryId,
        @NotNull LocalDateTime startAt,
        @NotNull AppointmentRequestChannel channel
) {
}
