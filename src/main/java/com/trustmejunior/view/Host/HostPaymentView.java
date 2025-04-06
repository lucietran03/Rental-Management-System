package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.utils.DateFormatter;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HostPaymentView implements Initializable {
    @FXML
    private Label amount;
    @FXML
    private Label dueDate;
    @FXML
    private Label paymentDate;
    @FXML
    private Label method;
    @FXML
    private Label status;
    @FXML
    private Label rentalAgreement;
    @FXML
    private Label tenant;
    @FXML
    private Button returnButton;

    private Account account = SessionManager.getCurrentAccount();

    private PaymentController paymentController = new PaymentController();
    private PropertyController propertyController = new PropertyController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private TenantController tenantController = new TenantController();

    public void initData(Payment payment) {
        // Set payment amount, due date, payment date, method, and status on the UI
        amount.setText(String.format("$%.2f", payment.getAmount()));
        dueDate.setText(DateFormatter.formatDate(payment.getDueDate(), "dd/MM/yyyy"));
        paymentDate.setText(payment.getPaidDate() != null
                ? DateFormatter.formatDate(payment.getPaidDate(), "dd/MM/yyyy")
                : "N/A");
        method.setText(payment.getMethod() != null
                ? payment.getMethod().toString()
                : "N/A");
        status.setText(payment.getStatus().toString());

        // Retrieve rental agreement and property info, then display on the UI
        RentalAgreement rentalAgreementObj = rentalAgreementController
                .getRentalAgreementById(payment.getRentalAgreementId());
        Property propertyObj = propertyController.getPropertyById(rentalAgreementObj.getPropertyId());
        rentalAgreement.setText(String.format("%s - %s to %s (ID: %d)", propertyObj.getAddress(),
                DateFormatter.formatDate(rentalAgreementObj.getStartDate(), "dd/MM/yyyy"),
                DateFormatter.formatDate(rentalAgreementObj.getEndDate(), "dd/MM/yyyy"),
                rentalAgreementObj.getRentalAgreementId()));

        // Retrieve tenant info and display on the UI
        Tenant tenantObj = tenantController.getTenantByAccountId(payment.getTenantId());
        tenant.setText(String.format("%s (ID: %d)", tenantObj.getFullName(), tenantObj.getAccountId()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up return button action to load the HostAllPaymentView
        returnButton.setOnAction(event -> {
            try {
                // Load the HostAllPaymentView
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/HostAllPaymentView.fxml"));
                Parent root = loader.load();
                HostAllPaymentView view = loader.getController();

                // Get all rental agreements for the host and retrieve related payments
                List<RentalAgreement> rentalAgreements = rentalAgreementController
                        .getRentalAgreementsByHostId(account.getAccountId());

                List<Payment> payments = new ArrayList<>();
                for (RentalAgreement rentalAgreement : rentalAgreements) {
                    payments.addAll(
                            paymentController.getPaymentsByRentalAgreementId(rentalAgreement.getRentalAgreementId()));
                }

                // Display all payments in the HostAllPaymentView
                view.showAllPayments(FXCollections.observableArrayList(payments));

                // Set the main layout to the HostAllPaymentView
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                // Handle any loading errors
                e.printStackTrace();
            }
        });
    }
}
