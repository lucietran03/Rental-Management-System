package com.trustmejunior.view.Manager;

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

public class ManagerCreateCommercialPropertyView implements Initializable {
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
    private Button createCommercialPropertyButton;

    @FXML
    private Button returnHomeButton;

    private File selectedImageFile;

    private Account account = SessionManager.getCurrentAccount();

    private AccountController accountController = new AccountController();
    private HostController hostController = new HostController();
    private CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
    private PropertyController propertyController = new PropertyController();
    private PropertyImageController propertyImageController = new PropertyImageController();

    public void initData() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set default status
        statusInput.getItems().addAll(PropertyStatus.values());
        statusInput.setValue(PropertyStatus.AVAILABLE);

        businessTypeInput.getItems().addAll(PropertyBusinessType.values());
        businessTypeInput.setValue(PropertyBusinessType.RETAIL);

        // Initialize radio buttons
        hasParkingYes.setToggleGroup(hasParkingInput);
        hasParkingNo.setToggleGroup(hasParkingInput);

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
        // Opens a file chooser to select an image file (PNG, JPG, JPEG)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());

        // If an image is selected, display it and enable remove button
        if (file != null) {
            selectedImageFile = file;
            propertyImage.setVisible(true);
            propertyImage.setImage(new Image(file.toURI().toString()));
            removeImageButton.setVisible(true);
        }
    }

    @FXML
    public void removeImage(ActionEvent event) {
        // Clears the selected image and hides the image and remove button
        selectedImageFile = null;
        propertyImage.setVisible(false);
        propertyImage.setImage(null);
        removeImageButton.setVisible(false);
    }

    @FXML
    public void createCommercialProperty(ActionEvent event) {
        try {
            // Validate form inputs
            if (priceInput.getText().isEmpty() || addressInput.getText().isEmpty() ||
                    businessTypeInput.getValue() == null || areaInput.getText().isEmpty() ||
                    ownerInput.getValue() == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Form Error", "Please fill in all required fields");
                return;
            }

            // Parse input values
            double price = Double.parseDouble(priceInput.getText());
            String address = addressInput.getText();
            PropertyStatus status = statusInput.getValue();
            PropertyBusinessType businessType = businessTypeInput.getValue();
            double area = Double.parseDouble(areaInput.getText());
            boolean hasParking = hasParkingYes.isSelected();

            // Extract owner ID from the selection
            String ownerSelection = ownerInput.getValue();
            int ownerId = Integer.parseInt(
                    ownerSelection.substring(ownerSelection.lastIndexOf("ID: ") + 4, ownerSelection.length() - 1));

            // Create property request and send it for creation
            CreateCommercialPropertyRequest request = new CreateCommercialPropertyRequest(
                    price, address, status, ownerId, businessType, area, hasParking);

            CommercialProperty property = commercialPropertyController.createCommercialProperty(request);

            // Set selected hosts for the property
            List<Integer> selectedHostIds = new ArrayList<>();
            for (String hostSelection : hostsInput.getSelectionModel().getSelectedItems()) {
                int hostId = Integer.parseInt(
                        hostSelection.substring(hostSelection.lastIndexOf("ID: ") + 4, hostSelection.length() - 1));
                selectedHostIds.add(hostId);
            }
            if (!selectedHostIds.isEmpty()) {
                propertyController.setHostIds(property.getPropertyId(), selectedHostIds);
            }

            // Upload property image if selected
            if (selectedImageFile != null) {
                String imageUrl = Storage.uploadFile(selectedImageFile, "uploads", "");
                CreatePropertyImageRequest propertyImageRequest = new CreatePropertyImageRequest(imageUrl,
                        property.getPropertyId());
                propertyImageController.createPropertyImage(propertyImageRequest);
            }

            // Show success message and return home
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Commercial property created successfully");
            returnHome();

        } catch (NumberFormatException e) {
            // Handle invalid input for price or area
            CustomAlert.show(Alert.AlertType.ERROR, "Input Error",
                    "Please enter valid numeric values for price and area");
        } catch (Exception e) {
            // Handle any other exceptions during property creation
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to create property: " + e.getMessage());
        }
    }

    @FXML
    public void returnHome() {
        try {
            // Navigate back to the manager property view and display all properties
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllPropertyView.fxml"));
            Parent root = loader.load();
            ManagerAllPropertyView view = loader.getController();
            view.showAllProperties(FXCollections.observableArrayList(propertyController.getAllProperties()));

            // Set the main view layout
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);

        } catch (IOException e) {
            // Handle errors in loading the home view
            e.printStackTrace();
        }
    }
}
