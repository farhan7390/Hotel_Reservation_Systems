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
        String sql = "SELECT user_id, full_name FROM Users WHERE role IN ('HOUSEKEEPING', 'STAFF') AND status = 'ACTIVE' ORDER BY full_name";
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
        String sql = "SELECT hr.request_id, hr.room_no, rc.category_name, r.floor_level, " +
                "ISNULL(u.full_name, 'Unassigned') AS staff_name, hr.task_status, " +
                "CONVERT(VARCHAR(16), hr.created_at, 120) AS created_time, " +
                "hr.request_type, ISNULL(hr.preferred_time_slot, 'Anytime') AS time_slot, " +
                "hr.assigned_staff_id " +
                "FROM HousekeepingRequests hr " +
                "INNER JOIN Rooms r ON hr.room_no = r.room_no " +
                "INNER JOIN RoomCategories rc ON r.category_id = rc.category_id " +
                "LEFT JOIN Users u ON hr.assigned_staff_id = u.user_id " +
                "ORDER BY hr.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("request_id"));
                row.add(rs.getString("room_no"));
                row.add(rs.getString("category_name"));
                row.add(rs.getString("floor_level"));
                row.add(rs.getString("staff_name"));
                row.add(rs.getString("task_status"));
                row.add(rs.getString("created_time"));
                row.add(rs.getString("request_type") + " (" + rs.getString("time_slot") + ")");
                row.add(rs.getString("assigned_staff_id"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean saveOrUpdateTask(Integer requestId, String roomNo, String staffId, String status, String requestType, boolean isUpdate) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);

            if (isUpdate && requestId != null) {
                String updateSql = "UPDATE HousekeepingRequests SET room_no = ?, assigned_staff_id = ?, " +
                        "task_status = ?, request_type = ?, " +
                        "completed_at = (CASE WHEN ? = 'COMPLETED' THEN CURRENT_TIMESTAMP ELSE completed_at END) " +
                        "WHERE request_id = ?";
                try (PreparedStatement pst = conn.prepareStatement(updateSql)) {
                    pst.setString(1, roomNo);
                    pst.setString(2, staffId);
                    pst.setString(3, status);
                    pst.setString(4, requestType);
                    pst.setString(5, status);
                    pst.setInt(6, requestId);
                    pst.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO HousekeepingRequests (room_no, assigned_staff_id, request_type, preferred_time_slot, task_status) " +
                        "VALUES (?, ?, ?, 'Scheduled Task', ?)";
                try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
                    pst.setString(1, roomNo);
                    pst.setString(2, staffId);
                    pst.setString(3, requestType);
                    pst.setString(4, status);
                    pst.executeUpdate();
                }
            }

            if ("COMPLETED".equalsIgnoreCase(status)) {
                String updateRoomSql = "UPDATE Rooms SET status = 'AVAILABLE' WHERE room_no = ? AND status != 'OCCUPIED'";
                try (PreparedStatement pstRoom = conn.prepareStatement(updateRoomSql)) {
                    pstRoom.setString(1, roomNo);
                    pstRoom.executeUpdate();
                }
            } else if ("MAINTENANCE".equalsIgnoreCase(status)) {
                String updateRoomSql = "UPDATE Rooms SET status = 'MAINTENANCE' WHERE room_no = ?";
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
}