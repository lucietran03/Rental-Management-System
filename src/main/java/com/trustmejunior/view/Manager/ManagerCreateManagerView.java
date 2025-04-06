package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.ManagerController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.request.CreateManagerRequest;
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

public class ManagerCreateManagerView implements Initializable {
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

            // Set the main layout view
            Scene currentScene = buttonReturnHome.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);

        } catch (IOException e) {
            // Handle errors in loading the view
            e.printStackTrace();
        }
    }

    @FXML
    public void createManager(ActionEvent event) {
        try {
            // Retrieve input values for creating a new manager
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

            // Convert the date of birth to Date type
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Create a new manager request and call the manager controller to create the
            // manager
            CreateManagerRequest request = new CreateManagerRequest(username, password, fullname, email, dateOfBirth);
            ManagerController managerController = new ManagerController();
            managerController.createManager(request);

            // Show success message and return to home view
            CustomAlert.show(Alert.AlertType.INFORMATION, "Manager Created", "Manager created successfully");
            returnHome(event);

        } catch (Exception e) {
            // Handle errors during manager creation
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize any necessary components (currently empty)
    }
}