package com.trustmejunior.model.RentalEntity;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;

import java.util.Date;

public class Payment {
    private int paymentId;
    private double amount;
    private Date dueDate;
    private Date paidDate;
    private PaymentMethod method;
    private PaymentStatus status;
    private int rentalAgreementId;
    private int tenantId;

    public Payment() {}

    public Payment(int paymentId, double amount, Date dueDate, Date paidDate, PaymentMethod method,
            PaymentStatus status, int rentalAgreementId, int tenantId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
        this.method = method;
        this.status = status;
        this.rentalAgreementId = rentalAgreementId;
        this.tenantId = tenantId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
