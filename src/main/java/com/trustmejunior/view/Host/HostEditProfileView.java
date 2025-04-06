package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.request.UpdateHostRequest;
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

public class HostEditProfileView implements Initializable {
    @FXML
    private DatePicker datepickerDob;

    @FXML
    private TextField txtfieldEmail;

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
    private Host host;

    private AccountController accountController = new AccountController();
    private HostController hostController = new HostController();

    public void initData(Host host) {
        // Initialize input fields with the host's details
        txtfieldUsername.setText(host.getUsername());
        txtfieldPassword.setText(host.getPassword());
        txtfieldFullname.setText(host.getFullName());
        txtfieldEmail.setText(host.getEmail());

        // Convert Date of Birth from Date to LocalDate for the DatePicker
        Date date = host.getDob();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        datepickerDob.setValue(localDate);

        // Store the host object for further use
        this.host = host;
    }

    @FXML
    public void returnHome(ActionEvent event) {
        try {
            // Load the HostProfileView and set the current host's data
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/HostProfileView.fxml"));
            Parent root = loader.load();
            HostProfileView view = loader.getController();
            Host host = hostController.getHostByAccountId(account.getAccountId());
            view.initData(host);

            // Replace the current scene with the HostProfileView
            Scene currentScene = returnButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);

        } catch (IOException e) {
            e.printStackTrace(); // Log exception if loading view fails
        }
    }

    @FXML
    public void editProfile(ActionEvent event) {
        try {
            // Get input values from the form fields
            String email = txtfieldEmail.getText();
            LocalDate localDob = datepickerDob.getValue();
            String password = txtfieldPassword.getText();
            String username = txtfieldUsername.getText();
            String fullname = txtfieldFullname.getText();

            // Check if any required field is empty and show an error alert
            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty() || localDob == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please enter all details");
                return;
            }

            // Convert LocalDate to Date format for the request
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Create request to update host details
            UpdateHostRequest request = new UpdateHostRequest(username, password, fullname, email, dateOfBirth);
            hostController.updateHost(host.getAccountId(), request);

            // Update the current account in session and show confirmation alert
            Account newAccount = accountController.getAccountById(account.getAccountId());
            SessionManager.setCurrentAccount(newAccount);
            CustomAlert.show(Alert.AlertType.CONFIRMATION, "Host Updated", "Host updated successfully");

            // Return to the home view after successful update
            returnHome(event);
        } catch (Exception e) {
            // Show error alert if something goes wrong during the update
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization logic (currently empty)
    }
}
