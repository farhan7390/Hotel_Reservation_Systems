package view;

import model.RoomDBA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Vector;

public class RoomManagementUI extends JPanel {

    private DefaultTableModel tableModel;
    private JTable roomTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField txtRoomNumber, txtBasePrice, txtMaxCapacity, txtImagePath;
    private JComboBox<String> cmbRoomType, cmbFloor, cmbStatus;
    private JCheckBox chkBalcony, chkSeaView, chkJacuzzi;
    private JButton btnSave, btnClear, btnBrowseImage, btnRefresh;
    private RoomImagePreviewPanel imagePreviewPanel;

    private JLabel lblTotalRooms, lblAvailableRooms, lblOccupiedRooms, lblBlockedRooms;
    private String selectedRoomNo = null;

    public RoomManagementUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        add(createRoomContent(), BorderLayout.CENTER);
        loadInitialData();
    }

    private JPanel createRoomContent() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. KPI Stats Cards Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        statsRow.setPreferredSize(new Dimension(1400, 120));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoomStatCard cardTotal = new RoomStatCard("🏨 Total Rooms", "0 Rooms", "Database Inventory", new Color(99, 102, 241), new Color(129, 140, 248));
        RoomStatCard cardAvail = new RoomStatCard("🟢 Available Now", "0 Rooms", "Ready to Assign", new Color(16, 185, 129), new Color(52, 211, 153));
        RoomStatCard cardOcc = new RoomStatCard("🔴 Occupied", "0 Rooms", "Guests In-House", new Color(239, 68, 68), new Color(248, 113, 113));
        RoomStatCard cardBlock = new RoomStatCard("🟡 Maintenance / Blocked", "0 Rooms", "Under Service", new Color(245, 158, 11), new Color(251, 191, 36));

        lblTotalRooms = cardTotal.getCountLabel();
        lblAvailableRooms = cardAvail.getCountLabel();
        lblOccupiedRooms = cardOcc.getCountLabel();
        lblBlockedRooms = cardBlock.getCountLabel();

        statsRow.add(cardTotal);
        statsRow.add(cardAvail);
        statsRow.add(cardOcc);
        statsRow.add(cardBlock);

        JPanel workspaceRow = new JPanel(new BorderLayout(18, 0));
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

        JLabel formTitle = new JLabel("Add / Edit Room Profile");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        formTitle.setForeground(new Color(30, 41, 59));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));

        imagePreviewPanel = new RoomImagePreviewPanel("/images/rooms/r101_1.jpg", "Room Photo Preview");
        imagePreviewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(imagePreviewPanel);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));

        txtRoomNumber = createStyledTextField("");
        txtBasePrice = createStyledTextField("0 MMK");
        txtBasePrice.setEditable(false);
        txtMaxCapacity = createStyledTextField("2 Guests");
        txtMaxCapacity.setEditable(false);

        txtImagePath = createStyledTextField("/images/rooms/r101_1.jpg");
        txtImagePath.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { imagePreviewPanel.setImagePath(txtImagePath.getText().trim()); }
            @Override public void removeUpdate(DocumentEvent e) { imagePreviewPanel.setImagePath(txtImagePath.getText().trim()); }
            @Override public void changedUpdate(DocumentEvent e) { imagePreviewPanel.setImagePath(txtImagePath.getText().trim()); }
        });

        btnBrowseImage = new JButton("📁 Browse");
        btnBrowseImage.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnBrowseImage.setBackground(new Color(241, 245, 249));
        btnBrowseImage.setForeground(new Color(51, 65, 85));
        btnBrowseImage.setFocusPainted(false);
        btnBrowseImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBrowseImage.addActionListener(e -> chooseLocalRoomImage());

        JPanel imgRow = new JPanel(new BorderLayout(6, 0));
        imgRow.setOpaque(false);
        imgRow.add(txtImagePath, BorderLayout.CENTER);
        imgRow.add(btnBrowseImage, BorderLayout.EAST);

        cmbRoomType = new JComboBox<>();
        styleComboBox(cmbRoomType);
        cmbRoomType.addActionListener(e -> updateCategoryDefaults());

        cmbFloor = new JComboBox<>(new String[]{"1st Floor", "2nd Floor", "3rd Floor", "4th Floor", "Penthouse Level"});
        styleComboBox(cmbFloor);

        cmbStatus = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE", "RESERVED"});
        styleComboBox(cmbStatus);

        chkBalcony = new JCheckBox("Balcony", true);
        chkSeaView = new JCheckBox("Sea View", false);
        chkJacuzzi = new JCheckBox("Jacuzzi Bath", false);
        chkBalcony.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        chkSeaView.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        chkJacuzzi.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        chkBalcony.setOpaque(false);
        chkSeaView.setOpaque(false);
        chkJacuzzi.setOpaque(false);

        addFormGroup(formCard, "Room Number (e.g. R-101)", txtRoomNumber);
        addFormGroup(formCard, "Room Category", cmbRoomType);
        addFormGroup(formCard, "Room Photo Location / Asset", imgRow);
        addFormGroup(formCard, "Floor Location", cmbFloor);

        JPanel numRow = new JPanel(new GridLayout(1, 2, 10, 0));
        numRow.setOpaque(false);
        numRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        numRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JPanel priceGroup = new JPanel(new BorderLayout(0, 4));
        priceGroup.setOpaque(false);
        JLabel lblP = new JLabel("Night Rate");
        lblP.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblP.setForeground(new Color(100, 116, 139));
        priceGroup.add(lblP, BorderLayout.NORTH);
        priceGroup.add(txtBasePrice, BorderLayout.CENTER);

        JPanel capGroup = new JPanel(new BorderLayout(0, 4));
        capGroup.setOpaque(false);
        JLabel lblC = new JLabel("Max Capacity");
        lblC.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblC.setForeground(new Color(100, 116, 139));
        capGroup.add(lblC, BorderLayout.NORTH);
        capGroup.add(txtMaxCapacity, BorderLayout.CENTER);

        numRow.add(priceGroup);
        numRow.add(capGroup);

        formCard.add(numRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));

        addFormGroup(formCard, "Operational Status", cmbStatus);

        JPanel amenitiesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        amenitiesRow.setOpaque(false);
        amenitiesRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        amenitiesRow.add(chkBalcony);
        amenitiesRow.add(chkSeaView);
        amenitiesRow.add(chkJacuzzi);

        formCard.add(amenitiesRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel actionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        actionBtns.setOpaque(false);
        actionBtns.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(71, 85, 105));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> clearForm());

        btnSave = new JButton("Save Room");
        btnSave.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnSave.setBackground(new Color(99, 102, 241));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> handleSaveRoom());

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

        JLabel tableTitle = new JLabel("Live Room Inventory & Photo Catalog (Database)");
        tableTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tableTitle.setForeground(new Color(30, 41, 59));

        JPanel tableControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tableControls.setOpaque(false);

        btnRefresh = new JButton("🔄 Refresh");
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
            JOptionPane.showMessageDialog(this, "Room inventory and live occupancy refreshed!");
        });

        JTextField searchBox = new JTextField();
        searchBox.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        searchBox.setPreferredSize(new Dimension(170, 28));
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        searchBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterTable(searchBox.getText().trim()); }
            @Override public void removeUpdate(DocumentEvent e) { filterTable(searchBox.getText().trim()); }
            @Override public void changedUpdate(DocumentEvent e) { filterTable(searchBox.getText().trim()); }
        });

        tableControls.add(btnRefresh);
        tableControls.add(searchBox);

        headerRow.add(tableTitle, BorderLayout.WEST);
        headerRow.add(tableControls, BorderLayout.EAST);

        String[] cols = {"Room No.", "Category", "Floor", "Night Rate", "Capacity", "Key Amenities", "Photo Path", "Status", "b", "s", "j"};
        tableModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        roomTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        roomTable.setRowSorter(rowSorter);

        roomTable.removeColumn(roomTable.getColumnModel().getColumn(10));
        roomTable.removeColumn(roomTable.getColumnModel().getColumn(9));
        roomTable.removeColumn(roomTable.getColumnModel().getColumn(8));

        roomTable.setRowHeight(38);
        roomTable.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        roomTable.setShowVerticalLines(false);
        roomTable.setGridColor(new Color(241, 245, 249));
        roomTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = roomTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        roomTable.getColumnModel().getColumn(7).setCellRenderer(new RoomStatusBadgeRenderer());

        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && roomTable.getSelectedRow() != -1) {
                int modelRow = roomTable.convertRowIndexToModel(roomTable.getSelectedRow());
                populateFormFromSelectedRow(modelRow);
            }
        });

        JScrollPane scrollPane = new JScrollPane(roomTable);
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

    private void chooseLocalRoomImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Real Room Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtImagePath.setText(selectedFile.getAbsolutePath());
            imagePreviewPanel.setImagePath(selectedFile.getAbsolutePath());
        }
    }

    public void loadInitialData() {
        cmbRoomType.removeAllItems();
        for (String cat : RoomDBA.getCategoryNames()) {
            cmbRoomType.addItem(cat);
        }
        updateCategoryDefaults();
        loadTableData();
    }

    private void updateCategoryDefaults() {
        String selectedCat = (String) cmbRoomType.getSelectedItem();
        if (selectedCat != null) {
            BigDecimal price = RoomDBA.getCategoryBasePrice(selectedCat);
            String cap = RoomDBA.getCategoryCapacity(selectedCat);
            txtBasePrice.setText(String.format("%,d MMK", price.longValue()));
            txtMaxCapacity.setText(cap);
        }
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        Vector<Vector<Object>> rooms = RoomDBA.getAllRooms();
        for (Vector<Object> row : rooms) {
            tableModel.addRow(row);
        }

        int[] stats = RoomDBA.getRoomInventoryStats();
        lblTotalRooms.setText(stats[0] + " Rooms");
        lblAvailableRooms.setText(stats[1] + " Rooms");
        lblOccupiedRooms.setText(stats[2] + " Rooms");
        lblBlockedRooms.setText(stats[3] + " Rooms");
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void populateFormFromSelectedRow(int modelRow) {
        selectedRoomNo = (String) tableModel.getValueAt(modelRow, 0);
        txtRoomNumber.setText(selectedRoomNo);
        txtRoomNumber.setEditable(false);

        cmbRoomType.setSelectedItem(tableModel.getValueAt(modelRow, 1));
        cmbFloor.setSelectedItem(tableModel.getValueAt(modelRow, 2));
        txtBasePrice.setText((String) tableModel.getValueAt(modelRow, 3));
        txtMaxCapacity.setText((String) tableModel.getValueAt(modelRow, 4));

        String imgPath = (String) tableModel.getValueAt(modelRow, 6);
        txtImagePath.setText(imgPath);
        imagePreviewPanel.setImagePath(imgPath);

        cmbStatus.setSelectedItem(tableModel.getValueAt(modelRow, 7));

        chkBalcony.setSelected((Boolean) tableModel.getValueAt(modelRow, 8));
        chkSeaView.setSelected((Boolean) tableModel.getValueAt(modelRow, 9));
        chkJacuzzi.setSelected((Boolean) tableModel.getValueAt(modelRow, 10));

        btnSave.setText("Update Room");
        btnSave.setBackground(new Color(16, 185, 129));
    }

    private void addFormGroup(JPanel parent, String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lbl.setForeground(new Color(100, 116, 139));

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
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
    }

    private void clearForm() {
        txtRoomNumber.setText("");
        txtRoomNumber.setEditable(true);
        txtImagePath.setText("/images/rooms/r101_1.jpg");
        imagePreviewPanel.setImagePath("/images/rooms/r101_1.jpg");
        if (cmbRoomType.getItemCount() > 0) cmbRoomType.setSelectedIndex(0);
        cmbFloor.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        chkBalcony.setSelected(true);
        chkSeaView.setSelected(false);
        chkJacuzzi.setSelected(false);
        selectedRoomNo = null;

        btnSave.setText("Save Room");
        btnSave.setBackground(new Color(99, 102, 241));
        roomTable.clearSelection();
        updateCategoryDefaults();
    }

    private void handleSaveRoom() {
        String roomNo = txtRoomNumber.getText().trim();
        String catName = (String) cmbRoomType.getSelectedItem();
        String floor = (String) cmbFloor.getSelectedItem();
        String status = (String) cmbStatus.getSelectedItem();
        String imgPath = txtImagePath.getText().trim();

        if (roomNo.isEmpty() || catName == null || floor == null) {
            JOptionPane.showMessageDialog(this, "Please enter Room Number and assign Category.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isUpdate = (selectedRoomNo != null);
        boolean success = RoomDBA.saveOrUpdateRoom(roomNo, catName, floor,
                chkBalcony.isSelected(), chkSeaView.isSelected(), chkJacuzzi.isSelected(),
                status, imgPath, isUpdate);

        if (success) {
            JOptionPane.showMessageDialog(this, isUpdate ? "Room " + roomNo + " updated successfully!" : "Room " + roomNo + " added to inventory!");
            loadTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Database operation failed. Room No may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class RoomImagePreviewPanel extends JPanel {
        private String resourcePath;
        private Image image;
        private final String fallbackText;

        public RoomImagePreviewPanel(String resourcePath, String fallbackText) {
            this.resourcePath = resourcePath;
            this.fallbackText = fallbackText;
            setPreferredSize(new Dimension(1400, 110));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            setMinimumSize(new Dimension(200, 110));
            setOpaque(false);
            loadImage();
        }

        public void setImagePath(String newPath) {
            this.resourcePath = newPath;
            loadImage();
            repaint();
        }

        private void loadImage() {
            this.image = null;
            if (resourcePath != null && !resourcePath.trim().isEmpty()) {
                try {
                    File file = new File(resourcePath);
                    if (file.exists() && file.isFile()) {
                        this.image = new ImageIcon(file.getAbsolutePath()).getImage();
                        return;
                    }

                    URL url = getClass().getResource(resourcePath);
                    if (url != null) {
                        this.image = new ImageIcon(url).getImage();
                    }
                } catch (Exception ignored) {}
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            Shape clipShape = new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 10, 10);
            g2.setClip(clipShape);

            if (image != null) {
                g2.drawImage(image, 0, 0, w, h, this);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, new Color(51, 65, 85), w, h, new Color(30, 41, 59));
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(255, 255, 255, 220));
                g2.setFont(new Font("Century Gothic", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "📷 " + fallbackText;
                int tx = (w - fm.stringWidth(txt)) / 2;
                int ty = ((h - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(txt, tx, ty);
            }

            g2.setClip(null);
            g2.setColor(new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new java.awt.geom.RoundRectangle2D.Double(0, 0, w - 1, h - 1, 10, 10));

            g2.dispose();
        }
    }

    static class RoomStatCard extends JPanel {
        private final Color c1, c2;
        private final JLabel lblCount;

        public RoomStatCard(String title, String count, String subtext, Color c1, Color c2) {
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

    static class RoomStatusBadgeRenderer extends DefaultTableCellRenderer {
        public RoomStatusBadgeRenderer() {
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
                case "AVAILABLE":
                    bg = new Color(16, 185, 129);
                    break;
                case "OCCUPIED":
                    bg = new Color(239, 68, 68);
                    break;
                case "MAINTENANCE":
                    bg = new Color(245, 158, 11);
                    break;
                case "RESERVED":
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