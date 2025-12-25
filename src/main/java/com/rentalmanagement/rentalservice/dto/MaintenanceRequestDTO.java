package com.rentalmanagement.rentalservice.dto;

import com.rentalmanagement.rentalservice.enums.MaintenancePriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequestDTO {
    private String title;
    private String description;
    private String category;
    private MaintenancePriority priority;
}
