package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.request.CreatePaymentRequest;
import com.trustmejunior.request.UpdatePaymentRequest;
import com.trustmejunior.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private Payment testPayment;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        testPayment = new Payment(1, 1000.0, testDate, testDate,
                PaymentMethod.BANK_TRANSFER, PaymentStatus.PAID, 1, 1);
    }

    @Test
    void getPaymentByIdTest() {
        // Arrange
        int paymentId = 1;
        when(paymentService.getPaymentById(paymentId)).thenReturn(testPayment);

        // Act
        Payment result = paymentController.getPaymentById(paymentId);

        // Assert
        assertNotNull(result, "Retrieved payment should not be null");
        assertEquals(paymentId, result.getPaymentId(), "Payment ID should match the requested ID");
        assertEquals(testPayment.getAmount(), result.getAmount(), "Payment amount should match");
        verify(paymentService).getPaymentById(paymentId);
    }

    @Test
    void getAllPaymentsTest() {
        // Arrange
        Payment secondPayment = new Payment(2, 2000.0, testDate, testDate,
                PaymentMethod.CREDIT_CARD, PaymentStatus.PENDING, 2, 2);
        List<Payment> mockPayments = Arrays.asList(testPayment, secondPayment);
        when(paymentService.getAllPayments()).thenReturn(mockPayments);

        // Act
        List<Payment> results = paymentController.getAllPayments();

        // Assert
        assertNotNull(results, "List of payments should not be null");
        assertEquals(2, results.size(), "Should return exactly 2 payments");
        assertEquals(testPayment.getPaymentId(), results.get(0).getPaymentId(),
                "First payment ID should match test payment");
        verify(paymentService).getAllPayments();
    }

    @Test
    void getPaymentsByRentalAgreementIdTest() {
        // Arrange
        int rentalAgreementId = 1;
        List<Payment> mockPayments = Arrays.asList(testPayment);
        when(paymentService.getPaymentsByRentalAgreementId(rentalAgreementId)).thenReturn(mockPayments);

        // Act
        List<Payment> results = paymentController.getPaymentsByRentalAgreementId(rentalAgreementId);

        // Assert
        assertNotNull(results, "List of payments for rental agreement should not be null");
        assertFalse(results.isEmpty(), "Payment list should not be empty");
        assertEquals(rentalAgreementId, results.get(0).getRentalAgreementId(),
                "Rental agreement ID should match the requested ID");
        verify(paymentService).getPaymentsByRentalAgreementId(rentalAgreementId);
    }

    @Test
    void getPaymentsByTenantIdTest() {
        // Arrange
        int tenantId = 1;
        List<Payment> mockPayments = Arrays.asList(testPayment);
        when(paymentService.getPaymentsByTenantId(tenantId)).thenReturn(mockPayments);

        // Act
        List<Payment> results = paymentController.getPaymentsByTenantId(tenantId);

        // Assert
        assertNotNull(results, "List of payments for tenant should not be null");
        assertFalse(results.isEmpty(), "Payment list should not be empty");
        assertEquals(tenantId, results.get(0).getTenantId(), "Tenant ID should match the requested ID");
        verify(paymentService).getPaymentsByTenantId(tenantId);
    }

    @Test
    void createPaymentTest() throws Exception {
        // Arrange
        CreatePaymentRequest request = new CreatePaymentRequest(
                1000.0,
                testDate,
                PaymentMethod.BANK_TRANSFER,
                PaymentStatus.PAID,
                1,
                1);
        when(paymentService.createPayment(request)).thenReturn(testPayment);

        // Act
        Payment result = paymentController.createPayment(request);

        // Assert
        assertNotNull(result, "Created payment should not be null");
        assertEquals(request.getAmount(), result.getAmount(), "Payment amount should match the requested amount");
        assertEquals(request.getMethod(), result.getMethod(), "Payment method should match the requested method");
        assertEquals(request.getStatus(), result.getStatus(), "Payment status should match the requested status");
        assertEquals(request.getRentalAgreementId(), result.getRentalAgreementId(), "Rental agreement ID should match");
        assertEquals(request.getTenantId(), result.getTenantId(), "Tenant ID should match");
        verify(paymentService).createPayment(request);
    }

    @Test
    void updatePaymentTest() throws Exception {
        // Arrange
        int paymentId = 1;
        UpdatePaymentRequest request = new UpdatePaymentRequest(
                2000.0,
                testDate,
                testDate,
                PaymentMethod.CREDIT_CARD,
                PaymentStatus.PAID,
                1,
                1);
        Payment updatedPayment = new Payment(paymentId, request.getAmount(), request.getDueDate(),
                request.getPaymentDate(), request.getMethod(), request.getStatus(),
                request.getRentalAgreementId(), request.getTenantId());

        when(paymentService.updatePayment(paymentId, request)).thenReturn(updatedPayment);

        // Act
        Payment result = paymentController.updatePayment(paymentId, request);

        // Assert
        assertNotNull(result, "Updated payment should not be null");
        assertEquals(request.getAmount(), result.getAmount(), "Updated amount should match the requested amount");
        assertEquals(request.getMethod(), result.getMethod(), "Updated method should match the requested method");
        assertEquals(request.getStatus(), result.getStatus(), "Updated status should match the requested status");
        verify(paymentService).updatePayment(paymentId, request);
    }

    @Test
    void deletePaymentByIdTest() throws Exception {
        // Arrange
        int paymentId = 1;
        doNothing().when(paymentService).deletePaymentById(paymentId);

        // Act & Assert
        assertDoesNotThrow(() -> paymentController.deletePaymentById(paymentId),
                "Deleting payment should not throw an exception");
        verify(paymentService).deletePaymentById(paymentId);
    }
}