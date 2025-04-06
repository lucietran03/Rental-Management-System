package com.trustmejunior.view.Owner;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.model.Enum.RentalPeriod;
import com.trustmejunior.model.Enum.RentalStatus;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.DateFormatter;
import com.trustmejunior.view.Layout;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class OwnerAllRAView implements Initializable {
    @FXML
    private TextField txtfieldsearchbar;

    @FXML
    private TableView<RentalAgreement> TableViewALLRA;

    @FXML
    private TableColumn<RentalAgreement, Integer> ra_id;

    @FXML
    private TableColumn<RentalAgreement, Double> fee;

    @FXML
    private TableColumn<RentalAgreement, String> startdate;

    @FXML
    private TableColumn<RentalAgreement, String> enddate;

    @FXML
    private TableColumn<RentalAgreement, RentalStatus> status;

    @FXML
    private TableColumn<RentalAgreement, RentalPeriod> period;

    @FXML
    private TableColumn<RentalAgreement, Integer> owner;

    @FXML
    private TableColumn<RentalAgreement, Integer> property;

    @FXML
    private Button viewRAButton;

    @FXML
    private ComboBox<RentalPeriod> droplistRAPeriod;

    @FXML
    private ComboBox<RentalStatus> droplistRAStatus;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button removeFilterButton;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<String> droplistFee;

    @FXML
    private ComboBox<String> droplistProperty;

    private ObservableList<RentalAgreement> originalRaList;

    private Account account = SessionManager.getCurrentAccount();

    private PropertyController propertyController = new PropertyController();

    public void initData() {
    }

    public void showAllRa(ObservableList<RentalAgreement> rentalAgreements) {
        // Convert the input list to an observable list for dynamic updates
        originalRaList = FXCollections.observableArrayList(rentalAgreements);

        // Create a filtered list to handle real-time search and filtering
        FilteredList<RentalAgreement> filteredData = new FilteredList<>(originalRaList, p -> true);

        // Add listener for the search bar to apply filters based on text input
        txtfieldsearchbar.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Add listeners for the dropdown menus to apply filters based on selected
        // values
        droplistRAStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistRAPeriod.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistFee.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistProperty.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Sort the filtered data and bind it to the table view
        SortedList<RentalAgreement> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableViewALLRA.comparatorProperty());
        TableViewALLRA.setItems(sortedData);

        // Collect property information for the property dropdown menu
        Set<String> propertyIds = new HashSet<>();
        for (RentalAgreement ra : originalRaList) {
            String formattedItem = String.format("%s (ID: %d)",
                    propertyController.getPropertyById(ra.getPropertyId()).getAddress(),
                    propertyController.getPropertyById(ra.getPropertyId()).getPropertyId());
            propertyIds.add(formattedItem);
        }

        // Update the property dropdown menu with available properties
        droplistProperty.getItems().clear();
        droplistProperty.getItems().addAll(propertyIds);
    }

    private void applyFilters(FilteredList<RentalAgreement> filteredList) {
        // Get the search query and selected filter options
        String searchQuery = txtfieldsearchbar.getText().toLowerCase();
        String selectedPeriod = String.valueOf(droplistRAPeriod.getSelectionModel().getSelectedItem());
        String selectedStatus = String.valueOf(droplistRAStatus.getSelectionModel().getSelectedItem());
        String selectedPrice = droplistFee.getValue();
        String selectedProperty = droplistProperty.getValue();

        // Convert LocalDate to Date for comparison
        final Date startDate = (startDatePicker.getValue() != null)
                ? Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        final Date endDate = (endDatePicker.getValue() != null)
                ? Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        // Set the predicate for filtering rental agreements based on conditions
        filteredList.setPredicate(rentalAgreement -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            // Format dates as strings for comparison
            String startDateString = rentalAgreement.getStartDate() != null
                    ? dateFormat.format(rentalAgreement.getStartDate())
                    : "";
            String endDateString = rentalAgreement.getEndDate() != null
                    ? dateFormat.format(rentalAgreement.getEndDate())
                    : "";

            // Filter by Search Query (searches fee, start/end dates, period, status)
            if (!searchQuery.isEmpty()) {
                boolean matchesQuery = String.valueOf(rentalAgreement.getFee()).contains(searchQuery) ||
                        startDateString.contains(searchQuery) ||
                        endDateString.contains(searchQuery) ||
                        String.valueOf(rentalAgreement.getPeriod()).toLowerCase().contains(searchQuery) ||
                        String.valueOf(rentalAgreement.getStatus()).toLowerCase().contains(searchQuery);
                if (!matchesQuery) {
                    return false; // Exclude if no match
                }
            }

            // Filter by selected payment period
            if (selectedPeriod != null && !selectedPeriod.trim().isEmpty() && !selectedPeriod.equals("null")) {
                boolean matchesMethod = selectedPeriod.equalsIgnoreCase(String.valueOf(rentalAgreement.getPeriod()));
                if (!matchesMethod) {
                    return false; // Exclude if period doesn't match
                }
            }

            // Filter by selected payment status
            if (selectedStatus != null && !selectedStatus.trim().isEmpty() && !selectedStatus.equals("null")) {
                boolean matchesStatus = selectedStatus.equalsIgnoreCase(String.valueOf(rentalAgreement.getStatus()));
                if (!matchesStatus) {
                    return false; // Exclude if status doesn't match
                }
            }

            // Filter by date range (start and end date)
            if (startDate != null || endDate != null) {
                Date rentalStartDate = rentalAgreement.getStartDate();
                Date rentalEndDate = rentalAgreement.getEndDate();

                // Handle start date filter
                if (startDate != null && endDate == null) {
                    if (rentalStartDate == null || rentalStartDate.before(startDate)) {
                        return false; // Exclude if rental start date is before selected start date
                    }
                }
                // Handle end date filter
                else if (endDate != null && startDate == null) {
                    if (rentalEndDate == null || rentalEndDate.after(endDate)) {
                        return false; // Exclude if rental end date is after selected end date
                    }
                }
                // Handle both start and end date filters
                else if (startDate != null && endDate != null) {
                    if ((rentalStartDate == null || rentalStartDate.before(startDate)) ||
                            (rentalEndDate == null || rentalEndDate.after(endDate))) {
                        return false; // Exclude if rental period is outside the selected date range
                    }
                }
            }

            // New Filter: Price Range (under, over or within a specific range)
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $10,000")) {
                        if (rentalAgreement.getFee() >= 10000) {
                            return false; // Exclude if fee is over $10,000
                        }
                    } else if (selectedPrice.equals("Over $100,000")) {
                        if (rentalAgreement.getFee() <= 100000) {
                            return false; // Exclude if fee is under $100,000
                        }
                    } else {
                        // Handle specific price ranges
                        String[] priceRange = selectedPrice.replace("$", "").split(" - ");
                        if (priceRange.length == 2) {
                            double minPrice = Double.parseDouble(priceRange[0].trim());
                            double maxPrice = Double.parseDouble(priceRange[1].trim());
                            if (rentalAgreement.getFee() < minPrice || rentalAgreement.getFee() > maxPrice) {
                                return false; // Exclude if fee is outside the range
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price range format: " + selectedPrice);
                    return false; // Exclude if price range format is invalid
                }
            }

            // Filter by selected property ID
            if (selectedProperty != null) {
                try {
                    int propertyId = Integer.parseInt(
                            selectedProperty.substring(selectedProperty.lastIndexOf("ID: ") + 4,
                                    selectedProperty.length() - 1));
                    if (rentalAgreement.getPropertyId() != propertyId) {
                        return false; // Exclude if property ID doesn't match
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid property ID format: " + selectedProperty);
                    return false; // Exclude if property ID format is invalid
                }
            }

            // Keep the rental agreement if it passes all filter conditions
            return true;
        });
    }

    @FXML
    void removeFilter() {
        // Clear all filters (search bar, dropdowns, and date pickers)
        txtfieldsearchbar.clear();
        droplistRAPeriod.setValue(null);
        droplistRAStatus.setValue(null);
        droplistFee.setValue(null);
        droplistProperty.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    @FXML
    public void viewRA(ActionEvent event) {
        // Get the selected rental agreement from the table
        RentalAgreement rentalAgreement = TableViewALLRA.getSelectionModel().getSelectedItem();

        // Show error if no rental agreement is selected
        if (rentalAgreement == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No rental agreement selected",
                    "Please select a rental agreement to view.");
            return;
        }

        try {
            // Load the view for the selected rental agreement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/OwnerRAView.fxml"));
            Parent root = loader.load();
            OwnerRAView view = loader.getController();
            view.initData(rentalAgreement);

            // Set the view as the main scene
            Scene currentScene = viewRAButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind columns to the rental agreement properties
        ra_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getRentalAgreementId()).asObject());
        fee.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getFee()).asObject());
        owner.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOwnerId()).asObject());
        property.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getPropertyId()).asObject());
        period.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPeriod()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        startdate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getStartDate(), "dd/MM/yyyy")));
        enddate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getEndDate(), "dd/MM/yyyy")));

        // Populate dropdowns with period and status values
        droplistRAPeriod.getItems().addAll(RentalPeriod.values());
        droplistRAStatus.getItems().addAll(RentalStatus.values());

        // Add fee ranges to the fee dropdown menu
        droplistFee.getItems().add("Under $10,000");
        for (int start = 10000; start < 100000; start += 10000) {
            int end = start + 9999;
            String range = String.format("$%d - $%d", start, end);
            droplistFee.getItems().add(range);
        }
        droplistFee.getItems().add("Over $100,000");
    }
}
