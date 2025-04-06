package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.request.CreateTenantRequest;
import com.trustmejunior.request.UpdateTenantRequest;
import com.trustmejunior.service.TenantService;
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
class TenantControllerTest {

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private TenantController tenantController;

    private Tenant testTenant;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        testTenant = new Tenant(1, "testTenant", "testPass", "Test Tenant", "test@example.com", testDate);
    }

    @Test
    void getTenantByAccountIdTest() {
        // Arrange
        int accountId = 1;
        when(tenantService.getTenantByAccountId(accountId)).thenReturn(testTenant);

        // Act
        Tenant result = tenantController.getTenantByAccountId(accountId);

        // Assert
        assertNotNull(result, "Retrieved tenant should not be null");
        assertEquals(accountId, result.getAccountId(), "Tenant account ID should match expected ID");
        assertEquals(testTenant.getUsername(), result.getUsername(), "Tenant username should match expected username");
        verify(tenantService).getTenantByAccountId(accountId);
    }

    @Test
    void getAllTenantsTest() {
        // Arrange
        Tenant secondTenant = new Tenant(2, "tenant2", "pass2", "Second Tenant", "tenant2@example.com", testDate);
        List<Tenant> mockTenants = Arrays.asList(testTenant, secondTenant);
        when(tenantService.getAllTenants()).thenReturn(mockTenants);

        // Act
        List<Tenant> results = tenantController.getAllTenants();

        // Assert
        assertNotNull(results, "List of tenants should not be null");
        assertEquals(2, results.size(), "Should return exactly 2 tenants");
        assertEquals(testTenant.getAccountId(), results.get(0).getAccountId(),
                "First tenant's account ID should match");
        verify(tenantService).getAllTenants();
    }

    @Test
    void getMainTenantByRentalAgreementIdTest() {
        // Arrange
        int rentalAgreementId = 1;
        when(tenantService.getMainTenantByRentalAgreementId(rentalAgreementId)).thenReturn(testTenant);

        // Act
        Tenant result = tenantController.getMainTenantByRentalAgreementId(rentalAgreementId);

        // Assert
        assertNotNull(result, "Main tenant should not be null");
        assertEquals(testTenant.getAccountId(), result.getAccountId(), "Main tenant's account ID should match");
        assertEquals(testTenant.getUsername(), result.getUsername(), "Main tenant's username should match");
        verify(tenantService).getMainTenantByRentalAgreementId(rentalAgreementId);
    }

    @Test
    void getSubTenantsByRentalAgreementIdTest() {
        // Arrange
        int rentalAgreementId = 1;
        List<Tenant> mockTenants = Arrays.asList(testTenant);
        when(tenantService.getSubTenantsByRentalAgreementId(rentalAgreementId)).thenReturn(mockTenants);

        // Act
        List<Tenant> results = tenantController.getSubTenantsByRentalAgreementId(rentalAgreementId);

        // Assert
        assertNotNull(results, "List of sub-tenants should not be null");
        assertFalse(results.isEmpty(), "Sub-tenants list should not be empty");
        assertEquals(testTenant.getAccountId(), results.get(0).getAccountId(), "Sub-tenant's account ID should match");
        verify(tenantService).getSubTenantsByRentalAgreementId(rentalAgreementId);
    }

    @Test
    void createTenantTest() throws Exception {
        // Arrange
        CreateTenantRequest request = new CreateTenantRequest(
                "newTenant",
                "newPass",
                "Test Tenant",
                "tenant@example.com",
                testDate);
        Tenant newTenant = new Tenant(1, request.getUsername(), request.getPassword(), request.getFullName(),
                request.getEmail(), request.getDob());
        when(tenantService.createTenant(request)).thenReturn(newTenant);

        // Act
        Tenant result = tenantController.createTenant(request);

        // Assert
        assertNotNull(result, "Created tenant should not be null");
        assertEquals(request.getUsername(), result.getUsername(), "Created tenant's username should match request");
        assertEquals(request.getFullName(), result.getFullName(), "Created tenant's full name should match request");
        assertEquals(request.getEmail(), result.getEmail(), "Created tenant's email should match request");
        verify(tenantService).createTenant(request);
    }

    @Test
    void updateTenantTest() throws Exception {
        // Arrange
        int tenantId = 1;
        UpdateTenantRequest request = new UpdateTenantRequest(
                "updatedTenant",
                "updatedPass",
                "Updated Tenant",
                "updated@example.com",
                testDate);
        Tenant updatedTenant = new Tenant(tenantId, request.getUsername(), request.getPassword(),
                request.getFullName(), request.getEmail(), request.getDob());

        when(tenantService.updateTenant(tenantId, request)).thenReturn(updatedTenant);

        // Act
        Tenant result = tenantController.updateTenant(tenantId, request);

        // Assert
        assertNotNull(result, "Updated tenant should not be null");
        assertEquals(request.getUsername(), result.getUsername(), "Updated tenant's username should match request");
        assertEquals(request.getFullName(), result.getFullName(), "Updated tenant's full name should match request");
        assertEquals(request.getEmail(), result.getEmail(), "Updated tenant's email should match request");
        verify(tenantService).updateTenant(tenantId, request);
    }

    @Test
    void deleteTenantByAccountIdTest() throws Exception {
        // Arrange
        int tenantId = 1;
        doNothing().when(tenantService).deleteTenantByAccountId(tenantId);

        // Act & Assert
        assertDoesNotThrow(() -> tenantController.deleteTenantByAccountId(tenantId),
                "Deleting tenant should not throw exception");
        verify(tenantService).deleteTenantByAccountId(tenantId);
    }

    @Test
    void setMainRentalAgreementIdsTest() throws Exception {
        // Arrange
        int tenantId = 1;
        List<Integer> rentalAgreementIds = Arrays.asList(1, 2, 3);
        doNothing().when(tenantService).setMainRentalAgreementIds(tenantId, rentalAgreementIds);

        // Act & Assert
        assertDoesNotThrow(() -> tenantController.setMainRentalAgreementIds(tenantId, rentalAgreementIds),
                "Setting main rental agreement IDs should not throw exception");
        verify(tenantService).setMainRentalAgreementIds(tenantId, rentalAgreementIds);
    }

    @Test
    void setSubRentalAgreementIdsTest() throws Exception {
        // Arrange
        int tenantId = 1;
        List<Integer> rentalAgreementIds = Arrays.asList(1, 2, 3);
        doNothing().when(tenantService).setSubRentalAgreementIds(tenantId, rentalAgreementIds);

        // Act & Assert
        assertDoesNotThrow(() -> tenantController.setSubRentalAgreementIds(tenantId, rentalAgreementIds),
                "Setting sub rental agreement IDs should not throw exception");
        verify(tenantService).setSubRentalAgreementIds(tenantId, rentalAgreementIds);
    }

    @Test
    void getTotalTenantsTest() {
        // Arrange
        when(tenantService.getTotalTenants()).thenReturn(5);

        // Act
        int result = tenantController.getTotalTenants();

        // Assert
        assertEquals(5, result, "Total number of tenants should be 5");
        verify(tenantService).getTotalTenants();
    }
}