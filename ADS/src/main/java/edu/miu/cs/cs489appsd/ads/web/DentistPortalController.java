package edu.miu.cs.cs489appsd.ads.web;

import edu.miu.cs.cs489appsd.ads.security.AdsUserDetails;
import edu.miu.cs.cs489appsd.ads.service.AppointmentService;
import edu.miu.cs.cs489appsd.ads.web.dto.AppointmentDetailResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return appointmentService.listForDentist(user.getDentistId());
    }
}
