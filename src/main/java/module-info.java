module com.trustmejunior {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.net.http;

    exports com.trustmejunior;

    opens com.trustmejunior to javafx.fxml;

    exports com.trustmejunior.view;

    opens com.trustmejunior.view to javafx.fxml;

    exports com.trustmejunior.view.Manager;

    opens com.trustmejunior.view.Manager to javafx.fxml;

    exports com.trustmejunior.view.Tenant;

    opens com.trustmejunior.view.Tenant to javafx.fxml;

    exports com.trustmejunior.view.Host;

    opens com.trustmejunior.view.Host to javafx.fxml;

    exports com.trustmejunior.view.Owner;

    opens com.trustmejunior.view.Owner to javafx.fxml;

    exports com.trustmejunior.view.Visitor;

    opens com.trustmejunior.view.Visitor to javafx.fxml;
}