package edu.miu.cs.cs489appsd.ads.graphql.input;

import java.time.LocalDateTime;

public record RescheduleRequestInput(
        LocalDateTime proposedStartAt
) {
}

