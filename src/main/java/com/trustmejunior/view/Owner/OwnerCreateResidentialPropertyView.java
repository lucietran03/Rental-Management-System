package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.*;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.request.CreateResidentialPropertyRequest;
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

public class OwnerCreateResidentialPropertyView implements Initializable {
    @FXML
    private RadioButton RadioButtonNoPet;

    @FXML
    private RadioButton RadioButtonNogarden;

    @FXML
    private RadioButton RadioButtonYesGarden;

    @FXML
    private RadioButton RadioButtonYesPet;

    @FXML
    private Button buttonCreatePropertyResidential;

    @FXML
    private Button buttonReturnHome;

    @FXML
    private ChoiceBox<PropertyStatus> choiceboxStatus;

    @FXML
    private ListView<String> listviewHost;

    @FXML
    private TextField textfieldAddress;

    @FXML
    private TextField textfieldAread;

    @FXML
    private TextField textfieldNumberofBedroom;

    @FXML
    private TextField textfieldPrice;

    @FXML
    private ToggleGroup hasGardenInput = new ToggleGroup();

    @FXML
    private ToggleGroup isPetFriendlyInput = new ToggleGroup();

    @FXML
    private ImageView propertyImage;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button removeImageButton;

    private File selectedImageFile;

    private Account account = SessionManager.getCurrentAccount();
    private HostController hostController = new HostController();
    private ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
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

        // Initialize radio buttons for garden
        RadioButtonYesGarden.setToggleGroup(hasGardenInput);
        RadioButtonNogarden.setToggleGroup(hasGardenInput);

        // Initialize radio buttons for pet friendly
        RadioButtonYesPet.setToggleGroup(isPetFriendlyInput);
        RadioButtonNoPet.setToggleGroup(isPetFriendlyInput);

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
        // Open file chooser to select an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file; // Save selected file
            propertyImage.setVisible(true); // Show image preview
            propertyImage.setImage(new Image(file.toURI().toString())); // Set image preview
            removeImageButton.setVisible(true); // Show remove image button
        }
    }

    @FXML
    void removeImage(ActionEvent event) {
        // Remove selected image
        selectedImageFile = null; // Clear selected file
        propertyImage.setVisible(false); // Hide image preview
        propertyImage.setImage(null); // Remove image
        removeImageButton.setVisible(false); // Hide remove button
    }

    @FXML
    void createResidentialProperty(ActionEvent event) {
        try {
            // Validate required fields
            if (textfieldPrice.getText().isEmpty() || textfieldAddress.getText().isEmpty() ||
                    textfieldNumberofBedroom.getText().isEmpty()) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please fill in all required fields");
                return;
            }

            // Parse input values
            double price = Double.parseDouble(textfieldPrice.getText());
            String address = textfieldAddress.getText();
            PropertyStatus status = choiceboxStatus.getValue();
            int numberOfBedrooms = Integer.parseInt(textfieldNumberofBedroom.getText());
            boolean hasGarden = RadioButtonYesGarden.isSelected();
            boolean isPetFriendly = RadioButtonYesPet.isSelected();

            int ownerId = account.getAccountId();

            // Create residential property request
            CreateResidentialPropertyRequest request = new CreateResidentialPropertyRequest(
                    price, address, status, ownerId, numberOfBedrooms, hasGarden, isPetFriendly);

            // Create residential property
            ResidentialProperty property = residentialPropertyController.createResidentialProperty(request);

            // Assign hosts to property if selected
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

            // Show success alert and return to home
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Residential property created successfully");
            returnHome();

        } catch (NumberFormatException e) {
            // Handle invalid number input
            CustomAlert.show(Alert.AlertType.ERROR, "Input Error",
                    "Please enter valid numeric values for price and number of bedrooms");
        } catch (Exception e) {
            // Handle other errors
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to create property: " + e.getMessage());
        }
    }

    @FXML
    void returnHome() {
        try {
            // Load and display the OwnerAllPropertyView
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/OwnerAllPropertyView.fxml"));
            Parent root = loader.load();
            OwnerAllPropertyView view = loader.getController();
            // Show all properties for the current owner
            view.showAllProperties(FXCollections.observableArrayList(propertyController.getPropertiesByOwnerId(account.getAccountId())));

            // Update the main layout
            Scene currentScene = buttonReturnHome.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle loading error
        }
    }

}