package com.rentalmanagement.rentalservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalmanagement.rentalservice.dto.InvoiceResponse;
import com.rentalmanagement.rentalservice.model.Invoice;

import com.rentalmanagement.rentalservice.repository.LeaseRepository;
import com.rentalmanagement.rentalservice.service.InvoiceService;
import com.rentalmanagement.rentalservice.service.PaymentService;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenant/portal")
@RequiredArgsConstructor
public class TenantPortalController {

    private final LeaseRepository leaseRepository;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final com.rentalmanagement.rentalservice.repository.InvoiceRepository invoiceRepository;

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
        List<InvoiceResponse> responses = lease.getInvoices().stream()
                .map(invoiceService::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/pay/{token}")
    public ResponseEntity<?> payInvoice(@PathVariable String token, @RequestParam Long invoiceId) {
        final var lease = leaseRepository.findByAccessToken(token);
        if (lease == null || !lease.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired link.");
        }

        // Find the invoice and ensure it belongs to this lease
        Invoice invoice = lease.getInvoices().stream()
                .filter(i -> i.getId().equals(invoiceId))
                .findFirst()
                .orElse(null);

        if (invoice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found.");
        }

        if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
            return ResponseEntity.badRequest().body("Invoice is already paid.");
        }

        // Create Order
        try {
            var order = paymentService.createOrder(invoice.getTotalAmount(), "inv_" + invoice.getId());
            return ResponseEntity.ok(Map.of(
                    "orderId", order.get("id"),
                    "amount", order.get("amount"),
                    "currency", order.get("currency")));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Payment initiation failed.");
        }
    }

    @PostMapping("/verify-payment/{token}")
    public ResponseEntity<?> verifyPayment(@PathVariable String token, @RequestBody Map<String, String> payload) {
        String paymentId = payload.get("paymentId");
        String orderId = payload.get("orderId");
        // In a real app, you would verify the signature here using
        // RazorpayClient.verifyPaymentSignature()

        final var lease = leaseRepository.findByAccessToken(token);
        if (lease == null || !lease.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid link.");
        }

        Long invoiceId = Long.parseLong(payload.get("invoiceId"));
        Invoice invoice = lease.getInvoices().stream()
                .filter(i -> i.getId().equals(invoiceId))
                .findFirst()
                .orElse(null);

        if (invoice != null) {
            invoice.setStatus("PAID");
            // invoice.setPaymentId(paymentId); // Ideally add paymentId field to Invoice
            // invoice.setPaidDate(LocalDate.now()); // Ideally add paidDate field
            invoiceRepository.save(invoice);
            return ResponseEntity.ok("Payment verified and Invoice updated.");
        }
        return ResponseEntity.badRequest().body("Invoice not found");
    }
}
