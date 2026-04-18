package edu.miu.cs.cs489appsd.ads.config;

import edu.miu.cs.cs489appsd.ads.domain.*;
import edu.miu.cs.cs489appsd.ads.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Creates {@code roles}, lab sample data (dentists, patients, addresses, surgeries,
 * appointments, bills), and default login users when the database is empty.
 */
@Service
public class DatabaseSeeder {

    private final RoleEntityRepository roleEntityRepository;
    private final SurgeryRepository surgeryRepository;
    private final DentistRepository dentistRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            RoleEntityRepository roleEntityRepository,
            SurgeryRepository surgeryRepository,
            DentistRepository dentistRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            BillRepository billRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder) {
        this.roleEntityRepository = roleEntityRepository;
        this.surgeryRepository = surgeryRepository;
        this.dentistRepository = dentistRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void seed() {
        seedRolesIfNeeded();
        if (surgeryRepository.count() == 0) {
            seedLabSampleData();
        }
        seedDefaultAccountsIfNeeded();
    }

    private void seedRolesIfNeeded() {
        if (roleEntityRepository.count() > 0) {
            return;
        }
        roleEntityRepository.save(new RoleEntity(null, Role.OFFICE_MANAGER.name()));
        roleEntityRepository.save(new RoleEntity(null, Role.DENTIST.name()));
        roleEntityRepository.save(new RoleEntity(null, Role.PATIENT.name()));
    }

    private void seedLabSampleData() {
        Dentist dAmelia = dentistRepository.save(new Dentist(null, "Amelia", "Brown",
                "515-555-0101", "amelia.brown@ads.com", "Orthodontics"));
        Dentist dNoah = dentistRepository.save(new Dentist(null, "Noah", "Carter",
                "515-555-0102", "noah.carter@ads.com", "Endodontics"));
        Dentist dOlivia = dentistRepository.save(new Dentist(null, "Olivia", "Davis",
                "515-555-0103", "olivia.davis@ads.com", "Pediatric Dentistry"));
        Dentist dLiam = dentistRepository.save(new Dentist(null, "Liam", "Evans",
                "515-555-0104", "liam.evans@ads.com", "Periodontics"));
        Dentist dSophia = dentistRepository.save(new Dentist(null, "Sophia", "Nguyen",
                "515-555-0105", "sophia.nguyen@ads.com", "General Dentistry"));

        Patient pEthan = patientRepository.save(new Patient(null, "Ethan", "Miller",
                "515-555-0201", "ethan.miller@gmail.com",
                "1000 N 4th St, Fairfield, IA", LocalDate.of(1997, 6, 11)));
        Patient pAva = patientRepository.save(new Patient(null, "Ava", "Wilson",
                "515-555-0202", "ava.wilson@gmail.com",
                "12 S Main St, Ottumwa, IA", LocalDate.of(1990, 2, 19)));
        Patient pMason = patientRepository.save(new Patient(null, "Mason", "Taylor",
                "515-555-0203", "mason.taylor@gmail.com",
                "55 W Jefferson, Des Moines, IA", LocalDate.of(1985, 11, 3)));
        Patient pIsla = patientRepository.save(new Patient(null, "Isla", "Anderson",
                "515-555-0204", "isla.anderson@gmail.com",
                "9 Cherry Ln, Ames, IA", LocalDate.of(2001, 9, 27)));

        Surgery sFairfield = surgeryRepository.save(new Surgery(null, "ADS - Fairfield",
                "2000 W Burlington Ave, Fairfield, IA", "515-555-0301"));
        Surgery sOttumwa = surgeryRepository.save(new Surgery(null, "ADS - Ottumwa",
                "22 S Market St, Ottumwa, IA", "515-555-0302"));
        Surgery sDesMoines = surgeryRepository.save(new Surgery(null, "ADS - Des Moines",
                "77 E Grand Ave, Des Moines, IA", "515-555-0303"));

        saveAppt(pEthan, dAmelia, sFairfield, LocalDateTime.of(2026, 4, 10, 9, 0), null,
                AppointmentStatus.BOOKED, AppointmentRequestChannel.PHONE);
        saveAppt(pAva, dAmelia, sOttumwa, LocalDateTime.of(2026, 4, 11, 10, 30), null,
                AppointmentStatus.BOOKED, AppointmentRequestChannel.ONLINE);
        saveAppt(pMason, dNoah, sDesMoines, LocalDateTime.of(2026, 4, 10, 13, 0), null,
                AppointmentStatus.CANCELLED, AppointmentRequestChannel.PHONE);
        saveAppt(pIsla, dOlivia, sFairfield, LocalDateTime.of(2026, 4, 12, 8, 15),
                LocalDateTime.of(2026, 4, 13, 8, 15),
                AppointmentStatus.RESCHEDULE_REQUESTED, AppointmentRequestChannel.ONLINE);
        saveAppt(pEthan, dLiam, sDesMoines, LocalDateTime.of(2026, 4, 15, 11, 0), null,
                AppointmentStatus.BOOKED, AppointmentRequestChannel.ONLINE);
        saveAppt(pAva, dSophia, sOttumwa, LocalDateTime.of(2026, 4, 15, 14, 0), null,
                AppointmentStatus.BOOKED, AppointmentRequestChannel.PHONE);

        billRepository.save(bill(pEthan.getPatientId(), new BigDecimal("95.00"),
                LocalDate.of(2026, 3, 20), true));
        billRepository.save(bill(pAva.getPatientId(), new BigDecimal("120.00"),
                LocalDate.of(2026, 3, 25), true));
        billRepository.save(bill(pMason.getPatientId(), new BigDecimal("250.00"),
                LocalDate.of(2026, 3, 15), false));
        billRepository.save(bill(pIsla.getPatientId(), new BigDecimal("80.00"),
                LocalDate.of(2026, 3, 30), true));
    }

    private void saveAppt(Patient p, Dentist d, Surgery s, LocalDateTime start,
                          LocalDateTime proposed, AppointmentStatus status, AppointmentRequestChannel ch) {
        Appointment a = new Appointment();
        a.setPatientId(p.getPatientId());
        a.setDentistId(d.getDentistId());
        a.setSurgeryId(s.getSurgeryId());
        a.setStartAt(start);
        a.setProposedStartAt(proposed);
        a.setStatus(status);
        a.setChannel(ch);
        appointmentRepository.save(a);
    }

    private static Bill bill(Long patientId, BigDecimal amt, LocalDate due, boolean paid) {
        Bill b = new Bill();
        b.setPatientId(patientId);
        b.setAmount(amt);
        b.setDueDate(due);
        b.setPaid(paid);
        return b;
    }

    private void seedDefaultAccountsIfNeeded() {
        if (accountRepository.findByUsernameIgnoreCase("manager").isPresent()) {
            return;
        }
        RoleEntity office = roleEntityRepository.findByName(Role.OFFICE_MANAGER.name()).orElseThrow();
        RoleEntity dentRole = roleEntityRepository.findByName(Role.DENTIST.name()).orElseThrow();
        RoleEntity patRole = roleEntityRepository.findByName(Role.PATIENT.name()).orElseThrow();

        Account manager = new Account();
        manager.setUsername("manager");
        manager.setPasswordHash(passwordEncoder.encode("password"));
        manager.setRoleEntity(office);
        accountRepository.save(manager);

        Long ameliaId = dentistRepository.findByEmail("amelia.brown@ads.com")
                .map(Dentist::getDentistId)
                .orElse(null);
        if (ameliaId != null) {
            Account dentAcc = new Account();
            dentAcc.setUsername("dentist1");
            dentAcc.setPasswordHash(passwordEncoder.encode("password"));
            dentAcc.setRoleEntity(dentRole);
            dentAcc.setDentistId(ameliaId);
            accountRepository.save(dentAcc);
        }

        Long ethanId = patientRepository.findByEmail("ethan.miller@gmail.com")
                .map(Patient::getPatientId)
                .orElse(null);
        if (ethanId != null) {
            Account patAcc = new Account();
            patAcc.setUsername("patient1");
            patAcc.setPasswordHash(passwordEncoder.encode("password"));
            patAcc.setRoleEntity(patRole);
            patAcc.setPatientId(ethanId);
            accountRepository.save(patAcc);
        }
    }
}
