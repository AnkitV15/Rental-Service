package com.rentalmanagement.rentalservice.repository;

import com.rentalmanagement.rentalservice.model.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findAllByTenantId(Long tenantId);

    List<MaintenanceRequest> findAllByPropertyOwnerId(Long ownerId);

    List<MaintenanceRequest> findAllByUnitId(Long unitId);
}
