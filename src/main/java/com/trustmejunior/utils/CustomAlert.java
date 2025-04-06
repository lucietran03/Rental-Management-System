package com.trustmejunior.utils;

/**
 * @author TrustMeJunior
 */

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class CustomAlert {
    public static void show(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }
}
