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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 640));
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

                g2.setColor(new Color(16, 185, 129, 25));
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
                String badge = "EXCLUSIVE GUEST MEMBERSHIP";
                g2.drawString(badge, (panelW - fm1.stringWidth(badge)) / 2, centerY + 36);

                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Century Gothic", Font.PLAIN, 12));
                FontMetrics fm2 = g2.getFontMetrics();
                String sub = "Unlock instant loyalty rewards, fast check-in & VIP amenities";
                g2.drawString(sub, (panelW - fm2.stringWidth(sub)) / 2, centerY + 62);

                g2.setColor(new Color(100, 116, 139, 160));
                g2.setFont(new Font("Century Gothic", Font.PLAIN, 11));
                String ver = "Grand Horizon Hospitality Group • Global Guest Experience";
                g2.drawString(ver, (panelW - g2.getFontMetrics().stringWidth(ver)) / 2, panelH - 32);

                g2.dispose();
            }
        };
    }

    private JPanel createRegisterFormPanel() {
        JPanel outerContainer = new JPanel(new GridBagLayout());
        outerContainer.setBackground(Color.WHITE);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setPreferredSize(new Dimension(460, 580));
        formCard.setMaximumSize(new Dimension(460, 580));
        formCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("Create Guest Account");
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 24));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Fill in your personal details to register for self-service portal amenities");
        lblSub.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(lblTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 4)));
        formCard.add(lblSub);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        txtFullName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtIDProof = new JTextField();
        txtPass = new JPasswordField();

        addInputField(formCard, "Full Legal Name", txtFullName, "👤");
        addInputField(formCard, "Phone Number", txtPhone, "📞");
        addInputField(formCard, "Email Address", txtEmail, "✉️");
        addInputField(formCard, "NRC or Passport ID", txtIDProof, "🪪");
        addInputField(formCard, "Account Password", txtPass, "🔒");

        formCard.add(Box.createRigidArea(new Dimension(0, 6)));

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

        getRootPane().setDefaultButton(btnRegister);

        actionRow.add(btnClear);
        actionRow.add(btnRegister);

        formCard.add(actionRow);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

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

        formCard.add(footerLink);

        outerContainer.add(formCard);
        return outerContainer;
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
        parent.add(Box.createRigidArea(new Dimension(0, 10)));
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
        wrapper.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        wrapper.setBorder(new EmptyBorder(0, 12, 0, 12));
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