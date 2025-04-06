package com.trustmejunior.view.Manager;

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
import com.trustmejunior.utils.DateFormatter;
import com.trustmejunior.utils.SessionManager;
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
import java.util.ResourceBundle;

public class ManagerPaymentView implements Initializable {
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

    // Initializes payment data into the form fields
    public void initData(Payment payment) {
        // Display payment details
        amount.setText(String.format("$%.2f", payment.getAmount()));
        dueDate.setText(DateFormatter.formatDate(payment.getDueDate(), "dd/MM/yyyy"));
        paymentDate.setText(payment.getPaidDate() != null
                ? DateFormatter.formatDate(payment.getPaidDate(), "dd/MM/yyyy")
                : "N/A");
        method.setText(payment.getMethod() != null
                ? payment.getMethod().toString()
                : "N/A");
        status.setText(payment.getStatus().toString());

        // Retrieve and display rental agreement information
        RentalAgreement rentalAgreementObj = rentalAgreementController
                .getRentalAgreementById(payment.getRentalAgreementId());
        Property propertyObj = propertyController.getPropertyById(rentalAgreementObj.getPropertyId());
        rentalAgreement.setText(String.format("%s - %s to %s (ID: %d)", propertyObj.getAddress(),
                DateFormatter.formatDate(rentalAgreementObj.getStartDate(), "dd/MM/yyyy"),
                DateFormatter.formatDate(rentalAgreementObj.getEndDate(), "dd/MM/yyyy"),
                rentalAgreementObj.getRentalAgreementId()));

        // Retrieve and display tenant information
        Tenant tenantObj = tenantController.getTenantByAccountId(payment.getTenantId());
        tenant.setText(String.format("%s (ID: %d)", tenantObj.getFullName(), tenantObj.getAccountId()));
    }

    // Sets up return button action to navigate to ManagerAllPaymentView
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        returnButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerAllPaymentView.fxml"));
                Parent root = loader.load();
                ManagerAllPaymentView view = loader.getController();
                view.showAllPayments(FXCollections.observableArrayList(paymentController.getAllPayments()));

                // Sets the main layout with the newly loaded view
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
