package com.rentalmanagement.rentalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.rentalmanagement.rentalservice.model.Lease;

public interface LeaseRepository extends JpaRepository<Lease, Long> {
    Lease findByAccessToken(String accessToken);

    // Find the currently active lease for a specific unit
    Lease findByUnitIdAndIsActiveTrue(Long unitId);

    List<Lease> findAllByUnitId(Long unitId);

    List<Lease> findAllByUnitPropertyOwnerId(Long ownerId);

    Lease findByTenantIdAndIsActiveTrue(Long tenantId);
}
