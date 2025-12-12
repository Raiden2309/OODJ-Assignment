package service;

import academic.RecoveryMilestone;
import domain.User; // Import User

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
            br.readLine(); // Skip header

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
                            status
                    ));
                }
            }
            System.err.println("Successfully loaded " + milestones.size() + " milestones.");
        } catch (Exception e) {
            System.err.println("Error loading milestones: " + e.getMessage());
        }
        return milestones;
    }

    // FIX: Updated to accept User object to match GUI call
    public void addMilestone(RecoveryMilestone milestone, User loggedInUser) {
        System.out.println("User " + (loggedInUser != null ? loggedInUser.getUserID() : "Unknown") + " is adding a milestone.");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MILESTONES_FILE_PATH, true))) {
            // Ensure file ends with newline before appending if not empty,
            // but usually append just works if previous write had newline.
            // Using toString() method of RecoveryMilestone which should format as CSV line
            bw.write(milestone.toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error adding milestone: " + e.getMessage());
        }
    }

    // Overloaded method for backward compatibility if needed
    public void addMilestone(RecoveryMilestone milestone) {
        addMilestone(milestone, null);
    }

    // FIX: Update signature if your GUI calls updateMilestone with User too
    public void updateMilestone(RecoveryMilestone updatedMilestone) {
        List<RecoveryMilestone> milestones = loadMilestones();
        boolean found = false;

        for (int i = 0; i < milestones.size(); i++) {
            if (milestones.get(i).getMilestoneID().equals(updatedMilestone.getMilestoneID())) {
                milestones.set(i, updatedMilestone);
                found = true;
                break;
            }
        }

        if (found) {
            rewriteFile(milestones);
        } else {
            System.err.println("Milestone ID not found for update: " + updatedMilestone.getMilestoneID());
        }
    }

    public void removeMilestone(String milestoneID) {
        List<RecoveryMilestone> milestones = loadMilestones();
        boolean removed = milestones.removeIf(m -> m.getMilestoneID().equals(milestoneID));

        if (removed) {
            rewriteFile(milestones);
        } else {
            System.err.println("Milestone ID not found for removal: " + milestoneID);
        }
    }

    // Helper to rewrite file (DRY principle)
    private void rewriteFile(List<RecoveryMilestone> milestones) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MILESTONES_FILE_PATH))) {
            bw.write("MilestoneID,StudentID,CourseID,StudyWeek,TaskDescription,Deadline,Status");
            bw.newLine();
            for (RecoveryMilestone m : milestones) {
                bw.write(m.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating file: " + e.getMessage());
        }
    }
}