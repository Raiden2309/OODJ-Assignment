package java.service;

import java.academic.Enrolment;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.SimpleDateFormat;

public class EnrolmentDAO {
    private final String ENROLMENT_FILE_PATH = "data/enrolment_records.csv";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Saves a new Enrollment record to the CSV file.
     * @param enrolment The Enrollment object created upon confirmation.
     * @return true if the save was successful.
     */
    public boolean saveEnrolment(Enrolment enrolment) {
        // Appends the record to the CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter(ENROLMENT_FILE_PATH, true))) {

            String dateString = DATE_FORMAT.format(new Date());

            // Format data: EnrolmentID, StudentID, PlanID, Date, Status
            String record = String.join(",",
                    enrolment.getEnrolmentID(),
                    enrolment.getStudentID(),
                    enrolment.getPlan().getPlanID(), // Assuming PlanID is available
                    dateString,
                    "Active" // Default status upon enrollment
            );

            writer.println(record);
            System.err.println("DAO: Successfully saved new enrolment record: " + enrolment.getEnrolmentID());
            return true;

        } catch (IOException e) {
            System.err.println("FATAL ERROR: Failed to save enrolment record: " + e.getMessage());
            return false;
        }
    }
}