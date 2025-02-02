package com.talatdaylan.lab14.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talatdaylan.lab14.config.SecurityTestConfig;
import com.talatdaylan.lab14.dto.AuthRequest;
import com.talatdaylan.lab14.dto.AuthResponse;
import com.talatdaylan.lab14.dto.RegisterRequest;
import com.talatdaylan.lab14.model.User;
import com.talatdaylan.lab14.security.JwtAuthenticationFilter;
import com.talatdaylan.lab14.security.JwtService;
import com.talatdaylan.lab14.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityTestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();

        authRequest = AuthRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        authResponse = AuthResponse.builder()
                .token("test.jwt.token")
                .build();

        when(jwtService.generateToken(any())).thenReturn("test.jwt.token");
    }

    @Test
    void register_Success() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("test.jwt.token"));
    }

    @Test
    void authenticate_Success() throws Exception {
        when(authService.authenticate(any(AuthRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("test.jwt.token"));
    }

    @Test
    void authenticate_InvalidCredentials() throws Exception {
        when(authService.authenticate(any(AuthRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }
}
