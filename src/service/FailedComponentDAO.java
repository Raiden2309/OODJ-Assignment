package service;

import academic.FailedComponent;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FailedComponentDAO {
    private final String FAILED_COMPONENT_FILE_PATH = "data/failed_components.csv";

    public List<FailedComponent> loadAllFailedComponents() {
        List<FailedComponent> failedComponents = new ArrayList<>();
        System.err.println("Attempting to load failed components from file:  " +  FAILED_COMPONENT_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(FAILED_COMPONENT_FILE_PATH))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length >= 7){
                    String studentID = values[0].trim();
                    String courseID = values[1].trim();
                    int examScore = Integer.parseInt(values[2].trim());
                    int assignmentScore = Integer.parseInt(values[3].trim());
                    int examWeight = Integer.parseInt(values[4].trim());
                    int assignmentWeight = Integer.parseInt(values[5].trim());
                    String failedComponent = values[6].trim();
                    failedComponents.add(new FailedComponent(studentID, courseID, examScore, assignmentScore, examWeight, assignmentWeight, failedComponent));
                }
            }

            System.err.println("Successfully loaded " + failedComponents.size() + " failed components.");
        }

        catch (IOException | NumberFormatException e) {
            System.err.println("ERROR: Failed to load failed components data. Check file path: " + e.getMessage());
        }

        return failedComponents;
    }
}