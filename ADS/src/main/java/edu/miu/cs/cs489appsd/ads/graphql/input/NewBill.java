package edu.miu.cs.cs489appsd.ads.graphql.input;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NewBill(
        Long patientId,
        BigDecimal amount,
        LocalDate dueDate,
        Boolean paid
) {
}

