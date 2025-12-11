package academic;

public class EnrolledCourse {
    private String studentID;
    private String courseID;
    private int year;
    private int semester;
    private int examScore;
    private int assignmentScore;
    private int examWeight;
    private int assignmentWeight;
    private String failedComponent;

    public EnrolledCourse(String studentID, String courseID, int year, int semester, int examScore, int assignmentScore, int examWeight, int assignmentWeight, String failedComponent){
        this.studentID = studentID;
        this.courseID = courseID;
        this.year = year;
        this.semester = semester;
        this.examScore = examScore;
        this.assignmentScore = assignmentScore;
        this.examWeight = examWeight;
        this.assignmentWeight = assignmentWeight;
        this.failedComponent = failedComponent;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getCourseID() {
        return courseID;
    }

    public int getYear() { return year; }

    public int getSemester() { return semester; }

    public int getExamScore() {
        return examScore;
    }

    public int getAssignmentScore() {
        return assignmentScore;
    }

    public int getExamWeight() {
        return examWeight;
    }

    public int getAssignmentWeight() {
        return assignmentWeight;
    }

    public String getFailedComponent() {
        return failedComponent;
    }

    public double calculateTotalScore() {
        return (examScore * examWeight / 100.0) + (assignmentScore * assignmentWeight / 100.0);
    }

    public boolean isExamFailed(){
        return "Exam".equalsIgnoreCase(failedComponent);
    }

    public boolean isAssignmentFailed(){
        return "Assignment".equalsIgnoreCase(failedComponent);
    }

    @Override
    public String toString() {
        return String.format("StudentID: %s | CourseID: %s | Exam: %d | Assignment: %d | Failed: %s",
                studentID, courseID, examScore, assignmentScore, failedComponent);
    }

}