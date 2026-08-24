package view;

import model.BillingDBA;

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

public class BillingUI extends JPanel {

    private DefaultTableModel tableModelInvoice, tableModelServices;
    private JTable invoiceTable, serviceBreakdownTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JComboBox<String> cmbInvoiceRoom, cmbPaymentMethod;
    private JTextField txtGuestName, txtRoomCharges, txtServiceCharges, txtTaxDiscount, txtNetPayable;

    private JLabel lblTotalRev, lblPendingSettlements, lblCardOnline, lblInvoicesIssued;
    private BillingDBA.ActiveFolio currentFolio = new BillingDBA.ActiveFolio();

    public BillingUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createBillingContent(), BorderLayout.CENTER);
        loadInitialData();
    }

    private JPanel createBillingContent() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        statsRow.setPreferredSize(new Dimension(1400, 115));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        BillingStatCard cardRevenue = new BillingStatCard("💵 Total Revenue", "0 MMK", "Settled Database Folios", new Color(16, 185, 129), new Color(52, 211, 153));
        BillingStatCard cardPending = new BillingStatCard("⏳ Pending Settlements", "0 MMK", "Unpaid Open Folios", new Color(245, 158, 11), new Color(251, 191, 36));
        BillingStatCard cardDigital = new BillingStatCard("💳 Card & Digital", "0 MMK", "KBZPay / Visa / CBPay", new Color(99, 102, 241), new Color(129, 140, 248));
        BillingStatCard cardCount = new BillingStatCard("🧾 Invoices Issued", "0 Invoices", "Lifetime Ledger", new Color(168, 85, 247), new Color(192, 132, 252));

        lblTotalRev = cardRevenue.getCountLabel();
        lblPendingSettlements = cardPending.getCountLabel();
        lblCardOnline = cardDigital.getCountLabel();
        lblInvoicesIssued = cardCount.getCountLabel();

        statsRow.add(cardRevenue);
        statsRow.add(cardPending);
        statsRow.add(cardDigital);
        statsRow.add(cardCount);

        JPanel workspaceRow = new JPanel(new BorderLayout(16, 0));
        workspaceRow.setOpaque(false);
        workspaceRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(380, 0));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel formTitle = new JLabel("Checkout Bill Settlement");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 12)));

        cmbInvoiceRoom = new JComboBox<>();
        styleComboBox(cmbInvoiceRoom);
        cmbInvoiceRoom.addActionListener(e -> updateBillBreakdown());

        txtGuestName = createStyledTextField("");
        txtGuestName.setEditable(false);

        txtRoomCharges = createStyledTextField("0 MMK");
        txtRoomCharges.setEditable(false);

        txtServiceCharges = createStyledTextField("0 MMK");
        txtServiceCharges.setEditable(false);

        txtTaxDiscount = createStyledTextField("0 MMK");
        txtTaxDiscount.setEditable(false);

        txtNetPayable = createStyledTextField("0 MMK");
        txtNetPayable.setFont(new Font("Century Gothic", Font.BOLD, 14));
        txtNetPayable.setForeground(new Color(79, 70, 229));
        txtNetPayable.setEditable(false);

        cmbPaymentMethod = new JComboBox<>(new String[]{"KBZPay", "Cash", "Credit Card", "WavePay", "Bank Transfer"});
        styleComboBox(cmbPaymentMethod);

        addFormGroup(formCard, "Active Checked-In Stay", cmbInvoiceRoom);
        addFormGroup(formCard, "Guest Full Name", txtGuestName);

        JPanel breakdownRow = new JPanel(new GridLayout(1, 2, 10, 0));
        breakdownRow.setOpaque(false);
        breakdownRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        breakdownRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JPanel roomGroup = new JPanel(new BorderLayout(0, 4));
        roomGroup.setOpaque(false);
        JLabel lblRoom = new JLabel("Room Charges");
        lblRoom.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblRoom.setForeground(new Color(100, 116, 139));
        roomGroup.add(lblRoom, BorderLayout.NORTH);
        roomGroup.add(txtRoomCharges, BorderLayout.CENTER);

        JPanel srvGroup = new JPanel(new BorderLayout(0, 4));
        srvGroup.setOpaque(false);
        JLabel lblSrv = new JLabel("Service / Dining");
        lblSrv.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblSrv.setForeground(new Color(100, 116, 139));
        srvGroup.add(lblSrv, BorderLayout.NORTH);
        srvGroup.add(txtServiceCharges, BorderLayout.CENTER);

        breakdownRow.add(roomGroup);
        breakdownRow.add(srvGroup);

        formCard.add(breakdownRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));

        addFormGroup(formCard, "Tax & Service Fee (5%)", txtTaxDiscount);
        addFormGroup(formCard, "Total Net Payable", txtNetPayable);
        addFormGroup(formCard, "Payment Settlement Mode", cmbPaymentMethod);

        JPanel actionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        actionBtns.setOpaque(false);
        actionBtns.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton btnPrint = new JButton("🖨️ Print Folio");
        btnPrint.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btnPrint.setBackground(new Color(241, 245, 249));
        btnPrint.setForeground(new Color(71, 85, 105));
        btnPrint.setFocusPainted(false);
        btnPrint.setBorderPainted(false);
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrint.addActionListener(e -> {
            if (txtGuestName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a room folio first.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Printed invoice receipt for " + txtGuestName.getText().trim() + " (" + txtNetPayable.getText() + ")");
        });

        JButton btnSettle = new JButton("Settle Payment");
        btnSettle.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnSettle.setBackground(new Color(16, 185, 129));
        btnSettle.setForeground(Color.WHITE);
        btnSettle.setFocusPainted(false);
        btnSettle.setBorderPainted(false);
        btnSettle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSettle.addActionListener(e -> handleSettlePayment());

        actionBtns.add(btnPrint);
        actionBtns.add(btnSettle);

        formCard.add(Box.createRigidArea(new Dimension(0, 8)));
        formCard.add(actionBtns);
        formCard.add(Box.createVerticalGlue());

        JPanel rightArea = new JPanel(new GridLayout(2, 1, 0, 14));
        rightArea.setOpaque(false);

        JPanel serviceItemCard = new JPanel(new BorderLayout());
        serviceItemCard.setBackground(Color.WHITE);
        serviceItemCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel srvTitle = new JLabel("Room Service & Dining Charges for Selected Stay");
        srvTitle.setFont(new Font("Century Gothic", Font.BOLD, 13));
        srvTitle.setForeground(new Color(30, 41, 59));
        srvTitle.setBorder(new EmptyBorder(0, 0, 8, 0));

        String[] srvCols = {"Service / Package Name", "Qty", "Total Amount", "Status"};
        tableModelServices = new DefaultTableModel(new Object[][]{}, srvCols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        serviceBreakdownTable = new JTable(tableModelServices);
        serviceBreakdownTable.setRowHeight(32);
        serviceBreakdownTable.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        serviceBreakdownTable.setShowVerticalLines(false);
        serviceBreakdownTable.setGridColor(new Color(241, 245, 249));

        JScrollPane srvScroll = new JScrollPane(serviceBreakdownTable);
        srvScroll.setBorder(null);
        srvScroll.getViewport().setBackground(Color.WHITE);

        serviceItemCard.add(srvTitle, BorderLayout.NORTH);
        serviceItemCard.add(srvScroll, BorderLayout.CENTER);

        JPanel invoiceCard = new JPanel(new BorderLayout());
        invoiceCard.setBackground(Color.WHITE);
        invoiceCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel tableTitle = new JLabel("Settled Invoice History & Audit");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 13));
        tableTitle.setForeground(new Color(30, 41, 59));

        JTextField searchBox = new JTextField();
        searchBox.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        searchBox.setPreferredSize(new Dimension(170, 26));
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(2, 6, 2, 6)
        ));

        searchBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterTable(searchBox.getText().trim()); }
            @Override public void removeUpdate(DocumentEvent e) { filterTable(searchBox.getText().trim()); }
            @Override public void changedUpdate(DocumentEvent e) { filterTable(searchBox.getText().trim()); }
        });

        headerRow.add(tableTitle, BorderLayout.WEST);
        headerRow.add(searchBox, BorderLayout.EAST);

        String[] cols = {"Invoice ID", "Room No.", "Guest Name", "Payment Mode", "Total Paid", "Date & Time", "Status"};
        tableModelInvoice = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        invoiceTable = new JTable(tableModelInvoice);
        rowSorter = new TableRowSorter<>(tableModelInvoice);
        invoiceTable.setRowSorter(rowSorter);
        invoiceTable.setRowHeight(32);
        invoiceTable.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        invoiceTable.setShowVerticalLines(false);
        invoiceTable.setGridColor(new Color(241, 245, 249));

        JTableHeader th = invoiceTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 12));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        invoiceTable.getColumnModel().getColumn(6).setCellRenderer(new BillingStatusBadgeRenderer());

        JScrollPane invoiceScroll = new JScrollPane(invoiceTable);
        invoiceScroll.setBorder(null);
        invoiceScroll.getViewport().setBackground(Color.WHITE);

        invoiceCard.add(headerRow, BorderLayout.NORTH);
        invoiceCard.add(invoiceScroll, BorderLayout.CENTER);

        rightArea.add(serviceItemCard);
        rightArea.add(invoiceCard);

        workspaceRow.add(formCard, BorderLayout.WEST);
        workspaceRow.add(rightArea, BorderLayout.CENTER);

        main.add(statsRow);
        main.add(Box.createRigidArea(new Dimension(0, 14)));
        main.add(workspaceRow);

        return main;
    }

    public void loadInitialData() {
        cmbInvoiceRoom.removeAllItems();
        Vector<String> activeRooms = BillingDBA.getActiveStayRooms();
        for (String r : activeRooms) {
            cmbInvoiceRoom.addItem(r);
        }
        updateBillBreakdown();
        loadTableData();
    }

    public void loadTableData() {
        tableModelInvoice.setRowCount(0);
        Vector<Vector<Object>> data = BillingDBA.getAllInvoices();
        for (Vector<Object> row : data) {
            tableModelInvoice.addRow(row);
        }

        BillingDBA.BillingKPIs kpis = BillingDBA.getBillingMetrics();
        lblTotalRev.setText(kpis.totalRevenue);
        lblPendingSettlements.setText(kpis.pendingAmount);
        lblCardOnline.setText(kpis.cardOnlineAmount);
        lblInvoicesIssued.setText(kpis.totalInvoices);
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void updateBillBreakdown() {
        String selected = (String) cmbInvoiceRoom.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            txtGuestName.setText("");
            txtRoomCharges.setText("0 MMK");
            txtServiceCharges.setText("0 MMK");
            txtTaxDiscount.setText("0 MMK");
            txtNetPayable.setText("0 MMK");
            tableModelServices.setRowCount(0);
            return;
        }

        String roomNo = selected.split(" ")[0].trim();
        currentFolio = BillingDBA.getFolioForRoom(roomNo);

        txtGuestName.setText(currentFolio.guestName);
        txtRoomCharges.setText(String.format("%,d MMK", currentFolio.roomCharges.longValue()));
        txtServiceCharges.setText(String.format("%,d MMK", currentFolio.serviceCharges.longValue()));
        txtTaxDiscount.setText(String.format("%,d MMK", currentFolio.taxAmount.longValue()));
        txtNetPayable.setText(String.format("%,d MMK", currentFolio.netPayable.longValue()));

        // Populate dynamic itemized services
        tableModelServices.setRowCount(0);
        if (currentFolio.bookingRef != null && !currentFolio.bookingRef.isEmpty()) {
            Vector<Vector<Object>> services = BillingDBA.getServiceOrdersForBooking(currentFolio.bookingRef);
            for (Vector<Object> s : services) {
                tableModelServices.addRow(s);
            }
        }
    }

    private void handleSettlePayment() {
        if (currentFolio.bookingRef == null || currentFolio.bookingRef.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an active guest room stay.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String method = (String) cmbPaymentMethod.getSelectedItem();
        boolean success = BillingDBA.settleInvoice(
                currentFolio.bookingRef,
                currentFolio.guestId,
                currentFolio.roomNo,
                currentFolio.roomCharges,
                currentFolio.serviceCharges,
                currentFolio.taxAmount,
                currentFolio.netPayable,
                method
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "Payment of " + txtNetPayable.getText() + " settled for " + currentFolio.guestName + " via " + method + "!\nRoom " + currentFolio.roomNo + " is now released to AVAILABLE.");
            loadInitialData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to record invoice settlement.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFormGroup(JPanel parent, String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lbl.setForeground(new Color(100, 116, 139));

        input.setPreferredSize(new Dimension(input.getPreferredSize().width, 30));

        group.add(lbl, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);

        parent.add(group);
        parent.add(Box.createRigidArea(new Dimension(0, 6)));
    }

    private JTextField createStyledTextField(String initialValue) {
        JTextField tf = new JTextField(initialValue);
        tf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        box.setBackground(Color.WHITE);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    static class BillingStatCard extends JPanel {
        private final Color c1, c2;
        private final JLabel lblCount;

        public BillingStatCard(String title, String count, String subtext, Color c1, Color c2) {
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

    static class BillingStatusBadgeRenderer extends DefaultTableCellRenderer {
        public BillingStatusBadgeRenderer() {
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
            Color bg = ("PAID".equalsIgnoreCase(status) || "SETTLED".equalsIgnoreCase(status)) ? new Color(16, 185, 129) :
                    "UNPAID".equalsIgnoreCase(status) ? new Color(239, 68, 68) :
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