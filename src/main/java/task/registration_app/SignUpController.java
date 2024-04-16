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


    public void onClickSignUp(ActionEvent actionEvent) throws IOException {
        // get form input
        String username = signUpUsername.getText();
        String password = signUpPassword.getText();

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
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/signup-view.fxml")));
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