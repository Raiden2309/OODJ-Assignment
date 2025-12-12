package academic;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String id;
    private List<String> grades;

    public Student(String id) {
        this.id = id;
        this.grades = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public List<String> getGrades() {
        return new ArrayList<>(grades);
    }

    public void addGrade(String grade) {
        grades.add(grade);
    }

    // Convert letter grade to grade points
    private double gradeToPoints(String grade) {
        if (grade.equals("A")) {
            return 4.0;
        } else if (grade.equals("A-")) {
            return 3.7;
        } else if (grade.equals("B+")) {
            return 3.3;
        } else if (grade.equals("B")) {
            return 3.0;
        } else if (grade.equals("B-")) {
            return 2.7;
        } else if (grade.equals("C+")) {
            return 2.3;
        } else if (grade.equals("C")) {
            return 2.0;
        } else if (grade.equals("C-")) {
            return 1.7;
        } else if (grade.equals("D")) {
            return 1.0;
        } else if (grade.equals("F")) {
            return 0.0;
        } else {
            return 0.0;
        }
    }

    // Calculate CGPA = Total Grade Points / Total Credits
    // Each course is 3 credits
    public double calculateCGPA() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        
        double totalGradePoints = 0.0;
        int totalCredits = 0;
        int creditsPerCourse = 3;
        
        for (String grade : grades) {
            double points = gradeToPoints(grade);
            totalGradePoints = totalGradePoints + (points * creditsPerCourse);
            totalCredits = totalCredits + creditsPerCourse;
        }
        
        return totalGradePoints / totalCredits;
    }

    // Count failed courses (Grade < C / 2.0)
    // Grades below C (2.0) are: C- (1.7), D (1.0), F (0.0)
    public int countFailedCourses() {
        int failedCount = 0;
        
        for (String grade : grades) {
            double points = gradeToPoints(grade);
            if (points < 2.0) {
                failedCount = failedCount + 1;
            }
        }
        
        return failedCount;
    }
}
