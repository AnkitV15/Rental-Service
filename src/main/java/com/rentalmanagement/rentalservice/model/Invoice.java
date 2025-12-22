package com.rentalmanagement.rentalservice.model;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lease_id")
    private Lease lease;

    private String billingMonth; // e.g., "October 2025" or "2025-10"
    private Double rentAmount;
    private Double electricityAmount;
    private Double totalAmount;

    // Missing fields from InvoiceService usage
    private Double previousMeterReading;

    private String electricityBillUrl; // Link to Cloudinary

    // InvoiceService treats status as String ("PENDING"), but model had Status enum
    // Let's stick effectively to String or change Service.
    // Given the error `setStatus(String)`, sticking to String is easier for now or
    // converting enum.
    // However, best practice is Enum.
    // Let's use String for simplicity as per Service code, or better: fix Service?
    // User asked to resolve errors. Service tries to setStatus("PENDING").
    // Let's change this to String to match Service for now, or use @Enumerated.
    // The previous code had `private Status status`.
    // I will change it to String to match the `InvoiceService` logic which does
    // `.setStatus("PENDING")`
    private String status;

    @CreatedDate
    private Date generatedDate;

    private LocalDateTime createdAt;
    private LocalDateTime paymentDate;
    private String paymentId;

    private java.time.LocalDate dueDate;
    private java.time.LocalDate periodStart;
    private java.time.LocalDate periodEnd;
}
