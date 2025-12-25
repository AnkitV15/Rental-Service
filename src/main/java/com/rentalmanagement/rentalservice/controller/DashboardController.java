package com.rentalmanagement.rentalservice.controller;

import com.rentalmanagement.rentalservice.dto.DashboardStatsDTO;
import com.rentalmanagement.rentalservice.model.Owner;
import com.rentalmanagement.rentalservice.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owner/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(@AuthenticationPrincipal Owner owner) {
        return ResponseEntity.ok(dashboardService.getOwnerStats(owner));
    }
}
