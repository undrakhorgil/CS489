package edu.miu.cs.cs489appsd.ads.web.dto;

import java.time.LocalDate;

public record PatientResponse(
        Long patientId,
        String firstName,
        String lastName,
        String contactPhoneNumber,
        String email,
        String mailingAddress,
        LocalDate dateOfBirth
) {
}
