/*
package view;

import model.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Customer_UI extends JFrame {
    private static final Color TEXT_GRAY = new Color(100, 100, 100);

    public Customer_UI() {
        setLayout(new GridLayout(1, 2));
        setTitle("Hotel Reservation Systems - Customer Register");
        setSize(850, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        User_UI userUi = new User_UI();

        add(userUi.leftBanner());
        add(rightBanner());
    }

    private JPanel rightBanner() {
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(25, 45, 25, 45));

        JLabel lblFormTitle = new JLabel("Customer Register");
        lblFormTitle.setFont(new Font("Century Gothic", Font.BOLD, 22));
        lblFormTitle.setForeground(Color.BLACK);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFullName = new JLabel("Full Name");
        lblFullName.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblFullName.setForeground(Color.BLACK);
        lblFullName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtFullName = createStyledTextField();

        JLabel lblPhone = new JLabel("Phone");
        lblPhone.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblPhone.setForeground(Color.BLACK);
        lblPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtPhone = createStyledTextField();

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblEmail.setForeground(Color.BLACK);
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtEmail = createStyledTextField();

        JLabel lblIDProof = new JLabel("ID (NRC or Passport Number)");
        lblIDProof.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblIDProof.setForeground(Color.BLACK);
        lblIDProof.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtIDProof = createStyledTextField();

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblPass.setForeground(Color.BLACK);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = createStyledPasswordField();

        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(1400, 36));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(71, 85, 105));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnClear.addActionListener(e -> {
            txtFullName.setText("");
            txtPhone.setText("");
            txtEmail.setText("");
            txtPass.setText("");
            txtIDProof.setText("");
        });

        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnRegister.setBackground(new Color(16, 185, 129));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRegister.addActionListener(e -> {
            String fullName = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtPass.getPassword()).trim();
            String idProof = txtIDProof.getText().trim();

            if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || idProof.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean registered = registerCustomerInDB(fullName, phone, email, password, idProof);

            if (registered) {
                JOptionPane.showMessageDialog(this, "Registration Successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                Customer_UI.this.dispose();
                SwingUtilities.invokeLater(() -> new User_UI().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed. Email, Phone, or Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonRow.add(btnClear);
        buttonRow.add(btnRegister);

        JPanel footerLink = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerLink.setOpaque(false);
        footerLink.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginLink = new JLabel("<html>Already a Member? <span style='color:#6366F1; font-weight:bold;'>Login Here</span></html>");
        loginLink.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        loginLink.setForeground(TEXT_GRAY);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Customer_UI.this.dispose();
                SwingUtilities.invokeLater(() -> new User_UI().setVisible(true));
            }
        });
        footerLink.add(loginLink);

        formPanel.add(lblFormTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(lblFullName);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtFullName);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(lblPhone);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtPhone);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(lblEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(lblPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(lblIDProof);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtIDProof);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(buttonRow);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(footerLink);

        return formPanel;
    }

    private boolean registerCustomerInDB(String fullName, String phone, String email, String password, String idProof) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String userId = "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String guestId = "GST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String username = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;

        String insertUserSql = "INSERT INTO Users (user_id, full_name, username, email, password_hash, phone, role, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'STAFF', 'ACTIVE')";

        String insertGuestSql = "INSERT INTO Guests (guest_id, user_id, full_name, nid_passport, phone, email, city, vip_tier, loyalty_points, guest_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'Yangon', 'STANDARD', 0, 'ACTIVE')";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement pstUser = conn.prepareStatement(insertUserSql)) {
                pstUser.setString(1, userId);
                pstUser.setString(2, fullName);
                pstUser.setString(3, username);
                pstUser.setString(4, email);
                pstUser.setString(5, password);
                pstUser.setString(6, phone);
                pstUser.executeUpdate();
            }

            try (PreparedStatement pstGuest = conn.prepareStatement(insertGuestSql)) {
                pstGuest.setString(1, guestId);
                pstGuest.setString(2, userId);
                pstGuest.setString(3, fullName);
                pstGuest.setString(4, idProof);
                pstGuest.setString(5, phone);
                pstGuest.setString(6, email);
                pstGuest.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        tf.setMaximumSize(new Dimension(1400, 32));
        tf.setPreferredSize(new Dimension(1400, 32));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return tf;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        pf.setMaximumSize(new Dimension(1400, 32));
        pf.setPreferredSize(new Dimension(1400, 32));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return pf;
    }
}*//*


package view;

import model.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Customer_UI extends JFrame {
    private static final Color TEXT_GRAY = new Color(100, 100, 100);

    public Customer_UI() {
        setLayout(new GridLayout(1, 2));
        setTitle("Hotel Reservation Systems - Customer Register");
        setSize(850, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        User_UI userUi = new User_UI();

        util.AppIcon.setFrameIcon(this, "/images/favicon1.png");

        add(userUi.leftBanner());
        add(rightBanner());
    }

    private JPanel rightBanner() {
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(25, 45, 25, 45));

        JLabel lblFormTitle = new JLabel("Customer Register");
        lblFormTitle.setFont(new Font("Century Gothic", Font.BOLD, 22));
        lblFormTitle.setForeground(Color.BLACK);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFullName = new JLabel("Full Name");
        lblFullName.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblFullName.setForeground(Color.BLACK);
        lblFullName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtFullName = createStyledTextField();

        JLabel lblPhone = new JLabel("Phone");
        lblPhone.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblPhone.setForeground(Color.BLACK);
        lblPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtPhone = createStyledTextField();

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblEmail.setForeground(Color.BLACK);
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtEmail = createStyledTextField();

        JLabel lblIDProof = new JLabel("ID (NRC or Passport Number)");
        lblIDProof.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblIDProof.setForeground(Color.BLACK);
        lblIDProof.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtIDProof = createStyledTextField();

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblPass.setForeground(Color.BLACK);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = createStyledPasswordField();

        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(1400, 36));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(71, 85, 105));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnClear.addActionListener(e -> {
            txtFullName.setText("");
            txtPhone.setText("");
            txtEmail.setText("");
            txtPass.setText("");
            txtIDProof.setText("");
        });

        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnRegister.setBackground(new Color(16, 185, 129));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRegister.addActionListener(e -> {
            String fullName = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtPass.getPassword()).trim();
            String idProof = txtIDProof.getText().trim();

            if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || idProof.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Trigger OTP Verification Dialog
            EmailVerificationDialog verifyDialog = new EmailVerificationDialog(this, email, fullName);
            verifyDialog.setVisible(true);

            // 2. Abort if user closed dialog without validating OTP
            if (!verifyDialog.isVerified()) {
                JOptionPane.showMessageDialog(this, "Registration cancelled: Email address was not verified.", "Verification Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Commit Verified Record into Database
            boolean registered = registerCustomerInDB(fullName, phone, email, password, idProof);

            if (registered) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Your email has been verified. You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                Customer_UI.this.dispose();
                SwingUtilities.invokeLater(() -> new User_UI().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed. Email, Phone, or Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        buttonRow.add(btnClear);
        buttonRow.add(btnRegister);

        JPanel footerLink = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerLink.setOpaque(false);
        footerLink.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginLink = new JLabel("<html>Already a Member? <span style='color:#6366F1; font-weight:bold;'>Login Here</span></html>");
        loginLink.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        loginLink.setForeground(TEXT_GRAY);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Customer_UI.this.dispose();
                SwingUtilities.invokeLater(() -> new User_UI().setVisible(true));
            }
        });
        footerLink.add(loginLink);

        formPanel.add(lblFormTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(lblFullName);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtFullName);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(lblPhone);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtPhone);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(lblEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(lblPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(lblIDProof);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(txtIDProof);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(buttonRow);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(footerLink);

        return formPanel;
    }

    private boolean registerCustomerInDB(String fullName, String phone, String email, String password, String idProof) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String guestId = "GST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String username = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;

//        String insertUserSql = "INSERT INTO Users (user_id, full_name, username, email, password_hash, phone, role, status) " +
//                "VALUES (?, ?, ?, ?, ?, ?, 'CUSTOMER', 'ACTIVE')";

        String insertGuestSql = "INSERT INTO Guests (guest_id, full_name, nid_passport, phone, email, password_hash, city, vip_tier, loyalty_points, guest_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'Yangon', 'STANDARD', 0, 'ACTIVE')";

        try {
            conn.setAutoCommit(false);

            */
/*try (PreparedStatement pstUser = conn.prepareStatement(insertUserSql)) {
                pstUser.setString(1, userId);
                pstUser.setString(2, fullName);
                pstUser.setString(3, username);
                pstUser.setString(4, email);
                pstUser.setString(5, password);
                pstUser.setString(6, phone);
                pstUser.executeUpdate();
            }*//*


            try (PreparedStatement pstGuest = conn.prepareStatement(insertGuestSql)) {
                pstGuest.setString(1, guestId);
                pstGuest.setString(2, fullName);
                pstGuest.setString(3, idProof);
                pstGuest.setString(4, phone);
                pstGuest.setString(5, email);
                pstGuest.setString(6, password);
                pstGuest.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        tf.setMaximumSize(new Dimension(1400, 32));
        tf.setPreferredSize(new Dimension(1400, 32));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return tf;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        pf.setMaximumSize(new Dimension(1400, 32));
        pf.setPreferredSize(new Dimension(1400, 32));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return pf;
    }
}*/

package view;

import model.DBConnection;
import util.AppIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Customer_UI extends JFrame {

    private JTextField txtFullName, txtPhone, txtEmail, txtIDProof;
    private JPasswordField txtPass;

    public Customer_UI() {
        setTitle("Grand Horizon Suites - Guest Registration");
        setSize(980, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        AppIcon.setFrameIcon(this, "/images/favicon1.png");

        add(createBrandingPanel());
        add(createRegisterFormPanel());
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

                // Ambient Glow Orbs (Emerald + Indigo)
                g2.setColor(new Color(99, 102, 241, 35));
                g2.fillOval(-60, -60, 280, 280);

                g2.setColor(new Color(168, 85, 247, 28));
                g2.fillOval(getWidth() - 200, getHeight() - 220, 300, 300);

                int panelW = getWidth();
                int panelH = getHeight();
                int centerY = panelH / 2 - 15;

                // Prominent Centered Hero Logo
                URL logoUrl = getClass().getResource("/images/logo3.png");
                if (logoUrl != null) {
                    Image logo = new ImageIcon(logoUrl).getImage();

                    int logoWidth = 240;
                    int logoHeight = 160;
                    int logoX = (panelW - logoWidth) / 2;
                    int logoY = centerY - (logoHeight / 2) - 30;

                    g2.drawImage(logo, logoX, logoY, logoWidth, logoHeight, this);
                }

                // Subtitle Badges (Cleaned up, no redundant title text)
                g2.setColor(new Color(129, 140, 248));
                g2.setFont(new Font("Century Gothic", Font.BOLD, 12));
                FontMetrics fm1 = g2.getFontMetrics();
                String badge = "EXCLUSIVE GUEST MEMBERSHIP";
                g2.drawString(badge, (panelW - fm1.stringWidth(badge)) / 2, centerY + 100);

                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Century Gothic", Font.PLAIN, 11));
                FontMetrics fm2 = g2.getFontMetrics();
                String sub = "Unlock instant loyalty rewards, fast check-in & amenities";
                g2.drawString(sub, (panelW - fm2.stringWidth(sub)) / 2, centerY + 122);

                // Bottom Brand Footer
                g2.setColor(new Color(100, 116, 139, 150));
                g2.setFont(new Font("Century Gothic", Font.PLAIN, 10));
                String ver = "Grand Horizon Hospitality Group";
                g2.drawString(ver, (panelW - g2.getFontMetrics().stringWidth(ver)) / 2, panelH - 24);

                g2.dispose();
            }
        };
    }

    private JPanel createRegisterFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 42, 30, 42));

        // Form Title
        JLabel lblTitle = new JLabel("Create Guest Account");
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Fill in your personal details to register for self-service");
        lblSub.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(lblSub);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        // Inputs with icons
        txtFullName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtIDProof = new JTextField();
        txtPass = new JPasswordField();

        addInputField(panel, "Full Legal Name", txtFullName, "👤");
        addInputField(panel, "Phone Number", txtPhone, "📞");
        addInputField(panel, "Email Address", txtEmail, "✉️");
        addInputField(panel, "NRC or Passport ID", txtIDProof, "🪪");
        addInputField(panel, "Account Password", txtPass, "🔒");

        panel.add(Box.createRigidArea(new Dimension(0, 4)));

        // Action Buttons
        JPanel actionRow = new JPanel(new GridLayout(1, 2, 12, 0));
        actionRow.setOpaque(false);
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(100, 116, 139));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> {
            txtFullName.setText("");
            txtPhone.setText("");
            txtEmail.setText("");
            txtIDProof.setText("");
            txtPass.setText("");
            txtFullName.requestFocus();
        });

        JButton btnRegister = new JButton("Create Account") {
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
        btnRegister.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setOpaque(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> handleRegistration());

        actionRow.add(btnClear);
        actionRow.add(btnRegister);

        panel.add(actionRow);
        panel.add(Box.createRigidArea(new Dimension(0, 24)));

        // Login Redirect Link
        JPanel footerLink = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        footerLink.setOpaque(false);
        footerLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        footerLink.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lblNoAcc = new JLabel("Already registered?");
        lblNoAcc.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        lblNoAcc.setForeground(new Color(100, 116, 139));

        JLabel lblLogin = new JLabel("<html><u>Sign In Here</u></html>");
        lblLogin.setFont(new Font("Century Gothic", Font.BOLD, 12));
        lblLogin.setForeground(new Color(99, 102, 241));
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Customer_UI.this.dispose();
                SwingUtilities.invokeLater(() -> new User_UI().setVisible(true));
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblLogin.setForeground(new Color(79, 70, 229));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblLogin.setForeground(new Color(99, 102, 241));
            }
        });

        footerLink.add(lblNoAcc);
        footerLink.add(lblLogin);

        panel.add(footerLink);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void addInputField(JPanel parent, String labelText, JComponent input, String icon) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 11));
        lbl.setForeground(new Color(51, 65, 85));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel wrapper = createRoundedInputBox(input, icon);

        parent.add(lbl);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
        parent.add(wrapper);
        parent.add(Box.createRigidArea(new Dimension(0, 8)));
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
                    g2.setColor(new Color(16, 185, 129));
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
        wrapper.setPreferredSize(new Dimension(Integer.MAX_VALUE, 36));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        wrapper.setBorder(new EmptyBorder(0, 10, 0, 10));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblIcon = new JLabel(iconSymbol);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        inputComponent.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        inputComponent.setForeground(new Color(15, 23, 42));
        inputComponent.setOpaque(false);
        if (inputComponent instanceof JTextField) {
            ((JTextField) inputComponent).setBorder(null);
        }

        wrapper.add(lblIcon, BorderLayout.WEST);
        wrapper.add(inputComponent, BorderLayout.CENTER);
        return wrapper;
    }

    private void handleRegistration() {
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPass.getPassword()).trim();
        String idProof = txtIDProof.getText().trim();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || idProof.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Email Verification Dialog hook
        EmailVerificationDialog verifyDialog = new EmailVerificationDialog(this, email, fullName);
        verifyDialog.setVisible(true);

        if (!verifyDialog.isVerified()) {
            JOptionPane.showMessageDialog(this, "Registration aborted: Email was not verified.", "Verification Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean registered = registerCustomerInDB(fullName, phone, email, password, idProof);
        if (registered) {
            JOptionPane.showMessageDialog(this, "Registration Successful! You can now sign in.", "Welcome", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            SwingUtilities.invokeLater(() -> new User_UI().setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed. Email or Phone number may already be registered.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean registerCustomerInDB(String fullName, String phone, String email, String password, String idProof) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String guestId = "GST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String insertGuestSql = "INSERT INTO Guests (guest_id, full_name, nid_passport, phone, email, password_hash, city, vip_tier, loyalty_points, guest_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'Yangon', 'STANDARD', 0, 'ACTIVE')";

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement pstGuest = conn.prepareStatement(insertGuestSql)) {
                pstGuest.setString(1, guestId);
                pstGuest.setString(2, fullName);
                pstGuest.setString(3, idProof);
                pstGuest.setString(4, phone);
                pstGuest.setString(5, email);
                pstGuest.setString(6, password);
                pstGuest.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
}
