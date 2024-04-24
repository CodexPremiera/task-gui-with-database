package task.registration_app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import task.database.TblTodo;
import task.entities.UserAccount;

import java.io.IOException;
import java.util.List;

public class LandingController {
    /* ELEMENTS */
    @FXML public Label landingGreeting;

    @FXML public Button btnLandingEdit;
    @FXML public Button btnLandingSubmit;
    @FXML public Button btnLandingDelete;
    @FXML public Button btnLandingUpdate;

    @FXML public TextArea landingInput;
    @FXML public Label landingRemark;

    @FXML public TableView<Todo> todoTable;
    @FXML private TableColumn<Todo, String> todoColumn;

    @FXML private TableColumn<Todo, String> dateColumn;


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
        showEntries();
    }

    private void showEntries() {
        List<Todo> todoList = TblTodo.retrieveAllOf(userAccount.getId());

        System.out.println("Todo List:");
        for (Todo todo : todoList) {
            System.out.println(todo.getId() + " | " + todo.getTodo() + " | " + todo.getCreateTime());
        }

        ObservableList<Todo> data = FXCollections.observableArrayList(todoList);
        todoTable.setItems(data);
        todoTable.refresh();
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        landingGreeting.setText("Hello, " + userAccount.getUsername());
    }

    public void addTodo() {
        String todo = landingInput.getText();
        if (todo.trim().isEmpty()) {
            landingRemark.setText("Can't add an empty task.");
            return;
        }

        try {
            TblTodo.insert(userAccount.getId(), todo);
            landingRemark.setText("A new todo is added.");
        } catch (IllegalArgumentException e) {
            landingRemark.setText("The user account does not exist.");
        }

        showEntries();
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
