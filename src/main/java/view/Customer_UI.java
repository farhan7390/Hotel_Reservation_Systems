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
}