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

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML public AnchorPane loginBorderPane;
    @FXML public AnchorPane switchToSignupContainer;
    @FXML public Button switchToSignUp;

    @FXML public AnchorPane loginContainer;
    @FXML public TextField loginUsername;
    @FXML public PasswordField loginPassword;
    @FXML public Button btnLogin;
    @FXML public Label loginRemark;

    private Parent root;
    private Stage stage;
    private Scene scene;


    public void onClickLogin(ActionEvent actionEvent) throws IOException {
        // get form input
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        // verify user
        /*if (!users.getList().containsKey(username)) {
            labelRemark.setText("User doesn't exist");
            return;
        }

        // verify password
        User user = users.getList().get(username);

        if (!user.getPassword().equals(password)) {
            labelRemark.setText("Wrong Password");
            return;
        }*/

        // login user
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/home-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    public void onClickLogout(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/Login.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}