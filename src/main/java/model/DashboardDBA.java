package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Vector;

public class DashboardDBA {

    public static class DashboardMetrics {
        public String occupancy = "0 / 0 Rooms";
        public String checkInsToday = "0";
        public String checkOutsToday = "0";
        public String totalRevenue = "0 MMK";
        public String expectedArrivals = "0 Guests";
    }

    public static DashboardMetrics getLiveMetrics() {
        DashboardMetrics metrics = new DashboardMetrics();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return metrics;

        String sqlOccupancy = "SELECT " +
                "SUM(CASE WHEN status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_count, " +
                "COUNT(*) AS total_rooms " +
                "FROM Rooms";

        String sqlBookings = "SELECT " +
                "SUM(CASE WHEN check_in_date = CAST(GETDATE() AS DATE) AND booking_status = 'CHECKED-IN' THEN 1 ELSE 0 END) AS checkins_today, " +
                "SUM(CASE WHEN check_out_date = CAST(GETDATE() AS DATE) AND booking_status = 'COMPLETED' THEN 1 ELSE 0 END) AS checkouts_today, " +
                "SUM(CASE WHEN check_in_date = CAST(GETDATE() AS DATE) AND booking_status = 'CONFIRMED' THEN 1 ELSE 0 END) AS expected_arrivals " +
                "FROM Bookings";

        String sqlRevenue = "SELECT ISNULL(SUM(net_payable), 0) AS total_revenue " +
                "FROM Invoices " +
                "WHERE payment_status IN ('PAID', 'SETTLED')";

        try {
            try (PreparedStatement pst = conn.prepareStatement(sqlOccupancy);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int occupied = rs.getInt("occupied_count");
                    int total = rs.getInt("total_rooms");
                    metrics.occupancy = occupied + " / " + total + " Rooms";
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(sqlBookings);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    metrics.checkInsToday = String.valueOf(rs.getInt("checkins_today"));
                    metrics.checkOutsToday = String.valueOf(rs.getInt("checkouts_today"));
                    metrics.expectedArrivals = rs.getInt("expected_arrivals") + " Guests";
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(sqlRevenue);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    double revenue = rs.getDouble("total_revenue");
                    DecimalFormat df = new DecimalFormat("#,##0");
                    metrics.totalRevenue = df.format(revenue) + " MMK";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return metrics;
    }

    public static Vector<Vector<Object>> getRecentReservations() {
        Vector<Vector<Object>> rows = new Vector<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return rows;

        String query = "SELECT TOP 15 " +
                "b.booking_ref, " +
                "g.guest_id, " +
                "g.full_name, " +
                "b.room_no, " +
                "pt.tier_name, " +
                "rc.category_name, " +
                "CONVERT(VARCHAR(10), b.check_in_date, 103) AS check_in, " +
                "CONVERT(VARCHAR(10), b.check_out_date, 103) AS check_out, " +
                "b.booking_status " +
                "FROM Bookings b " +
                "INNER JOIN Guests g ON b.guest_id = g.guest_id " +
                "INNER JOIN Rooms r ON b.room_no = r.room_no " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "INNER JOIN PricingTiers pt ON b.tier_id = pt.tier_id " +
                "ORDER BY b.created_at DESC";

        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("booking_ref"));
                row.add(rs.getString("guest_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("room_no"));
                row.add(rs.getString("tier_name"));
                row.add(rs.getString("category_name"));
                row.add(rs.getString("check_in"));
                row.add(rs.getString("check_out"));
                row.add(rs.getString("booking_status"));
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }
}