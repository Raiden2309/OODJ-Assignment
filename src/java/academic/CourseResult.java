package java.academic;

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
        this.grade = "";
    }

    public double calculateGradePoint(){
        if (grade == null) return 0;

        switch (grade.toUpperCase()) {
            case "A":
            case "A+":
                this.gradePoint = 4.00;
                break;
            case "A-":
                this.gradePoint = 3.70;
                break;
            case "B+":
                this.gradePoint = 3.30;
                break;
            case "B":
                this.gradePoint = 3.00;
                break;
            case "B-":
                this.gradePoint = 2.70;
                break;
            case "C+":
                this.gradePoint = 2.30;
                break;
            case "C":
                this.gradePoint = 2.00;
                break;
            case "C-":
                this.gradePoint = 1.70;
                break;
            case "D+":
                this.gradePoint = 1.30;
                break;
            case "D":
                this.gradePoint = 1.00;
                break;
            default:
                this.gradePoint = 0.00;
        }
        return gradePoint;
    }

    public boolean addAttempt(CourseAttempt attempt){
        this.currentAttempt = attempt;

        if (attempt.getResultGrade() != null){
            this.grade = attempt.getResultGrade();
        }
        return this.attempts.add(attempt);
    }

    public boolean isProgressAllowed(){
        return attempts.size() < 3 && !grade.equals("A");
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

    public List <CourseAttempt> getAttempts() {
        return attempts;
    }
}