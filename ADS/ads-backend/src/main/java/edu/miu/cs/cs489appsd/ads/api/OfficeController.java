package edu.miu.cs.cs489appsd.ads.api;

import edu.miu.cs.cs489appsd.ads.domain.Bill;
import edu.miu.cs.cs489appsd.ads.service.AppointmentService;
import edu.miu.cs.cs489appsd.ads.service.BillService;
import edu.miu.cs.cs489appsd.ads.service.RegistrationService;
import edu.miu.cs.cs489appsd.ads.api.dto.AppointmentDetailResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.AppointmentRequestDto;
import edu.miu.cs.cs489appsd.ads.api.dto.AvailableSlotResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.BillRequest;
import edu.miu.cs.cs489appsd.ads.api.dto.DayScheduleResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.DentistRequest;
import edu.miu.cs.cs489appsd.ads.api.dto.DentistResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.OfficeAppointmentRequestDto;
import edu.miu.cs.cs489appsd.ads.api.dto.PatientRequest;
import edu.miu.cs.cs489appsd.ads.api.dto.PatientResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.MonthScheduleKind;
import edu.miu.cs.cs489appsd.ads.api.dto.SurgeryRequest;
import edu.miu.cs.cs489appsd.ads.api.dto.SurgeryResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/patients")
    public List<PatientResponse> listPatients() {
        return registrationService.listAllPatients();
    }

    @GetMapping("/dentists")
    public List<DentistResponse> listDentists() {
        return registrationService.listAllDentists();
    }

    @GetMapping("/surgeries")
    public List<SurgeryResponse> listSurgeries() {
        return registrationService.listAllSurgeries();
    }

    @GetMapping("/appointments")
    public List<AppointmentDetailResponse> listAppointments() {
        return appointmentService.listAllAppointments();
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

    /** Month {@code yyyy-MM}; busy schedule for calendar (no patient names). */
    @GetMapping("/booking/month-schedule")
    public List<DayScheduleResponse> monthSchedule(
            @RequestParam long dentistId,
            @RequestParam String month
    ) {
        return appointmentService.dentistMonthSchedule(dentistId, YearMonth.parse(month),
                MonthScheduleKind.FOR_PATIENT_BOOKING_VIEW);
    }

    @PostMapping("/surgeries")
    public SurgeryResponse registerSurgery(@Valid @RequestBody SurgeryRequest request) {
        return registrationService.registerSurgery(request);
    }

    @PostMapping("/appointment-requests")
    public AppointmentDetailResponse createAppointmentRequestForPatient(
            @Valid @RequestBody OfficeAppointmentRequestDto dto
    ) {
        return appointmentService.requestAppointment(
                dto.patientId(),
                new AppointmentRequestDto(dto.dentistId(), dto.surgeryId(), dto.startAt(), dto.channel())
        );
    }

    /** Creates a {@code BOOKED} appointment (office calendar / walk-in). */
    @PostMapping("/appointments/direct-book")
    public AppointmentDetailResponse directBook(@Valid @RequestBody OfficeAppointmentRequestDto dto) {
        return appointmentService.officeBookImmediately(
                dto.patientId(),
                new AppointmentRequestDto(dto.dentistId(), dto.surgeryId(), dto.startAt(), dto.channel())
        );
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

    @PostMapping("/appointments/{appointmentId}/cancel-visit")
    public void cancelVisitOrRejectRequest(@PathVariable long appointmentId) {
        appointmentService.officeCancelVisitOrRejectRequest(appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/reject-reschedule")
    public void rejectReschedule(@PathVariable long appointmentId) {
        appointmentService.officeRejectRescheduleRequest(appointmentId);
    }
}
