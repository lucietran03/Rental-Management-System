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
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.request.CreatePaymentRequest;
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

public class ManagerCreatePaymentView implements Initializable {
    @FXML
    private TextField amountInput;

    @FXML
    private DatePicker dueDateInput;

    @FXML
    private ChoiceBox<PaymentMethod> methodInput;

    @FXML
    private ChoiceBox<PaymentStatus> statusInput;

    @FXML
    private ComboBox<String> rentalAgreementInput;

    @FXML
    private Button createPaymentButton;

    @FXML
    private Button returnHomeButton;

    private Account account = SessionManager.getCurrentAccount();

    private PaymentController paymentController = new PaymentController();
    private PropertyController propertyController = new PropertyController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private TenantController tenantController = new TenantController();

    public void initData() {
    }

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

    @FXML
    public void createPayment(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            double amount = Double.parseDouble(amountInput.getText());
            Date dueDate = Date.from(dueDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            PaymentMethod method = methodInput.getValue();
            PaymentStatus status = statusInput.getValue();

            // Extract rental agreement ID from the selected choice
            String selectedRA = rentalAgreementInput.getValue();
            int raId = Integer
                    .parseInt(selectedRA.substring(selectedRA.lastIndexOf("ID: ") + 4, selectedRA.length() - 1));
            Tenant tenant = tenantController.getMainTenantByRentalAgreementId(raId);
            int tenantId = tenant.getAccountId();

            // Create payment request
            CreatePaymentRequest request = new CreatePaymentRequest(amount, dueDate, method, status, raId, tenantId);

            paymentController.createPayment(request);
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Payment created successfully");

            returnHome(event);
        } catch (Exception e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to create payment: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        // Validate amount
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

        // Validate date
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
            // Load the ManagerAllPaymentView and display all payments
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllPaymentView.fxml"));
            Parent root = loader.load();
            ManagerAllPaymentView view = loader.getController();
            view.showAllPayments(FXCollections.observableArrayList(paymentController.getAllPayments()));

            // Set the main layout view
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Show error alert if loading the view fails
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to return to payment list: " + e.getMessage());
        }
    }
}
