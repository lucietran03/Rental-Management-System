package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.*;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.Notification.Notification;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.DateFormatter;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TenantNotificationView implements Initializable {
    @FXML
    private Label amount;

    @FXML
    private Label endDate;

    @FXML
    private Label hostId;

    @FXML
    private Label paymentId;

    @FXML
    private Label period;

    @FXML
    private Label rentalAgreementId;

    @FXML
    private Button returnButton;

    @FXML
    private Label startDate;

    @FXML
    private Label status;

    @FXML
    private Button payButton;

    private Account account = SessionManager.getCurrentAccount();
    private HostController hostController = new HostController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private PaymentController paymentController = new PaymentController();
    private NotificationController notificationController = new NotificationController();
    private int notificationId;

    @FXML
    void returnShowAllNotificationPage() {
        try {
            // Load and display the TenantAllNotificationView scene
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantAllNotificationView.fxml"));
            Parent root = loader.load();
            TenantAllNotificationView view = loader.getController();
            // Show all notifications for the current user
            view.showAllNotifications(FXCollections
                    .observableArrayList(notificationController.getNotificationByReceiverId(account.getAccountId())));

            // Navigate to the notification view page
            Scene currentScene = returnButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void directToCreatePaymentPage() {
        try {
            // Get the notification and related payment details
            Notification notification = notificationController.getNotificationById(notificationId);
            Payment payment = paymentController.getPaymentById(notification.getPaymentId());

            // Check if the payment is already made
            if (payment != null && (payment.getStatus() == PaymentStatus.PAID || payment.getStatus() == PaymentStatus.LATE)) {
                CustomAlert.show(Alert.AlertType.INFORMATION, "Payment Already Made",
                        "This payment has already been paid. Please return to the previous page.");
                return;
            }

            // Load the payment creation page
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantCreatePaymentView.fxml"));
            Parent root = loader.load();
            TenantCreatePaymentView view = loader.getController();
            view.initData(notificationId);

            // Navigate to the payment creation page
            Scene currentScene = payButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initData(Notification notification) {
        // Initialize data for the notification and payment details
        this.notificationId = notification.getNotificationId();

        // Set basic notification and payment information
        Payment payment = paymentController.getPaymentById(notification.getPaymentId());
        paymentId.setText(String.valueOf(payment.getPaymentId()));

        // Get host details from senderId
        Host host = hostController.getHostByAccountId(notification.getSenderId());
        hostId.setText(String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()));

        // Get rental agreement details from the payment
        RentalAgreement rentalAgreement = rentalAgreementController
                .getRentalAgreementById(payment.getRentalAgreementId());
        rentalAgreementId.setText(String.valueOf(rentalAgreement.getRentalAgreementId()));
        amount.setText(String.format("$%.2f", rentalAgreement.getFee()));
        startDate.setText(DateFormatter.formatDate(rentalAgreement.getStartDate(), "dd/MM/yyyy"));
        endDate.setText(DateFormatter.formatDate(rentalAgreement.getEndDate(), "dd/MM/yyyy"));
        period.setText(rentalAgreement.getPeriod().toString());
        status.setText(rentalAgreement.getStatus().toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize method for the controller, can be used for setup if needed
    }
}
