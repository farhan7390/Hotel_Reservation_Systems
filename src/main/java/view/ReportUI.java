package view;

import model.ReportDBA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Vector;

public class ReportUI extends JPanel {

    private DefaultTableModel tableModel;
    private JTable reportTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtSearch;

    private CleanMetricCard cardGrossSales, cardAdr, cardOccupancy, cardExpenses;
    private JLabel lblMarginVal;

    private JProgressBar barStaycation, barDaycation, barNightStay;
    private JLabel lblStaycationVal, lblDaycationVal, lblNightStayVal;

    private JProgressBar barRoom, barDining, barSpa;
    private JLabel lblRoomVal, lblDiningVal, lblSpaVal;

    public ReportUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createReportWorkspace(), BorderLayout.CENTER);
        loadReportData();
    }

    private JPanel createReportWorkspace() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel controlBar = new JPanel(new BorderLayout());
        controlBar.setOpaque(false);
        controlBar.setMaximumSize(new Dimension(1400, 38));
        controlBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel periodPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        periodPills.setOpaque(false);

        String[] periods = {"Today", "This Week", "This Month (August)", "Q3 2026", "Yearly"};
        for (int i = 0; i < periods.length; i++) {
            boolean isSelected = (i == 2);
            JButton pill = new JButton(periods[i]);
            pill.setFont(new Font("Century Gothic", Font.BOLD, 11));
            pill.setBackground(isSelected ? new Color(99, 102, 241) : Color.WHITE);
            pill.setForeground(isSelected ? Color.WHITE : new Color(71, 85, 105));
            pill.setFocusPainted(false);
            pill.setCursor(new Cursor(Cursor.HAND_CURSOR));
            pill.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(isSelected ? new Color(99, 102, 241) : new Color(226, 232, 240), 1, true),
                    new EmptyBorder(6, 14, 6, 14)
            ));
            periodPills.add(pill);
        }

        JPanel exportGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        exportGroup.setOpaque(false);

        JButton btnPrint = createToolbarActionBtn("🖨️ Print Report", new Color(241, 245, 249), new Color(71, 85, 105));
        btnPrint.addActionListener(e -> JOptionPane.showMessageDialog(this, "Printing executive report to system default printer."));

        JButton btnExport = createToolbarActionBtn("📥 Export PDF / XLS", new Color(16, 185, 129), Color.WHITE);
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Report exported to 'Hotel_Executive_Report_2026.pdf'"));

        exportGroup.add(btnPrint);
        exportGroup.add(btnExport);

        controlBar.add(periodPills, BorderLayout.WEST);
        controlBar.add(exportGroup, BorderLayout.EAST);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(1400, 115));
        statsRow.setPreferredSize(new Dimension(1400, 115));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardGrossSales = new CleanMetricCard("Total Gross Sales", "0 MMK", "▲ Database Settled", new Color(16, 185, 129), "💰");
        cardAdr = new CleanMetricCard("Average Daily Rate", "0 MMK", "▲ Per Stay Folio", new Color(99, 102, 241), "📈");
        cardOccupancy = new CleanMetricCard("Occupancy Rate", "0.0%", "▲ Current Live In-House", new Color(168, 85, 247), "🛏️");
        cardExpenses = new CleanMetricCard("Operating Expenses", "0 MMK", "▼ Est. 28% Cost", new Color(245, 158, 11), "🧾");

        statsRow.add(cardGrossSales);
        statsRow.add(cardAdr);
        statsRow.add(cardOccupancy);
        statsRow.add(cardExpenses);

        JPanel splitRow = new JPanel(new BorderLayout(18, 0));
        splitRow.setOpaque(false);
        splitRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftSummaryCard = createLeftBreakdownCard();
        JPanel rightTableCard = createRightAuditCard();

        splitRow.add(leftSummaryCard, BorderLayout.WEST);
        splitRow.add(rightTableCard, BorderLayout.CENTER);

        main.add(controlBar);
        main.add(Box.createRigidArea(new Dimension(0, 14)));
        main.add(statsRow);
        main.add(Box.createRigidArea(new Dimension(0, 16)));
        main.add(splitRow);

        return main;
    }

    private JPanel createLeftBreakdownCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(340, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel title = new JLabel("Revenue & Segment Distribution");
        title.setFont(new Font("Century Gothic", Font.BOLD, 14));
        title.setForeground(new Color(30, 41, 59));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        addSectionHeader(card, "BOOKING SEGMENTS");
        barStaycation = new JProgressBar(0, 100);
        lblStaycationVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Staycation (Overnight)", lblStaycationVal, barStaycation, new Color(99, 102, 241));

        barDaycation = new JProgressBar(0, 100);
        lblDaycationVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Daycation (Day Pass)", lblDaycationVal, barDaycation, new Color(168, 85, 247));

        barNightStay = new JProgressBar(0, 100);
        lblNightStayVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Night Stay (Transit)", lblNightStayVal, barNightStay, new Color(245, 158, 11));

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        addSectionHeader(card, "DEPARTMENT CONTRIBUTION");
        barRoom = new JProgressBar(0, 100);
        lblRoomVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Room Bookings", lblRoomVal, barRoom, new Color(16, 185, 129));

        barDining = new JProgressBar(0, 100);
        lblDiningVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Dining & Room Service", lblDiningVal, barDining, new Color(59, 130, 246));

        barSpa = new JProgressBar(0, 100);
        lblSpaVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Spa & Laundry Services", lblSpaVal, barSpa, new Color(100, 116, 139));

        card.add(Box.createVerticalGlue());

        JPanel netMarginBox = new JPanel(new BorderLayout());
        netMarginBox.setBackground(new Color(248, 250, 252));
        netMarginBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        netMarginBox.setMaximumSize(new Dimension(1400, 46));
        netMarginBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMargin = new JLabel("Net Operating Profit Margin");
        lblMargin.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblMargin.setForeground(new Color(100, 116, 139));

        lblMarginVal = new JLabel("0.0%");
        lblMarginVal.setFont(new Font("Century Gothic", Font.BOLD, 14));
        lblMarginVal.setForeground(new Color(16, 185, 129));

        netMarginBox.add(lblMargin, BorderLayout.WEST);
        netMarginBox.add(lblMarginVal, BorderLayout.EAST);

        card.add(netMarginBox);

        return card;
    }

    private JPanel createRightAuditCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel tableTitle = new JLabel("Audit Ledger & Settled Bookings (Database)");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 14));
        tableTitle.setForeground(new Color(30, 41, 59));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        txtSearch.setPreferredSize(new Dimension(170, 28));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(txtSearch.getText().trim()); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(txtSearch.getText().trim()); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(txtSearch.getText().trim()); }
        });

        headerRow.add(tableTitle, BorderLayout.WEST);
        headerRow.add(txtSearch, BorderLayout.EAST);

        String[] cols = {"Date", "Folio ID", "Guest", "Tier / Stay", "Room Rev", "Service", "Net Amount", "Status"};
        tableModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        reportTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        reportTable.setRowSorter(rowSorter);

        reportTable.setRowHeight(36);
        reportTable.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        reportTable.setShowVerticalLines(false);
        reportTable.setGridColor(new Color(241, 245, 249));
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = reportTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 12));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        reportTable.getColumnModel().getColumn(7).setCellRenderer(new ReportStatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(headerRow, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    public void loadReportData() {
        ReportDBA.ExecutiveMetrics kpis = ReportDBA.getExecutiveMetrics();
        cardGrossSales.setValue(kpis.grossSales);
        cardAdr.setValue(kpis.adr);
        cardOccupancy.setValue(kpis.occupancyRate);
        cardExpenses.setValue(kpis.operatingExpenses);
        lblMarginVal.setText(kpis.netMargin);

        ReportDBA.SegmentDistribution dist = ReportDBA.getDistributionBreakdown();
        barStaycation.setValue(dist.staycationPct);
        lblStaycationVal.setText(String.format("%,d MMK (%d%%)", dist.staycationRev.longValue(), dist.staycationPct));

        barDaycation.setValue(dist.daycationPct);
        lblDaycationVal.setText(String.format("%,d MMK (%d%%)", dist.daycationRev.longValue(), dist.daycationPct));

        barNightStay.setValue(dist.nightStayPct);
        lblNightStayVal.setText(String.format("%,d MMK (%d%%)", dist.nightStayRev.longValue(), dist.nightStayPct));

        barRoom.setValue(dist.roomPct);
        lblRoomVal.setText(String.format("%,d MMK (%d%%)", dist.roomRev.longValue(), dist.roomPct));

        barDining.setValue(dist.diningPct);
        lblDiningVal.setText(String.format("%,d MMK (%d%%)", dist.diningRev.longValue(), dist.diningPct));

        barSpa.setValue(dist.spaLaundryPct);
        lblSpaVal.setText(String.format("%,d MMK (%d%%)", dist.spaLaundryRev.longValue(), dist.spaLaundryPct));

        tableModel.setRowCount(0);
        Vector<Vector<Object>> ledger = ReportDBA.getAuditLedger();
        for (Vector<Object> row : ledger) {
            tableModel.addRow(row);
        }
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private JButton createToolbarActionBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        return btn;
    }

    private void addSectionHeader(JPanel parent, String heading) {
        JLabel lbl = new JLabel(heading);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    private void addProgressMeterComponent(JPanel parent, String label, JLabel lblVal, JProgressBar bar, Color barColor) {
        JPanel row = new JPanel(new BorderLayout(0, 2));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(1400, 38));

        JPanel labelRow = new JPanel(new BorderLayout());
        labelRow.setOpaque(false);

        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        lblName.setForeground(new Color(51, 65, 85));

        lblVal.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblVal.setForeground(new Color(30, 41, 59));

        labelRow.add(lblName, BorderLayout.WEST);
        labelRow.add(lblVal, BorderLayout.EAST);

        bar.setPreferredSize(new Dimension(0, 5));
        bar.setForeground(barColor);
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorderPainted(false);

        row.add(labelRow, BorderLayout.NORTH);
        row.add(bar, BorderLayout.CENTER);

        parent.add(row);
        parent.add(Box.createRigidArea(new Dimension(0, 6)));
    }

    static class CleanMetricCard extends JPanel {
        private final JLabel lblVal;

        public CleanMetricCard(String title, String value, String trend, Color trendColor, String icon) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(226, 232, 240), 1, true),
                    new EmptyBorder(12, 16, 12, 16)
            ));

            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 11));
            lblTitle.setForeground(new Color(100, 116, 139));

            JLabel lblIcon = new JLabel(icon);
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            topRow.add(lblTitle, BorderLayout.WEST);
            topRow.add(lblIcon, BorderLayout.EAST);

            lblVal = new JLabel(value);
            lblVal.setFont(new Font("Century Gothic", Font.BOLD, 18));
            lblVal.setForeground(new Color(30, 41, 59));
            lblVal.setBorder(new EmptyBorder(4, 0, 4, 0));

            JLabel lblTrend = new JLabel(trend);
            lblTrend.setFont(new Font("Century Gothic", Font.BOLD, 11));
            lblTrend.setForeground(trendColor);

            add(topRow, BorderLayout.NORTH);
            add(lblVal, BorderLayout.CENTER);
            add(lblTrend, BorderLayout.SOUTH);
        }

        public void setValue(String val) {
            lblVal.setText(val);
        }
    }

    static class ReportStatusBadgeRenderer extends DefaultTableCellRenderer {
        public ReportStatusBadgeRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Century Gothic", Font.BOLD, 10));
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setText(value != null ? value.toString() : "");
            setOpaque(false);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            String status = getText();
            Color bg = (status.equalsIgnoreCase("SETTLED") || status.equalsIgnoreCase("PAID")) ? new Color(16, 185, 129) :
                    status.equalsIgnoreCase("UNPAID") || status.equalsIgnoreCase("PENDING") ? new Color(245, 158, 11) :
                            new Color(100, 116, 139);

            int padX = 14;
            int badgeW = getWidth() - (padX * 2);
            int badgeH = getHeight() - 10;
            int badgeY = 5;

            g2.setColor(bg);
            g2.fillRoundRect(padX, badgeY, badgeW, badgeH, 6, 6);

            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(status)) / 2;
            int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(status, textX, textY);

            g2.dispose();
        }
    }
}