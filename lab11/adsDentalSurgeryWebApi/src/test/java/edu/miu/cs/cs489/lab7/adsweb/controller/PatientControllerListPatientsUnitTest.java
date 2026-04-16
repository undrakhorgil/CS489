package edu.miu.cs.cs489.lab7.adsweb.controller;

import edu.miu.cs.cs489.lab7.adsweb.dto.address.AddressResponse;
import edu.miu.cs.cs489.lab7.adsweb.dto.patient.PatientResponse;
import edu.miu.cs.cs489.lab7.adsweb.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerListPatientsUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Test
    void listPatients_returnsOkAndPatientsJsonArray() throws Exception {
        List<PatientResponse> payload = List.of(
                new PatientResponse(
                        1L,
                        "John",
                        "Doe",
                        "641-555-0100",
                        "john.doe@example.com",
                        LocalDate.of(1990, 1, 15),
                        "P-1001",
                        new AddressResponse(10L, "1000 N 4th St", "Fairfield", "IA", "52557")
                ),
                new PatientResponse(
                        2L,
                        "Jane",
                        "Smith",
                        "641-555-0199",
                        "jane.smith@example.com",
                        LocalDate.of(1992, 5, 20),
                        "P-1002",
                        new AddressResponse(11L, "2000 W 3rd St", "Fairfield", "IA", "52556")
                )
        );
        when(patientService.getAllPatientsSortedByLastName()).thenReturn(payload);

        mockMvc.perform(get("/adsweb/api/v1/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].patientId").value(1))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].patientId").value(2))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
    }
}

