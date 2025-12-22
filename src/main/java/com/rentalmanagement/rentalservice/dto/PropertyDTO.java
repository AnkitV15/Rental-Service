package com.rentalmanagement.rentalservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {

    @NotBlank(message = "Property name is required")
    private String name;

    @NotBlank(message = "Property address is required")
    private String address;
}
