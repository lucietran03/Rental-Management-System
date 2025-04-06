package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Notification.Notification;
import com.trustmejunior.request.CreateNotificationRequest;
import com.trustmejunior.service.NotificationService;

import java.util.List;

public class NotificationController {
    private NotificationService notificationService;

    public NotificationController() {
        this.notificationService = new NotificationService();
    }

    public Notification getNotificationById(int id) {
        return notificationService.getNotificationById(id);
    }

    public List<Notification> getAllNotification() {
        return this.notificationService.getAllNotification();
    }

    public List<Notification> getNotificationByReceiverId(int id) {
        return this.notificationService.getNotificationByReceiverId(id);
    }

    public List<Notification> getNotificationBySenderId(int id) {
        return this.notificationService.getNotificationBySenderId(id);
    }

    public List<Notification> getNotificationByPaymentId(int id) {
        return this.notificationService.getNotificationByPaymentId(id);
    }

    public Notification sendNotification(CreateNotificationRequest request) throws Exception {
        return this.notificationService.sendNotification(request);
    }
}
