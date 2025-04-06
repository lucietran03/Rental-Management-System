package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.ManagerController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Manager;
import com.trustmejunior.request.UpdateManagerRequest;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class ManagerEditManagerView implements Initializable {
    @FXML
    private DatePicker datepickerDob;

    @FXML
    private TextField txtfieldFullname;

    @FXML
    private TextField txtfieldEmail;

    @FXML
    private TextField txtfieldPassword;

    @FXML
    private TextField txtfieldUsername;

    @FXML
    private Button buttonReturnHome;

    private Account account = SessionManager.getCurrentAccount();
    private Manager manager;

    private AccountController accountController = new AccountController();

    // Initialize fields with data from the manager object
    public void initData(Manager manager) {
        txtfieldUsername.setText(manager.getUsername());
        txtfieldPassword.setText(manager.getPassword());
        txtfieldFullname.setText(manager.getFullName());
        txtfieldEmail.setText(manager.getEmail());

        // Convert Date to LocalDate for the datepicker
        Date date = manager.getDob();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        datepickerDob.setValue(localDate);

        // Store the manager object for later use
        this.manager = manager;
    }

    @FXML
    // Return to the home view showing all accounts
    public void returnHome(ActionEvent event) {
        try {
            // Load the ManagerAllAccountView and display all accounts
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllAccountView.fxml"));
            Parent root = loader.load();
            ManagerAllAccountView view = loader.getController();
            view.showAllAccounts(FXCollections.observableArrayList(accountController.getAllAccounts()));

            // Set the scene to display the ManagerAllAccountView
            Scene currentScene = buttonReturnHome.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle any loading errors
        }
    }

    @FXML
    // Handle the action to edit manager details
    public void editManager(ActionEvent event) {
        try {
            // Get input values from the fields
            String email = txtfieldEmail.getText();
            LocalDate localDob = datepickerDob.getValue();
            String password = txtfieldPassword.getText();
            String username = txtfieldUsername.getText();
            String fullname = txtfieldFullname.getText();

            // Check if all required fields are filled
            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty() || localDob == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please enter all details");
                return;
            }

            // Convert LocalDate to Date for the update request
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Create update request and update manager information
            UpdateManagerRequest updateManagerRequest = new UpdateManagerRequest(username, password, fullname, email,
                    dateOfBirth);
            ManagerController managerController = new ManagerController();
            managerController.updateManager(manager.getAccountId(), updateManagerRequest);

            // Show success message
            CustomAlert.show(Alert.AlertType.INFORMATION, "Manager Updated", "Manager updated successfully");

            // Return to the home view
            returnHome(event);
        } catch (Exception e) {
            // Show error message if there's an exception
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @Override
    // Initialize method (not used in this implementation)
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
