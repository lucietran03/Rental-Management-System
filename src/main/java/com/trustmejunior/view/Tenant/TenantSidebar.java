package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.*;
import com.trustmejunior.model.Notification.Notification;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TenantSidebar {
    @FXML
    private HBox profileSection;

    @FXML
    private Label username;

    @FXML
    private Label email;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button propertiesButton;

    @FXML
    private Button rentalAgreementsButton;

    @FXML
    private Button paymentsButton;

    @FXML
    private Button signOutButton;

    @FXML
    private Button notificationButton;

    private Account account = SessionManager.getCurrentAccount();

    private PropertyController propertyController = new PropertyController();
    private TenantController tenantController = new TenantController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private PaymentController paymentController = new PaymentController();
    private NotificationController notificationController = new NotificationController();

    @FXML
    public void viewProfile() {
        try {
            // Load the Tenant Profile view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantProfileView.fxml"));
            Parent root = loader.load();
            TenantProfileView view = loader.getController();

            // Initialize profile data with tenant info
            Tenant tenant = tenantController.getTenantByAccountId(account.getAccountId());
            view.initData(tenant);

            // Set the loaded view as the main content
            Scene currentScene = profileSection.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewDashboard() {
        try {
            // Load the Tenant Dashboard view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/TenantView.fxml"));
            Parent root = loader.load();
            TenantView view = loader.getController();
            view.initData();

            // Set the loaded view as the main content
            Scene currentScene = dashboardButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewAllProperties() {
        try {
            // Load the Tenant All Properties view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantAllPropertyView.fxml"));
            Parent root = loader.load();
            TenantAllPropertyView view = loader.getController();

            // Load properties associated with the tenant
            Task<List<Property>> loadPropertyTask = new Task<>() {
                @Override
                protected List<Property> call() throws Exception {
                    List<RentalAgreement> rentalAgreements = rentalAgreementController
                            .getRentalAgreementsByMainTenantId(account.getAccountId());
                    rentalAgreements.addAll(rentalAgreementController.getRentalAgreementsBySubTenantId(account.getAccountId()));

                    List<Property> properties = new ArrayList<>();
                    for (RentalAgreement rentalAgreement : rentalAgreements) {
                        properties.add(propertyController.getPropertyById(rentalAgreement.getPropertyId()));
                    }

                    return properties;
                }
            };

            loadPropertyTask.setOnSucceeded(workerStateEvent -> {
                // Display loaded properties in the view
                view.showAllProperties(FXCollections.observableArrayList(loadPropertyTask.getValue()));
                // Set the loaded view as the main content
                Scene currentScene = propertiesButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            loadPropertyTask.setOnFailed(workerStateEvent -> {
                // Show an error alert if properties loading fails
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load properties");
            });

            // Start the task in a new thread
            new Thread(loadPropertyTask).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewAllRentalAgreements() {
        try {
            // Load the Tenant All Rental Agreements view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantAllRAView.fxml"));
            Parent root = loader.load();
            TenantAllRAView view = loader.getController();

            // Load rental agreements associated with the tenant
            Task<List<RentalAgreement>> loadRentalAgreementTask = new Task<>() {
                @Override
                protected List<RentalAgreement> call() throws Exception {
                    List<RentalAgreement> rentalAgreements = rentalAgreementController.getRentalAgreementsByMainTenantId(account.getAccountId());
                    rentalAgreements.addAll(rentalAgreementController.getRentalAgreementsBySubTenantId(account.getAccountId()));
                    return rentalAgreements;
                }
            };

            loadRentalAgreementTask.setOnSucceeded(workerStateEvent -> {
                // Display loaded rental agreements in the view
                view.showAllRa(FXCollections.observableArrayList(loadRentalAgreementTask.getValue()));
                // Set the loaded view as the main content
                Scene currentScene = rentalAgreementsButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            loadRentalAgreementTask.setOnFailed(workerStateEvent -> {
                // Show an error alert if rental agreements loading fails
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load rental agreements");
            });

            // Start the task in a new thread
            new Thread(loadRentalAgreementTask).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewAllPayments() {
        try {
            // Load the Tenant All Payments view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantAllPaymentView.fxml"));
            Parent root = loader.load();
            TenantAllPaymentView view = loader.getController();

            // Load payments associated with the tenant
            Task<List<Payment>> loadPaymentTask = new Task<>() {
                @Override
                protected List<Payment> call() throws Exception {
                    return paymentController.getPaymentsByTenantId(account.getAccountId());
                }
            };

            loadPaymentTask.setOnSucceeded(workerStateEvent -> {
                // Display loaded payments in the view
                view.showAllPayments(FXCollections.observableArrayList(loadPaymentTask.getValue()));
                // Set the loaded view as the main content
                Scene currentScene = paymentsButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            loadPaymentTask.setOnFailed(workerStateEvent -> {
                // Show an error alert if payments loading fails
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load payments");
            });

            // Start the task in a new thread
            new Thread(loadPaymentTask).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void viewAllNotification() {
        try {
            // Load the Tenant All Notifications view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantAllNotificationView.fxml"));
            Parent root = loader.load();
            TenantAllNotificationView view = loader.getController();

            // Load notifications associated with the tenant
            Task<List<Notification>> loadNotificationTask = new Task<>() {
                @Override
                protected List<Notification> call() throws Exception {
                    return notificationController.getNotificationByReceiverId(account.getAccountId());
                }
            };

            loadNotificationTask.setOnSucceeded(workerStateEvent -> {
                // Display loaded notifications in the view
                view.showAllNotifications(FXCollections.observableArrayList(loadNotificationTask.getValue()));
                // Set the loaded view as the main content
                Scene currentScene = paymentsButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            loadNotificationTask.setOnFailed(workerStateEvent -> {
                // Show an error alert if notifications loading fails
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load notifications");
            });

            // Start the task in a new thread
            new Thread(loadNotificationTask).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Set account info if available
        if (account != null) {
            username.setText(account.getUsername());
            email.setText(account.getEmail());
        }

        // Handle sign out action
        signOutButton.setOnAction(event -> {
            try {
                // Clear the session and load the login view
                SessionManager.clearSession();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/LoginView.fxml"));
                Parent root = loader.load();

                // Get the current stage and set the new scene
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                // Show success message
                CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "You have been signed out successfully");
            } catch (IOException e) {
                e.printStackTrace();
                // Show error message if sign out fails
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "An error occurred while signing out");
            }
        });
    }
}
