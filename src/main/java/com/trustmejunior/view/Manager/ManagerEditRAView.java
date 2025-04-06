package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.Enum.RentalPeriod;
import com.trustmejunior.model.Enum.RentalStatus;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.model.User.Owner;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.request.UpdateRentalAgreementRequest;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ManagerEditRAView implements Initializable {
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
    private Button updateRAButton;

    @FXML
    private Button returnHomeButton;

    private Account account = SessionManager.getCurrentAccount();

    private RentalAgreement currentRA;

    private AccountController accountController = new AccountController();
    private HostController hostController = new HostController();
    private OwnerController ownerController = new OwnerController();
    private PropertyController propertyController = new PropertyController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private TenantController tenantController = new TenantController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize rental period and status dropdowns
        periodInput.setItems(FXCollections.observableArrayList(RentalPeriod.values()));
        periodInput.setValue(RentalPeriod.WEEKLY);

        statusInput.setItems(FXCollections.observableArrayList(RentalStatus.values()));
        statusInput.setValue(RentalStatus.ACTIVE);

        // Load and display owners with their properties
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

        // Load hosts when a property is selected
        ownerPropertyInput.setOnAction(event -> {
            String selection = ownerPropertyInput.getValue();
            loadHosts(selection);
        });
        hostsInput.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Load tenants and main tenant selection
        List<Tenant> tenants = tenantController.getAllTenants();
        List<String> tenantNames = tenants.stream()
                .map(tenant -> tenant.getFullName() + " (ID: " + tenant.getAccountId() + ")")
                .collect(Collectors.toList());
        mainTenantInput.setItems(FXCollections.observableArrayList(tenantNames));

        // Update sub-tenants when main tenant is selected
        mainTenantInput.setOnAction(event -> {
            String selectedMainTenant = mainTenantInput.getValue();
            loadSubTenants(selectedMainTenant);
        });
        subTenantsInput.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void loadHosts(String selection) {
        if (selection == null)
            return;

        // Extract property ID from selection and load hosts for that property
        int propertyId = extractId(selection.split(" - ")[1]);
        List<Host> propertyHosts = hostController.getHostsByPropertyId(propertyId);
        List<String> hostNames = propertyHosts.stream()
                .map(host -> host.getFullName() + " (ID: " + host.getAccountId() + ")")
                .collect(Collectors.toList());
        hostsInput.setItems(FXCollections.observableArrayList(hostNames));
    }

    private void loadSubTenants(String selection) {
        if (selection == null)
            return;

        // Extract main tenant ID and filter out that tenant from the list of
        // sub-tenants
        int mainTenantId = extractId(selection);
        List<Tenant> tenants = tenantController.getAllTenants();
        List<String> availableSubTenantNames = tenants.stream()
                .filter(tenant -> tenant.getAccountId() != mainTenantId)
                .map(tenant -> tenant.getFullName() + " (ID: " + tenant.getAccountId() + ")")
                .collect(Collectors.toList());
        subTenantsInput.setItems(FXCollections.observableArrayList(availableSubTenantNames));
    }

    public void initData(RentalAgreement rentalAgreement) {
        this.currentRA = rentalAgreement;

        // Initialize form fields with rental agreement data
        feeInput.setText(String.valueOf(rentalAgreement.getFee()));
        startDateInput.setValue(rentalAgreement.getStartDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        endDateInput.setValue(rentalAgreement.getEndDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        periodInput.setValue(rentalAgreement.getPeriod());
        statusInput.setValue(rentalAgreement.getStatus());

        // Set owner-property pair for rental agreement
        Account owner = accountController.getAccountById(rentalAgreement.getOwnerId());
        Property property = propertyController.getPropertyById(rentalAgreement.getPropertyId());
        String ownerPropertyValue = String.format("%s (ID: %d) - %s (ID: %d)",
                owner.getFullName(), owner.getAccountId(),
                property.getAddress(), property.getPropertyId());
        ownerPropertyInput.setValue(ownerPropertyValue);

        // Load hosts for the selected property
        String selection = ownerPropertyInput.getValue();
        loadHosts(selection);

        // Set selected hosts for rental agreement
        List<Host> hosts = hostController.getHostsByRentalAgreementId(rentalAgreement.getRentalAgreementId());
        if (hosts != null && !hosts.isEmpty()) {
            List<String> hostNames = hosts.stream()
                    .map(host -> host.getFullName() + " (ID: " + host.getAccountId() + ")")
                    .collect(Collectors.toList());
            for (String hostName : hostNames) {
                hostsInput.getSelectionModel().select(hostName);
            }
        }

        // Set main tenant for rental agreement
        Tenant mainTenant = tenantController.getMainTenantByRentalAgreementId(rentalAgreement.getRentalAgreementId());
        if (mainTenant != null) {
            Account mainTenantAccount = accountController.getAccountById(mainTenant.getAccountId());
            mainTenantInput.setValue(String.format("%s (ID: %d)",
                    mainTenantAccount.getFullName(), mainTenantAccount.getAccountId()));
        }

        // Load sub-tenants for main tenant
        loadSubTenants(mainTenantInput.getValue());

        // Set selected sub-tenants for rental agreement
        List<Tenant> subTenants = tenantController
                .getSubTenantsByRentalAgreementId(rentalAgreement.getRentalAgreementId());
        if (subTenants != null && !subTenants.isEmpty()) {
            List<String> subTenantNames = subTenants.stream()
                    .map(tenant -> {
                        Account tenantAccount = accountController.getAccountById(tenant.getAccountId());
                        return String.format("%s (ID: %d)", tenantAccount.getFullName(), tenantAccount.getAccountId());
                    })
                    .collect(Collectors.toList());
            for (String subTenant : subTenantNames) {
                subTenantsInput.getSelectionModel().select(subTenant);
            }
        }
    }

    // Extracts ID from a string by finding the portion between "ID: " and ")"
    private int extractId(String text) {
        int startIndex = text.indexOf("ID: ") + 4;
        int endIndex = text.indexOf(")", startIndex);
        return Integer.parseInt(text.substring(startIndex, endIndex));
    }

    @FXML
    public void updateRA(ActionEvent event) {
        try {
            // Validate inputs to ensure all required fields are filled
            if (feeInput.getText().isEmpty() || startDateInput.getValue() == null ||
                    endDateInput.getValue() == null || periodInput.getValue() == null ||
                    statusInput.getValue() == null || ownerPropertyInput.getValue() == null ||
                    hostsInput.getSelectionModel().getSelectedItems().isEmpty() ||
                    mainTenantInput.getValue() == null) {
                CustomAlert.show(Alert.AlertType.ERROR, "Error", "Please fill in all required fields");
                return;
            }

            // Extract owner and property IDs from the selection
            String ownerPropertySelection = ownerPropertyInput.getValue();
            String[] parts = ownerPropertySelection.split(" - ");
            String ownerName = parts[0];
            String propertyAddress = parts[1];
            int ownerId = extractId(ownerName);
            int propertyId = extractId(propertyAddress);

            // Create the update request object with user input
            UpdateRentalAgreementRequest request = new UpdateRentalAgreementRequest(
                    Double.parseDouble(feeInput.getText()),
                    Date.from(startDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(endDateInput.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    periodInput.getValue(),
                    statusInput.getValue(),
                    ownerId,
                    propertyId);

            // Update the rental agreement with the request object
            RentalAgreement updatedRA = rentalAgreementController.updateRentalAgreement(
                    currentRA.getRentalAgreementId(), request);

            // Set the hosts for the rental agreement
            List<Integer> hostIds = new ArrayList<>();
            for (String host : hostsInput.getSelectionModel().getSelectedItems()) {
                hostIds.add(extractId(host));
            }
            rentalAgreementController.setHostIds(updatedRA.getRentalAgreementId(), hostIds);

            // Set the main tenant for the rental agreement
            String mainTenantSelection = mainTenantInput.getValue();
            int mainTenantId = extractId(mainTenantSelection);
            rentalAgreementController.setMainTenantId(updatedRA.getRentalAgreementId(), mainTenantId);

            // Set the sub-tenants for the rental agreement
            List<Integer> subTenantIds = new ArrayList<>();
            for (String subTenant : subTenantsInput.getSelectionModel().getSelectedItems()) {
                subTenantIds.add(extractId(subTenant));
            }
            rentalAgreementController.setSubTenantIds(updatedRA.getRentalAgreementId(), subTenantIds);

            // Show success message and return to previous view
            CustomAlert.show(Alert.AlertType.INFORMATION, "Success", "Rental Agreement updated successfully");
            returnHome(event);

        } catch (Exception e) {
            // Handle errors and show error message
            CustomAlert.show(Alert.AlertType.ERROR, "Error", "Failed to update rental agreement: " + e.getMessage());
        }
    }

    @FXML
    public void returnHome(ActionEvent event) {
        try {
            // Load the ManagerAllRAView and display rental agreements
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/ManagerAllRAView.fxml"));
            Parent root = loader.load();
            ManagerAllRAView view = loader.getController();
            view.showAllRa(FXCollections.observableArrayList(rentalAgreementController.getAllRentalAgreements()));

            // Set the newly loaded view in the current scene
            Scene currentScene = returnHomeButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exception
        }
    }
}
