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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import task.database.TblUserAccount;
import task.entities.UserAccount;

import java.io.IOException;

public class LoginController {
    @FXML public AnchorPane loginContainer;
    @FXML public TextField loginEmail;
    @FXML public PasswordField loginPassword;
    @FXML public Button btnLogin;
    @FXML public Label loginRemark;

    private Parent root;
    private Stage stage;
    private Scene scene;


    public void launch(ActionEvent actionEvent, Parent launchRoot) throws IOException {
        this.root = launchRoot;

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    public void onClickLogin(ActionEvent actionEvent) throws IOException {
        // get form input
        String username = loginEmail.getText().trim();
        String password = loginPassword.getText();
        UserAccount userAccount;

        // validations
        if (username.equals("") || password.trim().equals("")) {
            loginRemark.setText("Please fill in all the fields.");
            return;
        }

        try {
            userAccount = TblUserAccount.retrieve(username, password);
        } catch (IllegalArgumentException e) {
            loginRemark.setText(e.getMessage());
            return;
        }

        // get controller of the profile view and launch it
        if (userAccount == null) {
            loginRemark.setText("Error occurred in database.");
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

    public void setSwitchSignUp(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/signup-view.fxml"));
        root = loader.load();

        SignUpController signUpController = loader.getController();
        signUpController.launch(actionEvent, root);
    }
}