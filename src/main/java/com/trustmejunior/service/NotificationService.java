package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Notification.Notification;
import com.trustmejunior.repository.NotificationRepository;
import com.trustmejunior.request.CreateNotificationRequest;

import java.util.List;

public class NotificationService {

    private NotificationRepository notificationRepository;

    public NotificationService() {
        this.notificationRepository = new NotificationRepository();
    }

    public Notification getNotificationById(int id) {
        return notificationRepository.getNotificationById(id);
    }

    public List<Notification> getAllNotification() {
        return this.notificationRepository.getAllNotification();
    }

    public List<Notification> getNotificationByReceiverId(int id) {
        return this.notificationRepository.getNotificationByReceiverId(id);
    }

    public List<Notification> getNotificationBySenderId(int id) {
        return this.notificationRepository.getNotificationBySenderId(id);
    }

    public List<Notification> getNotificationByPaymentId(int id) {
        return this.notificationRepository.getNotificationByPaymentId(id);
    }

    // Validates the notification data before sending the notification
    private void validateNotificationData(String content, int paymentId, int senderId, int receiverId)
            throws Exception {
        // Check if the notification content is null or empty
        if (content == null || content.trim().isEmpty()) {
            throw new Exception("Notification content cannot be empty");
        }

        // Ensure the notification content does not exceed the character limit
        if (content.length() > 500) {
            throw new Exception("Notification content cannot exceed 500 characters");
        }

        // Validate the payment ID to ensure it is a positive value
        if (paymentId <= 0) {
            throw new Exception("Invalid payment ID");
        }

        // Validate the sender ID to ensure it is a positive value
        if (senderId <= 0) {
            throw new Exception("Invalid sender ID");
        }

        // Validate the receiver ID to ensure it is a positive value
        if (receiverId <= 0) {
            throw new Exception("Invalid receiver ID");
        }

        // Ensure the sender and receiver are not the same
        if (senderId == receiverId) {
            throw new Exception("Sender and receiver cannot be the same");
        }
    }

    // Sends a notification by validating the input data and creating a notification
    // in the repository
    public Notification sendNotification(CreateNotificationRequest request) throws Exception {
        // Extract the details from the request
        String content = request.getContent();
        int paymentId = request.getPaymentId();
        int senderId = request.getSenderId();
        int receiverId = request.getReceiverId();

        // Validate the notification data
        validateNotificationData(content, paymentId, senderId, receiverId);

        // Create and send the notification via the repository
        Notification notification = notificationRepository.sendNotification(content, paymentId, senderId, receiverId);

        // If the notification could not be sent, throw an exception
        if (notification == null) {
            throw new Exception("Notification could not be sent");
        }

        // Return the created notification
        return notification;
    }
}
