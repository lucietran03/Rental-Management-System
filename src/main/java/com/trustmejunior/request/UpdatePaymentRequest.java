package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import java.util.Date;

import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;

public class UpdatePaymentRequest {
    private double amount;
    private Date dueDate;
    private Date paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;
    private int rentalAgreementId;
    private int tenantId;

    public UpdatePaymentRequest(double amount, Date dueDate, Date paymentDate, PaymentMethod method,
            PaymentStatus status, int rentalAgreementId, int tenantId) {
        this.amount = amount;
        this.dueDate = dueDate;
        this.paymentDate = paymentDate;
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

    public Date getPaymentDate() {
        return paymentDate;
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

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
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
