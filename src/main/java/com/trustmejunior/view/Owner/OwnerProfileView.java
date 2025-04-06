package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Owner;
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

public class OwnerProfileView implements Initializable {
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

    private OwnerController ownerController = new OwnerController();

    public void initData(Owner owner) {
        // Set owner data to respective fields
        fullName.setText(owner.getFullName());
        email.setText(owner.getEmail());
        dob.setText(DateFormatter.formatDate(owner.getDob(), "dd/MM/yyyy")); // Format and display DOB

        role.setText(owner.getAccountRole().toString()); // Set account role (e.g., Admin, User)
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Handle edit profile button click
        editProfileButton.setOnAction(event -> {
            try {
                // Load OwnerEditProfileView and pass current owner data to it
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/OwnerEditProfileView.fxml"));
                Parent root = loader.load();
                OwnerEditProfileView view = loader.getController();
                Owner owner = ownerController.getOwnerByAccountId(account.getAccountId());
                view.initData(owner);

                // Set new view as main layout
                Scene currentScene = editProfileButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle potential loading errors
            }
        });

        // Handle return button click
        returnButton.setOnAction(event -> {
            try {
                // Load OwnerView and initialize it
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/OwnerView.fxml"));
                Parent root = loader.load();
                OwnerView view = loader.getController();
                view.initData(); // Initialize data for OwnerView

                // Set new view as main layout
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle potential loading errors
            }
        });
    }
}
