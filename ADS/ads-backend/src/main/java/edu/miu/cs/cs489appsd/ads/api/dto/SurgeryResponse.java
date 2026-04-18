package edu.miu.cs.cs489appsd.ads.api.dto;

public record SurgeryResponse(
        Long surgeryId,
        String name,
        String locationAddress,
        String telephoneNumber
) {
}
