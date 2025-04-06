package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Property.Property;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;
import com.trustmejunior.request.CreatePropertyRequest;
import com.trustmejunior.request.UpdatePropertyRequest;
import com.trustmejunior.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyControllerTest {

    @Mock
    private PropertyService propertyService;

    @InjectMocks
    private PropertyController propertyController;

    private Property testProperty;

    @BeforeEach
    void setUp() {
        testProperty = new Property(1, 1000.0, "123 Test St",
                PropertyStatus.AVAILABLE, PropertyType.RESIDENTIAL_PROPERTY, 1);
    }

    @Test
    void getPropertyByIdTest() {
        // Arrange
        int propertyId = 1;
        when(propertyService.getPropertyById(propertyId)).thenReturn(testProperty);

        // Act
        Property result = propertyController.getPropertyById(propertyId);

        // Assert
        assertNotNull(result, "Retrieved property should not be null");
        assertEquals(propertyId, result.getPropertyId(), "Property ID should match the requested ID");
        verify(propertyService).getPropertyById(propertyId);
    }

    @Test
    void getAllPropertiesTest() {
        // Arrange
        Property secondProperty = new Property(2, 2000.0, "456 Test St",
                PropertyStatus.AVAILABLE, PropertyType.COMMERCIAL_PROPERTY, 2);
        List<Property> mockProperties = Arrays.asList(testProperty, secondProperty);
        when(propertyService.getAllProperties()).thenReturn(mockProperties);

        // Act
        List<Property> result = propertyController.getAllProperties();

        // Assert
        assertNotNull(result, "List of properties should not be null");
        assertEquals(2, result.size(), "Should return exactly 2 properties");
        verify(propertyService).getAllProperties();
    }

    @Test
    void getPropertiesByHostIdTest() {
        // Arrange
        int hostId = 1;
        List<Property> mockProperties = Arrays.asList(testProperty);
        when(propertyService.getPropertiesByHostId(hostId)).thenReturn(mockProperties);

        // Act
        List<Property> result = propertyController.getPropertiesByHostId(hostId);

        // Assert
        assertNotNull(result, "List of properties for host should not be null");
        assertEquals(1, result.size(), "Should return exactly 1 property for the host");
        verify(propertyService).getPropertiesByHostId(hostId);
    }

    @Test
    void getPropertiesByOwnerIdTest() {
        // Arrange
        int ownerId = 1;
        List<Property> mockProperties = Arrays.asList(testProperty);
        when(propertyService.getPropertiesByOwnerId(ownerId)).thenReturn(mockProperties);

        // Act
        List<Property> result = propertyController.getPropertiesByOwnerId(ownerId);

        // Assert
        assertNotNull(result, "List of properties for owner should not be null");
        assertEquals(1, result.size(), "Should return exactly 1 property for the owner");
        verify(propertyService).getPropertiesByOwnerId(ownerId);
    }

    @Test
    void getPropertiesByStatusTest() {
        // Arrange
        PropertyStatus status = PropertyStatus.AVAILABLE;
        List<Property> mockProperties = Arrays.asList(testProperty);
        when(propertyService.getPropertiesByStatus(status)).thenReturn(mockProperties);

        // Act
        List<Property> results = propertyController.getPropertiesByStatus(status);

        // Assert
        assertNotNull(results, "List of properties by status should not be null");
        assertFalse(results.isEmpty(), "Property list should not be empty");
        Property firstProperty = results.get(0);
        assertEquals(status, firstProperty.getStatus(), "Property status should match the requested status");
        verify(propertyService).getPropertiesByStatus(status);
    }

    @Test
    void createPropertyTest() throws Exception {
        // Arrange
        CreatePropertyRequest request = new CreatePropertyRequest(
                1000.0,
                "123 Test St",
                PropertyStatus.AVAILABLE,
                PropertyType.RESIDENTIAL_PROPERTY,
                1);
        when(propertyService.createProperty(request)).thenReturn(testProperty);

        // Act
        Property result = propertyController.createProperty(request);

        // Assert
        assertNotNull(result, "Created property should not be null");
        assertEquals(testProperty.getPrice(), result.getPrice(), "Property price should match the request");
        assertEquals(testProperty.getAddress(), result.getAddress(), "Property address should match the request");
        verify(propertyService).createProperty(request);
    }

    @Test
    void updatePropertyTest() throws Exception {
        // Arrange
        int propertyId = 1;
        UpdatePropertyRequest request = new UpdatePropertyRequest(
                2000.0,
                "456 Test St",
                PropertyStatus.UNDER_MAINTENANCE,
                PropertyType.RESIDENTIAL_PROPERTY,
                1);
        Property updatedProperty = new Property(propertyId, request.getPrice(), request.getAddress(),
                request.getStatus(), request.getType(), request.getOwnerId());

        when(propertyService.updateProperty(propertyId, request)).thenReturn(updatedProperty);

        // Act
        Property result = propertyController.updateProperty(propertyId, request);

        // Assert
        assertNotNull(result, "Updated property should not be null");
        assertEquals(request.getPrice(), result.getPrice(), "Updated price should match the request");
        assertEquals(request.getAddress(), result.getAddress(), "Updated address should match the request");
        verify(propertyService).updateProperty(propertyId, request);
    }

    @Test
    void deletePropertyByIdTest() throws Exception {
        // Arrange
        int propertyId = 1;
        doNothing().when(propertyService).deletePropertyById(propertyId);

        // Act & Assert
        assertDoesNotThrow(() -> propertyController.deletePropertyById(propertyId),
                "Property deletion should not throw an exception");
        verify(propertyService).deletePropertyById(propertyId);
    }

    @Test
    void setHostIdsTest() throws Exception {
        // Arrange
        int propertyId = 1;
        List<Integer> hostIds = Arrays.asList(1, 2, 3);
        doNothing().when(propertyService).setHostIds(propertyId, hostIds);

        // Act & Assert
        assertDoesNotThrow(() -> propertyController.setHostIds(propertyId, hostIds),
                "Setting host IDs should not throw an exception");
        verify(propertyService).setHostIds(propertyId, hostIds);
    }

    @Test
    void getTotalPropertiesTest() {
        // Arrange
        Integer hostId = 1;
        when(propertyService.getTotalProperties(hostId)).thenReturn(5);

        // Act
        int result = propertyController.getTotalProperties(hostId);

        // Assert
        assertEquals(5, result, "Total number of properties should be 5");
        verify(propertyService).getTotalProperties(hostId);
    }

    @Test
    void getTop5MostRentedPropertiesTest() {
        // Arrange
        Integer hostId = 1;
        Map<String, Integer> mockResult = new HashMap<>();
        mockResult.put("Property 1", 10);
        mockResult.put("Property 2", 8);
        when(propertyService.getTop5MostRentedProperties(hostId)).thenReturn(mockResult);

        // Act
        Map<String, Integer> result = propertyController.getTop5MostRentedProperties(hostId);

        // Assert
        assertNotNull(result, "Top 10 rented properties map should not be null");
        assertEquals(2, result.size(), "Should return exactly 2 most rented properties");
        assertEquals(10, result.get("Property 1"), "Property 1 should have 10 rentals");
        verify(propertyService).getTop5MostRentedProperties(hostId);
    }

    @Test
    void getRentedPropertyTypesCountTest() {
        // Arrange
        Integer hostId = 1;
        Map<String, Integer> mockResult = new HashMap<>();
        mockResult.put("RESIDENTIAL", 5);
        mockResult.put("COMMERCIAL", 3);
        when(propertyService.getRentedPropertyTypesCount(hostId)).thenReturn(mockResult);

        // Act
        Map<String, Integer> result = propertyController.getRentedPropertyTypesCount(hostId);

        // Assert
        assertNotNull(result, "Rented property types count map should not be null");
        assertEquals(2, result.size(), "Should return counts for exactly 2 property types");
        assertEquals(5, result.get("RESIDENTIAL"), "Should have 5 residential properties");
        assertEquals(3, result.get("COMMERCIAL"), "Should have 3 commercial properties");
        verify(propertyService).getRentedPropertyTypesCount(hostId);
    }
}