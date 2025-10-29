package domain;

import academic.*;
import service.NotificationService;
import java.util.List;

public class Student extends User {
    private String firstName;
    private String lastName;
    private String major;
    private String academicYear;
    private String email;
    private AcademicProfile academicProfile;

    public Student (String studentID, String password, SystemRole role, String firstName, String lastName, String major, String academicYear, String email){
        // Pass arguments received by this constructor to the super constructor (User)
        super(studentID, password, role);
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.academicYear = academicYear;
        this.email = email;

        this.academicProfile = new AcademicProfile(studentID);
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
        check.setEligible(check.checkCPGA(academicProfile) && check.checkFailedCourseLimit(academicProfile));
        return check;
    }

    // FIX 4: Use 'Enrollment' (double L) for class/variable names.
    public Enrolment enrol(CourseRecoveryPlan plan){
        // FIX 3: Check is done by calling the getter method 'isEligible()'
        if (checkEligibility().isEligible()){
            Enrolment enrolment = new Enrolment(this.getUserID(), plan);
            notify("Enrollment Confirmation", "You have been successfully enrolled.");
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
    public String getStudentID(){
        return getUserID();
    }

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