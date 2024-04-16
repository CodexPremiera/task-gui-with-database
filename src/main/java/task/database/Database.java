package task.database;

import java.sql.*;

public class Database {
    public void createTable() {
        String tableName = "User";

        String query = "CREATE TABLE " + tableName + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(100) NOT NULL,"
                + "email VARCHAR(100) NOT NULL,"
                + "password VARCHAR(100) NOT NULL"
                + ")";

        try (Connection connection = MySQLConnection.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(query);
            System.out.println("Table has been created.");
        } catch (SQLException | NullPointerException e) {
            System.out.println("Table products has not been created.");
        }
    }

    public void insert(String name, String email, String password) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO User (name, email, password) VALUES (?, ?, ?)");)
        {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            int rowsInserted = preparedStatement.executeUpdate();

            System.out.println(rowsInserted + " row(s) inserted.");
        } catch (SQLException e) {
            System.out.println("Data has not been inserted.");
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
