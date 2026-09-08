package model;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Vector;

public class ReportDBA {

    public static class ExecutiveMetrics {
        public String grossSales = "0 MMK";
        public String adr = "0 MMK";
        public String occupancyRate = "0.0%";
        public String operatingExpenses = "0 MMK";
        public String netMargin = "0.0%";
    }

    public static class SegmentDistribution {
        public BigDecimal staycationRev = BigDecimal.ZERO;
        public int staycationPct = 0;
        public BigDecimal daycationRev = BigDecimal.ZERO;
        public int daycationPct = 0;
        public BigDecimal nightStayRev = BigDecimal.ZERO;
        public int nightStayPct = 0;

        public BigDecimal roomRev = BigDecimal.ZERO;
        public int roomPct = 0;
        public BigDecimal diningRev = BigDecimal.ZERO;
        public int diningPct = 0;
        public BigDecimal spaLaundryRev = BigDecimal.ZERO;
        public int spaLaundryPct = 0;
    }

    private static String buildDateFilterClause(String period, String dateCol) {
        switch (period) {
            case "Today":
                return " CAST(" + dateCol + " AS DATE) = CAST(CURRENT_TIMESTAMP AS DATE) ";
            case "This Week":
                return " " + dateCol + " >= DATEADD(DAY, -7, CURRENT_TIMESTAMP) ";
            case "This Month":
                return " MONTH(" + dateCol + ") = MONTH(CURRENT_TIMESTAMP) AND YEAR(" + dateCol + ") = YEAR(CURRENT_TIMESTAMP) ";
            case "Q3 2026":
            case "Q3":
                return " " + dateCol + " >= '2026-07-01' AND " + dateCol + " < '2026-10-01' ";
            case "Yearly":
            default:
                return " YEAR(" + dateCol + ") = YEAR(CURRENT_TIMESTAMP) ";
        }
    }

    public static ExecutiveMetrics getExecutiveMetrics(String period) {
        ExecutiveMetrics m = new ExecutiveMetrics();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return m;

        String invWhere = buildDateFilterClause(period, "settled_at");
        String sqlSales = "SELECT ISNULL(SUM(net_payable), 0) AS total_sales, COUNT(*) AS invoice_count " +
                "FROM Invoices WHERE payment_status = 'PAID' AND " + invWhere;

        String sqlOcc = "SELECT " +
                "  (SELECT COUNT(*) FROM Rooms WHERE status = 'OCCUPIED') AS occupied_count, " +
                "  (SELECT COUNT(*) FROM Rooms WHERE status != 'MAINTENANCE') AS total_rooms";

        try {
            BigDecimal totalGross = BigDecimal.ZERO;
            int invoiceCount = 0;

            try (PreparedStatement pst = conn.prepareStatement(sqlSales);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    totalGross = rs.getBigDecimal("total_sales");
                    invoiceCount = rs.getInt("invoice_count");
                }
            }

            m.grossSales = String.format("%,d MMK", totalGross.longValue());

            BigDecimal adr = invoiceCount > 0 ? totalGross.divide(BigDecimal.valueOf(invoiceCount), BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
            m.adr = String.format("%,d MMK", adr.longValue());

            BigDecimal expenses = totalGross.multiply(new BigDecimal("0.28"));
            m.operatingExpenses = String.format("%,d MMK", expenses.longValue());

            BigDecimal netProfit = totalGross.subtract(expenses);
            double margin = totalGross.compareTo(BigDecimal.ZERO) > 0
                    ? (netProfit.doubleValue() / totalGross.doubleValue()) * 100.0
                    : 0.0;
            m.netMargin = String.format("%.1f%%", margin);

            try (PreparedStatement pstOcc = conn.prepareStatement(sqlOcc);
                 ResultSet rsOcc = pstOcc.executeQuery()) {
                if (rsOcc.next()) {
                    int occ = rsOcc.getInt("occupied_count");
                    int total = rsOcc.getInt("total_rooms");
                    double occRate = total > 0 ? ((double) occ / total) * 100.0 : 0.0;
                    m.occupancyRate = String.format("%.1f%%", occRate);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    public static SegmentDistribution getDistributionBreakdown(String period) {
        SegmentDistribution dist = new SegmentDistribution();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return dist;

        String bkgDateWhere = buildDateFilterClause(period, "b.created_at");
        String sqlSegments = "SELECT ISNULL(pt.tier_name, 'Other') AS tier_type, ISNULL(SUM(b.room_total_amount), 0) AS total_rev " +
                "FROM Bookings b " +
                "LEFT JOIN PricingTiers pt ON b.tier_id = pt.tier_id " +
                "WHERE b.booking_status IN ('CONFIRMED', 'CHECKED-IN', 'COMPLETED') AND " + bkgDateWhere +
                "GROUP BY pt.tier_name";

        String invDateWhere = buildDateFilterClause(period, "settled_at");
        String sqlDepts = "SELECT ISNULL(SUM(room_charges), 0) AS total_room, ISNULL(SUM(service_charges), 0) AS total_service " +
                "FROM Invoices WHERE payment_status = 'PAID' AND " + invDateWhere;

        try {
            BigDecimal totalSegmentRev = BigDecimal.ZERO;
            try (PreparedStatement pst = conn.prepareStatement(sqlSegments);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String tier = rs.getString("tier_type").toLowerCase();
                    BigDecimal amt = rs.getBigDecimal("total_rev");
                    totalSegmentRev = totalSegmentRev.add(amt);

                    if (tier.contains("staycation")) dist.staycationRev = dist.staycationRev.add(amt);
                    else if (tier.contains("daycation")) dist.daycationRev = dist.daycationRev.add(amt);
                    else if (tier.contains("transit") || tier.contains("night")) dist.nightStayRev = dist.nightStayRev.add(amt);
                }
            }

            if (totalSegmentRev.compareTo(BigDecimal.ZERO) > 0) {
                dist.staycationPct = (int) Math.round((dist.staycationRev.doubleValue() / totalSegmentRev.doubleValue()) * 100.0);
                dist.daycationPct = (int) Math.round((dist.daycationRev.doubleValue() / totalSegmentRev.doubleValue()) * 100.0);
                dist.nightStayPct = Math.max(0, 100 - (dist.staycationPct + dist.daycationPct));
            }

            try (PreparedStatement pstD = conn.prepareStatement(sqlDepts);
                 ResultSet rsD = pstD.executeQuery()) {
                if (rsD.next()) {
                    dist.roomRev = rsD.getBigDecimal("total_room");
                    dist.diningRev = rsD.getBigDecimal("total_service");
                    dist.spaLaundryRev = BigDecimal.ZERO; // Optional service ledger
                }
            }

            BigDecimal totalDept = dist.roomRev.add(dist.diningRev).add(dist.spaLaundryRev);
            if (totalDept.compareTo(BigDecimal.ZERO) > 0) {
                dist.roomPct = (int) Math.round((dist.roomRev.doubleValue() / totalDept.doubleValue()) * 100.0);
                dist.diningPct = (int) Math.round((dist.diningRev.doubleValue() / totalDept.doubleValue()) * 100.0);
                dist.spaLaundryPct = Math.max(0, 100 - (dist.roomPct + dist.diningPct));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dist;
    }

    public static Vector<Vector<Object>> getAuditLedger(String period) {
        Vector<Vector<Object>> data = new Vector<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return data;

        String invDateWhere = buildDateFilterClause(period, "inv.settled_at");
        String sql = "SELECT CONVERT(VARCHAR(10), inv.settled_at, 103) AS inv_date, " +
                "inv.invoice_id, " +
                "ISNULL(g.full_name, 'Guest') AS guest_name, " +
                "ISNULL(pt.tier_name, 'Staycation') AS stay_tier, " +
                "inv.room_charges, inv.service_charges, inv.net_payable, inv.payment_status " +
                "FROM Invoices inv " +
                "LEFT JOIN Guests g ON inv.guest_id = g.guest_id " +
                "LEFT JOIN Bookings b ON inv.booking_ref = b.booking_ref " +
                "LEFT JOIN PricingTiers pt ON b.tier_id = pt.tier_id " +
                "WHERE " + invDateWhere +
                "ORDER BY inv.settled_at DESC";

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("inv_date"));
                row.add(rs.getString("invoice_id"));
                row.add(rs.getString("guest_name"));
                row.add(rs.getString("stay_tier"));
                row.add(String.format("%,d MMK", rs.getBigDecimal("room_charges").longValue()));
                row.add(String.format("%,d MMK", rs.getBigDecimal("service_charges").longValue()));
                row.add(String.format("%,d MMK", rs.getBigDecimal("net_payable").longValue()));
                row.add(rs.getString("payment_status"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}