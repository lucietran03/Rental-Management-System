package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.NotificationController;
import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.request.CreateNotificationRequest;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;
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

public class HostAllPaymentView implements Initializable {
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
    private Button createPaymentButton;

    @FXML
    private Button viewPaymentButton;

    @FXML
    private Button editPaymentButton;

    @FXML
    private Button deletePaymentButton;

    @FXML
    private Button notifyTenantButton;

    @FXML
    private ComboBox<PaymentMethod> droplistPaymentMethod;

    @FXML
    private ComboBox<PaymentStatus> droplistPaymentStatus;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button removeFilterButton;

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

    @FXML
    void removeFilter() {
        // Clear the search bar text and reset the selections in all dropdowns
        textfieldSearchBar.clear();
        droplistAmount.getSelectionModel().clearSelection();
        droplistPaymentMethod.getSelectionModel().clearSelection();
        droplistTenant.getSelectionModel().clearSelection();
        droplistPaymentStatus.getSelectionModel().clearSelection();
    }

    public void showAllPayments(ObservableList<Payment> payments) {
        // Convert the list of payments to an observable list for use in the table view
        paymentList = FXCollections.observableArrayList(payments);

        // Create a filtered list with initial filter set to true for all items
        FilteredList<Payment> filteredData = new FilteredList<>(paymentList, p -> true);

        // Add listener to the search bar to apply filters on text change
        textfieldSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Add listener to the payment status dropdown to apply filters on value change
        droplistPaymentStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Add listener to the payment method dropdown to apply filters on value change
        droplistPaymentMethod.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Add listener to the start date picker to apply filters on date change
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Add listener to the end date picker to apply filters on date change
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Add listener to the tenant dropdown to apply filters on value change
        droplistTenant.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Add listener to the amount dropdown to apply filters on value change
        droplistAmount.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        // Bind the comparator property of sorted data to the table view comparator
        SortedList<Payment> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableViewALLPayment.comparatorProperty());
        TableViewALLPayment.setItems(sortedData);

        // Create a set of tenant IDs with their names and IDs formatted
        Set<String> tenantIds = new HashSet<>();
        for (Payment p : paymentList) {
            String formattedItem = String.format("%s (ID: %d)",
                    tenantController.getTenantByAccountId(p.getTenantId()).getFullName(),
                    tenantController.getTenantByAccountId(p.getTenantId()).getAccountId());
            tenantIds.add(formattedItem);
        }

        // Add the tenant IDs to the tenant dropdown list
        droplistTenant.getItems().addAll(tenantIds);
    }

    private void applyFilters(FilteredList<Payment> filteredList) {
        // Get the search query and selected filter options
        String searchQuery = textfieldSearchBar.getText().toLowerCase();
        String selectedMethod = String.valueOf(droplistPaymentMethod.getSelectionModel().getSelectedItem());
        String selectedStatus = String.valueOf(droplistPaymentStatus.getSelectionModel().getSelectedItem());
        String selectedPrice = droplistAmount.getValue();
        String selectedTenant = droplistTenant.getValue();

        // Convert LocalDate to Date for comparison (if start and end dates are
        // selected)
        final Date startDate = (startDatePicker.getValue() != null)
                ? Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        final Date endDate = (endDatePicker.getValue() != null)
                ? Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;

        // Set the predicate to filter the list based on the selected criteria
        filteredList.setPredicate(payment -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            // Format dates as strings
            String dueDateString = payment.getDueDate() != null ? dateFormat.format(payment.getDueDate()) : "";
            String paidDateString = payment.getPaidDate() != null ? dateFormat.format(payment.getPaidDate()) : "";

            // Filter by Search Query (if any)
            if (!searchQuery.isEmpty()) {
                boolean matchesQuery = String.valueOf(payment.getAmount()).toLowerCase().contains(searchQuery) ||
                        dueDateString.toLowerCase().contains(searchQuery) ||
                        paidDateString.toLowerCase().contains(searchQuery) ||
                        String.valueOf(payment.getMethod()).toLowerCase().contains(searchQuery) ||
                        String.valueOf(payment.getStatus()).toLowerCase().contains(searchQuery);
                if (!matchesQuery) {
                    return false; // Exclude payment if it doesn't match the search query
                }
            }

            // Filter by selected payment method (if any)
            if (selectedMethod != null && !selectedMethod.trim().isEmpty() && !selectedMethod.equals("null")) {
                boolean matchesMethod = selectedMethod.equalsIgnoreCase(String.valueOf(payment.getMethod()));
                if (!matchesMethod) {
                    return false; // Exclude payment if it doesn't match the selected method
                }
            }

            // Filter by selected payment status (if any)
            if (selectedStatus != null && !selectedStatus.trim().isEmpty() && !selectedStatus.equals("null")) {
                boolean matchesStatus = selectedStatus.equalsIgnoreCase(String.valueOf(payment.getStatus()));
                if (!matchesStatus) {
                    return false; // Exclude payment if it doesn't match the selected status
                }
            }

            // Filter by date range (start and end date, if any)
            if (startDate != null || endDate != null) {
                Date dueDate = payment.getDueDate();

                // Exclude if dueDate is null
                if (dueDate == null) {
                    return false;
                }

                // Check if due date is before the start date
                if (startDate != null && dueDate.before(startDate)) {
                    return false; // Exclude payment if due date is before start date
                }

                // Check if due date is after the end date
                if (endDate != null && dueDate.after(endDate)) {
                    return false; // Exclude payment if due date is after end date
                }
            }

            // Filter by Price Range (if any)
            if (selectedPrice != null) {
                try {
                    if (selectedPrice.equals("Under $100,000")) {
                        // Exclude payments with amount >= $100,000
                        if (payment.getAmount() >= 100000) {
                            return false;
                        }
                    } else if (selectedPrice.equals("Over $1,000,000")) {
                        // Exclude payments with amount <= $1,000,000
                        if (payment.getAmount() <= 1000000) {
                            return false;
                        }
                    } else {
                        // Handle ranges like "$10,000 - $19,999"
                        String[] priceRange = selectedPrice.replace("$", "").split(" - ");

                        // Check if price range is correctly split into two values
                        if (priceRange.length == 2) {
                            double minPrice = Double.parseDouble(priceRange[0].trim());
                            double maxPrice = Double.parseDouble(priceRange[1].trim());

                            // Check if the payment amount is within the specified range
                            if (payment.getAmount() < minPrice || payment.getAmount() > maxPrice) {
                                return false; // Exclude payment if the amount is outside the range
                            }
                        } else {
                            // Handle case where the price range format is incorrect
                            return false; // Exclude payment if the price range format is invalid
                        }
                    }
                } catch (NumberFormatException e) {
                    // Handle any NumberFormatExceptions caused by invalid price range format
                    return false; // Exclude payment if there's an error parsing the price range
                }
            }

            // Filter by Tenant (if selected)
            if (selectedTenant != null) {
                try {
                    int tenantId = Integer.parseInt(
                            selectedTenant.substring(selectedTenant.lastIndexOf("ID: ") + 4,
                                    selectedTenant.length() - 1));

                    if (payment.getTenantId() != tenantId) {
                        return false; // Exclude payment if tenant ID doesn't match
                    }
                } catch (NumberFormatException e) {
                    return false; // Exclude payment if there's an error parsing tenant ID
                }
            }

            // Keep the payment if it passes all filter conditions
            return true;
        });
    }

    @FXML
    public void createPayment() {
        try {
            // Load the 'create payment' view and initialize it
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/HostCreatePaymentView.fxml"));
            Parent root = loader.load();
            HostCreatePaymentView view = loader.getController();
            view.initData();

            // Set the loaded view as the main content in the current scene
            Scene currentScene = createPaymentButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Show error alert if view loading fails
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load create payment view: " + e.getMessage());
        }
    }

    @FXML
    public void viewPayment() {
        // Get the selected payment from the table view
        Payment selectedPayment = TableViewALLPayment.getSelectionModel().getSelectedItem();
        if (selectedPayment == null) {
            // Show error if no payment is selected
            CustomAlert.show(Alert.AlertType.ERROR, "No payment selected", "Please select a payment to view.");
            return;
        }

        try {
            // Load the 'view payment' view and initialize it with selected payment
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/trustmejunior/view/HostPaymentView.fxml"));
            Parent root = loader.load();
            HostPaymentView view = loader.getController();
            view.initData(selectedPayment);

            // Set the loaded view as the main content in the current scene
            Scene currentScene = viewPaymentButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Show error alert if view loading fails
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load payment view: " + e.getMessage());
        }
    }

    @FXML
    public void editPayment() {
        // Get the selected payment from the table view
        Payment selectedPayment = TableViewALLPayment.getSelectionModel().getSelectedItem();
        if (selectedPayment == null) {
            // Show error if no payment is selected
            CustomAlert.show(Alert.AlertType.ERROR, "No payment selected", "Please select a payment to edit.");
            return;
        }

        try {
            // Load the 'edit payment' view and initialize it with selected payment
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/HostEditPaymentView.fxml"));
            Parent root = loader.load();
            HostEditPaymentView view = loader.getController();
            view.initData(selectedPayment);

            // Set the loaded view as the main content in the current scene
            Scene currentScene = editPaymentButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            // Show error alert if view loading fails
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to load edit payment view: " + e.getMessage());
        }
    }

    @FXML
    public void deletePayment() {
        // Get selected payment
        Payment selectedPayment = TableViewALLPayment.getSelectionModel().getSelectedItem();

        // Show error if no payment selected
        if (selectedPayment == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No payment selected", "Please select a payment to delete.");
            return;
        }

        // Confirm delete action
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Payment");
        alert.setHeaderText("Delete Payment #" + selectedPayment.getPaymentId());
        alert.setContentText("Are you sure you want to delete this payment?");

        // Delete payment if confirmed
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

    @FXML
    void sendNotification(ActionEvent event) {
        // Get selected payment
        Payment selectedPayment = TableViewALLPayment.getSelectionModel().getSelectedItem();

        // Show error if no payment selected
        if (selectedPayment == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No payment selected", "Please select a payment to delete.");
            return;
        }

        // Check payment status to avoid sending notification if paid or late
        if (selectedPayment.getStatus() == PaymentStatus.PAID || selectedPayment.getStatus() == PaymentStatus.LATE) {
            CustomAlert.show(Alert.AlertType.WARNING, "Cannot send notification",
                    "This payment is PAID. Notifications cannot be sent.");
            return;
        }

        // Confirm notification sending
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Payment Notification");
        alert.setHeaderText("Send notification of unpaid payment #" + selectedPayment.getPaymentId() + " to Tenant #"
                + selectedPayment.getTenantId());
        alert.setContentText("Are you sure you want to send this notification?");

        // Send notification if confirmed
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                CreateNotificationRequest request = new CreateNotificationRequest(
                        selectedPayment.getPaymentId(),
                        selectedPayment.getTenantId(),
                        account.getAccountId(),
                        "You have an unpaid payment. Please settle it to avoid penalties.");
                NotificationController notificationController = new NotificationController();
                notificationController.sendNotification(request);

                // Show success message
                Alert confirmAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmAlert.setTitle("Success");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Notification sent successfully!");
                confirmAlert.showAndWait();
            } catch (Exception e) {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to send notification: " + e.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns
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

        // Populate dropdowns for payment methods and statuses
        droplistPaymentMethod.getItems().addAll(PaymentMethod.values());
        droplistPaymentStatus.getItems().addAll(PaymentStatus.values());

        // Add options for payment amounts
        droplistAmount.getItems().add("Under $100,000");

        // Add ranges from $100,000 to $999,999
        for (int start = 100000; start < 1000000; start += 100000) {
            int end = start + 99999;
            String range = String.format("$%d - $%d", start, end);
            droplistAmount.getItems().add(range);
        }

        // Add "Over $1,000,000" option
        droplistAmount.getItems().add("Over $1,000,000");
    }
}
