package domain;

import academic.*;
import service.NotificationService;
import java.util.List;

public class Student extends User{
    private String firstName;
    private String lastName;
    private String major;
    private String academicYear;
    private String email;
    private AcademicProfile academicProfile;

    public Student (String firstName, String lastName, String major, String academicYear, String email){
        super (userID, password, role);
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.academicYear = academicYear;
        this.email = email;
        this.academicProfile = new AcademicProfile (userID);
    }

    @Override
    public List<String> getPermissions(){
        return getRole().getPermissions();
    }

    public AcademicProfile viewAcademicProfile(){
        return academicProfile;
    }

    public EligibilityCheck checkEligibility() {
        EligibilityCheck check = new EligibilityCheck();
        check.setEligible(check.checkCGPA(academicProfile) && check.checkFailedCourseLimit(academicProfile));
        return check;
    }

    public Enrolment enrol(CourseRecoveryPlan plan){
        if (checkEligibility().isEligibile){
            Enrolment enrolment = new Enrolment(this.getUserID(), plan);
            notify("Enrolment Confirmation", "You have been successfully enrolled.");
            logActivity("Enrolled in plan " + plan.getPlanID());
            return enrolment;
        }
        logActivity("Failed to enroll due to ineligibility.");
        return null;
    }

    public void notify(String subject, String message){
        NotificationService service = new NotificationService();
        service.sendEmail(email, subject, message);
    }

    //Getters
    public String getEmail(){
        return email;
    }

    public AcademicProfile getAcademicProfile() {
        return academicProfile;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }
}