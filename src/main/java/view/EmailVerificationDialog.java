package view;

import util.EmailService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class EmailVerificationDialog extends JDialog {

    private final String recipientEmail;
    private final String recipientName;
    private String currentOtp;
    private boolean isVerified = false;

    private JTextField txtCode;
    private JButton btnVerify, btnResend;
    private JLabel lblStatus;

    public EmailVerificationDialog(Frame parent, String recipientEmail, String recipientName) {
        super(parent, "Verify Email Address", true);
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;

        setSize(380, 290);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        add(createContent(), BorderLayout.CENTER);
        sendCode();
    }

    private JPanel createContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblTitle = new JLabel("Enter 6-Digit Code");
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 41, 59));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("<html><center>We sent a verification code to:<br><b>" + recipientEmail + "</b></center></html>");
        lblSub.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtCode = new JTextField();
        txtCode.setFont(new Font("Century Gothic", Font.BOLD, 22));
        txtCode.setHorizontalAlignment(JTextField.CENTER);
        txtCode.setMaximumSize(new Dimension(180, 44));
        txtCode.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(99, 102, 241), 2, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        lblStatus = new JLabel("Sending verification code...");
        lblStatus.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(100, 116, 139));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnVerify = new JButton("Verify & Confirm");
        btnVerify.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnVerify.setBackground(new Color(99, 102, 241));
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnVerify.setFocusPainted(false);
        btnVerify.setBorderPainted(false);
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerify.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVerify.addActionListener(e -> verifyCode());

        btnResend = new JButton("Resend Code");
        btnResend.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        btnResend.setForeground(new Color(99, 102, 241));
        btnResend.setContentAreaFilled(false);
        btnResend.setBorderPainted(false);
        btnResend.setFocusPainted(false);
        btnResend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResend.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnResend.addActionListener(e -> sendCode());

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(lblSub);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(txtCode);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(lblStatus);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(btnVerify);
        panel.add(btnResend);

        return panel;
    }

    private void sendCode() {
        this.currentOtp = EmailService.generateOTP();
        lblStatus.setText("Code sent! Check your inbox.");
        lblStatus.setForeground(new Color(16, 185, 129));
        EmailService.sendOtpVerificationEmail(recipientEmail, recipientName, currentOtp);
    }

    private void verifyCode() {
        String entered = txtCode.getText().trim();
        if (entered.equals(currentOtp)) {
            isVerified = true;
            JOptionPane.showMessageDialog(this, "Email verified successfully!", "Verified", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            lblStatus.setText("Invalid code. Please try again.");
            lblStatus.setForeground(new Color(239, 68, 68));
            txtCode.selectAll();
            txtCode.requestFocus();
        }
    }

    public boolean isVerified() {
        return isVerified;
    }
}