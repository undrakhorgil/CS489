package edu.miu.cs.cs489.lab7b.welcome.graphql.input;

import java.time.LocalDateTime;

public record RescheduleRequestInput(
        LocalDateTime proposedStartAt
) {
}

