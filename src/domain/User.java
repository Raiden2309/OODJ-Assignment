package domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

public abstract class User {
    private String userID;
    private String passwordHash;
    private SystemRole role;
    private Date loginTimestamp;
    private boolean isActive;
    private String email;

    public User(String userID, String password, SystemRole role){
        this.userID = userID;

        // Auto-detect if password is already hashed (length 64)
        if (password != null && password.length() == 64) {
            this.passwordHash = password;
        } else {
            this.passwordHash = passwordHash(password);
        }

        this.role = role;
        this.isActive = true;
        this.email = "";
    }

    private String passwordHash(String password){
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("Fatal: SHA-256 algorithm not found", e);
        }
    }

    public abstract List<String> getPermissions();

    // --- SINGLE, UNAMBIGUOUS LOGIN METHOD ---
    public boolean login(String inputPassword){
        if (!isActive) return false;

        String inputHash = passwordHash(inputPassword);

        if (inputHash != null && inputHash.equals(this.passwordHash)){
            this.loginTimestamp = new Date();
            logActivity("Successful login");
            return true;
        } else{
            logActivity("Failed login attempt");
            return false;
        }
    }

    public void logout(){
        this.isActive = false;
        logActivity("Logout");
    }

    public boolean resetPassword (String newPassword){
        this.passwordHash = passwordHash(newPassword);
        logActivity("Password reset");
        return true;
    }

    public void logActivity(String action){
        System.err.println("[" + new Date() + "] " + userID + " performed: " + action);
    }

    //Getter and Setters
    public String getUserID() { return userID; }
    public SystemRole getRole() { return role; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active){ this.isActive = active; }

    public void setPassword (String password){
        this.passwordHash = passwordHash(password);
    }

    public String getPassword() { return passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Added missing getter for loginTimestamp
    public Date getLoginTimestamp() { return loginTimestamp; }
}