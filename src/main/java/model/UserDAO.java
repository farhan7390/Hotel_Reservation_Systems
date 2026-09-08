package model;

import model.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static String validateAdmin(String usernameOrEmail, String password) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        String sql = "SELECT role FROM Users WHERE (username = ? OR email = ?) AND password_hash = ? AND status = 'ACTIVE' AND role = 'ADMIN'";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, usernameOrEmail.trim());
            pst.setString(2, usernameOrEmail.trim());
            pst.setString(3, password.trim());

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
        if (conn == null || identifier == null || password == null) return false;

        String query = "SELECT g.guest_id FROM Guests g " +
                "WHERE (g.email = ? OR g.phone = ?) " +
                "AND g.password_hash = ? " +
                "AND g.guest_status != 'INACTIVE'";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, identifier.trim());
            pst.setString(2, identifier.trim());
            pst.setString(3, password.trim());

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}