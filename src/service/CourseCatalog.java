package service;

import academic.Course;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class CourseCatalog {
    private static CourseCatalog instance = new CourseCatalog();
    private Map<String, Course> courses;
    private final String COURSE_FILE_PATH = "data/course_assessment_information.csv";

    public CourseCatalog(){             //change private to public (from edwin's one)
        this.courses = new HashMap<>();
        loadCourses();
    }

    public static CourseCatalog getInstance(){
        return instance;
    }

    public void loadCourses(){
        System.err.println("Loading courses from file: " + COURSE_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(COURSE_FILE_PATH))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null){
                String[] values = line.split(",");
                if (values.length >= 7){
                    String courseID = values[0].trim();
                    String courseName = values[1].trim();
                    String semester = values[3].trim();
                    String instructor = values[4].trim();

                    int credits = Integer.parseInt(values[2].trim());
                    int examWeight =  Integer.parseInt(values[5].trim());
                    int assignmentWeight = Integer.parseInt(values[6].trim());

                    Course course = new Course (courseID,
                            courseName,
                            credits,
                            semester,
                            instructor,
                            examWeight,
                            assignmentWeight
                    );

                    courses.put(courseID, course);
                }
            }
            System.err.println("Successfully loaded " + courses.size() + " courses.");
        } catch (IOException e){
            System.err.println("ERROR: Failed to load courses from file." + e.getMessage());
        } catch (NumberFormatException e){
            System.err.println("ERROR: Data format error in course file." + e.getMessage());
        }
    }

    public Course getCourse(String courseID){
        return courses.get(courseID);
    }

    public Collection<Course> getAllCourses() {
        return this.courses.values();
    }
}