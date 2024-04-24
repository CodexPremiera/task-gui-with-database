package task.database;

import task.entities.UserAccount;

import java.sql.*;

public class TblUserAccount {
    public static void main(String[] args) {
        // run main to create table if not exist
        createTable();
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
                PRIMARY KEY (`ID_User`)
            )""";

        String createTriggerQuery = """
            CREATE TRIGGER pre_insert
                BEFORE INSERT ON tbl_user_account
                FOR EACH ROW
            BEGIN
                SET NEW.CreateTime = CURRENT_TIMESTAMP();
                SET NEW.ID_User = UUID();
            END""";

        Database.createTable(createTableQuery, createTriggerQuery);
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
    public static UserAccount insert(String username, String email, String password)
            throws IllegalArgumentException {
        if (emailExists(email))
            throw new IllegalArgumentException("The given email already exists.");

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO tbl_user_account (Username, Email, Password) " +
                             "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add user account." + "\n"
                    + e.getMessage());
            return null;
        }

        try {
            return retrieve(email, password);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean emailExists(String email) {
        try (Connection connection = Database.getConnection();
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
     * RETRIEVES
     * <p>
     * This method retrieves a user account from the database by email and password. If a
     * matching user account is found, the method returns it as a new {@link UserAccount}
     * object. If none is found or if the password is incorrect, it throws an exception.
     * <p>
     * Note: This method handles database connectivity internally and does not require
     * the caller to provide a database connection.
     *
     * @param email the email of the user account to retrieve
     * @param password the password of the user account to retrieve
     * @return a UserAccount object representing the retrieved user account
     * @throws IllegalArgumentException if the user account does not exist or if the
     * password is incorrect
     * @throws SQLException if there is a database failure to retrieve the user account
     * */
    public static UserAccount retrieve(String email, String password) throws IllegalArgumentException {
        try (Connection connection = Database.getConnection();
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
            return null;
        }
    }


    /**
     * UPDATE
     * <p>
     * This method updates the details of a user account in the database with the provided new name,
     * email, and password. It first checks if the provided user account exists in the database based
     * on its UUID using the {@link #userExists} method. If the user account exists, it updates the
     * corresponding record in the 'tbl_user_account' table with the new details.
     * <p>
     * Note: This method requires the caller to provide a valid {@link UserAccount} object and the
     * new details (name, email, and password) to perform the update operation.
     * <p>
     * If the provided user account does not exist, it throws an IllegalArgumentException.
     *
     * @param userAccount the UserAccount object representing the user account to be
     *                    updated
     * @param newName the new name to be set for the user account
     * @param newEmail the new email to be set for the user account
     * @param newPassword the new password to be set for the user account
     * @throws IllegalArgumentException if the provided user account does not exist
     * in the database
     * @throws SQLException if the database fails to update the user account
     * */

    public static void update(UserAccount userAccount, String newName, String newEmail, String newPassword)
            throws IllegalArgumentException, SQLException {
        String uuid = userAccount.getId();
        if (!userExists(uuid))
            throw new IllegalArgumentException("The given user-account does not exists.");

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE tbl_user_account SET Username = ?, Email = ?, Password = ? " +
                             "WHERE ID_User = ?"
             ) )
        {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setString(3, newPassword);
            preparedStatement.setString(4, uuid);

            preparedStatement.executeUpdate();

            userAccount.setUsername(newName);
            userAccount.setEmail(newEmail);
            userAccount.setPassword(newPassword);
        } catch (SQLException e) {
            throw new SQLException("Database failure. Data has not been updated.");
        }
    }


    /**
     * DELETE
     * <p>
     * This method deletes a user account from the database based on the provided UserAccount object.
     * It first checks if the provided user account exists in the database based on its UUID using
     * the {@link #userExists} method. If the user account exists, it deletes the corresponding record
     * from the 'tbl_user_account' table.
     * <p>
     * Note: This method requires the caller to provide a valid {@link UserAccount} object representing
     * the user account to be deleted.
     * <p>
     * If the provided user account does not exist, it throws an IllegalArgumentException.
     *
     * @param userAccount the UserAccount object representing the user account to be deleted
     * @throws IllegalArgumentException if the provided user account does not exist in the database
     * @throws SQLException if the database fails to delete the user account
     */
    public static void delete(UserAccount userAccount) throws IllegalArgumentException, SQLException {
        String uuid = userAccount.getId();
        if (!userExists(uuid))
            throw new IllegalArgumentException("Can't delete a not-existent user-account.");

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM tbl_user_account " +
                             "WHERE ID_User = ?")
        ) {
            preparedStatement.setString(1, uuid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Database failure. Data has not been deleted.");
        }
    }

    public static boolean userExists(String uuid) {
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM tbl_user_account WHERE ID_User = ?")
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
