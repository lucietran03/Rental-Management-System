package com.trustmejunior.view;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;

import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.request.LoginRequest;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;

import com.trustmejunior.view.Visitor.VisitorPropertyView;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginView implements Initializable {
    @FXML
    private Button ButtonLogin;

    @FXML
    private ImageView ImageViewLoginView;

    @FXML
    private PasswordField TextFieldPasswordLoginView;

    @FXML
    private TextField TextFieldUsernameLoginView;

    @FXML
    private Button registerButton;

    private AccountController accountController = new AccountController();
    // Validates the login credentials, determines the account role, and navigates to the appropriate view.
    private void validate(Event event, String username, String password) throws IOException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Account account = accountController.login(loginRequest);

        if (account != null) {
            FXMLLoader sidebarLoader = new FXMLLoader();
            FXMLLoader mainLoader = new FXMLLoader();

            Parent sidebar = null;
            Parent main = null;

            AccountRole accountRole = account.getAccountRole();

            switch (accountRole) {
                case MANAGER -> {
                    SessionManager.setCurrentAccount(account);

                    sidebarLoader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerSidebar.fxml"));
                    sidebar = sidebarLoader.load();

                    mainLoader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerView.fxml"));
                    main = mainLoader.load();
                }
                case TENANT -> {
                    SessionManager.setCurrentAccount(account);

                    sidebarLoader.setLocation(getClass().getResource("/com/trustmejunior/view/TenantSidebar.fxml"));
                    sidebar = sidebarLoader.load();

                    mainLoader.setLocation(getClass().getResource("/com/trustmejunior/view/TenantView.fxml"));
                    main = mainLoader.load();
                }
                case HOST -> {
                    SessionManager.setCurrentAccount(account);

                    sidebarLoader.setLocation(getClass().getResource("/com/trustmejunior/view/HostSidebar.fxml"));
                    sidebar = sidebarLoader.load();

                    mainLoader.setLocation(getClass().getResource("/com/trustmejunior/view/HostView.fxml"));
                    main = mainLoader.load();
                }
                case OWNER -> {
                    SessionManager.setCurrentAccount(account);

                    sidebarLoader.setLocation(getClass().getResource("/com/trustmejunior/view/OwnerSidebar.fxml"));
                    sidebar = sidebarLoader.load();

                    mainLoader.setLocation(getClass().getResource("/com/trustmejunior/view/OwnerView.fxml"));
                    main = mainLoader.load();
                }
                default -> {
                    CustomAlert.show(Alert.AlertType.ERROR, "Error", "Invalid account role");
                }
            }
            // Combines sidebar and main views into the layout and displays them.
            if (sidebar != null && main != null) {
                FXMLLoader layoutLoader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/Layout.fxml"));
                Parent layout = layoutLoader.load();

                Layout layoutController = layoutLoader.getController();
                layoutController.setSidebar(sidebar);
                layoutController.setMain(main);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(layout);
                scene.setUserData(layoutController);
                stage.setScene(scene);
                stage.show();
            }
        } else {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Invalid username or password");
        }
    }
    // Attempts login by validating input fields and invoking the validate method.
    private void attemptLogin(Event event) {
        String username = TextFieldUsernameLoginView.getText();
        String password = TextFieldPasswordLoginView.getText();

        if (username.isEmpty() || password.isEmpty()) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Please enter username and password");
            return;
        }

        try {
            validate(event, username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Clear any existing session when login view is initialized
        SessionManager.clearSession();
        // Sets up login button action.
        ButtonLogin.setOnAction(event -> {
            attemptLogin(event);
        });
        // Handles "Enter" key for username and password fields to trigger login.
        TextFieldUsernameLoginView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                attemptLogin(event);
            }
        });

        TextFieldPasswordLoginView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                attemptLogin(event);
            }
        });
    }

    @FXML
    private Button joinAsGuestButton;
    // Navigates to the visitor property view for guest access.
    @FXML
    void guestViewPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/trustmejunior/view/VisitorPropertyView.fxml"));
        Parent root = loader.load();
        VisitorPropertyView view = loader.getController();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setUserData(view);
        stage.setScene(scene);
        stage.show();
    }
    // Navigates to the account registration view.
    @FXML
    void registerAccount(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/trustmejunior/view/RegisterView.fxml"));
        Parent root = loader.load();
        RegisterView view = loader.getController();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setUserData(view);
        stage.setScene(scene);
        stage.show();
    }
}
