package edu.miu.cs.cs489.lab7b.welcome.graphql.input;

public record NewDentist(
        String firstName,
        String lastName,
        String contactPhoneNumber,
        String email,
        String specialization
) {
}

