package view;

import model.CategoryManagementDBA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Vector;

public class CategoryManagementUI extends JPanel {

    private DefaultTableModel modelRoomCat;
    private JTable tableRoomCat;
    private JTextField txtCatName, txtCatCapacity, txtCatBaseRate;
    private JButton btnSaveCat, btnClearCat;
    private Integer selectedCatId = null;

    private DefaultTableModel modelTier;
    private JTable tableTier;
    private JTextField txtTierName, txtTierHours, txtTierDesc;
    private JButton btnSaveTier, btnClearTier;
    private Integer selectedTierId = null;

    private DefaultTableModel modelReward;
    private JTable tableReward;
    private JTextField txtRewardTitle, txtRewardPoints, txtRewardDesc;
    private JCheckBox chkRewardActive;
    private JButton btnSaveReward, btnClearReward;
    private Integer selectedRewardId = null;

    public CategoryManagementUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createMainContent(), BorderLayout.CENTER);
        loadAllData();
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(16, 20, 16, 20));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Century Gothic", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Room Categories", createRoomCategoriesTab());
        tabbedPane.addTab("Booking Duration Tiers", createPricingTiersTab());
        tabbedPane.addTab("Loyalty & Rewards Catalog", createLoyaltyRewardsTab());

        main.add(tabbedPane, BorderLayout.CENTER);
        return main;
    }

    private JPanel createRoomCategoriesTab() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel formCard = createFormContainer("Configure Room Category");
        txtCatName = createStyledTextField();
        txtCatCapacity = createStyledTextField();
        txtCatBaseRate = createStyledTextField();

        addFormRow(formCard, "Category Name", txtCatName);
        addFormRow(formCard, "Capacity Description (e.g. 2 Guests)", txtCatCapacity);
        addFormRow(formCard, "Base Night Rate (MMK)", txtCatBaseRate);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnRow.setPreferredSize(new Dimension(320, 36));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnClearCat = createActionBtn("Clear", new Color(241, 245, 249), new Color(71, 85, 105));
        btnClearCat.addActionListener(e -> clearCatForm());
        btnSaveCat = createActionBtn("Save Category", new Color(99, 102, 241), Color.WHITE);
        btnSaveCat.addActionListener(e -> handleSaveCat());

        btnRow.add(btnClearCat);
        btnRow.add(btnSaveCat);

        formCard.add(Box.createRigidArea(new Dimension(0, 10)));
        formCard.add(btnRow);
        formCard.add(Box.createVerticalGlue());

        String[] cols = {"ID", "Category Name", "Max Capacity", "Base Rate", "Raw Rate"};
        modelRoomCat = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableRoomCat = createStyledTable(modelRoomCat);
        tableRoomCat.removeColumn(tableRoomCat.getColumnModel().getColumn(4));

        tableRoomCat.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableRoomCat.getSelectedRow() != -1) {
                int row = tableRoomCat.convertRowIndexToModel(tableRoomCat.getSelectedRow());
                selectedCatId = (Integer) modelRoomCat.getValueAt(row, 0);
                txtCatName.setText((String) modelRoomCat.getValueAt(row, 1));
                txtCatCapacity.setText((String) modelRoomCat.getValueAt(row, 2));
                txtCatBaseRate.setText(modelRoomCat.getValueAt(row, 4).toString());
                btnSaveCat.setText("Update Category");
                btnSaveCat.setBackground(new Color(16, 185, 129));
            }
        });

        panel.add(formCard, BorderLayout.WEST);
        panel.add(createTableContainer("Database Room Categories", tableRoomCat), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPricingTiersTab() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel formCard = createFormContainer("Configure Duration & Stay Tier");
        txtTierName = createStyledTextField();
        txtTierHours = createStyledTextField();
        txtTierDesc = createStyledTextField();

        addFormRow(formCard, "Tier Name (e.g. Staycation)", txtTierName);
        addFormRow(formCard, "Duration in Hours (e.g. 24)", txtTierHours);
        addFormRow(formCard, "Description / Details", txtTierDesc);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnRow.setPreferredSize(new Dimension(320, 36));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnClearTier = createActionBtn("Clear", new Color(241, 245, 249), new Color(71, 85, 105));
        btnClearTier.addActionListener(e -> clearTierForm());
        btnSaveTier = createActionBtn("Save Tier", new Color(99, 102, 241), Color.WHITE);
        btnSaveTier.addActionListener(e -> handleSaveTier());

        btnRow.add(btnClearTier);
        btnRow.add(btnSaveTier);

        formCard.add(Box.createRigidArea(new Dimension(0, 10)));
        formCard.add(btnRow);
        formCard.add(Box.createVerticalGlue());

        String[] cols = {"ID", "Tier Model", "Duration", "Description", "Raw Hours"};
        modelTier = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableTier = createStyledTable(modelTier);
        tableTier.removeColumn(tableTier.getColumnModel().getColumn(4));

        tableTier.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableTier.getSelectedRow() != -1) {
                int row = tableTier.convertRowIndexToModel(tableTier.getSelectedRow());
                selectedTierId = (Integer) modelTier.getValueAt(row, 0);
                txtTierName.setText((String) modelTier.getValueAt(row, 1));
                txtTierHours.setText(modelTier.getValueAt(row, 4).toString());
                txtTierDesc.setText((String) modelTier.getValueAt(row, 3));
                btnSaveTier.setText("Update Tier");
                btnSaveTier.setBackground(new Color(16, 185, 129));
            }
        });

        panel.add(formCard, BorderLayout.WEST);
        panel.add(createTableContainer("Configured Booking Tier Models", tableTier), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLoyaltyRewardsTab() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel formCard = createFormContainer("Configure Loyalty Voucher & Perk");
        txtRewardTitle = createStyledTextField();
        txtRewardPoints = createStyledTextField();
        txtRewardDesc = createStyledTextField();
        chkRewardActive = new JCheckBox("Active for Redemption", true);
        chkRewardActive.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        chkRewardActive.setOpaque(false);
        chkRewardActive.setAlignmentX(Component.LEFT_ALIGNMENT);

        addFormRow(formCard, "Reward Voucher Title", txtRewardTitle);
        addFormRow(formCard, "Points Cost", txtRewardPoints);
        addFormRow(formCard, "Terms / Description", txtRewardDesc);
        formCard.add(chkRewardActive);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnRow.setPreferredSize(new Dimension(320, 36));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnClearReward = createActionBtn("Clear", new Color(241, 245, 249), new Color(71, 85, 105));
        btnClearReward.addActionListener(e -> clearRewardForm());
        btnSaveReward = createActionBtn("Save Reward", new Color(99, 102, 241), Color.WHITE);
        btnSaveReward.addActionListener(e -> handleSaveReward());

        btnRow.add(btnClearReward);
        btnRow.add(btnSaveReward);

        formCard.add(Box.createRigidArea(new Dimension(0, 10)));
        formCard.add(btnRow);
        formCard.add(Box.createVerticalGlue());

        String[] cols = {"ID", "Reward Title", "Points Cost", "Description", "Status", "Raw Cost"};
        modelReward = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableReward = createStyledTable(modelReward);
        tableReward.removeColumn(tableReward.getColumnModel().getColumn(5));
        tableReward.getColumnModel().getColumn(4).setCellRenderer(new CategoryBadgeRenderer());

        tableReward.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableReward.getSelectedRow() != -1) {
                int row = tableReward.convertRowIndexToModel(tableReward.getSelectedRow());
                selectedRewardId = (Integer) modelReward.getValueAt(row, 0);
                txtRewardTitle.setText((String) modelReward.getValueAt(row, 1));
                txtRewardPoints.setText(modelReward.getValueAt(row, 5).toString());
                txtRewardDesc.setText((String) modelReward.getValueAt(row, 3));
                chkRewardActive.setSelected("ACTIVE".equalsIgnoreCase((String) modelReward.getValueAt(row, 4)));
                btnSaveReward.setText("Update Reward");
                btnSaveReward.setBackground(new Color(16, 185, 129));
            }
        });

        panel.add(formCard, BorderLayout.WEST);
        panel.add(createTableContainer("Guest Loyalty Catalog & Point Costs", tableReward), BorderLayout.CENTER);
        return panel;
    }

    private void loadAllData() {
        modelRoomCat.setRowCount(0);
        for (Vector<Object> r : CategoryManagementDBA.getAllRoomCategories()) modelRoomCat.addRow(r);

        modelTier.setRowCount(0);
        for (Vector<Object> r : CategoryManagementDBA.getAllPricingTiers()) modelTier.addRow(r);

        modelReward.setRowCount(0);
        for (Vector<Object> r : CategoryManagementDBA.getAllLoyaltyRewards()) modelReward.addRow(r);
    }

    private void handleSaveCat() {
        String name = txtCatName.getText().trim();
        String cap = txtCatCapacity.getText().trim();
        String rateText = txtCatBaseRate.getText().replaceAll("[^0-9]", "");

        if (name.isEmpty() || rateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Category Name and Base Rate.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal rate = new BigDecimal(rateText);
        boolean ok = CategoryManagementDBA.saveOrUpdateRoomCategory(selectedCatId, name, cap, rate);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Room category saved successfully!");
            clearCatForm();
            loadAllData();
        } else {
            JOptionPane.showMessageDialog(this, "Database error. Name may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearCatForm() {
        txtCatName.setText("");
        txtCatCapacity.setText("");
        txtCatBaseRate.setText("");
        selectedCatId = null;
        btnSaveCat.setText("Save Category");
        btnSaveCat.setBackground(new Color(99, 102, 241));
        tableRoomCat.clearSelection();
    }

    private void handleSaveTier() {
        String name = txtTierName.getText().trim();
        String hoursText = txtTierHours.getText().replaceAll("[^0-9]", "");
        String desc = txtTierDesc.getText().trim();

        if (name.isEmpty() || hoursText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Tier Name and Duration Hours.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int hours = Integer.parseInt(hoursText);
        boolean ok = CategoryManagementDBA.saveOrUpdatePricingTier(selectedTierId, name, hours, desc);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Pricing tier saved successfully!");
            clearTierForm();
            loadAllData();
        } else {
            JOptionPane.showMessageDialog(this, "Database error. Tier name may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearTierForm() {
        txtTierName.setText("");
        txtTierHours.setText("");
        txtTierDesc.setText("");
        selectedTierId = null;
        btnSaveTier.setText("Save Tier");
        btnSaveTier.setBackground(new Color(99, 102, 241));
        tableTier.clearSelection();
    }

    private void handleSaveReward() {
        String title = txtRewardTitle.getText().trim();
        String pointsText = txtRewardPoints.getText().replaceAll("[^0-9]", "");
        String desc = txtRewardDesc.getText().trim();
        boolean active = chkRewardActive.isSelected();

        if (title.isEmpty() || pointsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Reward Title and Points Cost.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int points = Integer.parseInt(pointsText);
        boolean ok = CategoryManagementDBA.saveOrUpdateLoyaltyReward(selectedRewardId, title, points, desc, active);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Loyalty reward perk saved successfully!");
            clearRewardForm();
            loadAllData();
        } else {
            JOptionPane.showMessageDialog(this, "Database error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearRewardForm() {
        txtRewardTitle.setText("");
        txtRewardPoints.setText("");
        txtRewardDesc.setText("");
        chkRewardActive.setSelected(true);
        selectedRewardId = null;
        btnSaveReward.setText("Save Reward");
        btnSaveReward.setBackground(new Color(99, 102, 241));
        tableReward.clearSelection();
    }

    private JPanel createFormContainer(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(360, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 15));
        lbl.setForeground(new Color(30, 41, 59));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        return card;
    }

    private JPanel createTableContainer(String title, JTable table) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 15));
        lbl.setForeground(new Color(30, 41, 59));
        lbl.setBorder(new EmptyBorder(0, 0, 12, 0));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(lbl, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void addFormRow(JPanel parent, String label, JComponent comp) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        group.setOpaque(false);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lbl.setForeground(new Color(100, 116, 139));

        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 32));

        group.add(lbl, BorderLayout.NORTH);
        group.add(comp, BorderLayout.CENTER);

        parent.add(group);
        parent.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JButton createActionBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 34));
        return btn;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        return table;
    }

    static class CategoryBadgeRenderer extends DefaultTableCellRenderer {
        public CategoryBadgeRenderer() {
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
            Color bg = "ACTIVE".equalsIgnoreCase(status) ? new Color(16, 185, 129) : new Color(239, 68, 68);

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