package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.trustmejunior.controller.CommercialPropertyController;
import com.trustmejunior.controller.OwnerController;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class HostAllPropertyView {
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
    private Button viewPropertyButton;

    @FXML
    private Button editPropertyButton;

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
        // Get user input values for filters
        final String searchQuery = textfieldsearch.getText().toLowerCase();
        final String selectedType;
        final String selectedCriteria = droplistPropertyCriteria.getValue();
        final String value = droplistPropertyValue.getValue();
        final String selectedPrice = droplistPrice.getValue();
        final String selectedOwner = droplistOwnerId.getValue();

        // Determine property type
        if ("Residential".equals(droplistPropertyType.getValue())) {
            selectedType = "RESIDENTIAL_PROPERTY";
        } else if ("Commercial".equals(droplistPropertyType.getValue())) {
            selectedType = "COMMERCIAL_PROPERTY";
        } else {
            selectedType = null;
        }

        // Set the filtering predicate for the list
        filteredList.setPredicate(property -> {
            // Check if property matches the search query
            if (!searchQuery.isEmpty() &&
                    !property.getAddress().toLowerCase().contains(searchQuery) &&
                    !String.valueOf(property.getPrice()).contains(searchQuery) &&
                    !property.getStatus().toString().toLowerCase().contains(searchQuery) &&
                    !property.getType().toString().toLowerCase().contains(searchQuery)) {
                return false;
            }

            // Filter by price range if selected
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $10,000") && property.getPrice() >= 10000) {
                        return false;
                    } else if (selectedPrice.equals("Over $100,000") && property.getPrice() <= 100000) {
                        return false;
                    } else {
                        // Handle custom price range
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

            // Filter by owner ID if selected
            if (selectedOwner != null) {
                try {
                    int ownerId = Integer.parseInt(selectedOwner.substring(selectedOwner.indexOf("ID: ") + 4, selectedOwner.indexOf(")")));
                    if (property.getOwnerId() != ownerId) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid owner ID format: " + selectedOwner);
                    return false;
                }
            }

            // Filter by property type if selected
            if (selectedType != null && !selectedType.equalsIgnoreCase(property.getType().toString())) {
                return false;
            }

            // Ensure criteria has a value before applying filter
            if (selectedCriteria != null && value == null) {
                return false;
            }

            // Apply specific criteria for commercial or residential properties
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
                                                        && commercialProperty.getArea() <= (maxArea);
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

            return true; // Return true if all filters pass
        });
    }

    public void showAllProperties(ObservableList<Property> properties) {
        propertyList = FXCollections.observableArrayList(properties);

        // Create a filtered list of properties with initial condition allowing all
        // properties
        FilteredList<Property> filteredData = new FilteredList<>(propertyList, p -> true);

        // Add listeners to apply filters when text or dropdown values change
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

        // Bind sorted data to TableView and display
        SortedList<Property> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableAllPropertyManagerView.comparatorProperty());
        TableAllPropertyManagerView.setItems(sortedData);

        // Prepare and populate owner dropdown with formatted owner information
        Set<String> ownerIds = new HashSet<>();
        for (Property p : propertyList) {
            String formattedItem = String.format("%s (ID: %d)",
                    ownerController.getOwnerByAccountId(p.getOwnerId()).getFullName(),
                    ownerController.getOwnerByAccountId(p.getOwnerId()).getAccountId());
            ownerIds.add(formattedItem);
        }
        droplistOwnerId.getItems().addAll(ownerIds);
    }

    @FXML
    public void viewProperty() {
        // Get selected property from the table
        Property selectedProperty = TableAllPropertyManagerView.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            // Show error if no property is selected
            CustomAlert.show(Alert.AlertType.ERROR, "No property selected", "Please select a property to view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            PropertyType propertyType = selectedProperty.getType();

            // Load appropriate view based on property type
            switch (propertyType) {
                case COMMERCIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/HostCommercialPropertyView.fxml"));
                    root = loader.load();
                    HostCommercialPropertyView view = loader.getController();
                    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
                    CommercialProperty commercialProperty = commercialPropertyController
                            .getCommercialPropertyById(selectedProperty.getPropertyId());
                    view.initData(commercialProperty);
                }
                case RESIDENTIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/HostResidentialPropertyView.fxml"));
                    root = loader.load();
                    HostResidentialPropertyView view = loader.getController();
                    ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
                    ResidentialProperty residentialProperty = residentialPropertyController
                            .getResidentialPropertyById(selectedProperty.getPropertyId());
                    view.initData(residentialProperty);
                }
            }

            // If view is loaded, set it as the main content in the current scene
            if (root != null) {
                Scene currentScene = viewPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace in case of error
        }
    }

    @FXML
    public void editProperty() {
        // Get the selected property from the table
        Property selectedProperty = TableAllPropertyManagerView.getSelectionModel().getSelectedItem();

        // If no property is selected, show an error message
        if (selectedProperty == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No property selected", "Please select a property to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            PropertyType propertyType = selectedProperty.getType(); // Get the type of the selected property

            // Load the appropriate view based on the property type
            switch (propertyType) {
                case COMMERCIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/HostEditCommercialPropertyView.fxml"));
                    root = loader.load();
                    HostEditCommercialPropertyView view = loader.getController();
                    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
                    CommercialProperty commercialProperty = commercialPropertyController
                            .getCommercialPropertyById(selectedProperty.getPropertyId());
                    view.initData(commercialProperty);
                }
                case RESIDENTIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/HostEditResidentialPropertyView.fxml"));
                    root = loader.load();
                    HostEditResidentialPropertyView view = loader.getController();
                    ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
                    ResidentialProperty residentialProperty = residentialPropertyController
                            .getResidentialPropertyById(selectedProperty.getPropertyId());
                    view.initData(residentialProperty);
                }
            }

            // Set the new scene if the root is not null
            if (root != null) {
                Scene currentScene = editPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log the error if the loading fails
        }
    }

    @FXML
    void handleViewAll(ActionEvent event) {
        // Clear the search text field and reset all dropdown selections
        textfieldsearch.clear();
        droplistPropertyType.getSelectionModel().clearSelection();
        droplistPropertyCriteria.getSelectionModel().clearSelection();
        droplistPropertyValue.getSelectionModel().clearSelection();
        droplistPrice.getSelectionModel().clearSelection();
        droplistOwnerId.getSelectionModel().clearSelection();
    }

    @FXML
    public void initialize() {
        // Initialize table columns to display property information
        id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPropertyId()).asObject());
        price.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        address.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        type.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getType()));
        owner_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getOwnerId()).asObject());

        // Populate property types dropdown with options
        droplistPropertyType.getItems().addAll("Residential", "Commercial");

        // Dynamically update criteria dropdown based on selected property type
        droplistPropertyType.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyCriteria.getItems().clear();
            if ("Residential".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Number of Bedrooms", "Has Garden", "Is Pet Friendly");
            } else if ("Commercial".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Business Type", "Area", "Has Parking");
            }
        });

        // Dynamically update value dropdown based on selected criteria
        droplistPropertyCriteria.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyValue.getItems().clear(); // Clear previous values

            // Add relevant options based on the selected criteria
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

        // Add price range options to the price dropdown
        droplistPrice.getItems().add("Under $10,000");

        // Add ranges from $10,000 to $99,999 in $10,000 increments
        for (int start = 10000; start < 100000; start += 10000) {
            int end = start + 9999; // Calculate the upper bound of the range
            String range = String.format("$%d - $%d", start, end);
            droplistPrice.getItems().add(range);
        }

        // Add option for prices over $100,000
        droplistPrice.getItems().add("Over $100,000");
    }
}
