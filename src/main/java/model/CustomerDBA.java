package model;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CustomerDBA {

    public static class GuestProfile {
        public String guestId = "";
        public String userId = "";
        public String fullName = "Guest";
        public String nidPassport = "";
        public String phone = "";
        public String email = "";
        public String city = "Yangon";
        public String vipTier = "STANDARD";
        public int loyaltyPoints = 0;
        public String guestStatus = "ACTIVE";
        public String preferences = "";
        public String activeRoomNo = "None";
        public String activeBookingRef = "";
    }

    public static class RoomCardData {
        public String roomNo;
        public String title;
        public String floor;
        public String price;
        public String tierBadge;
        public String features;
        public String[] imagePaths;
    }

    public static class LiveCustomerBill {
        public String bookingRef = "";
        public String roomNo = "None";
        public String roomDetails = "No active room stay";
        public BigDecimal roomCharges = BigDecimal.ZERO;
        public BigDecimal serviceCharges = BigDecimal.ZERO;
        public BigDecimal taxAmount = BigDecimal.ZERO;
        public BigDecimal netPayable = BigDecimal.ZERO;
        public String paymentStatus = "UNPAID";
        public boolean hasActiveStay = false;
    }

    public static boolean createCustomerBooking(String guestId, String roomNo, String tierName, LocalDate inDate, LocalDate outDate) {
        Connection conn = DBConnection.getConnection();
        if (conn == null || guestId == null || guestId.isEmpty()) return false;

        String getTierSql = "SELECT tier_id FROM PricingTiers WHERE tier_name = ? OR tier_name LIKE ?";
        String getCatSql = "SELECT rc.category_name, rc.base_night_rate FROM Rooms r " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id WHERE r.room_no = ?";
        String insertBkgSql = "INSERT INTO Bookings (booking_ref, guest_id, room_no, tier_id, check_in_date, check_out_date, " +
                "total_nights_days, room_total_amount, booking_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'CONFIRMED')";
        String updateRoomSql = "UPDATE Rooms SET status = 'OCCUPIED' WHERE room_no = ?";

        try {
            conn.setAutoCommit(false);

            int tierId = 1;
            String cleanTier = tierName.split(" \\(")[0].trim();
            try (PreparedStatement pstTier = conn.prepareStatement(getTierSql)) {
                pstTier.setString(1, tierName);
                pstTier.setString(2, cleanTier + "%");
                try (ResultSet rs = pstTier.executeQuery()) {
                    if (rs.next()) tierId = rs.getInt("tier_id");
                }
            }

            String categoryName = "Standard Double";
            try (PreparedStatement pstCat = conn.prepareStatement(getCatSql)) {
                pstCat.setString(1, roomNo);
                try (ResultSet rs = pstCat.executeQuery()) {
                    if (rs.next()) categoryName = rs.getString("category_name");
                }
            }

            long totalUnits = java.time.temporal.ChronoUnit.DAYS.between(inDate, outDate);
            if (totalUnits <= 0) totalUnits = 1;

            BigDecimal totalAmount = BookingDBA.calculateTariff(categoryName, cleanTier, inDate, outDate);
            String newBookingRef = "BKG-" + (System.currentTimeMillis() % 100000);

            try (PreparedStatement pstInsert = conn.prepareStatement(insertBkgSql)) {
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

    public static GuestProfile getGuestProfile(String userIdentifier) {
        GuestProfile profile = new GuestProfile();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return profile;

        String query = "SELECT g.guest_id, g.user_id, g.full_name, g.nid_passport, g.phone, " +
                "g.email, g.city, g.vip_tier, g.loyalty_points, g.guest_status, g.preferences_notes, " +
                "b.room_no AS active_room, b.booking_ref AS active_bkg " +
                "FROM Guests g " +
                "LEFT JOIN Users u ON g.user_id = u.user_id " +
                "LEFT JOIN Bookings b ON g.guest_id = b.guest_id AND b.booking_status IN ('CHECKED-IN', 'CONFIRMED') " +
                "WHERE g.email = ? OR g.phone = ? OR u.username = ? " +
                "ORDER BY b.created_at DESC";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, userIdentifier);
            pst.setString(2, userIdentifier);
            pst.setString(3, userIdentifier);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    profile.guestId = rs.getString("guest_id");
                    profile.userId = rs.getString("user_id");
                    profile.fullName = rs.getString("full_name");
                    profile.nidPassport = rs.getString("nid_passport");
                    profile.phone = rs.getString("phone");
                    profile.email = rs.getString("email") != null ? rs.getString("email") : "";
                    profile.city = rs.getString("city") != null ? rs.getString("city") : "Yangon";
                    profile.vipTier = rs.getString("vip_tier");
                    profile.loyaltyPoints = rs.getInt("loyalty_points");
                    profile.guestStatus = rs.getString("guest_status");
                    profile.preferences = rs.getString("preferences_notes") != null ? rs.getString("preferences_notes") : "";
                    profile.activeRoomNo = rs.getString("active_room") != null ? rs.getString("active_room") : "None";
                    profile.activeBookingRef = rs.getString("active_bkg") != null ? rs.getString("active_bkg") : "";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public static LiveCustomerBill getLiveCustomerBill(String guestId) {
        LiveCustomerBill bill = new LiveCustomerBill();
        Connection conn = DBConnection.getConnection();
        if (conn == null || guestId == null || guestId.isEmpty()) return bill;

        String bkgSql = "SELECT TOP 1 b.booking_ref, b.room_no, rc.category_name, b.room_total_amount " +
                "FROM Bookings b " +
                "INNER JOIN Rooms r ON b.room_no = r.room_no " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "WHERE b.guest_id = ? AND b.booking_status IN ('CHECKED-IN', 'CONFIRMED') " +
                "ORDER BY b.created_at DESC";

        String srvSql = "SELECT ISNULL(SUM(total_amount), 0) AS total_srv " +
                "FROM RoomServiceOrders WHERE booking_ref = ? AND order_status != 'CANCELLED'";

        try (PreparedStatement pst = conn.prepareStatement(bkgSql)) {
            pst.setString(1, guestId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    bill.hasActiveStay = true;
                    bill.bookingRef = rs.getString("booking_ref");
                    bill.roomNo = rs.getString("room_no");
                    bill.roomDetails = "Room " + bill.roomNo + " (" + rs.getString("category_name") + ")";
                    bill.roomCharges = rs.getBigDecimal("room_total_amount");

                    try (PreparedStatement pstSrv = conn.prepareStatement(srvSql)) {
                        pstSrv.setString(1, bill.bookingRef);
                        try (ResultSet rsSrv = pstSrv.executeQuery()) {
                            if (rsSrv.next()) {
                                bill.serviceCharges = rsSrv.getBigDecimal("total_srv");
                            }
                        }
                    }

                    BigDecimal subtotal = bill.roomCharges.add(bill.serviceCharges);
                    bill.taxAmount = subtotal.multiply(new BigDecimal("0.05"));
                    bill.netPayable = subtotal.add(bill.taxAmount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bill;
    }

    public static boolean settleCustomerPaymentAndAwardPoints(String guestId, String paymentMethod) {
        Connection conn = DBConnection.getConnection();
        String cleanPaymentMethod = "Cash";
        if (conn == null) return false;

        LiveCustomerBill bill = getLiveCustomerBill(guestId);
        if (!bill.hasActiveStay || bill.bookingRef.isEmpty()) return false;

        String invoiceId = "INV-" + (System.currentTimeMillis() % 100000);
        int earnedPoints = bill.netPayable.divide(new BigDecimal("1000"), BigDecimal.ROUND_FLOOR).intValue();

        String insertInvoiceSql = "INSERT INTO Invoices (invoice_id, booking_ref, guest_id, room_no, room_charges, " +
                "service_charges, tax_vat_amount, discount_amount, net_payable, payment_method, payment_status, settled_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 0.00, ?, ?, 'PAID', CURRENT_TIMESTAMP)";

        String updateBookingSql = "UPDATE Bookings SET booking_status = 'COMPLETED' WHERE booking_ref = ?";
        String updateOrdersSql = "UPDATE RoomServiceOrders SET order_status = 'BILLED' WHERE booking_ref = ? AND order_status != 'CANCELLED'";
        String updateRoomSql = "UPDATE Rooms SET status = 'AVAILABLE' WHERE room_no = ?";

        String updateGuestPointsSql = "UPDATE Guests SET loyalty_points = loyalty_points + ?, " +
                "vip_tier = CASE " +
                "  WHEN loyalty_points + ? >= 3500 THEN 'PLATINUM VIP' " +
                "  WHEN loyalty_points + ? >= 2000 THEN 'GOLD VIP' " +
                "  WHEN loyalty_points + ? >= 1000 THEN 'SILVER VIP' " +
                "  ELSE vip_tier END " +
                "WHERE guest_id = ?";

        String insertLoyaltyHistSql = "INSERT INTO GuestLoyaltyHistory (guest_id, activity_desc, reference_code, points_change, status) " +
                "VALUES (?, ?, ?, ?, 'CREDITED')";

        if (paymentMethod != null) {
            String pm = paymentMethod.trim();
            if (pm.equalsIgnoreCase("KBZPay")) cleanPaymentMethod = "KBZPay";
            else if (pm.equalsIgnoreCase("WavePay")) cleanPaymentMethod = "WavePay";
            else if (pm.toLowerCase().contains("card") || pm.toLowerCase().contains("credit")) cleanPaymentMethod = "Credit Card";
            else if (pm.toLowerCase().contains("bank") || pm.toLowerCase().contains("transfer")) cleanPaymentMethod = "Bank Transfer";
            else if (pm.equalsIgnoreCase("Pending")) cleanPaymentMethod = "Pending";
            else cleanPaymentMethod = "Cash";
        }

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement pstInv = conn.prepareStatement(insertInvoiceSql)) {
                pstInv.setString(1, invoiceId);
                pstInv.setString(2, bill.bookingRef);
                pstInv.setString(3, guestId);
                pstInv.setString(4, bill.roomNo);
                pstInv.setBigDecimal(5, bill.roomCharges);
                pstInv.setBigDecimal(6, bill.serviceCharges);
                pstInv.setBigDecimal(7, bill.taxAmount);
                pstInv.setBigDecimal(8, bill.netPayable);
                pstInv.setString(9, cleanPaymentMethod);
                pstInv.executeUpdate();
            }

            try (PreparedStatement pstBkg = conn.prepareStatement(updateBookingSql)) {
                pstBkg.setString(1, bill.bookingRef);
                pstBkg.executeUpdate();
            }
            try (PreparedStatement pstOrders = conn.prepareStatement(updateOrdersSql)) {
                pstOrders.setString(1, bill.bookingRef);
                pstOrders.executeUpdate();
            }

            try (PreparedStatement pstRoom = conn.prepareStatement(updateRoomSql)) {
                pstRoom.setString(1, bill.roomNo);
                pstRoom.executeUpdate();
            }

            try (PreparedStatement pstGuest = conn.prepareStatement(updateGuestPointsSql)) {
                pstGuest.setInt(1, earnedPoints);
                pstGuest.setInt(2, earnedPoints);
                pstGuest.setInt(3, earnedPoints);
                pstGuest.setInt(4, earnedPoints);
                pstGuest.setString(5, guestId);
                pstGuest.executeUpdate();
            }

            try (PreparedStatement pstHist = conn.prepareStatement(insertLoyaltyHistSql)) {
                pstHist.setString(1, guestId);
                pstHist.setString(2, "Stay Settlement (" + bill.roomDetails + ")");
                pstHist.setString(3, invoiceId);
                pstHist.setInt(4, earnedPoints);
                pstHist.executeUpdate();
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

    public static Vector<Vector<Object>> getLoyaltyHistory(String guestId) {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT CONVERT(VARCHAR(10), created_at, 103) AS hist_date, activity_desc, " +
                "ISNULL(reference_code, 'N/A') AS ref_code, points_change, status " +
                "FROM GuestLoyaltyHistory WHERE guest_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, guestId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("hist_date"));
                    row.add(rs.getString("activity_desc"));
                    row.add(rs.getString("ref_code"));
                    int pts = rs.getInt("points_change");
                    row.add((pts > 0 ? "+ " : "") + String.format("%,d pts", pts));
                    row.add(rs.getString("status"));
                    data.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean redeemLoyaltyPerk(String guestId, String perkTitle, int pointsCost) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String checkSql = "SELECT loyalty_points FROM Guests WHERE guest_id = ?";
        String deductSql = "UPDATE Guests SET loyalty_points = loyalty_points - ? WHERE guest_id = ?";
        String histSql = "INSERT INTO GuestLoyaltyHistory (guest_id, activity_desc, reference_code, points_change, status) " +
                "VALUES (?, ?, ?, ?, 'REDEEMED')";

        try {
            conn.setAutoCommit(false);
            int available = 0;
            try (PreparedStatement pstCheck = conn.prepareStatement(checkSql)) {
                pstCheck.setString(1, guestId);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    if (rs.next()) available = rs.getInt("loyalty_points");
                }
            }

            if (available < pointsCost) return false;

            try (PreparedStatement pstDeduct = conn.prepareStatement(deductSql)) {
                pstDeduct.setInt(1, pointsCost);
                pstDeduct.setString(2, guestId);
                pstDeduct.executeUpdate();
            }

            try (PreparedStatement pstHist = conn.prepareStatement(histSql)) {
                pstHist.setString(1, guestId);
                pstHist.setString(2, "Redeemed: " + perkTitle);
                pstHist.setString(3, "RWD-" + (System.currentTimeMillis() % 10000));
                pstHist.setInt(4, -pointsCost);
                pstHist.executeUpdate();
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

    public static List<RoomCardData> getAvailableRoomCards() {
        List<RoomCardData> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        String query = "SELECT r.room_no, rc.category_name, r.floor_level, rc.base_night_rate, " +
                "r.has_balcony, r.has_sea_view, r.has_jacuzzi, r.image_path " +
                "FROM Rooms r " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "WHERE r.status = 'AVAILABLE' ORDER BY r.room_no";

        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                RoomCardData r = new RoomCardData();
                r.roomNo = rs.getString("room_no");
                r.title = rs.getString("category_name");
                r.floor = rs.getString("floor_level");
                r.price = String.format("%,d MMK / night", rs.getBigDecimal("base_night_rate").longValue());
                r.tierBadge = "Available Now";

                StringBuilder amenities = new StringBuilder();
                if (rs.getBoolean("has_balcony")) amenities.append("Balcony, ");
                if (rs.getBoolean("has_sea_view")) amenities.append("Sea View, ");
                if (rs.getBoolean("has_jacuzzi")) amenities.append("Jacuzzi, ");
                amenities.append("Free Wi-Fi, Air-Conditioned");
                r.features = amenities.toString();

                String customImg = rs.getString("image_path");
                if (customImg != null && !customImg.trim().isEmpty()) {
                    r.imagePaths = new String[]{customImg};
                } else {
                    String cleanNum = r.roomNo.toLowerCase().replace("-", "");
                    r.imagePaths = new String[]{
                            "/images/rooms/" + cleanNum + "_1.jpg",
                            "/images/rooms/" + cleanNum + "_2.jpg",
                            "/images/rooms/" + cleanNum + "_3.jpg"
                    };
                }
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Vector<Vector<Object>> getGuestReservations(String guestId) {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT b.booking_ref, b.room_no + ' (' + rc.category_name + ')' AS room_info, " +
                "pt.tier_name, CONVERT(VARCHAR(10), b.check_in_date, 103) AS in_date, " +
                "CONVERT(VARCHAR(10), b.check_out_date, 103) AS out_date, " +
                "b.room_total_amount, b.booking_status " +
                "FROM Bookings b " +
                "INNER JOIN Rooms r ON b.room_no = r.room_no " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "INNER JOIN PricingTiers pt ON b.tier_id = pt.tier_id " +
                "WHERE b.guest_id = ? ORDER BY b.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, guestId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("booking_ref"));
                    row.add(rs.getString("room_info"));
                    row.add(rs.getString("tier_name"));
                    row.add(rs.getString("in_date"));
                    row.add(rs.getString("out_date"));
                    row.add(String.format("%,d MMK", rs.getBigDecimal("room_total_amount").longValue()));
                    row.add(rs.getString("booking_status"));
                    data.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Vector<Vector<Object>> getGuestServiceOrders(String guestId) {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT rso.order_id, sc.service_name, rso.quantity, rso.total_amount, " +
                "CONVERT(VARCHAR(8), rso.ordered_at, 108) AS order_time, rso.order_status " +
                "FROM RoomServiceOrders rso " +
                "INNER JOIN ServiceCatalog sc ON rso.service_id = sc.service_id " +
                "INNER JOIN Bookings b ON rso.booking_ref = b.booking_ref " +
                "WHERE b.guest_id = ? ORDER BY rso.ordered_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, guestId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("order_id"));
                    row.add(rs.getString("service_name"));
                    row.add(String.valueOf(rs.getInt("quantity")));
                    row.add(String.format("%,d MMK", rs.getBigDecimal("total_amount").longValue()));
                    row.add(rs.getString("order_time"));
                    row.add(rs.getString("order_status"));
                    data.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Vector<String> getCatalogServices() {
        Vector<String> items = new Vector<>();
        String sql = "SELECT service_id, service_name, unit_price FROM ServiceCatalog WHERE is_available = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                items.add(rs.getInt("service_id") + " - " + rs.getString("service_name") + " (" + String.format("%,d MMK", rs.getBigDecimal("unit_price").longValue()) + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static boolean placeRoomServiceOrder(String guestId, int serviceId, int quantity, String instructions) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String findBookingSql = "SELECT TOP 1 booking_ref, room_no FROM Bookings " +
                "WHERE guest_id = ? AND booking_status IN ('CHECKED-IN', 'CONFIRMED') " +
                "ORDER BY created_at DESC";
        String getPriceSql = "SELECT unit_price FROM ServiceCatalog WHERE service_id = ?";
        String insertSql = "INSERT INTO RoomServiceOrders (order_id, booking_ref, room_no, service_id, quantity, total_amount, delivery_instructions, order_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'PREPARING')";

        try {
            String bookingRef = null;
            String roomNo = null;
            try (PreparedStatement pstBkg = conn.prepareStatement(findBookingSql)) {
                pstBkg.setString(1, guestId);
                try (ResultSet rs = pstBkg.executeQuery()) {
                    if (rs.next()) {
                        bookingRef = rs.getString("booking_ref");
                        roomNo = rs.getString("room_no");
                    }
                }
            }

            if (bookingRef == null) return false;

            BigDecimal unitPrice = BigDecimal.ZERO;
            try (PreparedStatement pstPrice = conn.prepareStatement(getPriceSql)) {
                pstPrice.setInt(1, serviceId);
                try (ResultSet rs = pstPrice.executeQuery()) {
                    if (rs.next()) unitPrice = rs.getBigDecimal("unit_price");
                }
            }

            BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
            String orderId = "SR-" + (System.currentTimeMillis() % 100000);

            try (PreparedStatement pstInsert = conn.prepareStatement(insertSql)) {
                pstInsert.setString(1, orderId);
                pstInsert.setString(2, bookingRef);
                pstInsert.setString(3, roomNo);
                pstInsert.setInt(4, serviceId);
                pstInsert.setInt(5, quantity);
                pstInsert.setBigDecimal(6, total);
                pstInsert.setString(7, instructions.isEmpty() ? "Deliver to room" : instructions);
                return pstInsert.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean placeHousekeepingRequest(String guestId, String requestType, String timeSlot) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String findRoomSql = "SELECT TOP 1 room_no FROM Bookings WHERE guest_id = ? AND booking_status IN ('CHECKED-IN', 'CONFIRMED') ORDER BY created_at DESC";
        String insertSql = "INSERT INTO HousekeepingRequests (room_no, guest_id, request_type, preferred_time_slot, task_status) " +
                "VALUES (?, ?, ?, ?, 'PENDING')";

        try {
            String roomNo = "R-101";
            try (PreparedStatement pstRoom = conn.prepareStatement(findRoomSql)) {
                pstRoom.setString(1, guestId);
                try (ResultSet rs = pstRoom.executeQuery()) {
                    if (rs.next()) roomNo = rs.getString("room_no");
                }
            }

            try (PreparedStatement pstInsert = conn.prepareStatement(insertSql)) {
                pstInsert.setString(1, roomNo);
                pstInsert.setString(2, guestId);
                pstInsert.setString(3, requestType);
                pstInsert.setString(4, timeSlot);
                return pstInsert.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateGuestProfile(String guestId, String fullName, String phone, String email, String city, String nidPassport, String preferences) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String sql = "UPDATE Guests SET full_name = ?, phone = ?, email = ?, city = ?, nid_passport = ?, preferences_notes = ? WHERE guest_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, fullName);
            pst.setString(2, phone);
            pst.setString(3, email);
            pst.setString(4, city);
            pst.setString(5, nidPassport);
            pst.setString(6, preferences);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}