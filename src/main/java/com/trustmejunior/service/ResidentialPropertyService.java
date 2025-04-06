package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.repository.ResidentialPropertyRepository;
import com.trustmejunior.repository.PropertyRepository;
import com.trustmejunior.request.CreateResidentialPropertyRequest;
import com.trustmejunior.request.UpdateResidentialPropertyRequest;

import java.util.List;

public class ResidentialPropertyService {
    private ResidentialPropertyRepository residentialPropertyRepository;
    private PropertyRepository propertyRepository;
    private PropertyService propertyService;

    public ResidentialPropertyService() {
        this.residentialPropertyRepository = new ResidentialPropertyRepository();
        this.propertyRepository = new PropertyRepository();
        this.propertyService = new PropertyService();
    }

    public ResidentialProperty getResidentialPropertyById(int propertyId) {
        return residentialPropertyRepository.getResidentialPropertyById(propertyId);
    }

    public List<ResidentialProperty> getAllResidentialProperties() {
        return residentialPropertyRepository.getAllResidentialProperties();
    }

    // Validates the number of bedrooms in a residential property.
    private void validateResidentialPropertyData(int numberOfBedrooms) throws Exception {
        // Ensure number of bedrooms is within valid range
        if (numberOfBedrooms <= 0) {
            throw new Exception("Number of bedrooms must be greater than 0");
        }
        if (numberOfBedrooms > 20) {
            throw new Exception("Number of bedrooms cannot exceed 20");
        }
    }

    // Creates a new residential property based on provided request data.
    public ResidentialProperty createResidentialProperty(CreateResidentialPropertyRequest request) throws Exception {
        // Retrieve property data from the request
        double price = request.getPrice();
        String address = request.getAddress();
        PropertyStatus status = request.getStatus();
        PropertyType type = request.getType();
        int ownerId = request.getOwnerId();

        // Validate general property data
        propertyService.validatePropertyData(price, address, status, type, ownerId);

        // Validate residential property-specific data
        int numberOfBedrooms = request.getNumberOfBedrooms();
        boolean hasGarden = request.isHasGarden();
        boolean isPetFriendly = request.isPetFriendly();
        validateResidentialPropertyData(numberOfBedrooms);

        // Create the general property and check for success
        Property property = propertyRepository.createProperty(price, address, status, type, ownerId);
        if (property == null) {
            throw new Exception("Failed to create property");
        }

        // Create the residential property and return it
        int propertyId = property.getPropertyId();
        return residentialPropertyRepository.createResidentialProperty(propertyId, numberOfBedrooms, hasGarden,
                isPetFriendly);
    }

    // Updates an existing residential property based on the provided request data.
    public ResidentialProperty updateResidentialProperty(int rPropertyId, UpdateResidentialPropertyRequest request)
            throws Exception {
        // Validate the residential property ID
        if (rPropertyId <= 0) {
            throw new Exception("Invalid residential property ID");
        }

        // Retrieve and validate property data from the request
        double price = request.getPrice();
        String address = request.getAddress();
        PropertyStatus status = request.getStatus();
        PropertyType type = request.getType();
        int ownerId = request.getOwnerId();
        propertyService.validatePropertyData(price, address, status, type, ownerId);

        // Validate residential property-specific data
        int numberOfBedrooms = request.getNumberOfBedrooms();
        boolean hasGarden = request.isHasGarden();
        boolean isPetFriendly = request.isPetFriendly();
        validateResidentialPropertyData(numberOfBedrooms);

        // Update the general property and check for success
        Property property = propertyRepository.updateProperty(rPropertyId, price, address, status, type, ownerId);
        if (property == null) {
            throw new Exception("Failed to update property");
        }

        // Update the residential property and return it
        return residentialPropertyRepository.updateResidentialProperty(rPropertyId, numberOfBedrooms, hasGarden,
                isPetFriendly);
    }

    public void deleteResidentialPropertyById(int rPropertyId) {
        propertyRepository.deletePropertyById(rPropertyId);
        residentialPropertyRepository.deleteResidentialPropertyById(rPropertyId);
    }
}
