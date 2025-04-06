package com.trustmejunior.view.Tenant;

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
import java.util.List;
import java.util.ResourceBundle;

public class TenantPaymentView implements Initializable {
    @FXML
    private Label amount;

    @FXML
    private Label dueDate;

    @FXML
    private Label method;

    @FXML
    private Label status;

    @FXML
    private Label rentalAgreement;

    @FXML
    private Label tenant;

    @FXML
    private Label paymentDate;

    @FXML
    private Button returnButton;

    private Account account = SessionManager.getCurrentAccount();

    private PaymentController paymentController = new PaymentController();
    private PropertyController propertyController = new PropertyController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private TenantController tenantController = new TenantController();

    public void initData(Payment payment) {
        // Display payment details such as amount, due date, payment date, method, and
        // status
        amount.setText(String.format("$%.2f", payment.getAmount()));
        dueDate.setText(DateFormatter.formatDate(payment.getDueDate(), "dd/MM/yyyy"));
        paymentDate.setText(payment.getPaidDate() != null
                ? DateFormatter.formatDate(payment.getPaidDate(), "dd/MM/yyyy")
                : "N/A");
        method.setText(payment.getMethod() != null
                ? payment.getMethod().toString()
                : "N/A");
        status.setText(payment.getStatus().toString());

        // Fetch rental agreement details and display them with formatted dates
        RentalAgreement rentalAgreementObj = rentalAgreementController
                .getRentalAgreementById(payment.getRentalAgreementId());
        Property propertyObj = propertyController.getPropertyById(rentalAgreementObj.getPropertyId());
        rentalAgreement.setText(String.format("%s - %s to %s (ID: %d)", propertyObj.getAddress(),
                DateFormatter.formatDate(rentalAgreementObj.getStartDate(), "dd/MM/yyyy"),
                DateFormatter.formatDate(rentalAgreementObj.getEndDate(), "dd/MM/yyyy"),
                rentalAgreementObj.getRentalAgreementId()));

        // Display tenant information (full name and ID)
        Tenant tenantObj = tenantController.getTenantByAccountId(payment.getTenantId());
        tenant.setText(String.format("%s (ID: %d)", tenantObj.getFullName(), tenantObj.getAccountId()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set action for return button to navigate back to the previous view
        returnButton.setOnAction(event -> {
            try {
                // Load the TenantAllPaymentView and pass the payment data
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/TenantAllPaymentView.fxml"));
                Parent root = loader.load();
                TenantAllPaymentView view = loader.getController();

                // Fetch and display all payments associated with the tenant
                List<Payment> payments = paymentController.getPaymentsByTenantId(account.getAccountId());
                view.showAllPayments(FXCollections.observableArrayList(payments));

                // Switch to the new view
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
