module task.registration_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens task.registration_app to javafx.fxml;
    exports task.registration_app;
}