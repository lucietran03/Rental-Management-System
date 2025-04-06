package com.trustmejunior.view.Manager;

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

public class ManagerEditTenantView implements Initializable {
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

    private Tenant tenant;

    private AccountController accountController = new AccountController();

    // Initializes tenant data into the form fields
    public void initData(Tenant tenant) {
        txtfieldUsername.setText(tenant.getUsername());
        txtfieldPassword.setText(tenant.getPassword());
        txtfieldFullname.setText(tenant.getFullName());
        txtfieldEmail.setText(tenant.getEmail());

        // Converts Date of Birth to LocalDate for datepicker
        Date date = tenant.getDob();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        datepickerDob.setValue(localDate);

        this.tenant = tenant;
    }

    // Handles return action to home page
    @FXML
    public void returnHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllAccountView.fxml"));
            Parent root = loader.load();
            ManagerAllAccountView view = loader.getController();
            view.showAllAccounts(FXCollections.observableArrayList(accountController.getAllAccounts()));

            Scene currentScene = buttonReturnHome.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles tenant update action after form validation
    @FXML
    public void editTenant(ActionEvent event) {
        try {
            String email = txtfieldEmail.getText();
            LocalDate localDob = datepickerDob.getValue();
            String password = txtfieldPassword.getText();
            String username = txtfieldUsername.getText();
            String fullname = txtfieldFullname.getText();

            // Validates if all fields are filled
            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty() || localDob == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please enter all details");
                return;
            }

            // Converts LocalDate to Date for the database
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Sends update request to the tenant controller
            UpdateTenantRequest request = new UpdateTenantRequest(username, password, fullname, email, dateOfBirth);
            TenantController tenantController = new TenantController();
            tenantController.updateTenant(tenant.getAccountId(), request);
            CustomAlert.show(Alert.AlertType.CONFIRMATION, "Tenant Updated", "Tenant updated successfully");

            returnHome(event);
        } catch (Exception e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    // Initializes the controller, no actions on initialize
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
