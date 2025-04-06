package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.request.UpdateTenantRequest;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

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

public class TenantEditProfileView implements Initializable {
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
    private Button editProfileButton;

    @FXML
    private Button returnButton;

    private Account account = SessionManager.getCurrentAccount();
    private Tenant tenant;

    private AccountController accountController = new AccountController();
    private TenantController tenantController = new TenantController();

    public void initData(Tenant tenant) {
        // Initialize text fields with tenant's details
        txtfieldUsername.setText(tenant.getUsername());
        txtfieldPassword.setText(tenant.getPassword());
        txtfieldFullname.setText(tenant.getFullName());
        txtfieldEmail.setText(tenant.getEmail());

        // Convert tenant's date of birth to LocalDate and set it in the date picker
        Date date = tenant.getDob();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        datepickerDob.setValue(localDate);

        this.tenant = tenant;
    }

    @FXML
    public void returnHome(ActionEvent event) {
        try {
            // Load and display the TenantProfileView
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantProfileView.fxml"));
            Parent root = loader.load();
            TenantProfileView view = loader.getController();

            // Fetch and pass tenant data to the view
            Tenant tenant = tenantController.getTenantByAccountId(account.getAccountId());
            view.initData(tenant);

            // Change the layout to TenantProfileView
            Scene currentScene = returnButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void editProfile(ActionEvent event) {
        try {
            // Get updated details from input fields
            String email = txtfieldEmail.getText();
            LocalDate localDob = datepickerDob.getValue();
            String password = txtfieldPassword.getText();
            String username = txtfieldUsername.getText();
            String fullname = txtfieldFullname.getText();

            // Validate that all fields are filled
            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty() || localDob == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please enter all details");
                return;
            }

            // Convert LocalDate to Date for the date of birth
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Create request object to update tenant
            UpdateTenantRequest request = new UpdateTenantRequest(username, password, fullname, email, dateOfBirth);
            tenantController.updateTenant(tenant.getAccountId(), request);

            // Update current session account information
            Account newAccount = accountController.getAccountById(account.getAccountId());
            SessionManager.setCurrentAccount(newAccount);

            // Show success message
            CustomAlert.show(Alert.AlertType.CONFIRMATION, "Tenant Updated", "Tenant updated successfully");

            // Return to home view after update
            returnHome(event);
        } catch (Exception e) {
            // Show error message in case of an exception
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Method to initialize components (currently not used)
    }
}
