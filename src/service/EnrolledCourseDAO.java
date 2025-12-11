package service;

import academic.Course;
import academic.CourseResult;
import academic.EnrolledCourse;
import domain.GradingScheme;
import domain.Student;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnrolledCourseDAO {
    private final String ENROLLED_COURSES_FILE_PATH = "data/student_enrolled_courses.csv";
    // Dependency: We need the catalog to look up full Course objects by ID
    private CourseCatalog courseCatalog = CourseCatalog.getInstance();

    public List<EnrolledCourse> loadAllEnrolledCourses() {
        List<EnrolledCourse> enrolledCourses = new ArrayList<>();
        System.err.println("Attempting to load student enrolled courses from file:  " +  ENROLLED_COURSES_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(ENROLLED_COURSES_FILE_PATH))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length >= 9){
                    String studentID = values[0].trim();
                    String courseID = values[1].trim();
                    int year = Integer.parseInt(values[2].trim());
                    int semester = Integer.parseInt(values[3].trim());
                    int examScore = Integer.parseInt(values[4].trim());
                    int assignmentScore = Integer.parseInt(values[5].trim());
                    int examWeight = Integer.parseInt(values[6].trim());
                    int assignmentWeight = Integer.parseInt(values[7].trim());
                    String failedComponent = values[8].trim();
                    enrolledCourses.add(new EnrolledCourse(studentID, courseID, year, semester, examScore, assignmentScore, examWeight, assignmentWeight, failedComponent));
                }
            }

            System.err.println("Successfully loaded " + enrolledCourses.size() + " enrolled courses.");
        }

        catch (IOException | NumberFormatException e) {
            System.err.println("ERROR: Failed to load failed components data. Check file path: " + e.getMessage());
        }

        return enrolledCourses;
    }

    /**
     * Loads course results from the CSV and populates the AcademicProfile for each student.
     * @param students The list of students already loaded by StudentDAO.
     */
    public void loadRecords(List<Student> students) {

        // Optimization: Create a map for fast lookup of students by their ID
        Map<String, Student> studentMap = students.stream()
                .collect(Collectors.toMap(
                        Student::getUserID,
                        student -> student
                        )
                );

        System.err.println("DAO: Loading academic records from " + ENROLLED_COURSES_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(ENROLLED_COURSES_FILE_PATH))) {
            String line;
            br.readLine(); // Skip the header row

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Expected format: StudentID, CourseID, Semester, Grade
                if (values.length >= 4) {
                    String studentID = values[0].trim();
                    String courseID = values[1].trim();
                    String examScore = values[4].trim();
                    String assignmentScore = values[5].trim();
                    String examWeightage = values[6].trim();
                    String assignmentWeightage = values[7].trim();

                    double finalExamScore = Double.parseDouble(examScore) / 100 * Double.parseDouble(examWeightage);
                    double finalAssignmentScore = Double.parseDouble(assignmentScore) / 100 * Double.parseDouble(assignmentWeightage);

                    double finalScore = finalExamScore + finalAssignmentScore;

                    // 1. Find the Student object
                    Student student = studentMap.get(studentID);

                    // 2. Find the Course object from the catalog
                    Course course = courseCatalog.getCourse(courseID);

                    if (student != null && course != null) {
                        // 3. Create the Result object
                        CourseResult result = new CourseResult(course);

                        for (GradingScheme gs : GradingScheme.values())
                        {
                            if (finalScore >= gs.getMinScore())
                            {
                                result.setGrade(gs.getGrade());
                                break;
                            }
                        }

                        // 4. Add it to the student's profile
                        student.getAcademicProfile().addCourseResult(result);
                    }
                }
            }
            System.err.println("DAO: Successfully processed academic records.");

        } catch (IOException e) {
            System.err.println("ERROR: Failed to load academic records: " + e.getMessage());
        }
    }
}