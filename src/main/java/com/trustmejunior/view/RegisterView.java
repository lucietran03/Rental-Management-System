package com.trustmejunior.view;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.*;
import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.request.*;
import com.trustmejunior.utils.CustomAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class RegisterView implements Initializable {
    @FXML
    private Label fullnameErrorLabel;
    @FXML
    private TextField fullnameField;

    @FXML
    private Label usernameErrorLabel;
    @FXML
    private TextField usernameField;

    @FXML
    private Label emailErrorLabel;
    @FXML
    private TextField emailField;

    @FXML
    private Label passwordErrorLabel;
    @FXML
    private TextField passwordField;

    @FXML
    private Label dateErrorLabel;
    @FXML
    private DatePicker datePicker;

    @FXML
    private Label roleErrorLabel;
    @FXML
    private ComboBox<AccountRole> roleDropList;

    @FXML
    private Button signupButton;
    @FXML
    private Button returnToLoginPageButton;

    @FXML
    private ImageView imageLogo;

    // Validates the username field
    private boolean validateUsername(String username) {
        username = username.trim();
        if (username.isEmpty()) {
            usernameErrorLabel.setText("Username cannot be empty");
            return false;
        }
        if (username.length() < 5 || username.length() > 32) {
            usernameErrorLabel.setText("Username must be between 5 and 32 characters");
            return false;
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            usernameErrorLabel.setText("Username must contain only letters and numbers");
            return false;
        }
        usernameErrorLabel.setText(""); // Clear error label if valid
        return true;
    }

    // Validates the password field
    private boolean validatePassword(String password) {
        password = password.trim();
        if (password.isEmpty()) {
            passwordErrorLabel.setText("Password cannot be empty");
            return false;
        }
        if (password.length() < 6 || password.length() > 32) {
            passwordErrorLabel.setText("Password must be between 6 and 32 characters");
            return false;
        }
        if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
            passwordErrorLabel.setText("Password must contain uppercase, lowercase, and a number");
            return false;
        }
        passwordErrorLabel.setText("");// Clear error label if valid
        return true;
    }
    // Validates the fullname field
    private boolean validateFullname(String fullname) {
        fullname = fullname.trim();
        if (fullname.isEmpty()) {
            fullnameErrorLabel.setText("Fullname cannot be empty");
            return false;
        }
        if (!fullname.matches("[a-zA-Z\\s]+")) {
            fullnameErrorLabel.setText("Fullname cannot contain special characters");
            return false;
        }
        fullnameErrorLabel.setText("");
        return true;
    }
    // Validates the email field
    private boolean validateEmail(String email) {
        email = email.trim();
        if (email.isEmpty()) {
            emailErrorLabel.setText("Email cannot be empty");
            return false;
        }
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(regex)) {
            emailErrorLabel.setText("Invalid email format");
            return false;
        }
        emailErrorLabel.setText("");
        return true;
    }

    // Validates the date of birth field
    private boolean validateDateOfBirth(LocalDate date) {
        if (date == null || date.isAfter(LocalDate.now())) {
            dateErrorLabel.setText("Date of Birth cannot be today or in the future");
            return false;
        }
        dateErrorLabel.setText("");
        return true;
    }
    // Validates the role selection
    private boolean validateRole(AccountRole role) {
        if (role == null) {
            roleErrorLabel.setText("Role must be selected");
            return false;
        }
        roleErrorLabel.setText("");
        return true;
    }

    // Validates all form fields
    private boolean isFormValid(String username, String password, String fullname, String email, LocalDate dateOfBirth,
            AccountRole role) {
        boolean isValid = true;
        if (!validateUsername(username))
            isValid = false;
        if (!validatePassword(password))
            isValid = false;
        if (!validateFullname(fullname))
            isValid = false;
        if (!validateEmail(email))
            isValid = false;
        if (!validateDateOfBirth(dateOfBirth))
            isValid = false;
        if (!validateRole(role))
            isValid = false;
        return isValid;
    }

    // Handle form submission to validate inputs and show errors if necessary
    @FXML
    private void handleSubmit(ActionEvent event) {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String fullname = fullnameField.getText();
            String email = emailField.getText();
            LocalDate localDob = datePicker.getValue();
            AccountRole role = roleDropList.getSelectionModel().getSelectedItem();
            // Validate form
            if (!isFormValid(username, password, fullname, email, localDob, role)) {
                CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please fix the errors before submitting.");
                return;
            }
            // Convert LocalDate to Date
            Date dateOfBirth = Date.from(localDob.atStartOfDay(ZoneId.systemDefault()).toInstant());
            // Handle different roles and create account
            switch (role) {
                case MANAGER -> {
                    CreateManagerRequest request = new CreateManagerRequest(username, password, fullname, email,
                            dateOfBirth);
                    ManagerController managerController = new ManagerController();
                    managerController.createManager(request);
                    break;
                }
                case HOST -> {
                    CreateHostRequest request = new CreateHostRequest(username, password, fullname, email, dateOfBirth);
                    HostController hostController = new HostController();
                    hostController.createHost(request);
                    break;
                }
                case OWNER -> {
                    CreateOwnerRequest request = new CreateOwnerRequest(username, password, fullname, email,
                            dateOfBirth);
                    OwnerController ownerController = new OwnerController();
                    ownerController.createOwner(request);
                    break;
                }
                case TENANT -> {
                    CreateTenantRequest request = new CreateTenantRequest(username, password, fullname, email,
                            dateOfBirth);
                    TenantController tenantController = new TenantController();
                    tenantController.createTenant(request);
                    break;
                }
            }
            // Show success message
            String content = role.toString().substring(0, 1).toUpperCase() + role.toString().substring(1).toLowerCase()
                    + " created successfully!";
            CustomAlert.show(Alert.AlertType.CONFIRMATION, "ACCOUNT CREATED", content);
            // Return to login page
            returnToLoginPage(event);
        } catch (Exception e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", e.getMessage());
            e.printStackTrace();
        }
    }
    // Navigates to the login page
    @FXML
    public void returnToLoginPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/trustmejunior/view/LoginView.fxml"));
        Parent root = loader.load();
        LoginView view = loader.getController();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setUserData(view);
        stage.setScene(scene);
        stage.show();
    }
    // Initializes the controller and sets up listeners
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validate fullname: Show error message if empty or invalid
        fullnameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateFullname(newValue);
        });

        // Validate username: Show error message if empty or invalid
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateUsername(newValue);
        });

        // Validate email: Show error message if invalid
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEmail(newValue);
        });

        // Validate password: Show error message if too short or invalid
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePassword(newValue);
        });

        // Validate date of birth: Show error if invalid
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateDateOfBirth(newValue);
        });

        // Validate role selection
        roleDropList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            validateRole(newValue);
        });

        // Populate role dropdown
        roleDropList.getItems().addAll(AccountRole.values());
    }
}
