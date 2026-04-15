package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.bootstrap;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.*;
import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Sample rows aligned with the lab domain-model table (dentistName, patNo, patName, date, time, surgeryNo).
 */
@Component
public class SampleDataLoader {

    private final DentistRepository dentistRepository;
    private final PatientRepository patientRepository;
    private final SurgeryRepository surgeryRepository;
    private final AppointmentRepository appointmentRepository;
    private final AdsRoleRepository adsRoleRepository;
    private final AppUserRepository appUserRepository;

    public SampleDataLoader(
            DentistRepository dentistRepository,
            PatientRepository patientRepository,
            SurgeryRepository surgeryRepository,
            AppointmentRepository appointmentRepository,
            AdsRoleRepository adsRoleRepository,
            AppUserRepository appUserRepository) {
        this.dentistRepository = dentistRepository;
        this.patientRepository = patientRepository;
        this.surgeryRepository = surgeryRepository;
        this.appointmentRepository = appointmentRepository;
        this.adsRoleRepository = adsRoleRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public void loadIfEmpty() {
        if (dentistRepository.count() > 0) {
            return;
        }

        AdsRole roleOffice = adsRoleRepository.save(new AdsRole(null, "ROLE_OFFICE_MANAGER"));
        AdsRole roleDentist = adsRoleRepository.save(new AdsRole(null, "ROLE_DENTIST"));
        AdsRole rolePatient = adsRoleRepository.save(new AdsRole(null, "ROLE_PATIENT"));

        appUserRepository.save(user("manager", "password", "manager@ads.com", roleOffice));
        appUserRepository.save(user("demo_dentist", "password", "demo.dentist@ads.com", roleDentist));
        appUserRepository.save(user("demo_patient", "password", "demo.patient@example.com", rolePatient));

        Dentist tonySmith = dentistRepository.save(new Dentist(null, "Tony", "Smith",
                "555-0100", "tony.smith@dental.example", "General Dentistry"));
        Dentist helenPearson = dentistRepository.save(new Dentist(null, "Helen", "Pearson",
                "555-0101", "helen.pearson@dental.example", "General Dentistry"));
        Dentist robinPlevin = dentistRepository.save(new Dentist(null, "Robin", "Plevin",
                "555-0102", "robin.plevin@dental.example", "General Dentistry"));

        Patient p100 = patient("P100", "Gillian", "White", "gillian.white@example.com");
        Patient p105 = patient("P105", "Jill", "Bell", "jill.bell@example.com");
        Patient p108 = patient("P108", "Ian", "MacKay", "ian.mackay@example.com");
        Patient p110 = patient("P110", "John", "Walker", "john.walker@example.com");

        Address locS10 = new Address(null, "Dental Centre", "Surgery wing", "S10", null);
        Address locS13 = new Address(null, "Dental Centre", "Surgery wing", "S13", null);
        Address locS15 = new Address(null, "Dental Centre", "Surgery wing", "S15", null);

        Surgery s10 = surgeryRepository.save(new Surgery(null, "S10", locS10, "555-S10"));
        Surgery s13 = surgeryRepository.save(new Surgery(null, "S13", locS13, "555-S13"));
        Surgery s15 = surgeryRepository.save(new Surgery(null, "S15", locS15, "555-S15"));

        // Screenshot rows (12/13/14/15 Sep 2013)
        appt(p100, tonySmith, s15, LocalDateTime.of(2013, 9, 12, 10, 0));
        appt(p105, tonySmith, s15, LocalDateTime.of(2013, 9, 12, 12, 0));
        appt(p108, helenPearson, s10, LocalDateTime.of(2013, 9, 12, 10, 0));
        appt(p108, helenPearson, s10, LocalDateTime.of(2013, 9, 14, 14, 0));
        appt(p105, robinPlevin, s15, LocalDateTime.of(2013, 9, 14, 16, 30));
        appt(p110, robinPlevin, s13, LocalDateTime.of(2013, 9, 15, 18, 0));
    }

    private Patient patient(String ref, String first, String last, String email) {
        Address mailing = new Address(null, "1 High Street", "Anytown", "UK", null);
        Patient p = new Patient();
        p.setPatientRef(ref);
        p.setFirstName(first);
        p.setLastName(last);
        p.setContactPhoneNumber("555-0000");
        p.setEmail(email);
        p.setMailingAddress(mailing);
        p.setDateOfBirth(LocalDate.of(1980, 1, 1));
        return patientRepository.save(p);
    }

    private AppUser user(String username, String password, String email, AdsRole role) {
        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPassword(password);
        u.setEmail(email);
        u.setRole(role);
        return u;
    }

    private void appt(Patient p, Dentist d, Surgery s, LocalDateTime start) {
        Appointment a = new Appointment();
        a.setPatient(p);
        a.setDentist(d);
        a.setSurgery(s);
        a.setStartAt(start);
        a.setStatus(AppointmentStatus.SCHEDULED);
        a.setChannel(AppointmentChannel.PHONE);
        appointmentRepository.save(a);
    }
}
