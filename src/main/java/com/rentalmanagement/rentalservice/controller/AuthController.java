package com.rentalmanagement.rentalservice.controller;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rentalmanagement.rentalservice.dto.LoginRequest;
import com.rentalmanagement.rentalservice.dto.LoginResponse;
import com.rentalmanagement.rentalservice.dto.RegisterRequest;
import com.rentalmanagement.rentalservice.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registering user");
        authService.register(registerRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Logging in user");
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Stateless logout endpoint.
     * <p>
     * Since we use JWTs with {@code SessionCreationPolicy.STATELESS}, the server
     * does not keep session state.
     * Logging out is effectively a client-side operation: the frontend should
     * delete the stored access token.
     * </p>
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("Owner requested logout");
        // No server-side state to clear; client must discard the token
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        log.info("Inside AuthController - verifyEmail() : {}", token);
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("Message", "Email Verified Successfully"));
    }
}