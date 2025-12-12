package resources;

import data_access.DataAccess;
import domain.Student;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java. util.ArrayList;
import java.util. List;

public class EnrollmentGUI extends JFrame {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel studentIdLabel;
    private JTextField studentIdField;
    private JButton checkEligibilityButton;
    private JLabel detailsLabel;
    private JTextArea detailsTextArea;
    private JButton enrollButton;
    private JLabel statusLabel;

    private static final String ACADEMIC_RECORDS_FILE = "data/academic_records.csv";
    private static final String COURSE_INFO_FILE = "data/course_assessment_information.csv";

    private DataAccess dataAccess;
    private Student currentStudent;

    public EnrollmentGUI() {
        dataAccess = new DataAccess();

        // Set up the frame - DO NOT recreate components, they come from the .form file
        setTitle("Student Enrollment System");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        // Add action listeners only - UI is defined in .form file
        checkEligibilityButton.addActionListener(e -> checkStudentEligibility());
        enrollButton. addActionListener(e -> enrollStudent());
    }

    private void checkStudentEligibility() {
        String studentId = studentIdField.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find student from data
        List<Student> students = dataAccess.studentList();
        currentStudent = null;

        for (Student student : students) {
            if (student. getStudentId().equalsIgnoreCase(studentId)) {
                currentStudent = student;
                break;
            }
        }

        if (currentStudent == null) {
            detailsTextArea.setText("Student not found with ID: " + studentId);
            statusLabel.setText("Student not found");
            enrollButton.setEnabled(false);
            return;
        }

        // Get academic records from academic_records.csv
        List<String[]> studentRecords = getAcademicRecords(studentId);

        if (studentRecords.isEmpty()) {
            detailsTextArea.setText("No academic records found for student: " + studentId);
            statusLabel.setText("No records found");
            enrollButton.setEnabled(false);
            return;
        }

        // Calculate CGPA and count failed courses
        double totalGradePoints = 0.0;
        int totalCredits = 0;
        int failedCourses = 0;
        int creditsPerCourse = 3; // Assuming 3 credits per course

        for (String[] record : studentRecords) {
            String grade = record[1];
            double gradePoints = gradeToPoints(grade);

            totalGradePoints += gradePoints * creditsPerCourse;
            totalCredits += creditsPerCourse;

            if (gradePoints < 2.0) {
                failedCourses++;
            }
        }

        double cgpa = totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;

        // Build details text
        StringBuilder details = new StringBuilder();
        details.append("Student ID: ").append(currentStudent. getStudentId()).append("\n");
        details.append("Name: ").append(currentStudent.getFullName()).append("\n");
        details.append("Major: ").append(currentStudent.getMajor()).append("\n");
        details.append("Academic Year: ").append(currentStudent.getAcademicYear()).append("\n");
        details.append("Email: ").append(currentStudent.getEmail()).append("\n");
        details.append("----------------------------\n");
        details.append("Courses Taken:  ").append(studentRecords.size()).append("\n");
        details.append("CGPA: ").append(String.format("%.2f", cgpa)).append("\n");
        details.append("Failed Courses: ").append(failedCourses).append("\n");
        details.append("----------------------------\n");

        // Check eligibility - eligible if CGPA < 2.0 OR failed courses > 3
        boolean needsRecovery = cgpa < 2.0 || failedCourses > 3;

        if (needsRecovery) {
            details.append("Status: ELIGIBLE for Course Recovery Plan\n");
            details.append("Reason: ");
            if (cgpa < 2.0) {
                details.append("CGPA below 2.0. ");
            }
            if (failedCourses > 3) {
                details.append("More than 3 failed courses.");
            }
            enrollButton.setEnabled(true);
            statusLabel.setText("Student is eligible for enrollment");
        } else {
            details. append("Status: NOT ELIGIBLE for Course Recovery Plan\n");
            details. append("Reason:  CGPA >= 2.0 and failed courses <= 3");
            enrollButton. setEnabled(false);
            statusLabel. setText("Student is not eligible");
        }

        detailsTextArea.setText(details. toString());
    }

    /**
     * Reads academic records from academic_records.csv for a specific student
     * Returns list of [CourseID, Grade] arrays
     */
    private List<String[]> getAcademicRecords(String studentId) {
        List<String[]> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(ACADEMIC_RECORDS_FILE))) {
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Format: StudentID, CourseID, Semester, Grade
                if (data.length >= 4 && data[0]. trim().equalsIgnoreCase(studentId)) {
                    records.add(new String[]{data[1]. trim(), data[3].trim()}); // CourseID, Grade
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading academic records:  " + e.getMessage());
        }

        return records;
    }

    /**
     * Converts letter grade to grade points
     */
    private double gradeToPoints(String grade) {
        switch (grade.toUpperCase()) {
            case "A":  return 4.0;
            case "A-": return 3.7;
            case "B+":  return 3.3;
            case "B":  return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C":  return 2.0;
            case "C-":  return 1.7;
            case "D":  return 1.0;
            case "F":  return 0.0;
            default:   return 0.0;
        }
    }

    private void enrollStudent() {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "No student selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane. showConfirmDialog(
                this,
                "Confirm enrollment for " + currentStudent.getFullName() + "?",
                "Confirm Enrollment",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            statusLabel.setText("Student enrolled successfully!");
            JOptionPane.showMessageDialog(
                    this,
                    "Enrollment successful for " + currentStudent. getFullName() + "!",
                    "Success",
                    JOptionPane. INFORMATION_MESSAGE
            );
            enrollButton.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EnrollmentGUI().setVisible(true);
        });
    }
}