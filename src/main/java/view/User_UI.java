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