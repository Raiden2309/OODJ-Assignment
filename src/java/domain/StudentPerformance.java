package java.domain;
import data_access.DataAccess;

import java.util.ArrayList;
import java.util.List;

public class StudentPerformance
{
    private final String studentId;
    private double cgpa = 0;
    private int totalCredits = 0;
    private int failedCourses = 0;

    public StudentPerformance(String studentId) {
        this.studentId = studentId;
    }

    public List<String[]> getPerformance(DataAccess data)
    {
        List<String[]> enrollments = data.getEnrollments(new String[]{studentId});
        List<String[]> courses = data.getCourses(null);
        List<String[]> student_enrollments = new ArrayList<>();

        for (String[] enrollment : enrollments)
        {
            String courseId = enrollment[2];
            double examScore = Double.parseDouble(enrollment[5]);
            double assignmentScore = Double.parseDouble(enrollment[6]);

            for (String[] course : courses)
            {
                if (course[0].equals(courseId))
                {
                    student_enrollments.add(getStudentEnrolledCourses(course, examScore, assignmentScore));
                    break;
                }
            }
        }
        this.cgpa /= totalCredits;
        return student_enrollments;
    }

    public String[] getStudentEnrolledCourses(String[] course, double examScore, double assignmentScore)
    {
        int creditHours = Integer.parseInt(course[2]);
        double examWeightage = Double.parseDouble(course[4]) / 100;
        double assignmentWeightage = Double.parseDouble(course[5]) / 100;
        double finalScore = (examScore * examWeightage) + (assignmentScore * assignmentWeightage);
        String grade = calculateGrade(finalScore)[0];
        double gpa = Double.parseDouble(calculateGrade(finalScore)[1]);

        this.cgpa += gpa * creditHours;
        this.totalCredits += creditHours;

        if (gpa < 2.0)
        {
            this.failedCourses++;
        }
        return new String[]{course[0], course[1], course[2], grade, String.valueOf(gpa)};
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

