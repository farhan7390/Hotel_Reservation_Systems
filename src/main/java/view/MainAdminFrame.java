package view;

import model.BookingDBA;
import util.NotificationToast;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class MainAdminFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCardsPanel = new JPanel(cardLayout);
    private JPanel sidebarPanel;
    private Timestamp lastNotificationCheck = new Timestamp(System.currentTimeMillis() - 30000);

    private final java.util.Set<String> knownBookingRefs = new java.util.HashSet<>();
    private Timer bookingNotificationWatcher;

    private final String currentRole;

    public MainAdminFrame() {
        this.currentRole = User_UI.getUserRole();

        setTitle("Grand Horizon Suites — Enterprise Portal (" + currentRole + ")");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        add(createSideBar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(new Color(248, 250, 252));
        mainArea.add(createTopBar(), BorderLayout.NORTH);

        util.AppIcon.setFrameIcon(this, "/images/favicon1.png");

        contentCardsPanel.setBackground(new Color(248, 250, 252));
        registerAuthorizedCards();

        mainArea.add(contentCardsPanel, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        cardLayout.show(contentCardsPanel, "DASHBOARD");

        startBookingNotificationService();
    }

    private void registerAuthorizedCards() {
        contentCardsPanel.add(new DashboardUI(), "DASHBOARD");

        if (hasAccess("BOOKING")) contentCardsPanel.add(new BookingUI(), "BOOKING");
        if (hasAccess("HOUSE_KEEPING")) contentCardsPanel.add(new HouseKeepingUI(), "HOUSE_KEEPING");
        if (hasAccess("SERVICE")) contentCardsPanel.add(new ServiceUI(), "SERVICE");
        if (hasAccess("BILLING")) contentCardsPanel.add(new BillingUI(), "BILLING");
        if (hasAccess("REPORTS")) contentCardsPanel.add(new ReportUI(), "REPORTS");
        if (hasAccess("CATEGORY_MANAGEMENT")) contentCardsPanel.add(new CategoryManagementUI(), "CATEGORY_MANAGEMENT");
        if (hasAccess("CUSTOMER_MANAGEMENT")) contentCardsPanel.add(new CustomerUI(), "CUSTOMER_MANAGEMENT");
        if (hasAccess("SERVICE_MANAGEMENT")) contentCardsPanel.add(new ServiceManagementUI(), "SERVICE_MANAGEMENT");
        if (hasAccess("ROOMS_MANAGEMENT")) contentCardsPanel.add(new RoomManagementUI(), "ROOMS_MANAGEMENT");
        if (hasAccess("RATES_PRICING")) contentCardsPanel.add(new RateAndPricingUI(), "RATES_PRICING");
        if (hasAccess("USER_MANAGEMENT")) contentCardsPanel.add(new UserManagementUI(), "USER_MANAGEMENT");
    }

    private boolean hasAccess(String cardKey) {
        if ("ADMIN".equals(currentRole)) return true;

        switch (currentRole) {
            case "RECEPTIONIST":
                return Arrays.asList("DASHBOARD", "BOOKING", "SERVICE", "BILLING", "ROOMS_MANAGEMENT").contains(cardKey);
            case "HOUSEKEEPING_MANAGER":
                return Arrays.asList("DASHBOARD", "HOUSE_KEEPING", "ROOMS_MANAGEMENT").contains(cardKey);
            case "BILLING_MANAGER":
                return Arrays.asList("DASHBOARD", "BILLING", "REPORTS", "RATES_PRICING").contains(cardKey);
            case "STAFF":
                return Arrays.asList("DASHBOARD", "BOOKING", "SERVICE").contains(cardKey);
            case "HOUSEKEEPING" :
                return Arrays.asList("DASHBOARD", "HOUSE_KEEPING", "ROOMS_MANAGEMENT").contains(cardKey);
            default:
                return "DASHBOARD".equals(cardKey);
        }
    }

    private JPanel createSideBar() {
        sidebarPanel = new JPanel() {
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
                g2.setColor(new Color(99, 102, 241, 35));
                g2.fillOval(-40, getHeight() - 180, 220, 220);

                g2.dispose();
            }
        };

        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(new EmptyBorder(22, 16, 20, 16));

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

        sidebarPanel.add(brandLogoPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel sectionTitle = new JLabel("HOTEL OPERATIONS");
        sectionTitle.setFont(new Font("Century Gothic", Font.BOLD, 10));
        sectionTitle.setForeground(new Color(100, 116, 139));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(sectionTitle);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[][] mainNav = {
                {"📊  Operations Dashboard", "DASHBOARD"},
                {"📅  Room Bookings", "BOOKING"},
                {"🧹  Housekeeping Dispatch", "HOUSE_KEEPING"},
                {"🛎️  Room Service Orders", "SERVICE"},
                {"💳  Folio Billing & Pay", "BILLING"}
        };

        boolean firstButtonSet = false;
        for (String[] item : mainNav) {
            if (hasAccess(item[1])) {
                sidebarPanel.add(createSidebarNavButton(item[0], item[1], !firstButtonSet));
                sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                firstButtonSet = true;
            }
        }

        String[][] subNav = {
                {"📈  Financial Reports", "REPORTS"},
                {"🚪  Suites & Inventory", "ROOMS_MANAGEMENT"},
                {"🛎️  Service Menu Catalog", "SERVICE_MANAGEMENT"},
                {"👥  Guest Roster & VIP", "CUSTOMER_MANAGEMENT"},
                {"🗂️  Categories & Tiers", "CATEGORY_MANAGEMENT"},
                {"🏷️  Tariff Rate Manager", "RATES_PRICING"},
                {"👤  Staff & System Access", "USER_MANAGEMENT"}
        };

        boolean showSystemSection = false;
        for (String[] item : subNav) {
            if (hasAccess(item[1])) {
                showSystemSection = true;
                break;
            }
        }

        if (showSystemSection) {
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 14)));

            JLabel systemSection = new JLabel("SYSTEM CONTROL & ADMIN");
            systemSection.setFont(new Font("Century Gothic", Font.BOLD, 10));
            systemSection.setForeground(new Color(100, 116, 139));
            systemSection.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebarPanel.add(systemSection);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));

            for (String[] item : subNav) {
                if (hasAccess(item[1])) {
                    sidebarPanel.add(createSidebarNavButton(item[0], item[1], false));
                    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        }

        sidebarPanel.add(Box.createVerticalGlue());

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
            int confirm = JOptionPane.showConfirmDialog(this, "Log out from administrative management portal?", "Sign Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new User_UI().setVisible(true);
            }
        });

        sidebarPanel.add(btnLogout);
        return sidebarPanel;
    }

    private JButton createSidebarNavButton(String text, String cardKey, boolean isActive) {
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
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> {
            cardLayout.show(contentCardsPanel, cardKey);

            for (Component c : sidebarPanel.getComponents()) {
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

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(12, 28, 12, 28)
        ));

        JPanel leftBox = new JPanel(new GridLayout(2, 1, 0, 2));
        leftBox.setOpaque(false);

        JLabel title = new JLabel("Enterprise Hotel Management & Concierge Operations");
        title.setFont(new Font("Century Gothic", Font.BOLD, 16));
        title.setForeground(new Color(15, 23, 42));

        JLabel sub = new JLabel("Role-based authorization • Multi-tier tariff control • Live inventory dispatch");
        sub.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        sub.setForeground(new Color(100, 116, 139));

        leftBox.add(title);
        leftBox.add(sub);

        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightSection.setOpaque(false);

        JPanel userBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        userBlock.setOpaque(false);

        String user = User_UI.getUname() != null && !User_UI.getUname().isEmpty() ? User_UI.getUname() : "Admin";

        JLabel username = new JLabel(user, SwingConstants.RIGHT);
        username.setFont(new Font("Century Gothic", Font.BOLD, 13));
        username.setForeground(new Color(15, 23, 42));

        JLabel role = new JLabel(formatRoleName(currentRole), SwingConstants.RIGHT);
        role.setFont(new Font("Century Gothic", Font.BOLD, 11));
        role.setForeground(new Color(99, 102, 241));

        userBlock.add(username);
        userBlock.add(role);

        String initial = user.isEmpty() ? "A" : user.substring(0, 1).toUpperCase();
        CircularAvatar avatar = new CircularAvatar("/images/admin_avatar.png", initial, 38);

        rightSection.add(userBlock);
        rightSection.add(avatar);

        topBar.add(leftBox, BorderLayout.WEST);
        topBar.add(rightSection, BorderLayout.EAST);
        return topBar;
    }

    private String formatRoleName(String role) {
        if (role == null) return "Authorized Staff";
        switch (role) {
            case "ADMIN": return "System Administrator";
            case "RECEPTIONIST": return "Front Desk Receptionist";
            case "HOUSEKEEPING": return "Housekeeping Supervisor";
            case "BILLING_MANAGER": return "Finance & Billing Manager";
            case "STAFF": return "General Operations Staff";
            default: return role;
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

    private void startBookingNotificationService() {
        new Thread(() -> {
            List<BookingDBA.NewBookingAlert> initialBookings = BookingDBA.getLatestBookings();
            for (BookingDBA.NewBookingAlert b : initialBookings) {
                knownBookingRefs.add(b.bookingRef);
            }
            System.out.println(">>> [NOTIF] Initialized with " + knownBookingRefs.size() + " existing bookings.");
        }).start();

        bookingNotificationWatcher = new Timer(3000, e -> {
            try {
                List<BookingDBA.NewBookingAlert> latest = BookingDBA.getLatestBookings();
                for (BookingDBA.NewBookingAlert alert : latest) {
                    if (!knownBookingRefs.contains(alert.bookingRef)) {
                        knownBookingRefs.add(alert.bookingRef);
                        System.out.println(">>> [NEW BOOKING DETECTED] " + alert.bookingRef);

                        String title = "New Reservation Received!";
                        String msg = "<b>" + alert.guestName + "</b> reserved Suite <b>" + alert.roomNo + "</b> (" + alert.tierName + ").";

                        SwingUtilities.invokeLater(() -> {
                            NotificationToast toast = new NotificationToast(MainAdminFrame.this, title, msg, alert.bookingRef);
                            toast.showWithAutoDismiss(7000);
                        });

                        refreshActivePanels();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        bookingNotificationWatcher.start();
        System.out.println(">>> Booking notification background service started.");
    }

    private void refreshActivePanels() {
        SwingUtilities.invokeLater(() -> {
            for (Component c : contentCardsPanel.getComponents()) {
                if (c.isVisible()) {
                    if (c instanceof DashboardUI) {
                        ((DashboardUI) c).loadDashboardData();
                    } else if (c instanceof BookingUI) {
                        ((BookingUI) c).loadTableData();
                    }
                }
            }
        });
    }
}