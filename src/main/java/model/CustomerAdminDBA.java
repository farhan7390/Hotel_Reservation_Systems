package model;

import java.sql.*;
import java.util.Vector;

public class CustomerAdminDBA {

    public static class CustomerKPIs {
        public String totalGuests = "0 Guests";
        public String vipMembers = "0 Members";
        public String inHouseGuests = "0 Staying";
        public String repeatRate = "0.0%";
    }

    public static CustomerKPIs getCustomerMetrics() {
        CustomerKPIs kpis = new CustomerKPIs();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return kpis;

        String sqlGuests = "SELECT " +
                "COUNT(*) AS total_cnt, " +
                "SUM(CASE WHEN vip_tier IN ('GOLD VIP', 'PLATINUM VIP', 'SILVER VIP') THEN 1 ELSE 0 END) AS vip_cnt " +
                "FROM Guests";

        String sqlInHouse = "SELECT COUNT(DISTINCT guest_id) AS in_house_cnt FROM Bookings WHERE booking_status = 'CHECKED-IN'";

        String sqlRepeat = "SELECT " +
                "CAST(SUM(CASE WHEN stay_count > 1 THEN 1.0 ELSE 0.0 END) * 100.0 / NULLIF(COUNT(*), 0) AS DECIMAL(5,1)) AS repeat_pct " +
                "FROM (SELECT guest_id, COUNT(*) AS stay_count FROM Bookings GROUP BY guest_id) sub";

        try {
            try (PreparedStatement pst = conn.prepareStatement(sqlGuests);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    kpis.totalGuests = String.format("%,d Guests", rs.getInt("total_cnt"));
                    kpis.vipMembers = String.format("%,d Members", rs.getInt("vip_cnt"));
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(sqlInHouse);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    kpis.inHouseGuests = String.format("%,d Staying", rs.getInt("in_house_cnt"));
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(sqlRepeat);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next() && rs.getBigDecimal("repeat_pct") != null) {
                    kpis.repeatRate = rs.getBigDecimal("repeat_pct").toString() + "%";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kpis;
    }

    public static Vector<Vector<Object>> getAllGuests() {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT g.guest_id, g.full_name, g.phone, g.nid_passport, g.vip_tier, " +
                     "ISNULL(b.stay_count, 0) AS total_stays, g.guest_status, g.email, g.city, g.preferences_notes " +
                     "FROM Guests g " +
                     "LEFT JOIN (SELECT guest_id, COUNT(*) AS stay_count FROM Bookings GROUP BY guest_id) b ON g.guest_id = b.guest_id " +
                     "ORDER BY g.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("guest_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("nid_passport"));
                row.add(rs.getString("vip_tier"));
                int stays = rs.getInt("total_stays");
                row.add(stays + (stays == 1 ? " Stay" : " Stays"));
                row.add(rs.getString("guest_status"));
                row.add(rs.getString("email") != null ? rs.getString("email") : "");
                row.add(rs.getString("city") != null ? rs.getString("city") : "Yangon");
                row.add(rs.getString("preferences_notes") != null ? rs.getString("preferences_notes") : "");
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean saveOrUpdateGuest(String guestId, String name, String nid, String phone,
                                           String email, String city, String vipTier,
                                           String status, String preferences, boolean isUpdate) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        if (isUpdate && guestId != null && !guestId.trim().isEmpty()) {
            String updateSql = "UPDATE Guests SET full_name = ?, nid_passport = ?, phone = ?, " +
                               "email = ?, city = ?, vip_tier = ?, guest_status = ?, preferences_notes = ? " +
                               "WHERE guest_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(updateSql)) {
                pst.setString(1, name);
                pst.setString(2, nid.isEmpty() ? "N/A" : nid);
                pst.setString(3, phone);
                pst.setString(4, email);
                pst.setString(5, city);
                pst.setString(6, vipTier);
                pst.setString(7, status);
                pst.setString(8, preferences);
                pst.setString(9, guestId);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String newId = "GST-" + (System.currentTimeMillis() % 100000);
            String insertSql = "INSERT INTO Guests (guest_id, full_name, nid_passport, phone, email, city, vip_tier, guest_status, preferences_notes, loyalty_points) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
            try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
                pst.setString(1, newId);
                pst.setString(2, name);
                pst.setString(3, nid.isEmpty() ? "N/A" : nid);
                pst.setString(4, phone);
                pst.setString(5, email);
                pst.setString(6, city);
                pst.setString(7, vipTier);
                pst.setString(8, status);
                pst.setString(9, preferences);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}