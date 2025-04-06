package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

public class ManagerAllPaymentView implements Initializable {
    @FXML
    private ImageView PaymentLogoImageView;

    @FXML
    private TextField textfieldSearchBar;

    @FXML
    private TableView<Payment> TableViewALLPayment;

    @FXML
    private TableColumn<Payment, Integer> id;

    @FXML
    private TableColumn<Payment, Double> amount;

    @FXML
    private TableColumn<Payment, String> paymentDate;

    @FXML
    private TableColumn<Payment, String> dueDate;

    @FXML
    private TableColumn<Payment, PaymentMethod> method;

    @FXML
    private TableColumn<Payment, PaymentStatus> status;

    @FXML
    private TableColumn<Payment, Integer> rentalAgreementId;

    @FXML
    private TableColumn<Payment, Integer> tenantId;

    @FXML
    private Button createPaymentButton;

    @FXML
    private Button viewPaymentButton;

    @FXML
    private Button editPaymentButton;

    @FXML
    private Button deletePaymentButton;

    @FXML
    private ComboBox<PaymentMethod> droplistPaymentMethod;

    @FXML
    private ComboBox<PaymentStatus> droplistPaymentStatus;

    @FXML
    private Button removeFilterButton;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> droplistAmount;

    @FXML
    private ComboBox<String> droplistTenant;

    private ObservableList<Payment> paymentList;

    private Account account = SessionManager.getCurrentAccount();

    private PaymentController paymentController = new PaymentController();
    private TenantController tenantController = new TenantController();

    public void initData() {

    }

    public void showAllPayments(ObservableList<Payment> payments) {
        paymentList = FXCollections.observableArrayList(payments);

        // Initialize filtered list
        FilteredList<Payment> filteredData = new FilteredList<>(paymentList, p -> true);

        // Add listeners for search bar and filter dropdowns to apply filters
        textfieldSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistPaymentStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistPaymentMethod.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistTenant.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistAmount.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Bind sorted data to the table
        SortedList<Payment> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableViewALLPayment.comparatorProperty());
        TableViewALLPayment.setItems(sortedData);

        // Populate tenant dropdown with tenant names and IDs
        Set<String> tenantIds = new HashSet<>();
        for (Payment p : paymentList) {
            String formattedItem = String.format("%s (ID: %d)",
                    tenantController.getTenantByAccountId(p.getTenantId()).getFullName(),
                    tenantController.getTenantByAccountId(p.getTenantId()).getAccountId());
            tenantIds.add(formattedItem);
        }
        droplistTenant.getItems().addAll(tenantIds);
    }

    private void applyFilters(FilteredList<Payment> filteredList) {
        // Get filter values
        String searchQuery = textfieldSearchBar.getText().toLowerCase();
        String selectedMethod = String.valueOf(droplistPaymentMethod.getSelectionModel().getSelectedItem());
        String selectedStatus = String.valueOf(droplistPaymentStatus.getSelectionModel().getSelectedItem());
        String selectedPrice = droplistAmount.getValue();
        String selectedTenant = droplistTenant.getValue();

        // Convert LocalDate to Date for comparison (if dates are selected)
        final Date startDate = (startDatePicker.getValue() != null)
                ? Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        final Date endDate = (endDatePicker.getValue() != null)
                ? Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        // Set predicate to filter list based on criteria
        filteredList.setPredicate(payment -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String dueDateString = payment.getDueDate() != null ? dateFormat.format(payment.getDueDate()) : "";
            String paidDateString = payment.getPaidDate() != null ? dateFormat.format(payment.getPaidDate()) : "";

            // Search query filter
            if (!searchQuery.isEmpty()) {
                boolean matchesQuery = String.valueOf(payment.getAmount()).toLowerCase().contains(searchQuery) ||
                        dueDateString.toLowerCase().contains(searchQuery) ||
                        paidDateString.toLowerCase().contains(searchQuery) ||
                        String.valueOf(payment.getMethod()).toLowerCase().contains(searchQuery) ||
                        String.valueOf(payment.getStatus()).toLowerCase().contains(searchQuery);
                if (!matchesQuery) {
                    return false; // Exclude if no match
                }
            }

            // Filter by payment method
            if (selectedMethod != null && !selectedMethod.trim().isEmpty() && !selectedMethod.equals("null")) {
                boolean matchesMethod = selectedMethod.equalsIgnoreCase(String.valueOf(payment.getMethod()));
                if (!matchesMethod) {
                    return false; // Exclude if no match
                }
            }

            // Filter by payment status
            if (selectedStatus != null && !selectedStatus.trim().isEmpty() && !selectedStatus.equals("null")) {
                boolean matchesStatus = selectedStatus.equalsIgnoreCase(String.valueOf(payment.getStatus()));
                if (!matchesStatus) {
                    return false; // Exclude if no match
                }
            }

            // Filter by date range
            if (startDate != null || endDate != null) {
                Date dueDate = payment.getDueDate();
                if (dueDate == null || (startDate != null && dueDate.before(startDate))
                        || (endDate != null && dueDate.after(endDate))) {
                    return false; // Exclude if out of range
                }
            }

            // Filter by price range
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $100,000")) {
                        if (payment.getAmount() >= 100000) {
                            return false; // Exclude if amount >= $100,000
                        }
                    } else if (selectedPrice.equals("Over $1,000,000")) {
                        if (payment.getAmount() <= 1000000) {
                            return false; // Exclude if amount <= $1,000,000
                        }
                    } else {
                        // Handle custom price range
                        String[] priceRange = selectedPrice.replace("$", "").split(" - ");
                        if (priceRange.length == 2) {
                            double minPrice = Double.parseDouble(priceRange[0].trim());
                            double maxPrice = Double.parseDouble(priceRange[1].trim());
                            if (payment.getAmount() < minPrice || payment.getAmount() > maxPrice) {
                                return false; // Exclude if out of range
                            }
                        } else {
                            return false; // Exclude if invalid format
                        }
                    }
                } catch (NumberFormatException e) {
                    return false; // Exclude if error parsing price range
                }
            }

            // Filter by tenant
            if (selectedTenant != null) {
                try {
                    int tenantId = Integer.parseInt(selectedTenant.substring(selectedTenant.lastIndexOf("ID: ") + 4,
                            selectedTenant.length() - 1));
                    if (payment.getTenantId() != tenantId) {
                        return false; // Exclude if tenant ID doesn't match
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid tenant ID format: " + selectedTenant);
                    return false; // Exclude if error parsing tenant ID
                }
            }

            return true; // Keep payment if it matches all criteria
        });
    }

    @FXML
    void removeFilter() {
        // Clear the text field and reset all dropdowns
        textfieldSearchBar.clear();
        droplistAmount.getSelectionModel().clearSelection();
        droplistPaymentMethod.getSelectionModel().clearSelection();
        droplistTenant.getSelectionModel().clearSelection();
        droplistPaymentStatus.getSelectionModel().clearSelection();
    }

    @FXML
    public void createPayment() {
        // Load and display the create payment view
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerCreatePaymentView.fxml"));
            Parent root = loader.load();
            ManagerCreatePaymentView view = loader.getController();
            view.initData();

            Scene currentScene = createPaymentButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Show error alert if loading the create payment view fails
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load create payment view: " + e.getMessage());
        }
    }

    @FXML
    public void viewPayment() {
        // Get selected payment and load the payment view if selected
        Payment selectedPayment = TableViewALLPayment.getSelectionModel().getSelectedItem();
        if (selectedPayment == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No payment selected", "Please select a payment to view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerPaymentView.fxml"));
            Parent root = loader.load();
            ManagerPaymentView view = loader.getController();
            view.initData(selectedPayment);

            Scene currentScene = viewPaymentButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load payment view: " + e.getMessage());
        }
    }

    @FXML
    public void editPayment() {
        // Get selected payment and load the edit payment view if selected
        Payment selectedPayment = TableViewALLPayment.getSelectionModel().getSelectedItem();
        if (selectedPayment == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No payment selected", "Please select a payment to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerEditPaymentView.fxml"));
            Parent root = loader.load();
            ManagerEditPaymentView view = loader.getController();
            view.initData(selectedPayment);

            Scene currentScene = editPaymentButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load edit payment view: " + e.getMessage());
        }
    }

    @FXML
    public void deletePayment() {
        // Get selected payment and prompt for confirmation before deleting
        Payment selectedPayment = TableViewALLPayment.getSelectionModel().getSelectedItem();
        if (selectedPayment == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No payment selected", "Please select a payment to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Payment");
        alert.setHeaderText("Delete Payment #" + selectedPayment.getPaymentId());
        alert.setContentText("Are you sure you want to delete this payment?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                paymentController.deletePaymentById(selectedPayment.getPaymentId());
                showAllPayments(FXCollections.observableArrayList(paymentController.getAllPayments()));
                CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Payment deleted successfully");
            } catch (Exception e) {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to delete payment: " + e.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the table columns for payment details
        id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPaymentId()).asObject());
        amount.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
        dueDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getDueDate(), "dd/MM/yyyy")));
        paymentDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getDueDate(), "dd/MM/yyyy")));
        method.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMethod()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        rentalAgreementId.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getRentalAgreementId()).asObject());
        tenantId.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getTenantId()).asObject());

        // Populate the dropdown lists with payment method and status options
        droplistPaymentMethod.getItems().addAll(PaymentMethod.values());
        droplistPaymentStatus.getItems().addAll(PaymentStatus.values());

        // Add predefined payment amount ranges
        droplistAmount.getItems().add("Under $100,000");
        for (int start = 100000; start < 1000000; start += 100000) {
            int end = start + 99999;
            String range = String.format("$%d - $%d", start, end);
            droplistAmount.getItems().add(range);
        }
        droplistAmount.getItems().add("Over $1,000,000");
    }
}
