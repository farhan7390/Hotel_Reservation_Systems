package model;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Vector;

public class ServiceDBA {

    public static class ServiceItemData {
        public int serviceId;
        public String serviceName;
        public String category;
        public BigDecimal unitPrice;

        @Override
        public String toString() {
            return serviceName;
        }
    }

    public static class ServiceKPIs {
        public String activeOrders = "0 Orders";
        public String diningDelivered = "0 Delivered";
        public String expressLaundry = "0 Orders";
        public String serviceRevenue = "0 MMK";
    }

    public static ServiceKPIs getServiceMetrics() {
        ServiceKPIs kpis = new ServiceKPIs();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return kpis;

        String sql = "SELECT " +
                "SUM(CASE WHEN rso.order_status IN ('PREPARING', 'IN SERVICE') THEN 1 ELSE 0 END) AS active_cnt, " +
                "SUM(CASE WHEN sc.category = 'Food & In-Room Dining' AND rso.order_status = 'DELIVERED' THEN 1 ELSE 0 END) AS dining_cnt, " +
                "SUM(CASE WHEN sc.category = 'Laundry' AND rso.order_status IN ('PREPARING', 'IN SERVICE') THEN 1 ELSE 0 END) AS laundry_cnt, " +
                "ISNULL(SUM(CASE WHEN rso.order_status != 'CANCELLED' THEN rso.total_amount ELSE 0 END), 0) AS total_rev " +
                "FROM RoomServiceOrders rso " +
                "INNER JOIN ServiceCatalog sc ON rso.service_id = sc.service_id";

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                kpis.activeOrders = rs.getInt("active_cnt") + " Orders";
                kpis.diningDelivered = rs.getInt("dining_cnt") + " Delivered";
                kpis.expressLaundry = rs.getInt("laundry_cnt") + " Orders";

                DecimalFormat df = new DecimalFormat("#,##0");
                kpis.serviceRevenue = df.format(rs.getBigDecimal("total_rev")) + " MMK";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kpis;
    }

    public static Vector<String> getActiveStayRooms() {
        Vector<String> rooms = new Vector<>();
        String sql = "SELECT b.room_no, rc.category_name, g.full_name " +
                "FROM Bookings b " +
                "INNER JOIN Rooms r ON b.room_no = r.room_no " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "INNER JOIN Guests g ON b.guest_id = g.guest_id " +
                "WHERE b.booking_status = 'CHECKED-IN' " +
                "ORDER BY b.room_no";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                rooms.add(rs.getString("room_no") + " (" + rs.getString("category_name") + " - " + rs.getString("full_name") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static Vector<String> getCategories() {
        Vector<String> categories = new Vector<>();
        String sql = "SELECT DISTINCT category FROM ServiceCatalog WHERE is_available = 1 ORDER BY category";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public static Vector<ServiceItemData> getItemsByCategory(String category) {
        Vector<ServiceItemData> items = new Vector<>();
        String sql = "SELECT service_id, service_name, category, unit_price FROM ServiceCatalog " +
                "WHERE category = ? AND is_available = 1 ORDER BY service_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, category);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ServiceItemData item = new ServiceItemData();
                    item.serviceId = rs.getInt("service_id");
                    item.serviceName = rs.getString("service_name");
                    item.category = rs.getString("category");
                    item.unitPrice = rs.getBigDecimal("unit_price");
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static Vector<Vector<Object>> getAllOrders() {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT rso.order_id, rso.room_no, sc.service_name, rso.quantity, " +
                "rso.total_amount, CONVERT(VARCHAR(16), rso.ordered_at, 120) AS ordered_time, " +
                "rso.order_status, rso.delivery_instructions, sc.category " +
                "FROM RoomServiceOrders rso " +
                "INNER JOIN ServiceCatalog sc ON rso.service_id = sc.service_id " +
                "ORDER BY rso.ordered_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("order_id"));
                row.add(rs.getString("room_no"));
                row.add(rs.getString("service_name"));
                row.add(String.valueOf(rs.getInt("quantity")));
                row.add(String.format("%,d MMK", rs.getBigDecimal("total_amount").longValue()));
                row.add(rs.getString("ordered_time"));
                row.add(rs.getString("order_status"));
                row.add(rs.getString("delivery_instructions"));
                row.add(rs.getString("category"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean dispatchServiceOrder(String roomNo, int serviceId, int quantity, BigDecimal totalAmount, String instructions) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String findBookingSql = "SELECT TOP 1 booking_ref FROM Bookings WHERE room_no = ? AND booking_status = 'CHECKED-IN'";
        String insertSql = "INSERT INTO RoomServiceOrders (order_id, booking_ref, room_no, service_id, quantity, total_amount, delivery_instructions, order_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'PREPARING')";

        try {
            String bookingRef = null;
            try (PreparedStatement pstBkg = conn.prepareStatement(findBookingSql)) {
                pstBkg.setString(1, roomNo);
                try (ResultSet rs = pstBkg.executeQuery()) {
                    if (rs.next()) {
                        bookingRef = rs.getString("booking_ref");
                    }
                }
            }

            if (bookingRef == null) return false;

            String orderId = "SR-" + (System.currentTimeMillis() % 100000);
            try (PreparedStatement pstInsert = conn.prepareStatement(insertSql)) {
                pstInsert.setString(1, orderId);
                pstInsert.setString(2, bookingRef);
                pstInsert.setString(3, roomNo);
                pstInsert.setInt(4, serviceId);
                pstInsert.setInt(5, quantity);
                pstInsert.setBigDecimal(6, totalAmount);
                pstInsert.setString(7, instructions);
                return pstInsert.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateServiceOrderStatus(String orderId, String newStatus) {
        String sql = "UPDATE RoomServiceOrders SET order_status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, newStatus);
            pst.setString(2, orderId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}