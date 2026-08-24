package model;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Vector;

public class RateAndPricingDBA {

    public static class PricingKPIs {
        public String staycationAvg = "0 MMK";
        public String daycationAvg = "0 MMK";
        public String nightStayAvg = "0 MMK";
        public String activeSurge = "+0%";
    }

    public static PricingKPIs getPricingMetrics() {
        PricingKPIs kpis = new PricingKPIs();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return kpis;

        String sqlAvg = "SELECT pt.tier_name, ISNULL(AVG(tr.base_rate), 0) AS avg_rate " +
                "FROM TariffRules tr " +
                "INNER JOIN PricingTiers pt ON tr.tier_id = pt.tier_id " +
                "WHERE tr.is_active = 1 " +
                "GROUP BY pt.tier_name";

        String sqlSurge = "SELECT TOP 1 season_multiplier_name, multiplier_value " +
                "FROM TariffRules WHERE multiplier_value > 1.00 AND is_active = 1";

        try {
            DecimalFormat df = new DecimalFormat("#,##0");
            try (PreparedStatement pst = conn.prepareStatement(sqlAvg);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String tier = rs.getString("tier_name").toLowerCase();
                    BigDecimal avg = rs.getBigDecimal("avg_rate");
                    if (tier.contains("staycation")) kpis.staycationAvg = df.format(avg) + " MMK";
                    else if (tier.contains("daycation")) kpis.daycationAvg = df.format(avg) + " MMK";
                    else if (tier.contains("night")) kpis.nightStayAvg = df.format(avg) + " MMK";
                }
            }

            try (PreparedStatement pstSurge = conn.prepareStatement(sqlSurge);
                 ResultSet rsSurge = pstSurge.executeQuery()) {
                if (rsSurge.next()) {
                    double mult = rsSurge.getDouble("multiplier_value");
                    int pct = (int) Math.round((mult - 1.0) * 100);
                    kpis.activeSurge = "+" + pct + "% Weekend";
                } else {
                    kpis.activeSurge = "Standard (1.0x)";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kpis;
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

    public static Vector<Vector<Object>> getAllTariffRules() {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT tr.rule_id, rc.category_name, pt.tier_name, tr.base_rate, " +
                "tr.weekend_surcharge, tr.extra_bed_charge, tr.season_multiplier_name, " +
                "tr.multiplier_value, tr.is_active " +
                "FROM TariffRules tr " +
                "INNER JOIN RoomCategories rc ON tr.category_id = rc.category_id " +
                "INNER JOIN PricingTiers pt ON tr.tier_id = pt.tier_id " +
                "ORDER BY tr.category_id, tr.tier_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("rule_id"));
                row.add(rs.getString("category_name"));
                row.add(rs.getString("tier_name"));
                row.add(String.format("%,d MMK", rs.getBigDecimal("base_rate").longValue()));
                row.add(String.format("%,d MMK", rs.getBigDecimal("weekend_surcharge").longValue()));
                row.add(String.format("%,d MMK", rs.getBigDecimal("extra_bed_charge").longValue()));
                row.add(rs.getString("season_multiplier_name"));

                double mult = rs.getDouble("multiplier_value");
                String status = !rs.getBoolean("is_active") ? "INACTIVE" :
                        mult > 1.0 ? "SURGE" :
                                mult < 1.0 ? "DISCOUNTED" : "ACTIVE";
                row.add(status);
                row.add(rs.getBigDecimal("multiplier_value"));

                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean saveOrUpdateTariffRule(String categoryName, String tierName,
                                                 BigDecimal baseRate, BigDecimal weekendSurcharge,
                                                 BigDecimal extraBedFee, String seasonMultiplierName,
                                                 BigDecimal multiplierValue) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String getIdsSql = "SELECT rc.category_id, pt.tier_id " +
                "FROM RoomCategories rc, PricingTiers pt " +
                "WHERE rc.category_name = ? AND pt.tier_name = ?";

        String mergeSql = "MERGE TariffRules AS target " +
                "USING (SELECT ? AS category_id, ? AS tier_id) AS source " +
                "ON (target.category_id = source.category_id AND target.tier_id = source.tier_id) " +
                "WHEN MATCHED THEN " +
                "  UPDATE SET base_rate = ?, weekend_surcharge = ?, extra_bed_charge = ?, " +
                "             season_multiplier_name = ?, multiplier_value = ?, is_active = 1 " +
                "WHEN NOT MATCHED THEN " +
                "  INSERT (category_id, tier_id, base_rate, weekend_surcharge, extra_bed_charge, season_multiplier_name, multiplier_value, is_active) " +
                "  VALUES (source.category_id, source.tier_id, ?, ?, ?, ?, ?, 1);";

        try {
            int categoryId = 1, tierId = 1;
            try (PreparedStatement pstIds = conn.prepareStatement(getIdsSql)) {
                pstIds.setString(1, categoryName);
                pstIds.setString(2, tierName);
                try (ResultSet rs = pstIds.executeQuery()) {
                    if (rs.next()) {
                        categoryId = rs.getInt("category_id");
                        tierId = rs.getInt("tier_id");
                    }
                }
            }

            try (PreparedStatement pstMerge = conn.prepareStatement(mergeSql)) {
                pstMerge.setInt(1, categoryId);
                pstMerge.setInt(2, tierId);

                pstMerge.setBigDecimal(3, baseRate);
                pstMerge.setBigDecimal(4, weekendSurcharge);
                pstMerge.setBigDecimal(5, extraBedFee);
                pstMerge.setString(6, seasonMultiplierName);
                pstMerge.setBigDecimal(7, multiplierValue);

                pstMerge.setBigDecimal(8, baseRate);
                pstMerge.setBigDecimal(9, weekendSurcharge);
                pstMerge.setBigDecimal(10, extraBedFee);
                pstMerge.setString(11, seasonMultiplierName);
                pstMerge.setBigDecimal(12, multiplierValue);

                return pstMerge.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}