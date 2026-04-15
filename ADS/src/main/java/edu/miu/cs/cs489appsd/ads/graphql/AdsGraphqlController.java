package edu.miu.cs.cs489appsd.ads.graphql;

import edu.miu.cs.cs489appsd.ads.domain.Bill;
import edu.miu.cs.cs489appsd.ads.domain.Dentist;
import edu.miu.cs.cs489appsd.ads.domain.Patient;
import edu.miu.cs.cs489appsd.ads.domain.Surgery;
import edu.miu.cs.cs489appsd.ads.graphql.input.AppointmentRequestInput;
import edu.miu.cs.cs489appsd.ads.graphql.input.NewBill;
import edu.miu.cs.cs489appsd.ads.graphql.input.NewDentist;
import edu.miu.cs.cs489appsd.ads.graphql.input.NewPatient;
import edu.miu.cs.cs489appsd.ads.graphql.input.NewSurgery;
import edu.miu.cs.cs489appsd.ads.graphql.input.RescheduleRequestInput;
import edu.miu.cs.cs489appsd.ads.repository.BillRepository;
import edu.miu.cs.cs489appsd.ads.repository.DentistRepository;
import edu.miu.cs.cs489appsd.ads.repository.PatientRepository;
import edu.miu.cs.cs489appsd.ads.repository.SurgeryRepository;
import edu.miu.cs.cs489appsd.ads.service.AppointmentService;
import edu.miu.cs.cs489appsd.ads.service.BillService;
import edu.miu.cs.cs489appsd.ads.service.RegistrationService;
import edu.miu.cs.cs489appsd.ads.web.dto.AppointmentDetailResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.AppointmentRequestDto;
import edu.miu.cs.cs489appsd.ads.web.dto.BillRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.DentistRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.DentistResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.PatientRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.PatientResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.SurgeryRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.SurgeryResponse;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class AdsGraphqlController {

    private final RegistrationService registrationService;
    private final AppointmentService appointmentService;
    private final BillService billService;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final SurgeryRepository surgeryRepository;
    private final BillRepository billRepository;

    public AdsGraphqlController(RegistrationService registrationService,
                                AppointmentService appointmentService,
                                BillService billService,
                                PatientRepository patientRepository,
                                DentistRepository dentistRepository,
                                SurgeryRepository surgeryRepository,
                                BillRepository billRepository) {
        this.registrationService = registrationService;
        this.appointmentService = appointmentService;
        this.billService = billService;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.surgeryRepository = surgeryRepository;
        this.billRepository = billRepository;
    }

    @QueryMapping("allPatients")
    public List<PatientResponse> allPatients() {
        return patientRepository.findAll().stream().map(AdsGraphqlController::toPatientResponse).toList();
    }

    @QueryMapping("patientById")
    public Optional<PatientResponse> patientById(@Argument long patientId) {
        return patientRepository.findById(patientId).map(AdsGraphqlController::toPatientResponse);
    }

    @QueryMapping("allDentists")
    public List<DentistResponse> allDentists() {
        return dentistRepository.findAll().stream().map(AdsGraphqlController::toDentistResponse).toList();
    }

    @QueryMapping("dentistById")
    public Optional<DentistResponse> dentistById(@Argument long dentistId) {
        return dentistRepository.findById(dentistId).map(AdsGraphqlController::toDentistResponse);
    }

    @QueryMapping("allSurgeries")
    public List<SurgeryResponse> allSurgeries() {
        return surgeryRepository.findAll().stream().map(AdsGraphqlController::toSurgeryResponse).toList();
    }

    @QueryMapping("surgeryById")
    public Optional<SurgeryResponse> surgeryById(@Argument long surgeryId) {
        return surgeryRepository.findById(surgeryId).map(AdsGraphqlController::toSurgeryResponse);
    }

    @QueryMapping("appointmentsForPatient")
    public List<AppointmentDetailResponse> appointmentsForPatient(@Argument long patientId) {
        return appointmentService.listForPatient(patientId);
    }

    @QueryMapping("appointmentsForDentist")
    public List<AppointmentDetailResponse> appointmentsForDentist(@Argument long dentistId) {
        return appointmentService.listForDentist(dentistId);
    }

    @QueryMapping("billsForPatient")
    public List<Bill> billsForPatient(@Argument long patientId) {
        return billRepository.findByPatientId(patientId);
    }

    @MutationMapping("enrollPatient")
    public PatientResponse enrollPatient(@Argument NewPatient newPatient) {
        return registrationService.enrollPatient(new PatientRequest(
                newPatient.firstName(),
                newPatient.lastName(),
                newPatient.contactPhoneNumber(),
                newPatient.email(),
                newPatient.mailingAddress(),
                newPatient.dateOfBirth()
        ));
    }

    @MutationMapping("registerDentist")
    public DentistResponse registerDentist(@Argument NewDentist newDentist) {
        return registrationService.registerDentist(new DentistRequest(
                newDentist.firstName(),
                newDentist.lastName(),
                newDentist.contactPhoneNumber(),
                newDentist.email(),
                newDentist.specialization()
        ));
    }

    @MutationMapping("registerSurgery")
    public SurgeryResponse registerSurgery(@Argument NewSurgery newSurgery) {
        return registrationService.registerSurgery(new SurgeryRequest(
                newSurgery.name(),
                newSurgery.locationAddress(),
                newSurgery.telephoneNumber()
        ));
    }

    @MutationMapping("recordBill")
    public Bill recordBill(@Argument NewBill newBill) {
        return billService.recordBill(new BillRequest(
                newBill.patientId(),
                newBill.amount(),
                newBill.dueDate(),
                Boolean.TRUE.equals(newBill.paid())
        ));
    }

    @MutationMapping("requestAppointment")
    public AppointmentDetailResponse requestAppointment(
            @Argument long patientId,
            @Argument("request") AppointmentRequestInput request
    ) {
        return appointmentService.requestAppointment(patientId, new AppointmentRequestDto(
                request.dentistId(),
                request.surgeryId(),
                request.startAt(),
                request.channel()
        ));
    }

    @MutationMapping("bookAppointment")
    public AppointmentDetailResponse bookAppointment(@Argument long appointmentId) {
        return appointmentService.bookAppointment(appointmentId);
    }

    @MutationMapping("requestCancel")
    public boolean requestCancel(@Argument long patientId, @Argument long appointmentId) {
        appointmentService.requestCancel(patientId, appointmentId);
        return true;
    }

    @MutationMapping("requestReschedule")
    public boolean requestReschedule(
            @Argument long patientId,
            @Argument long appointmentId,
            @Argument("request") RescheduleRequestInput request
    ) {
        appointmentService.requestReschedule(patientId, appointmentId, request.proposedStartAt());
        return true;
    }

    @MutationMapping("confirmReschedule")
    public AppointmentDetailResponse confirmReschedule(@Argument long appointmentId) {
        return appointmentService.confirmReschedule(appointmentId);
    }

    @MutationMapping("confirmCancel")
    public boolean confirmCancel(@Argument long appointmentId) {
        appointmentService.confirmCancel(appointmentId);
        return true;
    }

    private static DentistResponse toDentistResponse(Dentist d) {
        return new DentistResponse(
                d.getDentistId(),
                d.getFirstName(),
                d.getLastName(),
                d.getContactPhoneNumber(),
                d.getEmail(),
                d.getSpecialization()
        );
    }

    private static PatientResponse toPatientResponse(Patient p) {
        return new PatientResponse(
                p.getPatientId(),
                p.getFirstName(),
                p.getLastName(),
                p.getContactPhoneNumber(),
                p.getEmail(),
                p.getMailingAddress(),
                p.getDateOfBirth()
        );
    }

    private static SurgeryResponse toSurgeryResponse(Surgery s) {
        return new SurgeryResponse(
                s.getSurgeryId(),
                s.getName(),
                s.getLocationAddress(),
                s.getTelephoneNumber()
        );
    }
}

