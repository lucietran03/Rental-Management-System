package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.model.User.Owner;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;
import com.trustmejunior.controller.PropertyImageController;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import com.trustmejunior.model.Property.PropertyImage;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TenantResidentialPropertyView implements Initializable {
    @FXML
    private Label price;

    @FXML
    private Label address;

    @FXML
    private Label status;

    @FXML
    private Label propertyType;

    @FXML
    private Label numberOfBedrooms;

    @FXML
    private Label hasGarden;

    @FXML
    private Label isPetFriendly;

    @FXML
    private Label owner;

    @FXML
    private VBox hostsVBox;

    @FXML
    private Button returnButton;

    @FXML
    private ImageView propertyImage;

    private Account account = SessionManager.getCurrentAccount();

    private HostController hostController = new HostController();
    private OwnerController ownerController = new OwnerController();
    private PropertyController propertyController = new PropertyController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private PropertyImageController propertyImageController = new PropertyImageController();

    public void initData(ResidentialProperty property) {
        // Set property details
        price.setText(String.format("$%.2f", property.getPrice())); // Display price
        address.setText(property.getAddress()); // Display address
        status.setText(property.getStatus().toString()); // Display property status
        propertyType.setText(property.getType().toString()); // Display property type
        numberOfBedrooms.setText(String.valueOf(property.getNumberOfBedrooms())); // Display number of bedrooms
        hasGarden.setText(property.isHasGarden() ? "Yes" : "No"); // Display garden info
        isPetFriendly.setText(property.isPetFriendly() ? "Yes" : "No"); // Display pet-friendly info

        // Load and display property image
        PropertyImage image = propertyImageController.getPropertyImageByPropertyId(property.getPropertyId());
        if (image != null) {
            propertyImage.setImage(new Image(image.getUrl())); // Set image URL
        }

        // Retrieve and display property owner information
        Owner propertyOwner = ownerController.getOwnerByAccountId(property.getOwnerId());
        owner.setText(String.format("%s (ID: %d)", propertyOwner.getFullName(), propertyOwner.getAccountId()));

        // Load and display hosts for the property
        List<Host> hosts = hostController.getHostsByPropertyId(property.getPropertyId());
        hostsVBox.getChildren().clear(); // Clear previous host entries
        for (Host host : hosts) {
            Label hostLabel = new Label(String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()));
            hostsVBox.getChildren().add(hostLabel); // Add host details to the list
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handler for the return button
        returnButton.setOnAction(event -> {
            try {
                // Load the all properties page
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/TenantAllPropertyView.fxml"));
                Parent root = loader.load();
                TenantAllPropertyView view = loader.getController();

                // Retrieve rental agreements and associated properties
                List<RentalAgreement> rentalAgreements = rentalAgreementController
                        .getRentalAgreementsByMainTenantId(account.getAccountId());
                rentalAgreements.addAll(rentalAgreementController.getRentalAgreementsBySubTenantId(account.getAccountId()));

                List<Property> properties = new ArrayList<>();
                for (RentalAgreement rentalAgreement : rentalAgreements) {
                    properties.add(propertyController.getPropertyById(rentalAgreement.getPropertyId()));
                }

                // Display all properties
                view.showAllProperties(FXCollections.observableArrayList(properties));

                // Navigate to the all properties page
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle error in loading the properties page
            }
        });
    }
}
