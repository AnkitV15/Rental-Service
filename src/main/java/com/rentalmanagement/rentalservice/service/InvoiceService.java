package com.rentalmanagement.rentalservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public InvoiceResponse createInvoice(Long unitId, Double currentMeterReading) {
        Lease lease = leaseRepository.findByUnitIdAndIsActiveTrue(unitId);

        if (lease == null) {
            throw new RuntimeException("No active lease found for this unit");
        }

        Unit unit = lease.getUnit();
        Double rentAmount = unit.getBaseRent();
        Double electricityAmount = 0.0;
        Double totalAmount = 0.0;
        Double usage = 0.0;

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

            usage = currentMeterReading - lastReading;
            Double rate = unit.getElectricityRate() != null ? unit.getElectricityRate() : 0.0;

            electricityAmount = usage * rate;
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

        if (unit.getBillingType() == BillingType.METERED) {
            invoice.setCurrentMeterReading(currentMeterReading);
            invoice.setUsage(usage);
            // Previous is Current - Usage
            invoice.setPreviousMeterReading(currentMeterReading - usage);
        } else {
            invoice.setCurrentMeterReading(null);
            invoice.setUsage(null);
            invoice.setPreviousMeterReading(null);
        }

        invoice.setBillingMonth(LocalDate.now().toString().substring(0, 7)); // YYYY-MM
        invoice.setGeneratedDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        invoice.setCreatedAt(LocalDateTime.now());
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
                .currentMeterReading(invoice.getCurrentMeterReading())
                .usage(invoice.getUsage())
                .status(invoice.getStatus())
                .dueDate(invoice.getDueDate())
                .periodStart(invoice.getPeriodStart())
                .periodEnd(invoice.getPeriodEnd())
                .billingMonth(invoice.getBillingMonth())
                .paymentId(invoice.getPaymentId())
                .createdAt(invoice.getCreatedAt())
                .build();
    }
}
