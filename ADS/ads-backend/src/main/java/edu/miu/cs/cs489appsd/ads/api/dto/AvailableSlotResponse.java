package edu.miu.cs.cs489appsd.ads.api.dto;

import java.time.LocalDateTime;

/** A bookable start time for online appointment requests (fixed 30-minute slots). */
public record AvailableSlotResponse(LocalDateTime startAt) {
}
