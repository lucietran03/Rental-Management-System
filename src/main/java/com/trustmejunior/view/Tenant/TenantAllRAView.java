package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.model.Enum.RentalPeriod;
import com.trustmejunior.model.Enum.RentalStatus;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.utils.DateFormatter;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

public class TenantAllRAView implements Initializable {
    @FXML
    private TextField textfieldSearchBar;

    @FXML
    private ImageView RALogoImageView;

    @FXML
    private TableView<RentalAgreement> TableViewALLRA;

    @FXML
    private TableColumn<RentalAgreement, Integer> id;

    @FXML
    private TableColumn<RentalAgreement, Double> fee;

    @FXML
    private TableColumn<RentalAgreement, String> startDate;

    @FXML
    private TableColumn<RentalAgreement, String> endDate;

    @FXML
    private TableColumn<RentalAgreement, RentalPeriod> period;

    @FXML
    private TableColumn<RentalAgreement, RentalStatus> status;

    @FXML
    private TableColumn<RentalAgreement, Integer> owner_id;

    @FXML
    private TableColumn<RentalAgreement, Integer> property_id;

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
    private ComboBox<String> droplistOwnerId;

    @FXML
    private ComboBox<String> droplistProperty;

    private ObservableList<RentalAgreement> originalRaList;

    private Account account = SessionManager.getCurrentAccount();

    private OwnerController ownerController = new OwnerController();
    private PropertyController propertyController = new PropertyController();

    public void initData() {

    }

    public void showAllRa(ObservableList<RentalAgreement> rentalAgreements) {
        // Create an observable list from the provided rental agreements
        originalRaList = FXCollections.observableArrayList(rentalAgreements);

        // Create a filtered list for rental agreements with an initial predicate that
        // always returns true
        FilteredList<RentalAgreement> filteredData = new FilteredList<>(originalRaList, p -> true);

        // Add listeners to update filters when user changes search or dropdown values
        textfieldSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

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

        droplistOwnerId.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistProperty.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Bind the sorted data to the table
        SortedList<RentalAgreement> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableViewALLRA.comparatorProperty());
        TableViewALLRA.setItems(sortedData);

        // Populate owner dropdown list with formatted owner info (name and ID)
        Set<String> ownerIds = new HashSet<>();
        for (RentalAgreement ra : originalRaList) {
            String formattedItem = String.format("%s (ID: %d)",
                    ownerController.getOwnerByAccountId(ra.getOwnerId()).getFullName(),
                    ownerController.getOwnerByAccountId(ra.getOwnerId()).getAccountId());
            ownerIds.add(formattedItem);
        }
        droplistOwnerId.getItems().clear();
        droplistOwnerId.getItems().addAll(ownerIds);

        // Populate property dropdown list with formatted property info (address and ID)
        Set<String> propertyIds = new HashSet<>();
        for (RentalAgreement ra : originalRaList) {
            String formattedItem = String.format("%s (ID: %d)",
                    propertyController.getPropertyById(ra.getPropertyId()).getAddress(),
                    propertyController.getPropertyById(ra.getPropertyId()).getPropertyId());
            propertyIds.add(formattedItem);
        }
        droplistProperty.getItems().clear();
        droplistProperty.getItems().addAll(propertyIds);
    }

    private void applyFilters(FilteredList<RentalAgreement> filteredList) {
        // Get the search query and selected filter options
        String searchQuery = textfieldSearchBar.getText().toLowerCase();
        String selectedPeriod = String.valueOf(droplistRAPeriod.getSelectionModel().getSelectedItem());
        String selectedStatus = String.valueOf(droplistRAStatus.getSelectionModel().getSelectedItem());
        String selectedPrice = droplistFee.getValue();
        String selectedOwner = droplistOwnerId.getValue();
        String selectedProperty = droplistProperty.getValue();

        // Convert LocalDate to Date for comparison
        final Date startDate = (startDatePicker.getValue() != null)
                ? Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;
        final Date endDate = (endDatePicker.getValue() != null)
                ? Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        filteredList.setPredicate(rentalAgreement -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String startDateString = rentalAgreement.getStartDate() != null
                        ? dateFormat.format(rentalAgreement.getStartDate())
                        : "";
                String endDateString = rentalAgreement.getEndDate() != null
                        ? dateFormat.format(rentalAgreement.getEndDate())
                        : "";

                // Filter by Search Query
                if (!searchQuery.isEmpty()) {
                    boolean matchesQuery = String.valueOf(rentalAgreement.getFee()).contains(searchQuery) ||
                            startDateString.contains(searchQuery) ||
                            endDateString.contains(searchQuery) ||
                            String.valueOf(rentalAgreement.getPeriod()).toLowerCase().contains(searchQuery) ||
                            String.valueOf(rentalAgreement.getStatus()).toLowerCase().contains(searchQuery);
                    if (!matchesQuery)
                        return false;
                }

                // Filter by Period
                if (selectedPeriod != null && !selectedPeriod.isEmpty() && !selectedPeriod.equals("null") &&
                        !selectedPeriod.equalsIgnoreCase(String.valueOf(rentalAgreement.getPeriod()))) {
                    return false;
                }

                // Filter by Status
                if (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("null") &&
                        !selectedStatus.equalsIgnoreCase(String.valueOf(rentalAgreement.getStatus()))) {
                    return false;
                }

                // Filter by Date Range
                Date rentalStartDate = rentalAgreement.getStartDate();
                Date rentalEndDate = rentalAgreement.getEndDate();

                if (startDate != null && rentalStartDate != null && rentalStartDate.before(startDate))
                    return false;
                if (endDate != null && rentalEndDate != null && rentalEndDate.after(endDate))
                    return false;

                // Filter by Price
                if (selectedPrice != null && !selectedPrice.isEmpty()) {
                    try {
                        if (selectedPrice.equals("Under $10,000") && rentalAgreement.getFee() >= 10000)
                            return false;
                        if (selectedPrice.equals("Over $100,000") && rentalAgreement.getFee() <= 100000)
                            return false;

                        String[] priceRange = selectedPrice.replace("$", "").split(" - ");
                        if (priceRange.length == 2) {
                            double minPrice = Double.parseDouble(priceRange[0].trim());
                            double maxPrice = Double.parseDouble(priceRange[1].trim());
                            if (rentalAgreement.getFee() < minPrice || rentalAgreement.getFee() > maxPrice)
                                return false;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price format: " + selectedPrice);
                        return false;
                    }
                }

                // Filter by Owner
                if (selectedOwner != null && !selectedOwner.isEmpty()) {
                    try {
                        int ownerId = Integer.parseInt(
                                selectedOwner.substring(selectedOwner.lastIndexOf("ID: ") + 4,
                                        selectedOwner.length() - 1));
                        if (rentalAgreement.getOwnerId() != ownerId)
                            return false;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid owner ID format: " + selectedOwner);
                        return false;
                    }
                }

                // Filter by Property
                if (selectedProperty != null && !selectedProperty.isEmpty()) {
                    try {
                        int propertyId = Integer.parseInt(
                                selectedProperty.substring(selectedProperty.lastIndexOf("ID: ") + 4,
                                        selectedProperty.length() - 1));
                        if (rentalAgreement.getPropertyId() != propertyId)
                            return false;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid property ID format: " + selectedProperty);
                        return false;
                    }
                }

                return true; // Include if all filters pass
            } catch (Exception e) {
                e.printStackTrace();
                return false; // Exclude if any unexpected exception occurs
            }
        });
    }

    @FXML
    void removeFilter() {
        // Clear all filter fields (search bar, dropdowns, and date pickers)
        textfieldSearchBar.clear();
        droplistRAPeriod.setValue(null);
        droplistRAStatus.setValue(null);
        droplistFee.setValue(null);
        droplistOwnerId.setValue(null);
        droplistProperty.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    @FXML
    public void viewRA(ActionEvent event) {
        // Get the selected rental agreement from the table
        RentalAgreement selectedRA = TableViewALLRA.getSelectionModel().getSelectedItem();
        if (selectedRA == null) {
            // Show an error alert if no rental agreement is selected
            CustomAlert.show(Alert.AlertType.ERROR, "No rental agreement selected",
                    "Please select a rental agreement to view.");
            return;
        }

        try {
            // Load the TenantRAView FXML and initialize the controller with the selected
            // rental agreement data
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantRAView.fxml"));
            Parent root = loader.load();
            TenantRAView view = loader.getController();
            view.initData(selectedRA);

            // Switch to the TenantRAView scene
            Scene currentScene = ((Node) event.getSource()).getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up cell value factories for each table column
        id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getRentalAgreementId()).asObject());
        fee.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getFee()).asObject());
        owner_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getOwnerId()).asObject());
        property_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getPropertyId()).asObject());
        period.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPeriod()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));

        // Format start and end dates for display in the table
        startDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getStartDate(), "dd/MM/yyyy")));
        endDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getEndDate(), "dd/MM/yyyy")));

        // Populate dropdowns with available values for period and status
        droplistRAPeriod.getItems().addAll(RentalPeriod.values());
        droplistRAStatus.getItems().addAll(RentalStatus.values());

        // Add fee range options to the fee dropdown
        droplistFee.getItems().add("Under $10,000");

        // Add fee ranges from $10,000 to $99,999 in increments of $10,000
        for (int start = 10000; start < 100000; start += 10000) {
            int end = start + 9999; // Calculate the upper bound of the range
            String range = String.format("$%d - $%d", start, end);
            droplistFee.getItems().add(range);
        }

        // Add option for "Over $100,000" fee range
        droplistFee.getItems().add("Over $100,000");
    }
}
