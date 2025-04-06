package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.request.CreatePropertyImageRequest;
import com.trustmejunior.request.UpdatePropertyImageRequest;
import com.trustmejunior.service.PropertyImageService;
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
class PropertyImageControllerTest {

    @Mock
    private PropertyImageService propertyImageService;

    @InjectMocks
    private PropertyImageController propertyImageController;

    private PropertyImage testImage;

    @BeforeEach
    void setUp() {
        testImage = new PropertyImage(1, "test-image.jpg", 1);
    }

    @Test
    void getPropertyImageByIdTest() {
        // Arrange
        int imageId = 1;
        when(propertyImageService.getPropertyImageById(imageId)).thenReturn(testImage);

        // Act
        PropertyImage result = propertyImageController.getPropertyImageById(imageId);

        // Assert
        assertNotNull(result, "Retrieved property image should not be null");
        assertEquals(imageId, result.getImageId(), "Property image ID should match the requested ID");
        assertEquals(testImage.getUrl(), result.getUrl(), "Property image URL should match");
        assertEquals(testImage.getPropertyId(), result.getPropertyId(), "Property ID should match");
        verify(propertyImageService).getPropertyImageById(imageId);
    }

    @Test
    void getPropertyImageByPropertyIdTest() {
        // Arrange
        int propertyId = 1;
        when(propertyImageService.getPropertyImageByPropertyId(propertyId)).thenReturn(testImage);

        // Act
        PropertyImage result = propertyImageController.getPropertyImageByPropertyId(propertyId);

        // Assert
        assertNotNull(result, "Retrieved property image should not be null");
        assertEquals(propertyId, result.getPropertyId(), "Property ID should match the requested ID");
        assertEquals(testImage.getUrl(), result.getUrl(), "Property image URL should match");
        verify(propertyImageService).getPropertyImageByPropertyId(propertyId);
    }

    @Test
    void getAllPropertyImagesTest() {
        // Arrange
        PropertyImage secondImage = new PropertyImage(2, "second-image.jpg", 2);
        List<PropertyImage> mockImages = Arrays.asList(testImage, secondImage);
        when(propertyImageService.getAllPropertyImages()).thenReturn(mockImages);

        // Act
        List<PropertyImage> results = propertyImageController.getAllPropertyImages();

        // Assert
        assertNotNull(results, "Retrieved property images list should not be null");
        assertEquals(2, results.size(), "Should return correct number of property images");
        assertEquals(testImage.getImageId(), results.get(0).getImageId(), "First image ID should match");
        assertEquals(testImage.getUrl(), results.get(0).getUrl(), "First image URL should match");
        verify(propertyImageService).getAllPropertyImages();
    }

    @Test
    void createPropertyImageTest() throws Exception {
        // Arrange
        CreatePropertyImageRequest request = new CreatePropertyImageRequest(
                "test-image.jpg",
                1);
        when(propertyImageService.createPropertyImage(request)).thenReturn(testImage);

        // Act
        PropertyImage result = propertyImageController.createPropertyImage(request);

        // Assert
        assertNotNull(result, "Created property image should not be null");
        assertEquals(request.getUrl(), result.getUrl(), "Created image URL should match request");
        assertEquals(request.getPropertyId(), result.getPropertyId(), "Created image property ID should match request");
        verify(propertyImageService).createPropertyImage(request);
    }

    @Test
    void updatePropertyImageTest() throws Exception {
        // Arrange
        int imageId = 1;
        UpdatePropertyImageRequest request = new UpdatePropertyImageRequest(
                "updated-image.jpg",
                1);
        PropertyImage updatedImage = new PropertyImage(imageId, request.getUrl(), request.getPropertyId());

        when(propertyImageService.updatePropertyImage(imageId, request)).thenReturn(updatedImage);

        // Act
        PropertyImage result = propertyImageController.updatePropertyImage(imageId, request);

        // Assert
        assertNotNull(result, "Updated property image should not be null");
        assertEquals(request.getUrl(), result.getUrl(), "Updated image URL should match request");
        assertEquals(request.getPropertyId(), result.getPropertyId(), "Updated image property ID should match request");
        verify(propertyImageService).updatePropertyImage(imageId, request);
    }

    @Test
    void deletePropertyImageByIdTest() throws Exception {
        // Arrange
        int imageId = 1;
        doNothing().when(propertyImageService).deletePropertyImageById(imageId);

        // Act & Assert
        assertDoesNotThrow(() -> propertyImageController.deletePropertyImageById(imageId),
                "Deleting property image should not throw exception");
        verify(propertyImageService).deletePropertyImageById(imageId);
    }
}