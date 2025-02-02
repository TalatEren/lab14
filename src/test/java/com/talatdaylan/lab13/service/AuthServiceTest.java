package com.talatdaylan.lab14.service;

import com.talatdaylan.lab14.dto.AuthRequest;
import com.talatdaylan.lab14.dto.RegisterRequest;
import com.talatdaylan.lab14.model.User;
import com.talatdaylan.lab14.repository.UserRepository;
import com.talatdaylan.lab14.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Test123456!")
                .build();

        authRequest = AuthRequest.builder()
                .username("testuser")
                .password("Test123456!")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        var response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null));

        var response = authService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
