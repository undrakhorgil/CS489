package edu.miu.cs.cs489appsd.ads.web.dto;

import edu.miu.cs.cs489appsd.ads.domain.AppointmentRequestChannel;
import edu.miu.cs.cs489appsd.ads.domain.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentDetailResponse(
        Long appointmentId,
        AppointmentStatus status,
        AppointmentRequestChannel channel,
        LocalDateTime startAt,
        LocalDateTime proposedStartAt,
        DentistResponse dentist,
        PatientResponse patient,
        SurgeryResponse surgery
) {
}
