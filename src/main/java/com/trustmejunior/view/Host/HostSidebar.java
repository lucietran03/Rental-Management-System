package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
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

public class HostSidebar {
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

    private Account account = SessionManager.getCurrentAccount();

    private HostController hostController = new HostController();
    private PaymentController paymentController = new PaymentController();
    private PropertyController propertyController = new PropertyController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();

    @FXML
    public void viewProfile() {
        try {
            // Load the HostProfileView and initialize data
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/HostProfileView.fxml"));
            Parent root = loader.load();
            HostProfileView view = loader.getController();
            Host host = hostController.getHostByAccountId(account.getAccountId());
            view.initData(host);

            // Set the main layout to HostProfileView
            Scene currentScene = profileSection.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Handle any loading errors
            e.printStackTrace();
        }
    }

    @FXML
    public void viewDashboard() {
        try {
            // Load the HostView and initialize data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/HostView.fxml"));
            Parent root = loader.load();
            HostView view = loader.getController();
            view.initData();

            // Set the main layout to HostView
            Scene currentScene = dashboardButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Handle any loading errors
            e.printStackTrace();
        }
    }

    @FXML
    public void viewAllProperties() {
        try {
            // Load the HostAllPropertyView
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/HostAllPropertyView.fxml"));
            Parent root = loader.load();
            HostAllPropertyView view = loader.getController();

            // Load properties in a background task
            Task<List<Property>> loadPropertyTask = new Task<>() {
                @Override
                protected List<Property> call() throws Exception {
                    return propertyController.getPropertiesByHostId(account.getAccountId());
                }
            };

            // On success, show properties in the HostAllPropertyView
            loadPropertyTask.setOnSucceeded(workerStateEvent -> {
                view.showAllProperties(FXCollections.observableArrayList(loadPropertyTask.getValue()));
                Scene currentScene = propertiesButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            // On failure, show error message
            loadPropertyTask.setOnFailed(workerStateEvent -> {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load properties");
            });

            // Start the task in a new thread
            new Thread(loadPropertyTask).start();

        } catch (IOException e) {
            // Handle any loading errors
            e.printStackTrace();
        }
    }

    @FXML
    public void viewAllRentalAgreements() {
        try {
            // Load the HostAllRAView FXML file
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/HostAllRAView.fxml"));
            Parent root = loader.load();
            HostAllRAView view = loader.getController();

            // Task to load rental agreements by host ID
            Task<List<RentalAgreement>> loadRentalAgreementTask = new Task<>() {
                @Override
                protected List<RentalAgreement> call() throws Exception {
                    return rentalAgreementController.getRentalAgreementsByHostId(account.getAccountId());
                }
            };

            // On success, display rental agreements and update scene
            loadRentalAgreementTask.setOnSucceeded(workerStateEvent -> {
                view.showAllRa(FXCollections.observableArrayList(loadRentalAgreementTask.getValue()));
                Scene currentScene = rentalAgreementsButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            // On failure, show error alert
            loadRentalAgreementTask.setOnFailed(workerStateEvent -> {
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
            // Load the HostAllPaymentView FXML file
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/HostAllPaymentView.fxml"));
            Parent root = loader.load();
            HostAllPaymentView view = loader.getController();

            // Task to load payments by host ID through rental agreements
            Task<List<Payment>> loadPaymentTask = new Task<>() {
                @Override
                protected List<Payment> call() throws Exception {
                    List<RentalAgreement> rentalAgreements = rentalAgreementController
                            .getRentalAgreementsByHostId(account.getAccountId());

                    List<Payment> payments = new ArrayList<>();
                    for (RentalAgreement rentalAgreement : rentalAgreements) {
                        payments.addAll(paymentController
                                .getPaymentsByRentalAgreementId(rentalAgreement.getRentalAgreementId()));
                    }

                    return payments;
                }
            };

            // On success, display payments and update scene
            loadPaymentTask.setOnSucceeded(workerStateEvent -> {
                view.showAllPayments(FXCollections.observableArrayList(loadPaymentTask.getValue()));
                Scene currentScene = paymentsButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            // On failure, show error alert
            loadPaymentTask.setOnFailed(workerStateEvent -> {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load payments");
            });

            // Start the task in a new thread
            new Thread(loadPaymentTask).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Set username and email if account is not null
        if (account != null) {
            username.setText(account.getUsername());
            email.setText(account.getEmail());
        }

        // Set action for the sign-out button
        signOutButton.setOnAction(event -> {
            try {
                // Clear the session
                SessionManager.clearSession();

                // Load the login view
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
                // Show error message if sign-out fails
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "An error occurred while signing out");
            }
        });
    }
}
