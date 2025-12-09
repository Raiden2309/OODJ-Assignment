package service;

import domain.Enrollment;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class EnrollmentDAO {

    private final String ENROLMENT_FILE_PATH = "data/enrolment_records.csv";
    // Date format for the CSV file
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public boolean saveEnrolment(Enrollment enrollment) {
        // 'true' in FileWriter constructor enables append mode (don't overwrite file)
        try (PrintWriter writer = new PrintWriter(new FileWriter(ENROLMENT_FILE_PATH, true))) {

            // Format the date if available, otherwise use current time or placeholder
            String dateString = (enrollment.getEnrolmentDate() != null)
                    ? DATE_FORMAT.format(enrollment.getEnrolmentDate())
                    : "Unknown Date";

            // Plan ID handling
            String planID = "UNKNOWN_PLAN";
            if (enrollment.getPlan() != null) {
                planID = enrollment.getPlan().getPlanID();
            }

            // Create the CSV line: EnrolmentID, StudentID, PlanID, Date, Status
            // Note: Adjusting to match your friend's 7-column CSV format if needed,
            // but for now sticking to the 5-column standard we established for persistence.
            // If you need to match your friend's format, you'll need to fetch the course details.
            String record = String.join(",",
                    enrollment.getEnrollmentId(),
                    enrollment.getStudentId(),
                    planID,
                    dateString,
                    "Active" // Default status
            );

            writer.println(record);
            System.err.println("DAO: Enrolment saved: " + enrollment.getEnrollmentId());
            return true;

        } catch (IOException e) {
            System.err.println("FATAL ERROR: Failed to save enrolment record: " + e.getMessage());
            return false;
        }
    }
}