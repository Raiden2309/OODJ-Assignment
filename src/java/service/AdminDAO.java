package java.service;

import java.domain.*;
import java.util.List;

public class AdminDAO {
    private SystemRole officerRole = new SystemRole("AcademicOfficer", List.of("Manage Users", "Authorize Enrollment"));
    private SystemRole adminRole = new SystemRole("CourseAdministrator", List.of("Manage Plans", "Set Grades"));

    public List<User> loadDefaultStaff() {
        AcademicOfficer officer = new AcademicOfficer("AO001", "pass", officerRole, "Alice", "Johnson", "Office A101");
        CourseAdministrator admin = new CourseAdministrator("CA001", "pass", adminRole, "Robert", "Chen", "Computer Science");

        return List.of(officer, admin);
    }
}