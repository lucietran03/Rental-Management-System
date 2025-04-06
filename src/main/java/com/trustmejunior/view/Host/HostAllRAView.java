package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
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

public class HostAllRAView implements Initializable {
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
    private Button editRAButton;

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
        // Initialize the original list of rental agreements
        originalRaList = FXCollections.observableArrayList(rentalAgreements);

        // Create a FilteredList to apply filters
        FilteredList<RentalAgreement> filteredData = new FilteredList<>(originalRaList, p -> true);

        // Set up listeners for each filter component to apply filters when changed
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

        // Bind sorted data to the TableView
        SortedList<RentalAgreement> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableViewALLRA.comparatorProperty());
        TableViewALLRA.setItems(sortedData);

        // Populate owner dropdown with formatted owner details
        Set<String> ownerIds = new HashSet<>();
        for (RentalAgreement ra : originalRaList) {
            String formattedItem = String.format("%s (ID: %d)",
                    ownerController.getOwnerByAccountId(ra.getOwnerId()).getFullName(),
                    ownerController.getOwnerByAccountId(ra.getOwnerId()).getAccountId());
            ownerIds.add(formattedItem);
        }
        droplistOwnerId.getItems().clear();
        droplistOwnerId.getItems().addAll(ownerIds);

        // Populate property dropdown with formatted property details
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

    // Apply the filters based on user inputs
    private void applyFilters(FilteredList<RentalAgreement> filteredList) {
        // Get filter values from the user interface components
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            // Format dates for comparison
            String startDateString = rentalAgreement.getStartDate() != null
                    ? dateFormat.format(rentalAgreement.getStartDate())
                    : "";
            String endDateString = rentalAgreement.getEndDate() != null
                    ? dateFormat.format(rentalAgreement.getEndDate())
                    : "";

            // Filter by search query if provided
            if (!searchQuery.isEmpty()) {
                boolean matchesQuery = String.valueOf(rentalAgreement.getFee()).contains(searchQuery) ||
                        startDateString.contains(searchQuery) ||
                        endDateString.contains(searchQuery) ||
                        String.valueOf(rentalAgreement.getPeriod()).toLowerCase().contains(searchQuery) ||
                        String.valueOf(rentalAgreement.getStatus()).toLowerCase().contains(searchQuery);
                if (!matchesQuery) {
                    return false;
                }
            }

            // Filter by selected rental period
            if (selectedPeriod != null && !selectedPeriod.trim().isEmpty() && !selectedPeriod.equals("null")) {
                boolean matchesMethod = selectedPeriod.equalsIgnoreCase(String.valueOf(rentalAgreement.getPeriod()));
                if (!matchesMethod) {
                    return false;
                }
            }

            // Filter by selected status
            if (selectedStatus != null && !selectedStatus.trim().isEmpty() && !selectedStatus.equals("null")) {
                boolean matchesStatus = selectedStatus.equalsIgnoreCase(String.valueOf(rentalAgreement.getStatus()));
                if (!matchesStatus) {
                    return false;
                }
            }

            // Filter by start and end date range
            if (startDate != null || endDate != null) {
                Date rentalStartDate = rentalAgreement.getStartDate();
                Date rentalEndDate = rentalAgreement.getEndDate();

                if (startDate != null && endDate == null) {
                    if (rentalStartDate == null || rentalStartDate.before(startDate)) {
                        return false;
                    }
                } else if (endDate != null && startDate == null) {
                    if (rentalEndDate == null || rentalEndDate.after(endDate)) {
                        return false;
                    }
                } else if (startDate != null && endDate != null) {
                    if ((rentalStartDate == null || rentalStartDate.before(startDate)) ||
                            (rentalEndDate == null || rentalEndDate.after(endDate))) {
                        return false;
                    }
                }
            }

            // Filter by price range
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $10,000")) {
                        if (rentalAgreement.getFee() >= 10000) {
                            return false;
                        }
                    } else if (selectedPrice.equals("Over $100,000")) {
                        if (rentalAgreement.getFee() <= 100000) {
                            return false;
                        }
                    } else {
                        String[] priceRange = selectedPrice.replace("$", "").split(" - ");
                        if (priceRange.length == 2) {
                            double minPrice = Double.parseDouble(priceRange[0].trim());
                            double maxPrice = Double.parseDouble(priceRange[1].trim());
                            if (rentalAgreement.getFee() < minPrice || rentalAgreement.getFee() > maxPrice) {
                                return false;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price range format: " + selectedPrice);
                    return false;
                }
            }

            // Filter by owner ID
            if (selectedOwner != null) {
                try {
                    int ownerId = Integer.parseInt(
                            selectedOwner.substring(selectedOwner.lastIndexOf("ID: ") + 4, selectedOwner.length() - 1));
                    if (rentalAgreement.getOwnerId() != ownerId) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid owner ID format: " + selectedOwner);
                    return false;
                }
            }

            // Filter by property ID
            if (selectedProperty != null) {
                try {
                    int propertyId = Integer.parseInt(
                            selectedProperty.substring(selectedProperty.lastIndexOf("ID: ") + 4,
                                    selectedProperty.length() - 1));
                    if (rentalAgreement.getPropertyId() != propertyId) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid property ID format: " + selectedProperty);
                    return false;
                }
            }

            // Keep the rental agreement if it passes all filter conditions
            return true;
        });
    }

    // Remove all filters and reset the form
    @FXML
    void removeFilter() {
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
        // Retrieve the selected rental agreement
        RentalAgreement selectedRA = TableViewALLRA.getSelectionModel().getSelectedItem();
        if (selectedRA == null) {
            // Show error if no rental agreement is selected
            CustomAlert.show(Alert.AlertType.ERROR, "No rental agreement selected",
                    "Please select a rental agreement to view.");
            return;
        }

        try {
            // Load and display the rental agreement view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/HostRAView.fxml"));
            Parent root = loader.load();
            HostRAView view = loader.getController();
            view.initData(selectedRA);

            // Set the new scene with the rental agreement view
            Scene currentScene = ((Node) event.getSource()).getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exception
        }
    }

    @FXML
    public void editRA(ActionEvent event) {
        // Retrieve the selected rental agreement
        RentalAgreement selectedRA = TableViewALLRA.getSelectionModel().getSelectedItem();
        if (selectedRA == null) {
            // Show error if no rental agreement is selected
            CustomAlert.show(Alert.AlertType.ERROR, "No rental agreement selected",
                    "Please select a rental agreement to edit.");
            return;
        }

        // Ensure the rental agreement is active before editing
        if (selectedRA.getStatus() != RentalStatus.ACTIVE) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Rental agreement is not in active status");
            return;
        }

        try {
            // Load and display the rental agreement edit view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/HostEditRAView.fxml"));
            Parent root = loader.load();
            HostEditRAView view = loader.getController();
            view.initData(selectedRA);

            // Set the new scene with the edit rental agreement view
            Scene currentScene = ((Node) event.getSource()).getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exception
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize table columns to display rental agreement details
        id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getRentalAgreementId()).asObject());
        fee.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getFee()).asObject());
        owner_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getOwnerId()).asObject());
        property_id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getPropertyId()).asObject());
        period.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPeriod()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        startDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getStartDate(), "dd/MM/yyyy")));
        endDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getEndDate(), "dd/MM/yyyy")));

        // Populate dropdown lists for rental agreement period and status
        droplistRAPeriod.getItems().addAll(RentalPeriod.values());
        droplistRAStatus.getItems().addAll(RentalStatus.values());

        // Populate fee range dropdown
        droplistFee.getItems().add("Under $10,000");

        // Add ranges from $10,000 to $99,999 in $10,000 increments
        for (int start = 10000; start < 100000; start += 10000) {
            int end = start + 9999; // Calculate the upper bound of the range
            String range = String.format("$%d - $%d", start, end);
            droplistFee.getItems().add(range);
        }

        // Add "Over $100,000" to fee range dropdown
        droplistFee.getItems().add("Over $100,000");
    }
}
