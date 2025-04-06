package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.request.CreateTenantRequest;
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

public class ManagerCreateTenantView implements Initializable {
    @FXML
    private TextField txtfieldEmail;

    @FXML
    private DatePicker datepickerDob;

    @FXML
    private TextField txtfieldFullname;

    @FXML
    private TextField txtfieldPassword;

    @FXML
    private TextField txtfieldUsername;

    @FXML
    private Button buttonReturnHome;

    private Account account = SessionManager.getCurrentAccount();

    private AccountController accountController = new AccountController();

    public void initData() {
    }

    @FXML
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
    public void createTenant(ActionEvent event) {
        try {
            // Retrieve tenant details from input fields
            String email = txtfieldEmail.getText();
            LocalDate localDob = datepickerDob.getValue();
            String password = txtfieldPassword.getText();
            String username = txtfieldUsername.getText();
            String fullname = txtfieldFullname.getText();

            // Check if any required field is empty
            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty() || localDob == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please enter all details");
                return; // Exit if validation fails
            }

            // Convert LocalDate to Date for the date of birth
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Create a request to create a new tenant
            CreateTenantRequest request = new CreateTenantRequest(username, password, fullname, email, dateOfBirth);
            TenantController tenantController = new TenantController();
            tenantController.createTenant(request); // Create the tenant

            // Show confirmation message and return to the home view
            CustomAlert.show(Alert.AlertType.CONFIRMATION, "Tenant Created", "Tenant created successfully");
            returnHome(event);
        } catch (Exception e) {
            // Show error message if any exception occurs
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization code (currently empty)
    }
}
