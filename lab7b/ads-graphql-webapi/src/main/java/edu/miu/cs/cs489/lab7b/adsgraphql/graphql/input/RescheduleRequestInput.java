package edu.miu.cs.cs489.lab7b.adsgraphql.graphql.input;

import java.time.LocalDateTime;

public record RescheduleRequestInput(
        LocalDateTime proposedStartAt
) {
}

