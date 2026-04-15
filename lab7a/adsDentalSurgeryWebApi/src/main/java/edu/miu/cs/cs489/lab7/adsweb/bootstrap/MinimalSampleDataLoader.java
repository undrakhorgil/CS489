package edu.miu.cs.cs489.lab7.adsweb.bootstrap;

import edu.miu.cs.cs489.lab7.adsweb.model.Address;
import edu.miu.cs.cs489.lab7.adsweb.model.Patient;
import edu.miu.cs.cs489.lab7.adsweb.repository.PatientRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

/** Inserts one demo row when {@code patients} is empty; Lab 6 CLI can load full sample data into the same DB. */
@Component
@Profile("!test")
@Order(10)
public class MinimalSampleDataLoader implements ApplicationRunner {

    private final PatientRepository patientRepository;

    public MinimalSampleDataLoader(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (patientRepository.count() > 0) {
            return;
        }
        Address addr = new Address(null, "1 High Street", "Anytown", "UK", null, new ArrayList<>());
        Patient p = new Patient();
        p.setFirstName("Demo");
        p.setLastName("Patient");
        p.setContactPhoneNumber("555-0000");
        p.setEmail("demo.patient@example.com");
        p.setDateOfBirth(LocalDate.of(1990, 1, 1));
        p.setPatientRef("P-DEMO");
        p.setMailingAddress(addr);
        patientRepository.save(p);
    }
}
