package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;
import com.trustmejunior.model.Enum.PropertyBusinessType;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.repository.CommercialPropertyRepository;
import com.trustmejunior.repository.PropertyRepository;
import com.trustmejunior.request.CreateCommercialPropertyRequest;
import com.trustmejunior.request.UpdateCommercialPropertyRequest;

import java.util.List;

public class CommercialPropertyService {
    private CommercialPropertyRepository commercialPropertyRepository;
    private PropertyRepository propertyRepository;
    private PropertyService propertyService;

    public CommercialPropertyService() {
        this.commercialPropertyRepository = new CommercialPropertyRepository();
        this.propertyRepository = new PropertyRepository();
        this.propertyService = new PropertyService();
    }

    // Retrieves a commercial property by its ID
    public CommercialProperty getCommercialPropertyById(int propertyId) {
        return commercialPropertyRepository.getCommercialPropertyById(propertyId);
    }

    // Retrieves all commercial properties from the repository
    public List<CommercialProperty> getAllCommercialProperties() {
        return commercialPropertyRepository.getAllCommercialProperties();
    }

    // Validates the data for a commercial property (business type and area)
    private void validateCommercialPropertyData(PropertyBusinessType businessType, double area) throws Exception {
        if (businessType == null) {
            throw new Exception("Business type cannot be empty");
        }
        if (area <= 0) {
            throw new Exception("Area must be greater than 0");
        }
        if (area > 10000) { // 10,000 square meters as a reasonable limit
            throw new Exception("Area cannot exceed 10,000 square meters");
        }
    }

    // Creates a new commercial property after validating the provided data
    public CommercialProperty createCommercialProperty(CreateCommercialPropertyRequest request) throws Exception {
        double price = request.getPrice();
        String address = request.getAddress();
        PropertyStatus status = request.getStatus();
        PropertyType type = request.getType();
        int ownerId = request.getOwnerId();

        // Validate common property data
        propertyService.validatePropertyData(price, address, status, type, ownerId);

        PropertyBusinessType businessType = request.getBusinessType();
        double area = request.getArea();
        boolean hasParking = request.isHasParking();

        // Validate commercial property-specific data
        validateCommercialPropertyData(businessType, area);

        // Create the property
        Property property = propertyRepository.createProperty(price, address, status, type, ownerId);
        if (property == null) {
            throw new Exception("Failed to create property");
        }

        int propertyId = property.getPropertyId();

        // Create the commercial property
        return commercialPropertyRepository.createCommercialProperty(propertyId, businessType, area, hasParking);
    }

    // Updates an existing commercial property with the provided data
    public CommercialProperty updateCommercialProperty(int cPropertyId, UpdateCommercialPropertyRequest request)
            throws Exception {
        if (cPropertyId <= 0) {
            throw new Exception("Invalid commercial property ID");
        }

        double price = request.getPrice();
        String address = request.getAddress();
        PropertyStatus status = request.getStatus();
        PropertyType type = request.getType();
        int ownerId = request.getOwnerId();

        // Validate common property data
        propertyService.validatePropertyData(price, address, status, type, ownerId);

        PropertyBusinessType businessType = request.getBusinessType();
        double area = request.getArea();
        boolean hasParking = request.isHasParking();

        // Validate commercial property-specific data
        validateCommercialPropertyData(businessType, area);

        // Update the property
        Property property = propertyRepository.updateProperty(cPropertyId, price, address, status, type, ownerId);
        if (property == null) {
            throw new Exception("Failed to update property");
        }

        // Update the commercial property
        return commercialPropertyRepository.updateCommercialProperty(cPropertyId, businessType, area, hasParking);
    }

    // Deletes a commercial property and its associated property data
    public void deleteCommercialPropertyById(int cPropertyId) {
        propertyRepository.deletePropertyById(cPropertyId);
        commercialPropertyRepository.deleteCommercialPropertyById(cPropertyId);
    }
}
