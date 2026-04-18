package edu.miu.cs.cs489appsd.ads.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DentistRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String contactPhoneNumber,
        @Email String email,
        @NotBlank String specialization
) {
}
