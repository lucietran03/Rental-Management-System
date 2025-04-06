package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Property.CommercialProperty;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerCommercialPropertyView implements Initializable {
    @FXML
    private ImageView propertyImage;

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

    private Account account = SessionManager.getCurrentAccount();

    private HostController hostController = new HostController();
    private OwnerController ownerController = new OwnerController();
    private PropertyController propertyController = new PropertyController();
    private PropertyImageController propertyImageController = new PropertyImageController();

    public void initData(CommercialProperty property) {
        // Display property information
        price.setText(String.format("$%.2f", property.getPrice()));
        address.setText(property.getAddress());
        status.setText(property.getStatus().toString());
        propertyType.setText(property.getType().toString());
        businessType.setText(property.getBusinessType().toString());
        area.setText(String.format("%.2f sqm", property.getArea()));
        hasParking.setText(property.isHasParking() ? "Yes" : "No");

        // Get and display owner information
        Owner propertyOwner = ownerController.getOwnerByAccountId(property.getOwnerId());
        owner.setText(String.format("%s (ID: %d)", propertyOwner.getFullName(), propertyOwner.getAccountId()));

        // Load and display host information
        List<Host> hosts = hostController.getHostsByPropertyId(property.getPropertyId());
        hostsVBox.getChildren().clear(); // Clear previous host labels
        for (Host host : hosts) {
            Label hostLabel = new Label(String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()));
            hostsVBox.getChildren().add(hostLabel); // Add each host as a label
        }

        // Load and display property image
        PropertyImage image = propertyImageController.getPropertyImageByPropertyId(property.getPropertyId());
        if (image != null) {
            propertyImage.setImage(new Image(image.getUrl())); // Set property image if available
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up return button action to navigate back to the property view
        returnButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerAllPropertyView.fxml"));
                Parent root = loader.load();
                ManagerAllPropertyView view = loader.getController();
                view.showAllProperties(FXCollections.observableArrayList(propertyController.getAllProperties()));

                // Set the scene to show all properties
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}