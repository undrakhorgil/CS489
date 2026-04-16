package edu.miu.cs.cs489.lab7.adsweb.service;

import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientResponse;
import edu.miu.cs.cs489.lab7.adsweb.exception.PatientNotFoundException;
import edu.miu.cs.cs489.lab7.adsweb.model.Address;
import edu.miu.cs.cs489.lab7.adsweb.model.Patient;
import edu.miu.cs.cs489.lab7.adsweb.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PatientServiceFindPatientByIdIntegrationTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void findPatientById_whenPatientIdExists_returnsPatient() {
        Patient saved = patientRepository.save(buildPatient(
                "John",
                "Doe",
                "641-555-0100",
                "john.doe@example.com",
                LocalDate.of(1990, 1, 15)));

        PatientResponse result = patientService.findPatientById(saved.getPatientId());

        assertNotNull(result);
        assertEquals(saved.getPatientId(), result.patientId());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("john.doe@example.com", result.email());
    }

    @Test
    void findPatientById_whenPatientIdIsInvalid_throwsPatientNotFoundException() {
        assertThrows(PatientNotFoundException.class, () -> patientService.findPatientById(999999L));
    }

    private static Patient buildPatient(
            String firstName,
            String lastName,
            String phone,
            String email,
            LocalDate dob
    ) {
        Address address = new Address(null, "1000 N 4th St", "Fairfield", "IA", "52557", new java.util.ArrayList<>());
        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setContactPhoneNumber(phone);
        patient.setEmail(email);
        patient.setDateOfBirth(dob);
        patient.setPatientRef("P-" + System.nanoTime());
        patient.setMailingAddress(address);
        return patient;
    }
}

