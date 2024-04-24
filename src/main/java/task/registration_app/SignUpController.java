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
import javafx.stage.Stage;
import task.database.TblUserAccount;
import task.entities.UserAccount;

import java.io.IOException;
import java.util.Objects;

public class SignUpController {
    @FXML public TextField signUpUsername;
    @FXML public TextField signUpEmail;
    @FXML public PasswordField signUpPassword;
    @FXML public Button btnSignUp;
    @FXML public Label signUpRemark;
    @FXML public Button switchLogin;

    private Parent root;
    private Stage stage;
    private Scene scene;

    private UserAccount userAccount;

    /* METHODS */

    public void onClickSignUp(ActionEvent actionEvent) throws IOException {
        // get form input
        String username = signUpUsername.getText().trim();
        String password = signUpPassword.getText();
        String email = signUpEmail.getText().trim();

        // validations
        if (username.isEmpty() || password.trim().isEmpty() || email.isEmpty()) {
            signUpRemark.setText("Please fill in all the fields.");
            return;
        }
        if (!Utils.isValidEmail(email)) {
            signUpRemark.setText("Please enter a valid email.");
            return;
        }
        if (password.length() < 8) {
            signUpRemark.setText("Password must have 8 characters or more.");
            return;
        }

        try {
            this.userAccount = TblUserAccount.insert(username, email, password);
        } catch (IllegalArgumentException e) {
            signUpRemark.setText(e.getMessage());
            return;
        }

        // get controller of the profile view and launch it
        switchToProfile(actionEvent);
    }

    private void switchToProfile(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/profile-view.fxml"));
        root = loader.load();

        ProfileController profileController = loader.getController();
        profileController.setUserAccount(userAccount);
        profileController.showProfile();

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void setSwitchLogin(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/login-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}