package view;

import model.DashboardDBA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Vector;

public class DashboardUI extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;

    private GradientStatCard cardOccupancy;
    private GradientStatCard cardCheckIn;
    private GradientStatCard cardCheckOut;
    private GradientStatCard cardRevenue;
    private GradientStatCard cardArrivals;

    public DashboardUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createMainContent(), BorderLayout.CENTER);
        loadDashboardData();
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel cardsRow = new JPanel(new GridLayout(1, 5, 16, 0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(1400, 135));
        cardsRow.setPreferredSize(new Dimension(1400, 135));
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardOccupancy = new GradientStatCard("🛏️", "Total Occupancy", "0 / 0 Rooms", "Realtime status", new Color(99, 102, 241), new Color(129, 140, 248));
        cardCheckIn = new GradientStatCard("📥", "Today Check In", "0", "Guests in-house", new Color(104, 45, 241), new Color(89, 86, 156));
        cardCheckOut = new GradientStatCard("📤", "Today Check Out", "0", "Completed stays", new Color(89, 14, 84), new Color(179, 46, 205));
        cardRevenue = new GradientStatCard("💰", "Total Revenue", "0 MMK", "Settled Invoices", new Color(168, 85, 247), new Color(192, 132, 252));
        cardArrivals = new GradientStatCard("🛎️", "Expected Arrivals", "0 Guests", "Pending today", new Color(131, 47, 15), new Color(182, 92, 47));

        cardsRow.add(cardOccupancy);
        cardsRow.add(cardCheckIn);
        cardsRow.add(cardCheckOut);
        cardsRow.add(cardRevenue);
        cardsRow.add(cardArrivals);

        mainPanel.add(cardsRow);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel tableTitle = new JLabel("Recent Reservations & Guest Movements");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tableTitle.setForeground(new Color(30, 41, 59));

        JButton btnRefresh = new JButton("🔄 Refresh Data");
        btnRefresh.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btnRefresh.setBackground(new Color(241, 245, 249));
        btnRefresh.setForeground(new Color(51, 65, 85));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 12, 5, 12)
        ));
        btnRefresh.addActionListener(e -> loadDashboardData());

        headerPanel.add(tableTitle, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        String[] columns = {"Booking Ref", "Guest ID", "Guest Name", "Room No.", "Tier", "Room Category", "Check In", "Check Out", "Status"};
        tableModel = new DefaultTableModel(new Object[][]{}, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Century Gothic", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 116, 139));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        table.getColumnModel().getColumn(8).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableContainer.add(headerPanel, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tableContainer);

        return mainPanel;
    }

    public void loadDashboardData() {
        DashboardDBA.DashboardMetrics metrics = DashboardDBA.getLiveMetrics();
        cardOccupancy.setValue(metrics.occupancy);
        cardCheckIn.setValue(metrics.checkInsToday);
        cardCheckOut.setValue(metrics.checkOutsToday);
        cardRevenue.setValue(metrics.totalRevenue);
        cardArrivals.setValue(metrics.expectedArrivals);

        tableModel.setRowCount(0);
        Vector<Vector<Object>> reservations = DashboardDBA.getRecentReservations();
        for (Vector<Object> row : reservations) {
            tableModel.addRow(row);
        }
    }

    static class GradientStatCard extends JPanel {
        private final Color c1, c2;
        private final JLabel valLabel;
        private final JLabel subLabel;

        public GradientStatCard(String icon, String title, String value, String subtext, Color c1, Color c2) {
            this.c1 = c1;
            this.c2 = c2;
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(16, 20, 16, 20));

            JPanel textBlock = new JPanel();
            textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
            textBlock.setOpaque(false);

            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            iconLabel.setForeground(Color.WHITE);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Century Gothic", Font.BOLD, 12));
            titleLabel.setForeground(new Color(255, 255, 255, 220));

            valLabel = new JLabel(value);
            valLabel.setFont(new Font("Century Gothic", Font.BOLD, 20));
            valLabel.setForeground(Color.WHITE);

            subLabel = new JLabel(subtext);
            subLabel.setFont(new Font("Century Gothic", Font.PLAIN, 11));
            subLabel.setForeground(new Color(255, 255, 255, 190));

            textBlock.add(iconLabel);
            textBlock.add(Box.createRigidArea(new Dimension(0, 6)));
            textBlock.add(titleLabel);
            textBlock.add(Box.createRigidArea(new Dimension(0, 4)));
            textBlock.add(valLabel);
            textBlock.add(Box.createRigidArea(new Dimension(0, 6)));
            textBlock.add(subLabel);

            add(textBlock, BorderLayout.WEST);
        }

        public void setValue(String value) {
            valLabel.setText(value);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

            g2.setColor(new Color(255, 255, 255, 25));
            int circleD = getHeight() + 40;
            g2.fillOval(getWidth() - 80, -20, circleD, circleD);
            g2.setColor(new Color(255, 255, 255, 15));
            g2.fillOval(getWidth() - 120, -10, circleD, circleD);
        }
    }

    static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        public StatusBadgeRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Century Gothic", Font.BOLD, 10));
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
            Color badgeBg;
            switch (status.toUpperCase()) {
                case "CONFIRMED":
                case "APPROVED":
                    badgeBg = new Color(99, 102, 241); // Indigo
                    break;
                case "CHECKED-IN":
                    badgeBg = new Color(34, 197, 94);  // Green
                    break;
                case "COMPLETED":
                    badgeBg = new Color(100, 116, 139); // Slate Gray
                    break;
                case "CANCELLED":
                    badgeBg = new Color(239, 68, 68);   // Red
                    break;
                default:
                    badgeBg = new Color(245, 158, 11);  // Amber
                    break;
            }

            int padX = 14;
            int badgeW = getWidth() - (padX * 2);
            int badgeH = getHeight() - 10;
            int badgeY = 5;

            g2.setColor(badgeBg);
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