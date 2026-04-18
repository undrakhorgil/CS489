package edu.miu.cs.cs489appsd.ads.api;

import edu.miu.cs.cs489appsd.ads.security.AdsUserDetails;
import edu.miu.cs.cs489appsd.ads.service.AppointmentService;
import edu.miu.cs.cs489appsd.ads.api.dto.AppointmentDetailResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.AppointmentRequestDto;
import edu.miu.cs.cs489appsd.ads.api.dto.AvailableSlotResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.DayScheduleResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.MonthScheduleKind;
import edu.miu.cs.cs489appsd.ads.api.dto.DentistResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.RescheduleRequestDto;
import edu.miu.cs.cs489appsd.ads.api.dto.SurgeryResponse;
import edu.miu.cs.cs489appsd.ads.api.support.PortalAccess;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientPortalController {

    private final AppointmentService appointmentService;

    public PatientPortalController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/booking/dentists")
    public List<DentistResponse> listDentistsForBooking() {
        return appointmentService.listDentistsForBooking();
    }

    @GetMapping("/booking/surgeries")
    public List<SurgeryResponse> listSurgeriesForBooking() {
        return appointmentService.listSurgeriesForBooking();
    }

    @GetMapping("/booking/availability")
    public List<AvailableSlotResponse> bookingAvailability(
            @RequestParam long dentistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long surgeryId
    ) {
        return appointmentService.findAvailableSlots(dentistId, date, surgeryId);
    }

    /** Month as {@code yyyy-MM} (e.g. {@code 2026-04}); appointments include times and surgery names. */
    @GetMapping("/booking/month-schedule")
    public List<DayScheduleResponse> monthSchedule(
            @RequestParam long dentistId,
            @RequestParam String month
    ) {
        return appointmentService.dentistMonthSchedule(dentistId, YearMonth.parse(month),
                MonthScheduleKind.FOR_PATIENT_BOOKING_VIEW);
    }

    @PostMapping("/appointment-requests")
    public AppointmentDetailResponse requestAppointment(
            @AuthenticationPrincipal AdsUserDetails user,
            @Valid @RequestBody AppointmentRequestDto dto
    ) {
        return appointmentService.requestAppointment(PortalAccess.requirePatientId(user), dto);
    }

    @GetMapping("/appointments")
    public List<AppointmentDetailResponse> myAppointments(@AuthenticationPrincipal AdsUserDetails user) {
        return appointmentService.listForPatient(PortalAccess.requirePatientId(user));
    }

    @PostMapping("/appointments/{appointmentId}/cancel-request")
    public void requestCancel(
            @AuthenticationPrincipal AdsUserDetails user,
            @PathVariable long appointmentId
    ) {
        appointmentService.requestCancel(PortalAccess.requirePatientId(user), appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/reschedule-request")
    public void requestReschedule(
            @AuthenticationPrincipal AdsUserDetails user,
            @PathVariable long appointmentId,
            @Valid @RequestBody RescheduleRequestDto dto
    ) {
        appointmentService.requestReschedule(PortalAccess.requirePatientId(user), appointmentId, dto.proposedStartAt());
    }
}
