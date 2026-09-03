/*
package view;

import model.CustomerDBA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

public class Customer_Screen extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCardsPanel = new JPanel(cardLayout);
    private DefaultTableModel modelRoomService;

    private CustomerDBA.GuestProfile currentGuest;
    private JLabel lblGuestTopName, lblGuestTopBadge;
    private CircularAvatar topAvatar;
    private JLabel lblBillRoomCharges, lblBillServiceCharges, lblBillTax, lblBillTotalNet;
    private JLabel lblBillRoomDetails;
    private JButton btnPayFolio;
    private DefaultTableModel modelLoyaltyHistory;
    private JLabel lblLoyaltyPointsBadge, lblLoyaltyTierBadge;
    private JProgressBar tierProgressBar;
    private DefaultTableModel modelMyReservations;
    private JPanel pnlVouchersList;
    private JLabel lblValName, lblValPhone, lblValEmail, lblValCity, lblValNid, lblValPref;

    public Customer_Screen() {
        this.currentGuest = CustomerDBA.getGuestProfile(User_UI.getUname());

        setTitle("Hotel Guest Portal - Self Service & Amenities");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createCustomerSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(createCustomerTopBar(), BorderLayout.NORTH);

        contentCardsPanel.add(createExploreAndBookPanel(), "EXPLORE_BOOK");
        contentCardsPanel.add(createMyReservationsPanel(), "MY_RESERVATIONS");
        contentCardsPanel.add(createLoyaltyRewardsPanel(), "LOYALTY_REWARDS");
        contentCardsPanel.add(createRoomServicePanel(), "ROOM_SERVICE");
        contentCardsPanel.add(createHousekeepingRequestPanel(), "HOUSEKEEPING");
        contentCardsPanel.add(createMyBillPanel(), "MY_BILL");
        contentCardsPanel.add(createProfilePanel(), "MY_PROFILE");

        util.AppIcon.setFrameIcon(this, "/images/favicon1.png");

        mainArea.add(contentCardsPanel, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        refreshCustomerSession();
    }

    private JPanel createCustomerSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        0, getHeight(), new Color(30, 41, 59)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(20, 14, 20, 14));

        JLabel hotelLogo = new JLabel("<html><b>HMS</b> <font color='#818cf8'>GUEST</font></html>");
        hotelLogo.setFont(new Font("Century Gothic", Font.BOLD, 20));
        hotelLogo.setForeground(Color.WHITE);
        hotelLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(hotelLogo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel sectionTitle = new JLabel("GUEST SERVICES");
        sectionTitle.setFont(new Font("Century Gothic", Font.BOLD, 10));
        sectionTitle.setForeground(new Color(148, 163, 184));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sectionTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        String[][] customerNav = {
                {"🏨  Explore & Book", "EXPLORE_BOOK"},
                {"📅  My Reservations", "MY_RESERVATIONS"},
                {"⭐  VIP Loyalty & Rewards", "LOYALTY_REWARDS"},
                {"🛎️  Order Room Service", "ROOM_SERVICE"},
                {"🧹  Housekeeping Request", "HOUSEKEEPING"},
                {"💳  My Folio & Bill", "MY_BILL"},
                {"👤  My Guest Profile", "MY_PROFILE"}
        };

        for (int i = 0; i < customerNav.length; i++) {
            String label = customerNav[i][0];
            String cardKey = customerNav[i][1];
            sidebar.add(createGuestNavButton(label, cardKey, i == 0));
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("🚪  Log Out");
        btnLogout.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnLogout.setForeground(new Color(254, 202, 202));
        btnLogout.setBackground(new Color(239, 68, 68, 40));
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(200, 36));
        btnLogout.setPreferredSize(new Dimension(200, 36));
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(239, 68, 68, 90), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Log out of your customer session?", "Log Out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new User_UI().setVisible(true);
            }
        });

        sidebar.add(btnLogout);
        return sidebar;
    }

    private JButton createGuestNavButton(String text, String cardKey, boolean isActive) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getClientProperty("active") == Boolean.TRUE) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(99, 102, 241, 140));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                super.paintComponent(g);
            }
        };

        btn.putClientProperty("active", isActive);
        btn.setFont(new Font("Segoe UI Emoji", isActive ? Font.BOLD : Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 36));
        btn.setPreferredSize(new Dimension(200, 36));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> {
            cardLayout.show(contentCardsPanel, cardKey);
            Container parent = btn.getParent();
            for (Component c : parent.getComponents()) {
                if (c instanceof JButton) {
                    ((JButton) c).putClientProperty("active", false);
                    c.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
                    c.repaint();
                }
            }
            btn.putClientProperty("active", true);
            btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
            btn.repaint();
        });

        return btn;
    }

    private JPanel createCustomerTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(10, 24, 10, 24)
        ));

        JLabel title = new JLabel("Welcome to Your Guest Stay & Amenities Portal");
        title.setFont(new Font("Century Gothic", Font.BOLD, 15));
        title.setForeground(new Color(30, 41, 59));

        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightSection.setOpaque(false);

        JPanel guestBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        guestBlock.setOpaque(false);

        lblGuestTopName = new JLabel(currentGuest.fullName, SwingConstants.RIGHT);
        lblGuestTopName.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblGuestTopName.setForeground(new Color(30, 41, 59));

        String roomText = currentGuest.activeRoomNo.equals("None") ? "No Active Check-in" : "Room " + currentGuest.activeRoomNo;
        lblGuestTopBadge = new JLabel(roomText + " • " + currentGuest.vipTier, SwingConstants.RIGHT);
        lblGuestTopBadge.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblGuestTopBadge.setForeground(new Color(99, 102, 241));

        guestBlock.add(lblGuestTopName);
        guestBlock.add(lblGuestTopBadge);

        String initial = currentGuest.fullName.isEmpty() ? "G" : currentGuest.fullName.substring(0, 1).toUpperCase();
        topAvatar = new CircularAvatar("/images/profile.png", initial, 36);

        rightSection.add(guestBlock);
        rightSection.add(topAvatar);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightSection, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createExploreAndBookPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel sectionTitle = new JLabel("Available Rooms & Suites for Booking");
        sectionTitle.setFont(new Font("Century Gothic", Font.BOLD, 16));
        sectionTitle.setForeground(new Color(30, 41, 59));

        JLabel subTitle = new JLabel("Search Available Rooms and Book");
        subTitle.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        subTitle.setForeground(new Color(100, 116, 139));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(sectionTitle);
        titleBox.add(subTitle);

        headerPanel.add(titleBox, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);

        List<CustomerDBA.RoomCardData> rooms = CustomerDBA.getAvailableRoomCards();
        int cols = 3;
        int rows = Math.max(1, (int) Math.ceil(rooms.size() / 3.0));

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 14, 14));
        gridPanel.setOpaque(false);

        for (CustomerDBA.RoomCardData r : rooms) {
            gridPanel.add(createRoomCard(
                    r.roomNo, r.title, r.floor, r.price, r.tierBadge, r.features, new Color(99, 102, 241), r.imagePaths
            ));
        }

        int remainder = rooms.size() % 3;
        if (remainder != 0) {
            int emptySlots = 3 - remainder;
            for (int i = 0; i < emptySlots; i++) {
                JPanel placeholder = new JPanel();
                placeholder.setOpaque(false);
                gridPanel.add(placeholder);
            }
        }

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoomCard(String roomNo, String title, String floor, String price, String tierBadge, String features, Color accentColor, String[] imagePaths) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(280, 275));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 275));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        RoomImageCarousel carousel = new RoomImageCarousel(imagePaths, roomNo + " (" + title + ")");
        carousel.setPreferredSize(new Dimension(260, 115));
        carousel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        carousel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel topRow = new JPanel(new BorderLayout(4, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRoom = new JLabel(roomNo + " • " + title);
        lblRoom.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        lblRoom.setForeground(new Color(30, 41, 59));

        JLabel lblBadge = new JLabel(tierBadge) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadge.setFont(new Font("Segoe UI Emoji", Font.BOLD, 9));
        lblBadge.setForeground(accentColor);
        lblBadge.setBorder(new EmptyBorder(2, 5, 2, 5));

        topRow.add(lblRoom, BorderLayout.WEST);
        topRow.add(lblBadge, BorderLayout.EAST);

        JLabel lblFloor = new JLabel(floor);
        lblFloor.setFont(new Font("Century Gothic", Font.PLAIN, 10));
        lblFloor.setForeground(new Color(148, 163, 184));
        lblFloor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFeatures = new JLabel("<html><font color='#64748b'>Amenities: </font>" + features + "</html>");
        lblFeatures.setFont(new Font("Century Gothic", Font.PLAIN, 10));
        lblFeatures.setForeground(new Color(51, 65, 85));
        lblFeatures.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bottomRow = new JPanel(new BorderLayout(6, 0));
        bottomRow.setOpaque(false);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPrice = new JLabel(price);
        lblPrice.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblPrice.setForeground(new Color(15, 23, 42));

        JButton btnBook = new JButton("Book This Room");
        btnBook.setFont(new Font("Century Gothic", Font.BOLD, 10));
        btnBook.setBackground(new Color(99, 102, 241));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setBorderPainted(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBook.setPreferredSize(new Dimension(120, 28));
        btnBook.addActionListener(e -> openBookingModal(roomNo, title, price));

        bottomRow.add(lblPrice, BorderLayout.WEST);
        bottomRow.add(btnBook, BorderLayout.EAST);

        card.add(carousel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(topRow);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(lblFloor);
        card.add(Box.createRigidArea(new Dimension(0, 3)));
        card.add(lblFeatures);
        card.add(Box.createVerticalGlue());
        card.add(bottomRow);

        return card;
    }

    private void openBookingModal(String roomNo, String roomTitle, String roomPrice) {
        JDialog dialog = new JDialog(this, "Complete Your Reservation", true);
        dialog.setSize(420, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblHeading = new JLabel("Booking for " + roomNo + " (" + roomTitle + ")");
        lblHeading.setFont(new Font("Century Gothic", Font.BOLD, 14));
        lblHeading.setForeground(new Color(30, 41, 59));
        lblHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRate = new JLabel("Rate: " + roomPrice);
        lblRate.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblRate.setForeground(new Color(99, 102, 241));
        lblRate.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtGuest = createStyledTextField();
        txtGuest.setText(currentGuest.fullName);
        txtGuest.setEditable(false);

        JTextField txtPhone = createStyledTextField();
        txtPhone.setText(currentGuest.phone);
        txtPhone.setEditable(false);

        JTextField txtCheckIn = createStyledTextField();
        txtCheckIn.setText(LocalDate.now().toString());

        JTextField txtCheckOut = createStyledTextField();
        txtCheckOut.setText(LocalDate.now().plusDays(2).toString());

        JComboBox<String> cmbTier = new JComboBox<>(new String[]{
                "Staycation (Overnight)", "Daycation (Day Pass)", "Night Stay (Transit)"
        });
        styleComboBox(cmbTier);

        formPanel.add(lblHeading);
        formPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        formPanel.add(lblRate);
        formPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        addGuestFormGroup(formPanel, "Guest Name", txtGuest);
        addGuestFormGroup(formPanel, "Contact Phone", txtPhone);
        addGuestFormGroup(formPanel, "Booking Tier Experience", cmbTier);

        JPanel dates = new JPanel(new GridLayout(1, 2, 10, 0));
        dates.setOpaque(false);
        dates.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        dates.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel d1 = new JPanel(new BorderLayout(0, 4));
        d1.setOpaque(false);
        JLabel lbl1 = new JLabel("Check-In Date (YYYY-MM-DD)");
        lbl1.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lbl1.setForeground(new Color(100, 116, 139));
        d1.add(lbl1, BorderLayout.NORTH);
        d1.add(txtCheckIn, BorderLayout.CENTER);

        JPanel d2 = new JPanel(new BorderLayout(0, 4));
        d2.setOpaque(false);
        JLabel lbl2 = new JLabel("Check-Out Date (YYYY-MM-DD)");
        lbl2.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lbl2.setForeground(new Color(100, 116, 139));
        d2.add(lbl2, BorderLayout.NORTH);
        d2.add(txtCheckOut, BorderLayout.CENTER);

        dates.add(d1);
        dates.add(d2);
        formPanel.add(dates);
        formPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        JButton btnConfirm = new JButton("Confirm & Reserve Room");
        btnConfirm.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnConfirm.setBackground(new Color(16, 185, 129));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);

        */
/*btnConfirm.addActionListener(e -> {
            try {
                LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim());
                LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim());
                String tier = (String) cmbTier.getSelectedItem();

                boolean success = CustomerDBA.createCustomerBooking(currentGuest.guestId, roomNo, tier, inDate, outDate);
                if (success) {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Success! Reservation confirmed for " + roomNo + " (" + roomTitle + ").", "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
                    refreshCustomerSession();
                    refreshMyReservationsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit booking into the database.", "Booking Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format. Please use YYYY-MM-DD.", "Date Error", JOptionPane.WARNING_MESSAGE);
            }
        });*//*


        btnConfirm.addActionListener(e -> {
            try {
                LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim());
                LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim());
                String tier = (String) cmbTier.getSelectedItem();

                // Pass currentGuest.fullName and currentGuest.email directly
                boolean success = CustomerDBA.createCustomerBooking(
                        currentGuest.guestId,
                        currentGuest.fullName,
                        currentGuest.email,
                        roomNo,
                        tier,
                        inDate,
                        outDate
                );

                if (success) {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Success! Reservation confirmed for " + roomNo + " (" + roomTitle + ").\nConfirmation email has been sent to " + currentGuest.email, "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
                    refreshCustomerSession();
                    refreshMyReservationsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit booking into the database.", "Booking Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format. Please use YYYY-MM-DD.", "Date Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        formPanel.add(btnConfirm);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel title = new JLabel("My Active & Past Room Bookings (Database)");
        title.setFont(new Font("Century Gothic", Font.BOLD, 15));
        title.setForeground(new Color(30, 41, 59));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        String[] cols = {"Booking Ref", "Room", "Tier", "Check-In", "Check-Out", "Room Total", "Status"};
        modelMyReservations = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(modelMyReservations);
        table.setRowHeight(38);
        table.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        table.getColumnModel().getColumn(6).setCellRenderer(new GuestStatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        tableCard.add(title, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        refreshMyReservationsTable();
        return panel;
    }

    public void refreshMyReservationsTable() {
        if (modelMyReservations == null) return;
        modelMyReservations.setRowCount(0);
        Vector<Vector<Object>> data = CustomerDBA.getGuestReservations(currentGuest.guestId);
        for (Vector<Object> r : data) {
            modelMyReservations.addRow(r);
        }
    }

    private JPanel createLoyaltyRewardsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Top Banner
        JPanel tierBanner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(99, 102, 241),
                        getWidth(), 0, new Color(168, 85, 247)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        tierBanner.setLayout(new BorderLayout(20, 0));
        tierBanner.setBorder(new EmptyBorder(18, 20, 18, 20));
        tierBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        tierBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tierLeft = new JPanel();
        tierLeft.setLayout(new BoxLayout(tierLeft, BoxLayout.Y_AXIS));
        tierLeft.setOpaque(false);

        lblLoyaltyTierBadge = new JLabel("⭐ " + currentGuest.vipTier + " MEMBER");
        lblLoyaltyTierBadge.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        lblLoyaltyTierBadge.setForeground(new Color(254, 240, 138));

        lblLoyaltyPointsBadge = new JLabel(String.format("%,d Points Available", currentGuest.loyaltyPoints));
        lblLoyaltyPointsBadge.setFont(new Font("Century Gothic", Font.BOLD, 22));
        lblLoyaltyPointsBadge.setForeground(Color.WHITE);

        JLabel lblProgress = new JLabel("Earn 1 Point per 1,000 MMK on settled stays");
        lblProgress.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblProgress.setForeground(new Color(238, 242, 255));

        tierLeft.add(lblLoyaltyTierBadge);
        tierLeft.add(Box.createRigidArea(new Dimension(0, 4)));
        tierLeft.add(lblLoyaltyPointsBadge);
        tierLeft.add(Box.createRigidArea(new Dimension(0, 4)));
        tierLeft.add(lblProgress);

        JPanel tierRight = new JPanel(new GridLayout(2, 1, 0, 4));
        tierRight.setOpaque(false);

        JLabel lblStats = new JLabel("City: " + currentGuest.city + " | ID: " + currentGuest.guestId, SwingConstants.RIGHT);
        lblStats.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblStats.setForeground(Color.WHITE);

        tierProgressBar = new JProgressBar(0, 5000);
        tierProgressBar.setValue(Math.min(currentGuest.loyaltyPoints, 5000));
        tierProgressBar.setPreferredSize(new Dimension(220, 8));
        tierProgressBar.setForeground(new Color(254, 240, 138));
        tierProgressBar.setBackground(new Color(255, 255, 255, 60));
        tierProgressBar.setBorderPainted(false);

        tierRight.add(lblStats);
        tierRight.add(tierProgressBar);

        tierBanner.add(tierLeft, BorderLayout.WEST);
        tierBanner.add(tierRight, BorderLayout.EAST);

        // Middle Workspace Row
        JPanel middleRow = new JPanel(new BorderLayout(18, 0));
        middleRow.setOpaque(false);
        middleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dynamic Vouchers Card (Left)
        JPanel vouchersCard = new JPanel(new BorderLayout());
        vouchersCard.setBackground(Color.WHITE);
        vouchersCard.setPreferredSize(new Dimension(420, 0));
        vouchersCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel vTitle = new JLabel("Redeem Loyalty Vouchers & Perks (Database)");
        vTitle.setFont(new Font("Century Gothic", Font.BOLD, 14));
        vTitle.setForeground(new Color(30, 41, 59));
        vTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        vouchersCard.add(vTitle, BorderLayout.NORTH);

        pnlVouchersList = new JPanel();
        pnlVouchersList.setLayout(new BoxLayout(pnlVouchersList, BoxLayout.Y_AXIS));
        pnlVouchersList.setOpaque(false);

        JScrollPane voucherScroll = new JScrollPane(pnlVouchersList);
        voucherScroll.setBorder(null);
        voucherScroll.setOpaque(false);
        voucherScroll.getViewport().setOpaque(false);
        voucherScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        vouchersCard.add(voucherScroll, BorderLayout.CENTER);

        // Right Ledger Table
        JPanel historyCard = new JPanel(new BorderLayout());
        historyCard.setBackground(Color.WHITE);
        historyCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel hTitle = new JLabel("Points Activity & Reward Ledger (Database)");
        hTitle.setFont(new Font("Century Gothic", Font.BOLD, 14));
        hTitle.setForeground(new Color(30, 41, 59));
        hTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        String[] cols = {"Date", "Activity Description", "Reference", "Points", "Status"};
        modelLoyaltyHistory = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable hTable = new JTable(modelLoyaltyHistory);
        hTable.setRowHeight(36);
        hTable.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        hTable.setShowVerticalLines(false);
        hTable.setGridColor(new Color(241, 245, 249));

        JTableHeader th = hTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 12));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        hTable.getColumnModel().getColumn(4).setCellRenderer(new GuestStatusBadgeRenderer());

        JScrollPane hScroll = new JScrollPane(hTable);
        hScroll.setBorder(null);
        hScroll.getViewport().setBackground(Color.WHITE);

        historyCard.add(hTitle, BorderLayout.NORTH);
        historyCard.add(hScroll, BorderLayout.CENTER);

        middleRow.add(vouchersCard, BorderLayout.WEST);
        middleRow.add(historyCard, BorderLayout.CENTER);

        panel.add(tierBanner);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(middleRow);

        reloadDynamicPerks();
        return panel;
    }

    private void reloadDynamicPerks() {
        if (pnlVouchersList == null) return;
        pnlVouchersList.removeAll();

        List<CustomerDBA.LoyaltyPerkData> perks = CustomerDBA.getActiveLoyaltyPerks();
        if (perks.isEmpty()) {
            JLabel emptyLbl = new JLabel("No loyalty perks currently available.");
            emptyLbl.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            emptyLbl.setForeground(new Color(148, 163, 184));
            pnlVouchersList.add(emptyLbl);
        } else {
            for (CustomerDBA.LoyaltyPerkData perk : perks) {
                pnlVouchersList.add(createRewardItem(
                        perk.title,
                        String.format("Cost: %,d Points", perk.pointsCost),
                        perk.pointsCost
                ));
                pnlVouchersList.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }
        pnlVouchersList.revalidate();
        pnlVouchersList.repaint();
    }

    private JPanel createRewardItem(String title, String costStr, int cost) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(new Color(248, 250, 252));
        item.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        lblT.setForeground(new Color(30, 41, 59));

        JLabel lblC = new JLabel(costStr);
        lblC.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblC.setForeground(new Color(99, 102, 241));

        text.add(lblT);
        text.add(lblC);

        JButton btnRedeem = new JButton("Redeem") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isArmed()) {
                    g2.setColor(new Color(13, 148, 136)); // Darker teal/green on click
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(5, 150, 105)); // Hover green
                } else {
                    g2.setColor(new Color(16, 185, 129)); // Default vibrant emerald
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }
        };

        btnRedeem.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnRedeem.setForeground(Color.WHITE);
        btnRedeem.setPreferredSize(new Dimension(88, 30));
        btnRedeem.setFocusPainted(false);
        btnRedeem.setBorderPainted(false);
        btnRedeem.setContentAreaFilled(false);
        btnRedeem.setOpaque(false);
        btnRedeem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRedeem.addActionListener(e -> {
            boolean ok = CustomerDBA.redeemLoyaltyPerk(currentGuest.guestId, title, cost);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Success! '" + title + "' redeemed for " + cost + " points.");
                refreshCustomerSession();
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient points. You have " + currentGuest.loyaltyPoints + " points.", "Redemption Failed", JOptionPane.WARNING_MESSAGE);
            }
        });

        item.add(text, BorderLayout.CENTER);
        item.add(btnRedeem, BorderLayout.EAST);
        return item;
    }

    private JPanel createRoomServicePanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel title = new JLabel("Order In-Room Dining & Amenities");
        title.setFont(new Font("Century Gothic", Font.BOLD, 15));
        title.setForeground(new Color(30, 41, 59));
        formCard.add(title);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

        Vector<String> catalog = CustomerDBA.getCatalogServices();
        JComboBox<String> cmbItem = new JComboBox<>(catalog);
        styleComboBox(cmbItem);

        JSpinner spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spinQty.setFont(new Font("Century Gothic", Font.PLAIN, 12));

        JTextField txtNotes = new JTextField("Deliver to Room " + currentGuest.activeRoomNo);
        txtNotes.setFont(new Font("Century Gothic", Font.PLAIN, 12));

        addGuestFormGroup(formCard, "Select Item / Service (From DB)", cmbItem);
        addGuestFormGroup(formCard, "Quantity", spinQty);
        addGuestFormGroup(formCard, "Delivery Instructions", txtNotes);

        JButton btnOrder = new JButton("Charge & Deliver to My Room");
        btnOrder.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnOrder.setBackground(new Color(99, 102, 241));
        btnOrder.setForeground(Color.WHITE);
        btnOrder.setMaximumSize(new Dimension(1400, 38));
        btnOrder.setFocusPainted(false);
        btnOrder.setBorderPainted(false);
        btnOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnOrder.addActionListener(e -> {
            if (cmbItem.getSelectedItem() == null) return;
            String sel = (String) cmbItem.getSelectedItem();
            int serviceId = Integer.parseInt(sel.split(" - ")[0]);
            int qty = (int) spinQty.getValue();
            boolean ok = CustomerDBA.placeRoomServiceOrder(currentGuest.guestId, serviceId, qty, txtNotes.getText().trim());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Order received! Room service is preparing your order.");
                refreshCustomerServiceTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to place order. Active booking required.", "Order Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(btnOrder);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel tblTitle = new JLabel("My Room Service Order History (Database)");
        tblTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tblTitle.setForeground(new Color(30, 41, 59));
        tblTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        String[] cols = {"Order ID", "Item", "Qty", "Amount", "Time", "Status"};
        modelRoomService = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(modelRoomService);
        table.setRowHeight(38);
        table.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        table.getColumnModel().getColumn(5).setCellRenderer(new GuestStatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        tableCard.add(tblTitle, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        panel.add(formCard, BorderLayout.WEST);
        panel.add(tableCard, BorderLayout.CENTER);

        refreshCustomerServiceTable();
        return panel;
    }

    private void refreshCustomerServiceTable() {
        if (modelRoomService == null) return;
        modelRoomService.setRowCount(0);
        Vector<Vector<Object>> data = CustomerDBA.getGuestServiceOrders(currentGuest.guestId);
        for (Vector<Object> row : data) {
            modelRoomService.addRow(row);
        }
    }

    private JPanel createHousekeepingRequestPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setMaximumSize(new Dimension(1400, 300));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formTitle = new JLabel("Request Room Cleaning & Amenities (Active Room: " + currentGuest.activeRoomNo + ")");
        formTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        formTitle.setForeground(new Color(30, 41, 59));
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

        JComboBox<String> cmbType = new JComboBox<>(new String[]{
                "Full Room Cleanup & Sanitization",
                "Fresh Towels & Toiletries",
                "Bed Linen & Pillow Change",
                "Do Not Disturb / Skip Cleaning Today"
        });
        styleComboBox(cmbType);

        JTextField txtTime = new JTextField("Today at 02:00 PM");
        txtTime.setFont(new Font("Century Gothic", Font.PLAIN, 12));

        addGuestFormGroup(formCard, "Select Cleaning / Amenity Service", cmbType);
        addGuestFormGroup(formCard, "Preferred Time Slot", txtTime);

        JButton btnSubmit = new JButton("Submit Housekeeping Request");
        btnSubmit.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnSubmit.setBackground(new Color(16, 185, 129));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setMaximumSize(new Dimension(1400, 38));
        btnSubmit.setFocusPainted(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(e -> {
            String type = (String) cmbType.getSelectedItem();
            boolean ok = CustomerDBA.placeHousekeepingRequest(currentGuest.guestId, type, txtTime.getText().trim());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Housekeeping request saved in database.");
            } else {
                JOptionPane.showMessageDialog(this, "Error saving housekeeping request.");
            }
        });

        formCard.add(btnSubmit);
        panel.add(formCard);

        return panel;
    }

    private JPanel createMyBillPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel billCard = new JPanel();
        billCard.setLayout(new BoxLayout(billCard, BoxLayout.Y_AXIS));
        billCard.setBackground(Color.WHITE);
        billCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        billCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));
        billCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Live Folio Balance & Checkout Summary (Database)");
        title.setFont(new Font("Century Gothic", Font.BOLD, 16));
        title.setForeground(new Color(30, 41, 59));
        billCard.add(title);
        billCard.add(Box.createRigidArea(new Dimension(0, 14)));

        lblBillRoomDetails = new JLabel("Room Accommodation Charges");
        lblBillRoomDetails.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        lblBillRoomDetails.setForeground(new Color(51, 65, 85));

        lblBillRoomCharges = new JLabel("0 MMK");
        lblBillRoomCharges.setFont(new Font("Century Gothic", Font.BOLD, 13));

        lblBillServiceCharges = new JLabel("0 MMK");
        lblBillServiceCharges.setFont(new Font("Century Gothic", Font.BOLD, 13));

        lblBillTax = new JLabel("0 MMK");
        lblBillTax.setFont(new Font("Century Gothic", Font.BOLD, 13));

        lblBillTotalNet = new JLabel("0 MMK");
        lblBillTotalNet.setFont(new Font("Century Gothic", Font.BOLD, 16));
        lblBillTotalNet.setForeground(new Color(79, 70, 229));

        billCard.add(createBillComponentRow(lblBillRoomDetails, lblBillRoomCharges));
        billCard.add(createBillComponentRow(new JLabel("Room Service & Dining Charges"), lblBillServiceCharges));
        billCard.add(createBillComponentRow(new JLabel("Commercial Tax & Service (5%)"), lblBillTax));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        billCard.add(sep);
        billCard.add(Box.createRigidArea(new Dimension(0, 10)));

        billCard.add(createBillComponentRow(new JLabel("<html><b>Total Net Payable</b></html>"), lblBillTotalNet));

        JPanel paymentModeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        paymentModeRow.setOpaque(false);
        paymentModeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        paymentModeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cmbPaymentMethod = new JComboBox<>(new String[]{"KBZPay", "WavePay", "Credit Card", "Cash", "Bank Transfer"});
        styleComboBox(cmbPaymentMethod);

        btnPayFolio = new JButton("💳 Pay & Settle Folio (+ Points Awarded)");
        btnPayFolio.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnPayFolio.setBackground(new Color(16, 185, 129));
        btnPayFolio.setForeground(Color.WHITE);
        btnPayFolio.setFocusPainted(false);
        btnPayFolio.setBorderPainted(false);
        btnPayFolio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPayFolio.setPreferredSize(new Dimension(280, 34));

        btnPayFolio.addActionListener(e -> {
            String method = (String) cmbPaymentMethod.getSelectedItem();
            boolean ok = CustomerDBA.settleCustomerPaymentAndAwardPoints(currentGuest.guestId, method);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Payment Settled Successfully!\nLoyalty Points have been credited to your VIP account.", "Payment Completed", JOptionPane.INFORMATION_MESSAGE);
                refreshCustomerSession();
            } else {
                JOptionPane.showMessageDialog(this, "No active unpaid room stay found to settle.", "Settlement Notice", JOptionPane.WARNING_MESSAGE);
            }
        });

        paymentModeRow.add(new JLabel("Pay Via:"));
        paymentModeRow.add(cmbPaymentMethod);
        paymentModeRow.add(btnPayFolio);

        billCard.add(Box.createRigidArea(new Dimension(0, 14)));
        billCard.add(paymentModeRow);

        panel.add(billCard);
        refreshCustomerBill();
        return panel;
    }

    private JPanel createBillComponentRow(JLabel label, JLabel val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(label, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    public void refreshCustomerBill() {
        if (lblBillRoomDetails == null || lblBillRoomCharges == null ||
                lblBillServiceCharges == null || lblBillTax == null ||
                lblBillTotalNet == null || btnPayFolio == null) {
            return;
        }

        CustomerDBA.LiveCustomerBill bill = CustomerDBA.getLiveCustomerBill(currentGuest.guestId);
        lblBillRoomDetails.setText(bill.roomDetails);
        lblBillRoomCharges.setText(String.format("%,d MMK", bill.roomCharges.longValue()));
        lblBillServiceCharges.setText(String.format("%,d MMK", bill.serviceCharges.longValue()));
        lblBillTax.setText(String.format("%,d MMK", bill.taxAmount.longValue()));
        lblBillTotalNet.setText(String.format("%,d MMK", bill.netPayable.longValue()));
        btnPayFolio.setEnabled(bill.hasActiveStay && bill.netPayable.compareTo(BigDecimal.ZERO) > 0);
    }

    public void refreshCustomerSession() {
        currentGuest = CustomerDBA.getGuestProfile(User_UI.getUname());

        lblGuestTopName.setText(currentGuest.fullName);
        String roomText = currentGuest.activeRoomNo.equals("None") ? "No Active Check-in" : "Room " + currentGuest.activeRoomNo;
        lblGuestTopBadge.setText(roomText + " • " + currentGuest.vipTier);

        if (lblLoyaltyPointsBadge != null) {
            lblLoyaltyPointsBadge.setText(String.format("%,d Points Available", currentGuest.loyaltyPoints));
            lblLoyaltyTierBadge.setText("⭐ " + currentGuest.vipTier + " MEMBER");
            tierProgressBar.setValue(Math.min(currentGuest.loyaltyPoints, 5000));
        }

        if (modelLoyaltyHistory != null) {
            modelLoyaltyHistory.setRowCount(0);
            Vector<Vector<Object>> hist = CustomerDBA.getLoyaltyHistory(currentGuest.guestId);
            for (Vector<Object> r : hist) modelLoyaltyHistory.addRow(r);
        }

        reloadDynamicPerks();
        refreshCustomerBill();
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel profileCard = new JPanel();
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setBackground(Color.WHITE);
        profileCard.setMaximumSize(new Dimension(1400, 520));
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(24, 28, 24, 28)
        ));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header with Action Buttons
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Guest Account & Identity Overview");
        title.setFont(new Font("Century Gothic", Font.BOLD, 17));
        title.setForeground(new Color(30, 41, 59));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnChangePass = new JButton("🔑 Change Password");
        btnChangePass.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btnChangePass.setBackground(new Color(241, 245, 249));
        btnChangePass.setForeground(new Color(51, 65, 85));
        btnChangePass.setFocusPainted(false);
        btnChangePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangePass.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnChangePass.addActionListener(e -> openChangePasswordDialog());

        JButton btnEditProfile = new JButton("✏️ Edit Profile");
        btnEditProfile.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btnEditProfile.setBackground(new Color(99, 102, 241));
        btnEditProfile.setForeground(Color.WHITE);
        btnEditProfile.setFocusPainted(false);
        btnEditProfile.setBorderPainted(false);
        btnEditProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditProfile.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btnEditProfile.addActionListener(e -> openEditProfileDialog());

        actions.add(btnChangePass);
        actions.add(btnEditProfile);

        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(actions, BorderLayout.EAST);

        profileCard.add(headerRow);
        profileCard.add(Box.createRigidArea(new Dimension(0, 16)));
        profileCard.add(new JSeparator());
        profileCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // Read-only Details Grid
        lblValName = createProfileValueLabel(currentGuest.fullName);
        lblValPhone = createProfileValueLabel(currentGuest.phone);
        lblValEmail = createProfileValueLabel(currentGuest.email);
        lblValCity = createProfileValueLabel(currentGuest.city);
        lblValNid = createProfileValueLabel(currentGuest.nidPassport);
        lblValPref = createProfileValueLabel(currentGuest.preferences.isEmpty() ? "None recorded" : currentGuest.preferences);

        addProfileFieldView(profileCard, "Full Name", lblValName);
        addProfileFieldView(profileCard, "Phone Number", lblValPhone);
        addProfileFieldView(profileCard, "Email Address", lblValEmail);
        addProfileFieldView(profileCard, "City / Location", lblValCity);
        addProfileFieldView(profileCard, "NRC / Passport ID", lblValNid);
        addProfileFieldView(profileCard, "Personal Notes & Preferences", lblValPref);

        panel.add(profileCard);
        return panel;
    }

    private void addProfileFieldView(JPanel parent, String title, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setBorder(new EmptyBorder(4, 0, 4, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 116, 139));
        lblTitle.setPreferredSize(new Dimension(240, 24));

        row.add(lblTitle, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);

        parent.add(row);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    private JLabel createProfileValueLabel(String text) {
        JLabel label = new JLabel(text != null && !text.isEmpty() ? text : "—");
        label.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        label.setForeground(new Color(15, 23, 42));
        return label;
    }

    private void openEditProfileDialog() {
        JDialog dialog = new JDialog(this, "Edit Guest Profile", true);
        dialog.setSize(440, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel heading = new JLabel("Update Your Information");
        heading.setFont(new Font("Century Gothic", Font.BOLD, 15));
        heading.setForeground(new Color(30, 41, 59));
        form.add(heading);
        form.add(Box.createRigidArea(new Dimension(0, 14)));

        JTextField txtName = createStyledTextField();
        txtName.setText(currentGuest.fullName);

        JTextField txtPhone = createStyledTextField();
        txtPhone.setText(currentGuest.phone);

        JTextField txtEmail = createStyledTextField();
        txtEmail.setText(currentGuest.email);

        JTextField txtCity = createStyledTextField();
        txtCity.setText(currentGuest.city);

        JTextField txtNid = createStyledTextField();
        txtNid.setText(currentGuest.nidPassport);

        JTextField txtPref = createStyledTextField();
        txtPref.setText(currentGuest.preferences);

        addGuestFormGroup(form, "Full Name", txtName);
        addGuestFormGroup(form, "Phone Number", txtPhone);
        addGuestFormGroup(form, "Email Address", txtEmail);
        addGuestFormGroup(form, "City", txtCity);
        addGuestFormGroup(form, "NRC / Passport ID", txtNid);
        addGuestFormGroup(form, "Personal Preferences", txtPref);

        JButton btnSave = new JButton("Save Changes");
        btnSave.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnSave.setBackground(new Color(16, 185, 129));
        btnSave.setForeground(Color.WHITE);
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            boolean ok = CustomerDBA.updateGuestProfile(
                    currentGuest.guestId,
                    txtName.getText().trim(),
                    txtPhone.getText().trim(),
                    txtEmail.getText().trim(),
                    txtCity.getText().trim(),
                    txtNid.getText().trim(),
                    txtPref.getText().trim()
            );

            if (ok) {
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCustomerSession();
                updateProfileLabels();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void openChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Security - Change Password", true);
        dialog.setSize(380, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel heading = new JLabel("Update Account Password");
        heading.setFont(new Font("Century Gothic", Font.BOLD, 15));
        heading.setForeground(new Color(30, 41, 59));
        form.add(heading);
        form.add(Box.createRigidArea(new Dimension(0, 14)));

        JPasswordField txtCurrent = new JPasswordField();
        stylePasswordField(txtCurrent);

        JPasswordField txtNew = new JPasswordField();
        stylePasswordField(txtNew);

        JPasswordField txtConfirm = new JPasswordField();
        stylePasswordField(txtConfirm);

        addGuestFormGroup(form, "Current Password", txtCurrent);
        addGuestFormGroup(form, "New Password", txtNew);
        addGuestFormGroup(form, "Confirm New Password", txtConfirm);

        JButton btnUpdate = new JButton("Change Password");
        btnUpdate.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnUpdate.setBackground(new Color(99, 102, 241));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnUpdate.addActionListener(e -> {
            String curr = new String(txtCurrent.getPassword()).trim();
            String nPass = new String(txtNew.getPassword()).trim();
            String cPass = new String(txtConfirm.getPassword()).trim();

            if (curr.isEmpty() || nPass.isEmpty() || cPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all password fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!nPass.equals(cPass)) {
                JOptionPane.showMessageDialog(dialog, "New passwords do not match.", "Password Mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = CustomerDBA.changeCustomerPassword(currentGuest.guestId, curr, nPass);
            if (ok) {
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Security Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Incorrect current password. Please try again.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(btnUpdate);

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void updateProfileLabels() {
        if (lblValName != null) lblValName.setText(currentGuest.fullName);
        if (lblValPhone != null) lblValPhone.setText(currentGuest.phone);
        if (lblValEmail != null) lblValEmail.setText(currentGuest.email.isEmpty() ? "—" : currentGuest.email);
        if (lblValCity != null) lblValCity.setText(currentGuest.city);
        if (lblValNid != null) lblValNid.setText(currentGuest.nidPassport);
        if (lblValPref != null) lblValPref.setText(currentGuest.preferences.isEmpty() ? "None recorded" : currentGuest.preferences);
    }

    private void stylePasswordField(JPasswordField pf) {
        pf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private void addGuestFormGroup(JPanel parent, String labelText, JComponent input) {
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

    private void addBillRow(JPanel parent, String label, String amount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(1400, 26));

        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        lblName.setForeground(new Color(51, 65, 85));

        JLabel lblVal = new JLabel(amount);
        lblVal.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblVal.setForeground(new Color(30, 41, 59));

        row.add(lblName, BorderLayout.WEST);
        row.add(lblVal, BorderLayout.EAST);

        parent.add(row);
        parent.add(Box.createRigidArea(new Dimension(0, 6)));
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        box.setBackground(Color.WHITE);
        box.setMaximumSize(new Dimension(1400, 32));
    }

    static class RoomImageCarousel extends JPanel {
        private final String[] paths;
        private final String fallbackText;
        private int currentIndex = 0;

        public RoomImageCarousel(String[] paths, String fallbackText) {
            this.paths = paths;
            this.fallbackText = fallbackText;
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(1400, 115));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            Image currentImg = null;
            if (paths != null && paths.length > 0 && currentIndex < paths.length) {
                String path = paths[currentIndex];
                if (path != null && !path.trim().isEmpty()) {
                    try {
                        if (path.startsWith("http://") || path.startsWith("https://")) {
                            currentImg = new ImageIcon(new java.net.URI(path).toURL()).getImage();
                        } else {
                            java.io.File file = new java.io.File(path);
                            if (file.exists() && file.isFile()) {
                                currentImg = new ImageIcon(file.getAbsolutePath()).getImage();
                            } else {
                                URL url = getClass().getResource(path);
                                if (url != null) {
                                    currentImg = new ImageIcon(url).getImage();
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }

            Shape clipShape = new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 10, 10);
            g2.setClip(clipShape);

            if (currentImg != null) {
                int imgW = currentImg.getWidth(null);
                int imgH = currentImg.getHeight(null);

                if (imgW > 0 && imgH > 0) {
                    double scale = Math.max((double) w / imgW, (double) h / imgH);
                    int drawW = (int) Math.round(imgW * scale);
                    int drawH = (int) Math.round(imgH * scale);
                    int drawX = (w - drawW) / 2;
                    int drawY = (h - drawH) / 2;

                    g2.drawImage(currentImg, drawX, drawY, drawW, drawH, this);
                } else {
                    g2.drawImage(currentImg, 0, 0, w, h, this);
                }
            } else {
                GradientPaint gp = new GradientPaint(0, 0, new Color(71, 85, 105), w, h, new Color(30, 41, 59));
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(255, 255, 255, 210));
                g2.setFont(new Font("Century Gothic", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "📷 " + fallbackText + " (Photo " + (currentIndex + 1) + ")";
                int tx = (w - fm.stringWidth(txt)) / 2;
                int ty = ((h - fm.getHeight()) / 2) + fm.getAscent() + 6;
                g2.drawString(txt, tx, ty);
            }

            g2.setClip(null);
            g2.setColor(new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new java.awt.geom.RoundRectangle2D.Double(0, 0, w - 1, h - 1, 10, 10));

            g2.dispose();
        }
    }

    static class GuestStatusBadgeRenderer extends DefaultTableCellRenderer {

        public GuestStatusBadgeRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Century Gothic", Font.BOLD, 10));
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
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
                case "DELIVERED":
                case "COMPLETED":
                case "CREDITED":
                    bg = new Color(16, 185, 129);
                    break;
                case "PREPARING":
                    bg = new Color(245, 158, 11);
                    break;
                case "REDEEMED":
                    bg = new Color(168, 85, 247);
                    break;
                default:
                    bg = new Color(100, 116, 139);
                    break;
            }

            int padX = 16;
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

    static class CircularAvatar extends JComponent {
        private Image avatarImage;
        private final String fallbackInitials;

        public CircularAvatar(String resourcePath, String fallbackInitials, int diameter) {
            this.fallbackInitials = fallbackInitials;
            setPreferredSize(new Dimension(diameter, diameter));
            setMaximumSize(new Dimension(diameter, diameter));
            setMinimumSize(new Dimension(diameter, diameter));

            try {
                URL url = getClass().getResource(resourcePath);
                if (url != null) {
                    this.avatarImage = new ImageIcon(url).getImage();
                }
            } catch (Exception ignored) {}
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int size = Math.min(getWidth(), getHeight());
            Shape circleShape = new Ellipse2D.Double(1, 1, size - 2, size - 2);

            if (avatarImage != null) {
                g2.setClip(circleShape);
                g2.drawImage(avatarImage, 1, 1, size - 2, size - 2, this);
                g2.setClip(null);
            } else {
                g2.setColor(new Color(238, 242, 255));
                g2.fill(circleShape);

                g2.setFont(new Font("Century Gothic", Font.BOLD, 12));
                g2.setColor(new Color(79, 70, 229));
                FontMetrics fm = g2.getFontMetrics();
                int x = (size - fm.stringWidth(fallbackInitials)) / 2;
                int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(fallbackInitials, x, y);
            }

            g2.setColor(new Color(199, 210, 254));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new Ellipse2D.Double(1, 1, size - 2, size - 2));
            g2.dispose();
        }
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
}*/

package view;

import model.CustomerDBA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

public class Customer_Screen extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCardsPanel = new JPanel(cardLayout);
    private DefaultTableModel modelRoomService;

    private CustomerDBA.GuestProfile currentGuest;
    private JLabel lblGuestTopName, lblGuestTopBadge;
    private CircularAvatar topAvatar;
    private JLabel lblBillRoomCharges, lblBillServiceCharges, lblBillTax, lblBillTotalNet;
    private JLabel lblBillRoomDetails;
    private JButton btnPayFolio;
    private DefaultTableModel modelLoyaltyHistory;
    private JLabel lblLoyaltyPointsBadge, lblLoyaltyTierBadge;
    private JProgressBar tierProgressBar;
    private DefaultTableModel modelMyReservations;
    private JPanel pnlVouchersList;
    private JLabel lblValName, lblValPhone, lblValEmail, lblValCity, lblValNid, lblValPref;
    private DefaultTableModel modelHousekeeping;
    private DefaultTableModel modelFolioLedger;

    public Customer_Screen() {
        this.currentGuest = CustomerDBA.getGuestProfile(User_UI.getUname());

        setTitle("Grand Horizon Suites — Guest Experience Portal");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        add(createCustomerSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(new Color(248, 250, 252));
        mainArea.add(createCustomerTopBar(), BorderLayout.NORTH);

        contentCardsPanel.setBackground(new Color(248, 250, 252));
        contentCardsPanel.add(createExploreAndBookPanel(), "EXPLORE_BOOK");
        contentCardsPanel.add(createMyReservationsPanel(), "MY_RESERVATIONS");
        contentCardsPanel.add(createLoyaltyRewardsPanel(), "LOYALTY_REWARDS");
        contentCardsPanel.add(createRoomServicePanel(), "ROOM_SERVICE");
        contentCardsPanel.add(createHousekeepingRequestPanel(), "HOUSEKEEPING");
        contentCardsPanel.add(createMyBillPanel(), "MY_BILL");
        contentCardsPanel.add(createProfilePanel(), "MY_PROFILE");

        util.AppIcon.setFrameIcon(this, "/images/favicon1.png");

        mainArea.add(contentCardsPanel, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        refreshCustomerSession();
    }

    private JPanel createCustomerSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        0, getHeight(), new Color(30, 41, 59)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Ambient gradient sphere in sidebar
                g2.setColor(new Color(99, 102, 241, 20));
                g2.fillOval(-40, getHeight() - 180, 220, 220);
                g2.dispose();
            }
        };

        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(28, 16, 24, 16));

        // Brand Title
        JPanel brandLogoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int panelW = getWidth();
                int panelH = getHeight();

                URL logoUrl = getClass().getResource("/images/logofinal.png");
                if (logoUrl != null) {
                    Image logo = new ImageIcon(logoUrl).getImage();
                    int logoW = 190;
                    int logoH = 80;
                    int x = (panelW - logoW) / 2;
                    int y = (panelH - logoH) / 2;

                    g2.drawImage(logo, x, y, logoW, logoH, this);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                    FontMetrics fm = g2.getFontMetrics();
                    String fallback = "🏨";
                    g2.drawString(fallback, (panelW - fm.stringWidth(fallback)) / 2, (panelH / 2) + 10);
                }
                g2.dispose();
            }
        };
        brandLogoPanel.setOpaque(false);
        brandLogoPanel.setPreferredSize(new Dimension(218, 74));
        brandLogoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        brandLogoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brandLogoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        String[][] customerNav = {
                {"🏨  Explore & Reserve", "EXPLORE_BOOK"},
                {"📅  My Reservations", "MY_RESERVATIONS"},
                {"⭐  VIP Rewards & Perks", "LOYALTY_REWARDS"},
                {"🛎️  In-Room Dining", "ROOM_SERVICE"},
                {"🧹  Housekeeping Dispatch", "HOUSEKEEPING"},
                {"💳  Live Folio & Payment", "MY_BILL"},
                {"👤  Guest Identity & Bio", "MY_PROFILE"}
        };

        for (int i = 0; i < customerNav.length; i++) {
            String label = customerNav[i][0];
            String cardKey = customerNav[i][1];
            sidebar.add(createModernNavButton(label, cardKey, i == 0));
            sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("🚪  Sign Out") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(239, 68, 68, 40));
                } else {
                    g2.setColor(new Color(239, 68, 68, 20));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(239, 68, 68, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btnLogout.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnLogout.setForeground(new Color(254, 202, 202));
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogout.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out of your session?", "Sign Out Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new User_UI().setVisible(true);
            }
        });

        sidebar.add(btnLogout);
        return sidebar;
    }

    private JButton createModernNavButton(String text, String cardKey, boolean isActive) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean active = getClientProperty("active") == Boolean.TRUE;
                if (active) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241), getWidth(), 0, new Color(139, 92, 246));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.putClientProperty("active", isActive);
        btn.setFont(new Font("Segoe UI Emoji", isActive ? Font.BOLD : Font.PLAIN, 12));
        btn.setForeground(isActive ? Color.WHITE : new Color(203, 213, 225));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> {
            cardLayout.show(contentCardsPanel, cardKey);
            Container parent = btn.getParent();
            for (Component c : parent.getComponents()) {
                if (c instanceof JButton && c != btn) {
                    ((JButton) c).putClientProperty("active", false);
                    c.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
                    c.setForeground(new Color(203, 213, 225));
                    c.repaint();
                }
            }
            btn.putClientProperty("active", true);
            btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
            btn.setForeground(Color.WHITE);
            btn.repaint();
        });

        return btn;
    }

    private JPanel createCustomerTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(12, 28, 12, 28)
        ));

        JPanel leftBox = new JPanel(new GridLayout(2, 1, 0, 2));
        leftBox.setOpaque(false);

        JLabel title = new JLabel("Welcome back to your luxury experience");
        title.setFont(new Font("Century Gothic", Font.BOLD, 16));
        title.setForeground(new Color(15, 23, 42));

        JLabel sub = new JLabel("Manage reservations, view instant folio statements and exclusive perks");
        sub.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        sub.setForeground(new Color(100, 116, 139));

        leftBox.add(title);
        leftBox.add(sub);

        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightSection.setOpaque(false);

        JPanel guestBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        guestBlock.setOpaque(false);

        lblGuestTopName = new JLabel(currentGuest.fullName, SwingConstants.RIGHT);
        lblGuestTopName.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblGuestTopName.setForeground(new Color(15, 23, 42));

        String roomText = currentGuest.activeRoomNo.equals("None") ? "No Active Check-in" : "Suite " + currentGuest.activeRoomNo;
        lblGuestTopBadge = new JLabel(roomText + " • " + currentGuest.vipTier, SwingConstants.RIGHT);
        lblGuestTopBadge.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblGuestTopBadge.setForeground(new Color(99, 102, 241));

        guestBlock.add(lblGuestTopName);
        guestBlock.add(lblGuestTopBadge);

        String initial = currentGuest.fullName.isEmpty() ? "G" : currentGuest.fullName.substring(0, 1).toUpperCase();
        topAvatar = new CircularAvatar("/images/profile.png", initial, 38);

        rightSection.add(guestBlock);
        rightSection.add(topAvatar);

        topBar.add(leftBox, BorderLayout.WEST);
        topBar.add(rightSection, BorderLayout.EAST);
        return topBar;
    }

    // State variables for active search filter
    private LocalDate searchCheckIn = LocalDate.now();
    private LocalDate searchCheckOut = LocalDate.now().plusDays(2);
    private int searchGuestCount = 2;
    private JPanel roomCardsGridContainer;

    private JPanel createExploreAndBookPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        // 1. Top Section: Header + Date & Guest Search Bar
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel sectionTitle = new JLabel("Explore Suites & Live Availability");
        sectionTitle.setFont(new Font("Century Gothic", Font.BOLD, 18));
        sectionTitle.setForeground(new Color(15, 23, 42));

        JLabel subTitle = new JLabel("Filter suites by scheduled dates to view units available for your itinerary");
        subTitle.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        subTitle.setForeground(new Color(100, 116, 139));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);
        titleBox.add(sectionTitle);
        titleBox.add(subTitle);
        headerPanel.add(titleBox, BorderLayout.WEST);

        topContainer.add(headerPanel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // Search Filter Bar Panel
        JPanel searchBarCard = createModernCardPanel();
        searchBarCard.setLayout(new BorderLayout(14, 0));
        searchBarCard.setBorder(new EmptyBorder(12, 18, 12, 18));
        searchBarCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JPanel filterInputs = new JPanel(new GridLayout(1, 3, 14, 0));
        filterInputs.setOpaque(false);

        // Check-in input
        JTextField txtFilterCheckIn = createStyledTextField();
        txtFilterCheckIn.setText(searchCheckIn.toString());
        JPanel pnlIn = createSearchFieldGroup("📅 CHECK-IN (YYYY-MM-DD)", txtFilterCheckIn);

        // Check-out input
        JTextField txtFilterCheckOut = createStyledTextField();
        txtFilterCheckOut.setText(searchCheckOut.toString());
        JPanel pnlOut = createSearchFieldGroup("📅 CHECK-OUT (YYYY-MM-DD)", txtFilterCheckOut);

        // Person / Guest chooser
        JComboBox<String> cmbGuests = new JComboBox<>(new String[]{
                "1 Adult (Solo Stay)",
                "2 Adults (Couple / Twin)",
                "3 Guests (Family Suite)",
                "4+ Guests (Executive / VIP)"
        });
        cmbGuests.setSelectedIndex(1); // Default to 2 adults
        styleComboBox(cmbGuests);
        JPanel pnlGuests = createSearchFieldGroup("👥 GUESTS & TRAVELERS", cmbGuests);

        filterInputs.add(pnlIn);
        filterInputs.add(pnlOut);
        filterInputs.add(pnlGuests);

        // Search Action Button
        JButton btnApplyFilter = new JButton("🔍 Find Available Suites") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(79, 70, 229), getWidth(), 0, new Color(67, 56, 202)));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(129, 140, 248), getWidth(), 0, new Color(99, 102, 241)));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241), getWidth(), 0, new Color(139, 92, 246)));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnApplyFilter.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnApplyFilter.setForeground(Color.WHITE);
        btnApplyFilter.setPreferredSize(new Dimension(190, 38));
        btnApplyFilter.setFocusPainted(false);
        btnApplyFilter.setBorderPainted(false);
        btnApplyFilter.setContentAreaFilled(false);
        btnApplyFilter.setOpaque(false);
        btnApplyFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnApplyFilter.addActionListener(e -> {
            try {
                LocalDate inDate = LocalDate.parse(txtFilterCheckIn.getText().trim());
                LocalDate outDate = LocalDate.parse(txtFilterCheckOut.getText().trim());

                if (!outDate.isAfter(inDate)) {
                    JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.", "Invalid Date Range", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                this.searchCheckIn = inDate;
                this.searchCheckOut = outDate;
                this.searchGuestCount = cmbGuests.getSelectedIndex() + 1;

                refreshExploreRoomGrid();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD format.", "Date Format Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        searchBarCard.add(filterInputs, BorderLayout.CENTER);
        searchBarCard.add(btnApplyFilter, BorderLayout.EAST);
        topContainer.add(searchBarCard);

        panel.add(topContainer, BorderLayout.NORTH);

        // 2. Room Cards Grid Container
        roomCardsGridContainer = new JPanel();
        roomCardsGridContainer.setOpaque(false);
        roomCardsGridContainer.setLayout(new BoxLayout(roomCardsGridContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(roomCardsGridContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Load initial availability
        refreshExploreRoomGrid();

        return panel;
    }

    private JPanel createSearchFieldGroup(String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 2));
        group.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 9));
        lbl.setForeground(new Color(100, 116, 139));

        input.setPreferredSize(new Dimension(input.getPreferredSize().width, 32));

        group.add(lbl, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);
        return group;
    }

    public void refreshExploreRoomGrid() {
        if (roomCardsGridContainer == null) return;
        roomCardsGridContainer.removeAll();

        List<CustomerDBA.RoomCardData> rooms = CustomerDBA.getAvailableRoomCardsForDates(searchCheckIn, searchCheckOut);

        if (rooms.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(60, 20, 60, 20));

            JLabel lblEmpty = new JLabel("🚫 No Suites Available for " + searchCheckIn + " to " + searchCheckOut);
            lblEmpty.setFont(new Font("Century Gothic", Font.BOLD, 15));
            lblEmpty.setForeground(new Color(71, 85, 105));
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblSub = new JLabel("All suites in our inventory are booked during these dates. Please choose alternative dates.");
            lblSub.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            lblSub.setForeground(new Color(148, 163, 184));
            lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(lblEmpty);
            emptyPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            emptyPanel.add(lblSub);

            roomCardsGridContainer.add(emptyPanel);
        } else {
            int cols = 3;
            int rows = Math.max(1, (int) Math.ceil(rooms.size() / 3.0));

            JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 18, 18));
            gridPanel.setOpaque(false);
            gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (CustomerDBA.RoomCardData r : rooms) {
                gridPanel.add(createRoomCard(
                        r.roomNo, r.title, r.floor, r.price, "Available for Dates", r.features, new Color(16, 185, 129), r.imagePaths
                ));
            }

            int remainder = rooms.size() % 3;
            if (remainder != 0) {
                int emptySlots = 3 - remainder;
                for (int i = 0; i < emptySlots; i++) {
                    JPanel placeholder = new JPanel();
                    placeholder.setOpaque(false);
                    gridPanel.add(placeholder);
                }
            }

            roomCardsGridContainer.add(gridPanel);
        }

        roomCardsGridContainer.revalidate();
        roomCardsGridContainer.repaint();
    }

    private JPanel createRoomCard(String roomNo, String title, String floor, String price, String tierBadge, String features, Color accentColor, String[] imagePaths) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(280, 285));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 285));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        RoomImageCarousel carousel = new RoomImageCarousel(imagePaths, roomNo + " (" + title + ")");
        carousel.setPreferredSize(new Dimension(260, 120));
        carousel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        carousel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel topRow = new JPanel(new BorderLayout(6, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRoom = new JLabel(roomNo + " • " + title);
        lblRoom.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblRoom.setForeground(new Color(15, 23, 42));

        JLabel lblBadge = new JLabel(tierBadge) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(16, 185, 129, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadge.setFont(new Font("Century Gothic", Font.BOLD, 9));
        lblBadge.setForeground(new Color(16, 185, 129));
        lblBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

        topRow.add(lblRoom, BorderLayout.WEST);
        topRow.add(lblBadge, BorderLayout.EAST);

        JLabel lblFloor = new JLabel("Floor: " + floor);
        lblFloor.setFont(new Font("Century Gothic", Font.PLAIN, 10));
        lblFloor.setForeground(new Color(148, 163, 184));
        lblFloor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFeatures = new JLabel("<html><font color='#94a3b8'>Perks: </font>" + features + "</html>");
        lblFeatures.setFont(new Font("Century Gothic", Font.PLAIN, 10));
        lblFeatures.setForeground(new Color(51, 65, 85));
        lblFeatures.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bottomRow = new JPanel(new BorderLayout(6, 0));
        bottomRow.setOpaque(false);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPrice = new JLabel(price);
        lblPrice.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblPrice.setForeground(new Color(15, 23, 42));

        JButton btnBook = new JButton("Reserve Suite") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(79, 70, 229), getWidth(), 0, new Color(67, 56, 202)));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(129, 140, 248), getWidth(), 0, new Color(99, 102, 241)));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241), getWidth(), 0, new Color(139, 92, 246)));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnBook.setFont(new Font("Century Gothic", Font.BOLD, 10));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setBorderPainted(false);
        btnBook.setContentAreaFilled(false);
        btnBook.setOpaque(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBook.setPreferredSize(new Dimension(110, 28));
        btnBook.addActionListener(e -> openBookingModal(roomNo, title, price));

        bottomRow.add(lblPrice, BorderLayout.WEST);
        bottomRow.add(btnBook, BorderLayout.EAST);

        card.add(carousel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(topRow);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(lblFloor);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblFeatures);
        card.add(Box.createVerticalGlue());
        card.add(bottomRow);

        return card;
    }

    private void openBookingModal(String roomNo, String roomTitle, String roomPrice) {
        JDialog dialog = new JDialog(this, "Reserve Your Stay", true);
        dialog.setSize(440, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(248, 250, 252));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JLabel lblH1 = new JLabel("Suite: " + roomNo + " (" + roomTitle + ")");
        lblH1.setFont(new Font("Century Gothic", Font.BOLD, 15));
        lblH1.setForeground(new Color(15, 23, 42));

        JLabel lblH2 = new JLabel("Tariff: " + roomPrice);
        lblH2.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblH2.setForeground(new Color(99, 102, 241));

        header.add(lblH1, BorderLayout.NORTH);
        header.add(lblH2, BorderLayout.SOUTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JTextField txtGuest = createStyledTextField();
        txtGuest.setText(currentGuest.fullName);
        txtGuest.setEditable(false);

        JTextField txtPhone = createStyledTextField();
        txtPhone.setText(currentGuest.phone);
        txtPhone.setEditable(false);

        // Uses the date filter values selected by the user
        JTextField txtCheckIn = createStyledTextField();
        txtCheckIn.setText(this.searchCheckIn.toString());

        JTextField txtCheckOut = createStyledTextField();
        txtCheckOut.setText(this.searchCheckOut.toString());

        JComboBox<String> cmbTier = new JComboBox<>(new String[]{
                "Staycation (Overnight)", "Daycation (Day Pass)", "Night Stay (Transit)"
        });
        styleComboBox(cmbTier);

        addGuestFormGroup(formPanel, "Guest Name", txtGuest);
        addGuestFormGroup(formPanel, "Contact Phone", txtPhone);
        addGuestFormGroup(formPanel, "Reservation Tier", cmbTier);

        JPanel dates = new JPanel(new GridLayout(1, 2, 10, 0));
        dates.setOpaque(false);
        dates.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        dates.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel d1 = new JPanel(new BorderLayout(0, 4));
        d1.setOpaque(false);
        JLabel lbl1 = new JLabel("Check-In Date");
        lbl1.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lbl1.setForeground(new Color(100, 116, 139));
        d1.add(lbl1, BorderLayout.NORTH);
        d1.add(txtCheckIn, BorderLayout.CENTER);

        JPanel d2 = new JPanel(new BorderLayout(0, 4));
        d2.setOpaque(false);
        JLabel lbl2 = new JLabel("Check-Out Date");
        lbl2.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lbl2.setForeground(new Color(100, 116, 139));
        d2.add(lbl2, BorderLayout.NORTH);
        d2.add(txtCheckOut, BorderLayout.CENTER);

        dates.add(d1);
        dates.add(d2);
        formPanel.add(dates);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton btnConfirm = new JButton("Confirm Reservation & Send Receipt") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(16, 185, 129), getWidth(), 0, new Color(5, 150, 105)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnConfirm.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setContentAreaFilled(false);
        btnConfirm.setOpaque(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnConfirm.addActionListener(e -> {
            try {
                LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim());
                LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim());
                String tier = (String) cmbTier.getSelectedItem();

                boolean success = CustomerDBA.createCustomerBooking(
                        currentGuest.guestId,
                        currentGuest.fullName,
                        currentGuest.email,
                        roomNo,
                        tier,
                        inDate,
                        outDate
                );

                if (success) {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Success! Reservation confirmed for " + roomNo + " (" + roomTitle + ").", "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
                    refreshCustomerSession();
                    refreshMyReservationsTable();
                    refreshExploreRoomGrid(); // Refresh grid so the newly booked room disappears from the date view
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit booking into the database.", "Booking Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format. Please use YYYY-MM-DD.", "Date Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        formPanel.add(btnConfirm);

        root.add(header, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);
        dialog.add(root);
        dialog.setVisible(true);
    }

    private JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel tableCard = createModernCardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = new JLabel("My Active & Past Room Bookings");
        title.setFont(new Font("Century Gothic", Font.BOLD, 16));
        title.setForeground(new Color(15, 23, 42));
        title.setBorder(new EmptyBorder(0, 0, 16, 0));

        String[] cols = {"Booking Ref", "Room", "Tier", "Check-In", "Check-Out", "Room Total", "Status"};
        modelMyReservations = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(modelMyReservations);
        table.setRowHeight(42);
        table.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 12));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        table.getColumnModel().getColumn(6).setCellRenderer(new GuestStatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        tableCard.add(title, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        refreshMyReservationsTable();
        return panel;
    }

    public void refreshMyReservationsTable() {
        if (modelMyReservations == null) return;
        modelMyReservations.setRowCount(0);
        Vector<Vector<Object>> data = CustomerDBA.getGuestReservations(currentGuest.guestId);
        for (Vector<Object> r : data) {
            modelMyReservations.addRow(r);
        }
    }

    private JPanel createLoyaltyRewardsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Top VIP Gradient Hero Banner
        JPanel tierBanner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(99, 102, 241),
                        getWidth(), 0, new Color(168, 85, 247)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        tierBanner.setLayout(new BorderLayout(20, 0));
        tierBanner.setBorder(new EmptyBorder(20, 24, 20, 24));
        tierBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        tierBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tierLeft = new JPanel();
        tierLeft.setLayout(new BoxLayout(tierLeft, BoxLayout.Y_AXIS));
        tierLeft.setOpaque(false);

        lblLoyaltyTierBadge = new JLabel("⭐ " + currentGuest.vipTier + " MEMBER");
        lblLoyaltyTierBadge.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        lblLoyaltyTierBadge.setForeground(new Color(254, 240, 138));

        lblLoyaltyPointsBadge = new JLabel(String.format("%,d Points Available", currentGuest.loyaltyPoints));
        lblLoyaltyPointsBadge.setFont(new Font("Century Gothic", Font.BOLD, 22));
        lblLoyaltyPointsBadge.setForeground(Color.WHITE);

        JLabel lblProgress = new JLabel("Earn 1 Point per 1,000 MMK on settled stays");
        lblProgress.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblProgress.setForeground(new Color(238, 242, 255));

        tierLeft.add(lblLoyaltyTierBadge);
        tierLeft.add(Box.createRigidArea(new Dimension(0, 4)));
        tierLeft.add(lblLoyaltyPointsBadge);
        tierLeft.add(Box.createRigidArea(new Dimension(0, 4)));
        tierLeft.add(lblProgress);

        JPanel tierRight = new JPanel(new GridLayout(2, 1, 0, 6));
        tierRight.setOpaque(false);

        JLabel lblStats = new JLabel("City: " + currentGuest.city + " | ID: " + currentGuest.guestId, SwingConstants.RIGHT);
        lblStats.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblStats.setForeground(Color.WHITE);

        tierProgressBar = new JProgressBar(0, 5000);
        tierProgressBar.setValue(Math.min(currentGuest.loyaltyPoints, 5000));
        tierProgressBar.setPreferredSize(new Dimension(220, 8));
        tierProgressBar.setForeground(new Color(254, 240, 138));
        tierProgressBar.setBackground(new Color(255, 255, 255, 60));
        tierProgressBar.setBorderPainted(false);

        tierRight.add(lblStats);
        tierRight.add(tierProgressBar);

        tierBanner.add(tierLeft, BorderLayout.WEST);
        tierBanner.add(tierRight, BorderLayout.EAST);

        // Middle Workspace
        JPanel middleRow = new JPanel(new BorderLayout(18, 0));
        middleRow.setOpaque(false);
        middleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dynamic Vouchers Card
        JPanel vouchersCard = createModernCardPanel();
        vouchersCard.setLayout(new BorderLayout());
        vouchersCard.setPreferredSize(new Dimension(430, 0));
        vouchersCard.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel vTitle = new JLabel("Redeem Loyalty Vouchers & Perks");
        vTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        vTitle.setForeground(new Color(15, 23, 42));
        vTitle.setBorder(new EmptyBorder(0, 0, 14, 0));
        vouchersCard.add(vTitle, BorderLayout.NORTH);

        pnlVouchersList = new JPanel();
        pnlVouchersList.setLayout(new BoxLayout(pnlVouchersList, BoxLayout.Y_AXIS));
        pnlVouchersList.setOpaque(false);

        JScrollPane voucherScroll = new JScrollPane(pnlVouchersList);
        voucherScroll.setBorder(null);
        voucherScroll.setOpaque(false);
        voucherScroll.getViewport().setOpaque(false);
        voucherScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        vouchersCard.add(voucherScroll, BorderLayout.CENTER);

        // Right Ledger Table
        JPanel historyCard = createModernCardPanel();
        historyCard.setLayout(new BorderLayout());
        historyCard.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel hTitle = new JLabel("Points Activity & Reward Ledger");
        hTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        hTitle.setForeground(new Color(15, 23, 42));
        hTitle.setBorder(new EmptyBorder(0, 0, 14, 0));

        String[] cols = {"Date", "Activity Description", "Reference", "Points", "Status"};
        modelLoyaltyHistory = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable hTable = new JTable(modelLoyaltyHistory);
        hTable.setRowHeight(38);
        hTable.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        hTable.setShowVerticalLines(false);
        hTable.setGridColor(new Color(241, 245, 249));

        JTableHeader th = hTable.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 12));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        hTable.getColumnModel().getColumn(4).setCellRenderer(new GuestStatusBadgeRenderer());

        JScrollPane hScroll = new JScrollPane(hTable);
        hScroll.setBorder(null);
        hScroll.getViewport().setBackground(Color.WHITE);

        historyCard.add(hTitle, BorderLayout.NORTH);
        historyCard.add(hScroll, BorderLayout.CENTER);

        middleRow.add(vouchersCard, BorderLayout.WEST);
        middleRow.add(historyCard, BorderLayout.CENTER);

        panel.add(tierBanner);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(middleRow);

        reloadDynamicPerks();
        return panel;
    }

    private void reloadDynamicPerks() {
        if (pnlVouchersList == null) return;
        pnlVouchersList.removeAll();

        List<CustomerDBA.LoyaltyPerkData> perks = CustomerDBA.getActiveLoyaltyPerks();
        if (perks.isEmpty()) {
            JLabel emptyLbl = new JLabel("No loyalty perks currently available.");
            emptyLbl.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            emptyLbl.setForeground(new Color(148, 163, 184));
            pnlVouchersList.add(emptyLbl);
        } else {
            for (CustomerDBA.LoyaltyPerkData perk : perks) {
                pnlVouchersList.add(createRewardItem(
                        perk.title,
                        String.format("Cost: %,d Points", perk.pointsCost),
                        perk.pointsCost
                ));
                pnlVouchersList.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }
        pnlVouchersList.revalidate();
        pnlVouchersList.repaint();
    }

    private JPanel createRewardItem(String title, String costStr, int cost) {
        JPanel item = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 252));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        item.setBorder(new EmptyBorder(10, 14, 10, 14));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblT.setForeground(new Color(15, 23, 42));

        JLabel lblC = new JLabel(costStr);
        lblC.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblC.setForeground(new Color(99, 102, 241));

        text.add(lblT);
        text.add(lblC);

        JButton btnRedeem = new JButton("Redeem") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isArmed()) {
                    g2.setColor(new Color(13, 148, 136));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(5, 150, 105));
                } else {
                    g2.setColor(new Color(16, 185, 129));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };

        btnRedeem.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnRedeem.setForeground(Color.WHITE);
        btnRedeem.setPreferredSize(new Dimension(84, 28));
        btnRedeem.setFocusPainted(false);
        btnRedeem.setBorderPainted(false);
        btnRedeem.setContentAreaFilled(false);
        btnRedeem.setOpaque(false);
        btnRedeem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRedeem.addActionListener(e -> {
            boolean ok = CustomerDBA.redeemLoyaltyPerk(currentGuest.guestId, title, cost);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Success! '" + title + "' redeemed for " + cost + " points.", "Reward Claimed", JOptionPane.INFORMATION_MESSAGE);
                refreshCustomerSession();
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient points. You have " + currentGuest.loyaltyPoints + " points.", "Redemption Failed", JOptionPane.WARNING_MESSAGE);
            }
        });

        item.add(text, BorderLayout.CENTER);
        item.add(btnRedeem, BorderLayout.EAST);
        return item;
    }

    private JPanel createRoomServicePanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel formCard = createModernCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(380, 0));
        formCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = new JLabel("In-Room Dining & Amenities");
        title.setFont(new Font("Century Gothic", Font.BOLD, 15));
        title.setForeground(new Color(15, 23, 42));
        formCard.add(title);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        Vector<String> catalog = CustomerDBA.getCatalogServices();
        JComboBox<String> cmbItem = new JComboBox<>(catalog);
        styleComboBox(cmbItem);

        JSpinner spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spinQty.setFont(new Font("Century Gothic", Font.PLAIN, 12));

        JTextField txtNotes = createStyledTextField();
        txtNotes.setText("Deliver to Suite " + currentGuest.activeRoomNo);

        addGuestFormGroup(formCard, "Select Service / Menu Item", cmbItem);
        addGuestFormGroup(formCard, "Order Quantity", spinQty);
        addGuestFormGroup(formCard, "Special Delivery Instructions", txtNotes);

        JButton btnOrder = new JButton("Charge & Dispatch Order") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241), getWidth(), 0, new Color(139, 92, 246)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnOrder.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnOrder.setForeground(Color.WHITE);
        btnOrder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnOrder.setFocusPainted(false);
        btnOrder.setBorderPainted(false);
        btnOrder.setContentAreaFilled(false);
        btnOrder.setOpaque(false);
        btnOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnOrder.addActionListener(e -> {
            if (cmbItem.getSelectedItem() == null) return;
            String sel = (String) cmbItem.getSelectedItem();
            int serviceId = Integer.parseInt(sel.split(" - ")[0]);
            int qty = (int) spinQty.getValue();
            boolean ok = CustomerDBA.placeRoomServiceOrder(currentGuest.guestId, serviceId, qty, txtNotes.getText().trim());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Order received! Room service is preparing your order.", "Order Dispatched", JOptionPane.INFORMATION_MESSAGE);
                refreshCustomerServiceTable();
                refreshCustomerBill();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to place order. Active booking required.", "Order Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(btnOrder);

        JPanel tableCard = createModernCardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel tblTitle = new JLabel("Order Status & Room Folio Entries");
        tblTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        tblTitle.setForeground(new Color(15, 23, 42));
        tblTitle.setBorder(new EmptyBorder(0, 0, 14, 0));

        String[] cols = {"Order ID", "Item", "Qty", "Amount", "Time", "Status"};
        modelRoomService = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(modelRoomService);
        table.setRowHeight(40);
        table.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 12));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        table.getColumnModel().getColumn(5).setCellRenderer(new GuestStatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        tableCard.add(tblTitle, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        panel.add(formCard, BorderLayout.WEST);
        panel.add(tableCard, BorderLayout.CENTER);

        refreshCustomerServiceTable();
        return panel;
    }

    private void refreshCustomerServiceTable() {
        if (modelRoomService == null) return;
        modelRoomService.setRowCount(0);
        Vector<Vector<Object>> data = CustomerDBA.getGuestServiceOrders(currentGuest.guestId);
        for (Vector<Object> row : data) {
            modelRoomService.addRow(row);
        }
    }

    private JPanel createHousekeepingRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Left Request Dispatch Console
        JPanel dispatchCard = createModernCardPanel();
        dispatchCard.setLayout(new BoxLayout(dispatchCard, BoxLayout.Y_AXIS));
        dispatchCard.setPreferredSize(new Dimension(420, 0));
        dispatchCard.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel lblTitle = new JLabel("Housekeeping Concierge");
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 17));
        lblTitle.setForeground(new Color(15, 23, 42));

        String activeRoomStr = currentGuest.activeRoomNo.equals("None") ? "No Active Stay" : "Suite " + currentGuest.activeRoomNo;
        JLabel lblSub = new JLabel("Assigned Unit: " + activeRoomStr);
        lblSub.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        lblSub.setForeground(new Color(99, 102, 241));

        dispatchCard.add(lblTitle);
        dispatchCard.add(Box.createRigidArea(new Dimension(0, 2)));
        dispatchCard.add(lblSub);
        dispatchCard.add(Box.createRigidArea(new Dimension(0, 16)));

        JLabel lblServiceHeading = new JLabel("SELECT SERVICE TYPE");
        lblServiceHeading.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lblServiceHeading.setForeground(new Color(100, 116, 139));
        dispatchCard.add(lblServiceHeading);
        dispatchCard.add(Box.createRigidArea(new Dimension(0, 8)));

        // Service Option Selection Cards
        ButtonGroup serviceGroup = new ButtonGroup();
        final String[] selectedService = {"Full Room Cleanup & Sanitization"};

        String[][] services = {
                {"Full Room Deep Clean", "Full Room Cleanup & Sanitization", "🧹", "Vacuum, linen sanitize & dusting"},
                {"Fresh Linen & Towels", "Fresh Towels & Luxury Toiletries", "🛁", "Extra towels, shampoo & bedsheets"},
                {"Evening Turndown", "Evening Turndown & Refresh", "🌙", "Bed prep, aromatherapy & trash clearing"},
                {"Do Not Disturb", "Do Not Disturb / Skip Cleaning Today", "🚫", "Pause housekeeping for today"}
        };

        JPanel serviceGrid = new JPanel(new GridLayout(4, 1, 0, 8));
        serviceGrid.setOpaque(false);
        serviceGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        for (int i = 0; i < services.length; i++) {
            String title = services[i][0];
            String fullValue = services[i][1];
            String icon = services[i][2];
            String desc = services[i][3];

            JToggleButton btnOpt = createServiceSelectCard(title, desc, icon);
            if (i == 0) {
                btnOpt.setSelected(true);
                selectedService[0] = fullValue;
            }

            btnOpt.addActionListener(e -> {
                selectedService[0] = fullValue;
                serviceGrid.repaint();
            });

            serviceGroup.add(btnOpt);
            serviceGrid.add(btnOpt);
        }
        dispatchCard.add(serviceGrid);
        dispatchCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // Time Slot Selection Pills
        JLabel lblTimeHeading = new JLabel("DISPATCH TIMING");
        lblTimeHeading.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lblTimeHeading.setForeground(new Color(100, 116, 139));
        dispatchCard.add(lblTimeHeading);
        dispatchCard.add(Box.createRigidArea(new Dimension(0, 8)));

        JTextField txtCustomTime = createStyledTextField();
        txtCustomTime.setText("Immediately (Next 15 Mins)");

        JPanel pillRow = new JPanel(new GridLayout(1, 3, 6, 0));
        pillRow.setOpaque(false);
        pillRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JButton btnNow = createQuickPill("⚡ Immediate", "Immediately (Next 15 Mins)", txtCustomTime);
        JButton btnAfternoon = createQuickPill("☀️ 02:00 PM", "Today at 02:00 PM", txtCustomTime);
        JButton btnEvening = createQuickPill("🌙 06:00 PM", "Today at 06:00 PM", txtCustomTime);

        pillRow.add(btnNow);
        pillRow.add(btnAfternoon);
        pillRow.add(btnEvening);

        dispatchCard.add(pillRow);
        dispatchCard.add(Box.createRigidArea(new Dimension(0, 8)));
        dispatchCard.add(txtCustomTime);
        dispatchCard.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnSubmit = new JButton("✨ Dispatch Concierge Request") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(5, 150, 105), getWidth(), 0, new Color(4, 120, 87)));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(52, 211, 153), getWidth(), 0, new Color(16, 185, 129)));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(16, 185, 129), getWidth(), 0, new Color(13, 148, 136)));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnSubmit.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSubmit.setFocusPainted(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setOpaque(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSubmit.addActionListener(e -> {
            if (currentGuest.activeRoomNo.equals("None")) {
                JOptionPane.showMessageDialog(this, "You need an active checked-in room stay to dispatch housekeeping.", "Active Stay Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String type = selectedService[0];
            String time = txtCustomTime.getText().trim();
            boolean ok = CustomerDBA.placeHousekeepingRequest(currentGuest.guestId, type, time);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Housekeeping request successfully dispatched for Suite " + currentGuest.activeRoomNo + "!", "Request Sent", JOptionPane.INFORMATION_MESSAGE);
                refreshHousekeepingTable();
            } else {
                JOptionPane.showMessageDialog(this, "Could not log request. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dispatchCard.add(btnSubmit);
        dispatchCard.add(Box.createVerticalGlue());

        // Right Activity Table Card
        JPanel historyCard = createModernCardPanel();
        historyCard.setLayout(new BorderLayout());
        historyCard.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel tblTitle = new JLabel("Live Request Dispatch Queue & Status");
        tblTitle.setFont(new Font("Century Gothic", Font.BOLD, 16));
        tblTitle.setForeground(new Color(15, 23, 42));
        tblTitle.setBorder(new EmptyBorder(0, 0, 16, 0));

        String[] cols = {"Ref", "Service Request", "Preferred Schedule", "Logged At", "Status"};
        modelHousekeeping = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(modelHousekeeping);
        table.setRowHeight(42);
        table.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 12));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        table.getColumnModel().getColumn(4).setCellRenderer(new GuestStatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        historyCard.add(tblTitle, BorderLayout.NORTH);
        historyCard.add(scroll, BorderLayout.CENTER);

        panel.add(dispatchCard, BorderLayout.WEST);
        panel.add(historyCard, BorderLayout.CENTER);

        refreshHousekeepingTable();
        return panel;
    }

    private JToggleButton createServiceSelectCard(String title, String desc, String icon) {
        JToggleButton btn = new JToggleButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean sel = isSelected();
                int w = getWidth();
                int h = getHeight();

                if (sel) {
                    g2.setColor(new Color(99, 102, 241, 20));
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                    g2.setColor(new Color(99, 102, 241));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 10, 10));
                } else {
                    g2.setColor(new Color(248, 250, 252));
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                    g2.setColor(new Color(226, 232, 240));
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 10, 10));
                }

                // Icon
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                g2.drawString(icon, 12, 28);

                // Title & Subtitle
                g2.setFont(new Font("Century Gothic", Font.BOLD, 12));
                g2.setColor(sel ? new Color(99, 102, 241) : new Color(30, 41, 59));
                g2.drawString(title, 40, 18);

                g2.setFont(new Font("Century Gothic", Font.PLAIN, 10));
                g2.setColor(new Color(100, 116, 139));
                g2.drawString(desc, 40, 34);

                // Right Check Circle
                int rx = w - 24;
                int ry = (h - 14) / 2;
                if (sel) {
                    g2.setColor(new Color(99, 102, 241));
                    g2.fillOval(rx, ry, 14, 14);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(rx + 4, ry + 4, 6, 6);
                } else {
                    g2.setColor(new Color(203, 213, 225));
                    g2.drawOval(rx, ry, 14, 14);
                }

                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(380, 48));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createQuickPill(String label, String timeVal, JTextField targetField) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 10));
        btn.setForeground(new Color(71, 85, 105));
        btn.setBackground(new Color(241, 245, 249));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));
        btn.addActionListener(e -> targetField.setText(timeVal));
        return btn;
    }

    public void refreshHousekeepingTable() {
        if (modelHousekeeping == null) return;
        modelHousekeeping.setRowCount(0);
        Vector<Vector<Object>> data = CustomerDBA.getGuestHousekeepingHistory(currentGuest.guestId);
        for (Vector<Object> r : data) {
            modelHousekeeping.addRow(r);
        }
    }

    private JPanel createMyBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Left Summary Card
        JPanel summaryCard = createModernCardPanel();
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
        summaryCard.setPreferredSize(new Dimension(440, 0));
        summaryCard.setBorder(new EmptyBorder(24, 26, 24, 26));

        // 1. Header Section (Left Aligned)
        JLabel title = new JLabel("Live Folio Balance & Checkout");
        title.setFont(new Font("Century Gothic", Font.BOLD, 18));
        title.setForeground(new Color(15, 23, 42));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        String activeRoomStr = currentGuest.activeRoomNo.equals("None") ? "No Active Checked-In Stay" : "Unit: Suite " + currentGuest.activeRoomNo;
        JLabel sub = new JLabel(activeRoomStr);
        sub.setFont(new Font("Century Gothic", Font.BOLD, 12));
        sub.setForeground(new Color(99, 102, 241));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryCard.add(title);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 4)));
        summaryCard.add(sub);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 22)));

        // 2. Itemized Breakdown Rows
        lblBillRoomDetails = new JLabel("Room Accommodation Charges");
        lblBillRoomDetails.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        lblBillRoomDetails.setForeground(new Color(71, 85, 105));

        lblBillRoomCharges = new JLabel("0 MMK");
        lblBillRoomCharges.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblBillRoomCharges.setForeground(new Color(15, 23, 42));

        lblBillServiceCharges = new JLabel("0 MMK");
        lblBillServiceCharges.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblBillServiceCharges.setForeground(new Color(15, 23, 42));

        lblBillTax = new JLabel("0 MMK");
        lblBillTax.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblBillTax.setForeground(new Color(15, 23, 42));

        JLabel lblServiceTitle = new JLabel("Room Service & Dining Charges");
        lblServiceTitle.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        lblServiceTitle.setForeground(new Color(71, 85, 105));

        JLabel lblTaxTitle = new JLabel("Commercial Tax & Service (5%)");
        lblTaxTitle.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        lblTaxTitle.setForeground(new Color(71, 85, 105));

        summaryCard.add(createBillComponentRow(lblBillRoomDetails, lblBillRoomCharges));
        summaryCard.add(Box.createRigidArea(new Dimension(0, 10)));
        summaryCard.add(createBillComponentRow(lblServiceTitle, lblBillServiceCharges));
        summaryCard.add(Box.createRigidArea(new Dimension(0, 10)));
        summaryCard.add(createBillComponentRow(lblTaxTitle, lblBillTax));
        summaryCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // 3. Divider
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(226, 232, 240));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryCard.add(sep);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // 4. Total Net Row
        JLabel lblTotalNetTitle = new JLabel("Total Net Payable");
        lblTotalNetTitle.setFont(new Font("Century Gothic", Font.BOLD, 15));
        lblTotalNetTitle.setForeground(new Color(15, 23, 42));

        lblBillTotalNet = new JLabel("0 MMK");
        lblBillTotalNet.setFont(new Font("Century Gothic", Font.BOLD, 20));
        lblBillTotalNet.setForeground(new Color(99, 102, 241));

        summaryCard.add(createBillComponentRow(lblTotalNetTitle, lblBillTotalNet));
        summaryCard.add(Box.createRigidArea(new Dimension(0, 26)));

        // 5. Payment Selection
        JLabel lblPayHeading = new JLabel("SELECT PAYMENT METHOD");
        lblPayHeading.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblPayHeading.setForeground(new Color(100, 116, 139));
        lblPayHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cmbPaymentMethod = new JComboBox<>(new String[]{"KBZPay", "WavePay", "Credit Card", "Cash", "Bank Transfer"});
        styleComboBox(cmbPaymentMethod);
        cmbPaymentMethod.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbPaymentMethod.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnPayFolio = new JButton("💳 Settle Folio (+ Earn Loyalty Points)") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isEnabled()) {
                    if (getModel().isArmed()) {
                        g2.setPaint(new GradientPaint(0, 0, new Color(5, 150, 105), getWidth(), 0, new Color(4, 120, 87)));
                    } else if (getModel().isRollover()) {
                        g2.setPaint(new GradientPaint(0, 0, new Color(52, 211, 153), getWidth(), 0, new Color(16, 185, 129)));
                    } else {
                        g2.setPaint(new GradientPaint(0, 0, new Color(16, 185, 129), getWidth(), 0, new Color(13, 148, 136)));
                    }
                } else {
                    g2.setColor(new Color(226, 232, 240));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(isEnabled() ? Color.WHITE : new Color(148, 163, 184));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnPayFolio.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnPayFolio.setForeground(Color.WHITE);
        btnPayFolio.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPayFolio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnPayFolio.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        btnPayFolio.setFocusPainted(false);
        btnPayFolio.setBorderPainted(false);
        btnPayFolio.setContentAreaFilled(false);
        btnPayFolio.setOpaque(false);
        btnPayFolio.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPayFolio.addActionListener(e -> {
            String method = (String) cmbPaymentMethod.getSelectedItem();
            boolean ok = CustomerDBA.settleCustomerPaymentAndAwardPoints(currentGuest.guestId, method);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Payment Settled Successfully!\nLoyalty Points have been credited to your VIP account.", "Payment Completed", JOptionPane.INFORMATION_MESSAGE);
                refreshCustomerSession();
            } else {
                JOptionPane.showMessageDialog(this, "No active unpaid room stay found to settle.", "Settlement Notice", JOptionPane.WARNING_MESSAGE);
            }
        });

        summaryCard.add(lblPayHeading);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 8)));
        summaryCard.add(cmbPaymentMethod);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 14)));
        summaryCard.add(btnPayFolio);
        summaryCard.add(Box.createVerticalGlue());

        // Right Ledger Table Card
        JPanel ledgerCard = createModernCardPanel();
        ledgerCard.setLayout(new BorderLayout());
        ledgerCard.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel tblTitle = new JLabel("Itemized Folio Transactions & In-Room Orders");
        tblTitle.setFont(new Font("Century Gothic", Font.BOLD, 16));
        tblTitle.setForeground(new Color(15, 23, 42));
        tblTitle.setBorder(new EmptyBorder(0, 0, 16, 0));

        String[] cols = {"Date / Time", "Charge Description", "Quantity", "Amount", "Status"};
        modelFolioLedger = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(modelFolioLedger);
        table.setRowHeight(40);
        table.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Century Gothic", Font.BOLD, 12));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(100, 116, 139));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        table.getColumnModel().getColumn(4).setCellRenderer(new GuestStatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        ledgerCard.add(tblTitle, BorderLayout.NORTH);
        ledgerCard.add(scroll, BorderLayout.CENTER);

        panel.add(summaryCard, BorderLayout.WEST);
        panel.add(ledgerCard, BorderLayout.CENTER);

        refreshCustomerBill();
        return panel;
    }

    public void refreshFolioLedgerTable() {
        if (modelFolioLedger == null) return;
        modelFolioLedger.setRowCount(0);
        Vector<Vector<Object>> data = CustomerDBA.getFolioItemizedCharges(currentGuest.guestId);
        for (Vector<Object> r : data) {
            modelFolioLedger.addRow(r);
        }
    }

    private JPanel createBillComponentRow(JLabel label, JLabel val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        label.setHorizontalAlignment(SwingConstants.LEFT);
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(label, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    public void refreshCustomerBill() {
        if (lblBillRoomDetails == null || lblBillRoomCharges == null ||
                lblBillServiceCharges == null || lblBillTax == null ||
                lblBillTotalNet == null || btnPayFolio == null) {
            return;
        }

        CustomerDBA.LiveCustomerBill bill = CustomerDBA.getLiveCustomerBill(currentGuest.guestId);
        lblBillRoomDetails.setText(bill.roomDetails);
        lblBillRoomCharges.setText(String.format("%,d MMK", bill.roomCharges.longValue()));
        lblBillServiceCharges.setText(String.format("%,d MMK", bill.serviceCharges.longValue()));
        lblBillTax.setText(String.format("%,d MMK", bill.taxAmount.longValue()));
        lblBillTotalNet.setText(String.format("%,d MMK", bill.netPayable.longValue()));
        btnPayFolio.setEnabled(bill.hasActiveStay && bill.netPayable.compareTo(BigDecimal.ZERO) > 0);

        refreshFolioLedgerTable();
    }

    public void refreshCustomerSession() {
        currentGuest = CustomerDBA.getGuestProfile(User_UI.getUname());

        lblGuestTopName.setText(currentGuest.fullName);
        String roomText = currentGuest.activeRoomNo.equals("None") ? "No Active Check-in" : "Suite " + currentGuest.activeRoomNo;
        lblGuestTopBadge.setText(roomText + " • " + currentGuest.vipTier);

        if (lblLoyaltyPointsBadge != null) {
            lblLoyaltyPointsBadge.setText(String.format("%,d Points Available", currentGuest.loyaltyPoints));
            lblLoyaltyTierBadge.setText("⭐ " + currentGuest.vipTier + " MEMBER");
            tierProgressBar.setValue(Math.min(currentGuest.loyaltyPoints, 5000));
        }

        if (modelLoyaltyHistory != null) {
            modelLoyaltyHistory.setRowCount(0);
            Vector<Vector<Object>> hist = CustomerDBA.getLoyaltyHistory(currentGuest.guestId);
            for (Vector<Object> r : hist) modelLoyaltyHistory.addRow(r);
        }

        reloadDynamicPerks();
        refreshCustomerBill();
        refreshHousekeepingTable();
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Left Identity & Hero Card
        JPanel leftHeroCard = createModernCardPanel();
        leftHeroCard.setLayout(new BoxLayout(leftHeroCard, BoxLayout.Y_AXIS));
        leftHeroCard.setPreferredSize(new Dimension(340, 0));
        leftHeroCard.setBorder(new EmptyBorder(24, 20, 24, 20));

        // 1. Centered Large Avatar
        String initial = currentGuest.fullName.isEmpty() ? "G" : currentGuest.fullName.substring(0, 1).toUpperCase();
        CircularAvatar largeAvatar = new CircularAvatar("/images/profile.png", initial, 74);
        largeAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftHeroCard.add(largeAvatar);
        leftHeroCard.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2. Full Name & Clean Star VIP Badge
        JLabel lblHeroName = new JLabel(currentGuest.fullName, SwingConstants.CENTER);
        lblHeroName.setFont(new Font("Century Gothic", Font.BOLD, 18));
        lblHeroName.setForeground(new Color(15, 23, 42));
        lblHeroName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblHeroTier = new JLabel(" ★ " + currentGuest.vipTier + " MEMBER ");
        lblHeroTier.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        lblHeroTier.setForeground(new Color(99, 102, 241));
        lblHeroTier.setBackground(new Color(238, 242, 255));
        lblHeroTier.setOpaque(true);
        lblHeroTier.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(199, 210, 254), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        lblHeroTier.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftHeroCard.add(lblHeroName);
        leftHeroCard.add(Box.createRigidArea(new Dimension(0, 6)));
        leftHeroCard.add(lblHeroTier);
        leftHeroCard.add(Box.createRigidArea(new Dimension(0, 18)));

        // 3. Stats Metric Badges
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 10, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        statsRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel stat1 = createStatBlock("LOYALTY POINTS", String.format("%,d", currentGuest.loyaltyPoints), new Color(99, 102, 241));
        String roomStr = currentGuest.activeRoomNo.equals("None") ? "None" : currentGuest.activeRoomNo;
        JPanel stat2 = createStatBlock("ACTIVE SUITE", roomStr, new Color(16, 185, 129));

        statsRow.add(stat1);
        statsRow.add(stat2);
        leftHeroCard.add(statsRow);
        leftHeroCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // 4. Action Buttons
        JButton btnEditProfile = new JButton("✏️ Edit Profile Info") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(79, 70, 229), getWidth(), 0, new Color(67, 56, 202)));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(129, 140, 248), getWidth(), 0, new Color(99, 102, 241)));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241), getWidth(), 0, new Color(139, 92, 246)));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnEditProfile.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnEditProfile.setForeground(Color.WHITE);
        btnEditProfile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnEditProfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEditProfile.setFocusPainted(false);
        btnEditProfile.setBorderPainted(false);
        btnEditProfile.setContentAreaFilled(false);
        btnEditProfile.setOpaque(false);
        btnEditProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditProfile.addActionListener(e -> openEditProfileDialog());

        JButton btnChangePass = new JButton("🔑 Change Password") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(226, 232, 240));
                } else {
                    g2.setColor(new Color(241, 245, 249));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(203, 213, 225));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2.setColor(new Color(51, 65, 85));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnChangePass.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnChangePass.setForeground(new Color(51, 65, 85));
        btnChangePass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnChangePass.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnChangePass.setFocusPainted(false);
        btnChangePass.setBorderPainted(false);
        btnChangePass.setContentAreaFilled(false);
        btnChangePass.setOpaque(false);
        btnChangePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangePass.addActionListener(e -> openChangePasswordDialog());

        leftHeroCard.add(btnEditProfile);
        leftHeroCard.add(Box.createRigidArea(new Dimension(0, 8)));
        leftHeroCard.add(btnChangePass);
        leftHeroCard.add(Box.createRigidArea(new Dimension(0, 18)));

        // 5. VIP Tier Privileges Card (Fills Bottom Space)
        JPanel privilegeBox = new JPanel();
        privilegeBox.setLayout(new BoxLayout(privilegeBox, BoxLayout.Y_AXIS));
        privilegeBox.setBackground(new Color(248, 250, 252));
        privilegeBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        privilegeBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        privilegeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel lblPrivTitle = new JLabel("TIER PRIVILEGES ACTIVE");
        lblPrivTitle.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lblPrivTitle.setForeground(new Color(100, 116, 139));
        lblPrivTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        privilegeBox.add(lblPrivTitle);
        privilegeBox.add(Box.createRigidArea(new Dimension(0, 8)));
        privilegeBox.add(createPrivilegeRow("✓  Complimentary High-Speed Wi-Fi"));
        privilegeBox.add(Box.createRigidArea(new Dimension(0, 4)));
        privilegeBox.add(createPrivilegeRow("✓  Express Concierge Check-in"));
        privilegeBox.add(Box.createRigidArea(new Dimension(0, 4)));
        privilegeBox.add(createPrivilegeRow("✓  1 Point per 1,000 MMK Spent"));
        privilegeBox.add(Box.createRigidArea(new Dimension(0, 4)));
        privilegeBox.add(createPrivilegeRow("✓  Priority Housekeeping Access"));

        leftHeroCard.add(privilegeBox);
        leftHeroCard.add(Box.createVerticalGlue());
        leftHeroCard.add(btnEditProfile);
        leftHeroCard.add(Box.createRigidArea(new Dimension(0, 10)));
        leftHeroCard.add(btnChangePass);
        leftHeroCard.add(Box.createVerticalGlue());

        // Right Structured Details Card
        JPanel detailsCard = createModernCardPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Guest Account & Identity Overview");
        title.setFont(new Font("Century Gothic", Font.BOLD, 17));
        title.setForeground(new Color(15, 23, 42));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Official registered credentials, security verifications & concierge records");
        sub.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        sub.setForeground(new Color(100, 116, 139));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsCard.add(title);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 2)));
        detailsCard.add(sub);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 18)));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(226, 232, 240));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsCard.add(sep);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 18)));

        // Field Value Labels
        lblValName = createProfileValueLabel(currentGuest.fullName);
        lblValPhone = createProfileValueLabel(currentGuest.phone);
        lblValEmail = createProfileValueLabel(currentGuest.email);
        lblValCity = createProfileValueLabel(currentGuest.city);
        lblValNid = createProfileValueLabel(currentGuest.nidPassport);
        lblValPref = createProfileValueLabel(currentGuest.preferences.isEmpty() ? "None recorded" : currentGuest.preferences);

        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 24, 18));
        infoGrid.setOpaque(false);
        infoGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        infoGrid.add(createFieldBlock("FULL LEGAL NAME", lblValName, "👤"));
        infoGrid.add(createFieldBlock("CONTACT PHONE NUMBER", lblValPhone, "📞"));
        infoGrid.add(createFieldBlock("VERIFIED EMAIL ADDRESS", lblValEmail, "✉️"));
        infoGrid.add(createFieldBlock("PRIMARY CITY / LOCATION", lblValCity, "📍"));
        infoGrid.add(createFieldBlock("NRC / PASSPORT ID", lblValNid, "🪪"));
        infoGrid.add(createFieldBlock("ACCOUNT SYSTEM ID", new JLabel(currentGuest.guestId), "🆔"));

        detailsCard.add(infoGrid);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 22)));

        // Concierge Preferences Card Section
        JPanel prefSection = new JPanel(new BorderLayout(0, 6));
        prefSection.setOpaque(false);
        prefSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        prefSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel lblPrefTitle = new JLabel("CONCIERGE & PERSONAL PREFERENCES");
        lblPrefTitle.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lblPrefTitle.setForeground(new Color(100, 116, 139));

        JPanel prefBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 252));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        prefBox.setOpaque(false);
        prefBox.setBorder(new EmptyBorder(10, 14, 10, 14));
        prefBox.add(lblValPref, BorderLayout.CENTER);

        prefSection.add(lblPrefTitle, BorderLayout.NORTH);
        prefSection.add(prefBox, BorderLayout.CENTER);

        detailsCard.add(prefSection);
        detailsCard.add(Box.createVerticalGlue());

        panel.add(leftHeroCard, BorderLayout.WEST);
        panel.add(detailsCard, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createPrivilegeRow(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        lbl.setForeground(new Color(51, 65, 85));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel createFieldBlock(String caption, JLabel valueLabel, String icon) {
        JPanel block = new JPanel(new BorderLayout(8, 2));
        block.setOpaque(false);

        JLabel lblCap = new JLabel(caption);
        lblCap.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lblCap.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Century Gothic", Font.BOLD, 13));
        valueLabel.setForeground(new Color(15, 23, 42));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JPanel textBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        textBlock.setOpaque(false);
        textBlock.add(lblCap);
        textBlock.add(valueLabel);

        block.add(lblIcon, BorderLayout.WEST);
        block.add(textBlock, BorderLayout.CENTER);
        return block;
    }

    private JPanel createStatBlock(String caption, String value, Color accent) {
        JPanel block = new JPanel(new GridLayout(2, 1, 0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 252));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        block.setOpaque(false);
        block.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblCap = new JLabel(caption);
        lblCap.setFont(new Font("Century Gothic", Font.BOLD, 9));
        lblCap.setForeground(new Color(100, 116, 139));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Century Gothic", Font.BOLD, 14));
        lblVal.setForeground(accent);

        block.add(lblCap);
        block.add(lblVal);
        return block;
    }

    private void addProfileFieldView(JPanel parent, String title, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setBorder(new EmptyBorder(4, 0, 4, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 116, 139));
        lblTitle.setPreferredSize(new Dimension(240, 24));

        row.add(lblTitle, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);

        parent.add(row);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    private JLabel createProfileValueLabel(String text) {
        JLabel label = new JLabel(text != null && !text.isEmpty() ? text : "—");
        label.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        label.setForeground(new Color(15, 23, 42));
        return label;
    }

    private void openEditProfileDialog() {
        JDialog dialog = new JDialog(this, "Edit Guest Profile", true);
        dialog.setSize(440, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(22, 26, 22, 26));

        JLabel heading = new JLabel("Update Your Information");
        heading.setFont(new Font("Century Gothic", Font.BOLD, 16));
        heading.setForeground(new Color(15, 23, 42));
        form.add(heading);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        JTextField txtName = createStyledTextField();
        txtName.setText(currentGuest.fullName);

        JTextField txtPhone = createStyledTextField();
        txtPhone.setText(currentGuest.phone);

        JTextField txtEmail = createStyledTextField();
        txtEmail.setText(currentGuest.email);

        JTextField txtCity = createStyledTextField();
        txtCity.setText(currentGuest.city);

        JTextField txtNid = createStyledTextField();
        txtNid.setText(currentGuest.nidPassport);

        JTextField txtPref = createStyledTextField();
        txtPref.setText(currentGuest.preferences);

        addGuestFormGroup(form, "Full Name", txtName);
        addGuestFormGroup(form, "Phone Number", txtPhone);
        addGuestFormGroup(form, "Email Address", txtEmail);
        addGuestFormGroup(form, "City", txtCity);
        addGuestFormGroup(form, "NRC / Passport ID", txtNid);
        addGuestFormGroup(form, "Personal Preferences", txtPref);

        JButton btnSave = new JButton("Save Changes") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(16, 185, 129), getWidth(), 0, new Color(5, 150, 105)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnSave.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnSave.setForeground(Color.WHITE);
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setContentAreaFilled(false);
        btnSave.setOpaque(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            boolean ok = CustomerDBA.updateGuestProfile(
                    currentGuest.guestId,
                    txtName.getText().trim(),
                    txtPhone.getText().trim(),
                    txtEmail.getText().trim(),
                    txtCity.getText().trim(),
                    txtNid.getText().trim(),
                    txtPref.getText().trim()
            );

            if (ok) {
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCustomerSession();
                updateProfileLabels();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void openChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Security - Change Password", true);
        dialog.setSize(380, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(22, 26, 22, 26));

        JLabel heading = new JLabel("Update Account Password");
        heading.setFont(new Font("Century Gothic", Font.BOLD, 16));
        heading.setForeground(new Color(15, 23, 42));
        form.add(heading);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        JPasswordField txtCurrent = new JPasswordField();
        stylePasswordField(txtCurrent);

        JPasswordField txtNew = new JPasswordField();
        stylePasswordField(txtNew);

        JPasswordField txtConfirm = new JPasswordField();
        stylePasswordField(txtConfirm);

        addGuestFormGroup(form, "Current Password", txtCurrent);
        addGuestFormGroup(form, "New Password", txtNew);
        addGuestFormGroup(form, "Confirm New Password", txtConfirm);

        JButton btnUpdate = new JButton("Change Password") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241), getWidth(), 0, new Color(139, 92, 246)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btnUpdate.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setContentAreaFilled(false);
        btnUpdate.setOpaque(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnUpdate.addActionListener(e -> {
            String curr = new String(txtCurrent.getPassword()).trim();
            String nPass = new String(txtNew.getPassword()).trim();
            String cPass = new String(txtConfirm.getPassword()).trim();

            if (curr.isEmpty() || nPass.isEmpty() || cPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all password fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!nPass.equals(cPass)) {
                JOptionPane.showMessageDialog(dialog, "New passwords do not match.", "Password Mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = CustomerDBA.changeCustomerPassword(currentGuest.guestId, curr, nPass);
            if (ok) {
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Security Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Incorrect current password. Please try again.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(btnUpdate);

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void updateProfileLabels() {
        if (lblValName != null) lblValName.setText(currentGuest.fullName);
        if (lblValPhone != null) lblValPhone.setText(currentGuest.phone);
        if (lblValEmail != null) lblValEmail.setText(currentGuest.email.isEmpty() ? "—" : currentGuest.email);
        if (lblValCity != null) lblValCity.setText(currentGuest.city);
        if (lblValNid != null) lblValNid.setText(currentGuest.nidPassport);
        if (lblValPref != null) lblValPref.setText(currentGuest.preferences.isEmpty() ? "None recorded" : currentGuest.preferences);
    }

    private JPanel createModernCardPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
    }

    private void stylePasswordField(JPasswordField pf) {
        pf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void addGuestFormGroup(JPanel parent, String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(1400, 60));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);

        input.setPreferredSize(new Dimension(input.getPreferredSize().width, 36));

        group.add(lbl, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);

        parent.add(group);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        box.setBackground(Color.WHITE);
        box.setMaximumSize(new Dimension(1400, 36));
    }

    static class RoomImageCarousel extends JPanel {
        private final String[] paths;
        private final String fallbackText;
        private int currentIndex = 0;

        public RoomImageCarousel(String[] paths, String fallbackText) {
            this.paths = paths;
            this.fallbackText = fallbackText;
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(1400, 120));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            Image currentImg = null;
            if (paths != null && paths.length > 0 && currentIndex < paths.length) {
                String path = paths[currentIndex];
                if (path != null && !path.trim().isEmpty()) {
                    try {
                        if (path.startsWith("http://") || path.startsWith("https://")) {
                            currentImg = new ImageIcon(new java.net.URI(path).toURL()).getImage();
                        } else {
                            java.io.File file = new java.io.File(path);
                            if (file.exists() && file.isFile()) {
                                currentImg = new ImageIcon(file.getAbsolutePath()).getImage();
                            } else {
                                URL url = getClass().getResource(path);
                                if (url != null) {
                                    currentImg = new ImageIcon(url).getImage();
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }

            Shape clipShape = new RoundRectangle2D.Double(0, 0, w, h, 12, 12);
            g2.setClip(clipShape);

            if (currentImg != null) {
                int imgW = currentImg.getWidth(null);
                int imgH = currentImg.getHeight(null);

                if (imgW > 0 && imgH > 0) {
                    double scale = Math.max((double) w / imgW, (double) h / imgH);
                    int drawW = (int) Math.round(imgW * scale);
                    int drawH = (int) Math.round(imgH * scale);
                    int drawX = (w - drawW) / 2;
                    int drawY = (h - drawH) / 2;

                    g2.drawImage(currentImg, drawX, drawY, drawW, drawH, this);
                } else {
                    g2.drawImage(currentImg, 0, 0, w, h, this);
                }
            } else {
                GradientPaint gp = new GradientPaint(0, 0, new Color(71, 85, 105), w, h, new Color(30, 41, 59));
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(255, 255, 255, 210));
                g2.setFont(new Font("Century Gothic", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "📷 " + fallbackText;
                int tx = (w - fm.stringWidth(txt)) / 2;
                int ty = ((h - fm.getHeight()) / 2) + fm.getAscent() + 6;
                g2.drawString(txt, tx, ty);
            }

            g2.setClip(null);
            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 12, 12));

            g2.dispose();
        }
    }

    static class GuestStatusBadgeRenderer extends DefaultTableCellRenderer {

        public GuestStatusBadgeRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Century Gothic", Font.BOLD, 10));
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
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
                case "DELIVERED":
                case "COMPLETED":
                case "CREDITED":
                    bg = new Color(16, 185, 129);
                    break;
                case "PREPARING":
                    bg = new Color(245, 158, 11);
                    break;
                case "REDEEMED":
                    bg = new Color(168, 85, 247);
                    break;
                default:
                    bg = new Color(100, 116, 139);
                    break;
            }

            int padX = 16;
            int badgeW = getWidth() - (padX * 2);
            int badgeH = getHeight() - 10;
            int badgeY = 5;

            g2.setColor(bg);
            g2.fillRoundRect(padX, badgeY, badgeW, badgeH, 8, 8);

            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(status)) / 2;
            int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(status, textX, textY);

            g2.dispose();
        }
    }

    static class CircularAvatar extends JComponent {
        private Image avatarImage;
        private final String fallbackInitials;

        public CircularAvatar(String resourcePath, String fallbackInitials, int diameter) {
            this.fallbackInitials = fallbackInitials;
            setPreferredSize(new Dimension(diameter, diameter));
            setMaximumSize(new Dimension(diameter, diameter));
            setMinimumSize(new Dimension(diameter, diameter));

            try {
                URL url = getClass().getResource(resourcePath);
                if (url != null) {
                    this.avatarImage = new ImageIcon(url).getImage();
                }
            } catch (Exception ignored) {}
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int size = Math.min(getWidth(), getHeight());
            Shape circleShape = new Ellipse2D.Double(1, 1, size - 2, size - 2);

            if (avatarImage != null) {
                g2.setClip(circleShape);
                g2.drawImage(avatarImage, 1, 1, size - 2, size - 2, this);
                g2.setClip(null);
            } else {
                g2.setColor(new Color(238, 242, 255));
                g2.fill(circleShape);

                g2.setFont(new Font("Century Gothic", Font.BOLD, 13));
                g2.setColor(new Color(79, 70, 229));
                FontMetrics fm = g2.getFontMetrics();
                int x = (size - fm.stringWidth(fallbackInitials)) / 2;
                int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(fallbackInitials, x, y);
            }

            g2.setColor(new Color(199, 210, 254));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new Ellipse2D.Double(1, 1, size - 2, size - 2));
            g2.dispose();
        }
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        return tf;
    }
}