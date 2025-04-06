package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.service.PropertyService;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.request.CreatePropertyRequest;
import com.trustmejunior.request.UpdatePropertyRequest;

import java.util.List;
import java.util.Map;

public class PropertyController {
    private PropertyService propertyService;

    public PropertyController() {
        this.propertyService = new PropertyService();
    }

    public Property getPropertyById(int propertyId) {
        return propertyService.getPropertyById(propertyId);
    }

    public List<Property> getAllProperties() {
        return propertyService.getAllProperties();
    }

    public List<Property> getPropertiesByHostId(int id) {
        return propertyService.getPropertiesByHostId(id);
    }

    public List<Property> getPropertiesByOwnerId(int id) {
        return propertyService.getPropertiesByOwnerId(id);
    }

    public List<Property> getPropertiesByStatus(PropertyStatus status) {
        return propertyService.getPropertiesByStatus(status);
    }

    public Property createProperty(CreatePropertyRequest request) throws Exception {
        return propertyService.createProperty(request);
    }

    public Property updateProperty(int propertyId, UpdatePropertyRequest request) throws Exception {
        return propertyService.updateProperty(propertyId, request);
    }

    public void deletePropertyById(int propertyId) {
        propertyService.deletePropertyById(propertyId);
    }

    public void setHostIds(int propertyId, List<Integer> hostIds) {
        propertyService.setHostIds(propertyId, hostIds);
    }

    public int getTotalProperties(Integer hostId) {
        return propertyService.getTotalProperties(hostId);
    }

    public Map<String, Integer> getTop5MostRentedProperties(Integer hostId) {
        return propertyService.getTop5MostRentedProperties(hostId);
    }

    public Map<String, Integer> getRentedPropertyTypesCount(Integer hostId) {
        return propertyService.getRentedPropertyTypesCount(hostId);
    }
}
