package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.ManagerController;
import com.trustmejunior.model.User.Manager;
import com.trustmejunior.request.CreateManagerRequest;
import com.trustmejunior.request.UpdateManagerRequest;
import com.trustmejunior.service.ManagerService;
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
class ManagerControllerTest {

    @Mock
    private ManagerService managerService;

    @InjectMocks
    private ManagerController managerController;

    private Manager testManager;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        testManager = new Manager(1, "testManager", "testPass", "test@example.com", "Test Manager", testDate);
    }

    @Test
    void getManagerByAccountId() {
        // Arrange
        int accountId = 1;
        when(managerService.getManagerByAccountId(accountId)).thenReturn(testManager);

        // Act
        Manager result = managerController.getManagerByAccountId(accountId);

        // Assert
        assertNotNull(result, "Retrieved manager should not be null");
        assertEquals(accountId, result.getAccountId(), "Manager account ID should match the requested ID");
        assertEquals(testManager.getUsername(), result.getUsername(), "Manager username should match");
        assertEquals(testManager.getEmail(), result.getEmail(), "Manager email should match");
        verify(managerService).getManagerByAccountId(accountId);
    }

    @Test
    void getAllManagersTest() {
        // Arrange
        Manager secondManager = new Manager(2, "manager2", "pass2", "manager2@example.com", "Second Manager", testDate);
        List<Manager> mockManagers = Arrays.asList(testManager, secondManager);
        when(managerService.getAllManagers()).thenReturn(mockManagers);

        // Act
        List<Manager> results = managerController.getAllManagers();

        // Assert
        assertNotNull(results, "Retrieved managers list should not be null");
        assertEquals(2, results.size(), "Should return exactly 2 managers");
        assertEquals(testManager.getAccountId(), results.get(0).getAccountId(),
                "First manager's account ID should match");
        assertEquals(testManager.getUsername(), results.get(0).getUsername(), "First manager's username should match");
        verify(managerService).getAllManagers();
    }

    @Test
    void createManagerTest() throws Exception {
        // Arrange
        CreateManagerRequest request = new CreateManagerRequest(
                "testManager",
                "testPass",
                "test@example.com",
                "Test Manager",
                testDate);
        when(managerService.createManager(request)).thenReturn(testManager);

        // Act
        Manager result = managerController.createManager(request);

        // Assert
        assertNotNull(result, "Created manager should not be null");
        assertEquals(request.getUsername(), result.getUsername(), "Created manager's username should match request");
        assertEquals(request.getEmail(), result.getEmail(), "Created manager's email should match request");
        assertEquals(request.getFullName(), result.getFullName(), "Created manager's full name should match request");
        verify(managerService).createManager(request);
    }

    @Test
    void updateManagerTest() throws Exception {
        // Arrange
        int managerId = 1;
        UpdateManagerRequest request = new UpdateManagerRequest(
                "updatedManager",
                "updatedPass",
                "updated@example.com",
                "Updated Manager",
                testDate);
        Manager updatedManager = new Manager(managerId, request.getUsername(), request.getPassword(),
                request.getFullName(), request.getEmail(), request.getDob());

        when(managerService.updateManager(managerId, request)).thenReturn(updatedManager);

        // Act
        Manager result = managerController.updateManager(managerId, request);

        // Assert
        assertNotNull(result, "Updated manager should not be null");
        assertEquals(request.getUsername(), result.getUsername(), "Updated manager's username should match request");
        assertEquals(request.getEmail(), result.getEmail(), "Updated manager's email should match request");
        assertEquals(request.getFullName(), result.getFullName(), "Updated manager's full name should match request");
        verify(managerService).updateManager(managerId, request);
    }

    @Test
    void deleteManagerByAccountIdTest() throws Exception {
        // Arrange
        int managerId = 1;
        doNothing().when(managerService).deleteManagerByAccountId(managerId);

        // Act & Assert
        assertDoesNotThrow(() -> managerController.deleteManagerByAccountId(managerId),
                "Manager deletion should not throw any exceptions");
        verify(managerService).deleteManagerByAccountId(managerId);
    }
}