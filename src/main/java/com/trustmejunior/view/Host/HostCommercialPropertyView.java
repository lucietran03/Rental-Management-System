package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.model.User.Owner;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HostCommercialPropertyView implements Initializable {
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
    private PropertyImageController propertyImageController = new PropertyImageController();

    public void initData(CommercialProperty property) {
        // Load and display the property image if available
        PropertyImage image = propertyImageController.getPropertyImageByPropertyId(property.getPropertyId());
        if (image != null) {
            propertyImage.setImage(new Image(image.getUrl())); // Set the image URL
        }

        // Display property details in the UI
        price.setText(String.format("$%.2f", property.getPrice())); // Format price
        address.setText(property.getAddress());
        status.setText(property.getStatus().toString());
        propertyType.setText(property.getType().toString());
        businessType.setText(property.getBusinessType().toString());
        area.setText(String.format("%.2f sqm", property.getArea())); // Format area
        hasParking.setText(property.isHasParking() ? "Yes" : "No"); // Display parking status

        // Retrieve and display property owner information
        Owner propertyOwner = ownerController.getOwnerByAccountId(property.getOwnerId());
        owner.setText(String.format("%s (ID: %d)", propertyOwner.getFullName(), propertyOwner.getAccountId()));

        // Load and display associated hosts for the property
        List<Host> hosts = hostController.getHostsByPropertyId(property.getPropertyId());
        hostsVBox.getChildren().clear(); // Clear previous host data
        for (Host host : hosts) {
            // Add each host as a label to the VBox
            Label hostLabel = new Label(String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()));
            hostsVBox.getChildren().add(hostLabel);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Handle the return button action to navigate back to the previous view
        returnButton.setOnAction(event -> {
            try {
                // Load the HostAllPropertyView and display all properties for the current host
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/HostAllPropertyView.fxml"));
                Parent root = loader.load();
                HostAllPropertyView view = loader.getController();
                List<Property> properties = propertyController.getPropertiesByHostId(account.getAccountId());
                view.showAllProperties(FXCollections.observableArrayList(properties));

                // Set the new scene
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace(); // Handle IO exception
            }
        });
    }
}
