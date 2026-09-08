package view;

import model.UserDAO;
import util.AppIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

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

    public static void setUname(String uname) { currentUsername = uname; }
    public static void setUserRole(String role) { currentUserRole = role; }

    public User_UI() {
        setTitle("Grand Horizon Suites - Guest & Staff Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 640));
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

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int panelW = getWidth();
                int panelH = getHeight();
                int centerY = panelH / 2;

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        panelW, panelH, new Color(30, 41, 59)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, panelW, panelH);

                g2.setColor(new Color(99, 102, 241, 35));
                g2.fillOval(-80, -80, 420, 420);

                g2.setColor(new Color(168, 85, 247, 28));
                g2.fillOval(panelW - 320, panelH - 340, 440, 440);

                URL logoUrl = getClass().getResource("/images/logo3.png");
                int logoWidth = 280;
                int logoHeight = 186;
                int logoX = (panelW - logoWidth) / 2;
                int logoY = centerY - logoHeight - 20;

                if (logoUrl != null) {
                    Image logo = new ImageIcon(logoUrl).getImage();
                    g2.drawImage(logo, logoX, logoY, logoWidth, logoHeight, this);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                    FontMetrics fm = g2.getFontMetrics();
                    String fallback = "🏨";
                    g2.drawString(fallback, (panelW - fm.stringWidth(fallback)) / 2, logoY + 100);
                }

                g2.setColor(new Color(129, 140, 248));
                g2.setFont(new Font("Century Gothic", Font.BOLD, 14));
                FontMetrics fm1 = g2.getFontMetrics();
                String badge = "HOTEL RESERVATION & CONCIERGE OPERATIONS";
                g2.drawString(badge, (panelW - fm1.stringWidth(badge)) / 2, centerY + 36);

                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Century Gothic", Font.PLAIN, 12));
                FontMetrics fm2 = g2.getFontMetrics();
                String sub = "Luxury Hospitality • Self Service Portal • Real-Time Inventory Sync";
                g2.drawString(sub, (panelW - fm2.stringWidth(sub)) / 2, centerY + 62);

                g2.setColor(new Color(100, 116, 139, 160));
                g2.setFont(new Font("Century Gothic", Font.PLAIN, 11));
                String ver = "v2.6 Enterprise Multi-Tier Deployment";
                g2.drawString(ver, (panelW - g2.getFontMetrics().stringWidth(ver)) / 2, panelH - 32);

                g2.dispose();
            }
        };
    }

    private JPanel createLoginFormPanel() {
        JPanel outerContainer = new JPanel(new GridBagLayout());
        outerContainer.setBackground(Color.WHITE);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setPreferredSize(new Dimension(420, 520));
        formCard.setMaximumSize(new Dimension(420, 520));
        formCard.setBorder(new EmptyBorder(10, 10, 10, 10));

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

        formCard.add(switchPillContainer);
        formCard.add(Box.createRigidArea(new Dimension(0, 26)));

        JLabel lblGreeting = new JLabel("Welcome Back");
        lblGreeting.setFont(new Font("Century Gothic", Font.BOLD, 24));
        lblGreeting.setForeground(new Color(15, 23, 42));
        lblGreeting.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSubHeader = new JLabel("Enter your guest email or phone to access portal amenities");
        lblSubHeader.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        lblSubHeader.setForeground(new Color(100, 116, 139));
        lblSubHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(lblGreeting);
        formCard.add(Box.createRigidArea(new Dimension(0, 6)));
        formCard.add(lblSubHeader);
        formCard.add(Box.createRigidArea(new Dimension(0, 26)));

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

        formCard.add(lblInputHeader);
        formCard.add(Box.createRigidArea(new Dimension(0, 6)));
        formCard.add(fieldIdentifier);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        formCard.add(lblPassHeader);
        formCard.add(Box.createRigidArea(new Dimension(0, 6)));
        formCard.add(fieldPass);
        formCard.add(Box.createRigidArea(new Dimension(0, 24)));

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

        getRootPane().setDefaultButton(btnLogin);

        actionRow.add(btnClear);
        actionRow.add(btnLogin);

        formCard.add(actionRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 22)));

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

        formCard.add(registerRow);

        outerContainer.add(formCard);
        return outerContainer;
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
        wrapper.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
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
                            pwdField.setEchoChar(isPasswordVisible ? (char) 0 : defaultEchoChar);
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

                    java.awt.geom.Path2D eyePath = new java.awt.geom.Path2D.Double();
                    eyePath.moveTo(cx - 9, cy);
                    eyePath.quadTo(cx, cy - 6.5, cx + 9, cy);
                    eyePath.quadTo(cx, cy + 6.5, cx - 9, cy);
                    g2.draw(eyePath);

                    if (isPasswordVisible) {
                        g2.fillOval(cx - 3, cy - 3, 6, 6);
                    } else {
                        g2.drawOval(cx - 3, cy - 3, 6, 6);
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new User_UI().setVisible(true));
    }
}