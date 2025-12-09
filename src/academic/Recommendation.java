package academic;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Recommendation {
    private String recID;
    private String studentID;
    private String courseID;
    private String description;
    private String timeline;
    private Date deadline;
    private String status;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Recommendation(String recID, String studentID, String courseID, String description, String timeline, Date deadline, String status) {

        this.recID = recID;
        this.studentID = studentID;
        this.courseID = courseID;
        this.description = description;
        this.timeline = timeline;
        this.deadline = deadline;
        this.status = status;

    }

    public String getRecID() {
        return recID;
    }

    public void setRecID(String recID) {
        this.recID = recID;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return recID + "," + studentID + "," + courseID + "," + description + "," + timeline + "," + dateFormat.format(deadline) + "," + status;
    }
}

