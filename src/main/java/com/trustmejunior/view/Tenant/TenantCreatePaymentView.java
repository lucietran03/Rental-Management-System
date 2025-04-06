package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.NotificationController;
import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.Notification.Notification;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.request.UpdatePaymentRequest;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class TenantCreatePaymentView implements Initializable {
    @FXML
    private ImageView PaymentLogoImageView;

    @FXML
    private TextField amountInput;

    @FXML
    private Button createPaymentButton;

    @FXML
    private DatePicker dateInput;

    @FXML
    private ComboBox<PaymentMethod> methodInput;

    @FXML
    private TextField rentalAgreementInput;

    @FXML
    private Button returnHomeButton;

    private Account account = SessionManager.getCurrentAccount();
    private NotificationController notificationController = new NotificationController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private PaymentController paymentController = new PaymentController();
    private Integer notificationId;
    private Integer paymentId;

    private boolean validateInputs() {
        try {
            // Check if a payment method is selected in the combo box
            if (methodInput.getValue() == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select a payment method.");
                return false;
            }

            // Additional validations for other fields can be added here

        } catch (Exception e) {
            e.printStackTrace();
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error",
                    "An unexpected error occurred during validation.");
            return false;
        }

        return true; // Return true if all validations pass
    }

    @FXML
    void createPayment() {
        // If validation fails, return and do not proceed
        if (!validateInputs()) {
            return;
        }

        try {
            Notification notification;
            Payment payment = new Payment();
            if (notificationId != null)
            {
                // Retrieve the notification and payment details based on notificationId
                notification = notificationController.getNotificationById(notificationId);
                payment = paymentController.getPaymentById(notification.getPaymentId());
            }
            else
            {
                payment = paymentController.getPaymentById(paymentId);
            }

            // Parse the input amount and payment date
            double amount = Double.parseDouble(amountInput.getText());
            Date paymentDate = Date.from(dateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            PaymentMethod method = methodInput.getValue();
            PaymentStatus status = null;

            // Determine payment status based on whether payment is made on time
            if (paymentDate.after(payment.getDueDate())) {
                status = PaymentStatus.LATE; // If payment is late
            } else {
                status = PaymentStatus.PAID; // If payment is on time
            }

            // Create an update payment request and update the payment details
            UpdatePaymentRequest request = new UpdatePaymentRequest(amount, payment.getDueDate(), paymentDate, method,
                    status, payment.getRentalAgreementId(), payment.getTenantId());
            paymentController.updatePayment(payment.getPaymentId(), request);
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Payment updated successfully");

            // Navigate back to the previous view
            returnHome();
        } catch (Exception e) {
            // Show an error alert if payment creation fails
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to create payment: " + e.getMessage());
        }
    }

    @FXML
    void returnHome() {
        try {
            // Load the TenantAllPaymentView scene
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantAllPaymentView.fxml"));
            Parent root = loader.load();
            TenantAllPaymentView view = loader.getController();
            view.showAllPayments(FXCollections.observableArrayList(paymentController.getPaymentsByTenantId(account.getAccountId())));

            // Navigate back to the home screen
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void initData(int Id) {
        // Initialize data based on the provided notification ID
        Notification notification = new Notification();
        Payment payment = new Payment();
        RentalAgreement rentalAgreement = new RentalAgreement();
        if (notificationController.getNotificationById(Id) != null) {
            notification = notificationController.getNotificationById(Id);
            this.notificationId = Id;
            payment = paymentController.getPaymentById(notification.getPaymentId());
            this.paymentId = notification.getPaymentId();
            rentalAgreement = rentalAgreementController.getRentalAgreementById(payment.getRentalAgreementId());
        }
        else
        {
            payment = paymentController.getPaymentById(Id);
            this.paymentId = Id;
            rentalAgreement = rentalAgreementController.getRentalAgreementById(payment.getRentalAgreementId());
        }

        // Set the input fields with payment and rental agreement data
        amountInput.setText(String.valueOf(payment.getAmount()));
        dateInput.setValue(LocalDate.now());
        rentalAgreementInput.setText(String.format("#%s", rentalAgreement.getRentalAgreementId()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize payment method combo box with available values
        methodInput.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
    }
}