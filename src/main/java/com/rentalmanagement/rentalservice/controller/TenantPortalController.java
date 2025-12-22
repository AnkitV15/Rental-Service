package com.rentalmanagement.rentalservice.controller;

import com.rentalmanagement.rentalservice.service.TenantPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tenant/portal")
@RequiredArgsConstructor
public class TenantPortalController {

    private final TenantPortalService tenantPortalService;

    @GetMapping("/view/{token}")
    public ResponseEntity<?> getTenantInvoice(@PathVariable String token) {
        try {
            return ResponseEntity.ok(tenantPortalService.getInvoices(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/pay/{token}")
    public ResponseEntity<?> payInvoice(@PathVariable String token, @RequestParam Long invoiceId) {
        try {
            return ResponseEntity.ok(tenantPortalService.initiatePayment(token, invoiceId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-payment/{token}")
    public ResponseEntity<?> verifyPayment(@PathVariable String token, @RequestBody Map<String, String> payload) {
        try {
            tenantPortalService.verifyPayment(token, payload);
            return ResponseEntity.ok("Payment verified and Invoice updated.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
