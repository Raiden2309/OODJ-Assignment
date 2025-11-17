package java.service;

import java.domain.Student;
import java.domain.SystemRole;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private final String STUDENT_FILE_PATH = "data/student_information.csv";
    private final String DEFAULT_STUDENT_PASSWORD = "password";
    private final SystemRole STUDENT_ROLE = new SystemRole("Student", List.of("View Profile", "Check Eligibility", "Enroll"));

    public List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();
        System.err.println("Attempting to load students from file: " + STUDENT_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE_PATH))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length >= 6) {
                    String studentID = values[0].trim();
                    String firstName = values[1].trim();
                    String lastName = values[2].trim();
                    String major = values[3].trim();
                    String academicYear = values[4].trim();
                    String email = values[5].trim();

                    Student student = new Student(
                            studentID,
                            DEFAULT_STUDENT_PASSWORD,
                            STUDENT_ROLE,
                            firstName,
                            lastName,
                            major,
                            academicYear,
                            email
                    );
                    students.add(student);
                }
            }
            System.err.println("Successfully loaded " + students.size() + " students.");
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load student data. Check file path: " + e.getMessage());
        }
        return students;
    }
    public boolean saveStudent(Student student) {
        System.err.println("Saving student " + student.getUserID() + " to file.");
        return true;
    }
}
