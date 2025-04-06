package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.*;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.Enum.RentalPeriod;
import com.trustmejunior.model.Enum.RentalStatus;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.model.User.Owner;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.request.CreatePaymentRequest;
import com.trustmejunior.request.CreateRentalAgreementRequest;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ManagerCreateRAView implements Initializable {
    @FXML
    private TextField feeInput;

    @FXML
    private DatePicker startDateInput;

    @FXML
    private DatePicker endDateInput;

    @FXML
    private ChoiceBox<RentalPeriod> periodInput;

    @FXML
    private ChoiceBox<RentalStatus> statusInput;

    @FXML
    private ComboBox<String> ownerPropertyInput;

    @FXML
    private ListView<String> hostsInput;

    @FXML
    private ComboBox<String> mainTenantInput;

    @FXML
    private ListView<String> subTenantsInput;

    @FXML
    private Button createRAButton;

    @FXML
    private Button returnHomeButton;

    private Account account = SessionManager.getCurrentAccount();

    private HostController hostController = new HostController();
    private OwnerController ownerController = new OwnerController();
    private PropertyController propertyController = new PropertyController();
    private TenantController tenantController = new TenantController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();

    public void initData() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize period and status input values
        periodInput.setItems(FXCollections.observableArrayList(RentalPeriod.values()));
        periodInput.setValue(RentalPeriod.WEEKLY);

        statusInput.setItems(FXCollections.observableArrayList(RentalStatus.ACTIVE));
        statusInput.setValue(RentalStatus.ACTIVE);

        // Load owners and their properties into the ownerPropertyInput
        List<Owner> owners = ownerController.getAllOwners();
        List<String> ownerPropertyPairs = new ArrayList<>();
        for (Owner owner : owners) {
            List<Property> ownerProperties = propertyController.getPropertiesByOwnerId(owner.getAccountId());
            for (Property property : ownerProperties) {
                ownerPropertyPairs.add(String.format("%s (ID: %d) - %s (ID: %d)",
                        owner.getFullName(),
                        owner.getAccountId(),
                        property.getAddress(),
                        property.getPropertyId()));
            }
        }
        ownerPropertyInput.setItems(FXCollections.observableArrayList(ownerPropertyPairs));

        // Set action listener for ownerPropertyInput to load hosts based on selection
        ownerPropertyInput.setOnAction(event -> {
            String selection = ownerPropertyInput.getValue();
            loadHosts(selection);
        });
        hostsInput.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Load tenants into the mainTenantInput
        List<Tenant> tenants = tenantController.getAllTenants();
        List<String> tenantNames = tenants.stream()
                .map(tenant -> tenant.getFullName() + " (ID: " + tenant.getAccountId() + ")")
                .collect(Collectors.toList());
        mainTenantInput.setItems(FXCollections.observableArrayList(tenantNames));

        // Add listener to update subTenantsInput based on selected main tenant
        mainTenantInput.setOnAction(event -> {
            String selectedMainTenant = mainTenantInput.getValue();
            loadSubTenants(selectedMainTenant);
        });
        subTenantsInput.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void loadHosts(String selection) {
        if (selection == null)
            return;

        // Extract property ID from the selection
        int propertyId = extractId(selection.split(" - ")[1]);

        // Get hosts associated with this property and update hostsInput
        List<Host> propertyHosts = hostController.getHostsByPropertyId(propertyId);
        List<String> hostNames = propertyHosts.stream()
                .map(host -> host.getFullName() + " (ID: " + host.getAccountId() + ")")
                .collect(Collectors.toList());
        hostsInput.setItems(FXCollections.observableArrayList(hostNames));
    }

    private void loadSubTenants(String selection) {
        if (selection == null)
            return;

        // Extract main tenant ID from the selection
        int mainTenantId = extractId(selection);

        // Get and filter tenants to exclude the main tenant
        List<Tenant> tenants = tenantController.getAllTenants();
        List<String> availableSubTenantNames = tenants.stream()
                .filter(tenant -> tenant.getAccountId() != mainTenantId)
                .map(tenant -> tenant.getFullName() + " (ID: " + tenant.getAccountId() + ")")
                .collect(Collectors.toList());
        subTenantsInput.setItems(FXCollections.observableArrayList(availableSubTenantNames));
    }

    @FXML
    public void createRA(ActionEvent event) {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }

            // Extract owner and property IDs from selected values
            String ownerPropertySelection = ownerPropertyInput.getValue();
            String[] parts = ownerPropertySelection.split(" - ");
            String ownerName = parts[0];
            String propertyAddress = parts[1];
            int ownerId = extractId(ownerName);
            int propertyId = extractId(propertyAddress);

            // Create rental agreement request object
            CreateRentalAgreementRequest request = new CreateRentalAgreementRequest(
                    Double.parseDouble(feeInput.getText()),
                    Date.from(startDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(endDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    periodInput.getValue(),
                    statusInput.getValue(),
                    ownerId,
                    propertyId);

            // Create rental agreement
            RentalAgreement rentalAgreement = rentalAgreementController.createRentalAgreement(request);

            // Set hosts for the rental agreement
            List<Integer> hostIds = new ArrayList<>();
            for (String host : hostsInput.getSelectionModel().getSelectedItems()) {
                hostIds.add(extractId(host));
            }
            rentalAgreementController.setHostIds(rentalAgreement.getRentalAgreementId(), hostIds);

            // Set main tenant for the rental agreement
            String mainTenantSelection = mainTenantInput.getValue();
            int mainTenantId = extractId(mainTenantSelection);
            rentalAgreementController.setMainTenantId(rentalAgreement.getRentalAgreementId(), mainTenantId);

            // Set sub-tenants for the rental agreement
            List<Integer> subTenantIds = new ArrayList<>();
            for (String subTenant : subTenantsInput.getSelectionModel().getSelectedItems()) {
                subTenantIds.add(extractId(subTenant));
            }
            rentalAgreementController.setSubTenantIds(rentalAgreement.getRentalAgreementId(), subTenantIds);

            // Create periodic payments for the rental agreement
            createPeriodicPayments(rentalAgreement);

            // Show success message
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Rental Agreement created successfully");

            // Return to previous view
            returnHome(event);

        } catch (Exception e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to create rental agreement: " + e.getMessage());
        }
    }

    private void createPeriodicPayments(RentalAgreement rentalAgreement) throws Exception {
        // Retrieve details from the rental agreement
        double fee = rentalAgreement.getFee();
        Date startDate = rentalAgreement.getStartDate();
        Date endDate = rentalAgreement.getEndDate();
        RentalPeriod period = rentalAgreement.getPeriod();
        int rentalAgreementId = rentalAgreement.getRentalAgreementId();

        // Get the main tenant for the rental agreement
        TenantController tenantController = new TenantController();
        Tenant tenant = tenantController.getMainTenantByRentalAgreementId(rentalAgreementId);

        // Validate tenant ID
        if (tenant.getAccountId() <= 0) {
            throw new Exception("Invalid tenant ID for the created rental agreement.");
        }

        // Generate payment dates based on the rental period
        List<Date> paymentDates = generatePaymentDates(startDate, endDate, period);

        // Create a payment for each payment date
        for (Date paymentDate : paymentDates) {
            CreatePaymentRequest paymentRequest = new CreatePaymentRequest(
                    fee,
                    paymentDate,
                    null,
                    PaymentStatus.PENDING,
                    rentalAgreementId,
                    tenant.getAccountId());

            // Create payment through the PaymentController
            PaymentController paymentController = new PaymentController();
            paymentController.createPayment(paymentRequest);
        }
    }

    private List<Date> generatePaymentDates(Date startDate, Date endDate, RentalPeriod period) {
        List<Date> paymentDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        // Generate payment dates based on the rental period (weekly, monthly, yearly)
        while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
            paymentDates.add(calendar.getTime());

            // Adjust the calendar based on the period
            switch (period) {
                case WEEKLY:
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                    break;
                case MONTHLY:
                    calendar.add(Calendar.MONTH, 1);
                    break;
                case YEARLY:
                    calendar.add(Calendar.YEAR, 1);
                    break;
            }
        }

        return paymentDates;
    }

    @FXML
    public void returnHome(ActionEvent event) {
        try {
            // Load the ManagerAllRAView and display all rental agreements
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllRAView.fxml"));
            Parent root = loader.load();
            ManagerAllRAView view = loader.getController();
            view.showAllRa(FXCollections.observableArrayList(rentalAgreementController.getAllRentalAgreements()));

            // Set the scene to show the ManagerAllRAView
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle any loading errors
        }
    }

    private boolean validateInputs() {
        // Validate fee input
        try {
            double fee = Double.parseDouble(feeInput.getText());
            if (fee <= 0) {
                CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Fee must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Fee must be a valid number");
            return false;
        }

        // Validate start and end dates
        if (startDateInput.getValue() == null || endDateInput.getValue() == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select both start and end dates");
            return false;
        }

        if (startDateInput.getValue().isAfter(endDateInput.getValue())) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Start date must be before end date");
            return false;
        }

        // Validate rental period and status selection
        if (periodInput.getValue() == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select a rental period");
            return false;
        }

        if (statusInput.getValue() == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select a rental status");
            return false;
        }

        // Validate owner-property selection
        if (ownerPropertyInput.getValue() == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select an owner and property");
            return false;
        }

        // Validate hosts selection
        if (hostsInput.getSelectionModel().getSelectedItems().isEmpty()) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select at least one host");
            return false;
        }

        // Validate main tenant selection
        if (mainTenantInput.getValue() == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "Validation Error", "Please select a main tenant");
            return false;
        }

        return true; // Return true if all validations pass
    }

    private int extractId(String selection) {
        // Extract ID from a string in the format "Name (ID: X)"
        int startIndex = selection.indexOf("ID: ") + 4;
        int endIndex = selection.indexOf(")");
        return Integer.parseInt(selection.substring(startIndex, endIndex));
    }
}
