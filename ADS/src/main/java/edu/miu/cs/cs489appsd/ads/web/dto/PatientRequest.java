package edu.miu.cs.cs489appsd.ads.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String contactPhoneNumber,
        @Email String email,
        @NotBlank String mailingAddress,
        @NotNull LocalDate dateOfBirth
) {
}
