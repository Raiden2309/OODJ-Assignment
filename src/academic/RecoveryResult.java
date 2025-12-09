package academic;

import academic.CourseResult;

public class RecoveryResult extends CourseResult {

    private String attemptID;
    private String studentID;
    private String courseID;
    private String failedComponent;
    private int examScore;
    private int assignmentScore;
    private String recoveryStatus;

    public RecoveryResult(String attemptID, String studentID, String courseID, Course course,String failedComponent, int examScore, int assignmentScore, String recoveryStatus) {
        super(course);
        this.attemptID = attemptID;
        this.studentID = studentID;
        this.courseID = courseID;
        this.failedComponent = failedComponent;
        this.examScore = examScore;
        this.assignmentScore = assignmentScore;
        this.recoveryStatus = recoveryStatus;
    }

    public String getAttemptID() {
        return attemptID;
    }

    public void setAttemptID(String attemptID) {
        this.attemptID = attemptID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getFailedComponent() {
        return failedComponent;
    }

    public void setFailedComponent(String failedComponent) {
        this.failedComponent = failedComponent;
    }

    public int getExamScore() {
        return examScore;
    }

    public void setExamScore(int examScore) {
        this.examScore = examScore;
    }

    public int getAssignmentScore() {
        return assignmentScore;
    }

    public void setAssignmentScore(int assignmentScore) {
        this.assignmentScore = assignmentScore;
    }

    public String getRecoveryStatus() {
        return recoveryStatus;
    }

    public void setRecoveryStatus(String recoveryStatus) {
        this.recoveryStatus = recoveryStatus;
    }

    @Override
    public String toString() {
        return attemptID + "," + studentID + "," + courseID + "," + failedComponent + "," + examScore + "," + assignmentScore + "," + recoveryStatus;
    }

}