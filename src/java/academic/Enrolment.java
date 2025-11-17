package java.academic;

import java.util.Date;

public class Enrolment {
    private String enrolmentID;
    private Date enrolmentDate;
    private String studentID;
    private CourseRecoveryPlan recoveryPlan;

    public Enrolment(String studentID, CourseRecoveryPlan recoveryPlan) {
        this.enrolmentID = "ENR-" + System.currentTimeMillis();
        this.enrolmentDate = new Date();
        this.studentID = studentID;
        this.recoveryPlan = recoveryPlan;
    }

    public String getEnrolmentID() {
        return enrolmentID;
    }

    public CourseRecoveryPlan getRecoveryPlan() {
        return recoveryPlan;
    }
}