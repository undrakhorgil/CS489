package edu.miu.cs.cs489.lab7b.welcome.graphql.dto;

import edu.miu.cs.cs489.lab7b.welcome.domain.AppointmentRequestChannel;
import edu.miu.cs.cs489.lab7b.welcome.domain.AppointmentStatus;
import edu.miu.cs.cs489.lab7b.welcome.domain.Dentist;
import edu.miu.cs.cs489.lab7b.welcome.domain.Patient;
import edu.miu.cs.cs489.lab7b.welcome.domain.Surgery;

import java.time.LocalDateTime;

public record AppointmentDetail(
        Long appointmentId,
        AppointmentStatus status,
        AppointmentRequestChannel channel,
        LocalDateTime startAt,
        LocalDateTime proposedStartAt,
        Dentist dentist,
        Patient patient,
        Surgery surgery
) {
}

