package com.trustmejunior.view.Owner;

import javafx.fxml.FXML;

/**
 * @author TrustMeJunior
 */

import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.utils.SessionManager;

public class OwnerView implements Initializable {
    @FXML
    private Label homeTotalProperties;

    @FXML
    private Label homeTotalRA;

    @FXML
    private Label nameField;

    private Account account = SessionManager.getCurrentAccount();
    private PropertyController propertyController = new PropertyController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();

    public void initData() {
        // Fetch and display the total number of properties owned by the account
        homeTotalProperties
                .setText(String.valueOf(propertyController.getPropertiesByOwnerId(account.getAccountId()).size()));

        // Fetch and display the total number of rental agreements associated with the account
        homeTotalRA.setText(
                String.valueOf(rentalAgreementController.getRentalAgreementsByOwnerId(account.getAccountId()).size()));

        // Set the owner's full name in the name field
        nameField.setText(account.getFullName());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the data when the view is loaded
        initData();
    }
}
