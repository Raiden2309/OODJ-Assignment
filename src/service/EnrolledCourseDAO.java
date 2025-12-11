package service;

import academic.EnrolledCourse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnrolledCourseDAO {
    private final String FAILED_COMPONENT_FILE_PATH = "data/student_enrolled_courses.csv";

    public List<EnrolledCourse> loadAllEnrolledCourses() {
        List<EnrolledCourse> enrolledCourses = new ArrayList<>();
        System.err.println("Attempting to load student enrolled courses from file:  " +  FAILED_COMPONENT_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(FAILED_COMPONENT_FILE_PATH))) {
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
}