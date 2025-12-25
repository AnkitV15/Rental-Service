package com.rentalmanagement.rentalservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Username cannot be empty")
    private String username;
}
