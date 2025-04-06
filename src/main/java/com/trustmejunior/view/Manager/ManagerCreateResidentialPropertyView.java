package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.PropertyImageController;
import com.trustmejunior.controller.ResidentialPropertyController;
import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.request.CreatePropertyImageRequest;
import com.trustmejunior.request.CreateResidentialPropertyRequest;
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

public class ManagerCreateResidentialPropertyView implements Initializable {
    @FXML
    private TextField priceInput;

    @FXML
    private TextField addressInput;

    @FXML
    private ChoiceBox<PropertyStatus> statusInput;

    @FXML
    private TextField numberOfBedroomsInput;

    @FXML
    private ToggleGroup hasGardenInput;

    @FXML
    private RadioButton hasGardenYes;

    @FXML
    private RadioButton hasGardenNo;

    @FXML
    private ToggleGroup isPetFriendlyInput;

    @FXML
    private RadioButton isPetFriendlyYes;

    @FXML
    private RadioButton isPetFriendlyNo;

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
    private Button createResidentialPropertyButton;

    @FXML
    private Button returnHomeButton;

    private File selectedImageFile;

    private Account account = SessionManager.getCurrentAccount();

    private AccountController accountController = new AccountController();
    private HostController hostController = new HostController();
    private ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
    private PropertyController propertyController = new PropertyController();
    private PropertyImageController propertyImageController = new PropertyImageController();

    public void initData() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set default status
        statusInput.getItems().addAll(PropertyStatus.values());
        statusInput.setValue(PropertyStatus.AVAILABLE);

        // Initialize radio buttons for garden
        hasGardenYes.setToggleGroup(hasGardenInput);
        hasGardenNo.setToggleGroup(hasGardenInput);

        // Initialize radio buttons for pet friendly
        isPetFriendlyYes.setToggleGroup(isPetFriendlyInput);
        isPetFriendlyNo.setToggleGroup(isPetFriendlyInput);

        // Load owners
        List<Account> allAccounts = accountController.getAllAccounts();
        List<String> ownerOptions = allAccounts.stream()
                .filter(account -> account.getAccountRole() == AccountRole.OWNER)
                .map(account -> String.format("%s (ID: %d)", account.getFullName(), account.getAccountId()))
                .collect(Collectors.toList());
        ownerInput.setItems(FXCollections.observableArrayList(ownerOptions));

        // Load hosts
        List<Host> allHosts = hostController.getAllHosts();
        List<String> hostOptions = allHosts.stream()
                .map(host -> String.format("%s (ID: %d)", host.getFullName(), host.getAccountId()))
                .collect(Collectors.toList());
        hostsInput.setItems(FXCollections.observableArrayList(hostOptions));
        hostsInput.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    public void chooseImage(ActionEvent event) {
        // Open file chooser to select an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (file != null) {
            // If a file is selected, display image and show remove button
            selectedImageFile = file;
            propertyImage.setVisible(true);
            propertyImage.setImage(new Image(file.toURI().toString()));
            removeImageButton.setVisible(true);
        }
    }

    @FXML
    public void removeImage(ActionEvent event) {
        // Remove selected image and hide image and remove button
        selectedImageFile = null;
        propertyImage.setVisible(false);
        propertyImage.setImage(null);
        removeImageButton.setVisible(false);
    }

    @FXML
    public void createResidentialProperty(ActionEvent event) {
        try {
            // Validate required inputs
            if (priceInput.getText().isEmpty() || addressInput.getText().isEmpty() ||
                    numberOfBedroomsInput.getText().isEmpty() || ownerInput.getValue() == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please fill in all required fields");
                return;
            }

            // Parse input values
            double price = Double.parseDouble(priceInput.getText());
            String address = addressInput.getText();
            PropertyStatus status = statusInput.getValue();
            int numberOfBedrooms = Integer.parseInt(numberOfBedroomsInput.getText());
            boolean hasGarden = hasGardenYes.isSelected();
            boolean isPetFriendly = isPetFriendlyYes.isSelected();

            // Extract owner ID from selection
            String ownerSelection = ownerInput.getValue();
            int ownerId = Integer.parseInt(
                    ownerSelection.substring(ownerSelection.lastIndexOf("ID: ") + 4, ownerSelection.length() - 1));

            // Create property request
            CreateResidentialPropertyRequest request = new CreateResidentialPropertyRequest(
                    price, address, status, ownerId, numberOfBedrooms, hasGarden, isPetFriendly);

            // Create property
            ResidentialProperty property = residentialPropertyController.createResidentialProperty(request);

            // Set hosts if selected
            List<Integer> selectedHostIds = new ArrayList<>();
            for (String hostSelection : hostsInput.getSelectionModel().getSelectedItems()) {
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

            // Show success message
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Residential property created successfully");
            returnHome();

        } catch (NumberFormatException e) {
            // Handle invalid number format input
            CustomAlert.show(Alert.AlertType.ERROR, "Input Error",
                    "Please enter valid numeric values for price and number of bedrooms");
        } catch (Exception e) {
            // Handle other errors
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to create property: " + e.getMessage());
        }
    }

    @FXML
    public void returnHome() {
        try {
            // Load and display the property management view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllPropertyView.fxml"));
            Parent root = loader.load();
            ManagerAllPropertyView view = loader.getController();
            view.showAllProperties(FXCollections.observableArrayList(propertyController.getAllProperties()));

            // Update the main view in the layout
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Handle errors when loading the view
            e.printStackTrace();
        }
    }
}
