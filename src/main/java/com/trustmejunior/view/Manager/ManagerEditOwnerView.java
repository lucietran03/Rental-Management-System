package com.trustmejunior.view.Manager;

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

public class ManagerEditOwnerView implements Initializable {
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
    private Owner owner;

    private AccountController accountController = new AccountController();

    public void initData(Owner owner) {
        // Initialize fields with owner data
        txtfieldUsername.setText(owner.getUsername());
        txtfieldPassword.setText(owner.getPassword());
        txtfieldFullname.setText(owner.getFullName());
        txtfieldEmail.setText(owner.getEmail());

        // Convert date of birth to LocalDate and set the date picker value
        Date date = owner.getDob();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        datepickerDob.setValue(localDate);

        this.owner = owner; // Store owner object for further use
    }

    @FXML
    public void returnHome(ActionEvent event) {
        try {
            // Load ManagerAllAccountView and set it as the main view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllAccountView.fxml"));
            Parent root = loader.load();
            ManagerAllAccountView view = loader.getController();
            view.showAllAccounts(FXCollections.observableArrayList(accountController.getAllAccounts()));

            // Set the newly loaded view in the current scene
            Scene currentScene = buttonReturnHome.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);

        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exception
        }
    }

    @FXML
    public void editOwner(ActionEvent event) {
        try {
            // Get input values from text fields and date picker
            String email = txtfieldEmail.getText();
            LocalDate localDob = datepickerDob.getValue();
            String password = txtfieldPassword.getText();
            String username = txtfieldUsername.getText();
            String fullname = txtfieldFullname.getText();

            // Validate inputs
            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty() || localDob == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please enter all details");
                return;
            }

            // Convert local date to Date for storage
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Prepare update request and send it to OwnerController
            UpdateOwnerRequest updateOwnerRequest = new UpdateOwnerRequest(username, password, fullname, email,
                    dateOfBirth);
            OwnerController ownerController = new OwnerController();
            ownerController.updateOwner(owner.getAccountId(), updateOwnerRequest);

            // Show confirmation message and return to home
            CustomAlert.show(Alert.AlertType.INFORMATION, "Owner Updated", "Owner updated successfully");
            returnHome(event);
        } catch (Exception e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage()); // Handle any errors
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize any necessary components (currently empty)
    }
}
