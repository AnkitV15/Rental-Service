package com.rentalmanagement.rentalservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalmanagement.rentalservice.repository.LeaseRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenant/portal")
@RequiredArgsConstructor
public class TenantPortalController {

    private final LeaseRepository leaseRepository;

    @GetMapping("/view/{token}")
    public ResponseEntity<?> getTenantInvoice(@PathVariable String token) {
        // We find the lease by the unique Magic Link token
        final var lease = leaseRepository.findByAccessToken(token);
        if (lease == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // IMPORTANT: If the lease is expired (is_active = false), block access
        if (!lease.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This link has expired.");
        }
        return ResponseEntity.ok(lease.getInvoices()); // Return their specific history
    }
}
