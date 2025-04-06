package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.trustmejunior.controller.CommercialPropertyController;
import com.trustmejunior.controller.OwnerController;
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
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class ManagerAllPropertyView {
    @FXML
    private ImageView ManagerLogoImageView;

    @FXML
    private TextField textfieldsearch;

    @FXML
    private TableView<Property> TableAllPropertyManagerView;

    @FXML
    private TableColumn<Property, Integer> id;

    @FXML
    private TableColumn<Property, Double> price;

    @FXML
    private TableColumn<Property, String> address;

    @FXML
    private TableColumn<Property, PropertyStatus> status;

    @FXML
    private TableColumn<Property, PropertyType> type;

    @FXML
    private TableColumn<Property, Integer> owner_id;

    @FXML
    private Button createPropertyButton;

    @FXML
    private Button viewPropertyButton;

    @FXML
    private Button editPropertyButton;

    @FXML
    private Button deletePropertyButton;

    @FXML
    private ContextMenu contextMenuCreateProperty;

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
    private ComboBox<String> droplistOwnerId;

    @FXML
    private ComboBox<String> droplistPrice;

    private ObservableList<Property> propertyList;
    private Account account = SessionManager.getCurrentAccount();

    private OwnerController ownerController = new OwnerController();

    public void initData() {

    }

    private void applyFilters(FilteredList<Property> filteredList) {
        // Get user input values from the search field and dropdowns
        final String searchQuery = textfieldsearch.getText().toLowerCase();
        final String selectedType;
        final String selectedCriteria = droplistPropertyCriteria.getValue();
        final String value = droplistPropertyValue.getValue();
        final String selectedPrice = droplistPrice.getValue();
        final String selectedOwner = droplistOwnerId.getValue();

        // Determine property type (Residential or Commercial)
        if ("Residential".equals(droplistPropertyType.getValue())) {
            selectedType = "RESIDENTIAL_PROPERTY";
        } else if ("Commercial".equals(droplistPropertyType.getValue())) {
            selectedType = "COMMERCIAL_PROPERTY";
        } else {
            selectedType = null;
        }

        // Set the filter conditions based on the selected values
        filteredList.setPredicate(property -> {
            // Filter based on the search query (address, price, status, type)
            if (!searchQuery.isEmpty() &&
                    !property.getAddress().toLowerCase().contains(searchQuery) &&
                    !String.valueOf(property.getPrice()).contains(searchQuery) &&
                    !property.getStatus().toString().toLowerCase().contains(searchQuery) &&
                    !property.getType().toString().toLowerCase().contains(searchQuery)) {
                return false;
            }

            // Price range filter
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $10,000")) {
                        if (property.getPrice() >= 10000) {
                            return false;
                        }
                    } else if (selectedPrice.equals("Over $100,000")) {
                        if (property.getPrice() <= 100000) {
                            return false;
                        }
                    } else {
                        // Handle custom price ranges
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

            // Filter based on owner ID
            if (selectedOwner != null) {
                try {
                    int ownerId = Integer.parseInt(
                            selectedOwner.substring(selectedOwner.lastIndexOf("ID: ") + 4, selectedOwner.length() - 1));
                    if (property.getOwnerId() != ownerId) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid owner ID format: " + selectedOwner);
                    return false;
                }
            }

            // Filter based on property type (Residential or Commercial)
            if (selectedType != null && !selectedType.equalsIgnoreCase(property.getType().toString())) {
                return false;
            }

            // Check if a property criteria is selected but no value is provided
            if (selectedCriteria != null && value == null) {
                return false;
            }

            // Additional filters based on property type (Residential or Commercial)
            if (selectedType != null) {
                switch (selectedType) {
                    case "COMMERCIAL_PROPERTY":
                        CommercialPropertyController commercialController = new CommercialPropertyController();
                        CommercialProperty commercialProperty = commercialController
                                .getCommercialPropertyById(property.getPropertyId());
                        if (selectedCriteria != null) {
                            switch (selectedCriteria) {
                                case "Business Type":
                                    return commercialProperty != null
                                            && commercialProperty.getBusinessType().toString().equalsIgnoreCase(value);
                                case "Area":
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
                                    return commercialProperty != null
                                            && commercialProperty.isHasParking() == Boolean.parseBoolean(value);
                            }
                        }
                        break;
                    case "RESIDENTIAL_PROPERTY":
                        ResidentialPropertyController residentialController = new ResidentialPropertyController();
                        ResidentialProperty residentialProperty = residentialController
                                .getResidentialPropertyById(property.getPropertyId());
                        if (selectedCriteria != null) {
                            switch (selectedCriteria) {
                                case "Number of Bedrooms":
                                    return residentialProperty != null
                                            && residentialProperty.getNumberOfBedrooms() == Integer.parseInt(value);
                                case "Has Garden":
                                    return residentialProperty != null
                                            && residentialProperty.isHasGarden() == Boolean.parseBoolean(value);
                                case "Is Pet Friendly":
                                    return residentialProperty != null
                                            && residentialProperty.isPetFriendly() == Boolean.parseBoolean(value);
                            }
                        }
                        break;
                }
            }

            return true; // Include property if all filters pass
        });
    }

    // Method to display all properties with filters and sorting
    public void showAllProperties(ObservableList<Property> properties) {
        propertyList = FXCollections.observableArrayList(properties);

        // Initialize filtered data
        FilteredList<Property> filteredData = new FilteredList<>(propertyList, p -> true);

        // Add listeners for search field and dropdowns to apply filters
        textfieldsearch.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistPropertyType.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistPropertyCriteria.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistPropertyValue.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistPrice.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistOwnerId.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Sort filtered data
        SortedList<Property> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableAllPropertyManagerView.comparatorProperty());
        TableAllPropertyManagerView.setItems(sortedData);

        // Populate owner IDs dropdown
        Set<String> ownerIds = new HashSet<>();
        for (Property p : propertyList) {
            String formattedItem = String.format("%s (ID: %d)",
                    ownerController.getOwnerByAccountId(p.getOwnerId()).getFullName(),
                    ownerController.getOwnerByAccountId(p.getOwnerId()).getAccountId());
            ownerIds.add(formattedItem);
        }
        droplistOwnerId.getItems().addAll(ownerIds);
    }

    // Method to reset all filters and clear search field
    @FXML
    void handleViewAll(ActionEvent event) {
        textfieldsearch.clear();
        droplistPropertyType.getSelectionModel().clearSelection();
        droplistPropertyCriteria.getSelectionModel().clearSelection();
        droplistPropertyValue.getSelectionModel().clearSelection();
        droplistPrice.getSelectionModel().clearSelection();
        droplistOwnerId.getSelectionModel().clearSelection();
    }

    // Method to show create property context menu
    @FXML
    public void createProperty() {
        contextMenuCreateProperty.show(createPropertyButton, Side.BOTTOM, 0, 0);
    }

    // Method to view selected property details
    @FXML
    public void viewProperty() {
        Property selectedProperty = TableAllPropertyManagerView.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No property selected", "Please select a property to view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            PropertyType propertyType = selectedProperty.getType();

            // Load respective view based on property type
            switch (propertyType) {
                case COMMERCIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/ManagerCommercialPropertyView.fxml"));
                    root = loader.load();
                    ManagerCommercialPropertyView view = loader.getController();
                    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
                    CommercialProperty commercialProperty = commercialPropertyController
                            .getCommercialPropertyById(selectedProperty.getPropertyId());
                    view.initData(commercialProperty);
                }
                case RESIDENTIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/ManagerResidentialPropertyView.fxml"));
                    root = loader.load();
                    ManagerResidentialPropertyView view = loader.getController();
                    ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
                    ResidentialProperty residentialProperty = residentialPropertyController
                            .getResidentialPropertyById(selectedProperty.getPropertyId());
                    view.initData(residentialProperty);
                }
            }

            // Display the loaded view
            if (root != null) {
                Scene currentScene = viewPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to edit selected property
    @FXML
    public void editProperty() {
        Property selectedProperty = TableAllPropertyManagerView.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            CustomAlert.show(Alert.AlertType.WARNING, "No Property Selected", "Please select a property to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            PropertyType propertyType = selectedProperty.getType();

            // Load respective edit view based on property type
            switch (propertyType) {
                case COMMERCIAL_PROPERTY -> {
                    loader.setLocation(getClass()
                            .getResource("/com/trustmejunior/view/ManagerEditCommercialPropertyView.fxml"));
                    root = loader.load();
                    ManagerEditCommercialPropertyView view = loader.getController();
                    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
                    CommercialProperty commercialProperty = commercialPropertyController
                            .getCommercialPropertyById(selectedProperty.getPropertyId());
                    view.initData(commercialProperty);
                }
                case RESIDENTIAL_PROPERTY -> {
                    loader.setLocation(getClass()
                            .getResource("/com/trustmejunior/view/ManagerEditResidentialPropertyView.fxml"));
                    root = loader.load();
                    ManagerEditResidentialPropertyView view = loader.getController();
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

            // Display the loaded edit view
            if (root != null) {
                Scene currentScene = editPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteProperty() {
        Property selectedProperty = TableAllPropertyManagerView.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            // Show warning if no property is selected
            CustomAlert.show(Alert.AlertType.WARNING, "No Property Selected", "Please select a property to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this property?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            PropertyController propertyController = new PropertyController();
            // Delete the property by ID
            propertyController.deletePropertyById(selectedProperty.getPropertyId());
            propertyList.remove(selectedProperty); // Remove property from list
        }
    }

    @FXML
    public void initialize() {
        // Initialize table columns to display property details
        id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPropertyId()).asObject());
        price.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        address.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        type.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getType()));
        owner_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getOwnerId()).asObject());

        // Context menu action for creating commercial property
        buttonCommercial.setOnAction(event -> {
            contextMenuCreateProperty.hide();
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerCreateCommercialPropertyView.fxml"));
                Parent root = loader.load();
                ManagerCreateCommercialPropertyView view = loader.getController();
                view.initData();

                Scene currentScene = createPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root); // Set main view to the commercial property creation view
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Context menu action for creating residential property
        buttonResidential.setOnAction(event -> {
            contextMenuCreateProperty.hide();
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerCreateResidentialPropertyView.fxml"));
                Parent root = loader.load();
                ManagerCreateResidentialPropertyView view = loader.getController();
                view.initData();

                Scene currentScene = createPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root); // Set main view to the residential property creation view
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Populate property type dropdown
        droplistPropertyType.getItems().addAll("Residential", "Commercial");

        // Update criteria options dynamically based on property type
        droplistPropertyType.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyCriteria.getItems().clear();
            if ("Residential".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Number of Bedrooms", "Has Garden", "Is Pet Friendly");
            } else if ("Commercial".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Business Type", "Area", "Has Parking");
            }
        });

        // Update value options dynamically based on selected criteria
        droplistPropertyCriteria.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyValue.getItems().clear(); // Clear previous value options

            // Add values based on selected criteria
            if ("Number of Bedrooms".equals(newVal)) {
                for (int i = 1; i <= 20; i++) {
                    droplistPropertyValue.getItems().add(String.valueOf(i));
                }
            } else if ("Has Garden".equals(newVal) || "Is Pet Friendly".equals(newVal)) {
                droplistPropertyValue.getItems().addAll("True", "False");
            } else if ("Business Type".equals(newVal)) {
                droplistPropertyValue.getItems().addAll(
                        "RETAIL", "OFFICE", "INDUSTRIAL", "RESTAURANT_CAFE", "HEALTHCARE", "HOTEL", "ENTERTAINMENT",
                        "EDUCATION", "STORAGE");
            } else if ("Area".equals(newVal)) {
                droplistPropertyValue.getItems().addAll(
                        "100 - 200m²", "201 - 500m²", "501 - 1000m²", "1001 - 2000m²", "2001 - 3000m²",
                        "3001 - 4000m²", "4001 - 5000m²", "5001 - 6000m²", "6001 - 8000m²", "8001 - 10000m²");
            } else if ("Has Parking".equals(newVal)) {
                droplistPropertyValue.getItems().addAll("True", "False");
            }
        });

        // Add predefined price options to the price dropdown
        droplistPrice.getItems().add("Under $10,000");

        // Add price range options in increments of $10,000 up to $99,999
        for (int start = 10000; start < 100000; start += 10000) {
            int end = start + 9999;
            String range = String.format("$%d - $%d", start, end);
            droplistPrice.getItems().add(range);
        }

        // Add option for "Over $100,000" to the price dropdown
        droplistPrice.getItems().add("Over $100,000");
    }
}
