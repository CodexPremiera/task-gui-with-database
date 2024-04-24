package task.registration_app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import task.entities.UserAccount;

import java.io.IOException;

public class LandingController {
    /* ELEMENTS */
    @FXML public Label landingGreeting;

    @FXML public Button btnLandingEdit;
    @FXML public Button btnLandingSubmit;
    @FXML public Button btnLandingDelete;
    @FXML public Button btnLandingUpdate;

    @FXML public TextArea landingInput;
    @FXML public Label landingRemark;

    @FXML public TableView todoTable;

    private Parent root;
    private Stage stage;
    private Scene scene;
    private UserAccount userAccount;


    /* METHODS */
    public void launch(ActionEvent actionEvent, Parent launchRoot, UserAccount userAccount) throws IOException {
        this.setUserAccount(userAccount);
        this.root = launchRoot;

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        btnLandingUpdate.setVisible(false);
        btnLandingDelete.setVisible(false);
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        landingGreeting.setText("Hello, " + userAccount.getUsername());
    }

    public void onClickOpenProfile(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/profile-view.fxml"));
        root = loader.load();

        ProfileController profileController = loader.getController();
        profileController.launch(actionEvent, root, userAccount);
    }

    public void onClickLogout(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/login-view.fxml"));
        root = loader.load();

        LoginController loginController = loader.getController();
        loginController.launch(actionEvent, root);
    }

}
