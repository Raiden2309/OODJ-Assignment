package domain;
import academic.Course;
import academic.EnrolledCourse;
import service.CourseCatalog;
import service.EnrolledCourseDAO;

import java.util.ArrayList;
import java.util.List;

public class StudentPerformance
{
    private final String studentId;
    private double cgpa = 0;
    private int totalCredits = 0;
    private int failedCourses = 0;

    EnrolledCourseDAO enrolledCourseDAO = new EnrolledCourseDAO();
    List<EnrolledCourse> studentEnrolments = enrolledCourseDAO.loadAllEnrolledCourses();
    CourseCatalog courses = CourseCatalog.getInstance();

    public StudentPerformance(String studentId) {
        this.studentId = studentId;
    }

    public List<String[]> getPerformance()
    {
        List<String[]> student_enrollments = new ArrayList<>();

        for (EnrolledCourse ec : studentEnrolments)
        {
            String courseId = ec.getCourseID();
            double examScore = ec.getExamScore();
            double assignmentScore = ec.getAssignmentScore();

            for (Course course : courses.getAllCourses())
            {
                if (course.getCourseId().equals(courseId) && ec.getStudentID().equals(studentId))
                {
                    student_enrollments.add((String[]) getStudentEnrolledCourses(course, examScore, assignmentScore));
                    break;
                }
            }
        }
        this.cgpa /= totalCredits;
        return student_enrollments;
    }

    public String[] getStudentEnrolledCourses(Course course, double examScore, double assignmentScore)
    {
        int creditHours = course.getCredits();
        double examWeightage = course.getExamWeight() / 100;
        double assignmentWeightage = course.getAssignmentWeight() / 100;
        double finalScore = (examScore * examWeightage) + (assignmentScore * assignmentWeightage);
        String grade = calculateGrade(finalScore)[0];
        double gpa = Double.parseDouble(calculateGrade(finalScore)[1]);

        this.cgpa += gpa * creditHours;
        this.totalCredits += creditHours;

        if (gpa < 2.0)
        {
            this.failedCourses++;
        }
        return new String[]{course.getCourseId(), course.getName(), String.valueOf(creditHours), grade, String.valueOf(gpa)};
    }

    public String[] calculateGrade(double score)
    {
        for (GradingScheme gs : GradingScheme.values())
        {
            if (score >= gs.getMinScore())
            {
                return new String[]{gs.getGrade(), String.valueOf(gs.getGpa())};
            }
        }
        return new String[0];
    }

    public String getStudentId()
    {
        return studentId;
    }

    public double getCgpa()
    {
        return cgpa;
    }

    public int getTotalCredits()
    {
        return totalCredits;
    }

    public int getFailedCourses()
    {
        return failedCourses;
    }
}