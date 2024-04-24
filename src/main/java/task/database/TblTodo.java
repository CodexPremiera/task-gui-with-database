package task.database;

import task.registration_app.Todo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TblTodo {
    public static void main(String[] args) {
        // run main to create table if not exist
        createTable();
    }


    public static void createTable() {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS `tbl_todo` (
                `ID_Todo` CHAR(36) NOT NULL,
                `ID_User` CHAR(36) NOT NULL,
                `Todo` VARCHAR(255) NOT NULL,
                `CreateTime` DATETIME,
                FOREIGN KEY (`ID_User`) REFERENCES `tbl_user_account`(`ID_User`),
                PRIMARY KEY (`ID_Todo`)
            )""";

        String createTriggerQuery = """
            CREATE TRIGGER pre_insert_todo
                BEFORE INSERT ON `tbl_todo`
                FOR EACH ROW
            BEGIN
                SET NEW.CreateTime = CURRENT_TIMESTAMP();
                SET NEW.ID_Todo = UUID();
            END""";

        Database.createTable(createTableQuery, createTriggerQuery);
    }

    public static Todo insert(String userAccountID, String todo) throws IllegalArgumentException {
        if (!TblUserAccount.userExists(userAccountID))
            throw new IllegalArgumentException("UserAccount does not exist");

        String id = null;

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO tbl_todo (ID_User, Todo) " +
                             "VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, userAccountID);
            preparedStatement.setString(2, todo);
            preparedStatement.executeUpdate();

            // get last inserted ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (!generatedKeys.next())
                    return null;

                id = generatedKeys.getString(1);
            }
        } catch (SQLException e) {
            System.out.println("Failed to add the new todo." + "\n"
                    + e.getMessage());
            return null;
        }

        try {
            return retrieve(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Todo retrieve(String id) throws IllegalArgumentException {
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM tbl_todo WHERE ID_Todo = ?"))
        {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet == null || !resultSet.next())
                throw new IllegalArgumentException("Todo does not exist.");

            String userId = resultSet.getString("ID_User");
            String todo = resultSet.getString("Todo");
            String createTime = resultSet.getString("CreateTime");

            return new Todo(id, userId, todo, createTime);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (SQLException e) {
            return null;
        }
    }

    public static List<Todo> retrieveAllOf(String userId) throws IllegalArgumentException {
        if (!TblUserAccount.userExists(userId))
            throw new IllegalArgumentException("UserAccount does not exist");

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     """
                             SELECT ROW_NUMBER() OVER () AS No,
                                    ToDo,
                                    CreateTime\s
                             FROM tbl_todo
                             WHERE ID_User = ?
                             ORDER BY CreateTime DESC;"""))
        {
            preparedStatement.setString(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Todo> todoList = new ArrayList<>();

                while (resultSet.next()) {
                    String id = resultSet.getString("No");
                    String todo = resultSet.getString("ToDo");
                    String createTime = resultSet.getString("CreateTime");

                    Todo todoItem = new Todo(id, null, todo, createTime);
                    todoList.add(todoItem);
                }

                return todoList;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static void update(Todo todo, String newTodo)
            throws IllegalArgumentException, SQLException {
        String uuid = todo.getId();
        if (!todoExists(uuid))
            throw new IllegalArgumentException("The given todo does not exists.");

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE tbl_todo SET Todo = ? " +
                             "WHERE ID_Todo = ?"
             ) )
        {
            preparedStatement.setString(1, newTodo);
            preparedStatement.setString(2, uuid);

            preparedStatement.executeUpdate();

            todo.setTodo(newTodo);
        } catch (SQLException e) {
            throw new SQLException("Database failure. Todo has not been updated.");
        }
    }

    public static void delete(Todo todo) throws IllegalArgumentException, SQLException {
        String uuid = todo.getId();
        if (!todoExists(uuid))
            throw new IllegalArgumentException("Can't delete a not-existent todo.");

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM tbl_todo " +
                             "WHERE ID_Todo = ?")
        ) {
            preparedStatement.setString(1, uuid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database failure. Todo has not been deleted.");
        }
    }

    public static boolean todoExists(String uuid) {
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM tbl_todo WHERE ID_Todo = ?")
        ) {
            preparedStatement.setString(1, uuid);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException ignored) {}

        return false;
    }
}
