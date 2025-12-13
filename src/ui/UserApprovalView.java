package ui;

import domain.User;
import domain.Student;
import domain.SystemRole;
import domain.AcademicOfficer;
import domain.CourseAdministrator;
import service.UserDAO;
import service.StudentDAO;
import data_access.DataAccess;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UserApprovalView extends JFrame {

    private User loggedInUser;
    private UserDAO userDAO;
    private StudentDAO studentDAO;
    private JTable table;
    private DefaultTableModel model;
    private Dashboard dashboard;
    private JComboBox<String> majorDropdown;
    private List<String> allMajors;
    private List<String> originalMajorItems;

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
        this.allMajors = new ArrayList<>(originalMajorItems);

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
        String[] columns = {"Temp ID", "Name", "Email", "Role"};
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
        JButton refreshBtn = createRoundedButton("Refresh", ACCENT_COLOR, Color.WHITE);

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

    private List<String> loadUniqueMajors() {
        DataAccess da = new DataAccess();
        List<String[]> students = da.getStudents();
        Set<String> majorSet = new HashSet<>();

        for (String[] s : students) {
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

        UserDAO freshUserDAO = new UserDAO();
        List<User> pendingUsers = freshUserDAO.loadAllUsers().stream()
                .filter(u -> u.getRole() != null && ROLE_PENDING.equalsIgnoreCase(u.getRole().getRoleName()))
                .collect(Collectors.toList());

        StudentDAO freshStudentDAO = new StudentDAO();
        List<Student> allStudents = freshStudentDAO.loadAllStudents();

        for (User u : pendingUsers) {
            String name = "Unknown";

            Student matchedStudent = allStudents.stream()
                    .filter(s -> s.getUserID().equals(u.getUserID()))
                    .findFirst()
                    .orElse(null);

            if (matchedStudent != null) {
                name = matchedStudent.getFullName();
            } else {
                if (u instanceof Student) {
                    String f = ((Student)u).getFirstName();
                    String l = ((Student)u).getLastName();
                    if (!"Pending".equals(f) || !"User".equals(l)) name = f + " " + l;
                }
            }

            model.addRow(new Object[]{
                    u.getUserID(),
                    name,
                    u.getEmail(),
                    u.getRole().getRoleName()
            });
        }
    }

    private void showAssignmentDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a pending request.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tempId = (String) model.getValueAt(selectedRow, 0);
        String currentName = (String) model.getValueAt(selectedRow, 1);

        UserDAO freshUserDAO = new UserDAO();
        User pendingUser = freshUserDAO.loadAllUsers().stream()
                .filter(u -> u.getUserID().equals(tempId))
                .findFirst().orElse(null);

        if (pendingUser == null) return;

        StudentDAO freshStudentDAO = new StudentDAO();
        Student pendingStudentDetail = freshStudentDAO.loadAllStudents().stream()
                .filter(s -> s.getUserID().equals(tempId))
                .findFirst().orElse(null);

        // --- Major Dropdown Setup ---
        majorDropdown = new JComboBox<>(this.originalMajorItems.toArray(new String[0]));
        majorDropdown.setEditable(true);
        majorDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField editor = (JTextField) majorDropdown.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterMajorDropdown(editor.getText());
            }
        });

        // --- UI Components ---
        JTextField newIdField = new JTextField(tempId);
        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"Student", "Academic Officer", "Course Administrator"});

        String initialMajor = (pendingStudentDetail != null && !pendingStudentDetail.getMajor().equals("UNASSIGNED")) ? pendingStudentDetail.getMajor() : "";
        if (!initialMajor.isEmpty()) {
            majorDropdown.setSelectedItem(initialMajor);
        } else {
            majorDropdown.setSelectedItem(null);
        }

        // UI Panel
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("Name:")); panel.add(new JLabel(currentName)); // Show Name
        panel.add(new JLabel("Email:")); panel.add(new JLabel(pendingUser.getEmail()));
        panel.add(new JLabel("New User ID:")); panel.add(newIdField);
        panel.add(new JLabel("Assign Role:")); panel.add(roleDropdown);
        panel.add(new JLabel("Major (Students Only):")); panel.add(majorDropdown);

        majorDropdown.setEnabled(false);

        roleDropdown.addActionListener(e -> {
            String selectedRole = (String) roleDropdown.getSelectedItem();
            String prefix = getPrefixForRole(selectedRole);

            if (tempId.startsWith("U")) {
                newIdField.setText(prefix + generateNextSequentialId(prefix));
            } else {
                newIdField.setEnabled(false);
            }
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

            String major = majorDropdown.isEditable() ?
                    (String) majorDropdown.getEditor().getItem() :
                    (String) majorDropdown.getSelectedItem();

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

            User userToActivate = (pendingStudentDetail != null) ? pendingStudentDetail : pendingUser;
            assignRoleAndActivate(userToActivate, finalId, newRoleName, major);
        }
    }

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

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(filteredList.toArray(new String[0]));
        majorDropdown.setModel(model);
        JTextField editor = (JTextField) majorDropdown.getEditor().getEditorComponent();
        editor.setText(text);

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
        UserDAO freshDAO = new UserDAO();
        List<User> all = freshDAO.loadAllUsers();
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
        // NOTE: PREFIX is missing here, assuming it's a constant defined elsewhere
        // Using prefix variable for robustness
        return String.format(prefix + "%03d", maxNum + 1);
    }

    private void assignRoleAndActivate(User oldUser, String newId, String newRoleName, String major) {
        SystemRole newRole = new SystemRole(newRoleName, new ArrayList<>());

        String passHash = oldUser.getPassword();
        String email = oldUser.getEmail();

        String firstName = "";
        String lastName = "";
        if (oldUser instanceof Student) {
            firstName = ((Student)oldUser).getFirstName();
            lastName = ((Student)oldUser).getLastName();
        }

        User finalUser;

        try {
            if ("Student".equals(newRoleName)) {
                finalUser = new Student(newId, passHash, newRole, firstName, lastName, major, "Year 1", email);
                studentDAO.saveStudent((Student) finalUser);

            } else if ("Academic Officer".equals(newRoleName)) {
                finalUser = new AcademicOfficer(newId, passHash, newRole, firstName, lastName, email);
            } else if ("Course Administrator".equals(newRoleName)) {
                finalUser = new CourseAdministrator(newId, passHash, newRole, firstName, lastName, email);
            } else {
                throw new IllegalArgumentException("Invalid final role.");
            }

            finalUser.setEmail(email);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during user object creation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        finalUser.setActive(true);

        UserDAO freshDAO = new UserDAO();

        // FIX 1: Use the explicit delete method
        if (freshDAO.deleteUser(oldUser.getUserID())) {
            // FIX 2: Manually add the new user back, as deleteUser only removes the old one
            if (freshDAO.saveUserCredentials(finalUser)) {
                JOptionPane.showMessageDialog(this, "User " + newId + " approved and activated successfully as " + newRoleName + ".");
                loadPendingUsers();
                if (dashboard != null) {
                    dashboard.refreshStaffContent();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: Approved user failed to save to credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error: Could not remove old pending credentials.", "Error", JOptionPane.ERROR_MESSAGE);
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
                "Are you sure you want to REJECT and permanently remove request " + tempId + "? This action is irreversible.",
                "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserDAO freshUserDAO = new UserDAO();

            // --- 1. REMOVE CREDENTIALS (user_credentials.csv) ---
            // FIX 3: Use the explicit delete method
            if (freshUserDAO.deleteUser(tempId)) {

                // --- 2. REMOVE STUDENT DETAILS (student_information.csv) ---
                StudentDAO freshStudentDAO = new StudentDAO();
                boolean studentRemoved = freshStudentDAO.removeStudent(tempId);

                if (studentRemoved) {
                    JOptionPane.showMessageDialog(this, "Request " + tempId + " rejected and fully removed.", "Removal Successful", JOptionPane.INFORMATION_MESSAGE);
                    loadPendingUsers();
                    if (dashboard != null) {
                        dashboard.refreshStaffContent();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Warning: Credentials removed, but student details (name/major) cleanup failed.", "Partial Removal", JOptionPane.WARNING_MESSAGE);
                    loadPendingUsers();
                }
                return;
            }
            JOptionPane.showMessageDialog(this, "Error removing user from system credentials.", "Error", JOptionPane.ERROR_MESSAGE);
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