package com.rentalmanagement.rentalservice.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rentalmanagement.rentalservice.model.Owner;
import com.rentalmanagement.rentalservice.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final OwnerRepository OwnerRepository;

    @Override
    public UserDetails loadUserByUsername(String emailString) throws UsernameNotFoundException {
        Owner owner = OwnerRepository.findByEmail(emailString)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailString));
        return new org.springframework.security.core.userdetails.User(owner.getEmail(), owner.getPasswordHash(),
                new java.util.ArrayList<>());
    }

}
