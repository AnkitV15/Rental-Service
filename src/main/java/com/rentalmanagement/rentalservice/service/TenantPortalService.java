package com.rentalmanagement.rentalservice.service;

import com.rentalmanagement.rentalservice.dto.InvoiceResponse;
import com.rentalmanagement.rentalservice.model.Invoice;
import com.rentalmanagement.rentalservice.model.Lease;
import com.rentalmanagement.rentalservice.repository.InvoiceRepository;
import com.rentalmanagement.rentalservice.repository.LeaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TenantPortalService {

    private final LeaseRepository leaseRepository;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final InvoiceRepository invoiceRepository;

    public List<InvoiceResponse> getInvoices(String token) {
        Lease lease = validateToken(token);
        return lease.getInvoices().stream()
                .map(invoiceService::mapToResponse)
                .toList();
    }

    public Map<String, Object> initiatePayment(String token, Long invoiceId) {
        Lease lease = validateToken(token);
        Invoice invoice = findInvoice(lease, invoiceId);

        if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
            throw new RuntimeException("Invoice is already paid.");
        }

        try {
            var order = paymentService.createOrder(invoice.getTotalAmount(), "inv_" + invoice.getId());
            return Map.of(
                    "orderId", order.get("id"),
                    "amount", order.get("amount"),
                    "currency", order.get("currency"));
        } catch (Exception e) {
            throw new RuntimeException("Payment initiation failed", e);
        }
    }

    @Transactional
    public void verifyPayment(String token, Map<String, String> payload) {
        String paymentId = payload.get("paymentId");
        String orderId = payload.get("orderId");
        String signature = payload.get("signature");

        boolean isValid;
        try {
            isValid = paymentService.verifySignature(orderId, paymentId, signature);
        } catch (Exception e) {
            throw new RuntimeException("Error verifying payment signature", e);
        }

        if (!isValid) {
            throw new RuntimeException("Invalid Payment Signature");
        }

        Lease lease = validateToken(token);
        Long invoiceId = Long.parseLong(payload.get("invoiceId"));
        Invoice invoice = findInvoice(lease, invoiceId);

        invoice.setStatus("PAID");
        invoice.setPaymentId(paymentId);
        invoice.setPaymentDate(LocalDateTime.now());
        invoiceRepository.save(invoice);
    }

    private Lease validateToken(String token) {
        Lease lease = leaseRepository.findByAccessToken(token);
        if (lease == null || !lease.getIsActive()) {
            throw new RuntimeException("Invalid or expired link.");
        }
        return lease;
    }

    private Invoice findInvoice(Lease lease, Long invoiceId) {
        return lease.getInvoices().stream()
                .filter(i -> i.getId().equals(invoiceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invoice not found or does not belong to this lease."));
    }
}
