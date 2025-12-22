package com.rentalmanagement.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn; // in milliseconds

    // Public, opaque identifier for the owner (UUID), safe to expose to clients
    private String ownerId;
    private String email;
    private String username;
    private String role;
}
