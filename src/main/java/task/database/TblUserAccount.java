package task.database;

import task.entities.UserAccount;

import java.sql.*;

public class TblUserAccount {
    public static void main(String[] args) {
        createTable();
        UserAccount userAccount = null;
        try {
            userAccount = retrieve("ashleey@email.com", "ashley_ken_123");
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println(
                    "Failed to retrieve user-account:" + "\n"
                            + e.getMessage() + "\n"
            );
        }
        System.out.println(userAccount);
    }


    /**
     * CREATE
     * <p>
     * This method creates the user account table and associated trigger in the database.
     * It executes SQL queries to create the 'tbl_user_account' table and a trigger named
     * 'pre_insert' in the database.
     * <p>
     * The 'tbl_user_account' table stores user account information including user ID,
     * username, email, password, creation time, and activation status. Meanwhile, the
     * trigger 'pre_insert' automatically sets the creation time and user ID before
     * inserting a new row into the 'tbl_user_account' table.
     * <p>
     * Upon successful execution, this method prints status messages indicating that the
     * table and trigger have been created, and commits the transaction. If an error occurs
     * during execution, it prints an error message, rolls back any changes, and closes the
     * database connection.
     * <p>
     * Note: This method does not return any value.
     */
    public static void createTable() {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS `tbl_user_account` (
            `ID_User` CHAR(36) NOT NULL,
            `Username` VARCHAR(255) NOT NULL,
            `Email` VARCHAR(255) UNIQUE NOT NULL,
            `Password` VARCHAR(255) NOT NULL,
            `CreateTime` DATETIME,
            `IsActive` BOOLEAN DEFAULT true,
            PRIMARY KEY (`ID_User`))""";

        String createTriggerQuery = """
            CREATE TRIGGER pre_insert
            BEFORE INSERT ON tbl_user_account
            FOR EACH ROW
            BEGIN
                SET NEW.CreateTime = CURRENT_TIMESTAMP();
                SET NEW.ID_User = UUID();
            END""";

        Connection connection = null;

        try {
            connection = MySQLConnection.getConnection();
            connection.setAutoCommit(false);

            Statement statement = connection.createStatement();
            statement.execute(createTableQuery);
            statement.execute(createTriggerQuery);

            connection.commit();
            System.out.println("Transaction committed.");
        } catch (SQLException | NullPointerException e) {
            System.out.println(
                    "Failed to create table. Rolling back changes." + "\n"
                    + e.getMessage() + "\n"
            );
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback failed." + "\n" + ex.getMessage());
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException ex) {
                System.out.println("Failed to close connection." + "\n" + ex.getMessage());
            }
        }
    }


    /**
     * INSERT
     *
     * <p>
     * This method inserts a new user-account as a new row in the tbl_user_account.
     * It first calls the {@link #emailExists} method to check if a user-account
     * associated with the given email already exists. If so, it throws an SQLException.
     * Otherwise, it inserts the new user-account the tbl_user_account and returns it
     * as a new {@link UserAccount} object through the {@link #retrieve} method.
     * @param username
     * @param email
     * @param password
     * @return UserAccount object of newly created class
     * @throws IllegalArgumentException the given email already exists
     * */
    public static UserAccount insert(String username, String email, String password) throws IllegalArgumentException {
        if (emailExists(email))
            throw new IllegalArgumentException("The given email already exists.");

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO tbl_user_account (Username, Email, Password) " +
                             "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Data has not been inserted." + "\n"
                    + e.getMessage());
        }

        try {
            return retrieve(email, password);
        } catch (SQLException e) {
            return null;
        }
    }

    private static boolean emailExists(String email) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM tbl_user_account WHERE Email = ?")
        ) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException ignored) {}

        return false;
    }


    /**
     * RETRIEVE
     * <p>
     * This method Retrieves a user account from the database. It queries the 'tbl_user_account'
     * table in the database to retrieve the user account associated with the specified email
     * and password.
     * <p>
     * If a matching user account is found, the method returns it as a new {@link UserAccount}
     * object. If none is found or if the password is wrong, it throws an exception.
     * <p>
     * Note: This method handles database connectivity internally and does not require
     * the caller to provide a database connection.
     *
     * @param email the email of the user account to retrieve
     * @param password the password of the user account to retrieve
     * @return a UserAccount object representing the retrieved user account,
     *         or null if no matching user account is found
     * @throws IllegalArgumentException indicating a non-existing account of a wrong password.
     * @throws SQLException indicating a database failure to retrieve the user account
     * */
    public static UserAccount retrieve(String email, String password) throws IllegalArgumentException, SQLException {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM tbl_user_account WHERE Email = ?"))
        {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet == null || !resultSet.next())
                throw new IllegalArgumentException("User account does not exist.");

            if (!resultSet.getString("Password").equals(password))
                throw new IllegalArgumentException("Wrong Password");

            String userId = resultSet.getString("ID_User");
            String username = resultSet.getString("Username");
            String createTime = resultSet.getString("CreateTime");

            return new UserAccount(userId, username, email, password, createTime);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }


    public void update(String newName, String newEmail, String newPassword) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE User SET name = ?, email = ?, password = ? " +
                             "WHERE id = ?"
             ) )
        {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setString(3, newPassword);
            preparedStatement.setInt(4, 1);

            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated + " row(s) updated.");
        } catch (SQLException e) {
            System.out.println("Data has not been updated.");
        }
    }

    public void delete(String email) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM User " +
                             "WHERE email = ?")
        ) {
            int id = 2;
            preparedStatement.setString(2, email);

            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println(rowsDeleted + " row(s) deleted.");
        } catch (SQLException e) {
            System.out.println("Data has not been deleted.");
        }
    }
}
