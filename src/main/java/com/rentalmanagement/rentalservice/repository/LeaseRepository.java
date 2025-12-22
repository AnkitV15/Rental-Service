package com.rentalmanagement.rentalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rentalmanagement.rentalservice.model.Lease;

public interface LeaseRepository extends JpaRepository<Lease, Long> {
    Lease findByAccessToken(String accessToken);

    // Find the currently active lease for a specific unit
    Lease findByUnitIdAndIsActiveTrue(Long unitId);
}
