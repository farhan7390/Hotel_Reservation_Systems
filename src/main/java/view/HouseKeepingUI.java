package view;

import model.HouseKeepingDBA;

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

public class HouseKeepingUI extends JPanel {
    private DefaultTableModel tableModel;
    private JTable taskTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JComboBox<String> cmbRoomNo, cmbCleaningStatus;
    private JComboBox<HouseKeepingDBA.StaffMember> cmbStaff;
    private JTextField txtRemarks;
    private JButton btnUpdate, btnClear;

    private JLabel lblCleanReady, lblDirtyVacant, lblInProgress, lblMaintenance;
    private Integer selectedRequestId = null;

    public HouseKeepingUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createMainContent(), BorderLayout.CENTER);
        loadInitialData();
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(1400, 120));
        statsRow.setPreferredSize(new Dimension(1400, 120));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        HouseKeepingStatCard cardClean = new HouseKeepingStatCard("✨ Clean & Ready", "0 Rooms", "Ready for Check-in", new Color(16, 185, 129), new Color(52, 211, 153));
        HouseKeepingStatCard cardDirty = new HouseKeepingStatCard("🧹 Pending Tasks", "0 Rooms", "Requires Housekeeping", new Color(239, 68, 68), new Color(248, 113, 113));
        HouseKeepingStatCard cardProgress = new HouseKeepingStatCard("⏳ In Progress", "0 Tasks", "Cleaning Underway", new Color(245, 158, 11), new Color(251, 191, 36));
        HouseKeepingStatCard cardMaint = new HouseKeepingStatCard("🛠️ Maintenance", "0 Rooms", "Out of Service", new Color(100, 116, 139), new Color(148, 163, 184));

        lblCleanReady = cardClean.getCountLabel();
        lblDirtyVacant = cardDirty.getCountLabel();
        lblInProgress = cardProgress.getCountLabel();
        lblMaintenance = cardMaint.getCountLabel();

        statsRow.add(cardClean);
        statsRow.add(cardDirty);
        statsRow.add(cardProgress);
        statsRow.add(cardMaint);

        JPanel opsPanel = new JPanel(new BorderLayout(18, 0));
        opsPanel.setOpaque(false);
        opsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel formTitle = new JLabel("Housekeeping Task & Dispatch");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        cmbRoomNo = new JComboBox<>();
        styleComboBox(cmbRoomNo);

        cmbCleaningStatus = new JComboBox<>(new String[]{"PENDING", "IN PROGRESS", "COMPLETED"});
        styleComboBox(cmbCleaningStatus);

        cmbStaff = new JComboBox<>();
        styleComboBox(cmbStaff);

        txtRemarks = createStyledTextField("");

        addFormGroup(formCard, "Select Room No.", cmbRoomNo);
        addFormGroup(formCard, "Cleaning Task Status", cmbCleaningStatus);
        addFormGroup(formCard, "Assigned Staff", cmbStaff);
        addFormGroup(formCard, "Task Details / Remarks", txtRemarks);

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

        btnUpdate = new JButton("Dispatch / Save");
        btnUpdate.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnUpdate.setBackground(new Color(99, 102, 241));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(e -> handleStatusUpdate());

        actionBtns.add(btnClear);
        actionBtns.add(btnUpdate);

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

        JLabel tableTitle = new JLabel("Live Housekeeping Requests & Task Log");
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

        String[] cols = {"Task ID", "Room No.", "Room Type", "Floor", "Assigned Staff", "Status", "Created Time", "Task / Notes", "Staff ID"};
        tableModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        taskTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        taskTable.setRowSorter(rowSorter);

        taskTable.removeColumn(taskTable.getColumnModel().getColumn(8));

        taskTable.setRowHeight(38);
        taskTable.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        taskTable.setShowVerticalLines(false);
        taskTable.setGridColor(new Color(241, 245, 249));
        taskTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = taskTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        taskTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());

        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && taskTable.getSelectedRow() != -1) {
                int modelRow = taskTable.convertRowIndexToModel(taskTable.getSelectedRow());
                populateFormFromSelectedRow(modelRow);
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(headerRow, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        opsPanel.add(formCard, BorderLayout.WEST);
        opsPanel.add(tableCard, BorderLayout.CENTER);

        main.add(statsRow);
        main.add(Box.createRigidArea(new Dimension(0, 18)));
        main.add(opsPanel);

        return main;
    }

    private void loadInitialData() {
        cmbRoomNo.removeAllItems();
        Vector<String> rooms = HouseKeepingDBA.getAllRooms();
        for (String r : rooms) cmbRoomNo.addItem(r);

        cmbStaff.removeAllItems();
        Vector<HouseKeepingDBA.StaffMember> staffList = HouseKeepingDBA.getHousekeepingStaff();
        for (HouseKeepingDBA.StaffMember s : staffList) cmbStaff.addItem(s);

        loadTableData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        Vector<Vector<Object>> tasks = HouseKeepingDBA.getAllHousekeepingTasks();
        for (Vector<Object> row : tasks) {
            tableModel.addRow(row);
        }

        HouseKeepingDBA.HousekeepingKPIs kpis = HouseKeepingDBA.getMetrics();
        lblCleanReady.setText(kpis.cleanReady);
        lblDirtyVacant.setText(kpis.dirtyVacant);
        lblInProgress.setText(kpis.inProgress);
        lblMaintenance.setText(kpis.maintenance);
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void populateFormFromSelectedRow(int modelRow) {
        selectedRequestId = (Integer) tableModel.getValueAt(modelRow, 0);
        cmbRoomNo.setSelectedItem(tableModel.getValueAt(modelRow, 1));
        cmbCleaningStatus.setSelectedItem(tableModel.getValueAt(modelRow, 5));
        txtRemarks.setText((String) tableModel.getValueAt(modelRow, 7));

        String staffId = (String) tableModel.getValueAt(modelRow, 8);
        if (staffId != null) {
            for (int i = 0; i < cmbStaff.getItemCount(); i++) {
                if (cmbStaff.getItemAt(i).userId.equals(staffId)) {
                    cmbStaff.setSelectedIndex(i);
                    break;
                }
            }
        }

        btnUpdate.setText("Update Task");
        btnUpdate.setBackground(new Color(16, 185, 129));
    }

    private void clearForm() {
        if (cmbRoomNo.getItemCount() > 0) cmbRoomNo.setSelectedIndex(0);
        cmbCleaningStatus.setSelectedIndex(0);
        if (cmbStaff.getItemCount() > 0) cmbStaff.setSelectedIndex(0);
        txtRemarks.setText("");
        selectedRequestId = null;

        btnUpdate.setText("Dispatch / Save");
        btnUpdate.setBackground(new Color(99, 102, 241));
        taskTable.clearSelection();
    }

    private void handleStatusUpdate() {
        String room = (String) cmbRoomNo.getSelectedItem();
        String status = (String) cmbCleaningStatus.getSelectedItem();
        HouseKeepingDBA.StaffMember staff = (HouseKeepingDBA.StaffMember) cmbStaff.getSelectedItem();
        String staffId = (staff != null) ? staff.userId : null;
        String notes = txtRemarks.getText().trim().isEmpty() ? "Routine Cleaning & Sanitization" : txtRemarks.getText().trim();

        if (room == null) {
            JOptionPane.showMessageDialog(this, "Please select a room.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isUpdate = (selectedRequestId != null);
        boolean success = HouseKeepingDBA.saveOrUpdateTask(selectedRequestId, room, staffId, status, notes, isUpdate);

        if (success) {
            JOptionPane.showMessageDialog(this, isUpdate ? "Task #" + selectedRequestId + " updated successfully!" : "Task dispatched for room " + room + "!");
            loadTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Database update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        public StatusBadgeRenderer() {
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
                case "COMPLETED":
                case "CLEAN":
                    bg = new Color(16, 185, 129);
                    break;
                case "PENDING":
                case "DIRTY":
                    bg = new Color(239, 68, 68);
                    break;
                case "IN PROGRESS":
                    bg = new Color(245, 158, 11);
                    break;
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

    static class HouseKeepingStatCard extends JPanel {
        private final Color c1, c2;
        private final JLabel lblCount;

        public HouseKeepingStatCard(String title, String count, String subtext, Color c1, Color c2) {
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
}