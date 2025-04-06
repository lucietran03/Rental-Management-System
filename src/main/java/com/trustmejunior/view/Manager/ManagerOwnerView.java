package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Owner;
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

public class ManagerOwnerView implements Initializable {
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

    public void initData(Owner owner) {
        // Initialize the UI fields with the owner data
        fullName.setText(owner.getFullName());
        email.setText(owner.getEmail());
        dob.setText(DateFormatter.formatDate(owner.getDob(), "dd/MM/yyyy"));
        role.setText(owner.getAccountRole().toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set the action for the return button
        returnButton.setOnAction(event -> {
            try {
                // Load the view for managing all accounts
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerAllAccountView.fxml"));
                Parent root = loader.load();
                ManagerAllAccountView view = loader.getController();
                view.showAllAccounts(FXCollections.observableArrayList(accountController.getAllAccounts()));

                // Set the new view in the current scene
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle IO exceptions during scene transition
            }
        });
    }
}
