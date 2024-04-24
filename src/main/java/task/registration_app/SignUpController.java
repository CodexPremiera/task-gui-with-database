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
import java.sql.SQLException;
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

    /* METHODS */
    public void launch(ActionEvent actionEvent, Parent launchRoot) throws IOException {
        this.root = launchRoot;

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void onClickSignUp(ActionEvent actionEvent) throws IOException {
        // get form input
        String username = signUpUsername.getText().trim();
        String password = signUpPassword.getText();
        String email = signUpEmail.getText().trim();
        UserAccount userAccount;

        // input validations
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

        // try insert and get row
        try {
            userAccount = TblUserAccount.insert(username, email, password);
        } catch (IllegalArgumentException e) {
            signUpRemark.setText(e.getMessage());
            return;
        }

        if (userAccount == null) {
            signUpRemark.setText("Error occurred in database.");
            return;
        }

        switchToLanding(actionEvent, userAccount);
    }

    private void switchToLanding(ActionEvent actionEvent, UserAccount userAccount) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/landing-view.fxml"));
        root = loader.load();

        LandingController landingController = loader.getController();
        landingController.launch(actionEvent, root, userAccount);
    }


    public void setSwitchLogin(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/login-view.fxml"));
        root = loader.load();

        LoginController loginController = loader.getController();
        loginController.launch(actionEvent, root);
    }
}