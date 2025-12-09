package academic;

import java.util.Date;
import java.text.SimpleDateFormat;

public class RecoveryMilestone{
    private String milestoneID;
    private String studentID;
    private String courseID;
    private String studyWeek;
    private String taskDescription;
    private Date deadline;
    private boolean completionStatus;
    private String status;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public RecoveryMilestone(String milestoneID, String studentID, String courseID, String studyWeek, String taskDescription, Date deadline, String status) {
        this.milestoneID = milestoneID;  //added smthing from edwin's one
        this.studentID = studentID;
        this.courseID = courseID;
        this.studyWeek = studyWeek;
        this.taskDescription = taskDescription;
        this.deadline = deadline;
        this.completionStatus = false;
        this.status = status;
    }

    public void setCompletionStatus(boolean isDone){

        this.completionStatus = isDone;
    }

    public boolean getStatus(){

        return completionStatus;
    }

    public String getTaskDescription() {

        return taskDescription;
    }

    public String getMilestoneID() {

        return milestoneID;

    }
    public void setMilestoneID(String milestoneID) {

        this.milestoneID = milestoneID;

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
    public String getStudyWeek() {

        return studyWeek;

    }

    public void setStudyWeek(String studyWeek) {

        this.studyWeek = studyWeek;

    }

    public Date getDeadline() {

        return deadline;

    }

    public void setDeadline(Date deadline) {

        this.deadline = deadline;

    }

    public String getStatusString() {

        return status;

    }

    public void setStatusString(String status) {

        this.status = status;

    }

    @Override
    public String toString() {

        return milestoneID + "," + studentID + "," + courseID + "," + studyWeek + "," + taskDescription + "," + dateFormat.format(deadline) + "," + status;

    }
}