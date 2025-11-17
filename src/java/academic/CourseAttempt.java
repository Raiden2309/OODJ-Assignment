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
}