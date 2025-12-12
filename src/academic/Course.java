package academic;

public class Course
{
    private String courseId;
    private String name;
    private int credits;
    private String semester;
    private String instructor;
    private double examWeight;
    private double assignmentWeight;

    public Course(String courseId, String name, int credits, String semester, String instructor, double examWeight, double assignmentWeight)
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

    public int getCredits() {
        return credits;
    }

    public String getSemester() { return semester; }

    public String getInstructor() {
        return instructor;
    }

    public double getExamWeight() {
        return examWeight;
    }

    public double getAssignmentWeight()
    {
        return assignmentWeight;
    }

    @Override
    public String toString()
    {
        return courseId + "," + name + "," + credits + "," + semester + "," + instructor + "," + examWeight + "," + assignmentWeight;
    }
}