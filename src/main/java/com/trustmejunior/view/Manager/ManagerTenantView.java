package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.utils.DateFormatter;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagerTenantView implements Initializable {
    @FXML
    private Label fullName;

    @FXML
    private Label email;

    @FXML
    private Label dob;

    @FXML
    private Label role;

    @FXML
    private Button returnButton;

    private Account account = SessionManager.getCurrentAccount();

    private AccountController accountController = new AccountController();

    // Initialize tenant data and display it in the relevant fields
    public void initData(Tenant Tenant) {
        fullName.setText(Tenant.getFullName()); // Set full name
        email.setText(Tenant.getEmail()); // Set email
        dob.setText(DateFormatter.formatDate(Tenant.getDob(), "dd/MM/yyyy")); // Format and set date of birth
        role.setText(Tenant.getAccountRole().toString()); // Set account role
    }

    // Initialize the view and set up return button action
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        returnButton.setOnAction(event -> {
            try {
                // Load ManagerAllAccountView and display all accounts
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerAllAccountView.fxml"));
                Parent root = loader.load();
                ManagerAllAccountView view = loader.getController();
                view.showAllAccounts(FXCollections.observableArrayList(accountController.getAllAccounts()));

                // Switch to the new view
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle loading errors
            }
        });
    }

}
