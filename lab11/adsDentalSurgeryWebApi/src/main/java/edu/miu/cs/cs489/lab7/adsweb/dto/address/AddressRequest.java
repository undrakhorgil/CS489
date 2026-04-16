package edu.miu.cs.cs489.lab7.adsweb.dto.address;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String state,
        String zipCode
) {
}
