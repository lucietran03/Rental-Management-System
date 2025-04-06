package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.model.User.Owner;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

public class TenantCommercialPropertyView implements Initializable {
    @FXML
    private Label price;

    @FXML
    private Label address;

    @FXML
    private Label status;

    @FXML
    private Label propertyType;

    @FXML
    private Label businessType;

    @FXML
    private Label area;

    @FXML
    private Label hasParking;

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

    public void initData(CommercialProperty property) {
        // Display property details
        price.setText(String.format("$%.2f", property.getPrice()));
        address.setText(property.getAddress());
        status.setText(property.getStatus().toString());
        propertyType.setText(property.getType().toString());
        businessType.setText(property.getBusinessType().toString());
        area.setText(String.format("%.2f sqm", property.getArea()));
        hasParking.setText(property.isHasParking() ? "Yes" : "No");

        // Load and display property image if available
        PropertyImage image = propertyImageController.getPropertyImageByPropertyId(property.getPropertyId());
        if (image != null) {
            propertyImage.setImage(new Image(image.getUrl()));
        }

        // Get and display property owner details
        Owner propertyOwner = ownerController.getOwnerByAccountId(property.getOwnerId());
        owner.setText(String.format("%s (ID: %d)", propertyOwner.getFullName(), propertyOwner.getAccountId()));

        // Load and display all hosts for the property
        List<Host> hosts = hostController.getHostsByPropertyId(property.getPropertyId());
        hostsVBox.getChildren().clear();
        for (Host host : hosts) {
            Label hostLabel = new Label(String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()));
            hostsVBox.getChildren().add(hostLabel);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Return button action: Navigate back to the list of properties
        returnButton.setOnAction(event -> {
            try {
                // Load and display tenant's properties
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/TenantAllPropertyView.fxml"));
                Parent root = loader.load();
                TenantAllPropertyView view = loader.getController();

                // Retrieve rental agreements for the tenant and their properties
                List<RentalAgreement> rentalAgreements = rentalAgreementController
                        .getRentalAgreementsByMainTenantId(account.getAccountId());
                rentalAgreements.addAll(rentalAgreementController.getRentalAgreementsBySubTenantId(account.getAccountId()));

                List<Property> properties = new ArrayList<>();
                for (RentalAgreement rentalAgreement : rentalAgreements) {
                    properties.add(propertyController.getPropertyById(rentalAgreement.getPropertyId()));
                }

                // Show the properties in the view
                view.showAllProperties(FXCollections.observableArrayList(properties));

                // Change the main layout view
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
