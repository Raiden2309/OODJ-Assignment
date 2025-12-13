package service;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class NotificationService {

    //====================
    //Field
    //====================

    //SMTP
    final String smtpHost = "smtp.gmail.com";
    final int smtpPort = 587;
    final String smtpUsername = "taichi.ihciat@gmail.com";
    final String smtpPassword = "hlptamkihphoubmg";
    final boolean useTls = true;
    //Sender information
    final String fromAddress = "crs_noreply@university.edu";
    final String fromName = "Email-Notification";

    // JavaMail
    final Properties mailProperties;
    final Session mailSession;

    //Information about the currently logged-in user
    private String currentFirstName;
    private String currentLastName;
    private String currentEmail;

    //====================
    //constructor
    //====================

    public NotificationService(String currentFirstName, String currentLastName, String currentEmail) {
        this.currentFirstName = currentFirstName;
        this.currentLastName = currentLastName;
        this.currentEmail = currentEmail;

        this.mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", smtpHost);
        mailProperties.put("mail.smtp.port", String.valueOf(smtpPort));
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.starttls.enable", String.valueOf(useTls));

        this.mailSession = Session.getInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });
    }

    // Default constructor for cases where we don't have user info yet (optional but useful)
    public NotificationService() {
        this("Unknown", "User", "unknown@example.com");
    }

    //====================
    // Method (Email Transmission)
    //====================

    public void sendMail(String recipientEmail, String subject, String body) {
        // Validation to prevent crashing if email is missing
        if (recipientEmail == null || recipientEmail.isEmpty() || recipientEmail.contains("unknown")) {
            System.err.println("Notification Skipped: Invalid recipient email.");
            return;
        }

        try {
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(fromAddress, fromName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);

        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            // e.printStackTrace(); // Optional: uncomment for deep debugging
        }
    }

    // Alias for Student.java if it calls sendEmail instead of sendMail
    public void sendEmail(String recipientEmail, String subject, String body) {
        sendMail(recipientEmail, subject, body);
    }

    //====================
    // Method (Notification Types)
    //====================

    public void sendLoginNotificationEmail() {
        String subject = "Login Notification";
        String body = "Dear " + currentFirstName + " " + currentLastName + ",\n\n"
                + "A login to your CRS account was detected.\n"
                + "If this was not you, please contact support immediately.\n\nRegards,\nCRS Team";

        sendMail(currentEmail, subject, body);
    }

    public void sendAccountCreatedEmail() {
        String subject = "Welcome to CRS - Account Created";
        String body = "Dear " + currentFirstName + " " + currentLastName + ",\n\n"
                + "Your CRS account has been successfully created.\n"
                + "You may now log in.\n\nRegards,\nCRS Team";

        sendMail(currentEmail, subject, body);
    }

    public void sendAccountUpdatedEmail() {
        String subject = "Your CRS Account Has Been Updated";
        String body = "Dear " + currentFirstName + " " + currentLastName + ",\n\n"
                + "Your account information has been updated.\n\nRegards,\nCRS Team";

        sendMail(currentEmail, subject, body);
    }

    public void sendPasswordChangedEmail() {
        String subject = "Your CRS Password Has Been Changed";
        String body = "Dear " + currentFirstName + " " + currentLastName + ",\n\n"
                + "Your password has been successfully updated.\n\nRegards,\nCRS Team";

        sendMail(currentEmail, subject, body);
    }

    public void sendAcademicReportEmail(int semester, double cgpa) {
        String subject = "Academic Report Available";
        String body = "Dear " + currentFirstName + " " + currentLastName + ",\n\n"
                + "Your academic report for semester " + semester
                + " is now available.\n"
                + "CGPA: " + cgpa + "\n\nRegards,\nCRS Team";

        sendMail(currentEmail, subject, body);
    }
}