package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import java.util.List;

import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.repository.PropertyImageRepository;
import com.trustmejunior.request.CreatePropertyImageRequest;
import com.trustmejunior.request.UpdatePropertyImageRequest;

public class PropertyImageService {
    private PropertyImageRepository propertyImageRepository;

    public PropertyImageService() {
        this.propertyImageRepository = new PropertyImageRepository();
    }

    public PropertyImage getPropertyImageById(int id) {
        return propertyImageRepository.getPropertyImageById(id);
    }

    public PropertyImage getPropertyImageByPropertyId(int id) {
        return propertyImageRepository.getPropertyImageByPropertyId(id);
    }

    public List<PropertyImage> getAllPropertyImages() {
        return propertyImageRepository.getAllPropertyImages();
    }

    // Validates the data for property image creation or update
    private void validatePropertyImageData(String url, int propertyId) throws Exception {
        if (url == null || url.trim().isEmpty()) {
            throw new Exception("Property image URL cannot be empty");
        }
        if (propertyId <= 0) {
            throw new Exception("Invalid property ID");
        }
    }

    // Creates a new property image after validating input data
    public PropertyImage createPropertyImage(CreatePropertyImageRequest request) throws Exception {
        String url = request.getUrl();
        int propertyId = request.getPropertyId();

        // Validate the property image data
        validatePropertyImageData(url, propertyId);

        // Save the property image record in the repository
        PropertyImage propertyImage = propertyImageRepository.createPropertyImage(url, propertyId);

        // Throw an exception if the property image creation fails
        if (propertyImage == null) {
            throw new Exception("Failed to create property image");
        }

        return propertyImage;
    }

    // Updates an existing property image after validating input data
    public PropertyImage updatePropertyImage(int imageId, UpdatePropertyImageRequest request) throws Exception {
        if (imageId <= 0) {
            throw new Exception("Invalid property image ID");
        }

        String url = request.getUrl();
        int propertyId = request.getPropertyId();

        // Validate the property image data
        validatePropertyImageData(url, propertyId);

        // Update the property image record in the repository
        PropertyImage propertyImage = propertyImageRepository.updatePropertyImage(imageId, url, propertyId);

        // Throw an exception if the property image update fails
        if (propertyImage == null) {
            throw new Exception("Failed to update property image");
        }

        return propertyImage;
    }

    // Deletes a property image record by its ID
    public void deletePropertyImageById(int imageId) {
        propertyImageRepository.deletePropertyImageById(imageId); // Remove the property image from the repository
    }
}
