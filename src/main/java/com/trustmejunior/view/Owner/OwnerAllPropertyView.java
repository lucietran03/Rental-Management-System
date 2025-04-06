package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.CommercialPropertyController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.ResidentialPropertyController;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OwnerAllPropertyView implements Initializable {

    @FXML
    private TableView<Property> TableOwnerView;

    @FXML
    private ContextMenu contextMenuCreateProperty;

    @FXML
    private TableColumn<Property, String> address;

    @FXML
    private Button createPropertyButton;

    @FXML
    private Button viewPropertyButton;

    @FXML
    private Button deletePropertyButton;

    @FXML
    private Button editPropertyButton;

    @FXML
    private TableColumn<Property, Integer> owner_id;

    @FXML
    private TableColumn<Property, Double> price;

    @FXML
    private TableColumn<Property, Integer> prop_id;

    @FXML
    private TableColumn<Property, PropertyStatus> status;

    @FXML
    private TableColumn<Property, PropertyType> type;

    @FXML
    private TextField textfieldsearchbar;

    @FXML
    private MenuItem buttonCommercial;

    @FXML
    private MenuItem buttonResidential;

    @FXML
    private ComboBox<String> droplistPropertyCriteria;

    @FXML
    private ComboBox<String> droplistPropertyType;

    @FXML
    private ComboBox<String> droplistPropertyValue;

    @FXML
    private Button handleViewAllButton;

    @FXML
    private ComboBox<String> droplistPrice;

    private ObservableList<Property> propertyList;

    private Account account = SessionManager.getCurrentAccount();

    public void initData() {
    }

    private void applyFilters(FilteredList<Property> filteredList) {
        final String searchQuery = textfieldsearchbar.getText().toLowerCase();
        final String selectedType;
        final String selectedCriteria = droplistPropertyCriteria.getValue();
        final String value = droplistPropertyValue.getValue();
        final String selectedPrice = droplistPrice.getValue();

        // Determine property type
        if ("Residential".equals(droplistPropertyType.getValue())) {
            selectedType = "RESIDENTIAL_PROPERTY";
        } else if ("Commercial".equals(droplistPropertyType.getValue())) {
            selectedType = "COMMERCIAL_PROPERTY";
        } else {
            selectedType = null;
        }

        // Apply filtering conditions to the property list
        filteredList.setPredicate(property -> {
            // Check if property matches the search query
            if (!searchQuery.isEmpty() &&
                    !property.getAddress().toLowerCase().contains(searchQuery) &&
                    !String.valueOf(property.getPrice()).contains(searchQuery) &&
                    !property.getStatus().toString().toLowerCase().contains(searchQuery) &&
                    !property.getType().toString().toLowerCase().contains(searchQuery)) {
                return false;
            }

            // Filter properties based on the selected price range
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $10,000")) {
                        // Properties priced below $10,000
                        if (property.getPrice() >= 10000) {
                            return false;
                        }
                    } else if (selectedPrice.equals("Over $100,000")) {
                        // Properties priced above $100,000
                        if (property.getPrice() <= 100000) {
                            return false;
                        }
                    } else {
                        // Handle specific price ranges like "$10,000 - $19,999"
                        String[] priceRange = selectedPrice.replace("$", "").split(" - ");
                        if (priceRange.length == 2) {
                            double minPrice = Double.parseDouble(priceRange[0].trim());
                            double maxPrice = Double.parseDouble(priceRange[1].trim());
                            if (property.getPrice() < minPrice || property.getPrice() > maxPrice) {
                                return false;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price range format: " + selectedPrice);
                    return false;
                }
            }

            // Filter by property type
            if (selectedType != null && !selectedType.equalsIgnoreCase(property.getType().toString())) {
                return false;
            }

            // Ensure criteria value is selected
            if (selectedCriteria != null && value == null) {
                return false;
            }

            // Apply filters based on selected property type and criteria
            if (selectedType != null) {
                switch (selectedType) {
                    case "COMMERCIAL_PROPERTY":
                        // Commercial property specific filters
                        CommercialPropertyController commercialController = new CommercialPropertyController();
                        CommercialProperty commercialProperty = commercialController
                                .getCommercialPropertyById(property.getPropertyId());
                        if (selectedCriteria != null) {
                            switch (selectedCriteria) {
                                case "Business Type":
                                    return commercialProperty != null
                                            && commercialProperty.getBusinessType().toString().equalsIgnoreCase(value);
                                case "Area":
                                    // Filter by area range for commercial properties
                                    if (commercialProperty != null && value != null) {
                                        String[] areaRange = value.split(" - ");
                                        if (areaRange.length == 2) {
                                            try {
                                                double minArea = Double
                                                        .parseDouble(areaRange[0].replace("m²", "").trim());
                                                double maxArea = Double
                                                        .parseDouble(areaRange[1].replace("m²", "").trim());
                                                return commercialProperty.getArea() >= minArea
                                                        && commercialProperty.getArea() <= maxArea;
                                            } catch (NumberFormatException e) {
                                                System.out.println("Invalid area format: " + value);
                                                return false;
                                            }
                                        }
                                    }
                                    return false;
                                case "Has Parking":
                                    // Filter by parking availability for commercial properties
                                    return commercialProperty != null
                                            && commercialProperty.isHasParking() == Boolean.parseBoolean(value);
                            }
                        }
                        break;
                    case "RESIDENTIAL_PROPERTY":
                        // Residential property specific filters
                        ResidentialPropertyController residentialController = new ResidentialPropertyController();
                        ResidentialProperty residentialProperty = residentialController
                                .getResidentialPropertyById(property.getPropertyId());
                        if (selectedCriteria != null) {
                            switch (selectedCriteria) {
                                case "Number of Bedrooms":
                                    // Filter by number of bedrooms for residential properties
                                    return residentialProperty != null
                                            && residentialProperty.getNumberOfBedrooms() == Integer.parseInt(value);
                                case "Has Garden":
                                    // Filter by garden availability for residential properties
                                    return residentialProperty != null
                                            && residentialProperty.isHasGarden() == Boolean.parseBoolean(value);
                                case "Is Pet Friendly":
                                    // Filter by pet-friendly status for residential properties
                                    return residentialProperty != null
                                            && residentialProperty.isPetFriendly() == Boolean.parseBoolean(value);
                            }
                        }
                        break;
                }
            }

            return true; // Return true if property passes all filters
        });
    }

    // Method to display all properties and apply filters
    public void showAllProperties(ObservableList<Property> properties) {
        propertyList = FXCollections.observableArrayList(properties); // Convert list of properties into an observable
                                                                      // list

        // Create a filtered list to store properties based on filters
        FilteredList<Property> filteredData = new FilteredList<>(propertyList, p -> true);

        // Add listeners to update filters when user modifies search, dropdowns, etc.
        textfieldsearchbar.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData); // Apply filters when search text changes
        });

        droplistPropertyType.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData); // Apply filters when property type changes
        });

        droplistPropertyCriteria.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData); // Apply filters when property criteria changes
        });

        droplistPropertyValue.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData); // Apply filters when property value changes
        });

        droplistPrice.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData); // Apply filters when price range changes
        });

        // Sort the filtered data based on the table's comparator and bind it to the
        // TableView
        SortedList<Property> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableOwnerView.comparatorProperty());
        TableOwnerView.setItems(sortedData); // Set sorted data to the table view
    }

    // Handler to reset filters and clear selections when the "View All" button is
    // clicked
    @FXML
    void handleViewAll(ActionEvent event) {
        textfieldsearchbar.clear(); // Clear search text
        droplistPropertyType.getSelectionModel().clearSelection(); // Clear property type selection
        droplistPropertyCriteria.getSelectionModel().clearSelection(); // Clear criteria selection
        droplistPropertyValue.getSelectionModel().clearSelection(); // Clear property value selection
        droplistPrice.getSelectionModel().clearSelection(); // Clear price selection
    }

    // Show the context menu for creating a new property when the "Create Property"
    // button is clicked
    @FXML
    public void createProperty() {
        contextMenuCreateProperty.show(createPropertyButton, Side.BOTTOM, 0, 0);
    }

    // View details of the selected property in a new scene
    @FXML
    public void viewProperty(ActionEvent event) {
        Property selectedProperty = TableOwnerView.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No property selected", "Please select a property to view.");
            return; // Exit if no property is selected
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            PropertyType propertyType = selectedProperty.getType();

            // Load different views based on the property type (Commercial or Residential)
            switch (propertyType) {
                case COMMERCIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/OwnerCommercialPropertyView.fxml"));
                    root = loader.load();
                    OwnerCommercialPropertyView view = loader.getController();
                    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
                    CommercialProperty commercialProperty = commercialPropertyController
                            .getCommercialPropertyById(selectedProperty.getPropertyId());
                    view.initData(commercialProperty);
                }
                case RESIDENTIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/OwnerResidentialPropertyView.fxml"));
                    root = loader.load();
                    OwnerResidentialPropertyView view = loader.getController();
                    ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
                    ResidentialProperty residentialProperty = residentialPropertyController
                            .getResidentialPropertyById(selectedProperty.getPropertyId());
                    view.initData(residentialProperty);
                }
            }

            // Update the current scene with the loaded property view
            if (root != null) {
                Scene currentScene = viewPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print error if view loading fails
        }
    }

    // Edit the selected property by loading the corresponding editing view
    @FXML
    public void editProperty(ActionEvent event) {
        Property selectedProperty = TableOwnerView.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            CustomAlert.show(Alert.AlertType.WARNING, "No Property Selected", "Please select a property to edit.");
            return; // Exit if no property is selected
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            PropertyType propertyType = selectedProperty.getType();

            // Load the appropriate edit view based on the property type
            switch (propertyType) {
                case COMMERCIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/OwnerEditCommercialPropertyView.fxml"));
                    root = loader.load();
                    OwnerEditCommercialPropertyView view = loader.getController();
                    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
                    CommercialProperty commercialProperty = commercialPropertyController
                            .getCommercialPropertyById(selectedProperty.getPropertyId());
                    view.initData(commercialProperty);
                }
                case RESIDENTIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/OwnerEditResidentialPropertyView.fxml"));
                    root = loader.load();
                    OwnerEditResidentialPropertyView view = loader.getController();
                    ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
                    ResidentialProperty residentialProperty = residentialPropertyController
                            .getResidentialPropertyById(selectedProperty.getPropertyId());
                    view.initData(residentialProperty);
                }
                default -> {
                    CustomAlert.show(Alert.AlertType.ERROR, "Invalid property type",
                            "Please select a valid property type.");
                }
            }

            // Update the current scene with the property edit view
            if (root != null) {
                Scene currentScene = editPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print error if edit view loading fails
        }
    }

    @FXML
    public void deleteProperty() {
        // Get selected property and show a warning if none is selected
        Property selectedProperty = TableOwnerView.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            CustomAlert.show(Alert.AlertType.WARNING, "No Property Selected", "Please select a property to delete.");
            return;
        }

        // Confirm property deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this property?");

        // Proceed with deletion if confirmed
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            PropertyController propertyController = new PropertyController();
            propertyController.deletePropertyById(selectedProperty.getPropertyId()); // Delete property by ID
            propertyList.remove(selectedProperty); // Remove property from list
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize cell value factories for property details
        prop_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getPropertyId()).asObject());
        price.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        address.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        type.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getType()));
        owner_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getOwnerId()).asObject());

        // Setup context menu for creating property views
        buttonCommercial.setOnAction(event -> {
            contextMenuCreateProperty.hide(); // Hide context menu
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/OwnerCreateCommercialPropertyView.fxml"));
                Parent root = loader.load();
                OwnerCreateCommercialPropertyView view = loader.getController();
                Scene currentScene = createPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root); // Set the main view to the new commercial property view
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buttonResidential.setOnAction(event -> {
            contextMenuCreateProperty.hide(); // Hide context menu
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/OwnerCreateResidentialPropertyView.fxml"));
                Parent root = loader.load();
                OwnerCreateResidentialPropertyView view = loader.getController();
                Scene currentScene = createPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root); // Set the main view to the new residential property view
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Populate property type dropdown with options
        droplistPropertyType.getItems().addAll("Residential", "Commercial");

        // Update criteria options dynamically based on selected property type
        droplistPropertyType.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyCriteria.getItems().clear();
            if ("Residential".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Number of Bedrooms", "Has Garden", "Is Pet Friendly");
            } else if ("Commercial".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Business Type", "Area", "Has Parking");
            }
        });

        // Update property values dynamically based on selected criteria
        droplistPropertyCriteria.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyValue.getItems().clear(); // Clear previous values
            if ("Number of Bedrooms".equals(newVal)) {
                for (int i = 1; i <= 20; i++) {
                    droplistPropertyValue.getItems().add(String.valueOf(i)); // Add number of bedrooms options
                }
            } else if ("Has Garden".equals(newVal) || "Is Pet Friendly".equals(newVal)) {
                droplistPropertyValue.getItems().addAll("True", "False"); // Add true/false options
            } else if ("Business Type".equals(newVal)) {
                droplistPropertyValue.getItems().addAll(
                        "RETAIL", "OFFICE", "INDUSTRIAL", "RESTAURANT_CAFE", "HEALTHCARE", "HOTEL", "ENTERTAINMENT",
                        "EDUCATION", "STORAGE");
            } else if ("Area".equals(newVal)) {
                droplistPropertyValue.getItems().addAll(
                        "100 - 200m²", "201 - 500m²", "501 - 1000m²", "1001 - 2000m²", "2001 - 3000m²",
                        "3001 - 4000m²", "4001 - 5000m²", "5001 - 6000m²", "6001 - 8000m²", "8001 - 10000m²");
            } else if ("Has Parking".equals(newVal)) {
                droplistPropertyValue.getItems().addAll("True", "False"); // Add true/false options
            }
        });

        // Add price range options to the dropdown
        droplistPrice.getItems().add("Under $10,000");
        for (int start = 10000; start < 100000; start += 10000) {
            int end = start + 9999; // Calculate upper bound of range
            String range = String.format("$%d - $%d", start, end);
            droplistPrice.getItems().add(range); // Add price range options
        }
        droplistPrice.getItems().add("Over $100,000");
    }
}
