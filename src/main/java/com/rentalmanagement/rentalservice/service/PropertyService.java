package com.rentalmanagement.rentalservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.rentalmanagement.rentalservice.dto.PropertyDTO;
import com.rentalmanagement.rentalservice.dto.PropertyResponse;
import com.rentalmanagement.rentalservice.model.Owner;
import com.rentalmanagement.rentalservice.model.Property;
import com.rentalmanagement.rentalservice.repository.PropertyRepository;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public PropertyResponse createProperty(PropertyDTO dto, MultipartFile file, Owner owner) {
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(file, "properties");
        }

        Property property = Property.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .owner(owner)
                .imageUrl(imageUrl)
                .build();
        Property saved = propertyRepository.save(property);
        return mapToResponse(saved);
    }

    public List<PropertyResponse> getAllProperties(Owner owner) {
        return propertyRepository.findAllByOwner(owner).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PropertyResponse getProperty(Long id, Owner owner) {
        Property property = propertyRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(owner.getId()))
                .orElseThrow(() -> new RuntimeException("Property not found or access denied"));
        return mapToResponse(property);
    }

    @Transactional
    public void deleteProperty(Long id, Owner owner) {
        Property property = propertyRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(owner.getId()))
                .orElseThrow(() -> new RuntimeException("Property not found or access denied"));
        propertyRepository.delete(property);
    }

    @Transactional
    public PropertyResponse updateProperty(Long id, PropertyDTO dto, MultipartFile file, Owner owner) {
        Property property = propertyRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(owner.getId()))
                .orElseThrow(() -> new RuntimeException("Property not found or access denied"));

        property.setName(dto.getName());
        property.setAddress(dto.getAddress());

        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(file, "properties");
            property.setImageUrl(imageUrl);
        }

        Property updated = propertyRepository.save(property);
        return mapToResponse(updated);
    }

    private PropertyResponse mapToResponse(Property property) {
        return PropertyResponse.builder()
                .id(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .ownerId(property.getOwner().getPublicId())
                .imageUrl(property.getImageUrl())
                .build();
    }
}
