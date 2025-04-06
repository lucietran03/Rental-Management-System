package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

public class CreateNotificationRequest {
    private int paymentId;
    private int receiverId;
    private int senderId;
    private String content;

    public CreateNotificationRequest(int paymentId, int receiverId, int senderId, String content) {
        this.paymentId = paymentId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
}
