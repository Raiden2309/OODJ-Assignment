package domain;

import service.PasswordUtil;
import java.util.Date;
import java.util.List;

public abstract class User {
    private String userID;
    private String passwordHash;
    private SystemRole role;
    private Date loginTimestamp;
    private boolean isActive;

    public User(String userID, String password, SystemRole role){
        this.userID = userID;
        this.passwordHash = passwordHash(password);
        this.role = role;
        this.isActive = true;
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

    public boolean login(){
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

    public void LogActivity(String action){
        System.err.println("[" + new Date() + "] " + userID + "performed: " + action);
    }

    //Getter and Setters
    public String getUserID() {
        return userID;
    }

    public SystemRole getRole() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void SetActive(boolean active){
        this.isActive = active;
    }

    public void setPassword (String password){
        this.passwordHash = passwordHash(password);
    }
}