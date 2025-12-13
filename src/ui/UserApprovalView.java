package ui;

import domain.User;
import domain.Student;
import domain.SystemRole;
import domain.AcademicOfficer;
import domain.CourseAdministrator;
import service.UserDAO;
import service.StudentDAO; // Needed for saving new student profile if role is 'Student'
import data_access.DataAccess; // Needed to potentially load major list

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Collections;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter; // NEW
import java.awt.event.KeyEvent; // NEW

public class UserApprovalView extends JFrame {

    private User loggedInUser;
    private UserDAO userDAO;
    private StudentDAO studentDAO;
    private JTable table;
    private DefaultTableModel model;
    private Dashboard dashboard;
    private JComboBox<String> majorDropdown; // Editable Major Dropdown
    private List<String> allMajors; // List to store all unique majors
    private List<String> originalMajorItems; // Store the original full list for filtering

    // Status constants
    private final String ROLE_PENDING = "Pending";
    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private final Color ERROR_COLOR = new Color(220, 53, 69);

    public UserApprovalView(User user, Dashboard dashboard) {
        this.loggedInUser = user;
        this.dashboard = dashboard;
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();
        this.originalMajorItems = loadUniqueMajors();
        this.allMajors = new ArrayList<>(originalMajorItems); // Working copy for initial dropdown


        setTitle("User Account Approval Queue (" + ROLE_PENDING + ")");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("Pending User Requests");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Temp ID", "Name (First)", "Email", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        actionPanel.setBackground(Color.WHITE);

        JButton viewDetailsBtn = createRoundedButton("View Details / Assign Role", ACCENT_COLOR, Color.WHITE);
        JButton rejectBtn = createRoundedButton("Reject Request", ERROR_COLOR, Color.WHITE);
        JButton refreshBtn = createRoundedButton("Refresh", new Color(108, 117, 125), Color.WHITE);

        actionPanel.add(viewDetailsBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(refreshBtn);
        add(actionPanel, BorderLayout.SOUTH);

        // Listeners
        refreshBtn.addActionListener(e -> loadPendingUsers());
        viewDetailsBtn.addActionListener(e -> showAssignmentDialog());
        rejectBtn.addActionListener(e -> removeSelectedUser());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showAssignmentDialog();
                }
            }
        });

        loadPendingUsers();
        setVisible(true);
    }

    // Load unique majors from student records for the dropdown
    private List<String> loadUniqueMajors() {
        // We rely on DataAccess to have the most comprehensive student list structure
        DataAccess da = new DataAccess();
        // Assuming DataAccess.getStudents() returns List<String[]> where Major is index 3
        List<String[]> students = da.getStudents();
        Set<String> majorSet = new HashSet<>();

        // The major is at index 3 in the String[] array returned by getStudents()
        for (String[] s : students) {
            // Index 3 is Major (StudentID, FirstName, LastName, Major, Year, Email)
            if (s.length > 3 && s[3] != null && !s[3].trim().isEmpty()) {
                majorSet.add(s[3].trim());
            }
        }

        List<String> majors = new ArrayList<>(majorSet);
        Collections.sort(majors);
        return majors;
    }


    private void loadPendingUsers() {
        model.setRowCount(0);
        // Load all users and filter by Role="Pending"
        List<User> pending = userDAO.loadAllUsers().stream()
                .filter(u -> u.getRole() != null && ROLE_PENDING.equalsIgnoreCase(u.getRole().getRoleName()))
                .collect(Collectors.toList());

        for (User u : pending) {
            // Note: Since RegisterView uses Student as a placeholder, we cast for name/email.
            String name = (u instanceof Student) ? ((Student) u).getFirstName() : "N/A";

            model.addRow(new Object[]{
                    u.getUserID(),
                    name,
                    u.getEmail(),
                    u.getRole().getRoleName()
            });
        }

        if (pending.isEmpty()) {
            System.out.println("User Approval Queue is empty.");
        }
    }

    private void showAssignmentDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a pending request.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tempId = (String) model.getValueAt(selectedRow, 0);

        User pendingUser = userDAO.loadAllUsers().stream()
                .filter(u -> u.getUserID().equals(tempId))
                .findFirst().orElse(null);

        if (pendingUser == null) return;

        // --- Major Dropdown Setup (Searchable/Editable) ---
        majorDropdown = new JComboBox<>(this.originalMajorItems.toArray(new String[0]));
        majorDropdown.setEditable(true);
        majorDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Add KeyListener to the editor component for filtering
        JTextField editor = (JTextField) majorDropdown.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Perform dynamic filtering based on typed text
                filterMajorDropdown(editor.getText());
            }
        });

        // --- Existing UI Components ---
        JTextField newIdField = new JTextField(tempId);
        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"Student", "Academic Officer", "Course Administrator"});

        String initialMajor = ((Student) pendingUser).getMajor().equals("UNASSIGNED") ? "" : ((Student) pendingUser).getMajor();
        if (!initialMajor.isEmpty()) {
            majorDropdown.setSelectedItem(initialMajor);
        } else {
            majorDropdown.setSelectedItem(null);
        }

        // UI Panel
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Current Email:")); panel.add(new JLabel(pendingUser.getEmail()));
        panel.add(new JLabel("New User ID:")); panel.add(newIdField);
        panel.add(new JLabel("Assign Role:")); panel.add(roleDropdown);
        panel.add(new JLabel("Major (Students Only):")); panel.add(majorDropdown);

        // Setup initial state and listeners
        majorDropdown.setEnabled(false); // Disable by default

        roleDropdown.addActionListener(e -> {
            String selectedRole = (String) roleDropdown.getSelectedItem();
            String prefix = getPrefixForRole(selectedRole);

            if (tempId.startsWith("U")) {
                newIdField.setText(prefix + generateNextSequentialId(prefix));
            } else {
                newIdField.setEnabled(false);
            }
            // Enable major dropdown ONLY for Students
            majorDropdown.setEnabled(selectedRole.equals("Student"));
        });

        if (tempId.startsWith("U")) {
            roleDropdown.setSelectedItem("Student");
            newIdField.setText("S" + generateNextSequentialId("S"));
        } else {
            newIdField.setEnabled(false);
        }

        int option = JOptionPane.showConfirmDialog(this, panel, "Approve and Assign Role", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newRoleName = (String) roleDropdown.getSelectedItem();
            String finalId = newIdField.getText().trim();

            // Get selected/typed major from the dropdown editor
            String major = majorDropdown.isEditable() ?
                    (String) majorDropdown.getEditor().getItem() :
                    (String) majorDropdown.getSelectedItem();

            // Clean up major if not a student
            if (!"Student".equals(newRoleName)) {
                major = "";
            } else if (major == null || major.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Major is required for Student role.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            if (finalId.isEmpty() || !finalId.startsWith(getPrefixForRole(newRoleName))) {
                JOptionPane.showMessageDialog(this, "New ID must start with the correct prefix: " + getPrefixForRole(newRoleName));
                return;
            }

            // Perform Role Assignment and Activation
            assignRoleAndActivate(pendingUser, finalId, newRoleName, major);
        }
    }

    // NEW: Filtering logic for the Major JComboBox
    private void filterMajorDropdown(String text) {
        String filterText = text.toLowerCase().trim();
        List<String> filteredList = new ArrayList<>();

        if (filterText.isEmpty()) {
            filteredList.addAll(originalMajorItems);
        } else {
            for (String major : originalMajorItems) {
                if (major.toLowerCase().contains(filterText)) {
                    filteredList.add(major);
                }
            }
        }

        // Update the combobox model
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(filteredList.toArray(new String[0]));
        majorDropdown.setModel(model);

        // Set the editor text back to what the user typed (crucial for maintaining search state)
        JTextField editor = (JTextField) majorDropdown.getEditor().getEditorComponent();
        editor.setText(text);

        // Show the dropdown popup if results exist
        if (!filteredList.isEmpty()) {
            majorDropdown.setPopupVisible(true);
        } else {
            majorDropdown.setPopupVisible(false);
        }
    }


    private String getPrefixForRole(String roleName) {
        if ("Student".equals(roleName)) return "S";
        if ("Academic Officer".equals(roleName)) return "AO";
        if ("Course Administrator".equals(roleName)) return "CA";
        return "U";
    }

    private String generateNextSequentialId(String prefix) {
        List<User> all = userDAO.loadAllUsers();
        int maxNum = 0;

        for (User u : all) {
            String id = u.getUserID();
            if (id.startsWith(prefix) && id.length() > 1) {
                try {
                    int num = Integer.parseInt(id.substring(prefix.length()));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException e) {}
            }
        }
        return String.format("%03d", maxNum + 1);
    }

    private void assignRoleAndActivate(User oldUser, String newId, String newRoleName, String major) {
        // 1. Determine new concrete class based on newRoleName
        SystemRole newRole = new SystemRole(newRoleName, new ArrayList<>());

        // Get generic data saved on the placeholder user
        String passHash = oldUser.getPassword();
        String email = oldUser.getEmail();
        String firstName = ((Student)oldUser).getFirstName();
        String lastName = ((Student)oldUser).getLastName();

        User finalUser;

        try {
            // Instantiate the correct CONCRETE class
            if ("Student".equals(newRoleName)) {
                // Student constructor requires 8 arguments
                finalUser = new Student(newId, passHash, newRole, firstName, lastName, major, "Year 1", email);
                // Save profile details to student_information.csv
                studentDAO.saveStudent((Student) finalUser);

            } else if ("Academic Officer".equals(newRoleName)) {
                // AO/CA constructor requires 6 arguments
                finalUser = new AcademicOfficer(newId, passHash, newRole, firstName, lastName, email);
            } else if ("Course Administrator".equals(newRoleName)) {
                // AO/CA constructor requires 6 arguments
                finalUser = new CourseAdministrator(newId, passHash, newRole, firstName, lastName, email);
            } else {
                throw new IllegalArgumentException("Invalid final role.");
            }

            // Manually ensure email is set for login consistency (Safety against concrete constructor issues)
            finalUser.setEmail(email);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during user object creation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Activate and Save Credentials
        finalUser.setActive(true);

        // CRITICAL STEP: Manually remove old placeholder entry from DAO cache
        List<User> allUsersCache = userDAO.loadAllUsers();
        allUsersCache.removeIf(u -> u.getUserID().equals(oldUser.getUserID()));

        // Save the new, active user with the correct ID/Role.
        if (userDAO.saveUserCredentials(finalUser)) {
            JOptionPane.showMessageDialog(this, "User " + newId + " approved and activated successfully as " + newRoleName + ".");
            loadPendingUsers(); // Refresh the list
            if (dashboard != null) {
                dashboard.refreshStaffContent();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error saving final credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a pending request to reject.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tempId = (String) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to REJECT and permanently remove request " + tempId + "?",
                "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Find and remove the user from the DAO cache
            List<User> all = userDAO.loadAllUsers();
            boolean removed = all.removeIf(u -> u.getUserID().equals(tempId));

            if (removed) {
                // Rewrite the credentials file without the rejected user
                if (userDAO.saveUserCredentials(null)) {
                    JOptionPane.showMessageDialog(this, "Request " + tempId + " rejected and removed.");
                    loadPendingUsers();
                    if (dashboard != null) {
                        dashboard.refreshStaffContent();
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Error removing user from system.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int shadowGap = 3;
                int arcSize = 30;
                int width = getWidth() - shadowGap;
                int height = getHeight() - shadowGap;

                g2.setColor(new Color(200, 200, 200));
                g2.fillRoundRect(shadowGap, shadowGap, width, height, arcSize, arcSize);

                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);

                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (width - fm.stringWidth(getText())) / 2;
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(250, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
}