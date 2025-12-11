package resources;


import domain.User;
import domain.Student;
import domain.AcademicOfficer;
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
    private StudentDAO studentDAO; // Needed for admin view

    public ManageAccountView(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();

        // Window Setup
        setTitle("Account Settings - " + currentUser.getUserID());
        setSize(1000, 700); // Increased size for admin table
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- TOP HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel appTitle = new JLabel("Course Recovery System");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, new Color(255, 69, 58));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        headerPanel.add(appTitle, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- MAIN CONTENT (Grid of Cards) ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // 1. Profile Card (Always Visible)
        JPanel profileCard = createCard("Profile Information");
        populateProfileInfo(profileCard);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // 30% width for profile
        contentPanel.add(profileCard, gbc);

        // 2. Dynamic Right Card (Student Settings OR Admin Management)
        JPanel rightCard;
        String role = currentUser.getRole().getRoleName();

        if ("AcademicOfficer".equalsIgnoreCase(role)) {
            rightCard = createCard("Student Management");
            populateAdminPanel(rightCard);
        } else {
            rightCard = createCard("Account Actions");
            populateSettingsActions(rightCard);
        }

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7; // 70% width for management/settings
        contentPanel.add(rightCard, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout()); // Changed to BorderLayout for flexibility
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

        card.add(detailsPanel, BorderLayout.CENTER);
    }

    // --- STUDENT VIEW COMPONENTS ---
    private void populateSettingsActions(JPanel card) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(Color.WHITE);

        JButton changePassBtn = new JButton("Change Password");
        styleButton(changePassBtn, new Color(0, 102, 204));

        changePassBtn.addActionListener(e -> showChangePasswordDialog());

        actionPanel.add(changePassBtn);
        card.add(actionPanel, BorderLayout.CENTER);
    }

    // --- ADMIN VIEW COMPONENTS ---
    private void populateAdminPanel(JPanel card) {
        // 1. Table Model
        String[] columns = {"ID", "Name", "Major", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // Load data
        List<Student> students = studentDAO.loadAllStudents();
        for (Student s : students) {
            model.addRow(new Object[]{
                    s.getUserID(),
                    s.getFullName(),
                    s.getMajor(),
                    s.isActive() ? "Active" : "Inactive"
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        card.add(scrollPane, BorderLayout.CENTER);

        // 2. Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        JButton activateBtn = new JButton("Activate");
        styleButton(activateBtn, new Color(40, 167, 69)); // Green

        JButton deactivateBtn = new JButton("Deactivate");
        styleButton(deactivateBtn, new Color(220, 53, 69)); // Red

        // Deactivate Logic
        deactivateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) model.getValueAt(row, 0);
                // In a real app, update object & DAO
                // For demonstration, we just update the table UI
                model.setValueAt("Inactive", row, 3);
                JOptionPane.showMessageDialog(this, "User " + id + " deactivated.");
                // TODO: userDAO.updateStatus(id, false);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student.");
            }
        });

        // Activate Logic
        activateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) model.getValueAt(row, 0);
                model.setValueAt("Active", row, 3);
                JOptionPane.showMessageDialog(this, "User " + id + " activated.");
                // TODO: userDAO.updateStatus(id, true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student.");
            }
        });

        btnPanel.add(activateBtn);
        btnPanel.add(deactivateBtn);
        card.add(btnPanel, BorderLayout.SOUTH);
    }

    // --- SHARED HELPER METHODS ---

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
        // btn.setBorderPainted(false); // Kept border for standard look
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
