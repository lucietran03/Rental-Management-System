package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.*;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Property.PropertyImage;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.request.CreatePropertyImageRequest;
import com.trustmejunior.request.UpdateResidentialPropertyRequest;
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

public class OwnerEditResidentialPropertyView implements Initializable {

    @FXML
    private RadioButton RadioButtonNoGarden;

    @FXML
    private RadioButton RadioButtonYesGarden;

    @FXML
    private Button buttonReturn;

    @FXML
    private Button buttonUpdate;

    @FXML
    private ChoiceBox<PropertyStatus> choiceBoxStatus;

    @FXML
    private ListView<String> listViewHost;

    @FXML
    private RadioButton radioButtonNoPet;

    @FXML
    private RadioButton radioButtonYesPet;

    @FXML
    private TextField textfieldAddress;

    @FXML
    private TextField textfieldNumberOfBedroom;

    @FXML
    private TextField textfieldPrice;

    @FXML
    private ToggleGroup hasGardenInput;

    @FXML
    private ToggleGroup isPetFriendlyInput;

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
    private ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
    private PropertyController propertyController = new PropertyController();
    private PropertyImageController propertyImageController = new PropertyImageController();
    private ResidentialProperty currentProperty;

    public void initData(ResidentialProperty residentialProperty) {
        this.currentProperty = residentialProperty;

        // Set current values
        textfieldPrice.setText(String.valueOf(residentialProperty.getPrice()));
        textfieldAddress.setText(residentialProperty.getAddress());
        choiceBoxStatus.setValue(residentialProperty.getStatus());
        textfieldNumberOfBedroom.setText(String.valueOf(residentialProperty.getNumberOfBedrooms()));

        // Set garden selection
        if (residentialProperty.isHasGarden()) {
            RadioButtonYesGarden.setSelected(true);
        } else {
            RadioButtonNoGarden.setSelected(true);
        }

        // Set pet friendly selection
        if (residentialProperty.isPetFriendly()) {
            radioButtonYesPet.setSelected(true);
        } else {
            radioButtonNoPet.setSelected(true);
        }

        // Set current hosts
        List<Host> currentHosts = hostController.getHostsByPropertyId(residentialProperty.getPropertyId());
        List<String> currentHostOptions = currentHosts.stream()
                .map(host -> String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()))
                .collect(Collectors.toList());
        for (String hostOption : listViewHost.getItems()) {
            if (currentHostOptions.contains(hostOption)) {
                listViewHost.getSelectionModel().select(hostOption);
            }
        }

        // Load current image if exists
        currentImage = propertyImageController.getPropertyImageByPropertyId(residentialProperty.getPropertyId());
        if (currentImage != null) {
            propertyImage.setVisible(true);
            propertyImage.setImage(new Image(currentImage.getUrl()));
            removeImageButton.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewHost.setDisable(true); // Disable list view for hosts by default

        // Set default status for property
        choiceBoxStatus.getItems().addAll(PropertyStatus.values());
        choiceBoxStatus.setValue(PropertyStatus.AVAILABLE);

        // Initialize radio buttons for garden and pet-friendly options
        RadioButtonYesGarden.setToggleGroup(hasGardenInput);
        RadioButtonNoGarden.setToggleGroup(hasGardenInput);
        radioButtonYesPet.setToggleGroup(isPetFriendlyInput);
        radioButtonNoPet.setToggleGroup(isPetFriendlyInput);

        // Load and display all hosts in the list view
        List<Host> allHosts = hostController.getAllHosts();
        List<String> hostOptions = allHosts.stream()
                .map(host -> String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()))
                .collect(Collectors.toList());
        listViewHost.setItems(FXCollections.observableArrayList(hostOptions));
        listViewHost.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    public void chooseImage(ActionEvent event) {
        // Open file chooser to select an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());

        if (file != null) { // If a file is selected, display the image
            selectedImageFile = file;
            propertyImage.setVisible(true);
            propertyImage.setImage(new Image(file.toURI().toString()));
            removeImageButton.setVisible(true);
        }
    }

    @FXML
    public void removeImage(ActionEvent event) {
        // Remove the selected image and reset the view
        selectedImageFile = null;
        propertyImage.setVisible(false);
        propertyImage.setImage(null);
        removeImageButton.setVisible(false);
    }

    @FXML
    public void updateProperty(ActionEvent event) {
        try {
            // Validate form inputs for required fields
            if (textfieldPrice.getText().isEmpty() || textfieldAddress.getText().isEmpty() ||
                    textfieldNumberOfBedroom.getText().isEmpty()) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please fill in all required fields");
                return;
            }

            // Parse values from the input fields
            double price = Double.parseDouble(textfieldPrice.getText());
            String address = textfieldAddress.getText();
            PropertyStatus status = choiceBoxStatus.getValue();
            int numberOfBedrooms = Integer.parseInt(textfieldNumberOfBedroom.getText());
            boolean hasGarden = RadioButtonYesGarden.isSelected();
            boolean isPetFriendly = radioButtonYesPet.isSelected();
            int ownerId = account.getAccountId();

            // Create update request for property
            UpdateResidentialPropertyRequest request = new UpdateResidentialPropertyRequest(
                    price, address, status, ownerId, numberOfBedrooms, hasGarden, isPetFriendly);

            // Update the property details
            residentialPropertyController.updateResidentialProperty(currentProperty.getPropertyId(), request);

            // Update selected hosts for the property
            List<Integer> selectedHostIds = new ArrayList<>();
            for (String hostSelection : listViewHost.getSelectionModel().getSelectedItems()) {
                int hostId = Integer.parseInt(
                        hostSelection.substring(hostSelection.lastIndexOf("ID: ") + 4, hostSelection.length() - 1));
                selectedHostIds.add(hostId);
            }
            propertyController.setHostIds(currentProperty.getPropertyId(), selectedHostIds);

            // Handle image upload if a new image is selected
            if (selectedImageFile != null) {
                if (currentImage != null) { // Delete old image if exists
                    Storage.deleteFile(currentImage.getUrl());
                    propertyImageController.deletePropertyImageById(currentImage.getImageId());
                }

                // Upload new image and link to property
                String imageUrl = Storage.uploadFile(selectedImageFile, "uploads", "");
                CreatePropertyImageRequest propertyImageRequest = new CreatePropertyImageRequest(imageUrl,
                        currentProperty.getPropertyId());
                propertyImageController.createPropertyImage(propertyImageRequest);
            }

            // Show success message and return to home view
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Residential property updated successfully");
            returnHome();

        } catch (NumberFormatException e) {
            // Handle invalid numeric input for price and number of bedrooms
            CustomAlert.show(Alert.AlertType.ERROR, "Input Error",
                    "Please enter valid numeric values for price and number of bedrooms");
        } catch (Exception e) {
            // Handle other exceptions
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to update property: " + e.getMessage());
        }
    }

    @FXML
    public void returnHome() {
        try {
            // Load and display the main property view for the owner
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerAllPropertyView.fxml"));
            Parent root = loader.load();
            OwnerAllPropertyView view = loader.getController();
            view.showAllProperties(FXCollections
                    .observableArrayList(propertyController.getPropertiesByOwnerId(account.getAccountId())));

            Scene currentScene = buttonReturn.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle error in loading the view
        }
    }
}
