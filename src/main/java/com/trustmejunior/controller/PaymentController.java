package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.service.PaymentService;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.request.CreatePaymentRequest;
import com.trustmejunior.request.UpdatePaymentRequest;

import java.util.List;

public class PaymentController {
    private PaymentService paymentService;

    public PaymentController() {
        this.paymentService = new PaymentService();
    }

    public Payment getPaymentById(int paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    public List<Payment> getPaymentsByRentalAgreementId(int id) {
        return paymentService.getPaymentsByRentalAgreementId(id);
    }

    public List<Payment> getPaymentsByTenantId(int id) {
        return paymentService.getPaymentsByTenantId(id);
    }

    public Payment createPayment(CreatePaymentRequest request) throws Exception {
        return paymentService.createPayment(request);
    }

    public Payment updatePayment(int paymentId, UpdatePaymentRequest request) throws Exception {
        return paymentService.updatePayment(paymentId, request);
    }

    public void deletePaymentById(int paymentId) {
        paymentService.deletePaymentById(paymentId);
    }

}
