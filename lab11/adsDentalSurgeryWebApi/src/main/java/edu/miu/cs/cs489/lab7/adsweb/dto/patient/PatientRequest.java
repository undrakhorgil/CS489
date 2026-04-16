package edu.miu.cs.cs489.lab7.adsweb.dto.patient;

import edu.miu.cs.cs489.lab7.adsweb.dto.address.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String contactPhoneNumber,
        @NotBlank @Email String email,
        @NotNull LocalDate dateOfBirth,
        String patientRef,
        @NotNull @Valid AddressRequest primaryAddress
) {
}
