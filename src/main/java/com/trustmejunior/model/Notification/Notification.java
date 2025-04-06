package com.trustmejunior.model.Notification;

/**
 * @author TrustMeJunior
 */

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Notification {
    private int notificationId;
    private int receiverId;
    private int senderId;
    private int paymentId;
    private String content;
    private LocalDateTime timestamp;

    public Notification() {
    }

    public Notification(int notificationId, int receiverId, int senderId, int paymentId, String content) {
        this.notificationId = notificationId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.paymentId = paymentId;
        this.content = content;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormattedSentTime() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy"));
    }

    public LocalDateTime getSentTime() {
        return timestamp;
    }

    public static LocalDateTime convertToLocalDateTime(String sentTimeString) {
        try {
            DateTimeFormatter formatterWithOffset = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX");
            return ZonedDateTime.parse(sentTimeString, formatterWithOffset).toLocalDateTime();
        } catch (DateTimeParseException e) {
            String dateTimeWithoutOffset = sentTimeString.substring(0, 26);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            return LocalDateTime.parse(dateTimeWithoutOffset, formatter);
        }
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
}
