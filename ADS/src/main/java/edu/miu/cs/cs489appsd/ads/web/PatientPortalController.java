package edu.miu.cs.cs489appsd.ads.web;

import edu.miu.cs.cs489appsd.ads.security.AdsUserDetails;
import edu.miu.cs.cs489appsd.ads.service.AppointmentService;
import edu.miu.cs.cs489appsd.ads.web.dto.AppointmentDetailResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.AppointmentRequestDto;
import edu.miu.cs.cs489appsd.ads.web.dto.RescheduleRequestDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientPortalController {

    private final AppointmentService appointmentService;

    public PatientPortalController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointment-requests")
    public AppointmentDetailResponse requestAppointment(
            @AuthenticationPrincipal AdsUserDetails user,
            @Valid @RequestBody AppointmentRequestDto dto
    ) {
        return appointmentService.requestAppointment(user.getPatientId(), dto);
    }

    @GetMapping("/appointments")
    public List<AppointmentDetailResponse> myAppointments(@AuthenticationPrincipal AdsUserDetails user) {
        return appointmentService.listForPatient(user.getPatientId());
    }

    @PostMapping("/appointments/{appointmentId}/cancel-request")
    public void requestCancel(
            @AuthenticationPrincipal AdsUserDetails user,
            @PathVariable long appointmentId
    ) {
        appointmentService.requestCancel(user.getPatientId(), appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/reschedule-request")
    public void requestReschedule(
            @AuthenticationPrincipal AdsUserDetails user,
            @PathVariable long appointmentId,
            @Valid @RequestBody RescheduleRequestDto dto
    ) {
        appointmentService.requestReschedule(user.getPatientId(), appointmentId, dto.proposedStartAt());
    }
}
