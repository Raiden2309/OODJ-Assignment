package domain;

import academic.*;
import service.NotificationService;
import java.util.List;

public class Student extends User
{
    private String studentId;
    private String firstName;
    private String lastName;
    private String programId;
    private String email;
    private String recoveryEligibility;
    private AcademicProfile academicProfile;

    public Student(String studentId, String password, SystemRole role, String firstName, String lastName, String programId, String email, String recoveryEligibility)
    {
        super(studentId, password, role);
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.programId = programId;
        this.email = email;
        this.recoveryEligibility = recoveryEligibility;

        this.academicProfile = new AcademicProfile(studentId);
    }

    @Override
    public List<String> getPermissions() {
        return getRole().getPermissions();
    }

    public AcademicProfile viewAcademicProfile() {
        return academicProfile;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProgramId() {
        return programId;
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
