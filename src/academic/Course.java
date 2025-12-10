package academic;

public class Course
{
    private String courseId;
    private String name;
    private String credits;
    private String semester;
    private String instructor;
    private String examWeight;
    private String assignmentWeight;

    public Course(String courseId, String name, String credits, String semester, String instructor, String examWeight, String assignmentWeight)
    {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.semester = semester;
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

    public String getSemester() { return semester; }

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

    @Override
    public String toString()
    {
        return courseId + "," + name + "," + credits + "," + semester + "," + instructor + "," + examWeight + "," + assignmentWeight;
    }
}