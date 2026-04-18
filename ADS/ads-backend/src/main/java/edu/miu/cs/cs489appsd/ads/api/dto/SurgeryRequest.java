package edu.miu.cs.cs489appsd.ads.api.dto;

import jakarta.validation.constraints.NotBlank;

public record SurgeryRequest(
        @NotBlank String name,
        @NotBlank String locationAddress,
        @NotBlank String telephoneNumber
) {
}
