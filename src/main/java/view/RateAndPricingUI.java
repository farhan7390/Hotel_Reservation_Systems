package view;

import model.RateAndPricingDBA;

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
import java.math.BigDecimal;
import java.util.Vector;

public class RateAndPricingUI extends JPanel {

    private DefaultTableModel tableModel;
    private JTable rateTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JComboBox<String> cmbRoomCategory, cmbPricingTier, cmbSeasonMultiplier;
    private JTextField txtBaseRate, txtWeekendSurcharge, txtExtraBedFee;
    private JCheckBox chkIncludeBreakfast, chkTaxInclusive;
    private JButton btnApply, btnReset;

    private JLabel lblStaycationAvg, lblDaycationAvg, lblNightStayAvg, lblSurgeRate;
    private Integer selectedRuleId = null;

    public RateAndPricingUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createPricingContent(), BorderLayout.CENTER);
        loadInitialData();
    }

    private JPanel createPricingContent() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(1400, 120));
        statsRow.setPreferredSize(new Dimension(1400, 120));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        PricingStatCard cardStaycation = new PricingStatCard("🏷️ Staycation (Avg)", "0 MMK", "Overnight Stays", new Color(99, 102, 241), new Color(129, 140, 248));
        PricingStatCard cardDaycation = new PricingStatCard("☀️ Daycation (Avg)", "0 MMK", "Day Pass Stays", new Color(168, 85, 247), new Color(192, 132, 252));
        PricingStatCard cardNightStay = new PricingStatCard("🌙 Night Stay (Avg)", "0 MMK", "Transit Stays", new Color(245, 158, 11), new Color(251, 191, 36));
        PricingStatCard cardSurge = new PricingStatCard("⚡ Active Surge Rate", "+0% Weekend", "High Season Modifier", new Color(16, 185, 129), new Color(52, 211, 153));

        lblStaycationAvg = cardStaycation.getCountLabel();
        lblDaycationAvg = cardDaycation.getCountLabel();
        lblNightStayAvg = cardNightStay.getCountLabel();
        lblSurgeRate = cardSurge.getCountLabel();

        statsRow.add(cardStaycation);
        statsRow.add(cardDaycation);
        statsRow.add(cardNightStay);
        statsRow.add(cardSurge);

        JPanel workspaceRow = new JPanel(new BorderLayout(18, 0));
        workspaceRow.setOpaque(false);
        workspaceRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel formTitle = new JLabel("Configure Room Rate & Tariff");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

        cmbRoomCategory = new JComboBox<>();
        styleComboBox(cmbRoomCategory);

        cmbPricingTier = new JComboBox<>();
        styleComboBox(cmbPricingTier);

        txtBaseRate = createStyledTextField("180,000 MMK");
        txtWeekendSurcharge = createStyledTextField("20,000 MMK");
        txtExtraBedFee = createStyledTextField("25,000 MMK");

        cmbSeasonMultiplier = new JComboBox<>(new String[]{
                "Standard Season (1.0x)",
                "Peak Holiday Season (1.25x)",
                "Monsoon Promo (0.85x)",
                "Corporate Bulk (0.90x)"
        });
        styleComboBox(cmbSeasonMultiplier);

        chkIncludeBreakfast = new JCheckBox("Complimentary Breakfast Buffet", true);
        chkTaxInclusive = new JCheckBox("Commercial Tax & Service (5%) Included", true);
        chkIncludeBreakfast.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        chkTaxInclusive.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        chkIncludeBreakfast.setOpaque(false);
        chkTaxInclusive.setOpaque(false);

        addFormGroup(formCard, "Room Category", cmbRoomCategory);
        addFormGroup(formCard, "Booking Tier Model", cmbPricingTier);

        JPanel feeRow = new JPanel(new GridLayout(1, 2, 10, 0));
        feeRow.setOpaque(false);
        feeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        feeRow.setMaximumSize(new Dimension(1400, 58));

        JPanel baseGroup = new JPanel(new BorderLayout(0, 4));
        baseGroup.setOpaque(false);
        JLabel lblB = new JLabel("Base Tariff");
        lblB.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblB.setForeground(new Color(100, 116, 139));
        baseGroup.add(lblB, BorderLayout.NORTH);
        baseGroup.add(txtBaseRate, BorderLayout.CENTER);

        JPanel surGroup = new JPanel(new BorderLayout(0, 4));
        surGroup.setOpaque(false);
        JLabel lblS = new JLabel("Weekend Surcharge");
        lblS.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblS.setForeground(new Color(100, 116, 139));
        surGroup.add(lblS, BorderLayout.NORTH);
        surGroup.add(txtWeekendSurcharge, BorderLayout.CENTER);

        feeRow.add(baseGroup);
        feeRow.add(surGroup);

        formCard.add(feeRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));

        addFormGroup(formCard, "Extra Bed / Person Charge", txtExtraBedFee);
        addFormGroup(formCard, "Seasonal Demand Multiplier", cmbSeasonMultiplier);

        formCard.add(chkIncludeBreakfast);
        formCard.add(chkTaxInclusive);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel actionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        actionBtns.setOpaque(false);
        actionBtns.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionBtns.setMaximumSize(new Dimension(1400, 36));

        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnReset.setBackground(new Color(241, 245, 249));
        btnReset.setForeground(new Color(71, 85, 105));
        btnReset.setFocusPainted(false);
        btnReset.setBorderPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> resetForm());

        btnApply = new JButton("Apply Tariff Rule");
        btnApply.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnApply.setBackground(new Color(99, 102, 241));
        btnApply.setForeground(Color.WHITE);
        btnApply.setFocusPainted(false);
        btnApply.setBorderPainted(false);
        btnApply.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnApply.addActionListener(e -> handleSaveRateRule());

        actionBtns.add(btnReset);
        actionBtns.add(btnApply);

        formCard.add(actionBtns);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel tableTitle = new JLabel("Live Tariff Rules & Seasonal Rate Schedule (Database)");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tableTitle.setForeground(new Color(30, 41, 59));

        JTextField searchBox = new JTextField();
        searchBox.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        searchBox.setPreferredSize(new Dimension(180, 28));
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        searchBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(searchBox.getText().trim()); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(searchBox.getText().trim()); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(searchBox.getText().trim()); }
        });

        headerRow.add(tableTitle, BorderLayout.WEST);
        headerRow.add(searchBox, BorderLayout.EAST);

        String[] cols = {"Rule ID", "Category", "Tier Model", "Base Rate", "Weekend Surcharge", "Extra Bed", "Season Mod", "Status", "Multiplier"};
        tableModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        rateTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        rateTable.setRowSorter(rowSorter);

        rateTable.removeColumn(rateTable.getColumnModel().getColumn(8));
        rateTable.removeColumn(rateTable.getColumnModel().getColumn(0));

        rateTable.setRowHeight(38);
        rateTable.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        rateTable.setShowVerticalLines(false);
        rateTable.setGridColor(new Color(241, 245, 249));
        rateTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = rateTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        rateTable.getColumnModel().getColumn(5).setCellRenderer(new RateStatusBadgeRenderer());

        rateTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && rateTable.getSelectedRow() != -1) {
                int modelRow = rateTable.convertRowIndexToModel(rateTable.getSelectedRow());
                populateFormFromSelectedRow(modelRow);
            }
        });

        JScrollPane scrollPane = new JScrollPane(rateTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(headerRow, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        workspaceRow.add(formCard, BorderLayout.WEST);
        workspaceRow.add(tableCard, BorderLayout.CENTER);

        main.add(statsRow);
        main.add(Box.createRigidArea(new Dimension(0, 18)));
        main.add(workspaceRow);

        return main;
    }

    private void loadInitialData() {
        cmbRoomCategory.removeAllItems();
        Vector<String> categories = RateAndPricingDBA.getRoomCategories();
        for (String c : categories) cmbRoomCategory.addItem(c);

        cmbPricingTier.removeAllItems();
        Vector<String> tiers = RateAndPricingDBA.getPricingTiers();
        for (String t : tiers) cmbPricingTier.addItem(t);

        loadTableData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        Vector<Vector<Object>> data = RateAndPricingDBA.getAllTariffRules();
        for (Vector<Object> row : data) {
            tableModel.addRow(row);
        }

        RateAndPricingDBA.PricingKPIs kpis = RateAndPricingDBA.getPricingMetrics();
        lblStaycationAvg.setText(kpis.staycationAvg);
        lblDaycationAvg.setText(kpis.daycationAvg);
        lblNightStayAvg.setText(kpis.nightStayAvg);
        lblSurgeRate.setText(kpis.activeSurge);
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void populateFormFromSelectedRow(int modelRow) {
        selectedRuleId = (Integer) tableModel.getValueAt(modelRow, 0);
        cmbRoomCategory.setSelectedItem(tableModel.getValueAt(modelRow, 1));
        cmbPricingTier.setSelectedItem(tableModel.getValueAt(modelRow, 2));

        txtBaseRate.setText((String) tableModel.getValueAt(modelRow, 3));
        txtWeekendSurcharge.setText((String) tableModel.getValueAt(modelRow, 4));
        txtExtraBedFee.setText((String) tableModel.getValueAt(modelRow, 5));

        String season = (String) tableModel.getValueAt(modelRow, 6);
        for (int i = 0; i < cmbSeasonMultiplier.getItemCount(); i++) {
            if (cmbSeasonMultiplier.getItemAt(i).equalsIgnoreCase(season)) {
                cmbSeasonMultiplier.setSelectedIndex(i);
                break;
            }
        }

        btnApply.setText("Update Rule");
        btnApply.setBackground(new Color(16, 185, 129));
    }

    private void addFormGroup(JPanel parent, String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(1400, 58));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);

        input.setPreferredSize(new Dimension(input.getPreferredSize().width, 32));

        group.add(lbl, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);

        parent.add(group);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JTextField createStyledTextField(String initialValue) {
        JTextField tf = new JTextField(initialValue);
        tf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        box.setBackground(Color.WHITE);
        box.setMaximumSize(new Dimension(1400, 32));
    }

    private void resetForm() {
        if (cmbRoomCategory.getItemCount() > 0) cmbRoomCategory.setSelectedIndex(0);
        if (cmbPricingTier.getItemCount() > 0) cmbPricingTier.setSelectedIndex(0);
        txtBaseRate.setText("180,000 MMK");
        txtWeekendSurcharge.setText("20,000 MMK");
        txtExtraBedFee.setText("25,000 MMK");
        cmbSeasonMultiplier.setSelectedIndex(0);
        selectedRuleId = null;

        btnApply.setText("Apply Tariff Rule");
        btnApply.setBackground(new Color(99, 102, 241));
        rateTable.clearSelection();
    }

    private void handleSaveRateRule() {
        String category = (String) cmbRoomCategory.getSelectedItem();
        String tier = (String) cmbPricingTier.getSelectedItem();
        String seasonName = (String) cmbSeasonMultiplier.getSelectedItem();

        if (category == null || tier == null || seasonName == null) {
            JOptionPane.showMessageDialog(this, "Please select category, tier, and season modifier.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal base = new BigDecimal(txtBaseRate.getText().replaceAll("[^0-9]", ""));
            BigDecimal weekend = new BigDecimal(txtWeekendSurcharge.getText().replaceAll("[^0-9]", ""));
            BigDecimal extra = new BigDecimal(txtExtraBedFee.getText().replaceAll("[^0-9]", ""));

            BigDecimal multiplier = new BigDecimal("1.00");
            if (seasonName.contains("1.25x")) multiplier = new BigDecimal("1.25");
            else if (seasonName.contains("0.85x")) multiplier = new BigDecimal("0.85");
            else if (seasonName.contains("0.90x")) multiplier = new BigDecimal("0.90");

            boolean success = RateAndPricingDBA.saveOrUpdateTariffRule(category, tier, base, weekend, extra, seasonName, multiplier);

            if (success) {
                JOptionPane.showMessageDialog(this, "Tariff rule for " + category + " (" + tier + ") applied successfully!");
                loadTableData();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to apply tariff rule in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid number entered for rates or surcharges.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class PricingStatCard extends JPanel {
        private final Color c1, c2;
        private final JLabel lblCount;

        public PricingStatCard(String title, String count, String subtext, Color c1, Color c2) {
            this.c1 = c1;
            this.c2 = c2;
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(14, 16, 14, 16));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
            lblTitle.setForeground(new Color(255, 255, 255, 230));

            lblCount = new JLabel(count);
            lblCount.setFont(new Font("Century Gothic", Font.BOLD, 20));
            lblCount.setForeground(Color.WHITE);

            JLabel lblSub = new JLabel(subtext);
            lblSub.setFont(new Font("Century Gothic", Font.PLAIN, 11));
            lblSub.setForeground(new Color(255, 255, 255, 190));

            textPanel.add(lblTitle);
            textPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            textPanel.add(lblCount);
            textPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            textPanel.add(lblSub);

            add(textPanel, BorderLayout.CENTER);
        }

        public JLabel getCountLabel() {
            return lblCount;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        }
    }

    static class RateStatusBadgeRenderer extends DefaultTableCellRenderer {
        public RateStatusBadgeRenderer() {
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
            Color bg;
            switch (status.toUpperCase()) {
                case "ACTIVE":
                    bg = new Color(16, 185, 129);
                    break;
                case "SURGE":
                    bg = new Color(239, 68, 68);
                    break;
                case "DISCOUNTED":
                    bg = new Color(245, 158, 11);
                    break;
                default:
                    bg = new Color(99, 102, 241);
                    break;
            }

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