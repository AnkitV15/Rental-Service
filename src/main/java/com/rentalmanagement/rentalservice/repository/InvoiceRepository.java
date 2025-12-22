package com.rentalmanagement.rentalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rentalmanagement.rentalservice.model.Invoice;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByLeaseId(Long leaseId);
}
