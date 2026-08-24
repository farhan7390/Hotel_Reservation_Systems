package view;

import model.DBConnection;

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
import java.sql.*;
import java.util.UUID;

public class UserManagementUI extends JPanel {

    private DefaultTableModel tableModel;
    private JTable userTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField txtFullName, txtUsername, txtEmail, txtPhone;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole, cmbStatus;
    private JButton btnSave, btnClear;

    private JLabel lblTotalStaff, lblAdminCount, lblActiveCount, lblRestrictedCount;
    private String selectedUserId = null;

    public UserManagementUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createUserContent(), BorderLayout.CENTER);
        loadUsersFromDatabase();
    }

    private JPanel createUserContent() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(1400, 120));
        statsRow.setPreferredSize(new Dimension(1400, 120));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        UserStatCard cardTotal = new UserStatCard("👥 Total Staff Accounts", "0 Users", "System-Wide", new Color(99, 102, 241), new Color(129, 140, 248));
        UserStatCard cardAdmin = new UserStatCard("🛡️ System Admins", "0 Accounts", "Full Access Rights", new Color(168, 85, 247), new Color(192, 132, 252));
        UserStatCard cardActive = new UserStatCard("🟢 Active Today", "0 Online", "Logged In Staff", new Color(16, 185, 129), new Color(52, 211, 153));
        UserStatCard cardRestricted = new UserStatCard("🔒 Restricted / Disabled", "0 Accounts", "Access Suspended", new Color(245, 158, 11), new Color(251, 191, 36));

        lblTotalStaff = cardTotal.getCountLabel();
        lblAdminCount = cardAdmin.getCountLabel();
        lblActiveCount = cardActive.getCountLabel();
        lblRestrictedCount = cardRestricted.getCountLabel();

        statsRow.add(cardTotal);
        statsRow.add(cardAdmin);
        statsRow.add(cardActive);
        statsRow.add(cardRestricted);

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

        JLabel formTitle = new JLabel("Create / Edit Staff Account");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

        txtFullName = createStyledTextField("");
        txtUsername = createStyledTextField("");
        txtEmail = createStyledTextField("");
        txtPhone = createStyledTextField("");

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        cmbRole = new JComboBox<>(new String[]{"ADMIN", "RECEPTIONIST", "HOUSEKEEPING", "BILLING_MANAGER", "STAFF"});
        styleComboBox(cmbRole);

        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "SUSPENDED"});
        styleComboBox(cmbStatus);

        addFormGroup(formCard, "Full Name", txtFullName);
        addFormGroup(formCard, "System Username", txtUsername);
        addFormGroup(formCard, "Work Email", txtEmail);
        addFormGroup(formCard, "Contact Number", txtPhone);
        addFormGroup(formCard, "Account Password", txtPassword);

        JPanel roleStatusRow = new JPanel(new GridLayout(1, 2, 10, 0));
        roleStatusRow.setOpaque(false);
        roleStatusRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleStatusRow.setMaximumSize(new Dimension(1400, 58));

        JPanel roleGroup = new JPanel(new BorderLayout(0, 4));
        roleGroup.setOpaque(false);
        JLabel lblR = new JLabel("Assign Role");
        lblR.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblR.setForeground(new Color(100, 116, 139));
        roleGroup.add(lblR, BorderLayout.NORTH);
        roleGroup.add(cmbRole, BorderLayout.CENTER);

        JPanel statusGroup = new JPanel(new BorderLayout(0, 4));
        statusGroup.setOpaque(false);
        JLabel lblS = new JLabel("Account Status");
        lblS.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblS.setForeground(new Color(100, 116, 139));
        statusGroup.add(lblS, BorderLayout.NORTH);
        statusGroup.add(cmbStatus, BorderLayout.CENTER);

        roleStatusRow.add(roleGroup);
        roleStatusRow.add(statusGroup);

        formCard.add(roleStatusRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

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

        btnSave = new JButton("Save Staff Account");
        btnSave.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnSave.setBackground(new Color(99, 102, 241));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> handleSaveUser());

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

        JLabel tableTitle = new JLabel("System Staff Directory & Access Level");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tableTitle.setForeground(new Color(30, 41, 59));

        JTextField searchBox = new JTextField();
        searchBox.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        searchBox.setPreferredSize(new Dimension(190, 28));
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

        String[] cols = {"User ID", "Full Name", "Username", "Email", "Role", "Last Login", "Status", "Phone"};
        tableModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        userTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(rowSorter);

        userTable.removeColumn(userTable.getColumnModel().getColumn(7));

        userTable.setRowHeight(38);
        userTable.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        userTable.setShowVerticalLines(false);
        userTable.setGridColor(new Color(241, 245, 249));
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = userTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        userTable.getColumnModel().getColumn(6).setCellRenderer(new UserStatusBadgeRenderer());

        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
                int modelRow = userTable.convertRowIndexToModel(userTable.getSelectedRow());
                populateFormFromSelectedRow(modelRow);
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
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

    private void loadUsersFromDatabase() {
        tableModel.setRowCount(0);
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Cannot connect to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT user_id, full_name, username, email, role, last_login, status, phone FROM Users ORDER BY created_at DESC";
        int total = 0, admins = 0, active = 0, restricted = 0;

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("user_id");
                String name = rs.getString("full_name");
                String uname = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");
                Timestamp lastLogin = rs.getTimestamp("last_login");
                String lastLoginStr = (lastLogin != null) ? lastLogin.toString().substring(0, 16) : "Never";
                String status = rs.getString("status");
                String phone = rs.getString("phone") != null ? rs.getString("phone") : "";

                tableModel.addRow(new Object[]{id, name, uname, email, role, lastLoginStr, status, phone});

                total++;
                if ("ADMIN".equalsIgnoreCase(role)) admins++;
                if ("ACTIVE".equalsIgnoreCase(status)) active++;
                if ("INACTIVE".equalsIgnoreCase(status) || "SUSPENDED".equalsIgnoreCase(status)) restricted++;
            }

            lblTotalStaff.setText(total + " Users");
            lblAdminCount.setText(admins + " Accounts");
            lblActiveCount.setText(active + " Online");
            lblRestrictedCount.setText(restricted + " Accounts");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load users: " + e.getMessage(), "Query Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void populateFormFromSelectedRow(int modelRow) {
        selectedUserId = (String) tableModel.getValueAt(modelRow, 0);
        txtFullName.setText((String) tableModel.getValueAt(modelRow, 1));
        txtUsername.setText((String) tableModel.getValueAt(modelRow, 2));
        txtEmail.setText((String) tableModel.getValueAt(modelRow, 3));
        cmbRole.setSelectedItem(tableModel.getValueAt(modelRow, 4));
        cmbStatus.setSelectedItem(tableModel.getValueAt(modelRow, 6));
        txtPhone.setText((String) tableModel.getValueAt(modelRow, 7));

        btnSave.setText("Update Account");
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

        input.setPreferredSize(new Dimension(input.getPreferredSize().width, 32));

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
        box.setMaximumSize(new Dimension(1400, 32));
    }

    private void clearForm() {
        txtFullName.setText("");
        txtUsername.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        selectedUserId = null;

        btnSave.setText("Save Staff Account");
        btnSave.setBackground(new Color(99, 102, 241));
        userTable.clearSelection();
    }

    private void handleSaveUser() {
        String name = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cmbRole.getSelectedItem();
        String status = (String) cmbStatus.getSelectedItem();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Full Name, Username, and Email.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection not available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isUpdate = (selectedUserId != null);

        if (!isUpdate && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new accounts.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (isUpdate) {
                boolean changePassword = !password.isEmpty();
                String updateSql = changePassword
                        ? "UPDATE Users SET full_name = ?, username = ?, email = ?, phone = ?, password_hash = ?, role = ?, status = ? WHERE user_id = ?"
                        : "UPDATE Users SET full_name = ?, username = ?, email = ?, phone = ?, role = ?, status = ? WHERE user_id = ?";

                try (PreparedStatement pst = conn.prepareStatement(updateSql)) {
                    pst.setString(1, name);
                    pst.setString(2, username);
                    pst.setString(3, email);
                    pst.setString(4, phone);
                    if (changePassword) {
                        pst.setString(5, password);
                        pst.setString(6, role);
                        pst.setString(7, status);
                        pst.setString(8, selectedUserId);
                    } else {
                        pst.setString(5, role);
                        pst.setString(6, status);
                        pst.setString(7, selectedUserId);
                    }
                    pst.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Staff user '" + username + "' updated successfully!");
            } else {
                String newUserId = "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String insertSql = "INSERT INTO Users (user_id, full_name, username, email, password_hash, phone, role, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
                    pst.setString(1, newUserId);
                    pst.setString(2, name);
                    pst.setString(3, username);
                    pst.setString(4, email);
                    pst.setString(5, password);
                    pst.setString(6, phone);
                    pst.setString(7, role);
                    pst.setString(8, status);
                    pst.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Staff account created successfully for '" + username + "'!");
            }

            loadUsersFromDatabase();
            clearForm();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class UserStatCard extends JPanel {
        private final Color c1, c2;
        private final JLabel lblCount;

        public UserStatCard(String title, String count, String subtext, Color c1, Color c2) {
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

    static class UserStatusBadgeRenderer extends DefaultTableCellRenderer {
        public UserStatusBadgeRenderer() {
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
                case "INACTIVE":
                    bg = new Color(100, 116, 139);
                    break;
                case "SUSPENDED":
                default:
                    bg = new Color(239, 68, 68);
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