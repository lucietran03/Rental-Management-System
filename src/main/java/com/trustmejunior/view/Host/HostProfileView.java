package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
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

public class HostProfileView implements Initializable {
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

    private HostController hostController = new HostController();

    public void initData(Host host) {
        // Set the host's details in the UI fields
        fullName.setText(host.getFullName());
        email.setText(host.getEmail());
        dob.setText(DateFormatter.formatDate(host.getDob(), "dd/MM/yyyy"));
        role.setText(host.getAccountRole().toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set action for the edit profile button
        editProfileButton.setOnAction(event -> {
            try {
                // Load and display the Host Edit Profile view
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/HostEditProfileView.fxml"));
                Parent root = loader.load();
                HostEditProfileView view = loader.getController();
                Host host = hostController.getHostByAccountId(account.getAccountId());
                view.initData(host);

                // Replace the current scene with the edit profile scene
                Scene currentScene = editProfileButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Set action for the return button
        returnButton.setOnAction(event -> {
            try {
                // Load and display the Host View
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/HostView.fxml"));
                Parent root = loader.load();
                HostView view = loader.getController();
                view.initData();

                // Replace the current scene with the host view scene
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
