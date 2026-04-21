package edu.miu.cs.cs489appsd.ads.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.miu.cs.cs489appsd.ads.api.dto.auth.LoginRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
class OfficeAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class OfficePortal {
        @Test
        void listPatients_allowsOfficeManagerJwt() throws Exception {
            String token = loginAndGetAccessToken("manager", "password");

            mockMvc.perform(
                            get("/api/v1/office/patients")
                                    .header("Authorization", "Bearer " + token)
                    )
                    .andExpect(status().isOk());
        }

        @Test
        void listPatients_rejectsNonOfficeRole() throws Exception {
            String token = loginAndGetAccessToken("patient1", "password");

            mockMvc.perform(
                            get("/api/v1/office/patients")
                                    .header("Authorization", "Bearer " + token)
                    )
                    .andExpect(status().isForbidden());
        }
    }

    private String loginAndGetAccessToken(String username, String password) throws Exception {
        LoginRequest req = new LoginRequest(username, password);

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        String accessToken = node.get("accessToken").asText();
        assertThat(accessToken).isNotBlank();
        return accessToken;
    }
}

