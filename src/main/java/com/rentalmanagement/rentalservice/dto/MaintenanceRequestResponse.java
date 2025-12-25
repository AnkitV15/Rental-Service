package com.rentalmanagement.rentalservice.dto;

import com.rentalmanagement.rentalservice.enums.MaintenancePriority;
import com.rentalmanagement.rentalservice.enums.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequestResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private MaintenancePriority priority;
    private MaintenanceStatus status;
    private String unitNumber;
    private Long unitId;
    private String propertyName;
    private String tenantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
