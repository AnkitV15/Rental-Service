package com.rentalmanagement.rentalservice.controller;

import com.rentalmanagement.rentalservice.dto.CreateLeaseRequest;
import com.rentalmanagement.rentalservice.model.Lease;
import com.rentalmanagement.rentalservice.service.LeaseService;
import lombok.RequiredArgsConstructor;
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
}
