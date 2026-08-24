package model;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Vector;

public class BillingDBA {

    public static class ActiveFolio {
        public String bookingRef = "";
        public String roomNo = "";
        public String guestId = "";
        public String guestName = "Guest";
        public BigDecimal roomCharges = BigDecimal.ZERO;
        public BigDecimal serviceCharges = BigDecimal.ZERO;
        public BigDecimal taxAmount = BigDecimal.ZERO;
        public BigDecimal netPayable = BigDecimal.ZERO;
    }

    public static class BillingKPIs {
        public String totalRevenue = "0 MMK";
        public String pendingAmount = "0 MMK";
        public String cardOnlineAmount = "0 MMK";
        public String totalInvoices = "0 Invoices";
    }

    public static BillingKPIs getBillingMetrics() {
        BillingKPIs kpis = new BillingKPIs();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return kpis;

        String sql = "SELECT " +
                "ISNULL(SUM(CASE WHEN payment_status IN ('PAID', 'SETTLED') THEN net_payable ELSE 0 END), 0) AS total_revenue, " +
                "ISNULL(SUM(CASE WHEN payment_status = 'UNPAID' THEN net_payable ELSE 0 END), 0) AS pending_amount, " +
                "ISNULL(SUM(CASE WHEN payment_status IN ('PAID', 'SETTLED') AND payment_method IN ('KBZPay', 'WavePay', 'Credit Card', 'Bank Transfer') THEN net_payable ELSE 0 END), 0) AS digital_revenue, " +
                "COUNT(*) AS total_invoices " +
                "FROM Invoices";

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                DecimalFormat df = new DecimalFormat("#,##0");
                kpis.totalRevenue = df.format(rs.getBigDecimal("total_revenue")) + " MMK";
                kpis.pendingAmount = df.format(rs.getBigDecimal("pending_amount")) + " MMK";
                kpis.cardOnlineAmount = df.format(rs.getBigDecimal("digital_revenue")) + " MMK";
                kpis.totalInvoices = rs.getInt("total_invoices") + " Invoices";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kpis;
    }

    public static Vector<String> getActiveStayRooms() {
        Vector<String> rooms = new Vector<>();
        String sql = "SELECT b.room_no, rc.category_name, g.full_name FROM Bookings b " +
                "INNER JOIN Rooms r ON b.room_no = r.room_no " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "INNER JOIN Guests g ON b.guest_id = g.guest_id " +
                "WHERE b.booking_status IN ('CHECKED-IN', 'CONFIRMED') " +
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

    public static ActiveFolio getFolioForRoom(String roomNo) {
        ActiveFolio folio = new ActiveFolio();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return folio;

        String bookingSql = "SELECT TOP 1 b.booking_ref, b.room_no, b.guest_id, g.full_name, b.room_total_amount " +
                "FROM Bookings b " +
                "INNER JOIN Guests g ON b.guest_id = g.guest_id " +
                "WHERE b.room_no = ? AND b.booking_status IN ('CHECKED-IN', 'CONFIRMED') " +
                "ORDER BY b.created_at DESC";

        String serviceSql = "SELECT ISNULL(SUM(total_amount), 0) AS total_services " +
                "FROM RoomServiceOrders WHERE booking_ref = ? AND order_status != 'CANCELLED'";

        try (PreparedStatement pst = conn.prepareStatement(bookingSql)) {
            pst.setString(1, roomNo);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    folio.bookingRef = rs.getString("booking_ref");
                    folio.roomNo = rs.getString("room_no");
                    folio.guestId = rs.getString("guest_id");
                    folio.guestName = rs.getString("full_name");
                    folio.roomCharges = rs.getBigDecimal("room_total_amount");

                    try (PreparedStatement pstSrv = conn.prepareStatement(serviceSql)) {
                        pstSrv.setString(1, folio.bookingRef);
                        try (ResultSet rsSrv = pstSrv.executeQuery()) {
                            if (rsSrv.next()) {
                                folio.serviceCharges = rsSrv.getBigDecimal("total_services");
                            }
                        }
                    }

                    BigDecimal subtotal = folio.roomCharges.add(folio.serviceCharges);
                    folio.taxAmount = subtotal.multiply(new BigDecimal("0.05"));
                    folio.netPayable = subtotal.add(folio.taxAmount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return folio;
    }

    public static Vector<Vector<Object>> getServiceOrdersForBooking(String bookingRef) {
        Vector<Vector<Object>> items = new Vector<>();
        String sql = "SELECT sc.service_name, rso.quantity, rso.total_amount, rso.order_status " +
                "FROM RoomServiceOrders rso " +
                "INNER JOIN ServiceCatalog sc ON rso.service_id = sc.service_id " +
                "WHERE rso.booking_ref = ? AND rso.order_status != 'CANCELLED' " +
                "ORDER BY rso.ordered_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, bookingRef);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("service_name"));
                    row.add(rs.getInt("quantity"));
                    row.add(String.format("%,d MMK", rs.getBigDecimal("total_amount").longValue()));
                    row.add(rs.getString("order_status"));
                    items.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static Vector<Vector<Object>> getAllInvoices() {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT i.invoice_id, i.room_no, g.full_name, i.payment_method, " +
                "i.net_payable, CONVERT(VARCHAR(16), i.settled_at, 120) AS settle_time, i.payment_status " +
                "FROM Invoices i " +
                "INNER JOIN Guests g ON i.guest_id = g.guest_id " +
                "ORDER BY i.issued_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("invoice_id"));
                row.add(rs.getString("room_no"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("payment_method"));
                row.add(String.format("%,d MMK", rs.getBigDecimal("net_payable").longValue()));
                String sTime = rs.getString("settle_time");
                row.add(sTime != null ? sTime : "Pending");
                row.add(rs.getString("payment_status"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean settleInvoice(String bookingRef, String guestId, String roomNo,
                                        BigDecimal roomCharges, BigDecimal serviceCharges,
                                        BigDecimal taxAmount, BigDecimal netPayable,
                                        String paymentMethod) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String invoiceId = "INV-" + (System.currentTimeMillis() % 100000);
        String insertInvoiceSql = "INSERT INTO Invoices (invoice_id, booking_ref, guest_id, room_no, " +
                "room_charges, service_charges, tax_vat_amount, discount_amount, " +
                "net_payable, payment_method, payment_status, settled_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 0.00, ?, ?, 'PAID', CURRENT_TIMESTAMP)";

        String updateBookingSql = "UPDATE Bookings SET booking_status = 'COMPLETED' WHERE booking_ref = ?";
        String updateOrdersSql = "UPDATE RoomServiceOrders SET order_status = 'BILLED' WHERE booking_ref = ? AND order_status != 'CANCELLED'";
        String updateRoomSql = "UPDATE Rooms SET status = 'AVAILABLE' WHERE room_no = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement pst = conn.prepareStatement(insertInvoiceSql)) {
                pst.setString(1, invoiceId);
                pst.setString(2, bookingRef);
                pst.setString(3, guestId);
                pst.setString(4, roomNo);
                pst.setBigDecimal(5, roomCharges);
                pst.setBigDecimal(6, serviceCharges);
                pst.setBigDecimal(7, taxAmount);
                pst.setBigDecimal(8, netPayable);
                pst.setString(9, paymentMethod);
                pst.executeUpdate();
            }

            try (PreparedStatement pstBkg = conn.prepareStatement(updateBookingSql)) {
                pstBkg.setString(1, bookingRef);
                pstBkg.executeUpdate();
            }

            try (PreparedStatement pstOrders = conn.prepareStatement(updateOrdersSql)) {
                pstOrders.setString(1, bookingRef);
                pstOrders.executeUpdate();
            }

            try (PreparedStatement pstRoom = conn.prepareStatement(updateRoomSql)) {
                pstRoom.setString(1, roomNo);
                pstRoom.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
}