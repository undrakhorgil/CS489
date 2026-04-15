package edu.miu.cs.cs489.lab7b.welcome.graphql;

import edu.miu.cs.cs489.lab7b.welcome.domain.Appointment;
import edu.miu.cs.cs489.lab7b.welcome.domain.AppointmentStatus;
import edu.miu.cs.cs489.lab7b.welcome.domain.Bill;
import edu.miu.cs.cs489.lab7b.welcome.domain.Dentist;
import edu.miu.cs.cs489.lab7b.welcome.domain.Patient;
import edu.miu.cs.cs489.lab7b.welcome.domain.Surgery;
import edu.miu.cs.cs489.lab7b.welcome.graphql.dto.AppointmentDetail;
import edu.miu.cs.cs489.lab7b.welcome.graphql.input.AppointmentRequestInput;
import edu.miu.cs.cs489.lab7b.welcome.graphql.input.NewBill;
import edu.miu.cs.cs489.lab7b.welcome.graphql.input.NewDentist;
import edu.miu.cs.cs489.lab7b.welcome.graphql.input.NewPatient;
import edu.miu.cs.cs489.lab7b.welcome.graphql.input.NewSurgery;
import edu.miu.cs.cs489.lab7b.welcome.graphql.input.RescheduleRequestInput;
import edu.miu.cs.cs489.lab7b.welcome.repository.AppointmentRepository;
import edu.miu.cs.cs489.lab7b.welcome.repository.BillRepository;
import edu.miu.cs.cs489.lab7b.welcome.repository.DentistRepository;
import edu.miu.cs.cs489.lab7b.welcome.repository.PatientRepository;
import edu.miu.cs.cs489.lab7b.welcome.repository.SurgeryRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class AdsGraphqlController {
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final SurgeryRepository surgeryRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;

    public AdsGraphqlController(
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            SurgeryRepository surgeryRepository,
            AppointmentRepository appointmentRepository,
            BillRepository billRepository
    ) {
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.surgeryRepository = surgeryRepository;
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
    }

    @QueryMapping
    public List<Patient> allPatients() {
        return patientRepository.findAll();
    }

    @QueryMapping
    public Optional<Patient> patientById(@Argument long patientId) {
        return patientRepository.findById(patientId);
    }

    @QueryMapping
    public List<Dentist> allDentists() {
        return dentistRepository.findAll();
    }

    @QueryMapping
    public Optional<Dentist> dentistById(@Argument long dentistId) {
        return dentistRepository.findById(dentistId);
    }

    @QueryMapping
    public List<Surgery> allSurgeries() {
        return surgeryRepository.findAll();
    }

    @QueryMapping
    public Optional<Surgery> surgeryById(@Argument long surgeryId) {
        return surgeryRepository.findById(surgeryId);
    }

    @QueryMapping
    public List<AppointmentDetail> appointmentsForPatient(@Argument long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream().map(this::toDetail).toList();
    }

    @QueryMapping
    public List<AppointmentDetail> appointmentsForDentist(@Argument long dentistId) {
        return appointmentRepository.findByDentistId(dentistId).stream().map(this::toDetail).toList();
    }

    @QueryMapping
    public List<Bill> billsForPatient(@Argument long patientId) {
        return billRepository.findByPatientId(patientId);
    }

    @MutationMapping
    public Patient enrollPatient(@Argument NewPatient newPatient) {
        Patient p = new Patient();
        p.setFirstName(newPatient.firstName());
        p.setLastName(newPatient.lastName());
        p.setContactPhoneNumber(newPatient.contactPhoneNumber());
        p.setEmail(newPatient.email());
        p.setMailingAddress(newPatient.mailingAddress());
        p.setDateOfBirth(newPatient.dateOfBirth());
        return patientRepository.save(p);
    }

    @MutationMapping
    public Dentist registerDentist(@Argument NewDentist newDentist) {
        Dentist d = new Dentist();
        d.setFirstName(newDentist.firstName());
        d.setLastName(newDentist.lastName());
        d.setContactPhoneNumber(newDentist.contactPhoneNumber());
        d.setEmail(newDentist.email());
        d.setSpecialization(newDentist.specialization());
        return dentistRepository.save(d);
    }

    @MutationMapping
    public Surgery registerSurgery(@Argument NewSurgery newSurgery) {
        Surgery s = new Surgery();
        s.setName(newSurgery.name());
        s.setLocationAddress(newSurgery.locationAddress());
        s.setTelephoneNumber(newSurgery.telephoneNumber());
        return surgeryRepository.save(s);
    }

    @MutationMapping
    public Bill recordBill(@Argument NewBill newBill) {
        Bill b = new Bill();
        b.setPatientId(newBill.patientId());
        b.setAmount(newBill.amount());
        b.setDueDate(newBill.dueDate());
        b.setPaid(Boolean.TRUE.equals(newBill.paid()));
        return billRepository.save(b);
    }

    @MutationMapping
    public AppointmentDetail requestAppointment(@Argument long patientId, @Argument AppointmentRequestInput request) {
        Appointment a = new Appointment();
        a.setPatientId(patientId);
        a.setDentistId(request.dentistId());
        a.setSurgeryId(request.surgeryId());
        a.setStartAt(request.startAt());
        a.setChannel(request.channel());
        a.setStatus(AppointmentStatus.REQUESTED);
        return toDetail(appointmentRepository.save(a));
    }

    @MutationMapping
    public AppointmentDetail bookAppointment(@Argument long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        a.setStatus(AppointmentStatus.BOOKED);
        return toDetail(appointmentRepository.save(a));
    }

    @MutationMapping
    public boolean requestCancel(@Argument long patientId, @Argument long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        if (!a.getPatientId().equals(patientId)) {
            return false;
        }
        a.setStatus(AppointmentStatus.CANCEL_REQUESTED);
        appointmentRepository.save(a);
        return true;
    }

    @MutationMapping
    public boolean requestReschedule(@Argument long patientId, @Argument long appointmentId, @Argument RescheduleRequestInput request) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        if (!a.getPatientId().equals(patientId)) {
            return false;
        }
        a.setStatus(AppointmentStatus.RESCHEDULE_REQUESTED);
        a.setProposedStartAt(request.proposedStartAt());
        appointmentRepository.save(a);
        return true;
    }

    @MutationMapping
    public AppointmentDetail confirmReschedule(@Argument long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        if (a.getProposedStartAt() != null) {
            a.setStartAt(a.getProposedStartAt());
            a.setProposedStartAt(null);
        }
        a.setStatus(AppointmentStatus.BOOKED);
        return toDetail(appointmentRepository.save(a));
    }

    @MutationMapping
    public boolean confirmCancel(@Argument long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        a.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(a);
        return true;
    }

    private AppointmentDetail toDetail(Appointment a) {
        Patient p = patientRepository.findById(a.getPatientId()).orElseThrow();
        Dentist d = dentistRepository.findById(a.getDentistId()).orElseThrow();
        Surgery s = surgeryRepository.findById(a.getSurgeryId()).orElseThrow();
        return new AppointmentDetail(
                a.getAppointmentId(),
                a.getStatus(),
                a.getChannel(),
                a.getStartAt(),
                a.getProposedStartAt(),
                d,
                p,
                s
        );
    }
}

