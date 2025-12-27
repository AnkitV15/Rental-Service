package com.rentalmanagement.rentalservice.service;

import com.rentalmanagement.rentalservice.dto.MaintenanceRequestDTO;
import com.rentalmanagement.rentalservice.dto.MaintenanceRequestResponse;
import com.rentalmanagement.rentalservice.enums.MaintenanceStatus;
import com.rentalmanagement.rentalservice.exception.InvalidCredentialsException;
import com.rentalmanagement.rentalservice.model.*;
import com.rentalmanagement.rentalservice.repository.LeaseRepository;
import com.rentalmanagement.rentalservice.repository.MaintenanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final LeaseRepository leaseRepository;

    @Transactional
    public MaintenanceRequestResponse createRequest(Tenant tenant, MaintenanceRequestDTO dto) {
        // Find active lease to link unit and property
        Lease activeLease = leaseRepository.findByTenantIdAndIsActiveTrue(tenant.getId());

        if (activeLease == null) {
            throw new RuntimeException("No active lease found. Cannot submit maintenance request.");
        }

        MaintenanceRequest request = MaintenanceRequest.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .priority(dto.getPriority())
                .status(MaintenanceStatus.PENDING)
                .tenant(tenant)
                .unit(activeLease.getUnit())
                .property(activeLease.getUnit().getProperty())
                .build();

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);
        return mapToResponse(saved);
    }

    public List<MaintenanceRequestResponse> getRequestsForTenant(Tenant tenant) {
        return maintenanceRequestRepository.findAllByTenantId(tenant.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MaintenanceRequestResponse> getRequestsForOwner(Owner owner) {
        return maintenanceRequestRepository.findAllByPropertyOwnerId(owner.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MaintenanceRequestResponse updateStatus(Long requestId, MaintenanceStatus status, Owner owner) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getProperty().getOwner().getId().equals(owner.getId())) {
            throw new InvalidCredentialsException("Access Denied");
        }

        request.setStatus(status);
        MaintenanceRequest updated = maintenanceRequestRepository.save(request);
        return mapToResponse(updated);
    }

    private MaintenanceRequestResponse mapToResponse(MaintenanceRequest request) {
        return MaintenanceRequestResponse.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .status(request.getStatus())
                .unitNumber(request.getUnit().getUnitNumber())
                .unitId(request.getUnit().getId())
                .propertyName(request.getProperty().getName())
                .tenantName(request.getTenant().getFullName())
                .tenantId(request.getTenant().getId())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
