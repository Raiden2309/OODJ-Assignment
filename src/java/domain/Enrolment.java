package java.domain;

import java.util.UUID; // Imported to generate unique IDs
import java.academic.CourseRecoveryPlan; // Assuming this is where the plan class lives

public class Enrolment {
    private String enrollmentId;
    private String studentId;
    private String courseId;
    private String year;
    private String semester;
    private String examScore;
    private String assignmentScore;

    // --- NEW CONSTRUCTOR ADDED HERE ---
    // This matches the call in Student.java: new Enrolment(this.getUserID(), plan);
    public Enrolment(String studentId, CourseRecoveryPlan plan) {
        // 1. Generate a unique ID for this new enrollment
        this.enrollmentId = UUID.randomUUID().toString();

        // 2. Set the student ID passed from Student.java
        this.studentId = studentId;

        // 3. Extract details from the CourseRecoveryPlan
        // NOTE: Check that these method names match what is inside your CourseRecoveryPlan class!
        this.courseId = plan.getCourseId();
        this.semester = plan.getSemester();

        // If the plan doesn't have a specific year, you might use the current year or a default
        this.year = "2025";

        // 4. Initialize scores to default values (since they just enrolled)
        this.examScore = "0";
        this.assignmentScore = "0";
    }

    // --- EXISTING CONSTRUCTOR (Kept for compatibility) ---
    public Enrolment(String enrollmentId, String studentId, String courseId, String year, String semester, String examScore, String assignmentScore) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
        this.examScore = examScore;
        this.assignmentScore = assignmentScore;
    }

    // --- GETTERS ---
    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getStudentId() {
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
}