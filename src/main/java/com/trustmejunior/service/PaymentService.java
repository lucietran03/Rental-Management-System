package com.trustmejunior.service;

import java.util.Date;
import java.util.List;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.repository.PaymentRepository;
import com.trustmejunior.request.CreatePaymentRequest;
import com.trustmejunior.request.UpdatePaymentRequest;

public class PaymentService {
    private PaymentRepository paymentRepository;

    public PaymentService() {
        this.paymentRepository = new PaymentRepository();
    }

    public Payment getPaymentById(int id) {
        return paymentRepository.getPaymentById(id);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.getAllPayments();
    }

    public List<Payment> getPaymentsByRentalAgreementId(int id) {
        return paymentRepository.getPaymentsByRentalAgreementId(id);
    }

    public List<Payment> getPaymentsByTenantId(int id) {
        return paymentRepository.getPaymentsByTenantId(id);
    }

    // Validates the data for creating or updating a payment
    private void validatePaymentData(double amount, Date dueDate, PaymentStatus status, int rentalAgreementId,
            int tenantId) throws Exception {
        if (amount <= 0) {
            throw new Exception("Payment amount must be greater than 0");
        }
        if (dueDate == null) {
            throw new Exception("Payment due date cannot be empty");
        }
        if (status == null) {
            throw new Exception("Payment status cannot be empty");
        }
        if (rentalAgreementId <= 0) {
            throw new Exception("Invalid rental agreement ID");
        }
        if (tenantId <= 0) {
            throw new Exception("Invalid tenant ID");
        }
    }

    // Creates a new payment record after validating the input data
    public Payment createPayment(CreatePaymentRequest request) throws Exception {
        double amount = request.getAmount();
        Date dueDate = request.getDueDate();
        PaymentMethod method = request.getMethod() != null ? request.getMethod() : null; // Optional payment method
        PaymentStatus status = request.getStatus();
        int rentalAgreementId = request.getRentalAgreementId();
        int tenantId = request.getTenantId();

        // Validate the payment data
        validatePaymentData(amount, dueDate, status, rentalAgreementId, tenantId);

        // Save the payment record in the repository
        Payment payment = paymentRepository.createPayment(amount, dueDate, method, status, rentalAgreementId, tenantId);

        // Throw an exception if the payment creation fails
        if (payment == null) {
            throw new Exception("Failed to create payment");
        }

        return payment;
    }

    // Updates an existing payment record after validating input data
    public Payment updatePayment(int paymentId, UpdatePaymentRequest request) throws Exception {
        if (paymentId <= 0) {
            throw new Exception("Invalid payment ID");
        }

        double amount = request.getAmount();
        Date dueDate = request.getDueDate();
        Date paymentDate = request.getPaymentDate();
        PaymentMethod method = request.getMethod() != null ? request.getMethod() : null; // Optional payment method
        PaymentStatus status = request.getStatus();
        int rentalAgreementId = request.getRentalAgreementId();
        int tenantId = request.getTenantId();

        // Validate the payment data
        validatePaymentData(amount, dueDate, status, rentalAgreementId, tenantId);

        // Check if the payment date is in the future
        if (paymentDate.after(new Date())) {
            throw new Exception("Payment date cannot be in the future");
        }

        // Update the payment record in the repository
        Payment payment = paymentRepository.updatePayment(paymentId, amount, dueDate, paymentDate, method, status,
                rentalAgreementId, tenantId);

        // Throw an exception if the payment update fails
        if (payment == null) {
            throw new Exception("Failed to update payment");
        }

        return payment;
    }

    // Deletes a payment record by its ID
    public void deletePaymentById(int paymentId) {
        paymentRepository.deletePaymentById(paymentId); // Remove the payment record from the repository
    }
}
