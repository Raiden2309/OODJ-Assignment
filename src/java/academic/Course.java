package java.academic;

import java.service.CourseCatalog;

public class Course {
    private String courseID;
    private String courseTitle;
    private int creditHours;
    private int examWeight;
    private int assignmentWeight;

    public Course(String courseID, String courseTitle, int creditHours, int examWeight, int assignmentWeight) {
        this.courseID = courseID;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.examWeight = examWeight;
        this.assignmentWeight = assignmentWeight;
    }

    public int getAssessmentWeight(String component){
        return component.equalsIgnoreCase("exam") ? examWeight : assignmentWeight;
    }

    public void loadFromCatalog(){
        CourseCatalog.getInstance().getCourse(courseID);
    }

    public String getCourseID() {
        return courseID;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public int getCreditHours() {
        return creditHours;
    }
}