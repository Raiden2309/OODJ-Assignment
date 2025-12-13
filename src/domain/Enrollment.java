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

    // NEW: Fields/Logic from your version (optional but recommended for your module)
    private CourseRecoveryPlan plan;
    private Date enrollmentDate;
    private String status; // FIX: Added status field

    // CONSTRUCTOR 1: The String-based one (Required for DataAccess.java)
    // DO NOT CHANGE THE SIGNATURE or DataAccess will break!
    public Enrollment(String enrollmentId, String studentId, String courseId, String year, String semester, String examScore, String assignmentScore)
    {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
        this.examScore = examScore;
        this.assignmentScore = assignmentScore;

        // Default init for fields not in CSV
        this.enrollmentDate = new Date();
        this.status = "Active"; // Default status if not read from file
    }

    // CONSTRUCTOR 2: The Object-based one (Required for Student.enrol())
    // Your Student class calls: new Enrollment(this.getUserID(), plan);
    public Enrollment(String studentId, CourseRecoveryPlan plan) {
        this.enrollmentId = "ENR-" + studentId + "-" + System.currentTimeMillis();
        this.studentId = studentId;
        this.plan = plan;
        this.courseId = plan.getCourseID(); // Assuming plan has this method
        this.enrollmentDate = new Date();

        // Set defaults for CSV fields not yet known
        this.year = "2023"; // Placeholder or Current Year
        this.semester = "Unknown";
        this.examScore = "0";
        this.assignmentScore = "0";
        this.status = "Pending Approval"; // Default enrollment status
    }

    // --- Getters from Friend's Code ---

    public String getEnrollmentId() {
        return enrollmentId;
    }

    // FIX: Added the missing setter to resolve the DAO compilation error.
    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    // Alias for getEnrolmentID() if your code uses that spelling
    public String getEnrollmentID() {
        return enrollmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    // Alias for getStudentID() if your code uses that capitalization
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

    // --- Getters from Your Code ---

    public CourseRecoveryPlan getPlan() {
        return plan;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    // Alias to match your code's expectation
    public Date getEnrolmentDate() {
        return enrollmentDate;
    }

    // FIX: Added getStatus() method to resolve the Dashboard error
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}