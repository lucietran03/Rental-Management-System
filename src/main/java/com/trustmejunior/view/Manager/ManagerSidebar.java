package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.ManagerController;
import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Manager;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import java.util.List;

public class ManagerSidebar {
    @FXML
    private HBox profileSection;

    @FXML
    private Label username;

    @FXML
    private Label email;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button propertiesButton;

    @FXML
    private Button rentalAgreementsButton;

    @FXML
    private Button paymentsButton;

    @FXML
    private Button signOutButton;

    private Account account = SessionManager.getCurrentAccount();

    private AccountController accountController = new AccountController();
    private PropertyController propertyController = new PropertyController();
    private PaymentController paymentController = new PaymentController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private ManagerController managerController = new ManagerController();

    @FXML
    public void viewProfile() {
        try {
            // Load and display the manager's profile view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerProfileView.fxml"));
            Parent root = loader.load();
            ManagerProfileView view = loader.getController();
            Manager manager = managerController.getManagerByAccountId(account.getAccountId());
            view.initData(manager);

            // Update the scene to display the manager's profile
            Scene currentScene = profileSection.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle loading errors
        }
    }

    @FXML
    public void viewDashboard(ActionEvent event) {
        try {
            // Load and display the manager dashboard view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/ManagerView.fxml"));
            Parent root = loader.load();
            ManagerView view = loader.getController();
            view.initData();

            // Update the scene to show the dashboard
            Scene currentScene = dashboardButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle loading errors
        }
    }

    @FXML
    public void viewAllAccounts(ActionEvent event) {
        try {
            // Load and display all accounts view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllAccountView.fxml"));
            Parent root = loader.load();
            ManagerAllAccountView view = loader.getController();

            Task<List<Account>> loadAccountTask = new Task<>() {
                @Override
                protected List<Account> call() throws Exception {
                    try {
                        return accountController.getAllAccounts();
                    } catch (Exception e) {
                        e.printStackTrace(); // Log exception chi tiết
                        throw new RuntimeException("Error loading accounts", e);
                    }
                }
            };

            // On successful account load, display the accounts
            loadAccountTask.setOnSucceeded(workerStateEvent -> {
                view.showAllAccounts(FXCollections.observableArrayList(loadAccountTask.getValue()));
                Scene currentScene = usersButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            // Show error message if loading accounts fails
            loadAccountTask.setOnFailed(workerStateEvent -> {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load accounts");
            });

            // Start the task in a new thread
            new Thread(loadAccountTask).start();
        } catch (IOException e) {
            throw new RuntimeException(e); // Handle loading errors
        }
    }

    @FXML
    public void viewAllProperties(ActionEvent event) {
        try {
            // Load and display all properties view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllPropertyView.fxml"));
            Parent root = loader.load();
            ManagerAllPropertyView view = loader.getController();

            // Load properties asynchronously
            Task<List<Property>> loadPropertiesTask = new Task<>() {
                @Override
                protected List<Property> call() throws Exception {
                    return propertyController.getAllProperties();
                }
            };

            // On successful property load, display the properties
            loadPropertiesTask.setOnSucceeded(workerStateEvent -> {
                view.showAllProperties(FXCollections.observableArrayList(loadPropertiesTask.getValue()));
                Scene currentScene = propertiesButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            // Show error message if loading properties fails
            loadPropertiesTask.setOnFailed(workerStateEvent -> {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load properties");
            });

            // Start the task in a new thread
            new Thread(loadPropertiesTask).start();
        } catch (IOException e) {
            throw new RuntimeException(e); // Handle loading errors
        }
    }

    // View all rental agreements and load them asynchronously
    @FXML
    public void viewAllRentalAgreements(ActionEvent event) {
        try {
            // Load the ManagerAllRAView
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllRAView.fxml"));
            Parent root = loader.load();
            ManagerAllRAView view = loader.getController();

            // Create a task to load rental agreements in the background
            Task<List<RentalAgreement>> loadRaTask = new Task<>() {
                @Override
                protected List<RentalAgreement> call() throws Exception {
                    return rentalAgreementController.getAllRentalAgreements();
                }
            };

            // Set what to do when the rental agreements are successfully loaded
            loadRaTask.setOnSucceeded(workerStateEvent -> {
                view.showAllRa(FXCollections.observableArrayList(loadRaTask.getValue()));
                Scene currentScene = rentalAgreementsButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            // Handle failure when loading rental agreements
            loadRaTask.setOnFailed(workerStateEvent -> {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load rental agreements");
            });

            // Start the background task
            new Thread(loadRaTask).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // View all payments and load them asynchronously
    @FXML
    public void viewAllPayments(ActionEvent event) {
        try {
            // Load the ManagerAllPaymentView
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllPaymentView.fxml"));
            Parent root = loader.load();
            ManagerAllPaymentView view = loader.getController();

            // Create a task to load payments in the background
            Task<List<Payment>> loadPaymentTask = new Task<>() {
                @Override
                protected List<Payment> call() throws Exception {
                    return paymentController.getAllPayments();
                }
            };

            // Set what to do when the payments are successfully loaded
            loadPaymentTask.setOnSucceeded(workerStateEvent -> {
                view.showAllPayments(FXCollections.observableArrayList(loadPaymentTask.getValue()));
                Scene currentScene = paymentsButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            // Handle failure when loading payments
            loadPaymentTask.setOnFailed(workerStateEvent -> {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load payments");
            });

            // Start the background task
            new Thread(loadPaymentTask).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Initialize the view, set username and email, and handle sign-out
    // functionality
    @FXML
    public void initialize() {
        if (account != null) {
            // Set account details
            username.setText(account.getUsername());
            email.setText(account.getEmail());
        }

        // Handle sign-out button click
        signOutButton.setOnAction(event -> {
            try {
                // Clear the session data
                SessionManager.clearSession();

                // Load the login view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/LoginView.fxml"));
                Parent root = loader.load();

                // Get the current stage and switch to the login scene
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
