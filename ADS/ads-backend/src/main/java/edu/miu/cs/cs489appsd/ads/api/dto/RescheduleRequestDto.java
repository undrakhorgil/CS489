package edu.miu.cs.cs489appsd.ads.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleRequestDto(@NotNull LocalDateTime proposedStartAt) {
}
