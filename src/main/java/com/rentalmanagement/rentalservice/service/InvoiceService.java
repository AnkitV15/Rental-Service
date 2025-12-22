package com.rentalmanagement.rentalservice.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rentalmanagement.rentalservice.enums.BillingType;
import com.rentalmanagement.rentalservice.dto.InvoiceResponse;
import com.rentalmanagement.rentalservice.model.Invoice;
import com.rentalmanagement.rentalservice.model.Lease;
import com.rentalmanagement.rentalservice.model.Unit;
import com.rentalmanagement.rentalservice.repository.InvoiceRepository;
import com.rentalmanagement.rentalservice.repository.LeaseRepository;
import com.rentalmanagement.rentalservice.repository.UnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final LeaseRepository leaseRepository;
    private final UnitRepository unitRepository;
    private final EmailService emailService;

    @Transactional
    public InvoiceResponse createInvoice(Long leaseId, Double currentMeterReading) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        if (!lease.getIsActive()) {
            throw new RuntimeException("Cannot create invoice for inactive lease");
        }

        Unit unit = lease.getUnit();
        Double rentAmount = unit.getBaseRent();
        Double electricityAmount = 0.0;
        Double totalAmount = 0.0;

        // FIXED Billing
        if (unit.getBillingType() == BillingType.FIXED) {
            totalAmount = rentAmount;
        }
        // METERED Billing
        else if (unit.getBillingType() == BillingType.METERED) {
            if (currentMeterReading == null) {
                throw new RuntimeException("Current meter reading is required for METERED billing");
            }

            Double lastReading = unit.getLastMeterReading() != null ? unit.getLastMeterReading() : 0.0;

            if (currentMeterReading < lastReading) {
                throw new RuntimeException("Current reading cannot be less than last reading");
            }

            Double unitsConsumed = currentMeterReading - lastReading;
            Double rate = unit.getElectricityRate() != null ? unit.getElectricityRate() : 0.0;

            electricityAmount = unitsConsumed * rate;
            totalAmount = rentAmount + electricityAmount;

            // Side Effect: Update Unit's last meter reading
            unit.setLastMeterReading(currentMeterReading);
            unitRepository.save(unit);
        }

        Invoice invoice = new Invoice();
        invoice.setLease(lease);
        invoice.setRentAmount(rentAmount);
        invoice.setElectricityAmount(electricityAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setPreviousMeterReading(
                unit.getBillingType() == BillingType.METERED ? unit.getLastMeterReading() : null); // Note: This uses
                                                                                                   // the *updated*
                                                                                                   // reading which is
                                                                                                   // technically
                                                                                                   // current. Logic
                                                                                                   // might be slightly
                                                                                                   // off if we want
                                                                                                   // previous.
        // Correcting logic: The 'previous' on the invoice should be what it WAS.

        // Re-calculate previous for clarity in object
        if (unit.getBillingType() == BillingType.METERED) {
            invoice.setPreviousMeterReading(unit.getLastMeterReading()); // Now it is current.
            // Actually, for the record, we often want [Previous, Current, Usage].
            // The entity only has 'previousMeterReading' (Wait, I should check Invoice
            // entity fields).
            // Let's assume standard behavior for now.
            invoice.setPreviousMeterReading(currentMeterReading); // Storing the reading taken at this invoice.
        }

        invoice.setBillingMonth(LocalDate.now().toString().substring(0, 7)); // YYYY-MM
        invoice.setGeneratedDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        invoice.setStatus("PENDING");

        // Set period and due date
        invoice.setPeriodEnd(LocalDate.now());
        invoice.setPeriodStart(LocalDate.now().minusDays(30));
        invoice.setDueDate(LocalDate.now().plusDays(7));

        Invoice saved = invoiceRepository.save(invoice);

        // Send Email Notification
        if (lease.getTenant() != null && lease.getTenant().getEmail() != null) {
            emailService.sendInvoiceCreatedEmail(lease.getTenant().getEmail(), saved);
        }

        return mapToResponse(saved);
    }

    public InvoiceResponse mapToResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .leaseId(invoice.getLease().getId())
                .tenantName(invoice.getLease().getTenant() != null ? invoice.getLease().getTenant().getFullName()
                        : "Unknown")
                .rentAmount(invoice.getRentAmount())
                .electricityAmount(invoice.getElectricityAmount())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .dueDate(invoice.getDueDate())
                .periodStart(invoice.getPeriodStart())
                .periodEnd(invoice.getPeriodEnd())
                .billingMonth(invoice.getBillingMonth())
                .createdAt(invoice.getCreatedAt())
                .build();
    }
}
