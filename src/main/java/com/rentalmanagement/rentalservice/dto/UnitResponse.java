package com.rentalmanagement.rentalservice.dto;

import com.rentalmanagement.rentalservice.enums.BillingType;
import com.rentalmanagement.rentalservice.enums.UnitStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnitResponse {
    private Long id;
    private String unitNumber;
    private Double baseRent;
    private BillingType billingType;
    private Double electricityRate;
    private Double lastMeterReading;
    private UnitStatus status;
    private Long propertyId;
    private String propertyName;
}
