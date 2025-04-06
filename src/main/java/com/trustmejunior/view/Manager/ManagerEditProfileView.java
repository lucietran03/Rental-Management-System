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

public class ManagerEditProfileView implements Initializable {
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
    private Button returnButton;

    private Account account = SessionManager.getCurrentAccount();
    private Manager manager;

    private AccountController accountController = new AccountController();
    private ManagerController managerController = new ManagerController();

    public void initData(Manager manager) {
        // Initialize fields with manager data
        txtfieldUsername.setText(manager.getUsername());
        txtfieldPassword.setText(manager.getPassword());
        txtfieldFullname.setText(manager.getFullName());
        txtfieldEmail.setText(manager.getEmail());

        // Convert date of birth to LocalDate and set the date picker value
        Date date = manager.getDob();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        datepickerDob.setValue(localDate);

        this.manager = manager; // Store manager object for further use
    }

    @FXML
    public void returnHome(ActionEvent event) {
        try {
            // Load ManagerProfileView and set it as the main view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerProfileView.fxml"));
            Parent root = loader.load();
            ManagerProfileView view = loader.getController();

            // Fetch manager details by account ID and initialize the view with data
            Manager manager = managerController.getManagerByAccountId(account.getAccountId());
            view.initData(manager);

            // Set the newly loaded view in the current scene
            Scene currentScene = returnButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);

        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exception
        }
    }

    @FXML
    public void editProfile(ActionEvent event) {
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

            // Prepare update request and send it to ManagerController
            UpdateManagerRequest updateManagerRequest = new UpdateManagerRequest(username, password, fullname, email,
                    dateOfBirth);
            managerController.updateManager(manager.getAccountId(), updateManagerRequest);

            // Fetch the updated account and store it in the session
            Account newAccount = accountController.getAccountById(account.getAccountId());
            SessionManager.setCurrentAccount(newAccount);

            // Show confirmation message and return to home
            CustomAlert.show(Alert.AlertType.INFORMATION, "Manager Updated", "Manager updated successfully");
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
