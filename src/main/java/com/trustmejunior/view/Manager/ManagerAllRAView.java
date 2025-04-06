package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

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
import java.util.*;

import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;

public class ManagerAllRAView implements Initializable {
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
    private Button createRAButton;

    @FXML
    private Button viewRAButton;

    @FXML
    private Button editRAButton;

    @FXML
    private Button deleteRAButton;

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
        originalRaList = FXCollections.observableArrayList(rentalAgreements);

        // Initialize FilteredList to hold filtered rental agreements
        FilteredList<RentalAgreement> filteredData = new FilteredList<>(originalRaList, p -> true);

        // Set up listeners to trigger filtering when changes occur in search fields and
        // dropdowns
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

        // Bind filtered and sorted data to the table
        SortedList<RentalAgreement> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableViewALLRA.comparatorProperty());
        TableViewALLRA.setItems(sortedData);

        // Populate Owner ID dropdown with formatted owner details
        Set<String> ownerIds = new HashSet<>();
        for (RentalAgreement ra : originalRaList) {
            String formattedItem = String.format("%s (ID: %d)",
                    ownerController.getOwnerByAccountId(ra.getOwnerId()).getFullName(),
                    ownerController.getOwnerByAccountId(ra.getOwnerId()).getAccountId());
            ownerIds.add(formattedItem);
        }
        droplistOwnerId.getItems().addAll(ownerIds);

        // Populate Property ID dropdown with formatted property details
        Set<String> propertyIds = new HashSet<>();
        for (RentalAgreement ra : originalRaList) {
            String formattedItem = String.format("%s (ID: %d)",
                    propertyController.getPropertyById(ra.getPropertyId()).getAddress(),
                    propertyController.getPropertyById(ra.getPropertyId()).getPropertyId());
            propertyIds.add(formattedItem);
        }
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

        // Apply filtering based on multiple conditions (search, date range, price,
        // etc.)
        filteredList.setPredicate(rentalAgreement -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            // Format dates for comparison
            String startDateString = rentalAgreement.getStartDate() != null
                    ? dateFormat.format(rentalAgreement.getStartDate())
                    : "";
            String endDateString = rentalAgreement.getEndDate() != null
                    ? dateFormat.format(rentalAgreement.getEndDate())
                    : "";

            // Filter by search query (fee, dates, period, status)
            if (!searchQuery.isEmpty()) {
                boolean matchesQuery = String.valueOf(rentalAgreement.getFee()).contains(searchQuery) ||
                        startDateString.contains(searchQuery) ||
                        endDateString.contains(searchQuery) ||
                        String.valueOf(rentalAgreement.getPeriod()).toLowerCase().contains(searchQuery) ||
                        String.valueOf(rentalAgreement.getStatus()).toLowerCase().contains(searchQuery);
                if (!matchesQuery) {
                    return false; // Exclude rental agreement if search query does not match
                }
            }

            // Filter by selected period
            if (selectedPeriod != null && !selectedPeriod.trim().isEmpty() && !selectedPeriod.equals("null")) {
                boolean matchesMethod = selectedPeriod.equalsIgnoreCase(String.valueOf(rentalAgreement.getPeriod()));
                if (!matchesMethod) {
                    return false; // Exclude if period does not match
                }
            }

            // Filter by selected status
            if (selectedStatus != null && !selectedStatus.trim().isEmpty() && !selectedStatus.equals("null")) {
                boolean matchesStatus = selectedStatus.equalsIgnoreCase(String.valueOf(rentalAgreement.getStatus()));
                if (!matchesStatus) {
                    return false; // Exclude if status does not match
                }
            }

            // Filter by date range
            if (startDate != null || endDate != null) {
                Date rentalStartDate = rentalAgreement.getStartDate();
                Date rentalEndDate = rentalAgreement.getEndDate();

                // Handle filtering for start and/or end date
                if (startDate != null && endDate == null) {
                    if (rentalStartDate == null || rentalStartDate.before(startDate)) {
                        return false; // Exclude if rental start date is before selected start date
                    }
                } else if (endDate != null && startDate == null) {
                    if (rentalEndDate == null || rentalEndDate.after(endDate)) {
                        return false; // Exclude if rental end date is after selected end date
                    }
                } else if (startDate != null && endDate != null) {
                    if ((rentalStartDate == null || rentalStartDate.before(startDate)) ||
                            (rentalEndDate == null || rentalEndDate.after(endDate))) {
                        return false; // Exclude if rental period is outside date range
                    }
                }
            }

            // Filter by price range (under $10,000, over $100,000, or specific range)
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $10,000")) {
                        if (rentalAgreement.getFee() >= 10000) {
                            return false; // Exclude if fee is greater than or equal to $10,000
                        }
                    } else if (selectedPrice.equals("Over $100,000")) {
                        if (rentalAgreement.getFee() <= 100000) {
                            return false; // Exclude if fee is less than or equal to $100,000
                        }
                    } else {
                        String[] priceRange = selectedPrice.replace("$", "").split(" - ");
                        if (priceRange.length == 2) {
                            double minPrice = Double.parseDouble(priceRange[0].trim());
                            double maxPrice = Double.parseDouble(priceRange[1].trim());
                            if (rentalAgreement.getFee() < minPrice || rentalAgreement.getFee() > maxPrice) {
                                return false; // Exclude if fee is outside the selected range
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price range format: " + selectedPrice);
                    return false; // Handle invalid price format
                }
            }

            // Filter by selected owner
            if (selectedOwner != null) {
                try {
                    int ownerId = Integer.parseInt(
                            selectedOwner.substring(selectedOwner.lastIndexOf("ID: ") + 4, selectedOwner.length() - 1));
                    if (rentalAgreement.getOwnerId() != ownerId) {
                        return false; // Exclude if owner ID does not match
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid owner ID format: " + selectedOwner);
                    return false; // Handle invalid owner ID format
                }
            }

            // Filter by selected property
            if (selectedProperty != null) {
                try {
                    int propertyId = Integer.parseInt(
                            selectedProperty.substring(selectedProperty.lastIndexOf("ID: ") + 4,
                                    selectedProperty.length() - 1));
                    if (rentalAgreement.getPropertyId() != propertyId) {
                        return false; // Exclude if property ID does not match
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid property ID format: " + selectedProperty);
                    return false; // Handle invalid property ID format
                }
            }

            // Include the rental agreement if it passes all filter conditions
            return true;
        });
    }

    @FXML
    void removeFilter() {
        // Clears all filters and resets the values in the UI controls
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
    public void createRA(ActionEvent event) {
        // Loads the Create Rental Agreement view
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerCreateRAView.fxml"));
            Parent root = loader.load();
            ManagerCreateRAView view = loader.getController();
            view.initData();

            Scene currentScene = ((Node) event.getSource()).getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewRA(ActionEvent event) {
        // Views the selected Rental Agreement
        RentalAgreement selectedRA = TableViewALLRA.getSelectionModel().getSelectedItem();
        if (selectedRA == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No rental agreement selected",
                    "Please select a rental agreement to view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/ManagerRAView.fxml"));
            Parent root = loader.load();
            ManagerRAView view = loader.getController();
            view.initData(selectedRA);

            Scene currentScene = ((Node) event.getSource()).getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void editRA(ActionEvent event) {
        // Edits the selected Rental Agreement
        RentalAgreement selectedRA = TableViewALLRA.getSelectionModel().getSelectedItem();
        if (selectedRA == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No rental agreement selected",
                    "Please select a rental agreement to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerEditRAView.fxml"));
            Parent root = loader.load();
            ManagerEditRAView view = loader.getController();
            view.initData(selectedRA);

            Scene currentScene = ((Node) event.getSource()).getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteRA(ActionEvent event) {
        // Deletes the selected Rental Agreement after confirmation
        RentalAgreement selectedRA = TableViewALLRA.getSelectionModel().getSelectedItem();
        if (selectedRA == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No rental agreement selected",
                    "Please select a rental agreement to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this rental agreement?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                RentalAgreementController rentalAgreementController = new RentalAgreementController();
                rentalAgreementController.deleteRentalAgreementById(selectedRA.getRentalAgreementId());
                originalRaList.remove(selectedRA);
                TableViewALLRA.setItems(originalRaList);

                CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Rental agreement deleted successfully.");
            } catch (Exception e) {
                CustomAlert.show(Alert.AlertType.ERROR, "Error",
                        "Failed to delete rental agreement: " + e.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Binds the table columns to the RentalAgreement properties
        id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getRentalAgreementId()).asObject());
        fee.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getFee()).asObject());
        owner_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getOwnerId()).asObject());
        property_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getPropertyId()).asObject());
        period.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPeriod()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));

        // Formats and binds date columns to the table
        startDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getStartDate(), "dd/MM/yyyy")));
        endDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getEndDate(), "dd/MM/yyyy")));

        // Populates dropdowns with values
        droplistRAPeriod.getItems().addAll(RentalPeriod.values());
        droplistRAStatus.getItems().addAll(RentalStatus.values());

        // Adds fee ranges to the fee dropdown
        droplistFee.getItems().add("Under $10,000");
        for (int start = 10000; start < 100000; start += 10000) {
            int end = start + 9999; // Calculate the upper bound of the range
            String range = String.format("$%d - $%d", start, end);
            droplistFee.getItems().add(range);
        }
        droplistFee.getItems().add("Over $100,000");
    }
}