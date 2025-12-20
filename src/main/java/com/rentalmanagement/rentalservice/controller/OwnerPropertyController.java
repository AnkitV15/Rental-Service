package com.rentalmanagement.rentalservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalmanagement.rentalservice.dto.UnitDTO;

@RestController
@RequestMapping("/api/owner")
public class OwnerPropertyController {

    @PostMapping("/create-unit")
    public ResponseEntity<?> createUnit(@RequestBody UnitDTO unitDto) {
        // Only an authenticated Owner can reach this line
        return ResponseEntity.ok("Unit Created successfully");
    }
}
