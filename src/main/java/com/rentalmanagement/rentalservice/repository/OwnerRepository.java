package com.rentalmanagement.rentalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rentalmanagement.rentalservice.model.Owner;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, String> {
    Optional<Owner> findByEmail(String emailString);

    boolean existsByEmail(String emailString);

    // Owner findByAccessToken(String accessToken);

    Optional<Owner> findByVerificationToken(String token);
}
