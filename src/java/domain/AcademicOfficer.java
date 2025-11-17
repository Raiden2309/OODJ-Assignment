package java.domain;

import java.util.List;

public class AcademicOfficer extends User{
    private String firstName;
    private String lastName;
    private String contactOffice;

    public AcademicOfficer(String userID, String password, SystemRole role, String alice, String johnson, String contactOffice){
        super(userID, password, role);
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactOffice = contactOffice;
    }

    @Override
    public List<String> getPermissions() {
        return getRole().getPermissions();
    }

    public User createAccount(String userType){
        logActivity("Creating a new " + userType + "account.");
        return null;
    }

    public boolean updateAccount(User user){
        logActivity("Updating account for "+ user.getUserID());
        return true;
    }

    public boolean deactivateAccount(User user){
        user.setActive(false);
        logActivity("Deactivated account: " + user.getUserID());
        return true;
    }

    public List<Student> getEligibleStudents(){
        logActivity("Retrieving list of eligible students.");
        return null;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }
}