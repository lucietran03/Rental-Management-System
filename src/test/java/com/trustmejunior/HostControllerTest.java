package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.User.Host;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.request.CreateHostRequest;
import com.trustmejunior.request.UpdateHostRequest;
import com.trustmejunior.service.HostService;
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
class HostControllerTest {

    @Mock
    private HostService hostService;

    @InjectMocks
    private HostController hostController;

    private Host testHost;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        testHost = new Host(1, "testHost", "testPass", "Test Host", "test@example.com", testDate);
    }

    @Test
    void getHostByAccountIdTest() {
        // Arrange
        int accountId = 1;
        when(hostService.getHostByAccountId(accountId)).thenReturn(testHost);

        // Act
        Host result = hostController.getHostByAccountId(accountId);

        // Assert
        assertNotNull(result, "Retrieved host should not be null");
        assertEquals(accountId, result.getAccountId(), "Host account ID should match the requested ID");
        assertEquals(testHost.getUsername(), result.getUsername(), "Host username should match test data");
        verify(hostService).getHostByAccountId(accountId);
    }

    @Test
    void getAllHostsTest() {
        // Arrange
        Host secondHost = new Host(2, "host2", "pass2", "Second Host", "host2@example.com", testDate);
        List<Host> mockHosts = Arrays.asList(testHost, secondHost);
        when(hostService.getAllHosts()).thenReturn(mockHosts);

        // Act
        List<Host> results = hostController.getAllHosts();

        // Assert
        assertNotNull(results, "List of hosts should not be null");
        assertEquals(2, results.size(), "Should return exactly 2 hosts");
        assertEquals(testHost.getAccountId(), results.get(0).getAccountId(),
                "First host's account ID should match test data");
        verify(hostService).getAllHosts();
    }

    @Test
    void getHostsByRentalAgreementIdTest() {
        // Arrange
        int rentalAgreementId = 1;
        List<Host> mockHosts = Arrays.asList(testHost);
        when(hostService.getHostsByRentalAgreementId(rentalAgreementId)).thenReturn(mockHosts);

        // Act
        List<Host> results = hostController.getHostsByRentalAgreementId(rentalAgreementId);

        // Assert
        assertNotNull(results, "List of hosts for rental agreement should not be null");
        assertFalse(results.isEmpty(), "List of hosts should not be empty");
        assertEquals(testHost.getAccountId(), results.get(0).getAccountId(), "Host account ID should match test data");
        verify(hostService).getHostsByRentalAgreementId(rentalAgreementId);
    }

    @Test
    void getHostsByPropertyIdTest() {
        // Arrange
        int propertyId = 1;
        List<Host> mockHosts = Arrays.asList(testHost);
        when(hostService.getHostsByPropertyId(propertyId)).thenReturn(mockHosts);

        // Act
        List<Host> results = hostController.getHostsByPropertyId(propertyId);

        // Assert
        assertNotNull(results, "List of hosts for property should not be null");
        assertFalse(results.isEmpty(), "List of hosts should not be empty");
        assertEquals(testHost.getAccountId(), results.get(0).getAccountId(), "Host account ID should match test data");
        verify(hostService).getHostsByPropertyId(propertyId);
    }

    @Test
    void createHostTest() throws Exception {
        // Arrange
        CreateHostRequest request = new CreateHostRequest(
                "testHost",
                "testPass",
                "Test Host",
                "test@example.com",
                testDate);
        when(hostService.createHost(request)).thenReturn(testHost);

        // Act
        Host result = hostController.createHost(request);

        // Assert
        assertNotNull(result, "Created host should not be null");
        assertEquals(request.getUsername(), result.getUsername(), "Created host username should match request");
        assertEquals(request.getFullName(), result.getFullName(), "Created host full name should match request");
        assertEquals(request.getEmail(), result.getEmail(), "Created host email should match request");
        verify(hostService).createHost(request);
    }

    @Test
    void updateHostTest() throws Exception {
        // Arrange
        int hostId = 1;
        UpdateHostRequest request = new UpdateHostRequest(
                "updatedHost",
                "updatedPass",
                "Updated Host",
                "updated@example.com",
                testDate);
        Host updatedHost = new Host(hostId, request.getUsername(), request.getPassword(),
                request.getFullName(), request.getEmail(), request.getDob());

        when(hostService.updateHost(hostId, request)).thenReturn(updatedHost);

        // Act
        Host result = hostController.updateHost(hostId, request);

        // Assert
        assertNotNull(result, "Updated host should not be null");
        assertEquals(request.getUsername(), result.getUsername(), "Updated host username should match request");
        assertEquals(request.getFullName(), result.getFullName(), "Updated host full name should match request");
        assertEquals(request.getEmail(), result.getEmail(), "Updated host email should match request");
        verify(hostService).updateHost(hostId, request);
    }

    @Test
    void deleteHostByAccountIdTest() throws Exception {
        // Arrange
        int hostId = 1;
        doNothing().when(hostService).deleteHostByAccountId(hostId);

        // Act & Assert
        assertDoesNotThrow(() -> hostController.deleteHostByAccountId(hostId),
                "Host deletion should not throw an exception");
        verify(hostService).deleteHostByAccountId(hostId);
    }

    @Test
    void setPropertyIdsTest() throws Exception {
        // Arrange
        int hostId = 1;
        List<Integer> propertyIds = Arrays.asList(1, 2, 3);
        doNothing().when(hostService).setPropertyIds(hostId, propertyIds);

        // Act & Assert
        assertDoesNotThrow(() -> hostController.setPropertyIds(hostId, propertyIds),
                "Setting property IDs should not throw an exception");
        verify(hostService).setPropertyIds(hostId, propertyIds);
    }

    @Test
    void setRentalAgreementIdsTest() throws Exception {
        // Arrange
        int hostId = 1;
        List<Integer> rentalAgreementIds = Arrays.asList(1, 2, 3);
        doNothing().when(hostService).setRentalAgreementIds(hostId, rentalAgreementIds);

        // Act & Assert
        assertDoesNotThrow(() -> hostController.setRentalAgreementIds(hostId, rentalAgreementIds),
                "Setting rental agreement IDs should not throw an exception");
        verify(hostService).setRentalAgreementIds(hostId, rentalAgreementIds);
    }
}