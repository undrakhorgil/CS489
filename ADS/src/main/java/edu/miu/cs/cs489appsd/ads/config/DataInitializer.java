package edu.miu.cs.cs489appsd.ads.config;

import edu.miu.cs.cs489appsd.ads.domain.Account;
import edu.miu.cs.cs489appsd.ads.domain.Role;
import edu.miu.cs.cs489appsd.ads.repository.AccountRepository;
import edu.miu.cs.cs489appsd.ads.repository.SurgeryRepository;
import edu.miu.cs.cs489appsd.ads.service.RegistrationService;
import edu.miu.cs.cs489appsd.ads.web.dto.DentistRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.PatientRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.SurgeryRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(
            SurgeryRepository surgeryRepository,
            RegistrationService registrationService,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!surgeryRepository.findAll().isEmpty()) {
                return;
            }

            registrationService.registerSurgery(new SurgeryRequest(
                    "ADS Central",
                    "123 Dental Way, SW Region",
                    "(555) 010-0001"
            ));
            registrationService.registerSurgery(new SurgeryRequest(
                    "ADS West Clinic",
                    "400 West Ave",
                    "(555) 010-0002"
            ));

            var dentist = registrationService.registerDentist(new DentistRequest(
                    "Jordan",
                    "Rivera",
                    "(555) 010-1000",
                    "j.rivera@ads.example",
                    "General Dentistry"
            ));

            var patient = registrationService.enrollPatient(new PatientRequest(
                    "Alex",
                    "Morgan",
                    "(555) 010-2000",
                    "alex.morgan@example.com",
                    "10 Patient Rd",
                    LocalDate.of(1990, 5, 15)
            ));

            Account manager = new Account();
            manager.setUsername("manager");
            manager.setPasswordHash(passwordEncoder.encode("password"));
            manager.setRole(Role.OFFICE_MANAGER);
            accountRepository.save(manager);

            Account dentAcc = new Account();
            dentAcc.setUsername("dentist1");
            dentAcc.setPasswordHash(passwordEncoder.encode("password"));
            dentAcc.setRole(Role.DENTIST);
            dentAcc.setDentistId(dentist.dentistId());
            accountRepository.save(dentAcc);

            Account patAcc = new Account();
            patAcc.setUsername("patient1");
            patAcc.setPasswordHash(passwordEncoder.encode("password"));
            patAcc.setRole(Role.PATIENT);
            patAcc.setPatientId(patient.patientId());
            accountRepository.save(patAcc);
        };
    }
}
