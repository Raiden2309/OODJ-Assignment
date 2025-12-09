package service;

import academic.Course;
import academic.CourseResult;
import domain.Student;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AcademicRecordDAO {

    // Ensure this matches the file name you created in the data folder
    private final String RECORDS_FILE_PATH = "data/academic_records.csv";

    // Dependency: We need the catalog to look up full Course objects by ID
    private CourseCatalog courseCatalog = CourseCatalog.getInstance();

    /**
     * Loads course results from the CSV and populates the AcademicProfile for each student.
     * @param students The list of students already loaded by StudentDAO.
     */
    public void loadRecords(List<Student> students) {

        // Optimization: Create a map for fast lookup of students by their ID
        Map<String, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getUserID, student -> student));

        System.err.println("DAO: Loading academic records from " + RECORDS_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(RECORDS_FILE_PATH))) {
            String line;
            br.readLine(); // Skip the header row

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Expected format: StudentID, CourseID, Semester, Grade
                if (values.length >= 4) {

                    String studentID = values[0].trim();
                    String courseID = values[1].trim();
                    String grade = values[3].trim();

                    // 1. Find the Student object
                    Student student = studentMap.get(studentID);

                    // 2. Find the Course object from the catalog
                    Course course = courseCatalog.getCourse(courseID);

                    if (student != null && course != null) {
                        // 3. Create the Result object
                        CourseResult result = new CourseResult(course);
                        result.setGrade(grade);

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
