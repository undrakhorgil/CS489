package edu.miu.cs.cs489.lab7b.welcome.graphql.input;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NewBill(
        Long patientId,
        BigDecimal amount,
        LocalDate dueDate,
        Boolean paid
) {
}

