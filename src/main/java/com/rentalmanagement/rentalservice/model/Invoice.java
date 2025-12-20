package com.rentalmanagement.rentalservice.model;

import com.rentalmanagement.rentalservice.enums.Status;
import java.time.LocalDateTime;

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
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lease_id")
    private Lease lease;

    private String billingMonth; // e.g., "October 2025"
    private Double rentAmount;
    private Double electricityAmount;
    private Double totalAmount;

    private String electricityBillUrl; // Link to Cloudinary

    private Status status = Status.PENDING; // UNPAID, PAID
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime paymentDate;
}
