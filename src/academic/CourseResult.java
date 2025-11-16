package academic;

import java.util.ArrayList;
import java.util.List;

public class CourseResult {
    private String grade;
    private double gradePoint;
    private CourseAttempt currentAttempt;
    private Course course;
    private List<CourseAttempt> attempts;

    public CourseResult(Course course){
        this.course = course;
        this.attempts = new ArrayList<>();
    }

    public double calculateGradePoint(){
        enum cgpa
        {
            A_PLUS("A+", 4.0),
            A("A", 3.7),
            B_PLUS("B+", 3.3),
            B("B", 3.0),
            C_PLUS("C+", 2.7),
            C("C", 2.3),
            C_MINUS("C-", 2.0),
            D("D", 1.7),
            F_PLUS("F+", 1.3),
            F("F", 1.0),
            F_MINUS("F-", 0.0);

            private final String grade;
            private final double points;

            cgpa(String grade, double points)
            {
                this.grade = grade;
                this.points = points;
            }

            public String getGrade()
            {
                return grade;
            }

            public double getPoints()
            {
                return points;
            }
        }

        for (cgpa g : cgpa.values())
        {
            if (g.getGrade().equals(grade))
            {
                return g.getPoints();
            }
        }
        return 0.0;
    }

    public boolean addAttempt(CourseAttempt attempt){
        this.currentAttempt = attempt;
        return this.attempts.add(attempt);
    }

    public boolean isProgresAllowed(){
        return attempts.size() < 3 && !grade.equals("A");
    }

    public String getGrade() {
        return grade;
    }

    public Course getCourse(){
        return course;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
