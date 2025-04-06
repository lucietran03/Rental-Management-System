package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.controller.ResidentialPropertyController;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.request.CreateResidentialPropertyRequest;
import com.trustmejunior.request.UpdateResidentialPropertyRequest;
import com.trustmejunior.service.ResidentialPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResidentialPropertyControllerTest {

        @Mock
        private ResidentialPropertyService residentialPropertyService;

        @InjectMocks
        private ResidentialPropertyController residentialPropertyController;

        private ResidentialProperty testProperty;

        @BeforeEach
        void setUp() {
                testProperty = new ResidentialProperty(1, 1000.0, "123 Test St", PropertyStatus.AVAILABLE, 1,
                                3, true, true);
        }

        @Test
        void getResidentialPropertyByIdTest() {
                // Arrange
                int propertyId = 1;
                when(residentialPropertyService.getResidentialPropertyById(propertyId)).thenReturn(testProperty);

                // Act
                ResidentialProperty result = residentialPropertyController.getResidentialPropertyById(propertyId);

                // Assert
                assertNotNull(result, "Retrieved residential property should not be null");
                assertEquals(propertyId, result.getPropertyId(), "Property ID should match the requested ID");
                assertEquals(testProperty.getPrice(), result.getPrice(), "Property price should match");
                assertEquals(testProperty.getNumberOfBedrooms(), result.getNumberOfBedrooms(),
                                "Number of bedrooms should match");
                verify(residentialPropertyService).getResidentialPropertyById(propertyId);
        }

        @Test
        void getAllResidentialPropertiesTest() {
                // Arrange
                ResidentialProperty secondProperty = new ResidentialProperty(2, 2000.0, "456 Test St",
                                PropertyStatus.AVAILABLE, 2, 4, false, false);
                List<ResidentialProperty> mockProperties = Arrays.asList(testProperty, secondProperty);
                when(residentialPropertyService.getAllResidentialProperties()).thenReturn(mockProperties);

                // Act
                List<ResidentialProperty> results = residentialPropertyController.getAllResidentialProperties();

                // Assert
                assertNotNull(results, "List of residential properties should not be null");
                assertEquals(2, results.size(), "Should return exactly 2 properties");
                assertEquals(testProperty.getPropertyId(), results.get(0).getPropertyId(),
                                "First property ID should match test property");
                assertEquals(testProperty.getNumberOfBedrooms(), results.get(0).getNumberOfBedrooms(),
                                "First property bedrooms should match test property");
                verify(residentialPropertyService).getAllResidentialProperties();
        }

        @Test
        void createResidentialPropertyTest() throws Exception {
                // Arrange
                CreateResidentialPropertyRequest request = new CreateResidentialPropertyRequest(
                                1000.0,
                                "123 Test St",
                                PropertyStatus.AVAILABLE,
                                1,
                                3,
                                true,
                                true);
                when(residentialPropertyService.createResidentialProperty(request)).thenReturn(testProperty);

                // Act
                ResidentialProperty result = residentialPropertyController.createResidentialProperty(request);

                // Assert
                assertNotNull(result, "Created residential property should not be null");
                assertEquals(request.getPrice(), result.getPrice(), "Created property price should match request");
                assertEquals(request.getAddress(), result.getAddress(),
                                "Created property address should match request");
                assertEquals(request.getNumberOfBedrooms(), result.getNumberOfBedrooms(),
                                "Created property bedrooms should match request");
                assertEquals(request.isHasGarden(), result.isHasGarden(),
                                "Created property garden status should match request");
                assertEquals(request.isPetFriendly(), result.isPetFriendly(),
                                "Created property pet-friendly status should match request");
                verify(residentialPropertyService).createResidentialProperty(request);
        }

        @Test
        void updateResidentialPropertyTest() throws Exception {
                // Arrange
                int propertyId = 1;
                UpdateResidentialPropertyRequest request = new UpdateResidentialPropertyRequest(
                                2000.0,
                                "456 Test St",
                                PropertyStatus.UNDER_MAINTENANCE,
                                1,
                                4,
                                false,
                                false);
                ResidentialProperty updatedProperty = new ResidentialProperty(propertyId, request.getPrice(),
                                request.getAddress(), request.getStatus(), request.getOwnerId(),
                                request.getNumberOfBedrooms(), request.isHasGarden(), request.isPetFriendly());

                when(residentialPropertyService.updateResidentialProperty(propertyId, request))
                                .thenReturn(updatedProperty);

                // Act
                ResidentialProperty result = residentialPropertyController.updateResidentialProperty(propertyId,
                                request);

                // Assert
                assertNotNull(result, "Updated residential property should not be null");
                assertEquals(request.getPrice(), result.getPrice(), "Updated property price should match request");
                assertEquals(request.getAddress(), result.getAddress(),
                                "Updated property address should match request");
                assertEquals(request.getNumberOfBedrooms(), result.getNumberOfBedrooms(),
                                "Updated property bedrooms should match request");
                assertEquals(request.isHasGarden(), result.isHasGarden(),
                                "Updated property garden status should match request");
                assertEquals(request.isPetFriendly(), result.isPetFriendly(),
                                "Updated property pet-friendly status should match request");
                verify(residentialPropertyService).updateResidentialProperty(propertyId, request);
        }

        @Test
        void deleteResidentialPropertyByIdTest() throws Exception {
                // Arrange
                int propertyId = 1;
                doNothing().when(residentialPropertyService).deleteResidentialPropertyById(propertyId);

                // Act & Assert
                assertDoesNotThrow(() -> residentialPropertyController.deleteResidentialPropertyById(propertyId),
                                "Deleting residential property should not throw exception");
                verify(residentialPropertyService).deleteResidentialPropertyById(propertyId);
        }
}