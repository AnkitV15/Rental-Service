package com.rentalmanagement.rentalservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.rentalmanagement.rentalservice.security.RoleConstants;

@Entity
@Table(name = "owners")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "properties", "password", "verificationToken", "verificationExpires" })
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String publicId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @Builder.Default
    private String role = RoleConstants.ROLE_OWNER;

    @JsonIgnore
    private String verificationToken;
    @JsonIgnore
    private LocalDateTime verificationExpires;

    private boolean isVerified;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Property> properties;
}
