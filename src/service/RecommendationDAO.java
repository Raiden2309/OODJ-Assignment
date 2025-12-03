package service;

import academic.Recommendation;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;


public class RecommendationDAO {
    private final String RECOMMENDATION_ENTRY_FILE_PATH = "data/recommendation_entry.csv";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public List<Recommendation> loadRecommendations() {
        List<Recommendation> recommendations = new ArrayList<>();
        System.err.println("Attempting to load recommendations from file:  " +  RECOMMENDATION_ENTRY_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(RECOMMENDATION_ENTRY_FILE_PATH))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length == 7) {
                    String recID = values[0].trim();
                    String studentID = values[1].trim();
                    String courseID = values[2].trim();
                    String description = values[3].trim();
                    String timeline = values[4].trim();
                    Date deadline = dateFormat.parse(values[5].trim());
                    String status = values[6].trim();

                    recommendations.add(new Recommendation(
                            recID,
                            studentID,
                            courseID,
                            description,
                            timeline,
                            deadline,
                            status)
                    );
                }
            }
            System.err.println("Successfully loaded " + recommendations.size() + " recommendations.");
        }
        catch (IOException | ParseException e) {
            System.err.println("Error loading recommendations: " + e.getMessage());
        }
        return recommendations;
    }

    public void saveRecommendation(Recommendation recommendation) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECOMMENDATION_ENTRY_FILE_PATH, true))) {
            bw.write(recommendation.toString());
            bw.newLine();
        }
        catch (IOException e) {
            System.err.println("Error saving recommendation: " + e.getMessage());
        }
    }

    public void updateRecommendation(String recID, String studentID, String courseID, Recommendation newRec) {
        List<Recommendation> recommendations = loadRecommendations();

        for (int i = 0; i < recommendations.size(); i++) {
            if (recommendations.get(i).getRecID().equals(recID)) {
                recommendations.set(i, newRec);  //CHECK BACK NA
                break;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECOMMENDATION_ENTRY_FILE_PATH))) {
            bw.write("RecID,StudentID,CourseID,Description,Timeline,Deadline,Status\n");
            for (Recommendation rec : recommendations) {
                bw.write(rec.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating recommendations: " + e.getMessage());
        }
    }

    public void removeRecommendation(String recID, String studentID, String courseID) {
        List<Recommendation> recommendations = loadRecommendations();
        recommendations.removeIf(m -> m.getRecID().equals(recID));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECOMMENDATION_ENTRY_FILE_PATH))) {
            bw.write("RecID,StudentID,CourseID,Description,Timeline,Deadline,Status\n");
            for (Recommendation rec : recommendations) {
                bw.write(rec.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error removing recommendations: " + e.getMessage());
        }
    }
}

