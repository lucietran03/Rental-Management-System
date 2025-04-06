package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Owner;
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

public class OwnerSidebar {
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
    private Button signOutButton;

    private Account account = SessionManager.getCurrentAccount();

    private OwnerController ownerController = new OwnerController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private PropertyController propertyController = new PropertyController();

    @FXML
    public void viewProfile() {
        try {
            // Load OwnerProfileView and pass current owner data to it
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerProfileView.fxml"));
            Parent root = loader.load();
            OwnerProfileView view = loader.getController();
            Owner owner = ownerController.getOwnerByAccountId(account.getAccountId());
            view.initData(owner);

            // Set new view as main layout
            Scene currentScene = profileSection.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle potential loading errors
        }
    }

    @FXML
    public void viewDashboard(ActionEvent event) {
        try {
            // Load OwnerView and initialize it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/OwnerView.fxml"));
            Parent root = loader.load();
            OwnerView view = loader.getController();
            view.initData(); // Initialize data for OwnerView

            // Set new view as main layout
            Scene currentScene = dashboardButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle potential loading errors
        }
    }

    @FXML
    public void viewAllProperties(ActionEvent event) {
        try {
            // Load OwnerAllPropertyView and fetch properties by owner ID
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerAllPropertyView.fxml"));
            Parent root = loader.load();
            OwnerAllPropertyView view = loader.getController();

            // Task to load properties associated with the owner
            Task<List<Property>> loadRaTask = new Task<>() {
                @Override
                protected List<Property> call() throws Exception {
                    return propertyController.getPropertiesByOwnerId(account.getAccountId());
                }
            };

            loadRaTask.setOnSucceeded(workerStateEvent -> {
                // Display properties when task is successful
                view.showAllProperties(FXCollections.observableArrayList(loadRaTask.getValue()));
                Scene currentScene = propertiesButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            loadRaTask.setOnFailed(workerStateEvent -> {
                // Show error alert if task fails
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to load rental agreements");
                alert.setContentText("Please try again later.");
                alert.showAndWait();
            });

            // Start the task in a new thread
            new Thread(loadRaTask).start();
        } catch (IOException e) {
            throw new RuntimeException(e); // Handle any loading issues
        }
    }

    @FXML
    public void viewAllRentalAgreements(ActionEvent event) {
        try {
            // Load the "OwnerAllRAView" for displaying rental agreements
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerAllRAView.fxml"));
            Parent root = loader.load();
            OwnerAllRAView view = loader.getController();
            view.initData(); // Initialize the data for the view

            // Create a background task to fetch rental agreements
            Task<List<RentalAgreement>> loadRaTask = new Task<>() {
                @Override
                protected List<RentalAgreement> call() throws Exception {
                    return rentalAgreementController.getRentalAgreementsByOwnerId(account.getAccountId());
                }
            };

            // On successful data load, display the rental agreements
            loadRaTask.setOnSucceeded(workerStateEvent -> {
                view.showAllRa(FXCollections.observableArrayList(loadRaTask.getValue())); // Show rental agreements
                Scene currentScene = rentalAgreementsButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root); // Update the current layout with the new view
            });

            // If the task fails, show an error alert
            loadRaTask.setOnFailed(workerStateEvent -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to load rental agreements");
                alert.setContentText("Please try again later.");
                alert.showAndWait();
            });

            // Start the background task
            new Thread(loadRaTask).start();
        } catch (IOException e) {
            // Handle any IO exceptions that occur
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        // Initialize the user information if available
        if (account != null) {
            username.setText(account.getUsername());
            email.setText(account.getEmail());
        }

        // Set up the sign-out button action
        signOutButton.setOnAction(event -> {
            try {
                // Clear the session data
                SessionManager.clearSession();

                // Load the login view after signing out
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/LoginView.fxml"));
                Parent root = loader.load();

                // Get the current stage and switch to the login scene
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                // Show a success message after signing out
                CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "You have been signed out successfully");
            } catch (IOException e) {
                // Handle any IO exceptions during sign-out
                e.printStackTrace();
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "An error occurred while signing out");
            }
        });
    }
}