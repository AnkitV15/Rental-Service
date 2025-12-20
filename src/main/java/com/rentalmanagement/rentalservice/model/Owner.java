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

@Entity
@Table(name = "owners")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;
    // Store enum as String in DB
    @Builder.Default
    private String role = "ROLE_OWNER";

    private String verificationToken;
    private LocalDateTime verificationExpires;

    private boolean isVerified;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Property> properties;
}
