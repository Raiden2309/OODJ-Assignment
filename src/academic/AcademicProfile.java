package academic;

import java.util.ArrayList;
import java.util.List;
import report.Report;

public class AcademicProfile{
    private String studentID;
    private List<CourseResult> courseResults;
    private double CGPA;
    private String semester;

    public AcademicProfile(String studentID){
        this.studentID = studentID;
        this.courseResults = new ArrayList<>();
    }

    public double calculateCGPA(){
        double totalGP = 0;
        int totalCredits = 0;
        for (CourseResult result: courseResults) {
            totalGP += result.calculateGradePoint() * result.getCourse().getCreditHours();
            totalCredits += result.getCourse().getCreditHours();
        }
        this.CGPA = totalCredits > 0 ? totalGP / totalCredits : 0.0;
        return CGPA;
    }

    // FIX: This method implements the logic required by EligibilityCheck
    public int getTotalFailedCourses(){
        return (int) courseResults.stream()
                .filter(r -> r.getGrade().equals("F"))
                .count();
    }

    public Report generateReport() {
        return new Report(this);
    }

    // Getters and Setters

    public String getStudentID() {
        return studentID;
    }

    public double getCGPA() {
        return CGPA;
    }

    public List<CourseResult> getCourseResults(){
        return courseResults;
    }

    public void addCourseResult(CourseResult result){
        this.courseResults.add(result);
    }
}