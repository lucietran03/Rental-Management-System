package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.request.UpdatePaymentRequest;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.DateFormatter;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class HostEditPaymentView implements Initializable {
    @FXML
    private TextField amountInput;

    @FXML
    private DatePicker dueDateInput;

    @FXML
    private DatePicker paymentDateInput;

    @FXML
    private ChoiceBox<PaymentMethod> methodInput;

    @FXML
    private ChoiceBox<PaymentStatus> statusInput;

    @FXML
    private ComboBox<String> rentalAgreementInput;

    @FXML
    private Button updatePaymentButton;

    @FXML
    private Button returnHomeButton;

    private Account account = SessionManager.getCurrentAccount();
    private PaymentController paymentController = new PaymentController();
    private PropertyController propertyController = new PropertyController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private TenantController tenantController = new TenantController();
    private Payment currentPayment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rentalAgreementInput.setDisable(true); // Disable rental agreement input initially

        // Initialize payment method choices
        methodInput.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        methodInput.setValue(PaymentMethod.CASH); // Default payment method

        // Initialize payment status choices
        statusInput.setItems(FXCollections.observableArrayList(PaymentStatus.values()));
        statusInput.setValue(PaymentStatus.PENDING); // Default payment status

        // Initialize rental agreement choices
        List<RentalAgreement> rentalAgreements = rentalAgreementController.getAllRentalAgreements();

        rentalAgreements.forEach(ra -> {
            Property property = propertyController.getPropertyById(ra.getPropertyId());
            String raChoice = String.format("%s - %s to %s (ID: %d)", // Format rental agreement details
                    property.getAddress(),
                    DateFormatter.formatDate(ra.getStartDate(), "dd/MM/yyyy"),
                    DateFormatter.formatDate(ra.getEndDate(), "dd/MM/yyyy"),
                    ra.getRentalAgreementId());
            rentalAgreementInput.getItems().add(raChoice);
        });
    }

    public void initData(Payment payment) {
        this.currentPayment = payment;

        // Set current payment values
        amountInput.setText(String.format("%.2f", payment.getAmount()));
        dueDateInput.setValue(payment.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        if (payment.getDueDate() != null) {
            paymentDateInput.setValue(payment.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } else
            paymentDateInput.setValue(null);
        methodInput.setValue(payment.getMethod());
        statusInput.setValue(payment.getStatus());

        // Set current rental agreement details
        RentalAgreement currentRA = rentalAgreementController.getRentalAgreementById(payment.getRentalAgreementId());
        Property property = propertyController.getPropertyById(currentRA.getPropertyId());
        String currentRAChoice = String.format("%s - %s to %s (ID: %d)", // Format current rental agreement
                property.getAddress(),
                DateFormatter.formatDate(currentRA.getStartDate(), "dd/MM/yyyy"),
                DateFormatter.formatDate(currentRA.getEndDate(), "dd/MM/yyyy"),
                currentRA.getRentalAgreementId());
        rentalAgreementInput.setValue(currentRAChoice);
    }

    @FXML
    public void updatePayment(ActionEvent event) {
        if (!validateInputs()) { // Validate inputs before proceeding
            return;
        }

        try {
            // Parse input values
            double amount = Double.parseDouble(amountInput.getText());
            Date dueDate = Date.from(dueDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date paymentDate = null;
            if (paymentDateInput.getValue() != null) {
                paymentDate = Date.from(paymentDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
            PaymentMethod method = methodInput.getValue();
            PaymentStatus status = statusInput.getValue();

            // Extract rental agreement ID
            String selectedRA = rentalAgreementInput.getValue();
            int raId = Integer
                    .parseInt(selectedRA.substring(selectedRA.lastIndexOf("ID: ") + 4, selectedRA.length() - 1));
            Tenant tenant = tenantController.getMainTenantByRentalAgreementId(raId);
            int tenantId = tenant.getAccountId();

            // Create update request for payment
            UpdatePaymentRequest request = new UpdatePaymentRequest(amount, dueDate, paymentDate, method, status, raId,
                    tenantId);
            paymentController.updatePayment(currentPayment.getPaymentId(), request);
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Payment updated successfully");

            returnHome(event); // Return to the previous screen
        } catch (Exception e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to update payment: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        // Validate amount input
        try {
            double amount = Double.parseDouble(amountInput.getText());
            if (amount <= 0) {
                CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Amount must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Amount must be a valid number");
            return false;
        }

        // Validate due date input
        if (dueDateInput.getValue() == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select a date");
            return false;
        }

        // Validate rental agreement selection
        if (rentalAgreementInput.getValue() == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select a rental agreement");
            return false;
        }

        return true;
    }

    @FXML
    public void returnHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/HostAllPaymentView.fxml"));
            Parent root = loader.load();
            HostAllPaymentView view = loader.getController();

            List<RentalAgreement> rentalAgreements = rentalAgreementController
                    .getRentalAgreementsByHostId(account.getAccountId());
            List<Payment> payments = new ArrayList<>();
            for (RentalAgreement rentalAgreement : rentalAgreements) {
                payments.addAll(
                        paymentController.getPaymentsByRentalAgreementId(rentalAgreement.getRentalAgreementId()));
            }
            view.showAllPayments(FXCollections.observableArrayList(payments));

            // Set the main view to return home
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to return to payment list: " + e.getMessage());
        }
    }
}
