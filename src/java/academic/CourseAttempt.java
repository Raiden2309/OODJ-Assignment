package java.academic;

import java.util.Date;
import java.util.List;

public class CourseAttempt{
    private int attemptNumber;
    private Date dateRecorded;
    private String resultGrade;
    private double examScore;
    private double assignmentScore;

    public CourseAttempt(int attemptNumber){
        this.attemptNumber = attemptNumber;
        this.dateRecorded = new Date();
        this.examScore = -1;
        this.assignmentScore = -1;
    }

    public List<String> getRequiredComponents(){
        return List.of("Exam", "Assignment");
    }

    public void recordGrade(String grade){
        this.resultGrade = grade;
    }

    public int getAttemptNumber(){
        return attemptNumber;
    }

    public String  getResultGrade() {
        return resultGrade;
    }

    public void setExamScore(double examScore) {
        this.examScore = examScore;
    }

    public double getExamScore() {
        return examScore;
    }

    public void setAssignmentScore(double assignmentScore) {
        this.assignmentScore = assignmentScore;
    }

    public double getAssignmentScore() {
        return assignmentScore;
    }

    public Date getDateRecorded() {
        return dateRecorded;
    }
}