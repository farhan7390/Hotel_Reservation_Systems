package view;

import model.ServiceManagementDBA;

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

public class ServiceManagementUI extends JPanel {

    private DefaultTableModel tableModel;
    private JTable catalogTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField txtServiceName, txtUnitPrice;
    private JComboBox<String> cmbCategory;
    private JCheckBox chkAvailable;
    private JButton btnSave, btnClear;

    private JLabel lblTotalServices, lblAvailableCount, lblDiningCount, lblSpaCount;
    private Integer selectedServiceId = null;

    public ServiceManagementUI() {
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

        ServiceMetricCard cardTotal = new ServiceMetricCard("📋 Total Services", "0 Items", "Master Catalog", new Color(99, 102, 241), new Color(129, 140, 248));
        ServiceMetricCard cardAvailable = new ServiceMetricCard("🟢 Active Offerings", "0 Items", "Ready to Order", new Color(16, 185, 129), new Color(52, 211, 153));
        ServiceMetricCard cardDining = new ServiceMetricCard("🍽️ In-Room Dining", "0 Items", "Kitchen & Bar", new Color(168, 85, 247), new Color(192, 132, 252));
        ServiceMetricCard cardSpa = new ServiceMetricCard("💆 Spa & Wellness", "0 Items", "Guest Treatments", new Color(245, 158, 11), new Color(251, 191, 36));

        lblTotalServices = cardTotal.getCountLabel();
        lblAvailableCount = cardAvailable.getCountLabel();
        lblDiningCount = cardDining.getCountLabel();
        lblSpaCount = cardSpa.getCountLabel();

        statsRow.add(cardTotal);
        statsRow.add(cardAvailable);
        statsRow.add(cardDining);
        statsRow.add(cardSpa);

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

        JLabel formTitle = new JLabel("Add / Edit Service Offering");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        txtServiceName = createStyledTextField("");
        cmbCategory = new JComboBox<>(new String[]{"Food & In-Room Dining", "Spa & Wellness", "Laundry", "Transport"});
        styleComboBox(cmbCategory);

        txtUnitPrice = createStyledTextField("15,000 MMK");

        chkAvailable = new JCheckBox("Available for Guest Ordering", true);
        chkAvailable.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        chkAvailable.setOpaque(false);

        addFormGroup(formCard, "Service / Package Name", txtServiceName);
        addFormGroup(formCard, "Service Category", cmbCategory);
        addFormGroup(formCard, "Unit Price (MMK)", txtUnitPrice);

        formCard.add(chkAvailable);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

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

        btnSave = new JButton("Save Service");
        btnSave.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnSave.setBackground(new Color(99, 102, 241));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> handleSaveService());

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

        JLabel tableTitle = new JLabel("Live Master Service Catalog (Database)");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tableTitle.setForeground(new Color(30, 41, 59));

        JPanel controlsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controlsRight.setOpaque(false);

        JButton btnToggle = new JButton("🔄 Toggle Availability");
        btnToggle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btnToggle.setBackground(new Color(241, 245, 249));
        btnToggle.setForeground(new Color(51, 65, 85));
        btnToggle.setFocusPainted(false);
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggle.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btnToggle.addActionListener(e -> handleToggleAvailability());

        JTextField searchBox = new JTextField();
        searchBox.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        searchBox.setPreferredSize(new Dimension(170, 28));
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

        controlsRight.add(btnToggle);
        controlsRight.add(searchBox);

        headerRow.add(tableTitle, BorderLayout.WEST);
        headerRow.add(controlsRight, BorderLayout.EAST);

        String[] cols = {"ID", "Service / Package Name", "Category", "Unit Price", "Status", "Numeric Price"};
        tableModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        catalogTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        catalogTable.setRowSorter(rowSorter);

        catalogTable.removeColumn(catalogTable.getColumnModel().getColumn(5));

        catalogTable.setRowHeight(38);
        catalogTable.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        catalogTable.setShowVerticalLines(false);
        catalogTable.setGridColor(new Color(241, 245, 249));
        catalogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = catalogTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        catalogTable.getColumnModel().getColumn(4).setCellRenderer(new ServiceCatalogBadgeRenderer());

        catalogTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && catalogTable.getSelectedRow() != -1) {
                int modelRow = catalogTable.convertRowIndexToModel(catalogTable.getSelectedRow());
                populateFormFromSelectedRow(modelRow);
            }
        });

        JScrollPane scrollPane = new JScrollPane(catalogTable);
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
        tableModel.setRowCount(0);
        Vector<Vector<Object>> data = ServiceManagementDBA.getAllCatalogServices();
        for (Vector<Object> row : data) {
            tableModel.addRow(row);
        }

        ServiceManagementDBA.ServiceCatalogKPIs kpis = ServiceManagementDBA.getMetrics();
        lblTotalServices.setText(kpis.totalServices);
        lblAvailableCount.setText(kpis.activeAvailable);
        lblDiningCount.setText(kpis.diningCount);
        lblSpaCount.setText(kpis.spaWellnessCount);
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void populateFormFromSelectedRow(int modelRow) {
        selectedServiceId = (Integer) tableModel.getValueAt(modelRow, 0);
        txtServiceName.setText((String) tableModel.getValueAt(modelRow, 1));
        cmbCategory.setSelectedItem(tableModel.getValueAt(modelRow, 2));
        txtUnitPrice.setText((String) tableModel.getValueAt(modelRow, 3));
        chkAvailable.setSelected("AVAILABLE".equalsIgnoreCase((String) tableModel.getValueAt(modelRow, 4)));

        btnSave.setText("Update Service");
        btnSave.setBackground(new Color(16, 185, 129));
    }

    private void clearForm() {
        txtServiceName.setText("");
        cmbCategory.setSelectedIndex(0);
        txtUnitPrice.setText("15,000 MMK");
        chkAvailable.setSelected(true);
        selectedServiceId = null;

        btnSave.setText("Save Service");
        btnSave.setBackground(new Color(99, 102, 241));
        catalogTable.clearSelection();
    }

    private void handleSaveService() {
        String name = txtServiceName.getText().trim();
        String category = (String) cmbCategory.getSelectedItem();
        String priceText = txtUnitPrice.getText().replaceAll("[^0-9]", "");

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter service name and price.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal price = new BigDecimal(priceText);
        boolean isAvailable = chkAvailable.isSelected();
        boolean isUpdate = (selectedServiceId != null);

        boolean success = ServiceManagementDBA.saveOrUpdateService(selectedServiceId, name, category, price, isAvailable, isUpdate);

        if (success) {
            JOptionPane.showMessageDialog(this, isUpdate ? "Service updated successfully!" : "New service added to master catalog!");
            loadInitialData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Database update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleToggleAvailability() {
        if (selectedServiceId == null) {
            JOptionPane.showMessageDialog(this, "Select a service from the table to toggle availability.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = ServiceManagementDBA.toggleServiceAvailability(selectedServiceId);
        if (ok) {
            loadInitialData();
            clearForm();
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

    static class ServiceMetricCard extends JPanel {
        private final Color c1, c2;
        private final JLabel lblCount;

        public ServiceMetricCard(String title, String count, String subtext, Color c1, Color c2) {
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

    static class ServiceCatalogBadgeRenderer extends DefaultTableCellRenderer {
        public ServiceCatalogBadgeRenderer() {
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
            Color bg = "AVAILABLE".equalsIgnoreCase(status) ? new Color(16, 185, 129) : new Color(239, 68, 68);

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