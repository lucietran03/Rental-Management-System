package com.trustmejunior.view.Manager;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.AccountController;
import com.trustmejunior.controller.HostController;
import com.trustmejunior.controller.ManagerController;
import com.trustmejunior.controller.OwnerController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.model.User.Manager;
import com.trustmejunior.model.User.Owner;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.DateFormatter;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;

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
import javafx.geometry.Side;
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

public class ManagerAllAccountView implements Initializable {
    @FXML
    private ImageView ManagerLogoImageView;

    @FXML
    private TableView<Account> TableAllAccountManagerView;

    @FXML
    private TableColumn<Account, Integer> id;

    @FXML
    private TableColumn<Account, String> username;

    @FXML
    private TableColumn<Account, String> password;

    @FXML
    private TableColumn<Account, String> fullname;

    @FXML
    private TableColumn<Account, String> Email;

    @FXML
    private TableColumn<Account, String> date_of_birth;

    @FXML
    private TableColumn<Account, AccountRole> role;

    @FXML
    private TextField textfieldsearch;

    @FXML
    private Button createAccountButton;

    @FXML
    private Button viewAccountButton;

    @FXML
    private Button editAccountButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private ContextMenu contextMenuCreateAccount;

    @FXML
    private MenuItem buttonTenant;

    @FXML
    private MenuItem buttonOwner;

    @FXML
    private MenuItem buttonHost;

    @FXML
    private MenuItem buttonManager;

    @FXML
    private ComboBox<AccountRole> droplistRole;

    @FXML
    private Button removeFilterButton;

    private ObservableList<Account> accountList;

    private Account account = SessionManager.getCurrentAccount();

    public void initData() {
    }

    public void showAllAccounts(ObservableList<Account> accounts) {
        accountList = FXCollections.observableArrayList(accounts);

        FilteredList<Account> filteredData = new FilteredList<>(accountList, p -> true);

        // Set up search functionality
        textfieldsearch.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        droplistRole.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        SortedList<Account> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TableAllAccountManagerView.comparatorProperty());
        TableAllAccountManagerView.setItems(sortedData);
    }

    private void applyFilters(FilteredList<Account> filteredList) {
        // Get the search query and selected filter options
        String searchQuery = textfieldsearch.getText().toLowerCase();
        String selectedRole = String.valueOf(droplistRole.getSelectionModel().getSelectedItem());

        // Set the predicate to filter the list based on the selected criteria
        filteredList.setPredicate(account -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            // Filter by Search Query (if any)
            if (!searchQuery.isEmpty()) {
                boolean matchesQuery = account.getUsername().toLowerCase().contains(searchQuery) ||
                        account.getPassword().toLowerCase().contains(searchQuery) ||
                        account.getFullName().toLowerCase().contains(searchQuery) ||
                        account.getEmail().toLowerCase().contains(searchQuery) ||
                        dateFormat.format(account.getDob()).toLowerCase().contains(searchQuery) ||
                        account.getAccountRole().toString().toLowerCase().contains(selectedRole);
                if (!matchesQuery) {
                    return false; // Exclude account if it doesn't match the search query
                }
            }

            // Filter by selected payment method (if any)
            if (selectedRole != null && !selectedRole.trim().isEmpty() && !selectedRole.equals("null")) {
                boolean matchesRole = selectedRole
                        .equalsIgnoreCase(String.valueOf(account.getAccountRole().toString()));
                if (!matchesRole) {
                    return false; // Exclude payment if it doesn't match the selected method
                }
            }
            // Keep if it passes all filter conditions
            return true;
        });
    }

    @FXML
    void removeFilter() {
        // Clear the search text field and reset the role dropdown selection
        textfieldsearch.clear();
        droplistRole.getSelectionModel().clearSelection();
    }

    @FXML
    public void createAccount(ActionEvent event) throws IOException {
        // Show context menu for account creation when the button is clicked
        contextMenuCreateAccount.show(createAccountButton, Side.BOTTOM, 0, 0);
    }

    @FXML
    public void viewAccount(ActionEvent event) {
        // Retrieve selected account from the table
        Account account = TableAllAccountManagerView.getSelectionModel().getSelectedItem();
        if (account == null) {
            // Show error if no account is selected
            CustomAlert.show(Alert.AlertType.ERROR, "No account selected", "Please select an account to view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            AccountRole accountRole = account.getAccountRole();

            // Load the appropriate view based on the account role
            switch (accountRole) {
                case TENANT -> {
                    loader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerTenantView.fxml"));
                    root = loader.load();
                    ManagerTenantView view = loader.getController();
                    TenantController tenantController = new TenantController();
                    Tenant tenant = tenantController.getTenantByAccountId(account.getAccountId());
                    view.initData(tenant);
                }
                case HOST -> {
                    loader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerHostView.fxml"));
                    root = loader.load();
                    ManagerHostView view = loader.getController();
                    HostController hostController = new HostController();
                    Host host = hostController.getHostByAccountId(account.getAccountId());
                    view.initData(host);
                }
                case OWNER -> {
                    loader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerOwnerView.fxml"));
                    root = loader.load();
                    ManagerOwnerView view = loader.getController();
                    OwnerController ownerController = new OwnerController();
                    Owner owner = ownerController.getOwnerByAccountId(account.getAccountId());
                    view.initData(owner);
                }
                case MANAGER -> {
                    loader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerManagerView.fxml"));
                    root = loader.load();
                    ManagerManagerView view = loader.getController();
                    ManagerController managerController = new ManagerController();
                    Manager manager = managerController.getManagerByAccountId(account.getAccountId());
                    view.initData(manager);
                }
                default -> {
                    // Show error for invalid account role
                    CustomAlert.show(Alert.AlertType.ERROR, "Invalid account role",
                            "The account role is invalid. Please try again.");
                }
            }

            // Set the main layout to the loaded view if successful
            if (root != null) {
                Scene currentScene = viewAccountButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void editAccount(ActionEvent event) {
        // Retrieve selected account from the table
        Account account = TableAllAccountManagerView.getSelectionModel().getSelectedItem();
        if (account == null) {
            // Show error if no account is selected
            CustomAlert.show(Alert.AlertType.ERROR, "No account selected", "Please select an account to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            AccountRole accountRole = account.getAccountRole();

            // Load the appropriate view based on the account role for editing
            switch (accountRole) {
                case TENANT -> {
                    loader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerEditTenantView.fxml"));
                    root = loader.load();
                    ManagerEditTenantView view = loader.getController();
                    TenantController tenantController = new TenantController();
                    Tenant tenant = tenantController.getTenantByAccountId(account.getAccountId());
                    view.initData(tenant);
                }
                case HOST -> {
                    loader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerEditHostView.fxml"));
                    root = loader.load();
                    ManagerEditHostView view = loader.getController();
                    HostController hostController = new HostController();
                    Host host = hostController.getHostByAccountId(account.getAccountId());
                    view.initData(host);
                }
                case OWNER -> {
                    loader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerEditOwnerView.fxml"));
                    root = loader.load();
                    ManagerEditOwnerView view = loader.getController();
                    OwnerController ownerController = new OwnerController();
                    Owner owner = ownerController.getOwnerByAccountId(account.getAccountId());
                    view.initData(owner);
                }
                case MANAGER -> {
                    loader.setLocation(getClass().getResource("/com/trustmejunior/view/ManagerEditManagerView.fxml"));
                    root = loader.load();
                    ManagerEditManagerView view = loader.getController();
                    ManagerController managerController = new ManagerController();
                    Manager manager = managerController.getManagerByAccountId(account.getAccountId());
                    view.initData(manager);
                }
                default -> {
                    // Show error for invalid account role
                    CustomAlert.show(Alert.AlertType.ERROR, "Invalid account role",
                            "The account role is invalid. Please try again.");
                }
            }

            // Set the main layout to the loaded view if successful
            if (root != null) {
                Scene currentScene = editAccountButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Deletes the selected account after confirming the action
    @FXML
    public void deleteAccount(ActionEvent event) {
        // Get the selected account from the table
        Account selectedAccount = TableAllAccountManagerView.getSelectionModel().getSelectedItem();

        // Show an error if no account is selected
        if (selectedAccount == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No account selected",
                    "Please select an account to delete.");
            return;
        }

        // Confirm the deletion action with a confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this account?");

        // If confirmed, delete the account and remove it from the list
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            AccountController accountController = new AccountController();
            accountController.deleteAccountById(selectedAccount.getAccountId());
            accountList.remove(selectedAccount);
        }
    }

    // Initializes the table columns and dropdown for account role selection
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table column bindings for account details
        id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAccountId()).asObject());
        username.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        password.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
        Email.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        date_of_birth.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateFormatter.formatDate(cellData.getValue().getDob(), "dd/MM/yyyy")));
        role.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAccountRole()));
        fullname.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));

        // Populate the role dropdown with available account roles
        droplistRole.getItems().addAll(AccountRole.values());

        // Set up action handlers for creating different account types
        buttonTenant.setOnAction(ev -> {
            contextMenuCreateAccount.hide();
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerCreateTenantView.fxml"));
                Parent root = loader.load();
                ManagerCreateTenantView view = loader.getController();
                view.initData();

                Scene currentScene = createAccountButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Similarly handle actions for creating Owner, Host, and Manager accounts
        buttonOwner.setOnAction(ev -> {
            contextMenuCreateAccount.hide();
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerCreateOwnerView.fxml"));
                Parent root = loader.load();
                ManagerCreateOwnerView view = loader.getController();
                view.initData();

                Scene currentScene = createAccountButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buttonHost.setOnAction(ev -> {
            contextMenuCreateAccount.hide();
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerCreateHostView.fxml"));
                Parent root = loader.load();
                ManagerCreateHostView view = loader.getController();
                view.initData();

                Scene currentScene = createAccountButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buttonManager.setOnAction(ev -> {
            contextMenuCreateAccount.hide();
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/trustmejunior/view/ManagerCreateManagerView.fxml"));
                Parent root = loader.load();
                ManagerCreateManagerView view = loader.getController();
                view.initData();

                Scene currentScene = createAccountButton.getScene();
                Layout layoutController = (Layout) currentScene.getUserData();
                layoutController.setMain(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
