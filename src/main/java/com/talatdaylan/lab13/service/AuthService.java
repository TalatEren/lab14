package com.talatdaylan.lab14.service;

import com.talatdaylan.lab14.dto.AuthRequest;
import com.talatdaylan.lab14.dto.AuthResponse;
import com.talatdaylan.lab14.dto.RegisterRequest;
import com.talatdaylan.lab14.model.User;
import com.talatdaylan.lab14.repository.UserRepository;
import com.talatdaylan.lab14.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        var user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role("USER")
            .active(true)
            .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
            .token(jwtToken)
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .active(user.isActive())
            .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        var user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("User is not active");
        }

        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
            .token(jwtToken)
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .active(user.isActive())
            .build();
    }
}
