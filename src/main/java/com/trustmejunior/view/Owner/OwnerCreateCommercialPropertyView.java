package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.CommercialPropertyController;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyBusinessType;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.request.CreateCommercialPropertyRequest;
import com.trustmejunior.request.CreatePropertyImageRequest;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.utils.Storage;
import com.trustmejunior.view.Layout;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OwnerCreateCommercialPropertyView implements Initializable {
    @FXML
    private Button buttonReturnHome;

    @FXML
    private Button buttoncreateProperty;

    @FXML
    private ChoiceBox<PropertyStatus> choiceboxStatus;

    @FXML
    private ListView<String> listviewHost;

    @FXML
    private ToggleGroup hasParkingInput = new ToggleGroup();

    @FXML
    private RadioButton radioButtonnoParking;

    @FXML
    private RadioButton radioButtonyesParking;

    @FXML
    private TextField textfieldAddress;

    @FXML
    private TextField textfieldArea;

    @FXML
    private ChoiceBox<PropertyBusinessType> choiceboxBusinessType;

    @FXML
    private TextField textfieldprice;

    @FXML
    private ImageView propertyImage;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button removeImageButton;

    private File selectedImageFile;

    private Account account = SessionManager.getCurrentAccount();

    private HostController hostController = new HostController();
    private CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
    private PropertyController propertyController = new PropertyController();
    private PropertyImageController propertyImageController = new PropertyImageController();

    public void initData() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listviewHost.setDisable(true);

        // Set default status
        choiceboxStatus.getItems().addAll(PropertyStatus.values());
        choiceboxStatus.setValue(PropertyStatus.AVAILABLE);

        // Initialize business type choice box
        choiceboxBusinessType.getItems().addAll(PropertyBusinessType.values());
        choiceboxBusinessType.setValue(PropertyBusinessType.RETAIL);

        // Initialize radio buttons
        radioButtonyesParking.setToggleGroup(hasParkingInput);
        radioButtonnoParking.setToggleGroup(hasParkingInput);

        // Load hosts
        List<Host> allHosts = hostController.getAllHosts();
        List<String> hostOptions = allHosts.stream()
                .map(host -> String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()))
                .collect(Collectors.toList());
        listviewHost.setItems(FXCollections.observableArrayList(hostOptions));
        listviewHost.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    void chooseImage(ActionEvent event) {
        // Open file chooser dialog to select an image file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());

        // If a file is selected, update the UI to display the image
        if (file != null) {
            selectedImageFile = file;
            propertyImage.setVisible(true);
            propertyImage.setImage(new Image(file.toURI().toString()));
            removeImageButton.setVisible(true);
        }
    }

    @FXML
    void removeImage(ActionEvent event) {
        // Remove the selected image and hide the image display
        selectedImageFile = null;
        propertyImage.setVisible(false);
        propertyImage.setImage(null);
        removeImageButton.setVisible(false);
    }

    @FXML
    void createPropertyOwner(ActionEvent event) {
        try {
            // Validate inputs to ensure required fields are filled
            if (textfieldprice.getText().isEmpty() || textfieldAddress.getText().isEmpty() ||
                    choiceboxBusinessType.getValue() == null || textfieldArea.getText().isEmpty()) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please fill in all required fields");
                return;
            }

            // Parse input values
            double price = Double.parseDouble(textfieldprice.getText());
            String address = textfieldAddress.getText();
            PropertyStatus status = choiceboxStatus.getValue();
            PropertyBusinessType businessType = choiceboxBusinessType.getValue();
            double area = Double.parseDouble(textfieldArea.getText());
            boolean hasParking = radioButtonyesParking.isSelected();

            int ownerId = account.getAccountId();

            // Create property request and save property
            CreateCommercialPropertyRequest request = new CreateCommercialPropertyRequest(
                    price, address, status, ownerId, businessType, area, hasParking);
            CommercialProperty property = commercialPropertyController.createCommercialProperty(request);

            // Set selected hosts for the property
            List<Integer> selectedHostIds = new ArrayList<>();
            for (String hostSelection : listviewHost.getSelectionModel().getSelectedItems()) {
                int hostId = Integer.parseInt(
                        hostSelection.substring(hostSelection.lastIndexOf("ID: ") + 4, hostSelection.length() - 1));
                selectedHostIds.add(hostId);
            }
            if (!selectedHostIds.isEmpty()) {
                propertyController.setHostIds(property.getPropertyId(), selectedHostIds);
            }

            // Upload image if selected
            if (selectedImageFile != null) {
                String imageUrl = Storage.uploadFile(selectedImageFile, "uploads", "");
                CreatePropertyImageRequest propertyImageRequest = new CreatePropertyImageRequest(imageUrl,
                        property.getPropertyId());
                propertyImageController.createPropertyImage(propertyImageRequest);
            }

            // Show success message and return to home view
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Commercial property created successfully");
            returnHome();

        } catch (NumberFormatException e) {
            // Show error if numeric input is invalid
            CustomAlert.show(Alert.AlertType.ERROR, "Input Error",
                    "Please enter valid numeric values for price and area");
        } catch (Exception e) {
            // Show error for general failure
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to create property: " + e.getMessage());
        }
    }

    @FXML
    void returnHome() {
        try {
            // Load the "OwnerAllPropertyView" and display all properties
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerAllPropertyView.fxml"));
            Parent root = loader.load();
            OwnerAllPropertyView view = loader.getController();
            view.showAllProperties(FXCollections.observableArrayList(propertyController.getPropertiesByOwnerId(account.getAccountId())));

            // Set the loaded view as the main scene
            Scene currentScene = buttonReturnHome.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Log error if loading view fails
            e.printStackTrace();
        }
    }

}