package com.rentalmanagement.rentalservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rentalmanagement.rentalservice.dto.LoginRequest;
import com.rentalmanagement.rentalservice.dto.RegisterRequest;
import com.rentalmanagement.rentalservice.exception.EmailAlreadyExistsException;
import com.rentalmanagement.rentalservice.exception.InvalidCredentialsException;
import com.rentalmanagement.rentalservice.exception.EmailNotVerifiedException;
import com.rentalmanagement.rentalservice.exception.VerificationTokenException;
import com.rentalmanagement.rentalservice.model.Owner;
import com.rentalmanagement.rentalservice.repository.OwnerRepository;
import com.rentalmanagement.rentalservice.util.JwtUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    private final JwtUtil jwtUtil;

    @Value("${spring.application.url}")
    private String appbaseUrl;

    public void register(RegisterRequest registerRequest) {
        log.info("Registering user: {}", registerRequest.getEmail());
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("Email Exists Check");
        if (ownerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        stopWatch.stop();

        stopWatch.start("Password Encoding");
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        stopWatch.stop();

        Owner owner = Owner.builder()
                .role("ROLE_OWNER")
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .passwordHash(hashedPassword)
                .isVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusMinutes(24))
                .build();

        stopWatch.start("Database Save");
        ownerRepository.save(owner);
        stopWatch.stop();

        stopWatch.start("Send Verification Email");
        notificationService.sendVerificationEmail(owner);
        stopWatch.stop();

        log.info("Registration process timing details: \n{}", stopWatch.prettyPrint());
    }

    public void login(LoginRequest loginRequest) {
        log.info("Attempting to log in user with email: {}", loginRequest.getEmail());
        Optional<Owner> ownerOptional = ownerRepository.findByEmail(loginRequest.getEmail());

        if (ownerOptional.isEmpty()) {
            log.error("Login failed: No user found with email {}", loginRequest.getEmail());
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        Owner owner = ownerOptional.get();
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), owner.getPasswordHash());

        if (!passwordMatches) {
            log.error("Login failed: Password does not match for user {}", loginRequest.getEmail());
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        if (!owner.isVerified()) {
            log.warn("Login failed: Email not verified for user {}", loginRequest.getEmail());
            throw new EmailNotVerifiedException("Email not Verified");
        }

        String jwt = jwtUtil.generateToken(owner.getEmail());
        owner.setVerificationToken(jwt);
    }

    public void verifyEmail(String token) {
        log.info("Inside Auth Service: verifyEmail(): {}", token);
        Owner owner = ownerRepository.findByVerificationToken(token)
                .orElseThrow(() -> new VerificationTokenException("Invalid or Expired Verfication Token"));

        if (owner.getVerificationExpires() != null && owner.getVerificationExpires().isBefore(LocalDateTime.now())) {
            throw new VerificationTokenException("Verification Token has Expired. Please request a new one");
        }

        owner.setVerified(true);
        owner.setVerificationToken(null);
        owner.setVerificationExpires(null);
        ownerRepository.save(owner);
    }

}