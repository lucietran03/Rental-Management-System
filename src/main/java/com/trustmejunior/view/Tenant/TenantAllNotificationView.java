package com.trustmejunior.view.Tenant;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.NotificationController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.utils.CustomAlert;
import com.trustmejunior.utils.SessionManager;
import com.trustmejunior.view.Layout;
import com.trustmejunior.model.Notification.Notification;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.collections.ObservableList;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TenantAllNotificationView implements Initializable {
    @FXML
    private TableView<Notification> TableViewALLNotification;

    @FXML
    private TableColumn<Notification, String> content;

    @FXML
    private TableColumn<Notification, Integer> id;

    @FXML
    private TableColumn<Notification, Integer> paymentId;

    @FXML
    private TableColumn<Notification, Integer> senderId;

    @FXML
    private TableColumn<Notification, String> time;

    @FXML
    private Button viewNotificationButton;

    private ObservableList<Notification> notificationList;

    private Account account = SessionManager.getCurrentAccount();

    private NotificationController notificationController = new NotificationController();

    @FXML
    void viewNotification(ActionEvent event) {
        // Get the selected notification from the TableView
        Notification selectedNotification = TableViewALLNotification.getSelectionModel().getSelectedItem();

        // Check if a notification is selected, show an error if not
        if (selectedNotification == null) {
            CustomAlert.show(Alert.AlertType.ERROR, "No notification selected",
                    "Please select a notification to view.");
            return;
        }

        try {
            // Load the TenantNotificationView and pass the selected notification ID for
            // further processing
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/trustmejunior/view/TenantNotificationView.fxml"));
            Parent root = loader.load();
            TenantNotificationView view = loader.getController();
            view.initData(selectedNotification);

            // Set the newly loaded view as the main scene in the current layout
            Scene currentScene = viewNotificationButton.getScene();
            Layout layoutController = (Layout) currentScene.getUserData();
            layoutController.setMain(root);
        } catch (IOException e) {
            e.printStackTrace(); // Log any I/O exception during loading the view
        }
    }

    public void showAllNotifications(ObservableList<Notification> notifications) {
        // Display all notifications in the TableView
        TableViewALLNotification.setItems(FXCollections.observableArrayList(notifications));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize TableView columns by binding data properties for each field
        id.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getNotificationId()).asObject());
        content.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));
        paymentId.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getPaymentId()).asObject());
        senderId.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getSenderId()).asObject());
        time.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedSentTime()));
    }
}
