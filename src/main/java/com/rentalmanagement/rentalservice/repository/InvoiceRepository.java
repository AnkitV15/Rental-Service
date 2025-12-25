package com.rentalmanagement.rentalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rentalmanagement.rentalservice.model.Invoice;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByLeaseId(Long leaseId);

    List<Invoice> findByLeaseUnitId(Long unitId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND i.lease.unit.property.owner.id = :ownerId")
    Double sumTotalRevenueByOwner(@org.springframework.data.repository.query.Param("ownerId") Long ownerId);
}
