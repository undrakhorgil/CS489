package edu.miu.cs.cs489appsd.ads.api.dto;

import edu.miu.cs.cs489appsd.ads.domain.AppointmentRequestChannel;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequestDto(
        @NotNull Long dentistId,
        @NotNull Long surgeryId,
        @NotNull LocalDateTime startAt,
        @NotNull AppointmentRequestChannel channel
) {
}
