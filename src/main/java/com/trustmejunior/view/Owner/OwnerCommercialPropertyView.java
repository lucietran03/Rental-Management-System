package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class OwnerCommercialPropertyView implements Initializable {

    @FXML
    private Label address;

    @FXML
    private Label area;

    @FXML
    private Label bussinessType;

    @FXML
    private Button buttonReturn;

    @FXML
    private Label hasparking;

    @FXML
    private Label price;

    @FXML
    private Label propertyType;

    @FXML
    private Label status;

    @FXML
    private VBox vboxHost;

    @FXML
    private ImageView propertyImage;

    private Account account = SessionManager.getCurrentAccount();
    private HostController hostController = new HostController();
    private PropertyController propertyController = new PropertyController();
    private PropertyImageController propertyImageController = new PropertyImageController();

    public void initData(CommercialProperty property) {
        // Display property details
        price.setText(String.format("$%.2f", property.getPrice())); // Format price as currency
        address.setText(property.getAddress()); // Set address text
        status.setText(property.getStatus().toString()); // Set status text
        propertyType.setText(property.getType().toString()); // Set property type
        bussinessType.setText(property.getBusinessType().toString()); // Set business type
        area.setText(String.format("%.2f sqm", property.getArea())); // Display area in square meters
        hasparking.setText(property.isHasParking() ? "Yes" : "No"); // Display parking availability

        // Load and display property image
        PropertyImage image = propertyImageController.getPropertyImageByPropertyId(property.getPropertyId());
        if (image != null) {
            propertyImage.setImage(new Image(image.getUrl())); // Set the property image
        }

        // Load and display hosts for the property
        List<Host> hosts = hostController.getHostsByPropertyId(property.getPropertyId());
        vboxHost.getChildren().clear(); // Clear previous hosts
        for (Host host : hosts) {
            Label hostLabel = new Label(String.format("%s (ID: %d)", host.getFullName(), host.getAccountId())); // Display
                                                                                                                // host
                                                                                                                // info
            vboxHost.getChildren().add(hostLabel); // Add host label to VBox
        }
    }

    @FXML
    void returnHome(ActionEvent event) {
        try {
            // Load and display the OwnerAllPropertyView
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerAllPropertyView.fxml"));
            Parent root = loader.load();
            OwnerAllPropertyView view = loader.getController();
            // Show properties of the current owner
            view.showAllProperties(FXCollections
                    .observableArrayList(propertyController.getPropertiesByOwnerId(account.getAccountId())));

            // Update the main layout with the new view
            Scene currentScene = buttonReturn.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle loading error
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic (empty for now)
    }
}
