package model;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
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

    public static ExecutiveMetrics getExecutiveMetrics() {
        ExecutiveMetrics metrics = new ExecutiveMetrics();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return metrics;

        String sqlSales = "SELECT " +
                "ISNULL(SUM(net_payable), 0) AS total_sales, " +
                "ISNULL(SUM(room_charges), 0) AS total_room_rev, " +
                "COUNT(*) AS total_invoices " +
                "FROM Invoices WHERE payment_status IN ('PAID', 'SETTLED')";

        String sqlOcc = "SELECT " +
                "COUNT(*) AS total_rooms, " +
                "SUM(CASE WHEN status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_rooms " +
                "FROM Rooms";

        try {
            BigDecimal totalSales = BigDecimal.ZERO;
            BigDecimal roomRev = BigDecimal.ZERO;
            int invoiceCount = 0;

            try (PreparedStatement pst = conn.prepareStatement(sqlSales);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    totalSales = rs.getBigDecimal("total_sales");
                    roomRev = rs.getBigDecimal("total_room_rev");
                    invoiceCount = rs.getInt("total_invoices");
                }
            }

            DecimalFormat df = new DecimalFormat("#,##0");
            metrics.grossSales = df.format(totalSales) + " MMK";

            if (invoiceCount > 0) {
                BigDecimal adrVal = roomRev.divide(BigDecimal.valueOf(invoiceCount), BigDecimal.ROUND_HALF_UP);
                metrics.adr = df.format(adrVal) + " MMK";
            } else {
                metrics.adr = "0 MMK";
            }

            try (PreparedStatement pstOcc = conn.prepareStatement(sqlOcc);
                 ResultSet rsOcc = pstOcc.executeQuery()) {
                if (rsOcc.next()) {
                    int total = rsOcc.getInt("total_rooms");
                    int occupied = rsOcc.getInt("occupied_rooms");
                    if (total > 0) {
                        double rate = ((double) occupied / total) * 100.0;
                        metrics.occupancyRate = String.format("%.1f%%", rate);
                    }
                }
            }

            BigDecimal expenses = totalSales.multiply(new BigDecimal("0.28"));
            metrics.operatingExpenses = df.format(expenses) + " MMK";

            BigDecimal netProfit = totalSales.subtract(expenses);
            if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
                double margin = netProfit.divide(totalSales, 4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100.0;
                metrics.netMargin = String.format("%.1f%%", margin);
            } else {
                metrics.netMargin = "0.0%";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return metrics;
    }

    public static SegmentDistribution getDistributionBreakdown() {
        SegmentDistribution dist = new SegmentDistribution();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return dist;

        String tierSql = "SELECT pt.tier_name, ISNULL(SUM(i.net_payable), 0) AS tier_rev " +
                "FROM Invoices i " +
                "INNER JOIN Bookings b ON i.booking_ref = b.booking_ref " +
                "INNER JOIN PricingTiers pt ON b.tier_id = pt.tier_id " +
                "WHERE i.payment_status IN ('PAID', 'SETTLED') " +
                "GROUP BY pt.tier_name";

        String deptSql = "SELECT " +
                "ISNULL(SUM(room_charges), 0) AS room_rev, " +
                "ISNULL(SUM(service_charges), 0) AS service_rev, " +
                "ISNULL(SUM(net_payable), 0) AS total_rev " +
                "FROM Invoices WHERE payment_status IN ('PAID', 'SETTLED')";

        try {
            BigDecimal totalTierRev = BigDecimal.ZERO;
            try (PreparedStatement pst = conn.prepareStatement(tierSql);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("tier_name");
                    BigDecimal val = rs.getBigDecimal("tier_rev");
                    totalTierRev = totalTierRev.add(val);

                    if (name.toLowerCase().contains("staycation")) dist.staycationRev = val;
                    else if (name.toLowerCase().contains("daycation")) dist.daycationRev = val;
                    else if (name.toLowerCase().contains("night")) dist.nightStayRev = val;
                }
            }

            if (totalTierRev.compareTo(BigDecimal.ZERO) > 0) {
                dist.staycationPct = dist.staycationRev.multiply(new BigDecimal(100)).divide(totalTierRev, BigDecimal.ROUND_HALF_UP).intValue();
                dist.daycationPct = dist.daycationRev.multiply(new BigDecimal(100)).divide(totalTierRev, BigDecimal.ROUND_HALF_UP).intValue();
                dist.nightStayPct = dist.nightStayRev.multiply(new BigDecimal(100)).divide(totalTierRev, BigDecimal.ROUND_HALF_UP).intValue();
            }

            try (PreparedStatement pstDept = conn.prepareStatement(deptSql);
                 ResultSet rsDept = pstDept.executeQuery()) {
                if (rsDept.next()) {
                    dist.roomRev = rsDept.getBigDecimal("room_rev");
                    BigDecimal totalSrv = rsDept.getBigDecimal("service_rev");
                    BigDecimal grandTotal = rsDept.getBigDecimal("total_rev");

                    dist.diningRev = totalSrv.multiply(new BigDecimal("0.70"));
                    dist.spaLaundryRev = totalSrv.multiply(new BigDecimal("0.30"));

                    if (grandTotal.compareTo(BigDecimal.ZERO) > 0) {
                        dist.roomPct = dist.roomRev.multiply(new BigDecimal(100)).divide(grandTotal, BigDecimal.ROUND_HALF_UP).intValue();
                        dist.diningPct = dist.diningRev.multiply(new BigDecimal(100)).divide(grandTotal, BigDecimal.ROUND_HALF_UP).intValue();
                        dist.spaLaundryPct = dist.spaLaundryRev.multiply(new BigDecimal(100)).divide(grandTotal, BigDecimal.ROUND_HALF_UP).intValue();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dist;
    }

    public static Vector<Vector<Object>> getAuditLedger() {
        Vector<Vector<Object>> rows = new Vector<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return rows;

        String sql = "SELECT " +
                "CONVERT(VARCHAR(10), ISNULL(i.settled_at, i.issued_at), 103) AS ledger_date, " +
                "i.invoice_id, " +
                "g.full_name, " +
                "pt.tier_name, " +
                "i.room_charges, " +
                "i.service_charges, " +
                "i.net_payable, " +
                "i.payment_status " +
                "FROM Invoices i " +
                "INNER JOIN Guests g ON i.guest_id = g.guest_id " +
                "INNER JOIN Bookings b ON i.booking_ref = b.booking_ref " +
                "INNER JOIN PricingTiers pt ON b.tier_id = pt.tier_id " +
                "ORDER BY ISNULL(i.settled_at, i.issued_at) DESC";

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("ledger_date"));
                row.add(rs.getString("invoice_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("tier_name"));
                row.add(String.format("%,d MMK", rs.getBigDecimal("room_charges").longValue()));
                row.add(String.format("%,d MMK", rs.getBigDecimal("service_charges").longValue()));
                row.add(String.format("%,d MMK", rs.getBigDecimal("net_payable").longValue()));
                row.add(rs.getString("payment_status").toUpperCase());
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }
}