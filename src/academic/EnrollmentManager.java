package academic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EnrollmentManager {
    private List<Student> students;

    public EnrollmentManager() {
        students = new ArrayList<>();
    }

    // Find a student by ID in the list
    private Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.getId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }

    // Load data from CSV file
    public void loadFromCSV(String filePath) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Parse CSV line: StudentID,CourseID,Semester,Grade
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String studentId = parts[0].trim();
                    String grade = parts[3].trim();
                    
                    // Get or create student
                    Student student = findStudentById(studentId);
                    if (student == null) {
                        student = new Student(studentId);
                        students.add(student);
                    }
                    
                    // Add grade to student
                    student.addGrade(grade);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error closing file: " + e.getMessage());
                }
            }
        }
    }

    // Check eligibility: CGPA >= 2.0 AND failed courses <= 3
    public boolean checkEligibility(String studentID) {
        Student student = findStudentById(studentID);
        
        if (student == null) {
            return false;
        }
        
        double cgpa = student.calculateCGPA();
        int failedCourses = student.countFailedCourses();
        
        return cgpa >= 2.0 && failedCourses <= 3;
    }

    // Get a student by ID
    public Student getStudent(String studentID) {
        return findStudentById(studentID);
    }

    // Get number of students loaded
    public int getStudentCount() {
        return students.size();
    }
}
