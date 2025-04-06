package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.CommercialPropertyController;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Enum.PropertyBusinessType;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.request.CreatePropertyImageRequest;
import com.trustmejunior.request.UpdateCommercialPropertyRequest;
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

public class OwnerEditCommercialPropertyView implements Initializable {
    @FXML
    private Button buttonReturnHome;

    @FXML
    private Button buttonUpdate;

    @FXML
    private ChoiceBox<PropertyStatus> choiceBoxStatus;

    @FXML
    private ListView<String> listviewHost;

    @FXML
    private RadioButton radioButtonNoParking;

    @FXML
    private RadioButton radioButtonYesParking;

    @FXML
    private ToggleGroup hasParkingInput;

    @FXML
    private TextField textfieldAddress;

    @FXML
    private TextField textfieldArea;

    @FXML
    private ChoiceBox<PropertyBusinessType> choiceBoxBusinessType;

    @FXML
    private TextField textfieldPrice;

    @FXML
    private ImageView propertyImage;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button removeImageButton;

    private File selectedImageFile;
    private PropertyImage currentImage;

    private Account account = SessionManager.getCurrentAccount();

    private HostController hostController = new HostController();
    private CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
    private PropertyController propertyController = new PropertyController();
    private PropertyImageController propertyImageController = new PropertyImageController();
    private CommercialProperty currentProperty;

    public void initData(CommercialProperty property) {
        this.currentProperty = property;

        // Set current values
        textfieldPrice.setText(String.valueOf(property.getPrice()));
        textfieldAddress.setText(property.getAddress());
        choiceBoxStatus.setValue(property.getStatus());
        choiceBoxBusinessType.setValue(property.getBusinessType());
        textfieldArea.setText(String.valueOf(property.getArea()));

        // Set parking selection
        if (property.isHasParking()) {
            radioButtonYesParking.setSelected(true);
        } else {
            radioButtonNoParking.setSelected(true);
        }

        // Set current hosts
        List<Host> currentHosts = hostController.getHostsByPropertyId(property.getPropertyId());
        List<String> currentHostOptions = currentHosts.stream()
                .map(host -> String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()))
                .collect(Collectors.toList());
        for (String hostOption : listviewHost.getItems()) {
            if (currentHostOptions.contains(hostOption)) {
                listviewHost.getSelectionModel().select(hostOption);
            }
        }

        // Load current image if exists
        currentImage = propertyImageController.getPropertyImageByPropertyId(property.getPropertyId());
        if (currentImage != null) {
            propertyImage.setVisible(true);
            propertyImage.setImage(new Image(currentImage.getUrl()));
            removeImageButton.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listviewHost.setDisable(true);

        // Set default status
        choiceBoxStatus.getItems().addAll(PropertyStatus.values());
        choiceBoxStatus.setValue(PropertyStatus.AVAILABLE);

        // Initialize business type choice box
        choiceBoxBusinessType.getItems().addAll(PropertyBusinessType.values());
        choiceBoxBusinessType.setValue(PropertyBusinessType.RETAIL);

        // Initialize radio buttons
        radioButtonYesParking.setToggleGroup(hasParkingInput);
        radioButtonNoParking.setToggleGroup(hasParkingInput);

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
    void updateProperty(ActionEvent event) {
        try {
            // Validate inputs to ensure required fields are filled
            if (textfieldPrice.getText().isEmpty() || textfieldAddress.getText().isEmpty() ||
                    choiceBoxBusinessType.getValue() == null || textfieldArea.getText().isEmpty()) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please fill in all required fields");
                return;
            }

            // Parse input values
            double price = Double.parseDouble(textfieldPrice.getText());
            String address = textfieldAddress.getText();
            PropertyStatus status = choiceBoxStatus.getValue();
            PropertyBusinessType businessType = choiceBoxBusinessType.getValue();
            double area = Double.parseDouble(textfieldArea.getText());
            boolean hasParking = radioButtonYesParking.isSelected();

            int ownerId = account.getAccountId();

            // Create update request and update the property
            UpdateCommercialPropertyRequest request = new UpdateCommercialPropertyRequest(
                    price, address, status, ownerId, businessType, area, hasParking);
            commercialPropertyController.updateCommercialProperty(currentProperty.getPropertyId(), request);

            // Update selected hosts for the property
            List<Integer> selectedHostIds = new ArrayList<>();
            for (String hostSelection : listviewHost.getSelectionModel().getSelectedItems()) {
                int hostId = Integer.parseInt(
                        hostSelection.substring(hostSelection.lastIndexOf("ID: ") + 4, hostSelection.length() - 1));
                selectedHostIds.add(hostId);
            }
            propertyController.setHostIds(currentProperty.getPropertyId(), selectedHostIds);

            // Update property image if a new one is selected
            if (selectedImageFile != null) {
                // Delete current image if exists
                if (currentImage != null) {
                    Storage.deleteFile(currentImage.getUrl());
                    propertyImageController.deletePropertyImageById(currentImage.getImageId());
                }

                // Upload new image and associate it with the property
                String imageUrl = Storage.uploadFile(selectedImageFile, "uploads", "");
                CreatePropertyImageRequest propertyImageRequest = new CreatePropertyImageRequest(imageUrl,
                        currentProperty.getPropertyId());
                propertyImageController.createPropertyImage(propertyImageRequest);
            }

            // Show success message and return to home view
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Commercial property updated successfully");
            returnHome();

        } catch (NumberFormatException e) {
            // Show error if numeric input is invalid
            CustomAlert.show(Alert.AlertType.ERROR, "Input Error",
                    "Please enter valid numeric values for price and area");
        } catch (Exception e) {
            // Show error for general failure
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to update property: " + e.getMessage());
        }
    }

    @FXML
    void returnHome() {
        try {
            // Load the "OwnerAllPropertyView" and display the properties owned by the
            // current account
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerAllPropertyView.fxml"));
            Parent root = loader.load();
            OwnerAllPropertyView view = loader.getController();
            view.showAllProperties(FXCollections
                    .observableArrayList(propertyController.getPropertiesByOwnerId(account.getAccountId())));

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