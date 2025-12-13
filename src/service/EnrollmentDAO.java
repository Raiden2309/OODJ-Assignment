package service;

import domain.Enrollment;
import domain.Student;
import academic.CourseRecoveryPlan;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EnrollmentDAO {

    private final String ENROLMENT_FILE_PATH = "data/enrolment_records.csv";
    // Date format for the CSV file
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final CourseRecoveryPlanDAO planDAO = new CourseRecoveryPlanDAO();

    // Utility method to rewrite the entire enrollment file
    private void rewriteFile(List<Enrollment> enrollments) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ENROLMENT_FILE_PATH, false))) {
            writer.println("EnrolmentID,StudentID,PlanID,Date,Status"); // Header
            for (Enrollment e : enrollments) {
                String dateString = (e.getEnrolmentDate() != null)
                        ? DATE_FORMAT.format(e.getEnrolmentDate())
                        : "Unknown Date";

                String planID = e.getPlan() != null ? e.getPlan().getPlanID() : "UNKNOWN_PLAN";

                String record = String.join(",",
                        e.getEnrollmentId(),
                        e.getStudentId(),
                        planID,
                        dateString,
                        e.getStatus()
                );
                writer.println(record);
            }
        }
    }

    public List<Enrollment> loadEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        StudentDAO studentDAO = new StudentDAO();
        List<Student> allStudents = studentDAO.loadAllStudents();

        try (BufferedReader br = new BufferedReader(new FileReader(ENROLMENT_FILE_PATH))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                // Expected format: EnrolmentID,StudentID,PlanID,Date,Status
                String[] values = line.split(",", -1);

                if (values.length >= 5) {
                    String enrollmentId = values[0].trim();
                    String studentId = values[1].trim();
                    String planID = values[2].trim();
                    String status = values[4].trim();

                    CourseRecoveryPlan plan = planDAO.getPlanByID(planID);

                    if (plan != null) {
                        // Use the object-based constructor
                        Enrollment enrollment = new Enrollment(studentId, plan);

                        // Set fields manually since they were missing setters/fields in domain.Enrollment previously
                        // NOTE: This relies on the setEnrollmentId and setStatus being in the domain.Enrollment.java
                        enrollment.setEnrollmentId(enrollmentId);
                        enrollment.setStatus(status);

                        enrollments.add(enrollment);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load enrollment records: " + e.getMessage());
        }
        return enrollments;
    }

    public boolean updateEnrollmentStatus(String enrollmentId, String newStatus) {
        try {
            List<Enrollment> enrollments = loadEnrollments();
            boolean updated = false;

            for (Enrollment e : enrollments) {
                if (e.getEnrollmentId().equalsIgnoreCase(enrollmentId)) {
                    e.setStatus(newStatus);
                    updated = true;
                    break;
                }
            }

            if (updated) {
                rewriteFile(enrollments);
                System.err.println("DAO: Enrollment " + enrollmentId + " status updated to " + newStatus);
                return true;
            } else {
                System.err.println("DAO: Enrollment ID not found: " + enrollmentId);
                return false;
            }
        } catch (IOException e) {
            System.err.println("FATAL ERROR: Failed to update enrollment status: " + e.getMessage());
            return false;
        }
    }


    public boolean saveEnrolment(Enrollment enrollment) {
        // 'true' in FileWriter constructor enables append mode (don't overwrite file)
        try (PrintWriter writer = new PrintWriter(new FileWriter(ENROLMENT_FILE_PATH, true))) {

            String dateString = (enrollment.getEnrolmentDate() != null)
                    ? DATE_FORMAT.format(enrollment.getEnrolmentDate())
                    : "Unknown Date";

            String planID = "UNKNOWN_PLAN";
            if (enrollment.getPlan() != null) {
                planID = enrollment.getPlan().getPlanID();
            }

            // Create the CSV line: EnrolmentID, StudentID, PlanID, Date, Status
            String record = String.join(",",
                    enrollment.getEnrollmentId(),
                    enrollment.getStudentId(),
                    planID,
                    dateString,
                    enrollment.getStatus()
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