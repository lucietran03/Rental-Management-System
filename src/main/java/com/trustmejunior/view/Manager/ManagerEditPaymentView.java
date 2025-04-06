package com.trustmejunior.view.Manager;

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
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerEditPaymentView implements Initializable {
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
        // Initialize payment method choices
        methodInput.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        methodInput.setValue(PaymentMethod.CASH);

        // Initialize payment status choices
        statusInput.setItems(FXCollections.observableArrayList(PaymentStatus.values()));
        statusInput.setValue(PaymentStatus.PENDING);

        // Initialize rental agreement choices
        List<RentalAgreement> rentalAgreements = rentalAgreementController.getAllRentalAgreements();

        rentalAgreements.forEach(ra -> {
            Property property = propertyController.getPropertyById(ra.getPropertyId());
            String raChoice = String.format("%s - %s to %s (ID: %d)",
                    property.getAddress(),
                    DateFormatter.formatDate(ra.getStartDate(), "dd/MM/yyyy"),
                    DateFormatter.formatDate(ra.getEndDate(), "dd/MM/yyyy"),
                    ra.getRentalAgreementId());
            rentalAgreementInput.getItems().add(raChoice);
        });
    }

    public void initData(Payment payment) {
        this.currentPayment = payment;

        // Set current values
        amountInput.setText(String.format("%.2f", payment.getAmount()));
        dueDateInput.setValue(payment.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        if (payment.getDueDate() != null) {
            paymentDateInput.setValue(payment.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } else
            paymentDateInput.setValue(null);
        methodInput.setValue(payment.getMethod());
        statusInput.setValue(payment.getStatus());

        // Set current rental agreement
        RentalAgreement currentRA = rentalAgreementController.getRentalAgreementById(payment.getRentalAgreementId());
        Property property = propertyController.getPropertyById(currentRA.getPropertyId());
        String currentRAChoice = String.format("%s - %s to %s (ID: %d)",
                property.getAddress(),
                DateFormatter.formatDate(currentRA.getStartDate(), "dd/MM/yyyy"),
                DateFormatter.formatDate(currentRA.getEndDate(), "dd/MM/yyyy"),
                currentRA.getRentalAgreementId());
        rentalAgreementInput.setValue(currentRAChoice);
    }

    @FXML
    // Handle the action to update payment information
    public void updatePayment(ActionEvent event) {
        // Validate inputs before processing the update
        if (!validateInputs()) {
            return; // Return early if validation fails
        }

        try {
            // Parse input values for payment update
            double amount = Double.parseDouble(amountInput.getText());
            Date dueDate = Date.from(dueDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date paymentDate = null;
            if (paymentDateInput.getValue() != null) {
                paymentDate = Date.from(paymentDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
            PaymentMethod method = methodInput.getValue();
            PaymentStatus status = statusInput.getValue();

            // Extract rental agreement ID and tenant details
            String selectedRA = rentalAgreementInput.getValue();
            int raId = Integer
                    .parseInt(selectedRA.substring(selectedRA.lastIndexOf("ID: ") + 4, selectedRA.length() - 1));
            Tenant tenant = tenantController.getMainTenantByRentalAgreementId(raId);
            int tenantId = tenant.getAccountId();

            // Create request for updating payment and call the update method
            UpdatePaymentRequest request = new UpdatePaymentRequest(amount, dueDate, paymentDate, method, status, raId,
                    tenantId);
            paymentController.updatePayment(currentPayment.getPaymentId(), request);

            // Show success message and return to the previous screen
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Payment updated successfully");
            returnHome(event);
        } catch (Exception e) {
            // Show error message if an exception occurs during the update process
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to update payment: " + e.getMessage());
        }
    }

    // Validate the inputs for payment details
    private boolean validateInputs() {
        // Validate amount (must be greater than 0)
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

        // Validate due date (must be selected)
        if (dueDateInput.getValue() == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select a date");
            return false;
        }

        // Validate rental agreement selection (must be selected)
        if (rentalAgreementInput.getValue() == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select a rental agreement");
            return false;
        }

        return true; // Return true if all validations pass
    }

    @FXML
    // Return to the home screen showing the list of payments
    public void returnHome(ActionEvent event) {
        try {
            // Load the ManagerAllPaymentView and display all payments
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllPaymentView.fxml"));
            Parent root = loader.load();
            ManagerAllPaymentView view = loader.getController();
            view.showAllPayments(FXCollections.observableArrayList(paymentController.getAllPayments()));

            // Set the scene to display the ManagerAllPaymentView
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Show error message if loading the view fails
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to return to payment list: " + e.getMessage());
        }
    }
}
