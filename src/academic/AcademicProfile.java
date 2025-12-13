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
        double totalCredits = 0; // Changed int to double to match parsing

        if (courseResults.isEmpty()) {
            this.CGPA = 0.0;
            return 0.0;
        }

        for (CourseResult result: courseResults) {
            try {
                double credits = Double.parseDouble(result.getCourse().getCredits());
                double gradePoint = result.calculateGradePoint();

                totalGP += gradePoint * credits;
                totalCredits += credits;
            } catch (NumberFormatException e) {
                // Ignore courses with invalid credits
                System.err.println("Error parsing credits for course: " + result.getCourse().getCourseId());
            }
        }

        this.CGPA = totalCredits > 0 ? totalGP / totalCredits : 0.0;
        return CGPA;
    }

    public int getTotalFailedCourses(){
        return (int) courseResults.stream()
                .filter(r -> r.getGrade().equalsIgnoreCase("F")) // Use ignoreCase for robustness
                .count();
    }

    public Report generateReport() {
        return new Report(this);
    }

    public String getStudentID() {
        return studentID;
    }

    public double getCGPA() {
        if (this.CGPA == 0.0 && !courseResults.isEmpty()) {
            return calculateCGPA();
        }
        return CGPA;
    }

    public List<CourseResult> getCourseResults(){
        return courseResults;
    }

    public void addCourseResult(CourseResult result){
        this.courseResults.add(result);
        // Optional: Recalculate immediately
        // calculateCGPA();
    }

    //Extra func from edwin's one
    public boolean isEligible(){
        // Simple check placeholder if needed by other classes
        return calculateCGPA() >= 2.0;
    }
}