package resources;

import domain.User;
import domain.Student;
import service.UserDAO;
import service.StudentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageAccountView extends JFrame {

    private User currentUser;
    private UserDAO userDAO;
    private StudentDAO studentDAO;

    public ManageAccountView(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();

        // Window Setup
        setTitle("Account Settings - " + currentUser.getUserID());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        // DISPOSE_ON_CLOSE ensures only this window closes, keeping Dashboard open
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- TOP HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Header Title Panel (Left)
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);

        // REMOVED BACK BUTTON as requested

        JLabel appTitle = new JLabel("Course Recovery System");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(Color.WHITE);

        leftHeader.add(appTitle);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // 1. Profile Card
        JPanel profileCard = createCard("Profile Information");
        populateProfileInfo(profileCard);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        contentPanel.add(profileCard, gbc);

        // 2. Dynamic Right Card
        JPanel rightCard;
        String role = currentUser.getRole().getRoleName();

        if ("AcademicOfficer".equalsIgnoreCase(role)) {
            // Note: If you moved Student Management to sidebar,
            // this panel might just be "Account Actions" for Admin too.
            // Keeping logic flexible here.
            rightCard = createCard("Account Actions");
            populateSettingsActions(rightCard);
        } else {
            rightCard = createCard("Account Actions");
            populateSettingsActions(rightCard);
        }

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        contentPanel.add(rightCard, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 50));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);

        card.add(titlePanel, BorderLayout.NORTH);
        return card;
    }

    private void populateProfileInfo(JPanel card) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);

        addDetailRow(detailsPanel, "User ID", currentUser.getUserID());
        addDetailRow(detailsPanel, "Email", currentUser.getEmail());
        addDetailRow(detailsPanel, "Role", currentUser.getRole().getRoleName());

        if (currentUser instanceof Student) {
            Student s = (Student) currentUser;
            addDetailRow(detailsPanel, "Full Name", s.getFullName());
            addDetailRow(detailsPanel, "Major", s.getMajor());
            addDetailRow(detailsPanel, "Year", s.getAcademicYear());
        }

        detailsPanel.add(Box.createVerticalGlue());

        // Logout Button
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, new Color(255, 69, 58));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(150, 40));
        logoutBtn.addActionListener(e -> {
            // Close all windows and go to Login
            // Since this is DISPOSE_ON_CLOSE, we might need to close Dashboard too if we want full logout
            // Ideally, loop through frames, but simple approach:
            for (Window w : Window.getWindows()) {
                w.dispose();
            }
            new resources.LoginView().setVisible(true);
        });

        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(logoutBtn);

        card.add(detailsPanel, BorderLayout.CENTER);
    }

    private void populateSettingsActions(JPanel card) {
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        JButton changePassBtn = new JButton("Change Password");
        styleButton(changePassBtn, new Color(0, 102, 204));
        changePassBtn.addActionListener(e -> showChangePasswordDialog());
        actionPanel.add(changePassBtn, gbc);

        if (currentUser instanceof Student) {
            gbc.gridy = 1;
            JButton viewRecordsBtn = new JButton("View Academic Records (PDF)");
            styleButton(viewRecordsBtn, new Color(40, 167, 69));

            viewRecordsBtn.addActionListener(e -> {
                new ReportGUI(currentUser).setVisible(true);
            });

            actionPanel.add(viewRecordsBtn, gbc);
        }

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(actionPanel, BorderLayout.NORTH);

        card.add(container, BorderLayout.CENTER);
    }

    // Removed populateAdminPanel as you requested it to be separate in previous turns
    // If you need it back, I can re-add it, but based on logic flow, it is now in StudentManagementView

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new GridLayout(1, 2));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.GRAY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(Color.BLACK);

        row.add(lbl);
        row.add(val);

        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
    }

    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();

        panel.add(new JLabel("Current Password:"));
        panel.add(oldPass);
        panel.add(new JLabel("New Password:"));
        panel.add(newPass);
        panel.add(new JLabel("Confirm New:"));
        panel.add(confirmPass);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String oldP = new String(oldPass.getPassword());
            String newP = new String(newPass.getPassword());
            String confP = new String(confirmPass.getPassword());

            if (currentUser.login(oldP)) {
                if (newP.equals(confP)) {
                    if (newP.length() >= 4) {
                        currentUser.resetPassword(newP);

                        if (userDAO.saveUserCredentials(currentUser)) {
                            JOptionPane.showMessageDialog(this, "Password updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Error saving password to file.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "New password is too short.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "New passwords do not match.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect current password.");
            }
        }
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(250, 40));
    }
}