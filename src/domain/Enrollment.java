package domain;

import academic.CourseRecoveryPlan; // Import your plan class
import java.util.Date;

public class Enrollment
{
    // Fields from your friend's version
    private String enrollmentId;
    private String studentId;
    private String courseId;
    private String year;
    private String semester;
    private String examScore;
    private String assignmentScore;

    private CourseRecoveryPlan plan;
    private Date enrollmentDate;

    public Enrollment(String enrollmentId, String studentId, String courseId, String year, String semester, String examScore, String assignmentScore)
    {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
        this.examScore = examScore;
        this.assignmentScore = assignmentScore;


        this.enrollmentDate = new Date();
    }

    public Enrollment(String studentId, CourseRecoveryPlan plan) {
        this.enrollmentId = "ENR-" + studentId + "-" + System.currentTimeMillis();
        this.studentId = studentId;
        this.plan = plan;
        this.courseId = plan.getCourseID(); // Assuming plan has this method
        this.enrollmentDate = new Date();

        this.year = "2025"; // Placeholder or Current Year
        this.semester = "Unknown";
        this.examScore = "0";
        this.assignmentScore = "0";
    }


    public String getEnrollmentId() {
        return enrollmentId;
    }


    public String getEnrolmentID() {
        return enrollmentId;
    }

    public String getStudentId() {
        return studentId;
    }


    public String getStudentID() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getYear() {
        return year;
    }

    public String getSemester() {
        return semester;
    }

    public String getExamScore() {
        return examScore;
    }

    public String getAssignmentScore() {
        return assignmentScore;
    }


    public CourseRecoveryPlan getPlan() {
        return plan;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public Date getEnrolmentDate() {
        return enrollmentDate;
    }
}