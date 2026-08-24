package model;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Vector;

public class CategoryManagementDBA {

    public static Vector<Vector<Object>> getAllRoomCategories() {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT category_id, category_name, max_capacity_desc, base_night_rate FROM RoomCategories ORDER BY category_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("category_id"));
                row.add(rs.getString("category_name"));
                row.add(rs.getString("max_capacity_desc"));
                row.add(String.format("%,d MMK", rs.getBigDecimal("base_night_rate").longValue()));
                row.add(rs.getBigDecimal("base_night_rate"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean saveOrUpdateRoomCategory(Integer categoryId, String name, String capacity, BigDecimal baseRate) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        if (categoryId != null) {
            String sql = "UPDATE RoomCategories SET category_name = ?, max_capacity_desc = ?, base_night_rate = ? WHERE category_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, name);
                pst.setString(2, capacity);
                pst.setBigDecimal(3, baseRate);
                pst.setInt(4, categoryId);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String sql = "INSERT INTO RoomCategories (category_name, max_capacity_desc, base_night_rate) VALUES (?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, name);
                pst.setString(2, capacity);
                pst.setBigDecimal(3, baseRate);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static Vector<Vector<Object>> getAllPricingTiers() {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT tier_id, tier_name, duration_hours, description FROM PricingTiers ORDER BY tier_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("tier_id"));
                row.add(rs.getString("tier_name"));
                row.add(rs.getInt("duration_hours") + " Hours");
                row.add(rs.getString("description") != null ? rs.getString("description") : "");
                row.add(rs.getInt("duration_hours"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean saveOrUpdatePricingTier(Integer tierId, String name, int durationHours, String description) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        if (tierId != null) {
            String sql = "UPDATE PricingTiers SET tier_name = ?, duration_hours = ?, description = ? WHERE tier_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, name);
                pst.setInt(2, durationHours);
                pst.setString(3, description);
                pst.setInt(4, tierId);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String sql = "INSERT INTO PricingTiers (tier_name, duration_hours, description) VALUES (?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, name);
                pst.setInt(2, durationHours);
                pst.setString(3, description);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static Vector<Vector<Object>> getAllLoyaltyRewards() {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT reward_id, reward_title, points_cost, description, is_active FROM LoyaltyRewards ORDER BY reward_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("reward_id"));
                row.add(rs.getString("reward_title"));
                row.add(String.format("%,d pts", rs.getInt("points_cost")));
                row.add(rs.getString("description") != null ? rs.getString("description") : "");
                row.add(rs.getBoolean("is_active") ? "ACTIVE" : "INACTIVE");
                row.add(rs.getInt("points_cost"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean saveOrUpdateLoyaltyReward(Integer rewardId, String title, int pointsCost, String description, boolean isActive) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        if (rewardId != null) {
            String sql = "UPDATE LoyaltyRewards SET reward_title = ?, points_cost = ?, description = ?, is_active = ? WHERE reward_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, title);
                pst.setInt(2, pointsCost);
                pst.setString(3, description);
                pst.setBoolean(4, isActive);
                pst.setInt(5, rewardId);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String sql = "INSERT INTO LoyaltyRewards (reward_title, points_cost, description, is_active) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, title);
                pst.setInt(2, pointsCost);
                pst.setString(3, description);
                pst.setBoolean(4, isActive);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}