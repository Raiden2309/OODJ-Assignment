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
     * Loads all users from the CSV into memory, reading the 5th column (IsActive).
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
            // Skip header: UserID,PasswordHash,Role,Email,IsActive <-- Assumed 5-column structure
            br.readLine();

            while ((line = br.readLine()) != null) {
                // Use -1 limit to ensure empty trailing fields are included
                String[] values = line.split(",", -1);

                if (values.length >= 4) {
                    String userId = values[0].trim();
                    String passwordHash = values[1].trim();
                    String roleName = values[2].trim();
                    String email = values[3].trim();

                    // Read IsActive status from the 5th column, or default to true
                    boolean isActive = true;
                    if (values.length > 4 && !values[4].trim().isEmpty()) {
                        try {
                            isActive = Boolean.parseBoolean(values[4].trim());
                        } catch (Exception e) {
                            System.err.println("Warning: Invalid IsActive value for " + userId + ". Defaulting to true.");
                        }
                    }

                    SystemRole role = new SystemRole(roleName, new ArrayList<>());
                    User user = null;

                    String staffFirstName = roleName.contains("Officer") ? "Academic" : (roleName.contains("Administrator") ? "Course" : "");
                    String staffLastName = roleName.contains("Officer") ? "Officer" : (roleName.contains("Administrator") ? "Admin" : "");


                    try {
                        switch (roleName) {
                            case "Student":
                            case "Pending":
                                user = new Student(userId, passwordHash, role, "", "", "", "", email);
                                break;
                            case "Academic Officer":
                            case "AO":
                                user = new AcademicOfficer(userId, passwordHash, role, staffFirstName, staffLastName, email);
                                break;
                            case "Course Administrator":
                            case "CA":
                                user = new CourseAdministrator(userId, passwordHash, role, staffFirstName, staffLastName, email);
                                break;
                            default:
                                System.err.println("Warning: Unknown role '" + roleName + "'. Skipping instantiation for " + userId);
                        }
                    } catch (Exception e) {
                        System.err.println("CRITICAL ERROR: Failed to instantiate user " + userId + " (" + roleName + "). Error: " + e.getMessage());
                    }

                    if (user != null) {
                        user.setEmail(email);
                        user.setActive(isActive);
                        allUsers.add(user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR loading credentials: " + e.getMessage());
        }
    }

    // Public getter for all users (Forces refresh via loadUsers())
    public List<User> loadAllUsers() {
        loadUsers();
        return allUsers;
    }


    public User login(String identifier, String plainPassword) {

        User foundUser = allUsers.stream()
                .filter(u -> u.getEmail() != null && !u.getEmail().isEmpty() && u.getEmail().equalsIgnoreCase(identifier))
                .findFirst()
                .orElse(null);

        if (foundUser == null && !identifier.contains("@")) {
            System.err.println("Attempting fallback search by UserID...");
            foundUser = allUsers.stream()
                    .filter(u -> u.getUserID().equalsIgnoreCase(identifier))
                    .findFirst()
                    .orElse(null);
        }

        if (foundUser != null) {

            if (!foundUser.isActive()) {
                System.err.println("Login Failed: User " + foundUser.getUserID() + " is inactive/pending approval.");
                JOptionPane.showMessageDialog(null, "Account is pending activation by an Administrator.", "Login Blocked", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            if (foundUser.login(plainPassword)) {
                System.err.println("Login Success for User: " + foundUser.getUserID());
                return foundUser;
            } else {
                System.err.println("Login Failed: Invalid password for user " + foundUser.getUserID());
            }
        } else {
            System.err.println("Login Failed: Identifier '" + identifier + "' not found.");
        }
        return null;
    }

    /**
     * Updates an existing user or adds a new one, then rewrites the file.
     */
    public boolean saveUserCredentials(User userToUpdate) {
        boolean found = false;

        if (userToUpdate != null) {
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getUserID().equals(userToUpdate.getUserID())) {
                    allUsers.set(i, userToUpdate);
                    found = true;
                    break;
                }
            }

            if (!found) {
                allUsers.add(userToUpdate);
            }
        }

        // Rewrite the entire CSV file
        return rewriteCredentialsFile();
    }

    /**
     * Removes a user by ID from the cache and rewrites the credentials file.
     */
    public boolean deleteUser(String userId) {
        // Must ensure we have the latest data before modifying the cache
        loadUsers();

        boolean removed = allUsers.removeIf(u -> u.getUserID().equals(userId));

        if (removed) {
            System.err.println("DAO: User " + userId + " removed from cache. Rewriting file.");
            return rewriteCredentialsFile();
        } else {
            System.err.println("DAO: User " + userId + " not found in cache for deletion.");
            return false;
        }
    }


    private boolean rewriteCredentialsFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CREDENTIALS_FILE))) {
            writer.println("UserID,PasswordHash,Role,Email,IsActive");

            for (User u : allUsers) {
                writer.printf("%s,%s,%s,%s,%s%n",
                        u.getUserID(),
                        u.getPassword(),
                        u.getRole().getRoleName(),
                        u.getEmail(),
                        u.isActive()
                );
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving credentials: " + e.getMessage());
            return false;
        }
    }
}