package task.registration_app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import task.database.TblUserAccount;
import task.entities.UserAccount;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class ProfileController {
    /* FXML ELEMENTS */
    @FXML public TextField profileUsername;
    @FXML public TextField profileEmail;
    @FXML public TextField profilePassword;
    @FXML public Label profileRemark;
    @FXML public Button profileLogout;
    @FXML public Button profileDelete;

    /* FIELDS */
    private Parent root;
    private Stage stage;
    private Scene scene;
    private UserAccount userAccount;


    /* METHODS */
    public void launch(ActionEvent actionEvent, Parent launchRoot, UserAccount userAccount) throws IOException {
        this.setUserAccount(userAccount);
        this.showProfile();
        this.root = launchRoot;

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
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
        String newUsername = profileUsername.getText();
        String newEmail = profileEmail.getText();
        String newPassword = profilePassword.getText();

        // validations
        if (newUsername.equals(userAccount.getUsername()) &&
                newPassword.equals(userAccount.getPassword()) &&
                newEmail.equals(userAccount.getEmail())) {
            profileRemark.setText("The profile stays the same.");
            return;
        }
        if (newUsername.isEmpty() || newPassword.trim().isEmpty() || newEmail.isEmpty()) {
            profileRemark.setText("Please fill in all the fields to update.");
            return;
        }
        if (!Utils.isValidEmail(newEmail)) {
            profileRemark.setText("Please enter a valid email.");
            return;
        }
        if (newPassword.length() < 8) {
            profileRemark.setText("Password must have 8 characters or more.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Account");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to update account?");

        // backend tries to update
        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            try {
                TblUserAccount.update(userAccount, newUsername, newEmail, newPassword);
                profileRemark.setText("Profile has been updated");
                return;
            } catch (IllegalArgumentException e) {
                profileRemark.setText("The user account does not exist in the database.");
            } catch (SQLException e) {
                profileRemark.setText("Database failed to update the user account");
            }
        }

        // show the profile
        profileRemark.setText("The profile stays the same.");
        showProfile();
    }

    public void onClickDelete(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete account?");

        // backend tries to delete
        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            try {
                TblUserAccount.delete(userAccount);
                profileRemark.setText("Profile has been deleted");
            } catch (IllegalArgumentException e) {
                profileRemark.setText("The user account does not exist in the database.");
            } catch (SQLException e) {
                profileRemark.setText("Database failed to delete the user account");
            }
            onClickLogout(actionEvent);
        }
    }

    public void onClickLogout(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/login-view.fxml"));
        root = loader.load();

        LoginController loginController = loader.getController();
        loginController.launch(actionEvent, root);
    }

}