package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.controller.CommercialPropertyController;
import com.trustmejunior.model.Enum.PropertyBusinessType;
import com.trustmejunior.request.CreateCommercialPropertyRequest;
import com.trustmejunior.request.UpdateCommercialPropertyRequest;
import com.trustmejunior.service.CommercialPropertyService;
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
class CommercialPropertyControllerTest {

        @Mock
        private CommercialPropertyService commercialPropertyService;

        @InjectMocks
        private CommercialPropertyController commercialPropertyController;

        private CommercialProperty testProperty;

        @BeforeEach
        void setUp() {
                testProperty = new CommercialProperty(1, 1000.0, "123 Test St", PropertyStatus.AVAILABLE, 1,
                                PropertyBusinessType.OFFICE, 100.0, true);
        }

        @Test
        void getCommercialPropertyByIdTest() {
                // Arrange
                int propertyId = 1;
                when(commercialPropertyService.getCommercialPropertyById(propertyId)).thenReturn(testProperty);

                // Act
                CommercialProperty result = commercialPropertyController.getCommercialPropertyById(propertyId);

                // Assert
                assertNotNull(result, "Retrieved commercial property should not be null");
                assertEquals(propertyId, result.getPropertyId(), "Property ID should match the requested ID");
                assertEquals(testProperty.getPrice(), result.getPrice(), "Property price should match");
                assertEquals(testProperty.getBusinessType(), result.getBusinessType(),
                                "Property business type should match");
                verify(commercialPropertyService).getCommercialPropertyById(propertyId);
        }

        @Test
        void getAllCommercialPropertiesTest() {
                // Arrange
                CommercialProperty secondProperty = new CommercialProperty(2, 2000.0, "456 Test St",
                                PropertyStatus.AVAILABLE, 2, PropertyBusinessType.RETAIL, 200.0, false);
                List<CommercialProperty> mockProperties = Arrays.asList(testProperty, secondProperty);
                when(commercialPropertyService.getAllCommercialProperties()).thenReturn(mockProperties);

                // Act
                List<CommercialProperty> results = commercialPropertyController.getAllCommercialProperties();

                // Assert
                assertNotNull(results, "List of commercial properties should not be null");
                assertEquals(2, results.size(), "Should return exactly 2 commercial properties");
                assertEquals(testProperty.getPropertyId(), results.get(0).getPropertyId(),
                                "First property ID should match test property");
                assertEquals(testProperty.getBusinessType(), results.get(0).getBusinessType(),
                                "First property business type should match");
                verify(commercialPropertyService).getAllCommercialProperties();
        }

        @Test
        void createCommercialPropertyTest() throws Exception {
                // Arrange
                CreateCommercialPropertyRequest request = new CreateCommercialPropertyRequest(
                                1000.0,
                                "123 Test St",
                                PropertyStatus.AVAILABLE,
                                1,
                                PropertyBusinessType.OFFICE,
                                100.0,
                                true);
                when(commercialPropertyService.createCommercialProperty(request)).thenReturn(testProperty);

                // Act
                CommercialProperty result = commercialPropertyController.createCommercialProperty(request);

                // Assert
                assertNotNull(result, "Created commercial property should not be null");
                assertEquals(request.getPrice(), result.getPrice(), "Created property price should match request");
                assertEquals(request.getAddress(), result.getAddress(),
                                "Created property address should match request");
                assertEquals(request.getBusinessType(), result.getBusinessType(),
                                "Created property business type should match request");
                assertEquals(request.getArea(), result.getArea(), "Created property area should match request");
                assertEquals(request.isHasParking(), result.isHasParking(),
                                "Created property parking status should match request");
                verify(commercialPropertyService).createCommercialProperty(request);
        }

        @Test
        void updateCommercialPropertyTest() throws Exception {
                // Arrange
                int propertyId = 1;
                UpdateCommercialPropertyRequest request = new UpdateCommercialPropertyRequest(
                                2000.0,
                                "456 Test St",
                                PropertyStatus.UNDER_MAINTENANCE,
                                1,
                                PropertyBusinessType.RETAIL,
                                200.0,
                                false);
                CommercialProperty updatedProperty = new CommercialProperty(propertyId, request.getPrice(),
                                request.getAddress(), request.getStatus(), request.getOwnerId(),
                                request.getBusinessType(), request.getArea(), request.isHasParking());

                when(commercialPropertyService.updateCommercialProperty(propertyId, request))
                                .thenReturn(updatedProperty);

                // Act
                CommercialProperty result = commercialPropertyController.updateCommercialProperty(propertyId, request);

                // Assert
                assertNotNull(result, "Updated commercial property should not be null");
                assertEquals(request.getPrice(), result.getPrice(), "Updated property price should match request");
                assertEquals(request.getAddress(), result.getAddress(),
                                "Updated property address should match request");
                assertEquals(request.getBusinessType(), result.getBusinessType(),
                                "Updated property business type should match request");
                assertEquals(request.getArea(), result.getArea(), "Updated property area should match request");
                assertEquals(request.isHasParking(), result.isHasParking(),
                                "Updated property parking status should match request");
                verify(commercialPropertyService).updateCommercialProperty(propertyId, request);
        }

        @Test
        void deleteCommercialPropertyByIdTest() throws Exception {
                // Arrange
                int propertyId = 1;
                doNothing().when(commercialPropertyService).deleteCommercialPropertyById(propertyId);

                // Act & Assert
                assertDoesNotThrow(() -> commercialPropertyController.deleteCommercialPropertyById(propertyId),
                                "Deleting commercial property should not throw an exception");
                verify(commercialPropertyService).deleteCommercialPropertyById(propertyId);
        }
}