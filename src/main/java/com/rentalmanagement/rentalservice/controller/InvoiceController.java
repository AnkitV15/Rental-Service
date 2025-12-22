package com.rentalmanagement.rentalservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalmanagement.rentalservice.dto.CreateInvoiceRequest;
import com.rentalmanagement.rentalservice.dto.InvoiceResponse;
import com.rentalmanagement.rentalservice.model.Invoice;
import com.rentalmanagement.rentalservice.repository.InvoiceRepository;
import com.rentalmanagement.rentalservice.service.InvoiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;

    @PostMapping("/generate")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceResponse invoice = invoiceService.createInvoice(request.getLeaseId(), request.getCurrentMeterReading());
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/lease/{leaseId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByLease(@PathVariable Long leaseId) {
        List<Invoice> invoices = invoiceRepository.findByLeaseId(leaseId);
        List<InvoiceResponse> responses = invoices.stream()
                .map(invoiceService::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
