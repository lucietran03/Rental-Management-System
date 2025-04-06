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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class HostCreatePaymentView implements Initializable {
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
//        rentalAgreementInput.setDisable(true);
//
        amountInput.setDisable(true);
        amountInput.setPromptText("Rental Agreement Fee");

        // Initialize payment method choices and set default to CASH
        methodInput.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        methodInput.setValue(null);

        // Initialize payment status choices and set default to PENDING
        statusInput.setItems(FXCollections.observableArrayList(PaymentStatus.values()));
        statusInput.setValue(PaymentStatus.PENDING);

        // Load rental agreements and populate the dropdown with formatted options
        List<RentalAgreement> rentalAgreements = rentalAgreementController.getRentalAgreementsByHostId(account.getAccountId());
        rentalAgreements.forEach(ra -> {
            Property property = propertyController.getPropertyById(ra.getPropertyId());
            String raChoice = String.format("%s - %s to %s (ID: %d)",
                    property.getAddress(),
                    DateFormatter.formatDate(ra.getStartDate(), "dd/MM/yyyy"),
                    DateFormatter.formatDate(ra.getEndDate(), "dd/MM/yyyy"),
                    ra.getRentalAgreementId());
            rentalAgreementInput.getItems().add(raChoice);
        });

        rentalAgreementInput.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (rentalAgreementInput.getValue() != null) {
                RentalAgreement rentalAgreement = rentalAgreementController.getRentalAgreementById(Integer.parseInt(rentalAgreementInput.getValue().substring(rentalAgreementInput.getValue().lastIndexOf("ID: ") + 4, rentalAgreementInput.getValue().length() - 1)));
                amountInput.setText(String.valueOf(rentalAgreement.getFee()));
            }
        });
    }

    @FXML
    public void createPayment(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            // Parse input values for payment creation
            Date dueDate = Date.from(dueDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            PaymentMethod method = methodInput.getValue();
            PaymentStatus status = statusInput.getValue();

            // Extract rental agreement ID from selected choice
            String selectedRA = rentalAgreementInput.getValue();
            int raId = Integer.parseInt(selectedRA.substring(selectedRA.lastIndexOf("ID: ") + 4, selectedRA.length() - 1));
            Tenant tenant = tenantController.getMainTenantByRentalAgreementId(raId);
            int tenantId = tenant.getAccountId();
            RentalAgreement rentalAgreement = rentalAgreementController.getRentalAgreementById(raId);
            double amount = rentalAgreement.getFee();

            // Create payment request and call controller to create payment
            CreatePaymentRequest request = new CreatePaymentRequest(amount, dueDate, method, status, raId, tenantId);
            paymentController.createPayment(request);
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Payment created successfully");

            returnHome();
        } catch (Exception e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to create payment: " + e.getMessage());
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
    public void returnHome() {
        try {
            // Load payment view and fetch related payments for host
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

            // Display all payments in the view
            view.showAllPayments(FXCollections.observableArrayList(payments));

            // Update the main view to display payment list
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to return to payment list: " + e.getMessage());
        }
    }
}
