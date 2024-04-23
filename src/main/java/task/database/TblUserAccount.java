package task.database;

import task.entities.UserAccount;

import java.sql.*;

public class TblUserAccount {
    public static void main(String[] args) throws SQLException {
        //createTable();
        /*try {
            insert("Ashley", "ashley@email.com", "password123");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

        UserAccount userAccount = getUserAccount("ashleey@email.com");
        System.out.println(userAccount);
    }

    public static void createTable() {
        // SQL queries to create the table and the trigger
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
            // setup connection and statements
            connection = MySQLConnection.getConnection();
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            // execute the create table and trigger queries
            statement.execute(createTableQuery);
            System.out.println("Table has been created.");

            statement.execute(createTriggerQuery);
            System.out.println("Trigger has been created.");

            // commit the transaction
            connection.commit();
            System.out.println("Transaction committed.");
        } catch (SQLException | NullPointerException e) {
            System.out.println("Table or trigger has not been created. Rolling back changes." + "\n" + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                    System.out.println("Rollback successful.");
                }
            } catch (SQLException ex) {
                System.out.println("Rollback failed.");
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException ex) {
                System.out.println("Failed to close connection.");
                ex.printStackTrace();
            }
        }
    }

    /**
     * INSERT
     *
     * <p>
     * This method inserts a new user-account as a new row in the tbl_user_account.
     * @return UUID of the newly created table
     * */
    public static UserAccount insert(String username, String email, String password) throws SQLException {
        // query the db if the email (which is unique) already exist. If so, return.
        if (emailExists(email))
            throw new SQLException("The given email already exists.");

        // do if the email does not exist yet
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO tbl_user_account (Username, Email, Password) " +
                             "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println(rowsInserted + " row(s) inserted.");
        } catch (SQLException e) {
            System.out.println("Data has not been inserted." + "\n"
                    + e.getMessage());
        }

        // return user account
        return getUserAccount(email);
    }

    private static boolean emailExists(String email) throws SQLException {
        boolean exists = false;
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM tbl_user_account WHERE Email = ?");
        ) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    exists = count > 0;
                }
            }
        }
        return exists;
    }


    private static UserAccount getUserAccount(String email) {

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM tbl_user_account WHERE Email = ?"))
        {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet != null && resultSet.next()) {
                String userId = resultSet.getString("ID_User");
                String username = resultSet.getString("Username");
                email = resultSet.getString("Email");
                String password = resultSet.getString("Password");
                String createTime = resultSet.getString("CreateTime");

                return new UserAccount(userId, username, email, password, createTime);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user account: " + e.getMessage());
        }

        return null;
    }


    public void update(String newName, String newEmail, String newPassword) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE User SET name = ?, email = ?, password = ? " +
                             "WHERE id = ?"
             ); )
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
