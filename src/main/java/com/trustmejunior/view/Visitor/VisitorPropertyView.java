package com.trustmejunior.view.Visitor;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.CommercialPropertyController;
import com.trustmejunior.controller.ResidentialPropertyController;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.view.LoginView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.trustmejunior.model.Enum.PropertyStatus.AVAILABLE;

public class VisitorPropertyView implements Initializable {
    @FXML
    private ImageView GuestLogoImageView1;

    @FXML
    private TableView<Property> TableGuestPropertyView;

    @FXML
    private TableColumn<Property, String> address;

    @FXML
    private TableColumn<Property, Integer> id;

    @FXML
    private TableColumn<Property, Integer> owner_id;

    @FXML
    private TableColumn<Property, Double> price;

    @FXML
    private Button returnToLoginPageButton;

    @FXML
    private TableColumn<Property, PropertyStatus> status;

    @FXML
    private TextField textfieldsearch;

    @FXML
    private TableColumn<Property, PropertyType> type;

    private ObservableList<Property> propertyList;
    private FilteredList<Property> filteredList;

    @FXML
    private Button viewPropertyDetailButton;

    @FXML
    private ComboBox<String> droplistPropertyCriteria;

    @FXML
    private ComboBox<String> droplistPropertyType;

    @FXML
    private ComboBox<String> droplistPropertyValue;

    @FXML
    private Button handleViewAllButton;

    // This ensures the application navigates back to the login page.
    @FXML
    void returnToLoginPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/trustmejunior/view/LoginView.fxml"));
        Parent root = loader.load();
        LoginView view = loader.getController();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setUserData(view);
        stage.setScene(scene);
        stage.show();
    }

    // View property details logic handles both residential and commercial
    // properties.
    @FXML
    void viewPropertyDetail(ActionEvent event) {
        Property selectedProperty = TableGuestPropertyView.getSelectionModel().getSelectedItem();
        // Checks the selected property's type and loads the respective detail view.
        if (selectedProperty == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No property selected", "Please select a property to view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            PropertyType propertyType = selectedProperty.getType();

            switch (propertyType) {
                case COMMERCIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass().getResource("/com/trustmejunior/view/VisitorCommercialPropertyDetailView.fxml"));
                    root = loader.load();
                    VisitorCommercialPropertyDetailView view = loader.getController();
                    CommercialPropertyController commercialPropertyController = new CommercialPropertyController();
                    CommercialProperty commercialProperty = commercialPropertyController
                            .getCommercialPropertyById(selectedProperty.getPropertyId());
                    view.initData(commercialProperty);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    scene.setUserData(view);
                    stage.setScene(scene);
                    stage.show();
                }
                case RESIDENTIAL_PROPERTY -> {
                    loader.setLocation(
                            getClass()
                                    .getResource("/com/trustmejunior/view/VisitorResidentialPropertyDetailView.fxml"));
                    root = loader.load();
                    VisitorResidentialPropertyDetailView view = loader.getController();
                    ResidentialPropertyController residentialPropertyController = new ResidentialPropertyController();
                    ResidentialProperty residentialProperty = residentialPropertyController
                            .getResidentialPropertyById(selectedProperty.getPropertyId());
                    view.initData(residentialProperty);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    scene.setUserData(view);
                    stage.setScene(scene);
                    stage.show();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewAll(ActionEvent event) {
        filteredList.setPredicate(p -> true);
    } // Removes all filters.

    // Applies dynamic filtering on the property list based on user inputs.
    private void applyFilters() {
        final String searchQuery = textfieldsearch.getText().toLowerCase();
        final String selectedType;
        final String selectedCriteria = droplistPropertyCriteria.getValue();
        final String value = droplistPropertyValue.getValue();

        // Determine the selected property type
        if ("Residential".equals(droplistPropertyType.getValue())) {
            selectedType = "RESIDENTIAL_PROPERTY";
        } else if ("Commercial".equals(droplistPropertyType.getValue())) {
            selectedType = "COMMERCIAL_PROPERTY";
        } else {
            selectedType = null;
        }

        // Apply filters based on the search query and selected criteria
        filteredList.setPredicate(property -> {
            // Search filter: checks if the property matches the search query
            if (!searchQuery.isEmpty() &&
                    !property.getAddress().toLowerCase().contains(searchQuery) &&
                    !String.valueOf(property.getPrice()).contains(searchQuery) &&
                    !property.getStatus().toString().toLowerCase().contains(searchQuery) &&
                    !property.getType().toString().toLowerCase().contains(searchQuery)) {
                return false;
            }

            // Filter by property type
            if (selectedType != null && !selectedType.equalsIgnoreCase(property.getType().toString())) {
                return false;
            }

            // If a specific criteria is selected, validate the value
            if (selectedCriteria != null && value == null) {
                return false;
            }

            // Property type-specific filtering logic
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

            return true; // Property matches all selected filters
        });
    }

    // Initializes data and sets up bindings for TableView columns.
    private void initializeData() {

        PropertyController propertyController = new PropertyController();
        List<Property> properties = propertyController.getPropertiesByStatus(AVAILABLE);

        propertyList = FXCollections.observableArrayList(properties);
        filteredList = new FilteredList<>(propertyList, p -> true);

        TableGuestPropertyView.setItems(filteredList);

        // Bind property attributes to corresponding TableView columns.
        id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPropertyId()).asObject());
        price.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        address.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        type.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getType()));
        owner_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getOwnerId()).asObject());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeData();

        // Populate property types in dropdown.
        droplistPropertyType.getItems().addAll("Residential", "Commercial");

        // Update criteria dynamically based on selected property type.
        droplistPropertyType.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyCriteria.getItems().clear();
            if ("Residential".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Number of Bedrooms", "Has Garden", "Is Pet Friendly");
            } else if ("Commercial".equals(newVal)) {
                droplistPropertyCriteria.getItems().addAll("Business Type", "Area", "Has Parking");
            }
        });

        // Update property values dynamically based on selected criteria.
        droplistPropertyCriteria.valueProperty().addListener((obs, oldVal, newVal) -> {
            droplistPropertyValue.getItems().clear();

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

        // Apply filters when the search text changes.
        textfieldsearch.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Apply filters when any of the dropdown selections change.
        droplistPropertyType.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
        droplistPropertyCriteria.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
        droplistPropertyValue.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }
}
