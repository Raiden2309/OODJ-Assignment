package domain;

import academic.CourseRecoveryPlan;
import academic.Enrolment;
import academic.RecoveryMilestone;

import java.util.List;

public class CourseAdministrator extends User {
    private String firstName;
    private String lastName;
    private String department;

    public CourseAdministrator (String userID, String password, SystemRole role, String firstName, String lastName, String department){
        super(userID, password, role);
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
    }

    @Override
    public List<String> getPermissions(){
        return getRole().getPermissions();
    }

    public boolean addRecoveryPlan(CourseRecoveryPlan plan){
        logActivity("Adding recovery plan: "+ plan.getPlanID());
        return true;
    }

    public String monitorProgress(CourseRecoveryPlan plan){
        List<RecoveryMilestone> milestones = plan.getMilestones();
        if (milestones.isEmpty()) return "No milestones found";

        long completed = milestones.stream().filter(RecoveryMilestone::getStatus).count();
        double progress = ((double) completed / milestones.size()) * 100;
        return String.format("%.2f%% Complete (%d/%d Milestones)", progress, completed, milestones.size());
    }

    public boolean  evaluatePlan(CourseRecoveryPlan plan){
        return plan.getStatus().equals("Completed") && plan.getMilestones().stream().allMatch(RecoveryMilestone::getStatus);
    }

    public void setFinalRecoveryGrade(Enrolment enrolment, String finalGrade){
        logActivity("Final grade of " + finalGrade + " set of enrolment " + enrolment.getEnrolmentID());
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }
}