package com.rentalmanagement.rentalservice.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rentalmanagement.rentalservice.dto.LoginRequest;
import com.rentalmanagement.rentalservice.dto.LoginResponse;
import com.rentalmanagement.rentalservice.dto.RegisterRequest;
import com.rentalmanagement.rentalservice.exception.EmailAlreadyExistsException;
import com.rentalmanagement.rentalservice.exception.EmailNotVerifiedException;
import com.rentalmanagement.rentalservice.exception.InvalidCredentialsException;
import com.rentalmanagement.rentalservice.exception.VerificationTokenException;
import com.rentalmanagement.rentalservice.model.Owner;
import com.rentalmanagement.rentalservice.repository.OwnerRepository;
import com.rentalmanagement.rentalservice.util.JwtUtil;
import com.rentalmanagement.rentalservice.security.RoleConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    private final JwtUtil jwtUtil;

    @Transactional
    public void register(RegisterRequest registerRequest) {
        String normalizedEmail = normalizeEmail(registerRequest.getEmail());
        log.info("Registering user: {}", normalizedEmail);

        if (ownerRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        Owner owner = Owner.builder()
                .role(RoleConstants.ROLE_OWNER)
                .publicId(UUID.randomUUID().toString())
                .username(registerRequest.getUsername())
                .email(normalizedEmail)
                .passwordHash(hashedPassword)
                .isVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();

        try {
            ownerRepository.save(owner);
        } catch (DataIntegrityViolationException e) {
            // Handle potential race condition where the email becomes non-unique between
            // existsByEmail and save
            log.warn("Data integrity violation while registering email {}: {}", normalizedEmail, e.getMessage());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        notificationService.sendVerificationEmail(owner);

    }

    public LoginResponse login(LoginRequest loginRequest) {
        String normalizedEmail = normalizeEmail(loginRequest.getEmail());
        log.info("Attempting to log in user with email: {}", normalizedEmail);
        Optional<Owner> ownerOptional = ownerRepository.findByEmail(normalizedEmail);

        if (ownerOptional.isEmpty()) {
            log.error("Login failed: No user found with email {}", normalizedEmail);
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        Owner owner = ownerOptional.get();
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), owner.getPasswordHash());

        if (!passwordMatches) {
            log.error("Login failed: Password does not match for user {}", normalizedEmail);
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        if (!owner.isVerified()) {
            log.warn("Login failed: Email not verified for user {}", normalizedEmail);
            throw new EmailNotVerifiedException("Email not Verified");
        }

        String jwt = jwtUtil.generateToken(owner.getEmail(), owner.getPublicId(), owner.getRole());

        LoginResponse response = LoginResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationMillis())
                .ownerId(owner.getPublicId())
                .email(owner.getEmail())
                .username(owner.getUsername())
                .role(owner.getRole())
                .build();

        return response;
    }

    @Transactional
    public void verifyEmail(String token) {
        log.info("Inside Auth Service: verifyEmail(): {}", token);
        Owner owner = ownerRepository.findByVerificationToken(token)
                .orElseThrow(() -> new VerificationTokenException("Invalid or Expired Verfication Token"));

        if (owner.getVerificationExpires() != null && owner.getVerificationExpires().isBefore(LocalDateTime.now())) {
            throw new VerificationTokenException("Verification Token has Expired. Please request a new one");
        }

        if (owner.isVerified()) {
            log.info("verifyEmail called for already verified email: {}", owner.getEmail());
            return;
        }

        owner.setVerified(true);
        owner.setVerificationToken(null);
        owner.setVerificationExpires(null);
        ownerRepository.save(owner);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}
