package edu.miu.cs.cs489appsd.ads.graphql.input;

import edu.miu.cs.cs489appsd.ads.domain.AppointmentRequestChannel;

import java.time.LocalDateTime;

public record AppointmentRequestInput(
        Long dentistId,
        Long surgeryId,
        LocalDateTime startAt,
        AppointmentRequestChannel channel
) {
}

