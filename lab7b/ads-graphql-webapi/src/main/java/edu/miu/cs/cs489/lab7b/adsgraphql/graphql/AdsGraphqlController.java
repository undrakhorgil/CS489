package edu.miu.cs.cs489.lab7b.adsgraphql.graphql;

import edu.miu.cs.cs489.lab7b.adsgraphql.graphql.input.AppointmentRequestInput;
import edu.miu.cs.cs489.lab7b.adsgraphql.graphql.input.NewBill;
import edu.miu.cs.cs489.lab7b.adsgraphql.graphql.input.NewDentist;
import edu.miu.cs.cs489.lab7b.adsgraphql.graphql.input.NewPatient;
import edu.miu.cs.cs489.lab7b.adsgraphql.graphql.input.NewSurgery;
import edu.miu.cs.cs489.lab7b.adsgraphql.graphql.input.RescheduleRequestInput;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.Appointment;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.AppointmentStatus;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.Bill;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.Dentist;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.Patient;
import edu.miu.cs.cs489.lab7b.adsgraphql.model.Surgery;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.AppointmentRepository;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.BillRepository;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.DentistRepository;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.PatientRepository;
import edu.miu.cs.cs489.lab7b.adsgraphql.repository.SurgeryRepository;
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

    public AdsGraphqlController(PatientRepository patientRepository,
                                DentistRepository dentistRepository,
                                SurgeryRepository surgeryRepository,
                                AppointmentRepository appointmentRepository,
                                BillRepository billRepository) {
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.surgeryRepository = surgeryRepository;
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
    }

    @QueryMapping("allPatients")
    public List<Patient> allPatients() {
        return patientRepository.findAll();
    }

    @QueryMapping("allDentists")
    public List<Dentist> allDentists() {
        return dentistRepository.findAll();
    }

    @QueryMapping("allSurgeries")
    public List<Surgery> allSurgeries() {
        return surgeryRepository.findAll();
    }

    @QueryMapping("patientById")
    public Optional<Patient> patientById(@Argument long patientId) {
        return patientRepository.findById(patientId);
    }

    @QueryMapping("dentistById")
    public Optional<Dentist> dentistById(@Argument long dentistId) {
        return dentistRepository.findById(dentistId);
    }

    @QueryMapping("surgeryById")
    public Optional<Surgery> surgeryById(@Argument long surgeryId) {
        return surgeryRepository.findById(surgeryId);
    }

    @QueryMapping("appointmentsForPatient")
    public List<Appointment> appointmentsForPatient(@Argument long patientId) {
        return appointmentRepository.findByPatient_PatientId(patientId);
    }

    @QueryMapping("appointmentsForDentist")
    public List<Appointment> appointmentsForDentist(@Argument long dentistId) {
        return appointmentRepository.findByDentist_DentistId(dentistId);
    }

    @QueryMapping("billsForPatient")
    public List<Bill> billsForPatient(@Argument long patientId) {
        return billRepository.findByPatient_PatientId(patientId);
    }

    @MutationMapping("enrollPatient")
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

    @MutationMapping("registerDentist")
    public Dentist registerDentist(@Argument NewDentist newDentist) {
        Dentist d = new Dentist();
        d.setFirstName(newDentist.firstName());
        d.setLastName(newDentist.lastName());
        d.setContactPhoneNumber(newDentist.contactPhoneNumber());
        d.setEmail(newDentist.email());
        d.setSpecialization(newDentist.specialization());
        return dentistRepository.save(d);
    }

    @MutationMapping("registerSurgery")
    public Surgery registerSurgery(@Argument NewSurgery newSurgery) {
        Surgery s = new Surgery();
        s.setName(newSurgery.name());
        s.setLocationAddress(newSurgery.locationAddress());
        s.setTelephoneNumber(newSurgery.telephoneNumber());
        return surgeryRepository.save(s);
    }

    @MutationMapping("requestAppointment")
    public Appointment requestAppointment(@Argument("request") AppointmentRequestInput req) {
        Patient p = patientRepository.findById(req.patientId()).orElseThrow();
        Dentist d = dentistRepository.findById(req.dentistId()).orElseThrow();
        Surgery s = surgeryRepository.findById(req.surgeryId()).orElseThrow();

        Appointment a = new Appointment();
        a.setPatient(p);
        a.setDentist(d);
        a.setSurgery(s);
        a.setStartAt(req.startAt());
        a.setStatus(AppointmentStatus.REQUESTED);
        a.setChannel(req.channel());
        return appointmentRepository.save(a);
    }

    @MutationMapping("bookAppointment")
    public Appointment bookAppointment(@Argument long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        a.setStatus(AppointmentStatus.BOOKED);
        return appointmentRepository.save(a);
    }

    @MutationMapping("requestCancel")
    public boolean requestCancel(@Argument long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        a.setStatus(AppointmentStatus.CANCEL_REQUESTED);
        appointmentRepository.save(a);
        return true;
    }

    @MutationMapping("requestReschedule")
    public Appointment requestReschedule(@Argument long appointmentId, @Argument("request") RescheduleRequestInput req) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        a.setProposedStartAt(req.proposedStartAt());
        a.setStatus(AppointmentStatus.RESCHEDULE_REQUESTED);
        return appointmentRepository.save(a);
    }

    @MutationMapping("confirmReschedule")
    public Appointment confirmReschedule(@Argument long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        if (a.getProposedStartAt() != null) {
            a.setStartAt(a.getProposedStartAt());
            a.setProposedStartAt(null);
        }
        a.setStatus(AppointmentStatus.BOOKED);
        return appointmentRepository.save(a);
    }

    @MutationMapping("confirmCancel")
    public boolean confirmCancel(@Argument long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        a.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(a);
        return true;
    }

    @MutationMapping("recordBill")
    public Bill recordBill(@Argument NewBill newBill) {
        Patient p = patientRepository.findById(newBill.patientId()).orElseThrow();
        Bill b = new Bill();
        b.setPatient(p);
        b.setAmount(newBill.amount());
        b.setDueDate(newBill.dueDate());
        b.setPaid(Boolean.TRUE.equals(newBill.paid()));
        return billRepository.save(b);
    }
}

