package com.rentalmanagement.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {
    private String propertyId;
    private String unitNumber;
    private String unitType;
    private Double rentAmount;
    private Integer numberOfBedrooms;
    private Double numberOfBathrooms;
    private Boolean isOccupied;
}
