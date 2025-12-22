package com.rentalmanagement.rentalservice.security;

/**
 * Central place for application role names used with Spring Security.
 */
public final class RoleConstants {

    private RoleConstants() {
        // Utility class
    }

    public static final String ROLE_OWNER = "ROLE_OWNER";
    public static final String ROLE_TENANT = "ROLE_TENANT";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
}


