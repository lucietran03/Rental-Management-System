package com.trustmejunior.view;

/**
 * @author TrustMeJunior
 */

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Layout implements Initializable {
    @FXML
    private AnchorPane sidebarContainer;

    @FXML
    private AnchorPane mainContainer;

    public void setSidebar(Node sidebar) {
        sidebarContainer.getChildren().setAll(sidebar);
    }

    public void setMain(Node main) {
        mainContainer.getChildren().setAll(main);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
