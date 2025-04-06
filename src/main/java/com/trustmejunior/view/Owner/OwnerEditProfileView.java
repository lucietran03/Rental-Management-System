package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Owner;
import com.trustmejunior.request.UpdateOwnerRequest;
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

public class OwnerEditProfileView implements Initializable {
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
    private Button editProfileButton;

    @FXML
    private Button buttonViewDetail;

    @FXML
    private Button returnButton;

    private Account account = SessionManager.getCurrentAccount();
    private Owner owner;

    private AccountController accountController = new AccountController();
    private OwnerController ownerController = new OwnerController();

    public void initData(Owner owner) {
        // Populate fields with owner data
        txtfieldUsername.setText(owner.getUsername());
        txtfieldPassword.setText(owner.getPassword());
        txtfieldFullname.setText(owner.getFullName());
        txtfieldEmail.setText(owner.getEmail());

        // Convert DOB to LocalDate for datepicker
        Date date = owner.getDob();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        datepickerDob.setValue(localDate);

        this.owner = owner; // Store the owner object
    }

    @FXML
    public void returnHome(ActionEvent event) {
        try {
            // Load the OwnerProfileView
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerProfileView.fxml"));
            Parent root = loader.load();
            OwnerProfileView view = loader.getController();

            // Retrieve and pass the owner data to the view
            Owner owner = ownerController.getOwnerByAccountId(account.getAccountId());
            view.initData(owner);

            // Update the main layout with the new view
            Scene currentScene = returnButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);

        } catch (IOException e) {
            e.printStackTrace(); // Handle potential IO errors
        }
    }

    @FXML
    public void editProfile(ActionEvent event) {
        try {
            // Retrieve data from form fields
            String email = txtfieldEmail.getText();
            LocalDate localDob = datepickerDob.getValue();
            String password = txtfieldPassword.getText();
            String username = txtfieldUsername.getText();
            String fullname = txtfieldFullname.getText();

            // Check for empty fields and show an error if any
            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty() || localDob == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please enter all details");
                return;
            }

            // Convert LocalDate to Date for backend processing
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Create update request for owner profile
            UpdateOwnerRequest updateOwnerRequest = new UpdateOwnerRequest(username, password, fullname, email,
                    dateOfBirth);

            // Send update request
            ownerController.updateOwner(owner.getAccountId(), updateOwnerRequest);

            // Refresh session with updated account information
            Account newAccount = accountController.getAccountById(account.getAccountId());
            SessionManager.setCurrentAccount(newAccount);

            // Show success message
            CustomAlert.show(Alert.AlertType.INFORMATION, "Owner Updated", "Owner updated successfully");

            // Return to home view
            returnHome(event);
        } catch (Exception e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage()); // Show error if update fails
            e.printStackTrace(); // Log the exception for debugging
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize any additional components (if required)
    }
}
