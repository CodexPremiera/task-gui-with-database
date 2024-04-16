module task.registration_app {
    requires javafx.controls;
    requires javafx.fxml;


    opens task.registration_app to javafx.fxml;
    exports task.registration_app;
}