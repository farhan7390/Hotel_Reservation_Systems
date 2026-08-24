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
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel sectionTitle = new JLabel("Available Rooms & Suites for Booking");
        sectionTitle.setFont(new Font("Century Gothic", Font.BOLD, 16));
        sectionTitle.setForeground(new Color(30, 41, 59));

        JLabel subtitle = new JLabel("Live real-time rooms ready for assignment in our database");
        subtitle.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        subtitle.setForeground(new Color(100, 116, 139));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(sectionTitle);
        titleBox.add(subtitle);

        headerPanel.add(titleBox, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);

        List<CustomerDBA.RoomCardData> rooms = CustomerDBA.getAvailableRoomCards();
        int rows = Math.max(1, (int) Math.ceil(rooms.size() / 2.0));

        JPanel gridPanel = new JPanel(new GridLayout(rows, 2, 18, 18));
        gridPanel.setOpaque(false);

        for (CustomerDBA.RoomCardData r : rooms) {
            gridPanel.add(createRoomCard(
                    r.roomNo, r.title, r.floor, r.price, r.tierBadge, r.features, new Color(99, 102, 241), r.imagePaths
            ));
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
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomCard(String roomNo, String title, String floor, String price, String tierBadge, String features, Color accentColor, String[] imagePaths) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(320, 275));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 275));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        RoomImageCarousel carousel = new RoomImageCarousel(imagePaths, roomNo + " (" + title + ")");
        carousel.setPreferredSize(new Dimension(300, 115));
        carousel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        carousel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRoom = new JLabel(roomNo + " • " + title);
        lblRoom.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        lblRoom.setForeground(new Color(30, 41, 59));

        JLabel lblBadge = new JLabel(tierBadge) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadge.setFont(new Font("Segoe UI Emoji", Font.BOLD, 10));
        lblBadge.setForeground(accentColor);
        lblBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

        topRow.add(lblRoom, BorderLayout.WEST);
        topRow.add(lblBadge, BorderLayout.EAST);

        JLabel lblFloor = new JLabel(floor);
        lblFloor.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblFloor.setForeground(new Color(148, 163, 184));
        lblFloor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFeatures = new JLabel("<html><font color='#64748b'>Amenities: </font>" + features + "</html>");
        lblFeatures.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblFeatures.setForeground(new Color(51, 65, 85));
        lblFeatures.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPrice = new JLabel(price);
        lblPrice.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblPrice.setForeground(new Color(15, 23, 42));

        JButton btnBook = new JButton("Book This Room");
        btnBook.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnBook.setBackground(new Color(99, 102, 241));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setBorderPainted(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBook.setPreferredSize(new Dimension(135, 30));
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

        btnConfirm.addActionListener(e -> {
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

        JPanel middleRow = new JPanel(new BorderLayout(18, 0));
        middleRow.setOpaque(false);
        middleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel vouchersCard = new JPanel();
        vouchersCard.setLayout(new BoxLayout(vouchersCard, BoxLayout.Y_AXIS));
        vouchersCard.setBackground(Color.WHITE);
        vouchersCard.setPreferredSize(new Dimension(420, 0));
        vouchersCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel vTitle = new JLabel("Redeem Loyalty Vouchers & Perks");
        vTitle.setFont(new Font("Century Gothic", Font.BOLD, 14));
        vTitle.setForeground(new Color(30, 41, 59));
        vTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        vouchersCard.add(vTitle);
        vouchersCard.add(Box.createRigidArea(new Dimension(0, 12)));

        vouchersCard.add(createRewardItem("💆 60-Min Aromatherapy Spa", "Cost: 1,200 Points", 1200));
        vouchersCard.add(Box.createRigidArea(new Dimension(0, 8)));
        vouchersCard.add(createRewardItem("🍽️ 30,000 MMK Dining Voucher", "Cost: 900 Points", 900));
        vouchersCard.add(Box.createRigidArea(new Dimension(0, 8)));
        vouchersCard.add(createRewardItem("🚗 Free Airport Drop Sedan", "Cost: 1,500 Points", 1500));

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

        return panel;
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

        JButton btnRedeem = new JButton("Redeem");
        btnRedeem.setFont(new Font("Century Gothic", Font.BOLD, 11));
        btnRedeem.setBackground(new Color(16, 185, 129));
        btnRedeem.setForeground(Color.WHITE);
        btnRedeem.setFocusPainted(false);
        btnRedeem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRedeem.setPreferredSize(new Dimension(82, 28));
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
        profileCard.setMaximumSize(new Dimension(1400, 500));
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Guest Account & Identity Settings");
        title.setFont(new Font("Century Gothic", Font.BOLD, 16));
        title.setForeground(new Color(30, 41, 59));
        profileCard.add(title);
        profileCard.add(Box.createRigidArea(new Dimension(0, 14)));

        JTextField txtName = new JTextField(currentGuest.fullName);
        JTextField txtPhone = new JTextField(currentGuest.phone);
        JTextField txtEmail = new JTextField(currentGuest.email);
        JTextField txtCity = new JTextField(currentGuest.city);
        JTextField txtNid = new JTextField(currentGuest.nidPassport);
        JTextArea txtPref = new JTextArea(currentGuest.preferences, 2, 20);
        txtPref.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        txtPref.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        addGuestFormGroup(profileCard, "Full Name", txtName);
        addGuestFormGroup(profileCard, "Phone Number", txtPhone);
        addGuestFormGroup(profileCard, "Email Address", txtEmail);
        addGuestFormGroup(profileCard, "City", txtCity);
        addGuestFormGroup(profileCard, "NRC / Passport ID", txtNid);
        addGuestFormGroup(profileCard, "Personal Notes & Preferences", txtPref);

        JButton btnSave = new JButton("Save Profile Changes");
        btnSave.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnSave.setBackground(new Color(99, 102, 241));
        btnSave.setForeground(Color.WHITE);
        btnSave.setMaximumSize(new Dimension(1400, 38));
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
                currentGuest = CustomerDBA.getGuestProfile(User_UI.getUname());
                lblGuestTopName.setText(currentGuest.fullName);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        profileCard.add(Box.createRigidArea(new Dimension(0, 10)));
        profileCard.add(btnSave);

        panel.add(profileCard);
        return panel;
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
        private final JLabel lblCounter;

        public RoomImageCarousel(String[] paths, String fallbackText) {
            this.paths = paths;
            this.fallbackText = fallbackText;
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(1400, 115));
            setOpaque(false);

            JPanel navOverlay = new JPanel(new BorderLayout());
            navOverlay.setOpaque(false);
            navOverlay.setBorder(new EmptyBorder(6, 8, 6, 8));

            JButton btnPrev = createArrowButton("◀");
            JButton btnNext = createArrowButton("▶");

            lblCounter = new JLabel("1/" + paths.length, SwingConstants.CENTER);
            lblCounter.setFont(new Font("Century Gothic", Font.BOLD, 10));
            lblCounter.setForeground(Color.WHITE);
            lblCounter.setOpaque(true);
            lblCounter.setBackground(new Color(15, 23, 42, 160));
            lblCounter.setBorder(new EmptyBorder(2, 6, 2, 6));

            btnPrev.addActionListener(e -> {
                if (currentIndex > 0) {
                    currentIndex--;
                } else {
                    currentIndex = paths.length - 1;
                }
                lblCounter.setText((currentIndex + 1) + "/" + paths.length);
                repaint();
            });

            btnNext.addActionListener(e -> {
                if (currentIndex < paths.length - 1) {
                    currentIndex++;
                } else {
                    currentIndex = 0;
                }
                lblCounter.setText((currentIndex + 1) + "/" + paths.length);
                repaint();
            });

            navOverlay.add(btnPrev, BorderLayout.WEST);
            navOverlay.add(lblCounter, BorderLayout.CENTER);
            navOverlay.add(btnNext, BorderLayout.EAST);

            add(navOverlay, BorderLayout.NORTH);
        }

        private JButton createArrowButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 10));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(15, 23, 42, 170));
            btn.setPreferredSize(new Dimension(28, 22));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
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
                        // 1. Check if it's a real file path on the local disk
                        java.io.File file = new java.io.File(path);
                        if (file.exists() && file.isFile()) {
                            currentImg = new ImageIcon(file.getAbsolutePath()).getImage();
                        } else {
                            // 2. Fallback to classpath resource
                            URL url = getClass().getResource(path);
                            if (url != null) {
                                currentImg = new ImageIcon(url).getImage();
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }

            Shape clipShape = new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 10, 10);
            g2.setClip(clipShape);

            if (currentImg != null) {
                g2.drawImage(currentImg, 0, 0, w, h, this);
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

}