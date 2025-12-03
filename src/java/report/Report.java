package report;

import academic.AcademicProfile;
import java.util.Date;

public class Report{
    private String reportID;
    private Date generationDate;
    private AcademicProfile sourceProfile;

    public Report (AcademicProfile profile){
        this.reportID = "RPT-" + System.currentTimeMillis();
        this.sourceProfile = profile;
        this.generationDate = new Date();
    }

    public String getContent(){
        return String.format("Report Content for %s (CGPA: %.2f) generated on %s",
                sourceProfile.getStudentID(),
                sourceProfile.getCGPA(),
                generationDate);
    }

    public String getReportID(){
        return reportID;
    }
}