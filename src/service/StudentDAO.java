package service;

import domain.Student;
import domain.SystemRole;
import domain.User; // Import User for isActive access

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private final String STUDENT_FILE_PATH = "data/student_information.csv";
    // NOTE: This default password is only used when loading the list initially,
    // it's overridden by UserDAO when logging in.
    private final String DEFAULT_STUDENT_PASSWORD = "password";
    private final SystemRole STUDENT_ROLE = new SystemRole("Student", List.of("View Profile", "Check Eligibility", "Enroll"));

    public List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();

        // Load User information (Active status)
        UserDAO userDAO = new UserDAO();
        List<User> allUsers = userDAO.loadAllUsers();

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE_PATH))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);

                if (values.length >= 6) {
                    String studentID = values[0].trim();
                    String firstName = values[1].trim();
                    String lastName = values[2].trim();
                    String major = values[3].trim();
                    String academicYear = values[4].trim();
                    String email = values[5].trim();

                    // Find corresponding User to get actual credentials/status
                    User userCreds = allUsers.stream()
                            .filter(u -> u.getUserID().equals(studentID))
                            .findFirst()
                            .orElse(null);

                    String passwordHash = (userCreds != null) ? userCreds.getPassword() : DEFAULT_STUDENT_PASSWORD;
                    boolean isActive = (userCreds != null) ? userCreds.isActive() : true; // Default to active if creds missing

                    Student student = new Student(
                            studentID,
                            passwordHash, // Use hash from UserDAO
                            STUDENT_ROLE,
                            firstName,
                            lastName,
                            major,
                            academicYear,
                            email
                    );
                    student.setActive(isActive);
                    students.add(student);
                }
            }
            System.err.println("Successfully loaded " + students.size() + " students.");
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load student data. Check file path: " + e.getMessage());
        }
        return students;
    }

    /**
     * Appends a new student record to student_information.csv.
     * This is used during registration.
     */
    public boolean saveStudent(Student student) {
        System.err.println("Saving student " + student.getUserID() + " to student_information.csv.");

        // Append mode (true)
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENT_FILE_PATH, true))) {

            // Format: StudentID,FirstName,LastName,Major,Year,Email
            String record = String.join(",",
                    student.getUserID(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getMajor(),
                    student.getAcademicYear(),
                    student.getEmail()
            );

            writer.println(record);
            return true;

        } catch (IOException e) {
            System.err.println("FATAL ERROR: Failed to save student information: " + e.getMessage());
            return false;
        }
    }
}
