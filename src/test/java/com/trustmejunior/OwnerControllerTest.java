package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.model.User.Owner;
import com.trustmejunior.request.CreateOwnerRequest;
import com.trustmejunior.request.UpdateOwnerRequest;
import com.trustmejunior.service.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private OwnerController ownerController;

    private Owner testOwner;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        testOwner = new Owner(1, "testOwner", "testPass", "Test Owner", "test@example.com", testDate);
    }

    @Test
    void getOwnerByAccountIdTest() {
        // Arrange
        int accountId = 1;
        when(ownerService.getOwnerByAccountId(accountId)).thenReturn(testOwner);

        // Act
        Owner result = ownerController.getOwnerByAccountId(accountId);

        // Assert
        assertNotNull(result, "Retrieved owner should not be null");
        assertEquals(accountId, result.getAccountId(), "Owner account ID should match the requested ID");
        assertEquals(testOwner.getUsername(), result.getUsername(), "Owner username should match the test data");
        assertEquals(testOwner.getEmail(), result.getEmail(), "Owner email should match the test data");
        verify(ownerService).getOwnerByAccountId(accountId);
    }

    @Test
    void getAllOwnersTest() {
        // Arrange
        Owner secondOwner = new Owner(2, "owner2", "pass2", "Second Owner", "owner2@example.com", testDate);
        List<Owner> mockOwners = Arrays.asList(testOwner, secondOwner);
        when(ownerService.getAllOwners()).thenReturn(mockOwners);

        // Act
        List<Owner> results = ownerController.getAllOwners();

        // Assert
        assertNotNull(results, "Retrieved owners list should not be null");
        assertEquals(2, results.size(), "Should return exactly two owners");
        assertEquals(testOwner.getAccountId(), results.get(0).getAccountId(),
                "First owner's account ID should match test data");
        assertEquals(testOwner.getUsername(), results.get(0).getUsername(),
                "First owner's username should match test data");
        verify(ownerService).getAllOwners();
    }

    @Test
    void createOwnerTest() throws Exception {
        // Arrange
        CreateOwnerRequest request = new CreateOwnerRequest(
                "testOwner",
                "testPass",
                "Test Owner",
                "test@example.com",
                testDate);
        when(ownerService.createOwner(request)).thenReturn(testOwner);

        // Act
        Owner result = ownerController.createOwner(request);

        // Assert
        assertNotNull(result, "Created owner should not be null");
        assertEquals(request.getUsername(), result.getUsername(), "Created owner's username should match request");
        assertEquals(request.getEmail(), result.getEmail(), "Created owner's email should match request");
        assertEquals(request.getFullName(), result.getFullName(), "Created owner's full name should match request");
        verify(ownerService).createOwner(request);
    }

    @Test
    void updateOwnerTest() throws Exception {
        // Arrange
        int ownerId = 1;
        UpdateOwnerRequest request = new UpdateOwnerRequest(
                "updatedOwner",
                "updatedPass",
                "Updated Owner",
                "updated@example.com",
                testDate);
        Owner updatedOwner = new Owner(ownerId, request.getUsername(), request.getPassword(),
                request.getFullName(), request.getEmail(), request.getDob());

        when(ownerService.updateOwner(ownerId, request)).thenReturn(updatedOwner);

        // Act
        Owner result = ownerController.updateOwner(ownerId, request);

        // Assert
        assertNotNull(result, "Updated owner should not be null");
        assertEquals(request.getUsername(), result.getUsername(), "Updated owner's username should match request");
        assertEquals(request.getEmail(), result.getEmail(), "Updated owner's email should match request");
        assertEquals(request.getFullName(), result.getFullName(), "Updated owner's full name should match request");
        verify(ownerService).updateOwner(ownerId, request);
    }

    @Test
    void deleteOwnerByAccountIdTest() throws Exception {
        // Arrange
        int ownerId = 1;
        doNothing().when(ownerService).deleteOwnerByAccountId(ownerId);

        // Act & Assert
        assertDoesNotThrow(() -> ownerController.deleteOwnerByAccountId(ownerId),
                "Owner deletion should not throw any exceptions");
        verify(ownerService).deleteOwnerByAccountId(ownerId);
    }
}