package data_access;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import model.Student;
import model.Course;
import model.Enrollment;
import model.Program;

public class DataAccess
{
    final String STUDENT_INFO = "data/student_information.csv";
    final String COURSE_INFO = "data/course_assessment_information.csv";
    final String STUDENT_ENROLLED_COURSES = "data/student_enrollment_information.csv";
    final String PROGRAM_INFO = "data/program_information.csv";

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
                Student student = new Student(data[0], data[1], data[2], data[3], data[4], data[5]);

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
                Course course = new Course(data[0], data[1], data[2], data[3], data[4], data[5]);

                courses.add(course);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
        return courses;
    }

    public List<Enrollment> enrollmentList()
    {
        List<Enrollment> enrollments = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_ENROLLED_COURSES)))
        {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(",");
                Enrollment enrollment = new Enrollment(data[0], data[1], data[2], data[3], data[4], data[5], data[6]);

                enrollments.add(enrollment);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
        return enrollments;
    }

    public List<Program> programList()
    {
        List<Program> programs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(PROGRAM_INFO)))
        {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(",");
                Program program = new Program(data[0], data[1], data[2]);

                programs.add(program);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
        return programs;
    }

    public List<String[]> getStudents()
    {
        List<String[]> allStudents = new ArrayList<>();

        for (Student s : studentList())
        {
            String[] student = {s.getStudentId(), s.getFirstName(), s.getLastName(), s.getProgramId(), s.getEmail(), s.getRecoveryEligibility()};
            allStudents.add(student);
        }
        return allStudents;
    }

    public List<String[]> getEnrollments(String[] student)
    {
        List<String[]> allEnrollments = new ArrayList<>();

        for (Enrollment en : enrollmentList())
        {
            String[] enrollment = {en.getEnrollmentId(), en.getStudentId(), en.getCourseId(), en.getYear(), en.getSemester(), en.getExamScore(), en.getAssignmentScore()};

            if (student == null)
            {
                allEnrollments.add(enrollment);
            }
            else
            {
                if (enrollment[1].trim().equals(student[0]))
                {
                    allEnrollments.add(enrollment);
                }
            }
        }
        return allEnrollments;
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

    public List<String[]> getPrograms(String[] student)
    {
        List<String[]> allPrograms = new ArrayList<>();

        for (Program p : programList()) {
            String[] program = {p.getProgramId(), p.getName(), p.getLevel()};

            if (student == null)
            {
                allPrograms.add(program);
            }
            else
            {
                if (program[0].trim().equals(student[3]))
                {
                    allPrograms.add(program);
                    break;
                }
            }
        }
        return allPrograms;
    }
}
