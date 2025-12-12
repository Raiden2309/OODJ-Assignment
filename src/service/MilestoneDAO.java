package service;

import academic.RecoveryMilestone;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MilestoneDAO {
    private final String MILESTONES_FILE_PATH = "data/milestones.csv";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public List<RecoveryMilestone> loadMilestones() {
        List<RecoveryMilestone> milestones = new ArrayList<>();
        System.err.println("Attempting to load milestones from file: " + MILESTONES_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(MILESTONES_FILE_PATH))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 7) {
                    String milestoneID = values[0].trim();
                    String studentID = values[1].trim();
                    String courseID = values[2].trim();
                    String studyWeek = values[3].trim();
                    String taskDescription = values[4].trim();
                    Date deadline = dateFormat.parse(values[5].trim());
                    String status = values[6].trim();

                    milestones.add(new RecoveryMilestone(
                            milestoneID,
                            studentID,
                            courseID,
                            studyWeek,
                            taskDescription,
                            deadline,
                            status)
                    );
                }
            }
            System.err.println("Successfully loaded " + milestones.size() + " recommendations.");
        }
        catch (Exception e) {
            System.err.println("Error loading milestones: " + e.getMessage());
        }
        return milestones;
    }

    public void saveMilestone(RecoveryMilestone milestone) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MILESTONES_FILE_PATH, true))) {
            bw.write(milestone.toString());
            bw.newLine();
        }
        catch (IOException e) {
            System.err.println("Error saving milestone: " + e.getMessage());
        }
    }

    public void updateMilestone(String milestoneID, RecoveryMilestone updatedMilestone) {
        List<RecoveryMilestone> milestones = loadMilestones();

        for (int i = 0; i < milestones.size(); i++) {
            if (milestones.get(i).getMilestoneID().equals(milestoneID)) {
                milestones.set(i, updatedMilestone);
                break;
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MILESTONES_FILE_PATH))) {
            bw.write("MilestoneID,StudentID,CourseID,StudyWeek,TaskDescription,Deadline,Status\n");
            for (RecoveryMilestone m : milestones) {
                bw.write(m.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating milestones: " + e.getMessage());
        }
    }

    public void removeMilestone(String milestoneID) {
        List<RecoveryMilestone> milestones = loadMilestones();
        milestones.removeIf(m -> m.getMilestoneID().equals(milestoneID));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MILESTONES_FILE_PATH))) {
            bw.write("MilestoneID,StudentID,CourseID,StudyWeek,TaskDescription,Deadline,Status\n");
            for (RecoveryMilestone m : milestones) {
                bw.write(m.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error removing milestone: " + e.getMessage());
        }
    }
}