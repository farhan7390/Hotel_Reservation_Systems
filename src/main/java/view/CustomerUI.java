package view;

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

public class CustomerUI extends JPanel {

    private DefaultTableModel tableModel;
    private JTable customerTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField txtGuestName, txtNIDPassport, txtPhone, txtEmail, txtCity;
    private JComboBox<String> cmbVipTier, cmbGuestStatus;
    private JTextArea txtSpecialPreferences;
    private JButton btnSave, btnClear;

    private int selectedRowIndex = -1;

    public CustomerUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createCustomerContent(), BorderLayout.CENTER);
    }

    private JPanel createCustomerContent() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(1400, 120));
        statsRow.setPreferredSize(new Dimension(1400, 120));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsRow.add(new CustomerStatCard("👥 Total Registered Guests", "1,240 Guests", "Database Records", new Color(99, 102, 241), new Color(129, 140, 248)));
        statsRow.add(new CustomerStatCard("⭐ VIP Members", "185 Members", "Gold & Platinum Tiers", new Color(168, 85, 247), new Color(192, 132, 252)));
        statsRow.add(new CustomerStatCard("🏨 In-House Guests", "42 Staying", "Currently Checked-In", new Color(16, 185, 129), new Color(52, 211, 153)));
        statsRow.add(new CustomerStatCard("🔄 Repeat Rate", "46.2%", "Loyalty Retention", new Color(245, 158, 11), new Color(251, 191, 36)));

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

        JLabel formTitle = new JLabel("Guest Profile & Identity Record");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

        txtGuestName = createStyledTextField("");
        txtNIDPassport = createStyledTextField("");
        txtPhone = createStyledTextField("");
        txtEmail = createStyledTextField("");
        txtCity = createStyledTextField("Yangon");

        cmbVipTier = new JComboBox<>(new String[]{"STANDARD", "SILVER VIP", "GOLD VIP", "PLATINUM VIP"});
        styleComboBox(cmbVipTier);

        cmbGuestStatus = new JComboBox<>(new String[]{"ACTIVE", "CHECKED-IN", "INACTIVE"});
        styleComboBox(cmbGuestStatus);

        txtSpecialPreferences = new JTextArea(2, 20);
        txtSpecialPreferences.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        txtSpecialPreferences.setLineWrap(true);
        txtSpecialPreferences.setWrapStyleWord(true);
        txtSpecialPreferences.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));

        addFormGroup(formCard, "Guest Full Name", txtGuestName);
        addFormGroup(formCard, "NRC / Passport / NID", txtNIDPassport);
        addFormGroup(formCard, "Phone Number", txtPhone);
        addFormGroup(formCard, "Email Address", txtEmail);

        JPanel cityVipRow = new JPanel(new GridLayout(1, 2, 10, 0));
        cityVipRow.setOpaque(false);
        cityVipRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cityVipRow.setMaximumSize(new Dimension(1400, 58));

        JPanel cityGroup = new JPanel(new BorderLayout(0, 4));
        cityGroup.setOpaque(false);
        JLabel lblC = new JLabel("City / Origin");
        lblC.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblC.setForeground(new Color(100, 116, 139));
        cityGroup.add(lblC, BorderLayout.NORTH);
        cityGroup.add(txtCity, BorderLayout.CENTER);

        JPanel vipGroup = new JPanel(new BorderLayout(0, 4));
        vipGroup.setOpaque(false);
        JLabel lblV = new JLabel("VIP Tier");
        lblV.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblV.setForeground(new Color(100, 116, 139));
        vipGroup.add(lblV, BorderLayout.NORTH);
        vipGroup.add(cmbVipTier, BorderLayout.CENTER);

        cityVipRow.add(cityGroup);
        cityVipRow.add(vipGroup);

        formCard.add(cityVipRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));

        addFormGroup(formCard, "Current Guest Status", cmbGuestStatus);
        addFormGroup(formCard, "Preferences / Allergy Notes", txtSpecialPreferences);

        JPanel actionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        actionBtns.setOpaque(false);
        actionBtns.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionBtns.setMaximumSize(new Dimension(1400, 36));

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(71, 85, 105));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> clearForm());

        btnSave = new JButton("Save Customer");
        btnSave.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnSave.setBackground(new Color(99, 102, 241));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> handleSaveCustomer());

        actionBtns.add(btnClear);
        actionBtns.add(btnSave);

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

        JLabel tableTitle = new JLabel("Master Guest Directory & Loyalty Record");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tableTitle.setForeground(new Color(30, 41, 59));

        JTextField searchBox = new JTextField();
        searchBox.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        searchBox.setPreferredSize(new Dimension(200, 28));
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

        String[] cols = {"Guest ID", "Full Name", "Contact / Phone", "NRC / Passport", "VIP Tier", "Total Visits", "Status"};
        Object[][] data = {
                {"GST-1001", "Sarah Jenkins", "+95 9 785 221 445", "12/AHLN(N)102931", "PLATINUM VIP", "14 Stays", "CHECKED-IN"},
                {"GST-1002", "Liam Anderson", "+95 9 450 112 889", "E-Passport (UK)", "GOLD VIP", "8 Stays", "CHECKED-IN"},
                {"GST-1003", "Marcus Vance", "+95 9 250 889 123", "14/YAKANA(N)054122", "SILVER VIP", "3 Stays", "CHECKED-IN"},
                {"GST-1004", "Elena Rostova", "+95 9 965 332 110", "E-Passport (RUS)", "STANDARD", "1 Stay", "ACTIVE"},
                {"GST-1005", "David Kim", "+95 9 421 990 778", "E-Passport (KOR)", "GOLD VIP", "6 Stays", "ACTIVE"},
                {"GST-1006", "Chloe Bennett", "+95 9 770 123 456", "12/LATHA(N)099120", "STANDARD", "2 Stays", "INACTIVE"}
        };

        tableModel = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        customerTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(rowSorter);

        customerTable.setRowHeight(38);
        customerTable.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        customerTable.setShowVerticalLines(false);
        customerTable.setGridColor(new Color(241, 245, 249));
        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = customerTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        customerTable.getColumnModel().getColumn(6).setCellRenderer(new CustomerStatusBadgeRenderer());

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customerTable.getSelectedRow() != -1) {
                int modelRow = customerTable.convertRowIndexToModel(customerTable.getSelectedRow());
                populateFormFromSelectedRow(modelRow);
            }
        });

        JScrollPane scrollPane = new JScrollPane(customerTable);
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

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void populateFormFromSelectedRow(int modelRow) {
        selectedRowIndex = modelRow;
        txtGuestName.setText((String) tableModel.getValueAt(modelRow, 1));
        txtPhone.setText((String) tableModel.getValueAt(modelRow, 2));
        txtNIDPassport.setText((String) tableModel.getValueAt(modelRow, 3));
        cmbVipTier.setSelectedItem(tableModel.getValueAt(modelRow, 4));
        cmbGuestStatus.setSelectedItem(tableModel.getValueAt(modelRow, 6));

        btnSave.setText("Update Profile");
        btnSave.setBackground(new Color(16, 185, 129));
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

        input.setPreferredSize(new Dimension(input.getPreferredSize().width, 30));

        group.add(lbl, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);

        parent.add(group);
        parent.add(Box.createRigidArea(new Dimension(0, 8)));
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
        box.setMaximumSize(new Dimension(1400, 30));
    }

    private void clearForm() {
        txtGuestName.setText("");
        txtNIDPassport.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtCity.setText("Yangon");
        txtSpecialPreferences.setText("");
        cmbVipTier.setSelectedIndex(0);
        cmbGuestStatus.setSelectedIndex(0);
        selectedRowIndex = -1;

        btnSave.setText("Save Customer");
        btnSave.setBackground(new Color(99, 102, 241));
        customerTable.clearSelection();
    }

    private void handleSaveCustomer() {
        String name = txtGuestName.getText().trim();
        String nid = txtNIDPassport.getText().trim();
        String phone = txtPhone.getText().trim();
        String tier = (String) cmbVipTier.getSelectedItem();
        String status = (String) cmbGuestStatus.getSelectedItem();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Guest Name and Phone Number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRowIndex != -1) {
            tableModel.setValueAt(name, selectedRowIndex, 1);
            tableModel.setValueAt(phone, selectedRowIndex, 2);
            tableModel.setValueAt(nid, selectedRowIndex, 3);
            tableModel.setValueAt(tier, selectedRowIndex, 4);
            tableModel.setValueAt(status, selectedRowIndex, 6);
            JOptionPane.showMessageDialog(this, "Guest profile updated successfully!");
        } else {
            String newId = "GST-" + (1000 + tableModel.getRowCount() + 1);
            tableModel.addRow(new Object[]{newId, name, phone, nid, tier, "1 Stay", status});
            JOptionPane.showMessageDialog(this, "Guest profile for " + name + " saved successfully!");
        }

        clearForm();
    }

    static class CustomerStatCard extends JPanel {
        private final Color c1, c2;

        public CustomerStatCard(String title, String count, String subtext, Color c1, Color c2) {
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

            JLabel lblCount = new JLabel(count);
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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        }
    }

    static class CustomerStatusBadgeRenderer extends DefaultTableCellRenderer {
        public CustomerStatusBadgeRenderer() {
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
                case "CHECKED-IN":
                    bg = new Color(16, 185, 129);
                    break;
                case "ACTIVE":
                    bg = new Color(99, 102, 241);
                    break;
                case "INACTIVE":
                default:
                    bg = new Color(100, 116, 139);
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