package view;

import model.BookingDBA;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class BookingUI extends JPanel {

    private DefaultTableModel tableModel;
    private JTable bookingTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField txtGuestName, txtContact, txtNID;
    private JComboBox<String> cmbRoomType, cmbBookingTier, cmbRoomNo;
    private JTextField txtCheckIn, txtCheckOut, txtTotalAmount;
    private JButton btnConfirm, btnClear;

    private String selectedBookingRef = null;
    private String currentSelectedRoom = null;
    private BigDecimal currentCalculatedAmount = BigDecimal.ZERO;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BookingUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        add(createBookingContent(), BorderLayout.CENTER);
        loadInitialData();
    }

    private JPanel createBookingContent() {
        JPanel main = new JPanel(new BorderLayout(20, 0));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(380, 0));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel formTitle = new JLabel("Reservation Details & Entry");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 16));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        txtGuestName = createStyledTextField();
        txtContact = createStyledTextField();
        txtNID = createStyledTextField();

        cmbBookingTier = new JComboBox<>();
        styleComboBox(cmbBookingTier);

        cmbRoomType = new JComboBox<>();
        styleComboBox(cmbRoomType);

        cmbRoomNo = new JComboBox<>();
        styleComboBox(cmbRoomNo);

        txtCheckIn = createStyledTextField();
        txtCheckIn.setText(LocalDate.now().format(FORMATTER));

        txtCheckOut = createStyledTextField();
        txtCheckOut.setText(LocalDate.now().plusDays(1).format(FORMATTER));

        txtTotalAmount = createStyledTextField();
        txtTotalAmount.setEditable(false);
        txtTotalAmount.setBackground(new Color(248, 250, 252));
        txtTotalAmount.setText("0 MMK");

        cmbBookingTier.addActionListener(e -> calculateEstimatedPrice());
        cmbRoomType.addActionListener(e -> {
            updateAvailableRooms();
            calculateEstimatedPrice();
        });

        DocumentListener dateListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calculateEstimatedPrice(); }
            @Override public void removeUpdate(DocumentEvent e) { calculateEstimatedPrice(); }
            @Override public void changedUpdate(DocumentEvent e) { calculateEstimatedPrice(); }
        };
        txtCheckIn.getDocument().addDocumentListener(dateListener);
        txtCheckOut.getDocument().addDocumentListener(dateListener);

        addFormGroup(formCard, "Guest Full Name", txtGuestName);
        addFormGroup(formCard, "Contact / Phone", txtContact);
        addFormGroup(formCard, "NID / Passport", txtNID);
        addFormGroup(formCard, "Booking Tier", cmbBookingTier);
        addFormGroup(formCard, "Room Category", cmbRoomType);
        addFormGroup(formCard, "Assign Room No.", cmbRoomNo);

        JPanel datesRow = new JPanel(new GridLayout(1, 2, 10, 0));
        datesRow.setOpaque(false);
        datesRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        datesRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JPanel inGroup = new JPanel(new BorderLayout(0, 4));
        inGroup.setOpaque(false);
        JLabel lblIn = new JLabel("Check In (YYYY-MM-DD)");
        lblIn.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lblIn.setForeground(new Color(100, 116, 139));
        inGroup.add(lblIn, BorderLayout.NORTH);
        inGroup.add(txtCheckIn, BorderLayout.CENTER);

        JPanel outGroup = new JPanel(new BorderLayout(0, 4));
        outGroup.setOpaque(false);
        JLabel lblOut = new JLabel("Check Out (YYYY-MM-DD)");
        lblOut.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lblOut.setForeground(new Color(100, 116, 139));
        outGroup.add(lblOut, BorderLayout.NORTH);
        outGroup.add(txtCheckOut, BorderLayout.CENTER);

        datesRow.add(inGroup);
        datesRow.add(outGroup);

        formCard.add(datesRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));

        addFormGroup(formCard, "Total Calculated Amount", txtTotalAmount);

        JPanel actionBtnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        actionBtnRow.setOpaque(false);
        actionBtnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionBtnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(71, 85, 105));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> clearFormFields());

        btnConfirm = new JButton("Book Now");
        btnConfirm.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnConfirm.setBackground(new Color(99, 102, 241));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> handleConfirmOrUpdateBooking());

        actionBtnRow.add(btnClear);
        actionBtnRow.add(btnConfirm);

        formCard.add(Box.createRigidArea(new Dimension(0, 8)));
        formCard.add(actionBtnRow);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel tableHeaderPanel = new JPanel(new BorderLayout(10, 0));
        tableHeaderPanel.setOpaque(false);
        tableHeaderPanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel tableTitle = new JLabel("Live Database Reservations");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 16));
        tableTitle.setForeground(new Color(30, 41, 59));

        JPanel tableControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tableControls.setOpaque(false);

        JButton btnEditStatus = new JButton("✏️ Change Status");
        btnEditStatus.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btnEditStatus.setBackground(new Color(241, 245, 249));
        btnEditStatus.setForeground(new Color(51, 65, 85));
        btnEditStatus.setFocusPainted(false);
        btnEditStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditStatus.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnEditStatus.addActionListener(e -> openStatusEditDialog());

        JButton btnCancel = new JButton("🗑️ Cancel");
        btnCancel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btnCancel.setBackground(new Color(254, 242, 242));
        btnCancel.setForeground(new Color(239, 68, 68));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnCancel.addActionListener(e -> handleCancelBooking());

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(170, 30));
        searchField.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        searchField.setToolTipText("Filter by Name, Room, Tier or Status");

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterTable(searchField.getText().trim()); }
            @Override public void removeUpdate(DocumentEvent e) { filterTable(searchField.getText().trim()); }
            @Override public void changedUpdate(DocumentEvent e) { filterTable(searchField.getText().trim()); }
        });

        tableControls.add(btnEditStatus);
        tableControls.add(btnCancel);
        tableControls.add(searchField);

        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);
        tableHeaderPanel.add(tableControls, BorderLayout.EAST);

        String[] cols = {"Ref", "Guest Name", "Room", "Tier", "Check In", "Check Out", "Total", "Status", "Phone", "NID", "Category"};
        tableModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        bookingTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        bookingTable.setRowSorter(rowSorter);

        bookingTable.removeColumn(bookingTable.getColumnModel().getColumn(10));
        bookingTable.removeColumn(bookingTable.getColumnModel().getColumn(9));
        bookingTable.removeColumn(bookingTable.getColumnModel().getColumn(8));

        bookingTable.setRowHeight(38);
        bookingTable.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        bookingTable.setShowVerticalLines(false);
        bookingTable.setGridColor(new Color(241, 245, 249));
        bookingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookingTable.getSelectedRow() != -1) {
                int modelRow = bookingTable.convertRowIndexToModel(bookingTable.getSelectedRow());
                populateFormFromSelectedRow(modelRow);
            }
        });

        JTableHeader th = bookingTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        bookingTable.getColumnModel().getColumn(7).setCellRenderer(new BookingStatusRenderer());

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(tableHeaderPanel, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        main.add(formCard, BorderLayout.WEST);
        main.add(tableCard, BorderLayout.CENTER);

        return main;
    }

    private void loadInitialData() {
        cmbBookingTier.removeAllItems();
        for (String t : BookingDBA.getPricingTiers()) cmbBookingTier.addItem(t);

        cmbRoomType.removeAllItems();
        for (String c : BookingDBA.getRoomCategories()) cmbRoomType.addItem(c);

        updateAvailableRooms();
        calculateEstimatedPrice();
        loadTableData();
    }

    private void updateAvailableRooms() {
        String selectedCategory = (String) cmbRoomType.getSelectedItem();
        cmbRoomNo.removeAllItems();
        Vector<String> rooms = BookingDBA.getAvailableRoomsByCategory(selectedCategory, currentSelectedRoom);
        for (String r : rooms) {
            cmbRoomNo.addItem(r);
        }
        if (currentSelectedRoom != null) {
            cmbRoomNo.setSelectedItem(currentSelectedRoom);
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        Vector<Vector<Object>> bookings = BookingDBA.getAllBookings();
        for (Vector<Object> row : bookings) {
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

    private void calculateEstimatedPrice() {
        String tier = (String) cmbBookingTier.getSelectedItem();
        String cat = (String) cmbRoomType.getSelectedItem();
        if (tier == null || cat == null) return;

        try {
            LocalDate in = LocalDate.parse(txtCheckIn.getText().trim(), FORMATTER);
            LocalDate out = LocalDate.parse(txtCheckOut.getText().trim(), FORMATTER);
            currentCalculatedAmount = BookingDBA.calculateTariff(cat, tier, in, out);
            txtTotalAmount.setText(String.format("%,d MMK", currentCalculatedAmount.longValue()));
        } catch (Exception ignored) {
            txtTotalAmount.setText("0 MMK");
        }
    }

    private void populateFormFromSelectedRow(int modelRow) {
        selectedBookingRef = (String) tableModel.getValueAt(modelRow, 0);
        txtGuestName.setText((String) tableModel.getValueAt(modelRow, 1));
        currentSelectedRoom = (String) tableModel.getValueAt(modelRow, 2);

        String tier = (String) tableModel.getValueAt(modelRow, 3);
        for (int i = 0; i < cmbBookingTier.getItemCount(); i++) {
            if (cmbBookingTier.getItemAt(i).equalsIgnoreCase(tier)) {
                cmbBookingTier.setSelectedIndex(i);
                break;
            }
        }

        txtTotalAmount.setText((String) tableModel.getValueAt(modelRow, 6));
        txtContact.setText((String) tableModel.getValueAt(modelRow, 8));
        txtNID.setText((String) tableModel.getValueAt(modelRow, 9));

        String category = (String) tableModel.getValueAt(modelRow, 10);
        cmbRoomType.setSelectedItem(category);
        updateAvailableRooms();

        btnConfirm.setText("Save Changes");
        btnConfirm.setBackground(new Color(16, 185, 129));
    }

    private void openStatusEditDialog() {
        int viewRow = bookingTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = bookingTable.convertRowIndexToModel(viewRow);
        String bookingId = (String) tableModel.getValueAt(modelRow, 0);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 7);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Booking Status", true);
        dialog.setSize(380, 240);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblTitle = new JLabel("Reservation Status: " + bookingId);
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 14));
        lblTitle.setForeground(new Color(30, 41, 59));

        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"CONFIRMED", "CHECKED-IN", "COMPLETED", "CANCELLED"});
        cmbStatus.setSelectedItem(currentStatus);
        styleComboBox(cmbStatus);

        JButton btnSave = new JButton("Update Status");
        btnSave.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnSave.setBackground(new Color(99, 102, 241));
        btnSave.setForeground(Color.WHITE);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnSave.addActionListener(e -> {
            String newStatus = (String) cmbStatus.getSelectedItem();
            boolean ok = BookingDBA.updateBookingStatus(bookingId, newStatus);
            if (ok) {
                dialog.dispose();
                loadTableData();
                updateAvailableRooms();
                JOptionPane.showMessageDialog(this, "Status updated to " + newStatus + "!");
            }
        });

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(cmbStatus);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnSave);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void handleCancelBooking() {
        int viewRow = bookingTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = bookingTable.convertRowIndexToModel(viewRow);
        String bookingId = (String) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel reservation " + bookingId + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            BookingDBA.updateBookingStatus(bookingId, "CANCELLED");
            loadTableData();
            updateAvailableRooms();
            clearFormFields();
        }
    }

    private void addFormGroup(JPanel parent, String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

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

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
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
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
    }

    private void clearFormFields() {
        txtGuestName.setText("");
        txtContact.setText("");
        txtNID.setText("");
        selectedBookingRef = null;
        currentSelectedRoom = null;
        if (cmbBookingTier.getItemCount() > 0) cmbBookingTier.setSelectedIndex(0);
        if (cmbRoomType.getItemCount() > 0) cmbRoomType.setSelectedIndex(0);

        btnConfirm.setText("Book Now");
        btnConfirm.setBackground(new Color(99, 102, 241));
        bookingTable.clearSelection();
        updateAvailableRooms();
        calculateEstimatedPrice();
    }

    private void handleConfirmOrUpdateBooking() {
        String name = txtGuestName.getText().trim();
        String contact = txtContact.getText().trim();
        String nid = txtNID.getText().trim();
        String room = (String) cmbRoomNo.getSelectedItem();
        String tier = (String) cmbBookingTier.getSelectedItem();

        if (name.isEmpty() || contact.isEmpty() || room == null || tier == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all details and ensure a Room is selected.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim(), FORMATTER);
            LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim(), FORMATTER);

            boolean isUpdate = (selectedBookingRef != null);
            boolean success = BookingDBA.saveOrUpdateBooking(selectedBookingRef, name, contact, nid, room, tier, inDate, outDate, currentCalculatedAmount, isUpdate);

            if (success) {
                JOptionPane.showMessageDialog(this, isUpdate ? "Booking updated successfully!" : "Reservation confirmed for " + name + " (" + room + ")!");
                loadTableData();
                clearFormFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save booking to database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Date Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    static class BookingStatusRenderer extends DefaultTableCellRenderer {
        public BookingStatusRenderer() {
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
                    bg = new Color(34, 197, 94);
                    break;
                case "CONFIRMED":
                    bg = new Color(99, 102, 241);
                    break;
                case "CANCELLED":
                    bg = new Color(239, 68, 68);
                    break;
                case "COMPLETED":
                default:
                    bg = new Color(148, 163, 184);
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