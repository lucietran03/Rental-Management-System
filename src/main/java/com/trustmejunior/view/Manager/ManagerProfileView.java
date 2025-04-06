package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.ManagerController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Manager;
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

public class ManagerProfileView implements Initializable {
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

    private ManagerController managerController = new ManagerController();

    public void initData(Manager manager) {
        // Populate the UI fields with manager data
        fullName.setText(manager.getFullName());
        email.setText(manager.getEmail());
        dob.setText(DateFormatter.formatDate(manager.getDob(), "dd/MM/yyyy"));
        role.setText(manager.getAccountRole().toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set action for the edit profile button
        editProfileButton.setOnAction(event -> {
            try {
                // Load the edit profile view for the manager
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerEditProfileView.fxml"));
                Parent root = loader.load();
                ManagerEditProfileView view = loader.getController();
                Manager manager = managerController.getManagerByAccountId(account.getAccountId());
                view.initData(manager);

                // Set the new scene for editing profile
                Scene currentScene = editProfileButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle IO exceptions during scene transition
            }
        });

        // Set action for the return button
        returnButton.setOnAction(event -> {
            try {
                // Load the main manager view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/ManagerView.fxml"));
                Parent root = loader.load();
                ManagerView view = loader.getController();
                view.initData();

                // Set the new scene for the main manager view
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle IO exceptions during scene transition
            }
        });
    }
}
