package com.rentalmanagement.rentalservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import com.rentalmanagement.rentalservice.dto.UnitDTO;
import com.rentalmanagement.rentalservice.dto.UnitResponse;
import com.rentalmanagement.rentalservice.enums.UnitStatus;
import com.rentalmanagement.rentalservice.exception.InvalidCredentialsException;
import com.rentalmanagement.rentalservice.model.Owner;
import com.rentalmanagement.rentalservice.model.Property;
import com.rentalmanagement.rentalservice.model.Unit;
import com.rentalmanagement.rentalservice.repository.PropertyRepository;
import com.rentalmanagement.rentalservice.repository.UnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;

    @Transactional
    public Unit createUnit(UnitDTO dto, Owner owner) {
        if (owner == null || owner.getId() == null) {
            throw new InvalidCredentialsException("Unauthorized");
        }

        Property property = propertyRepository.findByIdAndOwnerId(dto.getPropertyId(), owner.getId())
                .orElseThrow(() -> new InvalidCredentialsException("Property not found for this owner"));

        Unit unit = new Unit();
        unit.setUnitNumber(dto.getUnitNumber());
        unit.setBaseRent(dto.getBaseRent());
        unit.setBillingType(dto.getBillingType());
        unit.setElectricityRate(dto.getElectricityRate());
        unit.setLastMeterReading(dto.getLastMeterReading());
        unit.setStatus(dto.getStatus() == null ? UnitStatus.VACANT : dto.getStatus());
        unit.setProperty(property);

        return unitRepository.save(unit);
    }

    public List<UnitResponse> getAllUnitsByPropertyId(Long propertyId, Owner owner) {
        Property property = propertyRepository.findByIdAndOwnerId(propertyId, owner.getId())
                .orElseThrow(() -> new InvalidCredentialsException("Property not found or access denied"));

        return unitRepository.findAllByPropertyId(property.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UnitResponse getUnit(Long id, Owner owner) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (!unit.getProperty().getOwner().getId().equals(owner.getId())) {
            throw new InvalidCredentialsException("Access denied");
        }

        return mapToResponse(unit);
    }

    @Transactional
    public void deleteUnit(Long id, Owner owner) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (!unit.getProperty().getOwner().getId().equals(owner.getId())) {
            throw new InvalidCredentialsException("Access denied");
        }

        unitRepository.delete(unit);
    }

    @Transactional
    public UnitResponse updateUnit(Long id, UnitDTO dto, Owner owner) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (!unit.getProperty().getOwner().getId().equals(owner.getId())) {
            throw new InvalidCredentialsException("Access denied");
        }

        unit.setUnitNumber(dto.getUnitNumber());
        unit.setBaseRent(dto.getBaseRent());
        unit.setBillingType(dto.getBillingType());
        unit.setElectricityRate(dto.getElectricityRate());

        // Only update meter reading if provided, as it might be handled separately
        if (dto.getLastMeterReading() != null) {
            unit.setLastMeterReading(dto.getLastMeterReading());
        }

        Unit updated = unitRepository.save(unit);
        return mapToResponse(updated);
    }

    private UnitResponse mapToResponse(Unit unit) {
        return UnitResponse.builder()
                .id(unit.getId())
                .unitNumber(unit.getUnitNumber())
                .baseRent(unit.getBaseRent())
                .billingType(unit.getBillingType())
                .electricityRate(unit.getElectricityRate())
                .lastMeterReading(unit.getLastMeterReading())
                .status(unit.getStatus())
                .propertyId(unit.getProperty().getId())
                .propertyName(unit.getProperty().getName())
                .build();
    }
}
