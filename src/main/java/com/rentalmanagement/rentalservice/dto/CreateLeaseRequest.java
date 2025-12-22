package com.rentalmanagement.rentalservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateLeaseRequest {
    private Long unitId;
    private String tenantFullName;
    private String tenantEmail;
    private String tenantPhoneNumber;
    private LocalDate startDate;
    private LocalDate endDate;
}
