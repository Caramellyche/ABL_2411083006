package com.faizul.auth;

import com.faizul.auth.dto.AuthResponse;
import com.faizul.auth.dto.LoginRequest;
import com.faizul.auth.dto.RegisterRequest;
import com.faizul.auth.entity.User;
import com.faizul.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void register_shouldReturn200() throws Exception {
        RegisterRequest req = new RegisterRequest("faizul", "password123", "ADMIN");
        User user = new User(1L, "faizul", "hashed", "ADMIN");
        when(authService.register(any())).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("faizul"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        LoginRequest req = new LoginRequest("faizul", "password123");
        AuthResponse res = new AuthResponse("mock.jwt.token", "faizul", "ADMIN");
        when(authService.login(any())).thenReturn(res);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.username").value("faizul"));
    }

    @Test
    void login_shouldReturn401OnBadCredentials() throws Exception {
        LoginRequest req = new LoginRequest("faizul", "wrongpass");
        when(authService.login(any())).thenThrow(new IllegalArgumentException("Invalid username or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
