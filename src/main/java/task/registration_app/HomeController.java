package task.registration_app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import task.entities.UserAccount;

import java.io.IOException;
import java.util.Objects;

public class HomeController {
    @FXML public TextField homeUsername;
    @FXML public TextField homeEmail;
    @FXML public TextField homePassword;
    @FXML public Button homeLogout;

    private Parent root;
    private Stage stage;
    private Scene scene;
    private UserAccount userAccount;


    public void launchHome(ActionEvent actionEvent, UserAccount userAccount) throws IOException {
        this.userAccount = userAccount;
        launchHome(actionEvent);
    }

    public void launchHome(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/home-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public void onClickLogout(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/login-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}