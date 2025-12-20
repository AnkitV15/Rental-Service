package com.rentalmanagement.rentalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rentalmanagement.rentalservice.model.Lease;

public interface LeaseRepository extends JpaRepository<Lease, String> {
    Lease findByAccessToken(String accessToken);
}
