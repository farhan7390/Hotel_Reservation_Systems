package util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailService {

    // Configure your SMTP credentials
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "soemoelwin414@gmail.com"; // Replace with your email
    private static final String SENDER_APP_PASSWORD = "lpxchiqwrxpvgluh"; // Use Google App Password (not standard password)

    private static final ExecutorService mailExecutor = Executors.newSingleThreadExecutor();

    // 1. Generate secure 6-digit OTP
    public static String generateOTP() {
        int code = 100000 + new Random().nextInt(900000);
        return String.valueOf(code);
    }

    // 2. Dispatch OTP Verification Email
    public static void sendOtpVerificationEmail(String recipientEmail, String recipientName, String otpCode) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.contains("@")) {
            System.err.println("Skipping OTP email: No valid recipient email provided.");
            return;
        }

        mailExecutor.submit(() -> {
            try {
                Properties props = getSmtpProperties();

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, "HMS Security Team"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Your Verification Code: " + otpCode);

                String htmlContent = buildOtpHtml(recipientName, otpCode);
                message.setContent(htmlContent, "text/html; charset=UTF-8");
                Transport.send(message);

                System.out.println("OTP email dispatched to: " + recipientEmail);
            } catch (Exception e) {
                System.err.println("Failed to send OTP email: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // 3. Dispatch Booking Confirmation & Invoice Email
    public static void sendBookingConfirmationEmail(
            String recipientEmail,
            String guestName,
            String bookingRef,
            String roomNo,
            String categoryName,
            String tierName,
            LocalDate checkIn,
            LocalDate checkOut,
            BigDecimal totalAmount
    ) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.contains("@")) {
            System.err.println("Skipping email: No valid recipient email provided.");
            return;
        }

        mailExecutor.submit(() -> {
            try {
                Properties props = getSmtpProperties();

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, "HMS Grand Luxury Hotel & Resort"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Reservation Confirmed - " + bookingRef + " [" + roomNo + "]");

                String htmlContent = buildBookingInvoiceHtml(
                        guestName, bookingRef, roomNo, categoryName, tierName, checkIn, checkOut, totalAmount
                );

                message.setContent(htmlContent, "text/html; charset=UTF-8");
                Transport.send(message);

                System.out.println("Confirmation email sent successfully to: " + recipientEmail);
            } catch (Exception e) {
                System.err.println("Failed to send booking email: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static Properties getSmtpProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        return props;
    }

    private static String buildOtpHtml(String name, String otpCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 20px; }
                    .card { max-width: 500px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; border: 1px solid #e2e8f0; }
                    .header { background: linear-gradient(135deg, #4f46e5, #7c3aed); padding: 24px; color: #ffffff; text-align: center; }
                    .content { padding: 24px; text-align: center; color: #1e293b; }
                    .otp-box { display: inline-block; font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #4f46e5; background: #eef2ff; padding: 14px 28px; border-radius: 8px; border: 2px dashed #818cf8; margin: 20px 0; }
                    .footer { font-size: 11px; color: #94a3b8; padding: 16px; border-top: 1px solid #f1f5f9; text-align: center; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="header">
                        <h2 style="margin:0;">Email Verification</h2>
                        <p style="margin:4px 0 0 0; font-size:12px; opacity:0.9;">HMS Hotel Management System</p>
                    </div>
                    <div class="content">
                        <p>Hello <b>%s</b>,</p>
                        <p>Please use the verification code below to verify your email address:</p>
                        <div class="otp-box">%s</div>
                        <p style="font-size:12px; color:#64748b;">This code expires in 10 minutes. Do not share this code with anyone.</p>
                    </div>
                    <div class="footer">
                        © 2026 HMS Hotel Management System. All rights reserved.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name != null && !name.isEmpty() ? name : "Guest", otpCode);
    }

    private static String buildBookingInvoiceHtml(
            String guestName,
            String bookingRef,
            String roomNo,
            String categoryName,
            String tierName,
            LocalDate checkIn,
            LocalDate checkOut,
            BigDecimal totalAmount
    ) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 20px; color: #1e293b; }
                    .invoice-card { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); border: 1px solid #e2e8f0; }
                    .header { background: linear-gradient(135deg, #4f46e5, #7c3aed); padding: 30px 24px; color: #ffffff; text-align: center; }
                    .header h1 { margin: 0; font-size: 24px; letter-spacing: 0.5px; }
                    .header p { margin: 6px 0 0 0; opacity: 0.9; font-size: 13px; }
                    .content { padding: 28px 24px; }
                    .greeting { font-size: 16px; margin-bottom: 20px; }
                    .details-table { width: 100%%; border-collapse: collapse; margin-bottom: 24px; }
                    .details-table td { padding: 12px 8px; border-bottom: 1px solid #f1f5f9; font-size: 13px; }
                    .details-table td.label { color: #64748b; font-weight: 600; width: 40%%; }
                    .details-table td.value { color: #0f172a; font-weight: bold; text-align: right; }
                    .footer { text-align: center; font-size: 12px; color: #94a3b8; padding: 20px; border-top: 1px solid #f1f5f9; background: #fafafa; }
                </style>
            </head>
            <body>
                <div class="invoice-card">
                    <div class="header">
                        <h1>HMS GRAND LUXURY HOTEL</h1>
                        <p>Official Booking Confirmation &amp; Invoice</p>
                    </div>
                    <div class="content">
                        <div class="greeting">Dear <b>%s</b>,</div>
                        <p style="font-size: 13px; line-height: 1.5; color: #475569;">
                            Thank you for choosing our hotel. Your room reservation has been confirmed. Below are your booking and invoice details:
                        </p>
                        
                        <table class="details-table">
                            <tr>
                                <td class="label">Booking Reference</td>
                                <td class="value">%s</td>
                            </tr>
                            <tr>
                                <td class="label">Assigned Room</td>
                                <td class="value">%s (%s)</td>
                            </tr>
                            <tr>
                                <td class="label">Booking Experience Tier</td>
                                <td class="value">%s</td>
                            </tr>
                            <tr>
                                <td class="label">Check-In Date</td>
                                <td class="value">%s (From 02:00 PM)</td>
                            </tr>
                            <tr>
                                <td class="label">Check-Out Date</td>
                                <td class="value">%s (Until 12:00 PM)</td>
                            </tr>
                        </table>

                        <table style="width: 100%%; background: #f8fafc; border-radius: 8px; padding: 14px; border: 1px dashed #cbd5e1; margin-bottom: 20px;">
                            <tr>
                                <td style="font-size: 14px; font-weight: bold; color: #334155;">Total Room Tariff:</td>
                                <td style="font-size: 18px; font-weight: bold; color: #4f46e5; text-align: right;">%,d MMK</td>
                            </tr>
                        </table>

                        <p style="font-size: 12px; color: #64748b; line-height: 1.5;">
                            <b>Important Check-in Notice:</b> Please present your National ID / Passport along with this confirmation reference upon arrival at the front desk.
                        </p>
                    </div>
                    <div class="footer">
                        Need assistance? Contact our 24/7 concierge at support@hotel.com<br>
                        &copy; 2026 HMS Hotel Management System. All rights reserved.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                guestName != null && !guestName.isEmpty() ? guestName : "Valued Guest",
                bookingRef,
                roomNo,
                categoryName,
                tierName,
                checkIn != null ? checkIn.toString() : "N/A",
                checkOut != null ? checkOut.toString() : "N/A",
                totalAmount != null ? totalAmount.longValue() : 0L
        );
    }
}