package edu.miu.cs.cs489appsd.ads.api;

import edu.miu.cs.cs489appsd.ads.security.AdsUserDetails;
import edu.miu.cs.cs489appsd.ads.service.AppointmentService;
import edu.miu.cs.cs489appsd.ads.api.dto.AppointmentDetailResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.DayScheduleResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.MonthScheduleKind;
import edu.miu.cs.cs489appsd.ads.api.support.PortalAccess;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dentist")
public class DentistPortalController {

    private final AppointmentService appointmentService;

    public DentistPortalController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointments")
    public List<AppointmentDetailResponse> myAppointments(@AuthenticationPrincipal AdsUserDetails user) {
        return appointmentService.listForDentist(PortalAccess.requireDentistId(user));
    }

    /** Month as {@code yyyy-MM}; this dentist's blocking appointments with times and surgery names. */
    @GetMapping("/booking/month-schedule")
    public List<DayScheduleResponse> monthSchedule(
            @AuthenticationPrincipal AdsUserDetails user,
            @RequestParam String month
    ) {
        long dentistId = PortalAccess.requireDentistId(user);
        return appointmentService.dentistMonthSchedule(dentistId, YearMonth.parse(month),
                MonthScheduleKind.FOR_DENTIST_SELF);
    }

    @PostMapping("/appointments/{appointmentId}/confirm-booking")
    public AppointmentDetailResponse confirmBooking(
            @AuthenticationPrincipal AdsUserDetails user,
            @PathVariable long appointmentId
    ) {
        long dentistId = PortalAccess.requireDentistId(user);
        return appointmentService.dentistConfirmBooking(dentistId, appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/approve-patient-cancel")
    public void approvePatientCancel(
            @AuthenticationPrincipal AdsUserDetails user,
            @PathVariable long appointmentId
    ) {
        long dentistId = PortalAccess.requireDentistId(user);
        appointmentService.dentistApprovePatientCancel(dentistId, appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/approve-reschedule")
    public AppointmentDetailResponse approveReschedule(
            @AuthenticationPrincipal AdsUserDetails user,
            @PathVariable long appointmentId
    ) {
        long dentistId = PortalAccess.requireDentistId(user);
        return appointmentService.dentistApproveReschedule(dentistId, appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/reject-reschedule")
    public void rejectReschedule(
            @AuthenticationPrincipal AdsUserDetails user,
            @PathVariable long appointmentId
    ) {
        long dentistId = PortalAccess.requireDentistId(user);
        appointmentService.dentistRejectRescheduleRequest(dentistId, appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/cancel-visit")
    public void cancelVisitOrRejectRequest(
            @AuthenticationPrincipal AdsUserDetails user,
            @PathVariable long appointmentId
    ) {
        long dentistId = PortalAccess.requireDentistId(user);
        appointmentService.dentistCancelVisitOrRejectRequest(dentistId, appointmentId);
    }
}
