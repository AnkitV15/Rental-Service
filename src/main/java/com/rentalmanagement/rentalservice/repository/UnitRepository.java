package com.rentalmanagement.rentalservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rentalmanagement.rentalservice.model.Unit;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findAllByPropertyId(Long propertyId);
}
