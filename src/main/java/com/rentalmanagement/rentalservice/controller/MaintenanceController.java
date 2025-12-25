package com.rentalmanagement.rentalservice.controller;

import com.rentalmanagement.rentalservice.dto.MaintenanceRequestDTO;
import com.rentalmanagement.rentalservice.dto.MaintenanceRequestResponse;
import com.rentalmanagement.rentalservice.enums.MaintenanceStatus;
import com.rentalmanagement.rentalservice.model.Owner;
import com.rentalmanagement.rentalservice.model.Tenant;
import com.rentalmanagement.rentalservice.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping("/maintenance")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<MaintenanceRequestResponse> createRequest(
            @AuthenticationPrincipal Tenant tenant,
            @RequestBody MaintenanceRequestDTO dto) {
        return ResponseEntity.ok(maintenanceService.createRequest(tenant, dto));
    }

    @GetMapping("/maintenance/my-requests")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<MaintenanceRequestResponse>> getMyRequests(@AuthenticationPrincipal Tenant tenant) {
        return ResponseEntity.ok(maintenanceService.getRequestsForTenant(tenant));
    }

    @GetMapping("/owner/maintenance")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<MaintenanceRequestResponse>> getOwnerRequests(@AuthenticationPrincipal Owner owner) {
        return ResponseEntity.ok(maintenanceService.getRequestsForOwner(owner));
    }

    @PutMapping("/owner/maintenance/{id}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MaintenanceRequestResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal Owner owner) {
        String statusStr = payload.get("status");
        MaintenanceStatus status = MaintenanceStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(maintenanceService.updateStatus(id, status, owner));
    }
}
