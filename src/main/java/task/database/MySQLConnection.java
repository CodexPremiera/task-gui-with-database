package task.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection {
    public static final String URL = "jdbc:mysql://localhost:3306/db_comandao_jdbc";
    public static final String USER = "root";
    public static final String PASSWORD = "";


    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection to MySQL has been established.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Connection to MySQL has been failed.");
        }

        return conn;
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
