package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.Notification.Notification;
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
import java.util.ResourceBundle;

public class TenantAllPaymentView implements Initializable {
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
    private TableColumn<Payment, String> dueDate;

    @FXML
    private TableColumn<Payment, String> paymentDate;

    @FXML
    private TableColumn<Payment, PaymentMethod> method;

    @FXML
    private TableColumn<Payment, PaymentStatus> status;

    @FXML
    private TableColumn<Payment, Integer> rentalAgreementId;

    @FXML
    private TableColumn<Payment, Integer> tenantId;

    @FXML
    private Button viewPaymentButton;

    @FXML
    private ComboBox<PaymentMethod> droplistPaymentMethod;

    @FXML
    private ComboBox<PaymentStatus> droplistPaymentStatus;

    @FXML
    private Button removeFilterButton;

    @FXML
    private Button payNowButton;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> droplistAmount;

    private ObservableList<Payment> paymentList;

    private Account account = SessionManager.getCurrentAccount();

    private PaymentController paymentController = new PaymentController();

    public void showAllPayments(ObservableList<Payment> payments) {
        // Initialize the payment list and filter data
        paymentList = FXCollections.observableArrayList(payments);
        FilteredList<Payment> filteredData = new FilteredList<>(paymentList, p -> true);

        // Set up search bar filter functionality
        textfieldSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData); // Apply filters when search text changes
        });

        // Apply filters when payment status dropdown value changes
        droplistPaymentStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Apply filters when payment method dropdown value changes
        droplistPaymentMethod.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Apply filters when start date changes
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Apply filters when end date changes
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Apply filters when amount dropdown value changes
        droplistAmount.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Sort the filtered data and bind it to the table view
        SortedList<Payment> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableViewALLPayment.comparatorProperty());
        TableViewALLPayment.setItems(sortedData); // Set the filtered and sorted data to the table view
    }

    private void applyFilters(FilteredList<Payment> filteredList) {
        // Get search query and selected filter options
        String searchQuery = textfieldSearchBar.getText().toLowerCase();
        String selectedMethod = String.valueOf(droplistPaymentMethod.getSelectionModel().getSelectedItem());
        String selectedStatus = String.valueOf(droplistPaymentStatus.getSelectionModel().getSelectedItem());
        String selectedPrice = droplistAmount.getValue();

        // Convert selected dates to Date objects
        final Date startDate = (startDatePicker.getValue() != null)
                ? Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        final Date endDate = (endDatePicker.getValue() != null)
                ? Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        // Apply filters based on the selected criteria
        filteredList.setPredicate(payment -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String dueDateString = payment.getDueDate() != null ? dateFormat.format(payment.getDueDate()) : "";
            String paidDateString = payment.getPaidDate() != null ? dateFormat.format(payment.getPaidDate()) : "";

            // Search filter
            if (!searchQuery.isEmpty() && !String.valueOf(payment.getAmount()).toLowerCase().contains(searchQuery) &&
                    !dueDateString.toLowerCase().contains(searchQuery) &&
                    !paidDateString.toLowerCase().contains(searchQuery) &&
                    !String.valueOf(payment.getMethod()).toLowerCase().contains(searchQuery) &&
                    !String.valueOf(payment.getStatus()).toLowerCase().contains(searchQuery)) {
                return false;
            }

            // Payment method filter
            if (selectedMethod != null && !selectedMethod.trim().isEmpty() && !selectedMethod.equals("null") &&
                    !selectedMethod.equalsIgnoreCase(String.valueOf(payment.getMethod()))) {
                return false;
            }

            // Payment status filter
            if (selectedStatus != null && !selectedStatus.trim().isEmpty() && !selectedStatus.equals("null") &&
                    !selectedStatus.equalsIgnoreCase(String.valueOf(payment.getStatus()))) {
                return false;
            }

            // Date range filter
            if ((startDate != null || endDate != null) && payment.getDueDate() != null) {
                if ((startDate != null && payment.getDueDate().before(startDate)) ||
                        (endDate != null && payment.getDueDate().after(endDate))) {
                    return false;
                }
            }

            // Price range filter
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $100,000") && payment.getAmount() >= 100000 ||
                            selectedPrice.equals("Over $1,000,000") && payment.getAmount() <= 1000000) {
                        return false;
                    } else {
                        String[] priceRange = selectedPrice.replace("$", "").split(" - ");
                        if (priceRange.length == 2) {
                            double minPrice = Double.parseDouble(priceRange[0].trim());
                            double maxPrice = Double.parseDouble(priceRange[1].trim());
                            if (payment.getAmount() < minPrice || payment.getAmount() > maxPrice) {
                                return false;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    return false; // Exclude on invalid format
                }
            }

            // Return true if all conditions are met
            return true;
        });
    }

    @FXML
    public void viewPayment() {
        // Get the selected payment from the TableView
        Payment selectedPayment = TableViewALLPayment.getSelectionModel().getSelectedItem();

        // Show an error if no payment is selected
        if (selectedPayment == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No payment selected", "Please select a payment to view.");
            return;
        }

        try {
            // Load the TenantPaymentView and pass the selected payment for detailed view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantPaymentView.fxml"));
            Parent root = loader.load();
            TenantPaymentView view = loader.getController();
            view.initData(selectedPayment);

            // Set the new view as the main scene in the current layout
            Scene currentScene = viewPaymentButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Show an error if the payment view fails to load
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load payment view: " + e.getMessage());
        }
    }

    @FXML
    void removeFilter() {
        // Clear the search bar and reset all dropdown filters
        textfieldSearchBar.clear();
        droplistAmount.getSelectionModel().clearSelection();
        droplistPaymentMethod.getSelectionModel().clearSelection();
        droplistPaymentStatus.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns by binding them to corresponding properties of Payment
        // object
        id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPaymentId()).asObject());
        amount.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
        dueDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getDueDate(), "dd/MM/yyyy")));
        paymentDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getPaidDate(), "dd/MM/yyyy")));
        method.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMethod()));
        status.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        rentalAgreementId.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getRentalAgreementId()).asObject());
        tenantId.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getTenantId()).asObject());

        // Populate the payment method and status dropdowns with predefined values
        droplistPaymentMethod.getItems().addAll(PaymentMethod.values());
        droplistPaymentStatus.getItems().addAll(PaymentStatus.values());

        // Add predefined ranges to the amount dropdown
        droplistAmount.getItems().add("Under $100,000");
        for (int start = 100000; start < 1000000; start += 100000) {
            int end = start + 99999; // Calculate the range's upper bound
            String range = String.format("$%d - $%d", start, end);
            droplistAmount.getItems().add(range);
        }
        droplistAmount.getItems().add("Over $1,000,000");
    }

    @FXML
    void payNow() {
        try {
            Payment payment = TableViewALLPayment.getSelectionModel().getSelectedItem();

            // Show an error if no payment is selected
            if (payment == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "No payment selected", "Please select a payment to pay.");
                return;
            }

            // Check if the payment is already made
            if (payment != null && (payment.getStatus() == PaymentStatus.PAID || payment.getStatus() == PaymentStatus.LATE)) {
                CustomAlert.show(Alert.AlertType.INFORMATION, "Payment Already Made",
                        "This payment has already been paid. Please return to the previous page.");
                return;
            }

            // Load the payment creation page
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantCreatePaymentView.fxml"));
            Parent root = loader.load();
            TenantCreatePaymentView view = loader.getController();
            view.initData(payment.getPaymentId());

            // Navigate to the payment creation page
            Scene currentScene = payNowButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
