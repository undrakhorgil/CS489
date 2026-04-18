package edu.miu.cs.cs489appsd.ads.api.dto;

import java.util.List;

public record DayScheduleResponse(String date, List<CalendarAppointmentEntry> appointments) {
}
