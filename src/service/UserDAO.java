package service;

import domain.User;
import domain.Student;
import domain.AcademicOfficer;
import domain.CourseAdministrator;
import domain.SystemRole;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class UserDAO {
    private final String CREDENTIALS_FILE = "data/user_credentials.csv";

    // In-memory cache for fast lookup
    private List<User> allUsers = new ArrayList<>();

    public UserDAO() {
        loadUsers();
    }

    /**
     * Loads all users from the CSV into memory.
     * This simulates a database connection.
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
                String[] values = line.split(",");
                // Robust check: ensure we have at least ID, Hash, Role
                if (values.length >= 3) {
                    String userId = values[0].trim();
                    String hash = values[1].trim();
                    String roleName = values[2].trim();
                    String email = ""; // Default empty

                    // Only read email if the column exists (backward compatibility)
                    if (values.length >= 4) {
                        email = values[3].trim();
                    }

                    // Create the appropriate User object based on role
                    User user = createUserInstance(userId, hash, roleName, email);
                    if (user != null) {
                        allUsers.add(user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading credentials: " + e.getMessage());
        }
    }

    /**
     * Factory method to create specific User objects.
     * Note: This creates a 'shell' user with credentials. Full details (Name, etc.)
     * are loaded by other DAOs (StudentDAO) if needed.
     */
    private User createUserInstance(String id, String hash, String roleName, String email) {
        // Simple role mapping
        SystemRole role = new SystemRole(roleName, new ArrayList<>());
        User user = null;

        switch (roleName) {
            case "Student":
                // Assuming Student constructor matches: ID, Hash, Role, First, Last, Major, Year, Email
                user = new Student(id, hash, role, "", "", "", "", email);
                break;
            case "AcademicOfficer":
                // Constructor: ID, Hash, Role, First, Last, Office
                // Does NOT accept email, so we set it manually below
                user = new AcademicOfficer(id, hash, role, "", "", "");
                break;
            case "CourseAdministrator":
                // Constructor: ID, Hash, Role, First, Last, Dept
                // Does NOT accept email, so we set it manually below
                user = new CourseAdministrator(id, hash, role, "", "", "");
                break;
            default:
                return null;
        }

        // FIX: Explicitly set the email for ALL user types if the constructor didn't handle it
        if (user != null && (user.getEmail() == null || user.getEmail().isEmpty())) {
            user.setEmail(email);
        }

        return user;
    }

    // FIX: Added the missing login method required by LoginView
    /**
     * Authenticates a user via Email and Password.
     * @param email The email input.
     * @param plainPassword The plain text password input.
     * @return The User object if successful, null otherwise.
     */
    public User login(String email, String plainPassword) {
        for (User user : allUsers) {
            // Check if email matches (case-insensitive) and is not empty
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
                // Use the User object's own login method to verify the hash
                if (user.login(plainPassword)) {
                    return user;
                }
            }
        }
        return null; // Login failed
    }

    /**
     * Updates the user's password and saves the file.
     */
    public boolean saveUserCredentials(User userToUpdate) {
        // 1. Update the in-memory list
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUserID().equals(userToUpdate.getUserID())) {
                allUsers.set(i, userToUpdate);
                break;
            }
        }

        // 2. Rewrite the entire CSV file
        return rewriteCredentialsFile();
    }

    private boolean rewriteCredentialsFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CREDENTIALS_FILE))) {
            writer.println("UserID,PasswordHash,Role,Email"); // Header

            for (User u : allUsers) {
                String line = String.join(",",
                        u.getUserID(),
                        u.getPassword(), // This must return the HASH string
                        u.getRole().getRoleName(),
                        u.getEmail()
                );
                writer.println(line);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving credentials: " + e.getMessage());
            return false;
        }
    }
}