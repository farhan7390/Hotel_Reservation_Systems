package model;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Vector;

public class RoomDBA {

    public static Vector<String> getCategoryNames() {
        Vector<String> categories = new Vector<>();
        String sql = "SELECT category_name FROM RoomCategories ORDER BY category_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public static BigDecimal getCategoryBasePrice(String categoryName) {
        String sql = "SELECT base_night_rate FROM RoomCategories WHERE category_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, categoryName);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("base_night_rate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public static String getCategoryCapacity(String categoryName) {
        String sql = "SELECT max_capacity_desc FROM RoomCategories WHERE category_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, categoryName);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getString("max_capacity_desc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "2 Guests";
    }

    public static void syncActiveRoomOccupancy() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        String sqlOccupy = "UPDATE Rooms SET status = 'OCCUPIED' " +
                "WHERE room_no IN (" +
                "  SELECT room_no FROM Bookings " +
                "  WHERE booking_status IN ('CHECKED-IN', 'CONFIRMED') " +
                "  AND CAST(GETDATE() AS DATE) BETWEEN check_in_date AND check_out_date" +
                ") AND status != 'MAINTENANCE'";

        String sqlRelease = "UPDATE Rooms SET status = 'AVAILABLE' " +
                "WHERE status = 'OCCUPIED' AND room_no NOT IN (" +
                "  SELECT room_no FROM Bookings " +
                "  WHERE booking_status IN ('CHECKED-IN', 'CONFIRMED') " +
                "  AND CAST(GETDATE() AS DATE) BETWEEN check_in_date AND check_out_date" +
                ")";

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sqlOccupy);
            st.executeUpdate(sqlRelease);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int[] getRoomInventoryStats() {
        syncActiveRoomOccupancy();
        int[] stats = new int[4];
        String sql = "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_count, " +
                "SUM(CASE WHEN status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_count, " +
                "SUM(CASE WHEN status IN ('MAINTENANCE', 'RESERVED') THEN 1 ELSE 0 END) AS blocked_count " +
                "FROM Rooms";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("available_count");
                stats[2] = rs.getInt("occupied_count");
                stats[3] = rs.getInt("blocked_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public static Vector<Vector<Object>> getAllRooms() {
        syncActiveRoomOccupancy();
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT r.room_no, rc.category_name, r.floor_level, rc.base_night_rate, " +
                "rc.max_capacity_desc, r.has_balcony, r.has_sea_view, r.has_jacuzzi, r.status " +
                "FROM Rooms r " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "ORDER BY r.room_no";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                String roomNo = rs.getString("room_no");
                row.add(roomNo);
                row.add(rs.getString("category_name"));
                row.add(rs.getString("floor_level"));
                row.add(String.format("%,d MMK", rs.getBigDecimal("base_night_rate").longValue()));
                row.add(rs.getString("max_capacity_desc"));

                StringBuilder amenities = new StringBuilder();
                boolean balcony = rs.getBoolean("has_balcony");
                boolean seaView = rs.getBoolean("has_sea_view");
                boolean jacuzzi = rs.getBoolean("has_jacuzzi");

                if (balcony) amenities.append("Balcony, ");
                if (seaView) amenities.append("Sea View, ");
                if (jacuzzi) amenities.append("Jacuzzi, ");

                String amenityStr = amenities.length() > 2 ? amenities.substring(0, amenities.length() - 2) : "Standard amenities";
                row.add(amenityStr);

                String cleanNum = roomNo.toLowerCase().replace("-", "");
                row.add("/images/rooms/" + cleanNum + "_1.jpg");

                row.add(rs.getString("status"));
                row.add(balcony);
                row.add(seaView);
                row.add(jacuzzi);

                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean saveOrUpdateRoom(String roomNo, String categoryName, String floor,
                                           boolean balcony, boolean seaView, boolean jacuzzi,
                                           String status, String imagePath, boolean isUpdate) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        int categoryId = 1;
        String catSql = "SELECT category_id FROM RoomCategories WHERE category_name = ?";
        try (PreparedStatement pstCat = conn.prepareStatement(catSql)) {
            pstCat.setString(1, categoryName);
            try (ResultSet rs = pstCat.executeQuery()) {
                if (rs.next()) categoryId = rs.getInt("category_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (isUpdate) {
            String updateSql = "UPDATE Rooms SET category_id = ?, floor_level = ?, has_balcony = ?, " +
                    "has_sea_view = ?, has_jacuzzi = ?, status = ?, image_path = ? WHERE room_no = ?";
            try (PreparedStatement pst = conn.prepareStatement(updateSql)) {
                pst.setInt(1, categoryId);
                pst.setString(2, floor);
                pst.setBoolean(3, balcony);
                pst.setBoolean(4, seaView);
                pst.setBoolean(5, jacuzzi);
                pst.setString(6, status);
                pst.setString(7, imagePath);
                pst.setString(8, roomNo);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String insertSql = "INSERT INTO Rooms (room_no, category_id, floor_level, has_balcony, has_sea_view, has_jacuzzi, status, image_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
                pst.setString(1, roomNo);
                pst.setInt(2, categoryId);
                pst.setString(3, floor);
                pst.setBoolean(4, balcony);
                pst.setBoolean(5, seaView);
                pst.setBoolean(6, jacuzzi);
                pst.setString(7, status);
                pst.setString(8, imagePath);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}