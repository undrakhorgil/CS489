package edu.miu.cs.cs489appsd.ads.api;

import edu.miu.cs.cs489appsd.ads.api.dto.PatientResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.SurgeryRequest;
import edu.miu.cs.cs489appsd.ads.api.dto.SurgeryResponse;
import edu.miu.cs.cs489appsd.ads.service.AppointmentService;
import edu.miu.cs.cs489appsd.ads.service.RegistrationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class OfficeControllerUnitTest {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private OfficeController controller;

    @Nested
    class ListPatients {
        @Test
        void delegatesToService() {
            // Arrange
            List<PatientResponse> expected = List.of(
                    new PatientResponse(1L, "Ethan", "Miller", "515", "ethan@gmail.com", "Addr", LocalDate.of(1997, 6, 11))
            );
            when(registrationService.listAllPatients()).thenReturn(expected);

            // Act
            List<PatientResponse> actual = controller.listPatients();

            // Assert
            assertThat(actual).isEqualTo(expected);
            verify(registrationService).listAllPatients();
        }
    }

    @Nested
    class RegisterSurgery {
        @Test
        void delegatesToService() {
            // Arrange
            SurgeryRequest req = new SurgeryRequest("ADS - Fairfield", "2000 W Burlington Ave", "515-555-0301");
            SurgeryResponse expected = new SurgeryResponse(10L, req.name(), req.locationAddress(), req.telephoneNumber());
            when(registrationService.registerSurgery(req)).thenReturn(expected);

            // Act
            SurgeryResponse actual = controller.registerSurgery(req);

            // Assert
            assertThat(actual).isEqualTo(expected);
            verify(registrationService).registerSurgery(req);
        }
    }
}

