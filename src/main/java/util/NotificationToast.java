package util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class NotificationToast extends JWindow {
    public NotificationToast(Window parent, String title, String message, String bookingRef) {
        super(parent);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 0));


        JPanel toastPanel = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), getWidth(), 0, new Color(30, 41, 59));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));

                g2.setColor(new Color(16, 185, 129));
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);

                g2.setColor(new Color(99, 102, 241, 90));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() -1, getHeight() -1, 14, 14));
                g2.dispose();
            }
        };

        toastPanel.setOpaque(false);
        toastPanel.setBorder(new EmptyBorder(12, 18, 12, 16));

        JLabel lblIcon = new JLabel("🛎️");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        lblIcon.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 13));
        lblTitle.setForeground(new Color(248, 250, 252));

        JLabel lblMsg = new JLabel("<html>" + message + "</html>");
        lblMsg.setFont(new Font("Century Gothic", Font.PLAIN, 11));
        lblMsg.setForeground(new Color(203, 213, 225));

        JLabel lblRef = new JLabel("Ref: " + bookingRef);
        lblRef.setFont(new Font("Century Gothic", Font.BOLD, 10));
        lblRef.setForeground(new Color(129, 140, 248));

        textPanel.add(lblTitle);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(lblMsg);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(lblRef);

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Century Gothic", Font.BOLD, 12));
        btnClose.setForeground(new Color(148, 163, 184));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        toastPanel.add(lblIcon, BorderLayout.WEST);
        toastPanel.add(textPanel, BorderLayout.CENTER);
        toastPanel.add(btnClose, BorderLayout.EAST);

        getContentPane().add(toastPanel);
        pack();
        setSize(340, 78);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screen.width - getWidth() - 25;
        int y = screen.height - getHeight() - 65;
        setLocation(x, y);

        try {
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception ignored) {}
    }

    public void showWithAutoDismiss(int durationMillis) {
        setVisible(true);
        toFront();
        setAlwaysOnTop(true);
        Timer dismissTimer = new Timer(durationMillis, e -> {
            dispose();
            ((Timer) e.getSource()).stop();
        });
        dismissTimer.setRepeats(false);
        dismissTimer.start();
    }
}
