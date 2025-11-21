package java.service;

import java.academic.AcademicProfile;
import java.academic.Course;
import java.academic.CourseResult;
import java.domain.Student;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AcademicRecordDAO {

    private final String RECORDS_FILE_PATH = "data/academic_records.csv";

    // Dependencies
    private CourseCatalog courseCatalog = CourseCatalog.getInstance();

    /**
     * Loads course results from the academic records CSV and populates the AcademicProfile
     * for a given list of students.
     * @param students The list of all students loaded into the system.
     */
    public void loadRecords(List<Student> students) {

        Map<String, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getUserID, student -> student));

        System.err.println("DAO: Loading academic records from " + RECORDS_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(RECORDS_FILE_PATH))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {

                    String studentID = values[0].trim();
                    String courseID = values[1].trim();
                    String grade = values[3].trim();

                    // 2. Find the Student object and the Course object
                    Student student = studentMap.get(studentID);
                    Course course = courseCatalog.getCourse(courseID); // Fetches course details

                    if (student == null) {
                        System.err.println("WARNING: Record found for non-existent student ID: " + studentID);
                        continue;
                    }
                    if (course == null) {
                        System.err.println("WARNING: Record found for non-existent course ID: " + courseID);
                        continue;
                    }


                    CourseResult result = new CourseResult(course);
                    result.setGrade(grade);

                    student.getAcademicProfile().addCourseResult(result);
                }
            }
            System.err.println("DAO: Successfully finished processing academic records.");

        } catch (IOException e) {
            System.err.println("FATAL ERROR: Failed to load academic records. Check file path/data: " + e.getMessage());
        }
    }
}