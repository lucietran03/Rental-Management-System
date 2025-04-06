package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Manager;
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

public class ManagerManagerView implements Initializable {
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

    // Initializes manager data into the form fields
    public void initData(Manager manager) {
        fullName.setText(manager.getFullName());
        email.setText(manager.getEmail());
        dob.setText(DateFormatter.formatDate(manager.getDob(), "dd/MM/yyyy"));
        role.setText(manager.getAccountRole().toString());
    }

    // Sets up return button action to navigate to ManagerAllAccountView
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        returnButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/Manager/ManagerAllAccountView.fxml"));
                Parent root = loader.load();
                ManagerAllAccountView view = loader.getController();
                view.showAllAccounts(FXCollections.observableArrayList(accountController.getAllAccounts()));

                // Sets the main layout with the newly loaded view
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
