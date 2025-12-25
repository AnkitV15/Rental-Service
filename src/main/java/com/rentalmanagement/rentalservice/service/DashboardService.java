package com.rentalmanagement.rentalservice.service;

import com.rentalmanagement.rentalservice.dto.DashboardStatsDTO;
import com.rentalmanagement.rentalservice.model.Owner;
import com.rentalmanagement.rentalservice.repository.InvoiceRepository;
import com.rentalmanagement.rentalservice.repository.PropertyRepository;
import com.rentalmanagement.rentalservice.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;

    public DashboardStatsDTO getOwnerStats(Owner owner) {
        Long ownerId = owner.getId();

        Double totalRevenue = invoiceRepository.sumTotalRevenueByOwner(ownerId);
        if (totalRevenue == null)
            totalRevenue = 0.0;

        Integer totalUnits = unitRepository.countTotalUnitsByOwner(ownerId);
        if (totalUnits == null)
            totalUnits = 0;

        Integer occupiedUnits = unitRepository.countOccupiedUnitsByOwner(ownerId);
        if (occupiedUnits == null)
            occupiedUnits = 0;

        // Active Tenants can be considered same as occupied units or active leases
        // The previous query for leases was findAllByUnitPropertyOwnerId. We can reuse
        // or count.
        // Assuming 1 active lease per occupied unit roughly.
        // Let's keep it consistent with Occupied Units for now or count active leases
        // specifically.
        // But wait, unitRepository.countOccupiedUnitsByOwner is safer as it reflects
        // Unit Status.

        Integer totalProperties = propertyRepository.findAllByOwner(owner).size();

        double occupancyRate = 0.0;
        if (totalUnits > 0) {
            occupancyRate = ((double) occupiedUnits / totalUnits) * 100;
        }

        return DashboardStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .activeTenants(Long.valueOf(occupiedUnits))
                .occupancyRate(Math.round(occupancyRate * 10.0) / 10.0) // Round to 1 decimal
                .totalProperties(totalProperties)
                .totalUnits(totalUnits)
                .maintenanceRequests(0L) // TODO: Implement maintenance
                .build();
    }
}
