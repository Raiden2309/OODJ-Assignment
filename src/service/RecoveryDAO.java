package service;

import academic.RecoveryResult;
import academic.Course;
import service.CourseCatalog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

public class RecoveryDAO {
    private final String RECOVERY_RESULTS_FILE_PATH = "data/recovery_results.csv";

    public List<RecoveryResult> loadRecoveryResults() {
        List<RecoveryResult> results = new ArrayList<>();
        System.err.println("Attempting to load recovery result from file: " + RECOVERY_RESULTS_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(RECOVERY_RESULTS_FILE_PATH))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length == 7) {
                    String attemptID = values[0].trim();
                    String studentID = values[1].trim();
                    String courseID = values[2].trim();
                    String failedComponent = values[3].trim();
                    int examScore = Integer.parseInt(values[4].trim());
                    int assignmentScore = Integer.parseInt(values[5].trim());
                    String recoveryStatus = values[6].trim();

                    Course course = CourseCatalog.getInstance().getCourse(courseID);
                    RecoveryResult result = new RecoveryResult(attemptID, studentID, courseID, course, failedComponent, examScore, assignmentScore, recoveryStatus);
                    result.setRecoveryStatus(recoveryStatus);
                    results.add(result);
                }
            }
            System.err.println("Successfully loaded " + results.size() + " recovery results.");
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load recovery result data. Check file path: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Data format error in recovery results file." + e.getMessage());
        }

        return results;
    }

    public void saveRecoveryResult(RecoveryResult result) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECOVERY_RESULTS_FILE_PATH, true))) {
            bw.write(result.toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error saving recovery result: " + e.getMessage());
        }
    }

    public void updateRecoveryResult(String studentID, String courseID, String attemptID, RecoveryResult updatedResult) {
        List<RecoveryResult> results = loadRecoveryResults();
        for (int i = 0; i < results.size(); i++) {
            RecoveryResult r = results.get(i);

            if (r.getStudentID().equals(studentID) && r.getCourse().getCourseID().equals(courseID) && r.getAttemptID().equals(attemptID)) {
                results.set(i, updatedResult);
                break;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECOVERY_RESULTS_FILE_PATH))) {
            bw.write("AttemptID,StudentID,CourseID,FailedComponent,ExamScore,AssignmentScore,RecoveryStatus\n");
            for (RecoveryResult r : results) {
                bw.write(r.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating recovery results: " + e.getMessage());
        }
    }
}