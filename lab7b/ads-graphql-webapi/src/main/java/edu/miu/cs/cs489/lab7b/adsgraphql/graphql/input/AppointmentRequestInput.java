package edu.miu.cs.cs489.lab7b.adsgraphql.graphql.input;

import edu.miu.cs.cs489.lab7b.adsgraphql.model.AppointmentRequestChannel;

import java.time.LocalDateTime;

public record AppointmentRequestInput(
        Long patientId,
        Long dentistId,
        Long surgeryId,
        LocalDateTime startAt,
        AppointmentRequestChannel channel
) {
}

