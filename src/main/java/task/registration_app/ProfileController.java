package task.registration_app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import task.entities.UserAccount;

import java.io.IOException;
import java.util.Objects;

public class ProfileController {
    /* FXML ELEMENTS */
    @FXML public TextField profileUsername;
    @FXML public TextField profileEmail;
    @FXML public TextField profilePassword;
    @FXML public Button profileLogout;

    /* FIELDS */
    private Parent root;
    private Stage stage;
    private Scene scene;
    private UserAccount userAccount;


    /* METHODS */
    public void launchProfile(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/profile-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void showProfile() {
        profileUsername.setText(userAccount.getUsername());
        profileEmail.setText(userAccount.getEmail());
        profilePassword.setText(userAccount.getPassword());
    }

    public void onClickSave(ActionEvent actionEvent) throws IOException {
        showProfile();
    }

    public void onClickLogout(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/login-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}