package com.rentalmanagement.rentalservice.dto;

import com.rentalmanagement.rentalservice.enums.BillingType;
import com.rentalmanagement.rentalservice.enums.UnitStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {

    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotBlank(message = "unitNumber is required")
    private String unitNumber;

    @NotNull(message = "baseRent is required")
    @Min(value = 0, message = "baseRent must be >= 0")
    private Double baseRent;

    @NotNull(message = "billingType is required")
    private BillingType billingType;

    // Optional, depending on billing type
    private Double electricityRate;
    private Double lastMeterReading;

    private UnitStatus status = UnitStatus.VACANT;
}
