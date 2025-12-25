package com.rentalmanagement.rentalservice.controller;

import com.rentalmanagement.rentalservice.dto.InvoiceResponse;
import com.rentalmanagement.rentalservice.dto.CreateLeaseRequest;
import com.rentalmanagement.rentalservice.model.Lease;
import com.rentalmanagement.rentalservice.service.LeaseService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leases")
@RequiredArgsConstructor
public class LeaseController {

    private final LeaseService leaseService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Lease> createLease(@RequestBody CreateLeaseRequest request) {
        return ResponseEntity.ok(leaseService.createLease(request));
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<Lease> verifyLease(@PathVariable String token) {
        return ResponseEntity.ok(leaseService.getLeaseByToken(token));
    }

    @GetMapping("/verify/{token}/invoices")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByToken(@PathVariable String token) {
        return ResponseEntity.ok(leaseService.getInvoicesByToken(token));
    }

    @GetMapping("/verify/{token}/maintenance")
    public ResponseEntity<List<com.rentalmanagement.rentalservice.dto.MaintenanceRequestResponse>> getMaintenanceRequestsByToken(
            @PathVariable String token) {
        return ResponseEntity.ok(leaseService.getMaintenanceRequestsByToken(token));
    }

    @PostMapping("/verify/{token}/maintenance")
    public ResponseEntity<com.rentalmanagement.rentalservice.dto.MaintenanceRequestResponse> createMaintenanceRequestByToken(
            @PathVariable String token,
            @RequestBody com.rentalmanagement.rentalservice.dto.MaintenanceRequestDTO dto) {
        return ResponseEntity.ok(leaseService.createMaintenanceRequestByToken(token, dto));
    }

    @GetMapping("/unit/{unitId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<Lease>> getLeasesByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(leaseService.getLeasesByUnitId(unitId));
    }

    @GetMapping("/unit/{unitId}/active")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<com.rentalmanagement.rentalservice.dto.LeaseDTO> getActiveLeaseForUnit(
            @PathVariable Long unitId) {
        return ResponseEntity.ok(leaseService.getActiveLeaseDTOByUnitId(unitId));
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<com.rentalmanagement.rentalservice.dto.LeaseDTO>> getLeasesForOwner(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.rentalmanagement.rentalservice.model.Owner owner) {
        return ResponseEntity.ok(leaseService.getAllLeasesForOwner(owner));
    }
}
