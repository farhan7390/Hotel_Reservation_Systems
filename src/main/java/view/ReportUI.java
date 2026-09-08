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
import java.awt.geom.RoundRectangle2D;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private String activePeriod = "This Month";
    private JPanel periodPillsContainer;

    public ReportUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        add(createReportWorkspace(), BorderLayout.CENTER);
        loadReportData(activePeriod);
    }

    private JPanel createReportWorkspace() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(248, 250, 252));
        main.setBorder(new EmptyBorder(20, 26, 20, 26));

        JPanel controlBar = new JPanel(new BorderLayout());
        controlBar.setOpaque(false);
        controlBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        controlBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        periodPillsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        periodPillsContainer.setOpaque(false);

        String currentMonthName = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM"));
        String[] periods = {"Today", "This Week", "This Month (" + currentMonthName + ")", "Q3 2026", "Yearly"};

        for (String p : periods) {
            String filterKey = p.startsWith("This Month") ? "This Month" : p;
            boolean isSelected = filterKey.equals(activePeriod);

            JButton pill = new JButton(p) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean active = getClientProperty("active") == Boolean.TRUE;

                    if (active) {
                        g2.setColor(new Color(99, 102, 241));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(241, 245, 249));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                        g2.setColor(new Color(203, 213, 225));
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    } else {
                        g2.setColor(Color.WHITE);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                        g2.setColor(new Color(226, 232, 240));
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };

            pill.putClientProperty("active", isSelected);
            pill.putClientProperty("filterKey", filterKey);
            pill.setFont(new Font("Century Gothic", Font.BOLD, 11));
            pill.setForeground(isSelected ? Color.WHITE : new Color(71, 85, 105));
            pill.setContentAreaFilled(false);
            pill.setBorderPainted(false);
            pill.setFocusPainted(false);
            pill.setCursor(new Cursor(Cursor.HAND_CURSOR));
            pill.setBorder(new EmptyBorder(7, 16, 7, 16));

            pill.addActionListener(e -> {
                for (Component c : periodPillsContainer.getComponents()) {
                    if (c instanceof JButton) {
                        ((JButton) c).putClientProperty("active", false);
                        ((JButton) c).setForeground(new Color(71, 85, 105));
                        c.repaint();
                    }
                }
                pill.putClientProperty("active", true);
                pill.setForeground(Color.WHITE);
                pill.repaint();

                this.activePeriod = (String) pill.getClientProperty("filterKey");
                loadReportData(activePeriod);
            });

            periodPillsContainer.add(pill);
        }

        JPanel exportGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        exportGroup.setOpaque(false);

        JButton btnPrint = createToolbarActionBtn("🖨️  Print Report", new Color(241, 245, 249), new Color(30, 41, 59), new Color(203, 213, 225));
        btnPrint.addActionListener(e -> handlePrint());

        JButton btnExport = createToolbarActionBtn("📥  Export CSV / XLS", new Color(16, 185, 129), Color.WHITE, new Color(16, 185, 129));
        btnExport.addActionListener(e -> handleExportCSV());

        exportGroup.add(btnPrint);
        exportGroup.add(btnExport);

        controlBar.add(periodPillsContainer, BorderLayout.WEST);
        controlBar.add(exportGroup, BorderLayout.EAST);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        statsRow.setPreferredSize(new Dimension(Integer.MAX_VALUE, 115));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardGrossSales = new CleanMetricCard("Total Gross Sales", "0 MMK", "▲ Settled Revenue", new Color(16, 185, 129), "💰");
        cardAdr = new CleanMetricCard("Average Daily Rate", "0 MMK", "▲ Per Stay Average", new Color(99, 102, 241), "📈");
        cardOccupancy = new CleanMetricCard("Occupancy Rate", "0.0%", "▲ Current Live Occupancy", new Color(168, 85, 247), "🛏️");
        cardExpenses = new CleanMetricCard("Operating Expenses", "0 MMK", "▼ Est. 28% Operating Cost", new Color(245, 158, 11), "🧾");

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
        main.add(Box.createRigidArea(new Dimension(0, 16)));
        main.add(statsRow);
        main.add(Box.createRigidArea(new Dimension(0, 18)));
        main.add(splitRow);

        return main;
    }

    private JPanel createLeftBreakdownCard() {
        JPanel card = createModernCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(360, 0));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("Revenue & Segment Distribution");
        title.setFont(new Font("Century Gothic", Font.BOLD, 15));
        title.setForeground(new Color(15, 23, 42));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        addSectionHeader(card, "BOOKING STAY MODELS");
        barStaycation = new JProgressBar(0, 100);
        lblStaycationVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Staycation (Overnight)", lblStaycationVal, barStaycation, new Color(99, 102, 241));

        barDaycation = new JProgressBar(0, 100);
        lblDaycationVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Daycation (Day Pass)", lblDaycationVal, barDaycation, new Color(168, 85, 247));

        barNightStay = new JProgressBar(0, 100);
        lblNightStayVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Night Stay (Transit)", lblNightStayVal, barNightStay, new Color(245, 158, 11));

        card.add(Box.createRigidArea(new Dimension(0, 12)));

        addSectionHeader(card, "DEPARTMENT REVENUE CONTRIBUTION");
        barRoom = new JProgressBar(0, 100);
        lblRoomVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Room Bookings", lblRoomVal, barRoom, new Color(16, 185, 129));

        barDining = new JProgressBar(0, 100);
        lblDiningVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "In-Room Dining", lblDiningVal, barDining, new Color(59, 130, 246));

        barSpa = new JProgressBar(0, 100);
        lblSpaVal = new JLabel("0 MMK (0%)");
        addProgressMeterComponent(card, "Spa & Hotel Amenities", lblSpaVal, barSpa, new Color(100, 116, 139));

        card.add(Box.createVerticalGlue());

        JPanel netMarginBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 252));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(226, 232, 240));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        netMarginBox.setOpaque(false);
        netMarginBox.setBorder(new EmptyBorder(12, 14, 12, 14));
        netMarginBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        netMarginBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMargin = new JLabel("Operating Profit Margin");
        lblMargin.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblMargin.setForeground(new Color(100, 116, 139));

        lblMarginVal = new JLabel("0.0%");
        lblMarginVal.setFont(new Font("Century Gothic", Font.BOLD, 16));
        lblMarginVal.setForeground(new Color(16, 185, 129));

        netMarginBox.add(lblMargin, BorderLayout.WEST);
        netMarginBox.add(lblMarginVal, BorderLayout.EAST);

        card.add(netMarginBox);
        return card;
    }

    private JPanel createRightAuditCard() {
        JPanel card = createModernCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel headerRow = new JPanel(new BorderLayout(14, 0));
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel tableTitle = new JLabel("Audit Ledger & Settled Bookings");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tableTitle.setForeground(new Color(15, 23, 42));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        txtSearch.setPreferredSize(new Dimension(210, 32));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 10, 4, 10)
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

        String[] cols = {"Date", "Invoice / Folio", "Guest Name", "Stay Tier", "Room Charges", "Dining Charges", "Total Net Paid", "Status"};
        tableModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        reportTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        reportTable.setRowSorter(rowSorter);

        reportTable.setRowHeight(38);
        reportTable.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        reportTable.setShowVerticalLines(false);
        reportTable.setGridColor(new Color(241, 245, 249));

        reportTable.getColumnModel().getColumn(0).setPreferredWidth(85);   // Date
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(95);   // Invoice
        reportTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Guest
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(95);   // Tier
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(105);  // Room Charges
        reportTable.getColumnModel().getColumn(5).setPreferredWidth(95);   // Dining
        reportTable.getColumnModel().getColumn(6).setPreferredWidth(110);  // Total Net
        reportTable.getColumnModel().getColumn(7).setPreferredWidth(80);   // Status

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

    public void loadReportData(String period) {
        ReportDBA.ExecutiveMetrics kpis = ReportDBA.getExecutiveMetrics(period);
        cardGrossSales.setValue(kpis.grossSales);
        cardAdr.setValue(kpis.adr);
        cardOccupancy.setValue(kpis.occupancyRate);
        cardExpenses.setValue(kpis.operatingExpenses);
        lblMarginVal.setText(kpis.netMargin);

        ReportDBA.SegmentDistribution dist = ReportDBA.getDistributionBreakdown(period);
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
        Vector<Vector<Object>> ledger = ReportDBA.getAuditLedger(period);
        for (Vector<Object> row : ledger) {
            tableModel.addRow(row);
        }
    }

    private void handlePrint() {
        try {
            boolean complete = reportTable.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat("Grand Horizon Suites — Executive Audit Report (" + activePeriod + ")"),
                    new java.text.MessageFormat("Page - {0}")
            );
            if (complete) {
                JOptionPane.showMessageDialog(this, "Report printed successfully.", "Print Job", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(this, "Printing failed: " + pe.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleExportCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("GrandHorizon_Report_" + activePeriod.replace(" ", "_") + ".csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(file)) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    pw.print("\"" + tableModel.getColumnName(i) + "\"");
                    if (i < tableModel.getColumnCount() - 1) pw.print(",");
                }
                pw.println();

                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        Object val = tableModel.getValueAt(r, c);
                        pw.print("\"" + (val != null ? val.toString() : "") + "\"");
                        if (c < tableModel.getColumnCount() - 1) pw.print(",");
                    }
                    pw.println();
                }
                JOptionPane.showMessageDialog(this, "Audit ledger exported successfully to:\n" + file.getAbsolutePath(), "Export Completed", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private JButton createToolbarActionBtn(String text, Color bg, Color fg, Color borderColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        return btn;
    }

    private JPanel createModernCardPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
    }

    private void addSectionHeader(JPanel parent, String heading) {
        JLabel lbl = new JLabel(heading);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createRigidArea(new Dimension(0, 6)));
    }

    private void addProgressMeterComponent(JPanel parent, String label, JLabel lblVal, JProgressBar bar, Color barColor) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel labelRow = new JPanel(new BorderLayout());
        labelRow.setOpaque(false);

        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        lblName.setForeground(new Color(51, 65, 85));

        lblVal.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblVal.setForeground(new Color(15, 23, 42));

        labelRow.add(lblName, BorderLayout.WEST);
        labelRow.add(lblVal, BorderLayout.EAST);

        bar.setPreferredSize(new Dimension(0, 6));
        bar.setForeground(barColor);
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorderPainted(false);

        row.add(labelRow, BorderLayout.NORTH);
        row.add(bar, BorderLayout.CENTER);

        parent.add(row);
        parent.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    static class CleanMetricCard extends JPanel {
        private final JLabel lblVal;

        public CleanMetricCard(String title, String value, String trend, Color trendColor, String icon) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(226, 232, 240), 1, true),
                    new EmptyBorder(14, 18, 14, 18)
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
            lblVal.setFont(new Font("Century Gothic", Font.BOLD, 19));
            lblVal.setForeground(new Color(15, 23, 42));
            lblVal.setBorder(new EmptyBorder(6, 0, 4, 0));

            JLabel lblTrend = new JLabel(trend);
            lblTrend.setFont(new Font("Century Gothic", Font.BOLD, 11));
            lblTrend.setForeground(trendColor);

            add(topRow, BorderLayout.NORTH);
            add(lblVal, BorderLayout.CENTER);
            add(lblTrend, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
            g2.dispose();
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

            int padX = 12;
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