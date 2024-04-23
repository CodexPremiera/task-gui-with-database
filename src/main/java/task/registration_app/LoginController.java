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
import java.sql.SQLException;
import java.util.Objects;

public class LoginController {
    @FXML public AnchorPane loginContainer;
    @FXML public TextField loginEmail;
    @FXML public PasswordField loginPassword;
    @FXML public Button btnLogin;
    @FXML public Label loginRemark;

    private Parent root;
    private Stage stage;
    private Scene scene;
    private UserAccount userAccount;
    private HomeController homeController = new HomeController();


    public void onClickLogin(ActionEvent actionEvent) throws IOException {
        // get form input
        String username = loginEmail.getText().trim();
        String password = loginPassword.getText();

        // validations
        if (username.equals("") || password.trim().equals("")) {
            loginRemark.setText("Please fill in all the fields.");
            return;
        }

        try {
            this.userAccount = TblUserAccount.retrieve(username, password);
        } catch (IllegalArgumentException e) {
            loginRemark.setText(e.getMessage());
            return;
        } catch (SQLException e) {
            loginRemark.setText("Error occurred in database.");
            return;
        }

        // enter home
        homeController.launchHome(actionEvent, userAccount);
    }

    public void setSwitchSignUp(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/signup-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}