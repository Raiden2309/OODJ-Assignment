package service;

import academic.RecoveryMilestone;
import domain.User;

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
        File file = new File(MILESTONES_FILE_PATH);
        if (!file.exists()) {
            return milestones;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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
                            milestoneID, studentID, courseID, studyWeek, taskDescription, deadline, status
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading milestones: " + e.getMessage());
        }
        return milestones;
    }

    public void addMilestone(RecoveryMilestone milestone, User loggedInUser) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MILESTONES_FILE_PATH, true))) {
            File file = new File(MILESTONES_FILE_PATH);
            if (file.length() == 0) {
                bw.write("MilestoneID,StudentID,CourseID,StudyWeek,TaskDescription,Deadline,Status");
                bw.newLine();
            }
            bw.write(milestone.toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error adding milestone: " + e.getMessage());
        }
    }

    public void addMilestone(RecoveryMilestone milestone) {
        addMilestone(milestone, null);
    }

    public void updateMilestone(RecoveryMilestone updatedMilestone) {
        List<RecoveryMilestone> milestones = loadMilestones();
        boolean found = false;

        for (int i = 0; i < milestones.size(); i++) {
            String currentID = milestones.get(i).getMilestoneID().trim();
            String targetID = updatedMilestone.getMilestoneID().trim();

            if (currentID.equalsIgnoreCase(targetID)) {
                milestones.set(i, updatedMilestone);
                found = true;
                break;
            }
        }

        if (found) {
            rewriteFile(milestones);
            System.out.println("Milestone " + updatedMilestone.getMilestoneID() + " updated successfully.");
        } else {
            System.err.println("Milestone ID not found for update: " + updatedMilestone.getMilestoneID());
        }
    }

    public void removeMilestone(String milestoneID) {
        List<RecoveryMilestone> milestones = loadMilestones();
        boolean removed = milestones.removeIf(m -> m.getMilestoneID().trim().equalsIgnoreCase(milestoneID.trim()));

        if (removed) {
            rewriteFile(milestones);
        } else {
            System.err.println("Milestone ID not found for removal: " + milestoneID);
        }
    }

    public String generateNextID() {
        List<RecoveryMilestone> milestones = loadMilestones();
        int maxId = 0;
        for (RecoveryMilestone m : milestones) {
            try {
                String numPart = m.getMilestoneID().replaceAll("[^0-9]", "");
                int id = Integer.parseInt(numPart);
                if (id > maxId) maxId = id;
            } catch (Exception e) {}
        }
        return String.format("M%03d", maxId + 1);
    }

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