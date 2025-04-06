package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.NotificationController;
import com.trustmejunior.model.Notification.Notification;
import com.trustmejunior.request.CreateNotificationRequest;
import com.trustmejunior.service.NotificationService;
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
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new Notification(1, 1, 2, 3, "Test notification content");
    }

    @Test
    void getNotificationByIdTest() {
        // Arrange
        int notificationId = 1;
        when(notificationService.getNotificationById(notificationId)).thenReturn(testNotification);

        // Act
        Notification result = notificationController.getNotificationById(notificationId);

        // Assert
        assertNotNull(result, "Retrieved notification should not be null");
        assertEquals(notificationId, result.getNotificationId(), "Notification ID should match the requested ID");
        assertEquals(testNotification.getContent(), result.getContent(),
                "Notification content should match the test notification");
        verify(notificationService).getNotificationById(notificationId);
    }

    @Test
    void getAllNotificationTest() {
        // Arrange
        List<Notification> mockNotifications = Arrays.asList(
                testNotification,
                new Notification(2, 2, 3, 4, "Another test notification"));
        when(notificationService.getAllNotification()).thenReturn(mockNotifications);

        // Act
        List<Notification> results = notificationController.getAllNotification();

        // Assert
        assertNotNull(results, "Retrieved notifications list should not be null");
        assertEquals(2, results.size(), "Should return exactly 2 notifications");
        assertEquals(testNotification.getNotificationId(), results.get(0).getNotificationId(),
                "First notification ID should match test notification");
        verify(notificationService).getAllNotification();
    }

    @Test
    void getNotificationByReceiverIdTest() {
        // Arrange
        int receiverId = 1;
        List<Notification> mockNotifications = Arrays.asList(testNotification);
        when(notificationService.getNotificationByReceiverId(receiverId)).thenReturn(mockNotifications);

        // Act
        List<Notification> results = notificationController.getNotificationByReceiverId(receiverId);

        // Assert
        assertNotNull(results, "Retrieved notifications list should not be null");
        assertFalse(results.isEmpty(), "Notifications list should not be empty");
        assertEquals(receiverId, results.get(0).getReceiverId(), "Notification receiver ID should match requested ID");
        verify(notificationService).getNotificationByReceiverId(receiverId);
    }

    @Test
    void getNotificationBySenderIdTest() {
        // Arrange
        int senderId = 2;
        List<Notification> mockNotifications = Arrays.asList(testNotification);
        when(notificationService.getNotificationBySenderId(senderId)).thenReturn(mockNotifications);

        // Act
        List<Notification> results = notificationController.getNotificationBySenderId(senderId);

        // Assert
        assertNotNull(results, "Retrieved notifications list should not be null");
        assertFalse(results.isEmpty(), "Notifications list should not be empty");
        assertEquals(senderId, results.get(0).getSenderId(), "Notification sender ID should match requested ID");
        verify(notificationService).getNotificationBySenderId(senderId);
    }

    @Test
    void getNotificationByPaymentIdTest() {
        // Arrange
        int paymentId = 3;
        List<Notification> mockNotifications = Arrays.asList(testNotification);
        when(notificationService.getNotificationByPaymentId(paymentId)).thenReturn(mockNotifications);

        // Act
        List<Notification> results = notificationController.getNotificationByPaymentId(paymentId);

        // Assert
        assertNotNull(results, "Retrieved notifications list should not be null");
        assertFalse(results.isEmpty(), "Notifications list should not be empty");
        assertEquals(paymentId, results.get(0).getPaymentId(), "Notification payment ID should match requested ID");
        verify(notificationService).getNotificationByPaymentId(paymentId);
    }

    @Test
    void sendNotificationTest() throws Exception {
        // Arrange
        CreateNotificationRequest request = new CreateNotificationRequest(
                3, // paymentId
                1, // receiverId
                2, // senderId
                "Test notification content");
        when(notificationService.sendNotification(request)).thenReturn(testNotification);

        // Act
        Notification result = notificationController.sendNotification(request);

        // Assert
        assertNotNull(result, "Created notification should not be null");
        assertEquals(request.getContent(), result.getContent(), "Notification content should match request content");
        assertEquals(request.getPaymentId(), result.getPaymentId(),
                "Notification payment ID should match request payment ID");
        assertEquals(request.getReceiverId(), result.getReceiverId(),
                "Notification receiver ID should match request receiver ID");
        assertEquals(request.getSenderId(), result.getSenderId(),
                "Notification sender ID should match request sender ID");
        verify(notificationService).sendNotification(request);
    }
}