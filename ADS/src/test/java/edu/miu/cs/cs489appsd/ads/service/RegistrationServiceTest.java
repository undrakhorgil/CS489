package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Dentist;
import edu.miu.cs.cs489appsd.ads.domain.Patient;
import edu.miu.cs.cs489appsd.ads.domain.Surgery;
import edu.miu.cs.cs489appsd.ads.repository.DentistRepository;
import edu.miu.cs.cs489appsd.ads.repository.PatientRepository;
import edu.miu.cs.cs489appsd.ads.repository.SurgeryRepository;
import edu.miu.cs.cs489appsd.ads.web.dto.DentistRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.PatientRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.SurgeryRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class RegistrationServiceTest {

    @Mock
    DentistRepository dentistRepository;
    @Mock
    PatientRepository patientRepository;
    @Mock
    SurgeryRepository surgeryRepository;

    @InjectMocks
    RegistrationService registrationService;

    @Captor
    ArgumentCaptor<Dentist> dentistCaptor;
    @Captor
    ArgumentCaptor<Patient> patientCaptor;
    @Captor
    ArgumentCaptor<Surgery> surgeryCaptor;

    @Test
    void registerDentist_mapsAndPersists() {
        DentistRequest req = new DentistRequest("Amelia", "Brown", "515-555-0101", "amelia@ads.com", "Ortho");
        when(dentistRepository.save(ArgumentMatchers.any(Dentist.class)))
                .thenAnswer(inv -> {
                    Dentist d = inv.getArgument(0, Dentist.class);
                    d.setDentistId(10L);
                    return d;
                });

        var res = registrationService.registerDentist(req);

        verify(dentistRepository).save(dentistCaptor.capture());
        Dentist saved = dentistCaptor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("Amelia");
        assertThat(saved.getEmail()).isEqualTo("amelia@ads.com");
        assertThat(res.dentistId()).isEqualTo(10L);
    }

    @Test
    void enrollPatient_mapsAndPersists() {
        PatientRequest req = new PatientRequest("Ethan", "Miller", "515-555-0201", "ethan@gmail.com",
                "1000 N 4th St", LocalDate.of(1997, 6, 11));
        when(patientRepository.save(ArgumentMatchers.any(Patient.class)))
                .thenAnswer(inv -> {
                    Patient p = inv.getArgument(0, Patient.class);
                    p.setPatientId(20L);
                    return p;
                });

        var res = registrationService.enrollPatient(req);

        verify(patientRepository).save(patientCaptor.capture());
        Patient saved = patientCaptor.getValue();
        assertThat(saved.getLastName()).isEqualTo("Miller");
        assertThat(res.patientId()).isEqualTo(20L);
    }

    @Test
    void registerSurgery_mapsAndPersists() {
        SurgeryRequest req = new SurgeryRequest("ADS - Fairfield", "2000 W Burlington Ave", "515-555-0301");
        when(surgeryRepository.save(ArgumentMatchers.any(Surgery.class)))
                .thenAnswer(inv -> {
                    Surgery s = inv.getArgument(0, Surgery.class);
                    s.setSurgeryId(30L);
                    return s;
                });

        var res = registrationService.registerSurgery(req);

        verify(surgeryRepository).save(surgeryCaptor.capture());
        Surgery saved = surgeryCaptor.getValue();
        assertThat(saved.getName()).isEqualTo("ADS - Fairfield");
        assertThat(res.surgeryId()).isEqualTo(30L);
    }
}

