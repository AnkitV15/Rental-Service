package com.rentalmanagement.rentalservice.model;

import com.rentalmanagement.rentalservice.enums.BillingType;
import com.rentalmanagement.rentalservice.enums.UnitStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "units")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String unitNumber;
    private Double baseRent;

    // "FIXED" or "METERED"
    private BillingType billingType;

    private Double electricityRate;
    private Double lastMeterReading;

    @Enumerated(EnumType.STRING)
    private UnitStatus status = UnitStatus.VACANT;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
}
