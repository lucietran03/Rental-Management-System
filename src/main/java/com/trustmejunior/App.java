package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ConnectionManager.initConnection();

        Parent root = FXMLLoader
                .load(Objects.requireNonNull(getClass().getResource("/com/trustmejunior/view/LoginView.fxml")));

        Scene scene = new Scene(root);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/trustmejunior/images/appLogo.png")));
        stage.setTitle("Rental Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
