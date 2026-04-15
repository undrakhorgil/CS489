package edu.miu.cs.cs489.lab7b.adsgraphql.graphql.input;

import java.time.LocalDate;

public record NewPatient(
        String firstName,
        String lastName,
        String contactPhoneNumber,
        String email,
        String mailingAddress,
        LocalDate dateOfBirth
) {
}

