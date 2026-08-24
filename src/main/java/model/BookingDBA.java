package model;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Vector;

public class BookingDBA {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Vector<String> getAvailableRoomsByCategory(String categoryName, String currentRoomNo) {
        Vector<String> rooms = new Vector<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return rooms;

        String sql;
        boolean hasCategory = (categoryName != null && !categoryName.trim().isEmpty());

        if (hasCategory) {
            sql = "SELECT r.room_no FROM Rooms r " +
                    "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                    "WHERE rc.category_name = ? AND (r.status = 'AVAILABLE' OR r.room_no = ?) " +
                    "ORDER BY r.room_no";
        } else {
            sql = "SELECT room_no FROM Rooms WHERE status = 'AVAILABLE' OR room_no = ? ORDER BY room_no";
        }

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            if (hasCategory) {
                pst.setString(1, categoryName);
                pst.setString(2, currentRoomNo != null ? currentRoomNo : "");
            } else {
                pst.setString(1, currentRoomNo != null ? currentRoomNo : "");
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rooms.add(rs.getString("room_no"));
                }
            }

            if (rooms.isEmpty() && hasCategory) {
                String fallbackSql = "SELECT r.room_no FROM Rooms r " +
                        "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                        "WHERE rc.category_name = ? ORDER BY r.room_no";
                try (PreparedStatement pstFb = conn.prepareStatement(fallbackSql)) {
                    pstFb.setString(1, categoryName);
                    try (ResultSet rsFb = pstFb.executeQuery()) {
                        while (rsFb.next()) {
                            rooms.add(rsFb.getString("room_no"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static Vector<String> getPricingTiers() {
        Vector<String> tiers = new Vector<>();
        String sql = "SELECT tier_name FROM PricingTiers ORDER BY tier_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tiers.add(rs.getString("tier_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tiers;
    }

    public static Vector<String> getRoomCategories() {
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

    public static BigDecimal calculateTariff(String categoryName, String tierName, LocalDate inDate, LocalDate outDate) {
        BigDecimal total = BigDecimal.ZERO;
        long units = ChronoUnit.DAYS.between(inDate, outDate);
        if (units <= 0) units = 1;

        Connection conn = DBConnection.getConnection();
        if (conn == null || categoryName == null || tierName == null) return total;

        String sqlRule = "SELECT tr.base_rate, tr.multiplier_value FROM TariffRules tr " +
                "INNER JOIN RoomCategories rc ON tr.category_id = rc.category_id " +
                "INNER JOIN PricingTiers pt ON tr.tier_id = pt.tier_id " +
                "WHERE rc.category_name = ? AND pt.tier_name = ? AND tr.is_active = 1";

        try (PreparedStatement pst = conn.prepareStatement(sqlRule)) {
            pst.setString(1, categoryName);
            pst.setString(2, tierName);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    BigDecimal base = rs.getBigDecimal("base_rate");
                    BigDecimal multiplier = rs.getBigDecimal("multiplier_value");
                    return base.multiply(multiplier).multiply(BigDecimal.valueOf(units));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlFallback = "SELECT base_night_rate FROM RoomCategories WHERE category_name = ?";
        try (PreparedStatement pstFallback = conn.prepareStatement(sqlFallback)) {
            pstFallback.setString(1, categoryName);
            try (ResultSet rs = pstFallback.executeQuery()) {
                if (rs.next()) {
                    BigDecimal base = rs.getBigDecimal("base_night_rate");
                    double tierFactor = 1.0;
                    if (tierName.toLowerCase().contains("daycation")) tierFactor = 0.60;
                    else if (tierName.toLowerCase().contains("night")) tierFactor = 0.85;

                    total = base.multiply(BigDecimal.valueOf(tierFactor)).multiply(BigDecimal.valueOf(units));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public static Vector<Vector<Object>> getAllBookings() {
        Vector<Vector<Object>> data = new Vector<>();
        String query = "SELECT b.booking_ref, g.full_name, b.room_no, pt.tier_name, " +
                "CONVERT(VARCHAR(10), b.check_in_date, 103) AS in_date, " +
                "CONVERT(VARCHAR(10), b.check_out_date, 103) AS out_date, " +
                "b.room_total_amount, b.booking_status, g.phone, g.nid_passport, rc.category_name " +
                "FROM Bookings b " +
                "INNER JOIN Guests g ON b.guest_id = g.guest_id " +
                "INNER JOIN Rooms r ON b.room_no = r.room_no " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "INNER JOIN PricingTiers pt ON b.tier_id = pt.tier_id " +
                "ORDER BY b.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("booking_ref"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("room_no"));
                row.add(rs.getString("tier_name"));
                row.add(rs.getString("in_date"));
                row.add(rs.getString("out_date"));
                row.add(String.format("%,d MMK", rs.getBigDecimal("room_total_amount").longValue()));
                row.add(rs.getString("booking_status"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("nid_passport"));
                row.add(rs.getString("category_name"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean saveOrUpdateBooking(String bookingRef, String guestName, String contact, String nid,
                                              String roomNo, String tierName, LocalDate inDate, LocalDate outDate,
                                              BigDecimal totalAmount, boolean isUpdate) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);

            String guestId = null;
            String findGuestSql = "SELECT guest_id FROM Guests WHERE phone = ? OR nid_passport = ?";
            try (PreparedStatement pstFind = conn.prepareStatement(findGuestSql)) {
                pstFind.setString(1, contact);
                pstFind.setString(2, nid);
                try (ResultSet rs = pstFind.executeQuery()) {
                    if (rs.next()) {
                        guestId = rs.getString("guest_id");
                    }
                }
            }

            if (guestId == null) {
                guestId = "GST-" + (System.currentTimeMillis() % 100000);
                String insertGuestSql = "INSERT INTO Guests (guest_id, full_name, nid_passport, phone, vip_tier, guest_status) " +
                        "VALUES (?, ?, ?, ?, 'STANDARD', 'ACTIVE')";
                try (PreparedStatement pstGuest = conn.prepareStatement(insertGuestSql)) {
                    pstGuest.setString(1, guestId);
                    pstGuest.setString(2, guestName);
                    pstGuest.setString(3, nid.isEmpty() ? "N/A" : nid);
                    pstGuest.setString(4, contact);
                    pstGuest.executeUpdate();
                }
            }

            int tierId = 1;
            String findTierSql = "SELECT tier_id FROM PricingTiers WHERE tier_name = ?";
            try (PreparedStatement pstTier = conn.prepareStatement(findTierSql)) {
                pstTier.setString(1, tierName);
                try (ResultSet rs = pstTier.executeQuery()) {
                    if (rs.next()) tierId = rs.getInt("tier_id");
                }
            }

            long totalUnits = ChronoUnit.DAYS.between(inDate, outDate);
            if (totalUnits <= 0) totalUnits = 1;

            if (isUpdate) {
                String updateSql = "UPDATE Bookings SET room_no = ?, tier_id = ?, check_in_date = ?, check_out_date = ?, " +
                        "total_nights_days = ?, room_total_amount = ? WHERE booking_ref = ?";
                try (PreparedStatement pstUpdate = conn.prepareStatement(updateSql)) {
                    pstUpdate.setString(1, roomNo);
                    pstUpdate.setInt(2, tierId);
                    pstUpdate.setDate(3, java.sql.Date.valueOf(inDate));
                    pstUpdate.setDate(4, java.sql.Date.valueOf(outDate));
                    pstUpdate.setInt(5, (int) totalUnits);
                    pstUpdate.setBigDecimal(6, totalAmount);
                    pstUpdate.setString(7, bookingRef);
                    pstUpdate.executeUpdate();
                }
            } else {
                String newBookingRef = "BKG-" + (System.currentTimeMillis() % 100000);
                String insertBookingSql = "INSERT INTO Bookings (booking_ref, guest_id, room_no, tier_id, check_in_date, check_out_date, " +
                        "total_nights_days, room_total_amount, booking_status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'CONFIRMED')";
                try (PreparedStatement pstInsert = conn.prepareStatement(insertBookingSql)) {
                    pstInsert.setString(1, newBookingRef);
                    pstInsert.setString(2, guestId);
                    pstInsert.setString(3, roomNo);
                    pstInsert.setInt(4, tierId);
                    pstInsert.setDate(5, java.sql.Date.valueOf(inDate));
                    pstInsert.setDate(6, java.sql.Date.valueOf(outDate));
                    pstInsert.setInt(7, (int) totalUnits);
                    pstInsert.setBigDecimal(8, totalAmount);
                    pstInsert.executeUpdate();
                }

                String updateRoomSql = "UPDATE Rooms SET status = 'OCCUPIED' WHERE room_no = ?";
                try (PreparedStatement pstRoom = conn.prepareStatement(updateRoomSql)) {
                    pstRoom.setString(1, roomNo);
                    pstRoom.executeUpdate();
                }
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

    public static boolean updateBookingStatus(String bookingRef, String newStatus) {
        String updateStatusSql = "UPDATE Bookings SET booking_status = ? WHERE booking_ref = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(updateStatusSql)) {
            pst.setString(1, newStatus);
            pst.setString(2, bookingRef);

            boolean updated = pst.executeUpdate() > 0;
            if (updated && ("COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus))) {
                String releaseRoomSql = "UPDATE Rooms SET status = 'AVAILABLE' WHERE room_no = (SELECT room_no FROM Bookings WHERE booking_ref = ?)";
                try (PreparedStatement pstRoom = conn.prepareStatement(releaseRoomSql)) {
                    pstRoom.setString(1, bookingRef);
                    pstRoom.executeUpdate();
                }
            }
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}