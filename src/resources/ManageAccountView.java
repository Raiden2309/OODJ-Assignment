package resources;

import domain.User;
import domain.Student;
import service.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ManageAccountView extends JFrame {

    private User currentUser;
    private UserDAO userDAO;

    public ManageAccountView(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();

        setTitle("My Profile - " + currentUser.getUserID());
        setSize(800, 600); // Standard size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);

        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(255, 255, 255, 50));
        backBtn.setForeground(Color.WHITE);
        backBtn.setPreferredSize(new Dimension(80, 30));
        backBtn.addActionListener(e -> {
            dispose();
            new Dashboard(currentUser).setVisible(true);
        });

        JLabel appTitle = new JLabel("My Account Settings");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setForeground(Color.WHITE);

        leftHeader.add(backBtn);
        leftHeader.add(appTitle);
        headerPanel.add(leftHeader, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // --- CONTENT ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // 1. Profile Info
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4; gbc.weighty = 1.0;
        contentPanel.add(createProfilePanel(), gbc);

        // 2. Actions (Change Password, View Records)
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.6;
        contentPanel.add(createActionsPanel(), gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createProfilePanel() {
        JPanel card = createCard("Profile Details");
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(Color.WHITE);

        addDetailRow(details, "User ID", currentUser.getUserID());
        addDetailRow(details, "Email", currentUser.getEmail());
        addDetailRow(details, "Role", currentUser.getRole().getRoleName());

        if (currentUser instanceof Student) {
            Student s = (Student) currentUser;
            addDetailRow(details, "Full Name", s.getFullName());
            addDetailRow(details, "Major", s.getMajor());
            addDetailRow(details, "Year", s.getAcademicYear());
        }

        // Logout at bottom
        details.add(Box.createVerticalGlue());
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, new Color(255, 69, 58));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(150, 40));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        details.add(Box.createVerticalStrut(20));
        details.add(logoutBtn);

        card.add(details, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActionsPanel() {
        JPanel card = createCard("Account Actions");
        JPanel actions = new JPanel(new GridBagLayout());
        actions.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;

        // Change Password
        JButton changePassBtn = new JButton("Change Password");
        styleButton(changePassBtn, new Color(0, 102, 204));
        changePassBtn.addActionListener(e -> showChangePasswordDialog());
        actions.add(changePassBtn, gbc);

        // Student Specific Actions
        if (currentUser instanceof Student) {
            gbc.gridy++;
            JButton viewRecordsBtn = new JButton("View Academic Records (PDF)");
            styleButton(viewRecordsBtn, new Color(40, 167, 69));
            viewRecordsBtn.addActionListener(e -> new ReportGUI(currentUser).setVisible(true));
            actions.add(viewRecordsBtn, gbc);
        }

        // Push components to top
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(actions, BorderLayout.NORTH);

        card.add(wrapper, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(80, 80, 80));
        lbl.setBorder(new EmptyBorder(0, 0, 15, 0));
        card.add(lbl, BorderLayout.NORTH);
        return card;
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(Color.GRAY);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
    }

    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JPasswordField oldP = new JPasswordField();
        JPasswordField newP = new JPasswordField();
        JPasswordField confP = new JPasswordField();
        panel.add(new JLabel("Current:")); panel.add(oldP);
        panel.add(new JLabel("New:")); panel.add(newP);
        panel.add(new JLabel("Confirm:")); panel.add(confP);

        if (JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String old = new String(oldP.getPassword());
            String nw = new String(newP.getPassword());
            String cf = new String(confP.getPassword());

            if (currentUser.login(old)) {
                if (nw.equals(cf) && nw.length() >= 4) {
                    currentUser.resetPassword(nw);
                    if (userDAO.saveUserCredentials(currentUser)) JOptionPane.showMessageDialog(this, "Success!");
                    else JOptionPane.showMessageDialog(this, "Error saving.");
                } else JOptionPane.showMessageDialog(this, "Passwords mismatch or too short.");
            } else JOptionPane.showMessageDialog(this, "Incorrect password.");
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(250, 40));
    }
}