/*
package view;

import model.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class User_UI extends JFrame {
    private static final Color LEFT_BG = new Color(15, 23, 42);
    private static final Color TEXT_GRAY  = new Color(100, 100, 100);
    private static final Font headingFont = new Font("Century Gothic", Font.BOLD, 22);

    private static String uname = "Guest";
    private static String userRole = "STAFF";

    public User_UI() {
        setLayout(new GridLayout(1, 2));
        setTitle("Hotel Reservation Systems - Login");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        util.AppIcon.setFrameIcon(this, "/images/favicon1.png");

        add(leftBanner());
        add(rightBanner());
    }

    public JPanel leftBanner() {
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
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
        leftPanel.setBackground(LEFT_BG);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel centreBox = new JPanel();
        centreBox.setOpaque(false);
        centreBox.setLayout(new BoxLayout(centreBox, BoxLayout.Y_AXIS));

        URL imgUrl = getClass().getResource("/resources/images/logoPNG.png");
        if (imgUrl == null) {
            imgUrl = getClass().getResource("/images/logoPNG.png");
        }

        JLabel logoLabel;
        if (imgUrl != null) {
            ImageIcon rawIcon = new ImageIcon(imgUrl);
            Image scaledImg = rawIcon.getImage().getScaledInstance(180, 130, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledImg));
        } else {
            logoLabel = new JLabel("<html><center>[ Logo ]</center></html>", SwingConstants.CENTER);
            logoLabel.setForeground(Color.WHITE);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("<html><center>Hotel Reservation<br>Systems</center></html>", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centreBox.add(logoLabel);
        centreBox.add(Box.createRigidArea(new Dimension(0, 5)));
        centreBox.add(lblTitle);

        leftPanel.add(centreBox);
        return leftPanel;
    }

    private JTabbedPane rightBanner() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(LEFT_BG);
        tabbedPane.setFont(new Font("Century Gothic", Font.BOLD, 15));

        tabbedPane.addTab("Customer Login", createLoginForm("Customer Login Here", true));
        tabbedPane.addTab("Staff Login", createLoginForm("Staff / Admin Login Here", false));

        return tabbedPane;
    }

    private JPanel createLoginForm(String titleText, boolean isCustomer) {
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(30, 45, 30, 45));

        JLabel lblFormTitle = new JLabel(titleText);
        lblFormTitle.setFont(headingFont);
        lblFormTitle.setForeground(Color.BLACK);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel(isCustomer ? "Email / Phone" : "Username / Email");
        lblUser.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblUser.setForeground(Color.BLACK);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtUser = createStyledTextField();
        txtUser.setMaximumSize(new Dimension(1400, 34));
        txtUser.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblPass.setForeground(Color.BLACK);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = createStyledPasswordField();
        txtPass.setMaximumSize(new Dimension(1400, 34));
        txtPass.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actionBtnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        actionBtnRow.setOpaque(false);
        actionBtnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionBtnRow.setMaximumSize(new Dimension(1400, 38));

        JButton btnLogin = new JButton("Log In");
        btnLogin.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnLogin.setBackground(new Color(99, 102, 241));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> {
            String enteredUser = txtUser.getText().trim();
            String enteredPass = new String(txtPass.getPassword()).trim();

            if (enteredUser.isEmpty() || enteredPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your credentials.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (isCustomer) {
                boolean valid = UserDAO.validateCustomer(enteredUser, enteredPass);
                if (valid) {
                    User_UI.setUname(enteredUser);
                    User_UI.setUserRole("CUSTOMER");
                    JOptionPane.showMessageDialog(this, "Customer Login Successful: " + enteredUser);
                    this.dispose();
                    SwingUtilities.invokeLater(() -> new Customer_Screen().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Customer Email/Phone or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                String authenticatedRole = UserDAO.validateAdmin(enteredUser, enteredPass);
                if (authenticatedRole != null) {
                    User_UI.setUname(enteredUser);
                    User_UI.setUserRole(authenticatedRole); // Store role from database
                    JOptionPane.showMessageDialog(this, "Login Successful! Role: " + authenticatedRole);
                    this.dispose();
                    SwingUtilities.invokeLater(() -> new MainAdminFrame().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(71, 85, 105));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> {
            txtUser.setText("");
            txtPass.setText("");
        });

        actionBtnRow.add(btnClear);
        actionBtnRow.add(btnLogin);

        JPanel footerLink = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerLink.setOpaque(false);
        footerLink.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isCustomer) {
            JLabel registerLabel = new JLabel("<html>New Member? <span style='color:#1877F2; font-weight:bold;'>Register Here</span></html>");
            registerLabel.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            registerLabel.setForeground(TEXT_GRAY);
            registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            registerLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    User_UI.this.dispose();
                    SwingUtilities.invokeLater(() -> new Customer_UI().setVisible(true));
                }
            });
            footerLink.add(registerLabel);
        }

        formPanel.add(lblFormTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(lblUser);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(txtUser);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(lblPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(txtPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(actionBtnRow);
        formPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        formPanel.add(footerLink);

        return formPanel;
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

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return pf;
    }

    public static void setUname(String username) {
        uname = username;
    }

    public static String getUname() {
        return (uname != null && !uname.trim().isEmpty()) ? uname : "Guest";
    }

    public static void setUserRole(String role) {
        userRole = role;
    }

    public static String getUserRole() {
        return (userRole != null && !userRole.trim().isEmpty()) ? userRole.toUpperCase() : "STAFF";
    }

    static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new User_UI().setVisible(true));
    }
}
*/

package view;

import model.CustomerDBA;
import model.DBConnection;
import model.UserDAO;
import util.AppIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User_UI extends JFrame {

    private static String currentUsername = "";
    private static String currentUserRole = "";

    private JTextField txtIdentifier;
    private JPasswordField txtPassword;
    private JButton btnCustomerTab, btnStaffTab;
    private JLabel lblInputHeader, lblSubHeader;
    private JPanel registerRow;
    private boolean isStaffMode = false;

    public static String getUname() {
        return currentUsername;
    }

    public static String getUserRole() {
        return currentUserRole;
    }

    public User_UI() {
        setTitle("Grand Horizon Suites - Guest & Staff Portal");
        setSize(960, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new GridLayout(1, 2));

        AppIcon.setFrameIcon(this, "/images/favicon1.png");

        add(createBrandingPanel());
        add(createLoginFormPanel());
    }

    private JPanel createBrandingPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                // Ultra-smooth rendering & interpolation
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Deep Midnight Gradient Background
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        getWidth(), getHeight(), new Color(30, 41, 59)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Ambient Radial Glow Circles
                g2.setColor(new Color(99, 102, 241, 35));
                g2.fillOval(-60, -60, 280, 280);

                g2.setColor(new Color(168, 85, 247, 28));
                g2.fillOval(getWidth() - 200, getHeight() - 220, 300, 300);

                int panelW = getWidth();
                int panelH = getHeight();
                int centerY = panelH / 2 - 15;

                // Prominent Centered Logo Presentation
                URL logoUrl = getClass().getResource("/images/logo3.png");
                if (logoUrl != null) {
                    Image logo = new ImageIcon(logoUrl).getImage();

                    // Large hero logo scaling (240px wide)
                    int logoWidth = 240;
                    int logoHeight = 160;
                    int logoX = (panelW - logoWidth) / 2;
                    int logoY = centerY - (logoHeight / 2) - 30;

                    g2.drawImage(logo, logoX, logoY, logoWidth, logoHeight, this);
                }

                // Subtitle Badges (Cleaned up, no duplicate "GRAND HORIZON" text)
                g2.setColor(new Color(129, 140, 248));
                g2.setFont(new Font("Century Gothic", Font.BOLD, 12));
                FontMetrics fm1 = g2.getFontMetrics();
                String badge = "HOTEL RESERVATION & MANAGEMENT";
                g2.drawString(badge, (panelW - fm1.stringWidth(badge)) / 2, centerY + 100);

                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Century Gothic", Font.PLAIN, 11));
                FontMetrics fm2 = g2.getFontMetrics();
                String sub = "Luxury Hospitality • Self Service Portal • VIP Ledger";
                g2.drawString(sub, (panelW - fm2.stringWidth(sub)) / 2, centerY + 122);

                // Bottom Version Note
                g2.setColor(new Color(100, 116, 139, 140));
                g2.setFont(new Font("Century Gothic", Font.PLAIN, 10));
                String ver = "v2.6 Multi-Tier Enterprise Build";
                g2.drawString(ver, (panelW - g2.getFontMetrics().stringWidth(ver)) / 2, panelH - 24);

                g2.dispose();
            }
        };
    }

    private JPanel createLoginFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 48, 40, 48));

        // Segmented Switch Pills (Customer vs Staff)
        JPanel switchPillContainer = new JPanel(new GridLayout(1, 2, 6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        switchPillContainer.setOpaque(false);
        switchPillContainer.setBorder(new EmptyBorder(4, 4, 4, 4));
        switchPillContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        switchPillContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCustomerTab = createPillButton("🏨  Guest Stay", true);
        btnStaffTab = createPillButton("💼  Hotel Staff", false);

        btnCustomerTab.addActionListener(e -> setLoginMode(false));
        btnStaffTab.addActionListener(e -> setLoginMode(true));

        switchPillContainer.add(btnCustomerTab);
        switchPillContainer.add(btnStaffTab);

        panel.add(switchPillContainer);
        panel.add(Box.createRigidArea(new Dimension(0, 26)));

        // Title Texts
        JLabel lblGreeting = new JLabel("Welcome Back");
        lblGreeting.setFont(new Font("Century Gothic", Font.BOLD, 22));
        lblGreeting.setForeground(new Color(15, 23, 42));
        lblGreeting.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSubHeader = new JLabel("Enter your guest email or phone to access portal amenities");
        lblSubHeader.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        lblSubHeader.setForeground(new Color(100, 116, 139));
        lblSubHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblGreeting);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(lblSubHeader);
        panel.add(Box.createRigidArea(new Dimension(0, 24)));

        // Inputs
        lblInputHeader = new JLabel("Email / Phone Number");
        lblInputHeader.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblInputHeader.setForeground(new Color(51, 65, 85));
        lblInputHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtIdentifier = new JTextField();
        JPanel fieldIdentifier = createRoundedInputBox(txtIdentifier, "✉️");

        JLabel lblPassHeader = new JLabel("Account Password");
        lblPassHeader.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lblPassHeader.setForeground(new Color(51, 65, 85));
        lblPassHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        JPanel fieldPass = createRoundedInputBox(txtPassword, "🔒");

        panel.add(lblInputHeader);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(fieldIdentifier);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        panel.add(lblPassHeader);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(fieldPass);
        panel.add(Box.createRigidArea(new Dimension(0, 22)));

        // Action Buttons Row (Clear & Sign In)
        JPanel actionRow = new JPanel(new GridLayout(1, 2, 12, 0));
        actionRow.setOpaque(false);
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(100, 116, 139));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> {
            txtIdentifier.setText("");
            txtPassword.setText("");
            txtIdentifier.requestFocus();
        });

        JButton btnLogin = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(79, 70, 229), getWidth(), 0, new Color(67, 56, 202)));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(129, 140, 248), getWidth(), 0, new Color(99, 102, 241)));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241), getWidth(), 0, new Color(168, 85, 247)));
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

        btnLogin.setFont(new Font("Century Gothic", Font.BOLD, 13));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setOpaque(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> handleLogin());

        // Enable Enter key submission
        getRootPane().setDefaultButton(btnLogin);

        actionRow.add(btnClear);
        actionRow.add(btnLogin);

        panel.add(actionRow);
        panel.add(Box.createRigidArea(new Dimension(0, 24)));

        registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerRow.setOpaque(false);
        registerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lblNoAcc = new JLabel("New Member?");
        lblNoAcc.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        lblNoAcc.setForeground(new Color(100, 116, 139));

        JLabel lblRegister = new JLabel("<html><u>Register Here</u></html>");
        lblRegister.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblRegister.setForeground(new Color(99, 102, 241));
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                User_UI.this.dispose();
                SwingUtilities.invokeLater(() -> new Customer_UI().setVisible(true));
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lblRegister.setForeground(new Color(79, 70, 229));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lblRegister.setForeground(new Color(99, 102, 241));
            }
        });

        registerRow.add(lblNoAcc);
        registerRow.add(lblRegister);

        panel.add(registerRow);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void setLoginMode(boolean staff) {
        this.isStaffMode = staff;
        btnCustomerTab.putClientProperty("active", !staff);
        btnStaffTab.putClientProperty("active", staff);

        btnCustomerTab.repaint();
        btnStaffTab.repaint();

        if (staff) {
            lblInputHeader.setText("Staff Username or Work Email");
            lblSubHeader.setText("Enter operational credentials for hotel management systems");
        } else {
            lblInputHeader.setText("Email / Phone Number");
            lblSubHeader.setText("Enter your guest email or phone to access portal amenities");
        }

        if (registerRow != null) {
            registerRow.setVisible(!staff);
        }

        txtIdentifier.setText("");
        txtPassword.setText("");
        txtIdentifier.requestFocus();
    }

    private JButton createPillButton(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Boolean isActive = (Boolean) getClientProperty("active");
                if (isActive != null && isActive) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(226, 232, 240));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        btn.putClientProperty("active", active);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btn.setForeground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createRoundedInputBox(JComponent inputComponent, String iconSymbol) {
        JPanel wrapper = new JPanel(new BorderLayout(8, 0)) {
            private boolean isFocused = false;

            {
                inputComponent.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        isFocused = true;
                        repaint();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        isFocused = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 252));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));

                if (isFocused) {
                    g2.setColor(new Color(99, 102, 241));
                    g2.setStroke(new BasicStroke(1.5f));
                } else {
                    g2.setColor(new Color(226, 232, 240));
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        wrapper.setBorder(new EmptyBorder(0, 12, 0, 12));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblIcon = new JLabel(iconSymbol);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        inputComponent.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        inputComponent.setForeground(new Color(15, 23, 42));
        inputComponent.setOpaque(false);
        if (inputComponent instanceof JTextField) {
            ((JTextField) inputComponent).setBorder(null);
        }

        wrapper.add(lblIcon, BorderLayout.WEST);
        wrapper.add(inputComponent, BorderLayout.CENTER);
        if (inputComponent instanceof JPasswordField) {
            JPasswordField pwdField = (JPasswordField) inputComponent;
            char defaultEchoChar = pwdField.getEchoChar();

            JComponent eyeToggle = new JComponent() {
                private boolean isPasswordVisible = false;
                private boolean isHovered = false;

                {
                    setPreferredSize(new Dimension(24, 20));
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    setToolTipText("Show/Hide password");

                    addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            isPasswordVisible = !isPasswordVisible;
                            if (isPasswordVisible) {
                                pwdField.setEchoChar((char) 0); // Show password
                            } else {
                                pwdField.setEchoChar(defaultEchoChar); // Mask password
                            }
                            repaint();
                            pwdField.requestFocus();
                        }

                        @Override
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                            isHovered = true;
                            repaint();
                        }

                        @Override
                        public void mouseExited(java.awt.event.MouseEvent e) {
                            isHovered = false;
                            repaint();
                        }
                    });
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                    Color iconColor = isHovered ? new Color(99, 102, 241) : new Color(148, 163, 184);
                    g2.setColor(iconColor);
                    g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    int w = getWidth();
                    int h = getHeight();
                    int cx = w / 2;
                    int cy = h / 2;

                    // 1. Draw Eye Outline (Curved Upper and Lower Arcs)
                    java.awt.geom.Path2D eyePath = new java.awt.geom.Path2D.Double();
                    eyePath.moveTo(cx - 9, cy);
                    eyePath.quadTo(cx, cy - 6.5, cx + 9, cy);
                    eyePath.quadTo(cx, cy + 6.5, cx - 9, cy);
                    g2.draw(eyePath);

                    // 2. Draw Pupil
                    if (isPasswordVisible) {
                        g2.fillOval(cx - 3, cy - 3, 6, 6);
                    } else {
                        g2.drawOval(cx - 3, cy - 3, 6, 6);
                        // Diagonal Slash Line when password is hidden
                        g2.drawLine(cx - 7, cy + 6, cx + 7, cy - 6);
                    }

                    g2.dispose();
                }
            };

            wrapper.add(eyeToggle, BorderLayout.EAST);
        }
        return wrapper;
    }

    private void handleLogin() {
        String identifier = txtIdentifier.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both your credentials and password.", "Required Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isStaffMode) {
            // Staff Authentication via DAO
            String authenticatedRole = UserDAO.validateAdmin(identifier, password);
            if (authenticatedRole != null) {
                currentUsername = identifier;
                currentUserRole = authenticatedRole;

                JOptionPane.showMessageDialog(this, "Login Successful! Role: " + authenticatedRole, "Access Granted", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                SwingUtilities.invokeLater(() -> new MainAdminFrame().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid staff username/email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Customer Authentication via DAO
            boolean valid = UserDAO.validateCustomer(identifier, password);
            if (valid) {
                currentUsername = identifier;
                currentUserRole = "CUSTOMER";

                JOptionPane.showMessageDialog(this, "Welcome back, " + identifier + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                SwingUtilities.invokeLater(() -> new Customer_Screen().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Customer Email/Phone or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new User_UI().setVisible(true));
    }
}

