package task.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    public static final String URL = "jdbc:mysql://localhost:3306/db_comandao_jdbc";
    public static final String USER = "root";
    public static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Connection to MySQL has been failed." + "\n" + e.getMessage());
        }

        return connection;
    }

    public static void createTable(String createTableQuery, String createTriggerQuery) {
        Connection connection = null;

        try {
            connection = Database.getConnection();
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

    public static void main(String[] args) {
        try {
            Connection connection = getConnection();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Connection to MySQL has been failed.");
        }
    }
}
