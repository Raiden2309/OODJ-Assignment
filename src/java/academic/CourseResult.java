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

    public double calculateGradePoint(){
        this.gradePoint = grade.equals("A") ? 4.0 : (grade.equals("B") ? 3.0 : 0.0);
        return gradePoint;
    }

    public boolean addAttempt(CourseAttempt attempt){
        this.currentAttempt = attempt;
        return this.attempts.add(attempt);
    }

    public boolean isProgresAllowed(){
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
}