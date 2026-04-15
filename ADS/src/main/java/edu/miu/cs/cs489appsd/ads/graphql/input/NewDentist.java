package edu.miu.cs.cs489appsd.ads.graphql.input;

public record NewDentist(
        String firstName,
        String lastName,
        String contactPhoneNumber,
        String email,
        String specialization
) {
}

