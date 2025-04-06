package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.utils.DateFormatter;
import com.trustmejunior.view.Layout;

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

public class TenantProfileView implements Initializable {
    @FXML
    private Label fullName;

    @FXML
    private Label email;

    @FXML
    private Label dob;

    @FXML
    private Label role;

    @FXML
    private Button editProfileButton;

    @FXML
    private Button returnButton;

    private Account account = SessionManager.getCurrentAccount();

    private TenantController tenantController = new TenantController();

    public void initData(Tenant Tenant) {
        // Initialize data for displaying tenant information
        fullName.setText(Tenant.getFullName()); // Set full name
        email.setText(Tenant.getEmail()); // Set email
        dob.setText(DateFormatter.formatDate(Tenant.getDob(), "dd/MM/yyyy")); // Format and set date of birth

        role.setText(Tenant.getAccountRole().toString()); // Set account role (e.g., Tenant, Admin)
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handler for the edit profile button
        editProfileButton.setOnAction(event -> {
            try {
                // Load the edit profile page
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/TenantEditProfileView.fxml"));
                Parent root = loader.load();
                TenantEditProfileView view = loader.getController();
                // Retrieve tenant data and pass it to the edit profile view
                Tenant tenant = tenantController.getTenantByAccountId(account.getAccountId());
                view.initData(tenant);

                // Navigate to the edit profile page
                Scene currentScene = editProfileButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle error in loading the profile edit page
            }
        });

        // Set up event handler for the return button
        returnButton.setOnAction(event -> {
            try {
                // Load the tenant view page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/TenantView.fxml"));
                Parent root = loader.load();
                TenantView view = loader.getController();
                view.initData(); // Initialize tenant view data

                // Navigate to the tenant view page
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle error in loading the tenant view page
            }
        });
    }
}
