package com.platform.obour.service;

import com.platform.obour.dto.AuthResponse;
import com.platform.obour.dto.LoginRequest;
import com.platform.obour.dto.RegisterRequest;
import com.platform.obour.entity.User;
import com.platform.obour.entity.enums.Role;
import com.platform.obour.repository.UserRepository;
import com.platform.obour.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ResponseEntity<Map<String, String>> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Email already exists"));
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        try {
            user.setRole(Role.valueOf(request.role().trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Invalid role. Must be STUDENT or TEACHER"));
        }

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtUtil.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(
                user.getId(),
                jwt,
                user.getName(),
                user.getRole().name()
        ));
    }
    public ResponseEntity<Map<String, Object>> getCurrentUser(String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }

}
