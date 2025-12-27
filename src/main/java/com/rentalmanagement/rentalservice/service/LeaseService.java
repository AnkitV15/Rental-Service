package com.rentalmanagement.rentalservice.service;

import com.rentalmanagement.rentalservice.dto.CreateLeaseRequest;
import com.rentalmanagement.rentalservice.model.Lease;
import com.rentalmanagement.rentalservice.model.Tenant;
import com.rentalmanagement.rentalservice.model.Unit;
import com.rentalmanagement.rentalservice.repository.LeaseRepository;
import com.rentalmanagement.rentalservice.repository.TenantRepository;
import com.rentalmanagement.rentalservice.repository.UnitRepository;
import com.rentalmanagement.rentalservice.enums.UnitStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final TenantRepository tenantRepository;
    private final UnitRepository unitRepository;
    private final com.rentalmanagement.rentalservice.repository.InvoiceRepository invoiceRepository;
    private final com.rentalmanagement.rentalservice.repository.MaintenanceRequestRepository maintenanceRequestRepository;

    @Transactional
    public com.rentalmanagement.rentalservice.dto.MaintenanceRequestResponse createMaintenanceRequestByToken(
            String token, com.rentalmanagement.rentalservice.dto.MaintenanceRequestDTO dto) {
        Lease lease = getLeaseByToken(token);

        com.rentalmanagement.rentalservice.model.MaintenanceRequest request = com.rentalmanagement.rentalservice.model.MaintenanceRequest
                .builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .priority(dto.getPriority())
                .status(com.rentalmanagement.rentalservice.enums.MaintenanceStatus.PENDING)
                .tenant(lease.getTenant())
                .unit(lease.getUnit())
                .property(lease.getUnit().getProperty())
                .build();

        com.rentalmanagement.rentalservice.model.MaintenanceRequest saved = maintenanceRequestRepository.save(request);
        return mapToMaintenanceResponse(saved);
    }

    public List<com.rentalmanagement.rentalservice.dto.MaintenanceRequestResponse> getMaintenanceRequestsByToken(
            String token) {
        Lease lease = getLeaseByToken(token);
        List<com.rentalmanagement.rentalservice.model.MaintenanceRequest> requests = maintenanceRequestRepository
                .findAllByTenantId(lease.getTenant().getId());
        return requests.stream().map(this::mapToMaintenanceResponse).collect(java.util.stream.Collectors.toList());
    }

    private com.rentalmanagement.rentalservice.dto.MaintenanceRequestResponse mapToMaintenanceResponse(
            com.rentalmanagement.rentalservice.model.MaintenanceRequest request) {
        return com.rentalmanagement.rentalservice.dto.MaintenanceRequestResponse.builder()
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
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    @Transactional
    public Lease createLease(CreateLeaseRequest request) {
        // ... (existing code)
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

        // Update Unit Status
        unit.setStatus(UnitStatus.OCCUPIED);
        unitRepository.save(unit);

        return leaseRepository.save(lease);
    }

    public List<Lease> getLeasesByUnitId(Long unitId) {
        return leaseRepository.findAllByUnitId(unitId);
    }

    public Lease getLeaseByToken(String token) {
        Lease lease = leaseRepository.findByAccessToken(token);
        if (lease == null || !lease.getIsActive()) {
            throw new RuntimeException("Invalid or inactive lease");
        }
        return lease;
    }

    public List<com.rentalmanagement.rentalservice.dto.InvoiceResponse> getInvoicesByToken(String token) {
        Lease lease = getLeaseByToken(token);
        List<com.rentalmanagement.rentalservice.model.Invoice> invoices = invoiceRepository
                .findByLeaseId(lease.getId());

        return invoices.stream().map(invoice -> com.rentalmanagement.rentalservice.dto.InvoiceResponse.builder()
                .id(invoice.getId())
                .leaseId(invoice.getLease().getId())
                .tenantName(invoice.getLease().getTenant() != null ? invoice.getLease().getTenant().getFullName()
                        : "Unknown")
                .rentAmount(invoice.getRentAmount())
                .electricityAmount(invoice.getElectricityAmount())
                .totalAmount(invoice.getTotalAmount())
                .currentMeterReading(invoice.getCurrentMeterReading())
                .usage(invoice.getUsage())
                .status(invoice.getStatus())
                .dueDate(invoice.getDueDate())
                .periodStart(invoice.getPeriodStart())
                .periodEnd(invoice.getPeriodEnd())
                .billingMonth(invoice.getBillingMonth())
                .createdAt(invoice.getCreatedAt())
                .build()).collect(java.util.stream.Collectors.toList());
    }

    public List<com.rentalmanagement.rentalservice.dto.LeaseDTO> getAllLeasesForOwner(
            com.rentalmanagement.rentalservice.model.Owner owner) {
        List<Lease> leases = leaseRepository.findAllByUnitPropertyOwnerId(owner.getId());
        return leases.stream().map(this::mapToDTO).collect(java.util.stream.Collectors.toList());
    }

    public com.rentalmanagement.rentalservice.dto.LeaseDTO getActiveLeaseDTOByUnitId(Long unitId) {
        Lease lease = leaseRepository.findByUnitIdAndIsActiveTrue(unitId);
        if (lease == null) {
            return null;
        }
        return mapToDTO(lease);
    }

    private com.rentalmanagement.rentalservice.dto.LeaseDTO mapToDTO(Lease lease) {
        return com.rentalmanagement.rentalservice.dto.LeaseDTO.builder()
                .id(lease.getId())
                .tenantName(lease.getTenant() != null ? lease.getTenant().getFullName() : "Unknown")
                .tenantEmail(lease.getTenant() != null ? lease.getTenant().getEmail() : null)
                .tenantPhoneNumber(lease.getTenant() != null ? lease.getTenant().getPhoneNumber() : null)
                .tenantId(lease.getTenant() != null ? lease.getTenant().getId() : null)
                .unitId(lease.getUnit().getId())
                .unitNumber(lease.getUnit().getUnitNumber())
                .propertyId(lease.getUnit().getProperty().getId())
                .propertyName(lease.getUnit().getProperty().getName())
                .startDate(lease.getStartDate())
                .endDate(lease.getEndDate())
                .isActive(lease.getIsActive())
                .status(lease.getIsActive() ? "Active" : "Inactive")
                .accessToken(lease.getAccessToken())
                .build();
    }
}
