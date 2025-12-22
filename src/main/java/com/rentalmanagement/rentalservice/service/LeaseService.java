package com.rentalmanagement.rentalservice.service;

import com.rentalmanagement.rentalservice.dto.CreateLeaseRequest;
import com.rentalmanagement.rentalservice.model.Lease;
import com.rentalmanagement.rentalservice.model.Tenant;
import com.rentalmanagement.rentalservice.model.Unit;
import com.rentalmanagement.rentalservice.repository.LeaseRepository;
import com.rentalmanagement.rentalservice.repository.TenantRepository;
import com.rentalmanagement.rentalservice.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final TenantRepository tenantRepository;
    private final UnitRepository unitRepository;

    @Transactional
    public Lease createLease(CreateLeaseRequest request) {
        // 1. Fetch Unit
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        // 2. Switchover Logic: Deactivate existing active lease
        Lease activeLease = leaseRepository.findByUnitIdAndIsActiveTrue(unit.getId());
        if (activeLease != null) {
            activeLease.setIsActive(false);
            // Optionally set end date to yesterday or today if open-ended,
            // but usually we just flip the flag.
            leaseRepository.save(activeLease);
        }

        // 3. Find or Create Tenant
        Tenant tenant = tenantRepository.findByEmail(request.getTenantEmail())
                .orElseGet(() -> {
                    Tenant newTenant = new Tenant();
                    newTenant.setFullName(request.getTenantFullName());
                    newTenant.setEmail(request.getTenantEmail());
                    newTenant.setPhoneNumber(request.getTenantPhoneNumber());
                    return tenantRepository.save(newTenant);
                });

        // 4. Create New Lease with Magic Link
        Lease lease = new Lease();
        lease.setUnit(unit);
        lease.setTenant(tenant);
        lease.setStartDate(request.getStartDate());
        lease.setEndDate(request.getEndDate());
        lease.setIsActive(true);
        lease.setAccessToken(UUID.randomUUID().toString()); // Magic Link Token

        return leaseRepository.save(lease);
    }

    public Lease getLeaseByToken(String token) {
        Lease lease = leaseRepository.findByAccessToken(token);
        if (lease == null || !lease.getIsActive()) {
            throw new RuntimeException("Invalid or inactive lease");
        }
        return lease;
    }
}
