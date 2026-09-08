package model;

import java.sql.*;
import java.util.Vector;

public class HouseKeepingDBA {

    public static class StaffMember {
        public String userId;
        public String fullName;

        public StaffMember(String userId, String fullName) {
            this.userId = userId;
            this.fullName = fullName;
        }

        @Override
        public String toString() {
            return fullName;
        }
    }

    public static class HousekeepingKPIs {
        public String cleanReady = "0 Rooms";
        public String dirtyVacant = "0 Rooms";
        public String inProgress = "0 Rooms";
        public String maintenance = "0 Rooms";
    }

    public static HousekeepingKPIs getMetrics() {
        HousekeepingKPIs kpis = new HousekeepingKPIs();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return kpis;

        String sqlRooms = "SELECT " +
                "SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) AS clean_cnt, " +
                "SUM(CASE WHEN status = 'MAINTENANCE' THEN 1 ELSE 0 END) AS maint_cnt " +
                "FROM Rooms";

        String sqlTasks = "SELECT " +
                "SUM(CASE WHEN task_status = 'PENDING' THEN 1 ELSE 0 END) AS dirty_cnt, " +
                "SUM(CASE WHEN task_status = 'IN PROGRESS' THEN 1 ELSE 0 END) AS in_progress_cnt " +
                "FROM HousekeepingRequests";

        try {
            try (PreparedStatement pst = conn.prepareStatement(sqlRooms);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    kpis.cleanReady = rs.getInt("clean_cnt") + " Rooms";
                    kpis.maintenance = rs.getInt("maint_cnt") + " Rooms";
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(sqlTasks);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    kpis.dirtyVacant = rs.getInt("dirty_cnt") + " Rooms";
                    kpis.inProgress = rs.getInt("in_progress_cnt") + " Tasks";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kpis;
    }

    public static Vector<String> getAllRooms() {
        Vector<String> rooms = new Vector<>();
        String sql = "SELECT room_no FROM Rooms ORDER BY room_no";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                rooms.add(rs.getString("room_no"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static Vector<StaffMember> getHousekeepingStaff() {
        Vector<StaffMember> staffList = new Vector<>();
        String sql = "SELECT user_id, full_name FROM Users WHERE role = 'HOUSEKEEPING' AND status = 'ACTIVE' ORDER BY full_name ASC;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                staffList.add(new StaffMember(rs.getString("user_id"), rs.getString("full_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }

    public static Vector<Vector<Object>> getAllHousekeepingTasks() {
        Vector<Vector<Object>> data = new Vector<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return data;

        String sql = "SELECT hr.request_id, hr.room_no, " +
                "ISNULL(rc.category_name, 'Standard Suite') AS category_name, " +
                "ISNULL(u.full_name, 'Unassigned') AS staff_name, " +
                "hr.task_status, " +
                "CONVERT(VARCHAR(10), hr.created_at, 103) + ' ' + CONVERT(VARCHAR(5), hr.created_at, 108) AS created_time, " +
                "hr.request_type + ' (' + ISNULL(hr.preferred_time_slot, 'Anytime') + ')' AS task_details, " +
                "hr.assigned_staff_id " +
                "FROM HousekeepingRequests hr " +
                "LEFT JOIN Rooms r ON hr.room_no = r.room_no " +
                "LEFT JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "LEFT JOIN Users u ON hr.assigned_staff_id = u.user_id " +
                "ORDER BY hr.created_at DESC";

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String roomNo = rs.getString("room_no");
                String floorDisplay = deriveFloorFromRoomNo(roomNo);

                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("request_id"));
                row.add(roomNo);
                row.add(rs.getString("category_name"));
                row.add(floorDisplay);
                row.add(rs.getString("staff_name"));
                row.add(rs.getString("task_status"));
                row.add(rs.getString("created_time"));
                row.add(rs.getString("task_details"));
                row.add(rs.getString("assigned_staff_id"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static String deriveFloorFromRoomNo(String roomNo) {
        if (roomNo == null || roomNo.isEmpty()) return "Floor 1";

        String digitsOnly = roomNo.replaceAll("[^0-9]", "");
        if (!digitsOnly.isEmpty()) {
            if (digitsOnly.length() >= 3) {
                return "Floor " + digitsOnly.charAt(0);
            } else {
                return "Floor " + digitsOnly;
            }
        }

        if (roomNo.toUpperCase().contains("PH")) return "Penthouse";
        return "Floor 1";
    }

    public static boolean saveOrUpdateTask(Integer requestId, String roomNo, String staffId, String status, String notes, boolean isUpdate) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String cleanedNotes = (notes != null) ? notes.replaceAll("(\\s*\\([^)]*\\)){2,}", "$1").trim() : "Routine Cleaning";

        String timeSlot = "Immediate";
        String requestType = cleanedNotes;

        if (cleanedNotes.contains("(") && cleanedNotes.endsWith(")")) {
            int startIdx = cleanedNotes.indexOf("(");
            timeSlot = cleanedNotes.substring(startIdx + 1, cleanedNotes.length() - 1).trim();
            requestType = cleanedNotes.substring(0, startIdx).trim();
        }

        if (requestType.length() > 255) requestType = requestType.substring(0, 255);
        if (timeSlot.length() > 50) timeSlot = timeSlot.substring(0, 50);

        if (isUpdate && requestId != null) {
            String sql = "UPDATE HousekeepingRequests SET " +
                    "request_type = ?, " +
                    "preferred_time_slot = ?, " +
                    "assigned_staff_id = ?, " +
                    "task_status = ?, " +
                    "completed_at = CASE WHEN ? = 'COMPLETED' THEN CURRENT_TIMESTAMP ELSE completed_at END " +
                    "WHERE request_id = ?";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, requestType);
                pst.setString(2, timeSlot);
                pst.setString(3, staffId);
                pst.setString(4, status);
                pst.setString(5, status);
                pst.setInt(6, requestId);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String sql = "INSERT INTO HousekeepingRequests (room_no, request_type, preferred_time_slot, assigned_staff_id, task_status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, roomNo);
                pst.setString(2, requestType);
                pst.setString(3, timeSlot);
                pst.setString(4, staffId);
                pst.setString(5, status);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}