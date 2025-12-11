package data_access;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import academic.Course;
import academic.EnrolledCourse;
import domain.Student;
import domain.SystemRole;

public class DataAccess
{
    final String STUDENT_INFO = "data/student_information.csv";
    final String COURSE_INFO = "data/course_assessment_information.csv";
    final String STUDENT_ENROLLED_COURSES = "data/student_enrolled_courses.csv";

    private final String DEFAULT_PASSWORD = "pass";
    private final SystemRole STUDENT_ROLE = new SystemRole("Student", List.of("View Profile", "Enroll"));

    public List<Student> studentList()
    {
        List<Student> students = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_INFO)))
        {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(",");
                Student student = new Student(
                        data[0],            // studentID
                        DEFAULT_PASSWORD,   // password (Added)
                        STUDENT_ROLE,       // role (Added)
                        data[1],            // firstName
                        data[2],            // lastName
                        data[3],            // major
                        data[4],            // academicYear
                        data[5]             // email
                );


                students.add(student);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
        return students;
    }

    public List<Course> courseList()
    {
        List<Course> courses = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(COURSE_INFO)))
        {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(",");
                Course course = new Course(data[0], data[1], data[2], data[3], data[4], data[5], data[6]);

                courses.add(course);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
        return courses;
    }

    public List<EnrolledCourse> enrolledCoursesList()
    {
        List<EnrolledCourse> enrolledCourses = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_ENROLLED_COURSES)))
        {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(",");
                EnrolledCourse enrolledCourse = new EnrolledCourse(
                        data[0],
                        data[1],
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[3]),
                        Integer.parseInt(data[4]),
                        Integer.parseInt(data[5]),
                        Integer.parseInt(data[6]),
                        Integer.parseInt(data[7]),
                        data[8]);

                enrolledCourses.add(enrolledCourse);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
        return enrolledCourses;
    }

    public List<String[]> getStudents()
    {
        List<String[]> allStudents = new ArrayList<>();

        for (Student s : studentList())
        {
            String[] student = {s.getStudentId(), s.getFirstName(), s.getLastName(), s.getMajor(), s.getAcademicYear(), s.getEmail(), s.getRecoveryEligibility()};
            allStudents.add(student);
        }
        return allStudents;
    }

    public List<String[]> getEnrolledCourses(String[] student)
    {
        List<String[]> enrolledCourses = new ArrayList<>();

        for (EnrolledCourse ec : enrolledCoursesList())
        {
            String[] enrolledCourse = {
                    ec.getStudentID(),
                    ec.getCourseID(),
                    String.valueOf(ec.getYear()),
                    String.valueOf(ec.getSemester()),
                    String.valueOf(ec.getExamScore()),
                    String.valueOf(ec.getAssignmentScore()),
                    String.valueOf(ec.getExamWeight()),
                    String.valueOf(ec.getAssignmentWeight())
            };

            if (student == null)
            {
                enrolledCourses.add(enrolledCourse);
            }
            else
            {
                if (enrolledCourse[0].trim().equals(student[0]))
                {
                    enrolledCourses.add(enrolledCourse);
                }
            }
        }
        return enrolledCourses;
    }

    public List<String[]> getCourses(String[] student)
    {
        List<String[]> allCourses = new ArrayList<>();

        for (Course c : courseList()) {
            String[] course = {c.getCourseId(), c.getName(), c.getCredits(), c.getInstructor(), c.getExamWeight(), c.getAssignmentWeight()};

            if (student == null)
            {
                allCourses.add(course);
            }
            else
            {
                if (course[0].trim().equals(student[2]))
                {
                    allCourses.add(course);
                }
            }
        }
        return allCourses;
    }
}