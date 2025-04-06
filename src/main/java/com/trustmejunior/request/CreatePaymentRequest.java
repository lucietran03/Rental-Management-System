package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import java.util.Date;

import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;

public class CreatePaymentRequest {
    private double amount;
    private Date dueDate;
    private PaymentMethod method;
    private PaymentStatus status;
    private int rentalAgreementId;
    private int tenantId;

    public CreatePaymentRequest(double amount, Date dueDate, PaymentMethod method, PaymentStatus status,
            int rentalAgreementId, int tenantId) {
        this.amount = amount;
        this.dueDate = dueDate;
        this.method = method;
        this.status = status;
        this.rentalAgreementId = rentalAgreementId;
        this.tenantId = tenantId;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public int getRentalAgreementId() {
        return rentalAgreementId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setRentalAgreementId(int rentalAgreementId) {
        this.rentalAgreementId = rentalAgreementId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
}
