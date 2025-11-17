package java.academic;

import java.util.Date;

public class RecoveryMilestone{
    private String studyWeek;
    private String taskDescription;
    private Date deadline;
    private boolean completionStatus;

    public RecoveryMilestone(String studyWeek, String taskDescription, Date deadline){
        this.studyWeek = studyWeek;
        this.taskDescription = taskDescription;
        this.deadline = deadline;
        this.completionStatus = false;
    }

    public void setCompletionStatus(boolean isDone){
        this.completionStatus = isDone;
    }

    public boolean getStatus(){
        return completionStatus;
    }

    public String getTaskDescription() {
        return taskDescription;
    }
}