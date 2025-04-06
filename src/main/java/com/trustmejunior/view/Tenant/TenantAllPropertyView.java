package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.*;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class TenantAllPropertyView implements Initializable {
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
    private ComboBox<String> droplistPropertyCriteria;

    @FXML
    private ComboBox<String> droplistPropertyType;

    @FXML
    private ComboBox<String> droplistPropertyValue;

    @FXML
    private Button handleViewAllButton;

    @FXML
    private Button viewPaymentListButton;

    @FXML
    private ComboBox<String> droplistOwnerId;

    @FXML
    private ComboBox<String> droplistPrice;

    private ObservableList<Property> propertyList;

    private PaymentController paymentController = new PaymentController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private TenantController tenantController = new TenantController();
    private Account account = SessionManager.getCurrentAccount();
    private OwnerController ownerController = new OwnerController();

    public void initData() {

    }

    private void applyFilters(FilteredList<Property> filteredList) {
        // Get filter values from the UI components
        final String searchQuery = textfieldsearch.getText().toLowerCase();
        final String selectedType;
        final String selectedCriteria = droplistPropertyCriteria.getValue();
        final String value = droplistPropertyValue.getValue();
        final String selectedPrice = droplistPrice.getValue();
        final String selectedOwner = droplistOwnerId.getValue();

        // Determine the property type based on the dropdown selection
        if ("Residential".equals(droplistPropertyType.getValue())) {
            selectedType = "RESIDENTIAL_PROPERTY";
        } else if ("Commercial".equals(droplistPropertyType.getValue())) {
            selectedType = "COMMERCIAL_PROPERTY";
        } else {
            selectedType = null;
        }

        // Apply the filter predicate to the filtered list
        filteredList.setPredicate(property -> {

            // Search filter: check if property matches the search query
            if (!searchQuery.isEmpty() &&
                    !property.getAddress().toLowerCase().contains(searchQuery) &&
                    !String.valueOf(property.getPrice()).contains(searchQuery) &&
                    !property.getStatus().toString().toLowerCase().contains(searchQuery) &&
                    !property.getType().toString().toLowerCase().contains(searchQuery)) {
                return false;
            }

            // Price range filter: filter properties based on selected price range
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $10,000")) {
                        if (property.getPrice() >= 10000)
                            return false;
                    } else if (selectedPrice.equals("Over $100,000")) {
                        if (property.getPrice() <= 100000)
                            return false;
                    } else {
                        // Handle price ranges like "$10,000 - $19,999"
                        String[] priceRange = selectedPrice.replace("$", "").split(" - ");
                        if (priceRange.length == 2) {
                            double minPrice = Double.parseDouble(priceRange[0].trim());
                            double maxPrice = Double.parseDouble(priceRange[1].trim());
                            if (property.getPrice() < minPrice || property.getPrice() > maxPrice)
                                return false;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price range format: " + selectedPrice);
                    return false;
                }
            }

            // Owner filter: filter properties based on selected owner
            if (selectedOwner != null) {
                try {
                    int ownerId = Integer.parseInt(selectedOwner.substring(selectedOwner.lastIndexOf("ID: ") + 4, selectedOwner.length() - 1));
                    if (property.getOwnerId() != ownerId)
                        return false;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid owner ID format: " + selectedOwner);
                    return false;
                }
            }

            // Property type filter: match the property type
            if (selectedType != null && !selectedType.equalsIgnoreCase(property.getType().toString())) {
                return false;
            }

            // Criteria filter: only apply if a value is selected
            if (selectedCriteria != null && value == null) {
                return false;
            }

            // Apply additional filters for commercial and residential properties
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

            return true;
        });
    }

    public void showAllProperties(ObservableList<Property> properties) {
        // Initialize property list and apply filters to the displayed properties
        propertyList = FXCollections.observableArrayList(properties);
        FilteredList<Property> filteredData = new FilteredList<>(propertyList, p -> true);

        // Add listeners to apply filters when input changes
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

        // Bind sorted data to the table view
        SortedList<Property> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableAllPropertyManagerView.comparatorProperty());
        TableAllPropertyManagerView.setItems(sortedData);

        // Populate owner ID dropdown with formatted owner names
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
        // Get selected property and handle if none is selected
        Property selectedProperty = TableAllPropertyManagerView.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No property selected", "Please select a property to view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            PropertyType propertyType = selectedProperty.getType();

            // Load different views based on the property type
            switch (propertyType) {
                case COMMERCIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/TenantCommercialPropertyView.fxml"));
                    root = loader.load();
                    TenantCommercialPropertyView view = loader.getController();
                    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
                    CommercialProperty commercialProperty = commercialPropertyController
                            .getCommercialPropertyById(selectedProperty.getPropertyId());
                    view.initData(commercialProperty);
                }
                case RESIDENTIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/TenantResidentialPropertyView.fxml"));
                    root = loader.load();
                    TenantResidentialPropertyView view = loader.getController();
                    ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
                    ResidentialProperty residentialProperty = residentialPropertyController
                            .getResidentialPropertyById(selectedProperty.getPropertyId());
                    view.initData(residentialProperty);
                }
            }

            // Set the loaded view in the current scene
            if (root != null) {
                Scene currentScene = viewPropertyButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewAll(ActionEvent event) {
        // Clear search and reset dropdowns
        textfieldsearch.clear();
        droplistPropertyType.getSelectionModel().clearSelection();
        droplistPropertyCriteria.getSelectionModel().clearSelection();
        droplistPropertyValue.getSelectionModel().clearSelection();
        droplistPrice.getSelectionModel().clearSelection();
        droplistOwnerId.getSelectionModel().clearSelection();
    }

    @FXML
    void viewPaymentList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantAllPaymentView.fxml"));
            Parent root = loader.load();
            TenantAllPaymentView view = loader.getController();

            // Get selected property and fetch payments
            Property property = TableAllPropertyManagerView.getSelectionModel().getSelectedItem();
            Task<List<Payment>> loadPaymentTask = new Task<>() {
                @Override
                protected List<Payment> call() throws Exception {
                    List<RentalAgreement> rentalAgreements = rentalAgreementController
                            .getRentalAgreementsByPropertyId(property.getPropertyId());
                    rentalAgreements
                            .removeIf(r -> tenantController.getMainTenantByRentalAgreementId(r.getRentalAgreementId())
                                    .getAccountId() != account.getAccountId());
                    List<Payment> payments = new ArrayList<>();
                    for (RentalAgreement r : rentalAgreements) {
                        payments.addAll(paymentController.getPaymentsByRentalAgreementId(r.getRentalAgreementId()));
                    }
                    return payments;
                }
            };

            // Handle task success and failure
            loadPaymentTask.setOnSucceeded(workerStateEvent -> {
                view.showAllPayments(FXCollections.observableArrayList(loadPaymentTask.getValue()));
                Scene currentScene = viewPaymentListButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            });

            loadPaymentTask.setOnFailed(workerStateEvent -> {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load payments");
            });

            // Start the task in a new thread
            new Thread(loadPaymentTask).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize table columns
        id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPropertyId()).asObject());
        price.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        address.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        type.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getType()));
        owner_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getOwnerId()).asObject());

        // Populate property types
        droplistPropertyType.getItems().addAll("Residential", "Commercial");

        // Update criteria dynamically based on property type
        droplistPropertyType.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyCriteria.getItems().clear();
            if ("Residential".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Number of Bedrooms", "Has Garden", "Is Pet Friendly");
            } else if ("Commercial".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Business Type", "Area", "Has Parking");
            }
        });

        // Update values dynamically based on criteria selection
        droplistPropertyCriteria.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyValue.getItems().clear(); // Clear previous values

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

        // Add "Under $10,000"
        droplistPrice.getItems().add("Under $10,000");

        // Add ranges from $10,000 to $99,999 in $10,000 increments
        for (int start = 10000; start < 100000; start += 10000) {
            int end = start + 9999; // Calculate the upper bound of the range
            String range = String.format("$%d - $%d", start, end);
            droplistPrice.getItems().add(range);
        }

        // Add "Over $100,000"
        droplistPrice.getItems().add("Over $100,000");
    }
}
