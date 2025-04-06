package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.CommercialPropertyController;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyBusinessType;
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

public class HostEditCommercialPropertyView implements Initializable {
    @FXML
    private TextField priceInput;

    @FXML
    private TextField addressInput;

    @FXML
    private ChoiceBox<PropertyStatus> statusInput;

    @FXML
    private ChoiceBox<PropertyBusinessType> businessTypeInput;

    @FXML
    private TextField areaInput;

    @FXML
    private ToggleGroup hasParkingInput;

    @FXML
    private RadioButton hasParkingYes;

    @FXML
    private RadioButton hasParkingNo;

    @FXML
    private ComboBox<String> ownerInput;

    @FXML
    private ListView<String> hostsInput;

    @FXML
    private ImageView propertyImage;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button removeImageButton;

    @FXML
    private Button updateCommercialPropertyButton;

    @FXML
    private Button returnHomeButton;

    private File selectedImageFile;
    private PropertyImage currentImage;

    private Account account = SessionManager.getCurrentAccount();
    private CommercialProperty currentProperty;

    private AccountController accountController = new AccountController();
    private HostController hostController = new HostController();
    private CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
    private PropertyController propertyController = new PropertyController();
    private PropertyImageController propertyImageController = new PropertyImageController();

    public void initData(CommercialProperty commercialProperty) {
        this.currentProperty = commercialProperty;

        // Set the current property values to the UI fields
        priceInput.setText(String.valueOf(commercialProperty.getPrice()));
        addressInput.setText(commercialProperty.getAddress());
        statusInput.setValue(commercialProperty.getStatus());
        businessTypeInput.setValue(commercialProperty.getBusinessType());
        areaInput.setText(String.valueOf(commercialProperty.getArea()));

        // Set parking selection based on property information
        if (commercialProperty.isHasParking()) {
            hasParkingYes.setSelected(true);
        } else {
            hasParkingNo.setSelected(true);
        }

        // Set the current owner by matching ID in the owner list
        String currentOwner = ownerInput.getItems().stream()
                .filter(option -> option.endsWith("(ID: " + commercialProperty.getOwnerId() + ")"))
                .findFirst()
                .orElse(null);
        ownerInput.setValue(currentOwner);

        // Set current hosts by checking host list based on property ID
        List<Host> currentHosts = hostController.getHostsByPropertyId(commercialProperty.getPropertyId());
        List<String> currentHostOptions = currentHosts.stream()
                .map(host -> String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()))
                .collect(Collectors.toList());
        for (String hostOption : hostsInput.getItems()) {
            if (currentHostOptions.contains(hostOption)) {
                hostsInput.getSelectionModel().select(hostOption);
            }
        }

        // Load and display current image if available
        currentImage = propertyImageController.getPropertyImageByPropertyId(commercialProperty.getPropertyId());
        if (currentImage != null) {
            propertyImage.setVisible(true);
            propertyImage.setImage(new Image(currentImage.getUrl()));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Disable input fields that should not be edited
        priceInput.setDisable(true);
        addressInput.setDisable(true);
        // statusInput.setDisable(true); // Uncomment if status should also be disabled
        businessTypeInput.setDisable(true);
        areaInput.setDisable(true);
        hasParkingYes.setDisable(true);
        hasParkingNo.setDisable(true);
        ownerInput.setDisable(true);
        hostsInput.setDisable(true);
        chooseImageButton.setVisible(false);

        // Initialize status choice box with available property statuses
        statusInput.getItems().addAll(PropertyStatus.values());
        statusInput.setValue(PropertyStatus.AVAILABLE);

        // Initialize business type choice box with available business types
        businessTypeInput.getItems().addAll(PropertyBusinessType.values());
        businessTypeInput.setValue(PropertyBusinessType.OFFICE);

        // Initialize parking option radio buttons
        hasParkingYes.setToggleGroup(hasParkingInput);
        hasParkingNo.setToggleGroup(hasParkingInput);

        // Load and display owner options based on accounts with the "OWNER" role
        List<Account> allAccounts = accountController.getAllAccounts();
        List<String> ownerOptions = allAccounts.stream()
                .filter(account -> account.getAccountRole() == AccountRole.OWNER)
                .map(account -> String.format("%s (ID: %d)", account.getFullName(), account.getAccountId()))
                .collect(Collectors.toList());
        ownerInput.setItems(FXCollections.observableArrayList(ownerOptions));

        // Load and display host options
        List<Host> allHosts = hostController.getAllHosts();
        List<String> hostOptions = allHosts.stream()
                .map(host -> String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()))
                .collect(Collectors.toList());
        hostsInput.setItems(FXCollections.observableArrayList(hostOptions));
        hostsInput.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    public void chooseImage(ActionEvent event) {
        // Open a file chooser to select an image file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());

        // If a file is selected, display the image and enable the remove button
        if (file != null) {
            selectedImageFile = file;
            propertyImage.setVisible(true);
            propertyImage.setImage(new Image(file.toURI().toString()));
            removeImageButton.setVisible(true);
        }
    }

    @FXML
    public void removeImage(ActionEvent event) {
        // Remove the selected image and hide the image and remove button
        selectedImageFile = null;
        propertyImage.setVisible(false);
        propertyImage.setImage(null);
        removeImageButton.setVisible(false);
    }

    @FXML
    public void updateCommercialProperty(ActionEvent event) {
        try {
            // Validate required fields
            if (priceInput.getText().isEmpty() || addressInput.getText().isEmpty() ||
                    businessTypeInput.getValue() == null || areaInput.getText().isEmpty() ||
                    ownerInput.getValue() == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please fill in all required fields");
                return;
            }

            // Parse values from the form
            double price = Double.parseDouble(priceInput.getText());
            String address = addressInput.getText();
            PropertyStatus status = statusInput.getValue();
            PropertyBusinessType businessType = businessTypeInput.getValue();
            double area = Double.parseDouble(areaInput.getText());
            boolean hasParking = hasParkingYes.isSelected();

            // Extract owner ID from selection
            String ownerSelection = ownerInput.getValue();
            int ownerId = Integer.parseInt(
                    ownerSelection.substring(ownerSelection.lastIndexOf("ID: ") + 4, ownerSelection.length() - 1));

            // Create an update request for the property
            UpdateCommercialPropertyRequest request = new UpdateCommercialPropertyRequest(price, address, status,
                    ownerId, businessType, area, hasParking);

            // Update the property details
            commercialPropertyController.updateCommercialProperty(currentProperty.getPropertyId(), request);

            // Update selected hosts for the property
            List<Integer> selectedHostIds = new ArrayList<>();
            for (String hostSelection : hostsInput.getSelectionModel().getSelectedItems()) {
                int hostId = Integer.parseInt(
                        hostSelection.substring(hostSelection.lastIndexOf("ID: ") + 4, hostSelection.length() - 1));
                selectedHostIds.add(hostId);
            }
            propertyController.setHostIds(currentProperty.getPropertyId(), selectedHostIds);

            // If an image is selected, update the image
            if (selectedImageFile != null) {
                if (currentImage != null) {
                    // Delete old image from storage and database
                    Storage.deleteFile(currentImage.getUrl());
                    propertyImageController.deletePropertyImageById(currentImage.getImageId());
                }

                // Upload the new image to storage
                String imageUrl = Storage.uploadFile(selectedImageFile, "uploads", "");

                // Create a new image request and add it to the database
                CreatePropertyImageRequest propertyImageRequest = new CreatePropertyImageRequest(imageUrl,
                        currentProperty.getPropertyId());
                propertyImageController.createPropertyImage(propertyImageRequest);
            }

            // Show success message
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Commercial property updated successfully");
            returnHome();

        } catch (NumberFormatException e) {
            // Show error for invalid numeric inputs
            CustomAlert.show(Alert.AlertType.ERROR, "Input Error",
                    "Please enter valid numeric values for price and area");
        } catch (Exception e) {
            // Show error for general exceptions
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to update property: " + e.getMessage());
        }
    }

    @FXML
    public void returnHome() {
        try {
            // Navigate back to the HostAllPropertyView
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/HostAllPropertyView.fxml"));
            Parent root = loader.load();
            HostAllPropertyView view = loader.getController();
            view.showAllProperties(FXCollections.observableArrayList(propertyController.getPropertiesByHostId(account.getAccountId())));

            // Set the new scene
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle IOException
        }
    }
}
