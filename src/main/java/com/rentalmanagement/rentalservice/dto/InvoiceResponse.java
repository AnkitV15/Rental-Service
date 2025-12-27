package com.rentalmanagement.rentalservice.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceResponse {
    private Long id;
    private Long leaseId;
    private String tenantName;
    private Double rentAmount;
    private Double electricityAmount;
    private Double totalAmount;
    private Double currentMeterReading;
    private Double usage;
    private String status;
    private LocalDate dueDate;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String billingMonth;
    private String paymentId;
    private LocalDateTime createdAt;
}
