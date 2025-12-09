package service;

import academic.CourseRecoveryPlan;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseRecoveryPlanDAO {

    private final String PLANS_FILE_PATH = "data/recovery_plans.csv";

    public List<CourseRecoveryPlan> loadAllPlans() {
        List<CourseRecoveryPlan> plans = new ArrayList<>();
        System.err.println("DAO: Loading recovery plans from " + PLANS_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(PLANS_FILE_PATH))) {
            String line;
            br.readLine(); // Skip header: PlanID,CourseID,Recommendation,Status

            while ((line = br.readLine()) != null) {
                // Use a limit of -1 to include empty trailing strings if any
                String[] values = line.split(",", -1);

                if (values.length >= 4) {
                    String planID = values[0].trim();
                    String courseID = values[1].trim();
                    String recommendation = values[2].trim();
                    String status = values[3].trim();

                    // Create the plan object
                    // Note: Constructor order is (PlanID, CourseID, Recommendation)
                    CourseRecoveryPlan plan = new CourseRecoveryPlan(planID, courseID, recommendation);
                    plan.setStatus(status);

                    plans.add(plan);
                }
            }
            System.err.println("DAO: Successfully loaded " + plans.size() + " recovery plans.");

        } catch (IOException e) {
            System.err.println("ERROR: Failed to load recovery plans: " + e.getMessage());
        }

        return plans;
    }


    public CourseRecoveryPlan getPlanByID(String planID) {
        List<CourseRecoveryPlan> allPlans = loadAllPlans();
        for (CourseRecoveryPlan plan : allPlans) {
            if (plan.getPlanID().equalsIgnoreCase(planID)) {
                return plan;
            }
        }
        return null;
    }
}