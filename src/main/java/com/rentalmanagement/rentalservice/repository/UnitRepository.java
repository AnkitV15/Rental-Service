package com.rentalmanagement.rentalservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rentalmanagement.rentalservice.model.Unit;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findAllByPropertyId(Long propertyId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) FROM Unit u WHERE u.property.owner.id = :ownerId")
    Integer countTotalUnitsByOwner(@org.springframework.data.repository.query.Param("ownerId") Long ownerId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) FROM Unit u WHERE u.status = 'OCCUPIED' AND u.property.owner.id = :ownerId")
    Integer countOccupiedUnitsByOwner(@org.springframework.data.repository.query.Param("ownerId") Long ownerId);
}
