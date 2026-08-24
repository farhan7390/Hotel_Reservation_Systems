package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=HotelReservationDB;encrypt=true;trustServerCertificate=true";
    private static final String USER = "hmsDBMS";
    private static final String PASSWORD = "admin12@";

    private static Connection connection = null;

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MSSQL Driver missing from classpath: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("MSSQL Connection Failed: " + e.getMessage());
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}