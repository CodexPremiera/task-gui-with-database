package task.database;

import java.sql.*;

public class Database {
    public static void main(String[] args) {
        createTable();
        insert("Ashley", "ashley@email.com", "password123");
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

            // execute the create table query
            statement.execute(createTableQuery);
            System.out.println("Table has been created.");

            // execute the create trigger query
            statement.execute(createTriggerQuery);
            System.out.println("Trigger has been created.");

            // commit the transaction
            connection.commit();
            System.out.println("Transaction committed.");
        } catch (SQLException | NullPointerException e) {
            System.out.println("Table or trigger has not been created. Rolling back changes." + "\n"
                                + e.getMessage());
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


    public static void insert(String username, String email, String password) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO tbl_user_account (Username, Email, Password) " +
                             "VALUES (?, ?, ?)");
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
    }

    public void read(String findEmail) {
        try (Connection connection = MySQLConnection.getConnection();
             Statement statement = connection.createStatement(); )
        {
            String query =
                    "SELECT ID, NAME, EMAIL FROM USER" +
                            " WHERE email = '" + findEmail + "'";
            statement.execute(query);

            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                System.out.printf("""
                        ID: %d
                        Name: %s
                        Email: %s
                        %n""", id, name, email);
            }

        } catch (SQLException e) {
            System.out.println("Data has not been inserted.");
        }
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
