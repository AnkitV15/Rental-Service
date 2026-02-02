package com.rentalmanagement.rentalservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Rental Service Backend API is running. Access API at /api/... or check health at /actuator/health";
    }
}
