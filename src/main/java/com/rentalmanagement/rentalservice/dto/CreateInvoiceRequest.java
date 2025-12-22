package com.rentalmanagement.rentalservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInvoiceRequest {
    @NotNull(message = "leaseId is required")
    private Long leaseId;

    // Optional for FIXED billing, required for METERED
    private Double currentMeterReading;
}
