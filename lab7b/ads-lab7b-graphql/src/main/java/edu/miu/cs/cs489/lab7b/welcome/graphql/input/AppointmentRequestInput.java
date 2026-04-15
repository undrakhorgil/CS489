package edu.miu.cs.cs489.lab7b.welcome.graphql.input;

import edu.miu.cs.cs489.lab7b.welcome.domain.AppointmentRequestChannel;

import java.time.LocalDateTime;

public record AppointmentRequestInput(
        Long dentistId,
        Long surgeryId,
        LocalDateTime startAt,
        AppointmentRequestChannel channel
) {
}

