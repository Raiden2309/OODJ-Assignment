package domain;

import academic.AcademicProfile;
import academic.CourseRecoveryPlan;
import academic.EligibilityCheck;
import academic.Enrolment;
import service.EnrollmentDAO;
import service.NotificationService;

import java.util.List;

public class Student extends User {
    private String firstName;
    private String lastName;
    private String major;
    private String academicYear;
    private String email;
    private String recoveryEligibility;
    private AcademicProfile academicProfile;

    // Updated Constructor to match the 8 arguments passed by DataAccess.java
    // Order: ID, Password, Role, First, Last, Major, Year, Email
    public Student(String studentId, String password, SystemRole role, String firstName, String lastName, String major, String academicYear, String email) {
        super(studentId, password, role);
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.academicYear = academicYear;
        this.email = email;
        this.recoveryEligibility = "Unknown"; // Default until checked

        this.academicProfile = new AcademicProfile(studentId);
    }

    @Override
    public List<String> getPermissions() {
        return getRole().getPermissions();
    }

    public AcademicProfile viewAcademicProfile() {
        return academicProfile;
    }

    // Explicit getter for AcademicProfile to fix "Cannot resolve method" errors
    public AcademicProfile getAcademicProfile() {
        return academicProfile;
    }

    public EligibilityCheck checkEligibility() {
        EligibilityCheck check = new EligibilityCheck();
        // Ensure checkCGPA and checkFailedCourseLimit are public in EligibilityCheck
        boolean eligible = !check.checkCGPA(academicProfile) || !check.checkFailedCourseLimit(academicProfile);
        check.setEligible(eligible);

        // Update local status string for display
        this.recoveryEligibility = eligible ? "Eligible" : "Not Eligible";

        return check;
    }

    public Enrollment enroll(CourseRecoveryPlan plan) {
        if (checkEligibility().isEligible()) {
            Enrollment enrollment = new Enrollment(this.getUserID(), plan);

            EnrollmentDAO dao = new EnrollmentDAO();
            if (dao.saveEnrolment(enrollment)) {
                notify("Enrolment Confirmation", "You have been successfully enrolled.");
                logActivity("Enrolled in plan " + plan.getPlanID());
                return enrollment;
            } else {
                logActivity("ERROR: Enrollment failed due to data saving error.");
                return null;
            }
        }
        logActivity("Failed to enroll due to ineligibility.");
        return null;
    }

    public void notify(String subject, String message) {
        NotificationService service = new NotificationService();
        service.sendEmail(email, subject, message);
    }

    // --- Getters ---

    public String getStudentId() {
        return getUserID(); // Use parent method
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMajor() {
        return major;
    }

    // Added to match constructor
    public String getAcademicYear() {
        return academicYear;
    }

    public String getEmail() {
        return email;
    }

    public String getRecoveryEligibility() {
        return recoveryEligibility;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}