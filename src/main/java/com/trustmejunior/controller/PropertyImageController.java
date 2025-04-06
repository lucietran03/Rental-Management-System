package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.service.PropertyImageService;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.request.CreatePropertyImageRequest;
import com.trustmejunior.request.UpdatePropertyImageRequest;

import java.util.List;

public class PropertyImageController {
    private PropertyImageService propertyImageService;

    public PropertyImageController() {
        this.propertyImageService = new PropertyImageService();
    }

    public PropertyImage getPropertyImageById(int imageId) {
        return propertyImageService.getPropertyImageById(imageId);
    }

    public PropertyImage getPropertyImageByPropertyId(int propertyId) {
        return propertyImageService.getPropertyImageByPropertyId(propertyId);
    }

    public List<PropertyImage> getAllPropertyImages() {
        return propertyImageService.getAllPropertyImages();
    }

    public PropertyImage createPropertyImage(CreatePropertyImageRequest request) throws Exception {
        return propertyImageService.createPropertyImage(request);
    }

    public PropertyImage updatePropertyImage(int imageId, UpdatePropertyImageRequest request) throws Exception {
        return propertyImageService.updatePropertyImage(imageId, request);
    }

    public void deletePropertyImageById(int imageId) {
        propertyImageService.deletePropertyImageById(imageId);
    }
}
