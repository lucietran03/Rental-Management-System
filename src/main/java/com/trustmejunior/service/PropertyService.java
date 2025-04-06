package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.repository.HostPropertyRepository;
import com.trustmejunior.repository.PropertyRepository;
import com.trustmejunior.repository.HostRepository;
import com.trustmejunior.request.CreatePropertyRequest;
import com.trustmejunior.request.UpdatePropertyRequest;

public class PropertyService {
    private HostPropertyRepository hostPropertyRepository;
    private PropertyRepository propertyRepository;
    private HostRepository hostRepository;

    public PropertyService() {
        this.hostPropertyRepository = new HostPropertyRepository();
        this.propertyRepository = new PropertyRepository();
        this.hostRepository = new HostRepository();
    }

    public Property getPropertyById(int id) {
        return propertyRepository.getPropertyById(id);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.getAllProperties();
    }

    public List<Property> getPropertiesByHostId(int id) {
        return propertyRepository.getPropertiesByHostId(id);
    }

    public List<Property> getPropertiesByOwnerId(int id) {
        return propertyRepository.getPropertiesByOwnerId(id);
    }

    public List<Property> getPropertiesByStatus(PropertyStatus status) {
        return propertyRepository.getPropertiesByStatus(status);
    }

    // Validates property data to ensure all required fields are valid
    public void validatePropertyData(double price, String address, PropertyStatus status, PropertyType type,
            int ownerId) throws Exception {
        if (price <= 0) {
            throw new Exception("Property price must be greater than 0");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new Exception("Property address cannot be empty");
        }
        if (address.length() > 200) {
            throw new Exception("Property address cannot exceed 200 characters");
        }
        if (status == null) {
            throw new Exception("Property status cannot be empty");
        }
        if (type == null) {
            throw new Exception("Property type cannot be empty");
        }
        if (ownerId <= 0) {
            throw new Exception("Invalid owner ID");
        }
    }

    // Creates a new property after validating the input data
    public Property createProperty(CreatePropertyRequest request) throws Exception {
        double price = request.getPrice();
        String address = request.getAddress();
        PropertyStatus status = request.getStatus();
        PropertyType type = request.getType();
        int ownerId = request.getOwnerId();

        // Validate property data
        validatePropertyData(price, address, status, type, ownerId);

        // Save the property to the repository
        Property property = propertyRepository.createProperty(price, address, status, type, ownerId);

        // Throw an exception if property creation fails
        if (property == null) {
            throw new Exception("Failed to create property");
        }

        return property;
    }

    // Updates an existing property after validating the input data
    public Property updateProperty(int propertyId, UpdatePropertyRequest request) throws Exception {
        if (propertyId <= 0) {
            throw new Exception("Invalid property ID");
        }

        double price = request.getPrice();
        String address = request.getAddress();
        PropertyStatus status = request.getStatus();
        PropertyType type = request.getType();
        int ownerId = request.getOwnerId();

        // Validate property data
        validatePropertyData(price, address, status, type, ownerId);

        // Update the property in the repository
        Property property = propertyRepository.updateProperty(propertyId, price, address, status, type, ownerId);

        // Throw an exception if property update fails
        if (property == null) {
            throw new Exception("Failed to update property");
        }

        return property;
    }

    // Deletes a property by its ID
    public void deletePropertyById(int propertyId) {
        propertyRepository.deletePropertyById(propertyId); // Remove the property from the repository
    }

    // Updates the hosts associated with a property
    public void setHostIds(int propertyId, List<Integer> hostIds) {
        // Get the current list of hosts associated with the property
        List<Host> oldHosts = hostRepository.getHostsByPropertyId(propertyId);
        List<Integer> oldHostIds = new ArrayList<>();

        // Extract current host IDs
        for (Host host : oldHosts) {
            oldHostIds.add(host.getAccountId());
        }

        // Add new host-property links if they don't exist
        for (int hostId : hostIds) {
            if (!oldHostIds.contains(hostId)) {
                hostPropertyRepository.linkHostToProperty(hostId, propertyId);
            }
        }

        // Remove old host-property links that are no longer valid
        for (int oldHostId : oldHostIds) {
            if (!hostIds.contains(oldHostId)) {
                hostPropertyRepository.unlinkHostFromProperty(oldHostId, propertyId);
            }
        }
    }

    public int getTotalProperties(Integer hostId) {
        return propertyRepository.getTotalProperties(hostId);
    }

    public Map<String, Integer> getTop5MostRentedProperties(Integer hostId) {
        return propertyRepository.getTop5MostRentedProperties(hostId);
    }

    public Map<String, Integer> getRentedPropertyTypesCount(Integer hostId) {
        return propertyRepository.getRentedPropertyTypesCount(hostId);
    }
}
