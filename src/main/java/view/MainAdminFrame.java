package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MainAdminFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCardsPanel = new JPanel(cardLayout);
    private JPanel sidebarPanel;

    private final String currentRole;

    public MainAdminFrame() {
        this.currentRole = User_UI.getUserRole();

        setTitle("Hotel Management System - Enterprise Portal (" + currentRole + ")");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSideBar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(createTopBar(), BorderLayout.NORTH);

        registerAuthorizedCards();

        mainArea.add(contentCardsPanel, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        cardLayout.show(contentCardsPanel, "DASHBOARD");
    }

    private void registerAuthorizedCards() {
        contentCardsPanel.add(new DashboardUI(), "DASHBOARD");

        if (hasAccess("BOOKING")) contentCardsPanel.add(new BookingUI(), "BOOKING");
        if (hasAccess("HOUSE_KEEPING")) contentCardsPanel.add(new HouseKeepingUI(), "HOUSE_KEEPING");
        if (hasAccess("SERVICE")) contentCardsPanel.add(new ServiceUI(), "SERVICE");
        if (hasAccess("BILLING")) contentCardsPanel.add(new BillingUI(), "BILLING");
        if (hasAccess("REPORTS")) contentCardsPanel.add(new ReportUI(), "REPORTS");
        if (hasAccess("CATEGORY_MANAGEMENT")) {
            contentCardsPanel.add(new CategoryManagementUI(), "CATEGORY_MANAGEMENT");
        }
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
            case "HOUSEKEEPING":
                return Arrays.asList("DASHBOARD", "HOUSE_KEEPING", "ROOMS_MANAGEMENT").contains(cardKey);
            case "BILLING_MANAGER":
                return Arrays.asList("DASHBOARD", "BILLING", "REPORTS", "RATES_PRICING").contains(cardKey);
            case "STAFF":
                return Arrays.asList("DASHBOARD", "BOOKING", "SERVICE").contains(cardKey);
            default:
                return "DASHBOARD".equals(cardKey);
        }
    }

    private JPanel createSideBar() {
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        0, getHeight(), new Color(31, 47, 83)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(230, 0));
        sidebarPanel.setBorder(new EmptyBorder(20, 14, 20, 14));

        JLabel hotelLogo = new JLabel("<html><b>HMS</b> <font color='#818cf8'>" + currentRole + "</font></html>");
        hotelLogo.setFont(new Font("Century Gothic", Font.BOLD, 20));
        hotelLogo.setForeground(Color.WHITE);
        hotelLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(hotelLogo);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // 1. Operations Navigation Section
        String[][] mainNav = {
                {"📊  Dashboard", "DASHBOARD"},
                {"📅  Booking", "BOOKING"},
                {"🧹  House Keeping", "HOUSE_KEEPING"},
                {"🛎️  Service", "SERVICE"},
                {"💳  Billing", "BILLING"}
        };

        boolean firstButtonSet = false;
        for (String[] item : mainNav) {
            if (hasAccess(item[1])) {
                sidebarPanel.add(createSidebarNavButton(item[0], item[1], !firstButtonSet));
                sidebarPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                firstButtonSet = true;
            }
        }

        String[][] subNav = {
                {"📈  Reports", "REPORTS"},
                {"🚪  Rooms Management", "ROOMS_MANAGEMENT"},
                {"🛎️  Service Catalog", "SERVICE_MANAGEMENT"},
                {"🗂️  Category & Tiers", "CATEGORY_MANAGEMENT"},
                {"🏷️  Rates & Pricing", "RATES_PRICING"},
                {"👥  User Management", "USER_MANAGEMENT"}
        };

        boolean showSystemSection = false;
        for (String[] item : subNav) {
            if (hasAccess(item[1])) {
                showSystemSection = true;
                break;
            }
        }

        if (showSystemSection) {
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 16)));

            JLabel systemSection = new JLabel("SYSTEM & CONTROL");
            systemSection.setFont(new Font("Century Gothic", Font.BOLD, 10));
            systemSection.setForeground(new Color(186, 230, 253, 180));
            systemSection.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebarPanel.add(systemSection);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));

            for (String[] item : subNav) {
                if (hasAccess(item[1])) {
                    sidebarPanel.add(createSidebarNavButton(item[0], item[1], false));
                    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                }
            }
        }

        sidebarPanel.add(Box.createVerticalGlue());

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
            int confirm = JOptionPane.showConfirmDialog(this, "Log out from administrative portal?", "Sign Out", JOptionPane.YES_NO_OPTION);
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

            for (Component c : sidebarPanel.getComponents()) {
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

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(10, 24, 10, 24)
        ));

        JLabel title = new JLabel("Enterprise Hotel Management & Operations");
        title.setFont(new Font("Century Gothic", Font.BOLD, 15));
        title.setForeground(new Color(30, 41, 59));

        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightSection.setOpaque(false);

        JPanel userBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        userBlock.setOpaque(false);

        String user = User_UI.getUname();

        JLabel username = new JLabel(user, SwingConstants.RIGHT);
        username.setFont(new Font("Century Gothic", Font.BOLD, 13));
        username.setForeground(new Color(30, 41, 59));

        JLabel role = new JLabel(formatRoleName(currentRole), SwingConstants.RIGHT);
        role.setFont(new Font("Century Gothic", Font.BOLD, 11));
        role.setForeground(new Color(99, 102, 241));

        userBlock.add(username);
        userBlock.add(role);

        rightSection.add(userBlock);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightSection, BorderLayout.EAST);
        return topBar;
    }

    private String formatRoleName(String role) {
        switch (role) {
            case "ADMIN": return "System Administrator";
            case "RECEPTIONIST": return "Front Desk Receptionist";
            case "HOUSEKEEPING": return "Housekeeping Supervisor";
            case "BILLING_MANAGER": return "Finance & Billing Manager";
            case "STAFF": return "General Operations Staff";
            default: return role;
        }
    }
}