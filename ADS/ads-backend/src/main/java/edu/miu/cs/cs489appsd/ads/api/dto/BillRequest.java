package edu.miu.cs.cs489appsd.ads.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BillRequest(
        @NotNull Long patientId,
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate dueDate,
        boolean paid
) {
}
