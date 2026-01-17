package com.plaintext.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaintext.auth.advice.SecurityExceptionHandler;
import com.plaintext.auth.dto.AuthResponse;
import com.plaintext.auth.dto.LoginRequest;
import com.plaintext.auth.dto.SignupRequest;
import com.plaintext.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new SecurityExceptionHandler())
                .build();
    }

    @Test
    void registerUser_Success() throws Exception {
        SignupRequest request = new SignupRequest("user", "user@test.com", "password", "bio");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void registerUser_ValidationFailure() throws Exception {
        SignupRequest request = new SignupRequest("", "invalid-email", "", ""); // Invalid data

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_Success() throws Exception {
        LoginRequest request = new LoginRequest("user", "password");
        AuthResponse authResponse = new AuthResponse("jwt-token", "user", "test@example.com", "USER", false);

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.requiresTncAcceptance").value(false));
    }

    @Test
    void loginUser_BadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("user", "wrong");

        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()); // SecurityExceptionHandler handles this
    }

    @Test
    void acceptTnc_Success() throws Exception {
        java.security.Principal principal = new UsernamePasswordAuthenticationToken("user", null);

        mockMvc.perform(post("/api/auth/tnc/accept")
                .principal(principal))
                .andExpect(status().isOk());

        // Verify service was called with correct username
        org.mockito.Mockito.verify(authService).acceptTnc("user");
    }
}
