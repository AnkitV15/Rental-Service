package com.rentalmanagement.rentalservice.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rentalmanagement.rentalservice.dto.PropertyDTO;
import com.rentalmanagement.rentalservice.dto.PropertyResponse;
import com.rentalmanagement.rentalservice.dto.UnitDTO;
import com.rentalmanagement.rentalservice.dto.UnitResponse;
import com.rentalmanagement.rentalservice.model.Owner;
import com.rentalmanagement.rentalservice.model.Unit;
import com.rentalmanagement.rentalservice.service.PropertyService;
import com.rentalmanagement.rentalservice.service.UnitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerPropertyController {

    private final UnitService unitService;
    private final PropertyService propertyService;

    @PostMapping("/create-unit")
    public ResponseEntity<UnitResponse> createUnit(@Valid @RequestBody UnitDTO unitDto,
            @AuthenticationPrincipal Owner owner) {
        Unit created = unitService.createUnit(unitDto, owner);

        UnitResponse response = UnitResponse.builder()
                .id(created.getId())
                .unitNumber(created.getUnitNumber())
                .baseRent(created.getBaseRent())
                .billingType(created.getBillingType())
                .electricityRate(created.getElectricityRate())
                .lastMeterReading(created.getLastMeterReading())
                .status(created.getStatus())
                .propertyId(created.getProperty().getId())
                .propertyName(created.getProperty().getName())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/create-property", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProperty(@Valid @RequestPart("property") PropertyDTO propertyDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Owner owner) {
        PropertyResponse created = propertyService.createProperty(propertyDto, file, owner);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/properties")
    public ResponseEntity<List<PropertyResponse>> getAllProperties(@AuthenticationPrincipal Owner owner) {
        return ResponseEntity.ok(propertyService.getAllProperties(owner));
    }

    @GetMapping("/properties/{id}")
    public ResponseEntity<PropertyResponse> getProperty(@PathVariable Long id, @AuthenticationPrincipal Owner owner) {
        return ResponseEntity.ok(propertyService.getProperty(id, owner));
    }

    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id, @AuthenticationPrincipal Owner owner) {
        propertyService.deleteProperty(id, owner);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/properties/{id}/units")
    public ResponseEntity<List<UnitResponse>> getUnitsByProperty(@PathVariable Long id,
            @AuthenticationPrincipal Owner owner) {
        return ResponseEntity.ok(unitService.getAllUnitsByPropertyId(id, owner));
    }

    @GetMapping("/units/{id}")
    public ResponseEntity<UnitResponse> getUnit(@PathVariable Long id, @AuthenticationPrincipal Owner owner) {
        return ResponseEntity.ok(unitService.getUnit(id, owner));
    }

    @DeleteMapping("/units/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id, @AuthenticationPrincipal Owner owner) {
        unitService.deleteUnit(id, owner);
        return ResponseEntity.noContent().build();
    }
}
