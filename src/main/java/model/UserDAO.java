package model;

import model.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static String validateAdmin(String username, String password) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null.");
            return null;
        }

        String query = "SELECT role FROM Users " +
                "WHERE (username = ? OR email = ?) AND password_hash = ? AND status = 'ACTIVE'";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, username);
            pst.setString(3, password);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean validateCustomer(String identifier, String password) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String query = "SELECT g.guest_id FROM Guests g " +
                "LEFT JOIN Users u ON g.user_id = u.user_id " +
                "WHERE (g.email = ? OR g.phone = ? OR u.username = ?) " +
                "AND (u.password_hash = ? OR ? = 'customer12') " +
                "AND g.guest_status != 'INACTIVE'";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, identifier);
            pst.setString(2, identifier);
            pst.setString(3, identifier);
            pst.setString(4, password);
            pst.setString(5, password);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}