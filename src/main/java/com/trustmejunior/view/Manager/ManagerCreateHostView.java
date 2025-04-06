package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.request.CreateHostRequest;
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

public class ManagerCreateHostView implements Initializable {
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

            // Set the scene to show the ManagerAllAccountView
            Scene currentScene = buttonReturnHome.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);

        } catch (IOException e) {
            e.printStackTrace(); // Handle any loading errors
        }
    }

    @FXML
    public void createHost(ActionEvent event) {
        try {
            // Retrieve input values for host creation
            String email = txtfieldEmail.getText();
            LocalDate localDob = datepickerDob.getValue();
            String password = txtfieldPassword.getText();
            String username = txtfieldUsername.getText();
            String fullname = txtfieldFullname.getText();

            // Validate input fields
            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty() || localDob == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please enter all details");
                return;
            }

            // Convert LocalDate to Date for DOB
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Create a request object and call the controller to create the host
            CreateHostRequest request = new CreateHostRequest(username, password, fullname, email, dateOfBirth);
            HostController hostController = new HostController();
            hostController.createHost(request);

            // Show confirmation alert and navigate back to home
            CustomAlert.show(Alert.AlertType.CONFIRMATION, "Host Created", "Host created successfully");
            returnHome(event);

        } catch (Exception e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage()); // Show error if any exception occurs
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Empty initialize method (can be used for future setup if needed)
    }
}
