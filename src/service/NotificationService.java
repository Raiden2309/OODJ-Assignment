package service;

public class NotificationService {
    private Object javaMailAPI = new Object();
    private final String senderEmail = "crs_noreply@university.edu";

    public boolean sendEmail(String recipient, String subject, String body){
        System.err.println("Notification: Attempting to send email to " + recipient);
    }

    public boolean sendAlert(String type, String recipient){
        return sendEmail(recipient, type + "Alert", "System alert: Your status has changed.");
    }
}