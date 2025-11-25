package model;

public class Enrollment
{
    private String enrollmentId;
    private String studentId;
    private String courseId;
    private String year;
    private String semester;
    private String examScore;
    private String assignmentScore;

    public Enrollment(String enrollmentId, String studentId, String courseId, String year, String semester, String examScore, String assignmentScore)
    {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
        this.examScore = examScore;
        this.assignmentScore = assignmentScore;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getYear()
    {
        return year;
    }

    public String getSemester()
    {
        return semester;
    }

    public String getExamScore() {
        return examScore;
    }

    public String getAssignmentScore()
    {
        return assignmentScore;
    }

    @Override
    public String toString()
    {
        return enrollmentId + "," + studentId + "," + courseId + "," + year + "," + semester + "," + examScore + "," + assignmentScore;
    }
}
