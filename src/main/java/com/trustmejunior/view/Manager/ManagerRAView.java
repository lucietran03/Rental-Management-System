package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.utils.DateFormatter;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerRAView implements Initializable {
    @FXML
    private Label fee;

    @FXML
    private Label startDate;

    @FXML
    private Label endDate;

    @FXML
    private Label period;

    @FXML
    private Label status;

    @FXML
    private Label owner;

    @FXML
    private Label property;

    @FXML
    private VBox hostsVBox;

    @FXML
    private Label mainTenant;

    @FXML
    private VBox subTenantsVBox;

    @FXML
    private Button returnButton;

    private Account account = SessionManager.getCurrentAccount();

    private AccountController accountController = new AccountController();
    private HostController hostController = new HostController();
    private PropertyController propertyController = new PropertyController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private TenantController tenantController = new TenantController();

    // Initializes rental agreement data into the form fields
    public void initData(RentalAgreement rentalAgreement) {
        // Set basic rental agreement information
        fee.setText(String.format("$%.2f", rentalAgreement.getFee()));
        startDate.setText(DateFormatter.formatDate(rentalAgreement.getStartDate(), "dd/MM/yyyy"));
        endDate.setText(DateFormatter.formatDate(rentalAgreement.getEndDate(), "dd/MM/yyyy"));
        period.setText(rentalAgreement.getPeriod().toString());
        status.setText(rentalAgreement.getStatus().toString());

        // Set owner information
        Account ownerAccount = accountController.getAccountById(rentalAgreement.getOwnerId());
        owner.setText(String.format("%s (ID: %d)", ownerAccount.getFullName(), ownerAccount.getAccountId()));

        // Set property information
        Property propertyObj = propertyController.getPropertyById(rentalAgreement.getPropertyId());
        property.setText(String.format("%s (ID: %d)", propertyObj.getAddress(), propertyObj.getPropertyId()));

        // Set hosts information
        List<Host> hosts = hostController.getHostsByRentalAgreementId(rentalAgreement.getRentalAgreementId());
        hostsVBox.getChildren().clear();
        for (Host host : hosts) {
            Account hostAccount = accountController.getAccountById(host.getAccountId());
            Label hostLabel = new Label(
                    String.format("%s (ID: %d)", hostAccount.getFullName(), hostAccount.getAccountId()));
            hostsVBox.getChildren().add(hostLabel);
        }

        // Set main tenant information
        Tenant mainTenantObj = tenantController
                .getMainTenantByRentalAgreementId(rentalAgreement.getRentalAgreementId());
        if (mainTenantObj != null) {
            Account mainTenantAccount = accountController.getAccountById(mainTenantObj.getAccountId());
            mainTenant.setText(
                    String.format("%s (ID: %d)", mainTenantAccount.getFullName(), mainTenantAccount.getAccountId()));
        }

        // Set sub-tenants information
        List<Tenant> subTenants = tenantController
                .getSubTenantsByRentalAgreementId(rentalAgreement.getRentalAgreementId());
        subTenantsVBox.getChildren().clear();
        for (Tenant subTenant : subTenants) {
            Account subTenantAccount = accountController.getAccountById(subTenant.getAccountId());
            Label subTenantLabel = new Label(
                    String.format("%s (ID: %d)", subTenantAccount.getFullName(), subTenantAccount.getAccountId()));
            subTenantsVBox.getChildren().add(subTenantLabel);
        }
    }

    // Initializes the return button action to navigate to the ManagerAllRAView
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        returnButton.setOnAction(event -> {
            try {
                // Load the ManagerAllRAView
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerAllRAView.fxml"));
                Parent root = loader.load();
                ManagerAllRAView view = loader.getController();
                view.showAllRa(FXCollections.observableArrayList(rentalAgreementController.getAllRentalAgreements()));

                // Set the main layout with the newly loaded view
                Scene currentScene = returnButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
