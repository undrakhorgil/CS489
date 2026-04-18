package edu.miu.cs.cs489appsd.ads.api.dto;

public record DentistResponse(
        Long dentistId,
        String firstName,
        String lastName,
        String contactPhoneNumber,
        String email,
        String specialization
) {
}
