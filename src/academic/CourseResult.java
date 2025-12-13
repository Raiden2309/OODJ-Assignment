package academic;

import java.util.ArrayList;
import java.util.List;

public class CourseResult {
    private String grade;
    private double gradePoint;
    private CourseAttempt currentAttempt;
    private Course course;
    private List<CourseAttempt> attempts;

    public CourseResult(Course course){
        this.course = course;
        this.attempts = new ArrayList<>();
    }

    /**
     * Calculates and returns the Grade Point based on the letter grade.
     * Uses the standard 4.0 scale:
     * A+ = 4.0, A = 3.7, A- = 3.7 (or 3.4 depending on system, using std here)
     * B+ = 3.3, B = 3.0, B- = 2.7
     * C+ = 2.3, C = 2.0, C- = 1.7
     * D+ = 1.3, D = 1.0
     * F = 0.0
     */
    public double calculateGradePoint(){
        if (grade == null) return 0.0;

        switch (grade.toUpperCase()) {
            case "A+":
                this.gradePoint = 4.0;
                break;
            case "A":
                this.gradePoint = 3.7; // Standard 4.0 scale often uses 4.0 for A, but request implies specific scheme.
                // Adjusting to common mappings if GradingScheme enum isn't imported.
                // If using the Enum logic provided in prompt:
                // A+ = 4.0, A = 3.7, B+ = 3.3 ...
                break;
            case "A-":
                this.gradePoint = 3.7; // Or 3.67/3.7 depending on uni
                break;
            case "B+":
                this.gradePoint = 3.3;
                break;
            case "B":
                this.gradePoint = 3.0;
                break;
            case "B-":
                this.gradePoint = 2.7;
                break;
            case "C+":
                this.gradePoint = 2.3;
                break;
            case "C":
                this.gradePoint = 2.0;
                break;
            case "C-":
                this.gradePoint = 1.7; // Or 2.0 depending on uni
                break;
            case "D+":
                this.gradePoint = 1.3;
                break;
            case "D":
                this.gradePoint = 1.0;
                break;
            case "F":
            default:
                this.gradePoint = 0.0;
        }
        return gradePoint;
    }

    public boolean addAttempt(CourseAttempt attempt){
        this.currentAttempt = attempt;
        // Auto-update grade if attempt has one
        if (attempt.getResultGrade() != null) {
            this.grade = attempt.getResultGrade();
        }
        return this.attempts.add(attempt);
    }

    public boolean isProgressAllowed(){
        return attempts.size() < 3 && !grade.equals("A") && !grade.equals("A+");
    }

    public String getGrade() {
        return grade;
    }

    public Course getCourse(){
        return course;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}