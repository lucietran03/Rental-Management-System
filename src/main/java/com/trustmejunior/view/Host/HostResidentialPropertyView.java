package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.model.Property.ResidentialProperty;
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
import java.util.List;
import java.util.ResourceBundle;

public class HostResidentialPropertyView implements Initializable {
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
    private PropertyImageController propertyImageController = new PropertyImageController();

    public void initData(ResidentialProperty property) {
        // Load and set property image
        PropertyImage image = propertyImageController.getPropertyImageByPropertyId(property.getPropertyId());
        if (image != null) {
            propertyImage.setImage(new Image(image.getUrl()));
        }

        // Display property details
        price.setText(String.format("$%.2f", property.getPrice()));
        address.setText(property.getAddress());
        status.setText(property.getStatus().toString());
        propertyType.setText(property.getType().toString());
        numberOfBedrooms.setText(String.valueOf(property.getNumberOfBedrooms()));
        hasGarden.setText(property.isHasGarden() ? "Yes" : "No");
        isPetFriendly.setText(property.isPetFriendly() ? "Yes" : "No");

        // Get and display owner information
        Owner propertyOwner = ownerController.getOwnerByAccountId(property.getOwnerId());
        owner.setText(String.format("%s (ID: %d)", propertyOwner.getFullName(), propertyOwner.getAccountId()));

        // Load and display hosts for the property
        List<Host> hosts = hostController.getHostsByPropertyId(property.getPropertyId());
        hostsVBox.getChildren().clear();
        for (Host host : hosts) {
            Label hostLabel = new Label(String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()));
            hostsVBox.getChildren().add(hostLabel);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set action for the return button to navigate to the HostAllPropertyView
        returnButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/HostAllPropertyView.fxml"));
                Parent root = loader.load();
                HostAllPropertyView view = loader.getController();

                // Get and display all properties for the host
                List<Property> properties = propertyController.getPropertiesByHostId(account.getAccountId());
                view.showAllProperties(FXCollections.observableArrayList(properties));

                // Replace the current scene with the HostAllPropertyView scene
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
