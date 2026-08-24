package view;

import model.ServiceDBA;

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

public class ServiceUI extends JPanel {

    private DefaultTableModel tableModel;
    private JTable serviceTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JComboBox<String> cmbRoomNo, cmbServiceCategory;
    private JComboBox<ServiceDBA.ServiceItemData> cmbServiceItem;
    private JSpinner spinQuantity;
    private JTextField txtUnitPrice, txtSpecialInstructions;
    private JCheckBox chkChargeToBill;
    private JButton btnDispatch, btnClear;

    private JLabel lblActiveOrders, lblDiningDelivered, lblExpressLaundry, lblServiceRevenue;
    private String selectedOrderId = null;

    public ServiceUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createServiceContent(), BorderLayout.CENTER);
        loadInitialData();
    }

    private JPanel createServiceContent() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(1400, 120));
        statsRow.setPreferredSize(new Dimension(1400, 120));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        ServiceStatCard cardActive = new ServiceStatCard("🛎️ Active Orders", "0 Orders", "In Kitchen / In Transit", new Color(99, 102, 241), new Color(129, 140, 248));
        ServiceStatCard cardDining = new ServiceStatCard("🍽️ Dining & Food", "0 Delivered", "Delivered In-Room Dining", new Color(168, 85, 247), new Color(192, 132, 252));
        ServiceStatCard cardLaundry = new ServiceStatCard("👔 Express Laundry", "0 Orders", "Processing & Care", new Color(217, 119, 6), new Color(245, 158, 11));
        ServiceStatCard cardRevenue = new ServiceStatCard("💵 Service Revenue", "0 MMK", "Billed to Room Folios", new Color(16, 185, 129), new Color(52, 211, 153));

        lblActiveOrders = cardActive.getCountLabel();
        lblDiningDelivered = cardDining.getCountLabel();
        lblExpressLaundry = cardLaundry.getCountLabel();
        lblServiceRevenue = cardRevenue.getCountLabel();

        statsRow.add(cardActive);
        statsRow.add(cardDining);
        statsRow.add(cardLaundry);
        statsRow.add(cardRevenue);

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

        JLabel formTitle = new JLabel("Place Room Service Request");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        cmbRoomNo = new JComboBox<>();
        styleComboBox(cmbRoomNo);

        cmbServiceCategory = new JComboBox<>();
        styleComboBox(cmbServiceCategory);
        cmbServiceCategory.addActionListener(e -> updateItemOptions());

        cmbServiceItem = new JComboBox<>();
        styleComboBox(cmbServiceItem);
        cmbServiceItem.addActionListener(e -> updateItemPrice());

        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        spinQuantity.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        spinQuantity.setPreferredSize(new Dimension(80, 32));

        txtUnitPrice = createStyledTextField("0 MMK");
        txtUnitPrice.setEditable(false);

        txtSpecialInstructions = createStyledTextField("");

        chkChargeToBill = new JCheckBox("Charge directly to Room Folio Bill", true);
        chkChargeToBill.setFont(new Font("Century Gothic", Font.BOLD, 11));
        chkChargeToBill.setForeground(new Color(71, 85, 105));
        chkChargeToBill.setOpaque(false);
        chkChargeToBill.setAlignmentX(Component.LEFT_ALIGNMENT);

        addFormGroup(formCard, "Target Checked-in Room", cmbRoomNo);
        addFormGroup(formCard, "Service Category", cmbServiceCategory);
        addFormGroup(formCard, "Service Item / Package", cmbServiceItem);

        JPanel priceRow = new JPanel(new GridLayout(1, 2, 10, 0));
        priceRow.setOpaque(false);
        priceRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceRow.setMaximumSize(new Dimension(1400, 58));

        JPanel qtyGroup = new JPanel(new BorderLayout(0, 4));
        qtyGroup.setOpaque(false);
        JLabel lblQty = new JLabel("Quantity");
        lblQty.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblQty.setForeground(new Color(100, 116, 139));
        qtyGroup.add(lblQty, BorderLayout.NORTH);
        qtyGroup.add(spinQuantity, BorderLayout.CENTER);

        JPanel unitPriceGroup = new JPanel(new BorderLayout(0, 4));
        unitPriceGroup.setOpaque(false);
        JLabel lblPrice = new JLabel("Unit Price");
        lblPrice.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblPrice.setForeground(new Color(100, 116, 139));
        unitPriceGroup.add(lblPrice, BorderLayout.NORTH);
        unitPriceGroup.add(txtUnitPrice, BorderLayout.CENTER);

        priceRow.add(qtyGroup);
        priceRow.add(unitPriceGroup);

        formCard.add(priceRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));

        addFormGroup(formCard, "Delivery Instructions / Notes", txtSpecialInstructions);

        formCard.add(chkChargeToBill);
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

        btnDispatch = new JButton("Dispatch Order");
        btnDispatch.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnDispatch.setBackground(new Color(99, 102, 241));
        btnDispatch.setForeground(Color.WHITE);
        btnDispatch.setFocusPainted(false);
        btnDispatch.setBorderPainted(false);
        btnDispatch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDispatch.addActionListener(e -> handleDispatchOrder());

        actionBtns.add(btnClear);
        actionBtns.add(btnDispatch);

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

        JLabel tableTitle = new JLabel("Service and Delivery");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tableTitle.setForeground(new Color(30, 41, 59));

        JPanel controlsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controlsRight.setOpaque(false);

        JButton btnChangeStatus = new JButton("✏️ Change Status");
        btnChangeStatus.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btnChangeStatus.setBackground(new Color(241, 245, 249));
        btnChangeStatus.setForeground(new Color(51, 65, 85));
        btnChangeStatus.setFocusPainted(false);
        btnChangeStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangeStatus.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btnChangeStatus.addActionListener(e -> openStatusDialog());

        JTextField searchBox = new JTextField();
        searchBox.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        searchBox.setPreferredSize(new Dimension(160, 28));
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

        JButton btnRefresh = new JButton("🔄 Refresh Queue");
        btnRefresh.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btnRefresh.setBackground(new Color(241, 245, 249));
        btnRefresh.setForeground(new Color(51, 65, 85));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btnRefresh.addActionListener(e -> {
            loadInitialData();
            JOptionPane.showMessageDialog(this, "Service queue refreshed with latest orders!");
        });

        controlsRight.add(btnRefresh);

        controlsRight.add(btnChangeStatus);
        controlsRight.add(searchBox);

        headerRow.add(tableTitle, BorderLayout.WEST);
        headerRow.add(controlsRight, BorderLayout.EAST);

        String[] cols = {"Order ID", "Room No.", "Service Item", "Qty", "Total (MMK)", "Ordered Time", "Status", "Instructions", "Category"};
        tableModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        serviceTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        serviceTable.setRowSorter(rowSorter);

        serviceTable.removeColumn(serviceTable.getColumnModel().getColumn(8));
        serviceTable.removeColumn(serviceTable.getColumnModel().getColumn(7));

        serviceTable.setRowHeight(38);
        serviceTable.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        serviceTable.setShowVerticalLines(false);
        serviceTable.setGridColor(new Color(241, 245, 249));
        serviceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = serviceTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        serviceTable.getColumnModel().getColumn(6).setCellRenderer(new ServiceStatusBadgeRenderer());

        serviceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && serviceTable.getSelectedRow() != -1) {
                int modelRow = serviceTable.convertRowIndexToModel(serviceTable.getSelectedRow());
                selectedOrderId = (String) tableModel.getValueAt(modelRow, 0);
            }
        });

        JScrollPane scrollPane = new JScrollPane(serviceTable);
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
        cmbRoomNo.removeAllItems();
        Vector<String> rooms = ServiceDBA.getActiveStayRooms();
        for (String r : rooms) cmbRoomNo.addItem(r);

        cmbServiceCategory.removeAllItems();
        Vector<String> categories = ServiceDBA.getCategories();
        for (String c : categories) cmbServiceCategory.addItem(c);

        updateItemOptions();
        loadTableData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        Vector<Vector<Object>> data = ServiceDBA.getAllOrders();
        for (Vector<Object> row : data) {
            tableModel.addRow(row);
        }

        ServiceDBA.ServiceKPIs kpis = ServiceDBA.getServiceMetrics();
        lblActiveOrders.setText(kpis.activeOrders);
        lblDiningDelivered.setText(kpis.diningDelivered);
        lblExpressLaundry.setText(kpis.expressLaundry);
        lblServiceRevenue.setText(kpis.serviceRevenue);
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void updateItemOptions() {
        String category = (String) cmbServiceCategory.getSelectedItem();
        if (category == null) return;

        cmbServiceItem.removeAllItems();
        Vector<ServiceDBA.ServiceItemData> items = ServiceDBA.getItemsByCategory(category);
        for (ServiceDBA.ServiceItemData item : items) {
            cmbServiceItem.addItem(item);
        }
        updateItemPrice();
    }

    private void updateItemPrice() {
        ServiceDBA.ServiceItemData item = (ServiceDBA.ServiceItemData) cmbServiceItem.getSelectedItem();
        if (item != null) {
            txtUnitPrice.setText(String.format("%,d MMK", item.unitPrice.longValue()));
        } else {
            txtUnitPrice.setText("0 MMK");
        }
    }

    private void openStatusDialog() {
        if (selectedOrderId == null) {
            JOptionPane.showMessageDialog(this, "Please select an order from the queue table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Service Order Status", true);
        dialog.setSize(360, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblTitle = new JLabel("Order: " + selectedOrderId);
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 14));
        lblTitle.setForeground(new Color(30, 41, 59));

        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"PREPARING", "IN SERVICE", "DELIVERED", "BILLED", "CANCELLED"});
        styleComboBox(cmbStatus);

        JButton btnSave = new JButton("Update Status");
        btnSave.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnSave.setBackground(new Color(99, 102, 241));
        btnSave.setForeground(Color.WHITE);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setMaximumSize(new Dimension(1400, 36));
        btnSave.addActionListener(e -> {
            String newStatus = (String) cmbStatus.getSelectedItem();
            boolean ok = ServiceDBA.updateServiceOrderStatus(selectedOrderId, newStatus);
            if (ok) {
                dialog.dispose();
                loadTableData();
                JOptionPane.showMessageDialog(this, "Order " + selectedOrderId + " updated to " + newStatus + "!");
            }
        });

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(cmbStatus);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(btnSave);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
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

    private void clearForm() {
        if (cmbRoomNo.getItemCount() > 0) cmbRoomNo.setSelectedIndex(0);
        if (cmbServiceCategory.getItemCount() > 0) cmbServiceCategory.setSelectedIndex(0);
        spinQuantity.setValue(1);
        txtSpecialInstructions.setText("");
        selectedOrderId = null;
        serviceTable.clearSelection();
    }

    private void handleDispatchOrder() {
        String selectedRoom = (String) cmbRoomNo.getSelectedItem();
        ServiceDBA.ServiceItemData item = (ServiceDBA.ServiceItemData) cmbServiceItem.getSelectedItem();

        if (selectedRoom == null || item == null) {
            JOptionPane.showMessageDialog(this, "Please select an active stay room and a service item.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roomNo = selectedRoom.split(" ")[0].trim();
        int qty = (int) spinQuantity.getValue();
        BigDecimal total = item.unitPrice.multiply(BigDecimal.valueOf(qty));
        String instructions = txtSpecialInstructions.getText().trim();

        boolean success = ServiceDBA.dispatchServiceOrder(roomNo, item.serviceId, qty, total, instructions);

        if (success) {
            JOptionPane.showMessageDialog(this, "Service order dispatched to " + roomNo + " (" + item.serviceName + ")!");
            loadTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to dispatch service order. Ensure room has an active checked-in booking.", "Dispatch Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class ServiceStatCard extends JPanel {
        private final Color c1, c2;
        private final JLabel lblCount;

        public ServiceStatCard(String title, String count, String subtext, Color c1, Color c2) {
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

    static class ServiceStatusBadgeRenderer extends DefaultTableCellRenderer {
        public ServiceStatusBadgeRenderer() {
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
                case "DELIVERED":
                case "BILLED":
                    bg = new Color(16, 185, 129);
                    break;
                case "PREPARING":
                    bg = new Color(245, 158, 11);
                    break;
                case "IN SERVICE":
                    bg = new Color(99, 102, 241);
                    break;
                case "CANCELLED":
                    bg = new Color(239, 68, 68);
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
}