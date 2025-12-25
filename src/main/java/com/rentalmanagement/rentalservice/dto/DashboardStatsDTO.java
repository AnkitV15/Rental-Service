package com.rentalmanagement.rentalservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {
    private Double totalRevenue;
    private Long activeTenants;
    private Double occupancyRate;
    private Integer totalProperties;
    private Integer totalUnits;
    private Long maintenanceRequests; // Placeholder for now
}
