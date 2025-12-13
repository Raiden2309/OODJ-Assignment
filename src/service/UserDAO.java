package service;

import domain.User;
import domain.Student;
import domain.AcademicOfficer;
import domain.CourseAdministrator;
import domain.SystemRole;

import javax.swing.JOptionPane;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final String CREDENTIALS_FILE = "data/user_credentials.csv";

    // In-memory cache for fast lookup
    private List<User> allUsers = new ArrayList<>();

    public UserDAO() {
        loadUsers();
    }

    /**
     * Loads all users from the CSV into memory.
     */
    private void loadUsers() {
        allUsers.clear();
        File file = new File(CREDENTIALS_FILE);

        if (!file.exists()) {
            System.err.println("User credentials file not found: " + CREDENTIALS_FILE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header: UserID,PasswordHash,Role,Email

            while ((line = br.readLine()) != null) {
                // Use -1 limit to ensure empty trailing fields are included
                String[] values = line.split(",", -1);

                if (values.length >= 3) {
                    String userId = values[0].trim();
                    String passwordHash = values[1].trim();
                    String roleName = values[2].trim();
                    String email = values.length > 3 ? values[3].trim() : "";

                    SystemRole role = new SystemRole(roleName, new ArrayList<>());
                    User user = null;

                    // Use a slightly more descriptive name placeholder for staff
                    String staffFirstName = roleName.contains("Officer") ? "Academic" : (roleName.contains("Administrator") ? "Course" : "");
                    String staffLastName = roleName.contains("Officer") ? "Officer" : (roleName.contains("Administrator") ? "Admin" : "");


                    // Instantiation logic based on role's expected constructor arguments
                    try {
                        switch (roleName) {
                            case "Student":
                                // Student needs 8 arguments (ID, Hash, Role, FName, LName, Major, Year, Email)
                                user = new Student(userId, passwordHash, role, "", "", "", "", email);
                                user.setActive(true);
                                break;
                            case "Academic Officer":
                            case "AO":
                                // CA/AO expect 6 arguments: (ID, Hash, Role, FName, LName, ContactOffice/Email)
                                // We pass the email in the last argument slot as per CSV layout
                                user = new AcademicOfficer(userId, passwordHash, role, staffFirstName, staffLastName, email);
                                user.setActive(true);
                                break;
                            case "Course Administrator":
                            case "CA":
                                // CA/AO expect 6 arguments
                                user = new CourseAdministrator(userId, passwordHash, role, staffFirstName, staffLastName, email);
                                user.setActive(true);
                                break;
                            case "Pending":
                                // Pending uses Student placeholder (8 arguments needed)
                                user = new Student(userId, passwordHash, role, "Pending", "User", "", "", email);
                                user.setActive(false);
                                break;
                            default:
                                System.err.println("Warning: Unknown role '" + roleName + "'. Skipping instantiation for " + userId);
                        }
                    } catch (Exception e) {
                        System.err.println("CRITICAL ERROR: Failed to instantiate user " + userId + " (" + roleName + "). Constructor mismatch or missing file. Error: " + e.getMessage());
                    }

                    if (user != null) {
                        // FIX: Ensure the email is set on the base User object (which is needed for login lookup)
                        user.setEmail(email);
                        allUsers.add(user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR loading credentials: " + e.getMessage());
        }
    }

    // Public getter for all users
    public List<User> loadAllUsers() {
        loadUsers();
        return allUsers;
    }


    /**
     * Attempts to log in using the provided identifier (EMAIL) and password.
     */
    public User login(String identifier, String plainPassword) {

        // 1. Find user by EMAIL (Prioritized for security)
        User foundUser = allUsers.stream()
                // Defensive filter: Check if email is available and matches identifier
                .filter(u -> u.getEmail() != null && !u.getEmail().isEmpty() && u.getEmail().equalsIgnoreCase(identifier))
                .findFirst()
                .orElse(null);

        // FALLBACK: If email wasn't found, check ID (for backward compatibility if user types ID)
        if (foundUser == null && !identifier.contains("@")) {
            System.err.println("Attempting fallback search by UserID...");
            foundUser = allUsers.stream()
                    .filter(u -> u.getUserID().equalsIgnoreCase(identifier))
                    .findFirst()
                    .orElse(null);
        }

        if (foundUser != null) {

            // Check if the user is active (crucial for new pending users)
            if (!foundUser.isActive()) {
                System.err.println("Login Failed: User " + foundUser.getUserID() + " is inactive/pending approval.");
                JOptionPane.showMessageDialog(null, "Account is pending activation by an Administrator.", "Login Blocked", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            // Assume the User object's own login method to verify the hash
            if (foundUser.login(plainPassword)) {
                System.err.println("Login Success for User: " + foundUser.getUserID());
                return foundUser;
            } else {
                System.err.println("Login Failed: Invalid password for user " + foundUser.getUserID());
            }
        } else {
            System.err.println("Login Failed: Identifier '" + identifier + "' not found.");
        }
        return null; // Login failed
    }

    /**
     * Updates the user's password/details or adds a new user if not found.
     */
    public boolean saveUserCredentials(User userToUpdate) {
        boolean found = false;

        // 1. Update the in-memory list
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUserID().equals(userToUpdate.getUserID())) {
                allUsers.set(i, userToUpdate);
                found = true;
                break;
            }
        }

        // If it's a NEW user
        if (!found && userToUpdate != null) {
            allUsers.add(userToUpdate);
        }

        // 2. Rewrite the entire CSV file
        return rewriteCredentialsFile();
    }

    private boolean rewriteCredentialsFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CREDENTIALS_FILE))) {
            writer.println("UserID,PasswordHash,Role,Email"); // Header

            for (User u : allUsers) {
                // Ensure the output format remains consistent with the CSV structure
                writer.printf("%s,%s,%s,%s%n",
                        u.getUserID(),
                        u.getPassword(),
                        u.getRole().getRoleName(),
                        u.getEmail()
                );
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving credentials: " + e.getMessage());
            return false;
        }
    }
}