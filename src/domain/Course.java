package domain;

public class Course
{
    private String courseId;
    private String name;
    private String credits;
    private String instructor;
    private String examWeight;
    private String assignmentWeight;

    public Course(String courseId, String name, String credits, String instructor, String examWeight, String assignmentWeight)
    {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.instructor = instructor;
        this.examWeight = examWeight;
        this.assignmentWeight = assignmentWeight;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getCredits() {
        return credits;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getExamWeight() {
        return examWeight;
    }

    public String getAssignmentWeight()
    {
        return assignmentWeight;
    }
}