package model;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Vector;

public class ServiceManagementDBA {

    public static class ServiceCatalogKPIs {
        public String totalServices = "0 Items";
        public String activeAvailable = "0 Items";
        public String diningCount = "0 Items";
        public String spaWellnessCount = "0 Items";
    }

    public static ServiceCatalogKPIs getMetrics() {
        ServiceCatalogKPIs kpis = new ServiceCatalogKPIs();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return kpis;

        String sql = "SELECT " +
                "COUNT(*) AS total_cnt, " +
                "SUM(CASE WHEN is_available = 1 THEN 1 ELSE 0 END) AS available_cnt, " +
                "SUM(CASE WHEN category = 'Food & In-Room Dining' THEN 1 ELSE 0 END) AS dining_cnt, " +
                "SUM(CASE WHEN category = 'Spa & Wellness' THEN 1 ELSE 0 END) AS spa_cnt " +
                "FROM ServiceCatalog";

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                kpis.totalServices = rs.getInt("total_cnt") + " Items";
                kpis.activeAvailable = rs.getInt("available_cnt") + " Available";
                kpis.diningCount = rs.getInt("dining_cnt") + " Items";
                kpis.spaWellnessCount = rs.getInt("spa_cnt") + " Items";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kpis;
    }

    public static Vector<Vector<Object>> getAllCatalogServices() {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT service_id, service_name, category, unit_price, is_available FROM ServiceCatalog ORDER BY category, service_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("service_id"));
                row.add(rs.getString("service_name"));
                row.add(rs.getString("category"));
                row.add(String.format("%,d MMK", rs.getBigDecimal("unit_price").longValue()));
                row.add(rs.getBoolean("is_available") ? "AVAILABLE" : "UNAVAILABLE");
                row.add(rs.getBigDecimal("unit_price"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean saveOrUpdateService(Integer serviceId, String serviceName, String category, BigDecimal unitPrice, boolean isAvailable, boolean isUpdate) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        if (isUpdate && serviceId != null) {
            String sql = "UPDATE ServiceCatalog SET service_name = ?, category = ?, unit_price = ?, is_available = ? WHERE service_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, serviceName);
                pst.setString(2, category);
                pst.setBigDecimal(3, unitPrice);
                pst.setBoolean(4, isAvailable);
                pst.setInt(5, serviceId);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String sql = "INSERT INTO ServiceCatalog (service_name, category, unit_price, is_available) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, serviceName);
                pst.setString(2, category);
                pst.setBigDecimal(3, unitPrice);
                pst.setBoolean(4, isAvailable);
                return pst.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static boolean toggleServiceAvailability(int serviceId) {
        String sql = "UPDATE ServiceCatalog SET is_available = (CASE WHEN is_available = 1 THEN 0 ELSE 1 END) WHERE service_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, serviceId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}