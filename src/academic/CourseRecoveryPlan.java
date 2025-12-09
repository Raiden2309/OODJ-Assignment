package academic;

import java.util.ArrayList;
import java.util.List;

public class CourseRecoveryPlan {
    private String planID;
    private String courseID;
    private String recommendation;
    private List<RecoveryMilestone> milestones;
    private String status;

    public CourseRecoveryPlan(String planID, String courseID, String recommendation){
        this.planID = planID;
        this.courseID = courseID;
        this.recommendation = recommendation;
        this.milestones = new ArrayList<>();
        this.status = "Draft";
    }

    public boolean addMilestone(RecoveryMilestone milestone){
        return milestones.add(milestone);
    }

    public List<String> listFailedComponents(){
        return List.of("Exam Topic A", "Assigmnet 2");
    }

    public String updateProgress() {
        long completed = milestones.stream().filter(RecoveryMilestone::getStatus).count();
        if (milestones.isEmpty()) {
            this.status = "No Milestones";
        } else if (completed == milestones.size()) {
            this.status = "Ready for Evaluation";
        } else {
            this.status = "In Progress";
        }
        return this.status;
    }

    public String getPlanID() {
        return planID;
    }

    // NEW GETTER - This fixes your error
    public String getCourseID() {
        return courseID;
    }

    public List<RecoveryMilestone> getMilestones() {
        return milestones;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}