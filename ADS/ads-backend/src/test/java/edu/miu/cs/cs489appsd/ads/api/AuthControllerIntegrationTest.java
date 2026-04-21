package edu.miu.cs.cs489appsd.ads.api;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class PublicEndpoints {
        @Test
        void health_isPublic() throws Exception {
            mockMvc.perform(get("/api/v1/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"));
        }
    }

    @Nested
    class Login {
        @Test
        void login_returnsJwt() throws Exception {
            // Arrange
            LoginRequest req = new LoginRequest("manager", "password");

            // Act + Assert
            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(req))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.username").value("manager"))
                    .andExpect(jsonPath("$.role").isNotEmpty());
        }
    }

    @Nested
    class Authorization {
        @Test
        void protectedEndpoint_rejectsWithoutToken() throws Exception {
            mockMvc.perform(post("/api/v1/office/surgeries"))
                    .andExpect(status().isForbidden());
        }
    }
}

