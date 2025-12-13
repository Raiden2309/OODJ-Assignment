package ui;

import domain.User;
import domain.Student;
import service.StudentDAO;
import service.UserDAO;
import service.AcademicRecordDAO;
import service.EnrollmentDAO;
import domain.Enrollment;
import academic.AcademicProfile; // Required for loadStudentData

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Optional;

public class StudentManagementView extends JFrame {

    private User loggedInUser;
    private StudentDAO studentDAO;
    private UserDAO userDAO;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> majorFilter;
    private TableRowSorter<DefaultTableModel> sorter;

    public StudentManagementView(User user) {
        this.loggedInUser = user;
        this.studentDAO = new StudentDAO();
        this.userDAO = new UserDAO();

        setTitle("Student Account Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Manage Student Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        // Search & Filter Panel (Center of Header)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        searchPanel.setOpaque(false);

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateFilters();
            }
        });

        JLabel filterLbl = new JLabel("Filter by Major:");
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        majorFilter = new JComboBox<>();
        majorFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        majorFilter.setPreferredSize(new Dimension(180, 30));
        majorFilter.addItem("All Majors");
        majorFilter.addActionListener(e -> updateFilters());

        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(filterLbl);
        searchPanel.add(majorFilter);

        // Back Button (Removed as per previous request)
        // Note: Staff should use the X or the Dashboard link in the menu to navigate.

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // --- TABLE ---
        String[] columns = {"ID", "Name", "Major", "Email", "CGPA", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String studentID = (String) table.getValueAt(table.convertRowIndexToModel(row), 0);
                        showStudentProfile(studentID);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- ACTION BUTTONS ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        actionPanel.setBackground(Color.WHITE);

        JButton viewProfileBtn = createRoundedButton("View Profile", new Color(0, 102, 204), Color.WHITE);
        JButton activateBtn = createRoundedButton("Activate Account", new Color(40, 167, 69), Color.WHITE);
        JButton deactivateBtn = createRoundedButton("Deactivate Account", new Color(220, 53, 69), Color.WHITE);

        actionPanel.add(viewProfileBtn);
        actionPanel.add(activateBtn);
        actionPanel.add(deactivateBtn);
        add(actionPanel, BorderLayout.SOUTH);

        // Load Data
        loadStudentData();

        // Listeners
        activateBtn.addActionListener(e -> updateStatus(true));
        deactivateBtn.addActionListener(e -> updateStatus(false));
        viewProfileBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String id = (String) model.getValueAt(table.convertRowIndexToModel(row), 0);
                showStudentProfile(id);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student.");
            }
        });

        setVisible(true);
    }

    private void updateFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // 1. Text Search Filter (ID or Name)
        String text = searchField.getText().trim();
        if (text.length() > 0) {
            filters.add(RowFilter.regexFilter("(?i)" + text, 0, 1));
        }

        // 2. Major Filter
        String selectedMajor = (String) majorFilter.getSelectedItem();
        if (selectedMajor != null && !selectedMajor.equals("All Majors")) {
            filters.add(RowFilter.regexFilter("^" + selectedMajor + "$", 2));
        }

        // Apply
        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void loadStudentData() {
        model.setRowCount(0);
        List<Student> students = studentDAO.loadAllStudents();

        // Load academic records to calculate CGPA
        AcademicRecordDAO recordDAO = new AcademicRecordDAO();
        recordDAO.loadRecords(students);

        // NEW: Collect unique majors for filter
        Set<String> majors = new HashSet<>();

        for (Student s : students) {
            s.getAcademicProfile().calculateCGPA();

            // Note: We use s.isActive() directly because StudentDAO now populates it correctly
            model.addRow(new Object[]{
                    s.getUserID(),
                    s.getFullName(),
                    s.getMajor(),
                    s.getEmail(),
                    String.format("%.2f", s.getAcademicProfile().getCGPA()), // CGPA Column
                    s.isActive() ? "Active" : "Inactive" // Use status from Student object
            });

            // Add major to set
            if (s.getMajor() != null && !s.getMajor().isEmpty()) {
                majors.add(s.getMajor());
            }
        }

        // Populate Filter Dropdown
        List<String> sortedMajors = new ArrayList<>(majors);
        Collections.sort(sortedMajors);

        // Avoid duplicate items if reloading
        if (majorFilter.getItemCount() <= 1) {
            for (String m : sortedMajors) {
                majorFilter.addItem(m);
            }
        }
    }

    private void updateStatus(boolean isActive) {
        int selectedRowView = table.getSelectedRow();
        if (selectedRowView == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRowView);
        String studentID = (String) model.getValueAt(modelRow, 0);
        String action = isActive ? "Activate" : "Deactivate";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + action.toLowerCase() + " user " + studentID + "? This affects login access.",
                "Confirm " + action, JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            UserDAO freshDAO = new UserDAO();
            User target = freshDAO.loadAllUsers().stream()
                    .filter(u -> u.getUserID().equals(studentID))
                    .findFirst().orElse(null);

            if(target != null) {
                target.setActive(isActive);

                if(freshDAO.saveUserCredentials(target)) {
                    JOptionPane.showMessageDialog(this, "Success! User " + studentID + " is now " + (isActive ? "Active" : "Inactive") + ". Credentials updated.");

                    // FIX: Manually update the table model row to reflect changes instantly without full reload
                    // This bypasses potential file read race conditions for the immediate UI feedback.
                    model.setValueAt(isActive ? "Active" : "Inactive", modelRow, 5); // Status is column 5

                } else {
                    JOptionPane.showMessageDialog(this, "Error saving status to credentials file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: User ID not found in system credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- SHOW PROFILE DIALOG ---
    private void showStudentProfile(String studentID) {
        StudentDAO sDao = new StudentDAO();
        List<Student> all = sDao.loadAllStudents();
        Student s = all.stream().filter(st -> st.getUserID().equals(studentID)).findFirst().orElse(null);

        if (s == null) return;

        AcademicRecordDAO rDao = new AcademicRecordDAO();
        rDao.loadRecords(all);

        s.getAcademicProfile().calculateCGPA();

        EnrollmentDAO eDao = new EnrollmentDAO();
        List<Enrollment> enrollments = eDao.loadEnrollments();
        List<Enrollment> studentEnrollments = enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentID))
                .collect(Collectors.toList());

        // FIX: Force reload of UserDAO to get fresh status for the dialog
        UserDAO freshDAO = new UserDAO();
        User userCreds = freshDAO.loadAllUsers().stream()
                .filter(u -> u.getUserID().equals(studentID))
                .findFirst().orElse(null);

        boolean isActive = (userCreds != null) ? userCreds.isActive() : s.isActive();

        JDialog dialog = new JDialog(this, "Student Profile: " + s.getFullName(), true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        addProfileRow(content, "ID:", s.getUserID());
        addProfileRow(content, "Name:", s.getFullName());
        addProfileRow(content, "Email:", s.getEmail());
        addProfileRow(content, "Major:", s.getMajor());
        addProfileRow(content, "Year:", s.getAcademicYear());
        addProfileRow(content, "Status:", isActive ? "Active" : "Inactive");

        content.add(Box.createVerticalStrut(20));
        JLabel acadHeader = new JLabel("Academic Info");
        acadHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        content.add(acadHeader);

        addProfileRow(content, "CGPA:", String.format("%.2f", s.getAcademicProfile().getCGPA()));
        addProfileRow(content, "Failed Courses:", String.valueOf(s.getAcademicProfile().getTotalFailedCourses()));

        content.add(Box.createVerticalStrut(20));
        JLabel enrolHeader = new JLabel("Course Recovery Plans");
        enrolHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        content.add(enrolHeader);

        if (studentEnrollments.isEmpty()) {
            addProfileRow(content, "Plan:", "None");
        } else {
            for (Enrollment e : studentEnrollments) {
                String plan = (e.getPlan() != null) ? e.getPlan().getPlanID() : "Unknown";
                addProfileRow(content, "Plan ID:", plan + " (" + e.getStatus() + ")");
            }
        }

        JButton closeBtn = createRoundedButton("Close", new Color(108, 117, 125), Color.WHITE);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(closeBtn);

        dialog.add(new JScrollPane(content), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addProfileRow(JPanel p, String key, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(500, 30));
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        row.add(k, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        p.add(row);
        p.add(Box.createVerticalStrut(5));
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

        btn.setPreferredSize(new Dimension(180, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
}