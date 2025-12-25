package com.rentalmanagement.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaseDTO {
    private Long id;
    private String tenantName;
    private Long unitId;
    private String unitNumber;
    private Long propertyId;
    private String propertyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String status; // Derived from isActive
    private String accessToken; // Magic Link Token
}
