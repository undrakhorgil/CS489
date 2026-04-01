package edu.miu.cs.cs489appsd.ads.web;

import edu.miu.cs.cs489appsd.ads.domain.Bill;
import edu.miu.cs.cs489appsd.ads.service.AppointmentService;
import edu.miu.cs.cs489appsd.ads.service.BillService;
import edu.miu.cs.cs489appsd.ads.service.RegistrationService;
import edu.miu.cs.cs489appsd.ads.web.dto.AppointmentDetailResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.BillRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.DentistRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.DentistResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.PatientRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.PatientResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.SurgeryRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.SurgeryResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/office")
public class OfficeController {

    private final RegistrationService registrationService;
    private final BillService billService;
    private final AppointmentService appointmentService;

    public OfficeController(RegistrationService registrationService,
                            BillService billService,
                            AppointmentService appointmentService) {
        this.registrationService = registrationService;
        this.billService = billService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/surgeries")
    public SurgeryResponse registerSurgery(@Valid @RequestBody SurgeryRequest request) {
        return registrationService.registerSurgery(request);
    }

    @PostMapping("/dentists")
    public DentistResponse registerDentist(@Valid @RequestBody DentistRequest request) {
        return registrationService.registerDentist(request);
    }

    @PostMapping("/patients")
    public PatientResponse enrollPatient(@Valid @RequestBody PatientRequest request) {
        return registrationService.enrollPatient(request);
    }

    @PostMapping("/bills")
    public Bill recordBill(@Valid @RequestBody BillRequest request) {
        return billService.recordBill(request);
    }

    @PostMapping("/appointments/{appointmentId}/book")
    public AppointmentDetailResponse book(@PathVariable long appointmentId) {
        return appointmentService.bookAppointment(appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/confirm-reschedule")
    public AppointmentDetailResponse confirmReschedule(@PathVariable long appointmentId) {
        return appointmentService.confirmReschedule(appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/confirm-cancel")
    public void confirmCancel(@PathVariable long appointmentId) {
        appointmentService.confirmCancel(appointmentId);
    }
}
