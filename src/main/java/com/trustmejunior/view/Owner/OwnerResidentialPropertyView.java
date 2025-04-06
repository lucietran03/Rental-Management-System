package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OwnerResidentialPropertyView implements Initializable {

    @FXML
    private ImageView propertyImage;

    @FXML
    private Label address;

    @FXML
    private Label hasGarden;

    @FXML
    private Label numberOfBedroom;

    @FXML
    private Label petFriendly;

    @FXML
    private Label price;

    @FXML
    private Label propertyType;

    @FXML
    private Label status;

    @FXML
    private VBox vboxHost;

    @FXML
    private Button buttonReturn;

    private PropertyImageController propertyImageController = new PropertyImageController();
    private Account account = SessionManager.getCurrentAccount();
    private HostController hostController = new HostController();
    private OwnerController ownerController = new OwnerController();
    private PropertyController propertyController = new PropertyController();

    public void initData(ResidentialProperty property) {
        // Load and display the property image if available
        PropertyImage image = propertyImageController.getPropertyImageByPropertyId(property.getPropertyId());
        if (image != null) {
            propertyImage.setImage(new Image(image.getUrl()));
        }

        // Display property details
        price.setText(String.format("$%.2f", property.getPrice()));
        address.setText(property.getAddress());
        status.setText(property.getStatus().toString());
        propertyType.setText(property.getType().toString());
        numberOfBedroom.setText(String.valueOf(property.getNumberOfBedrooms()));
        hasGarden.setText(property.isHasGarden() ? "Yes" : "No");
        petFriendly.setText(property.isPetFriendly() ? "Yes" : "No");

        // Load and display the list of hosts associated with the property
        List<Host> hosts = hostController.getHostsByPropertyId(property.getPropertyId());
        vboxHost.getChildren().clear(); // Clear any previous content
        for (Host host : hosts) {
            // Add a label for each host
            Label hostLabel = new Label(String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()));
            vboxHost.getChildren().add(hostLabel);
        }
    }

    @FXML
    public void returnHome() {
        try {
            // Navigate to the "OwnerAllPropertyView" and display all properties owned by
            // the current account
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerAllPropertyView.fxml"));
            Parent root = loader.load();
            OwnerAllPropertyView view = loader.getController();
            view.showAllProperties(FXCollections
                    .observableArrayList(propertyController.getPropertiesByOwnerId(account.getAccountId())));

            // Set the new scene in the current layout
            Scene currentScene = buttonReturn.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle any loading errors
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize any required settings or components (empty for now)
    }
}
