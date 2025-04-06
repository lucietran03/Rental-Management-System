package com.trustmejunior.view.Visitor;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.model.User.Owner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VisitorResidentialPropertyDetailView implements Initializable {
    @FXML
    private ImageView propertyImage;

    @FXML
    private Label address;

    @FXML
    private Label hasGarden;

    @FXML
    private VBox hostsVBox;

    @FXML
    private Label isPetFriendly;

    @FXML
    private Label numberOfBedrooms;

    @FXML
    private Label owner;

    @FXML
    private Label price;

    @FXML
    private Label propertyType;

    @FXML
    private Button returnButton;

    @FXML
    private Label status;

    private HostController hostController = new HostController();
    private OwnerController ownerController = new OwnerController();
    private PropertyImageController propertyImageController = new PropertyImageController();

    // Initializes the details view with the residential property data
    public void initData(ResidentialProperty property) {
        price.setText(String.format("$%.2f", property.getPrice()));
        address.setText(property.getAddress());
        status.setText(property.getStatus().toString());
        propertyType.setText(property.getType().toString());
        numberOfBedrooms.setText(String.valueOf(property.getNumberOfBedrooms()));
        hasGarden.setText(property.isHasGarden() ? "Yes" : "No");
        isPetFriendly.setText(property.isPetFriendly() ? "Yes" : "No");

        // Fetch owner details and display
        Owner propertyOwner = ownerController.getOwnerByAccountId(property.getOwnerId());
        owner.setText(String.format("%s (ID: %d)", propertyOwner.getFullName(), propertyOwner.getAccountId()));

        // Display associated hosts
        List<Host> hosts = hostController.getHostsByPropertyId(property.getPropertyId());
        hostsVBox.getChildren().clear();
        for (Host host : hosts) {
            Label hostLabel = new Label(String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()));
            hostsVBox.getChildren().add(hostLabel);
        }

        // Display property image if available
        PropertyImage image = propertyImageController.getPropertyImageByPropertyId(property.getPropertyId());
        if (image != null) {
            propertyImage.setImage(new Image(image.getUrl()));
        }
    }

    // Navigates back to the Visitor Property View when the "Return" button is
    // clicked
    @FXML
    void returnToViewAllProperty(ActionEvent event) throws IOException {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
