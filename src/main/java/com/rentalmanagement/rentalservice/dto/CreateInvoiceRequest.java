package com.rentalmanagement.rentalservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInvoiceRequest {
    @NotNull(message = "unitId is required")
    private Long unitId;

    // Optional for FIXED billing, required for METERED
    private Double currentMeterReading;
}
