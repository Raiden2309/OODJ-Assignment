package service;

import domain.Student;
import domain.SystemRole;
import domain.User;

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
    private final String DEFAULT_STUDENT_PASSWORD = "password";
    private final SystemRole STUDENT_ROLE = new SystemRole("Student", List.of("View Profile", "Check Eligibility", "Enroll"));

    public List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();

        // 1. Force a fresh load of credentials to get the latest status
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

                    // 2. Synchronization: Match student to credential record
                    User userCreds = allUsers.stream()
                            .filter(u -> u.getUserID().equalsIgnoreCase(studentID))
                            .findFirst()
                            .orElse(null);

                    String passwordHash = (userCreds != null) ? userCreds.getPassword() : DEFAULT_STUDENT_PASSWORD;

                    boolean isActive = (userCreds != null) ? userCreds.isActive() : true;

                    Student student = new Student(
                            studentID,
                            passwordHash,
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
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load student data. Check file path: " + e.getMessage());
        }
        return students;
    }

    /**
     * Appends a new student record to student_information.csv.
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

    /**
     * Removes a student record by ID and rewrites the file.
     */
    public boolean removeStudent(String studentID) {
        List<Student> students = loadAllStudents();

        // Remove the target student by comparing trimmed IDs
        boolean removed = students.removeIf(s -> s.getUserID().trim().equalsIgnoreCase(studentID.trim()));

        if (removed) {
            return rewriteStudentFile(students);
        } else {
            return false;
        }
    }

    /**
     * Rewrites the entire student_information.csv file based on the provided list.
     */
    private boolean rewriteStudentFile(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENT_FILE_PATH, false))) { // Overwrite (false)
            // Header
            writer.println("StudentID,FirstName,LastName,Major,AcademicYear,Email");

            for (Student s : students) {
                String record = String.join(",",
                        s.getUserID(),
                        s.getFirstName(),
                        s.getLastName(),
                        s.getMajor(),
                        s.getAcademicYear(),
                        s.getEmail()
                );
                writer.println(record);
            }
            System.err.println("DAO: Student information file rewritten.");
            return true;
        } catch (IOException e) {
            System.err.println("FATAL ERROR: Failed to rewrite student information file: " + e.getMessage());
            return false;
        }
    }
}
