package resources;

import service.ReportGenerator;
import domain.User;
import domain.Student;
import service.StudentDAO;
import service.AcademicRecordDAO;
import academic.CourseResult;
import academic.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class ReportGUI extends JFrame {

    private JComboBox<String> studentDropdown;
    private JButton generateButton;
    private JLabel titleLabel;
    private JLabel selectLabel;
    private JLabel statusLabel;
    private JPanel mainPanel;

    // New components for Student View
    private JTextArea previewArea;
    private JPanel studentDetailsPanel;

    private ReportGenerator reportGen;
    private User loggedInUser;
    private static final String STUDENT_FILE = "data/student_information.csv";

    // Constructor accepts User to determine view mode
    public ReportGUI(User user) {
        this.loggedInUser = user;

        setTitle("Academic Performance Report Generator");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 2. Initialize Components
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        mainPanel.setBackground(new Color(245, 247, 250));

        titleLabel = new JLabel("Generate Student Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));

        selectLabel = new JLabel("Select Student:");
        selectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        studentDropdown = new JComboBox<>();
        studentDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentDropdown.setPreferredSize(new Dimension(300, 40));
        studentDropdown.addItem("-- Select a student --");

        generateButton = new JButton("Generate Report (PDF)");
        generateButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        generateButton.setBackground(new Color(0, 102, 204));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateButton.setPreferredSize(new Dimension(250, 45));

        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(Color.GRAY);

        // Preview Area for Student View
        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        previewArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane previewScroll = new JScrollPane(previewArea);
        previewScroll.setPreferredSize(new Dimension(600, 300)); // Larger preview

        // 3. Layout Components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title
        gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Logic split based on Role
        if (loggedInUser instanceof Student) {
            // --- STUDENT VIEW (Read Only Records + Generate) ---
            titleLabel.setText("My Academic Record");

            gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 1.0; // Fill space
            gbc.fill = GridBagConstraints.BOTH;
            mainPanel.add(previewScroll, gbc);

            // Reset weights for buttons
            gbc.weightx = 0; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.NONE;

            loadStudentPreview((Student) loggedInUser);
        } else {
            // --- STAFF VIEW (Dropdown Selection) ---
            gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
            mainPanel.add(selectLabel, gbc);

            gbc.gridy = 2; gbc.gridwidth = 2;
            mainPanel.add(studentDropdown, gbc);

            loadStudents(); // Load list for staff
        }

        // Button (Shared)
        gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(generateButton, gbc);

        // Status (Shared)
        gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(statusLabel, gbc);

        setContentPane(mainPanel);
        reportGen = new ReportGenerator();

        generateButton.addActionListener(e -> generateReport());
    }

    // Default constructor for backward compatibility
    public ReportGUI() {
        this(null);
    }

    private void loadStudentPreview(Student student) {
        AcademicRecordDAO recordDAO = new AcademicRecordDAO();
        if (student.getAcademicProfile().getCourseResults().isEmpty()) {
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Student: ").append(student.getFullName()).append(" (").append(student.getUserID()).append(")\n");
        sb.append("Major:   ").append(student.getMajor()).append("\n");
        sb.append("Year:    ").append(student.getAcademicYear()).append("\n\n");

        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("%-10s %-35s %-5s\n", "Code", "Course Name", "Grade"));
        sb.append("------------------------------------------------------------\n");

        for (CourseResult res : student.getAcademicProfile().getCourseResults()) {
            Course c = res.getCourse();
            sb.append(String.format("%-10s %-35s %-5s\n",
                    c.getCourseId(),
                    c.getName(),
                    res.getGrade()));
        }

        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("Current CGPA:   %.2f\n", student.getAcademicProfile().getCGPA()));
        sb.append("Failed Courses: ").append(student.getAcademicProfile().getTotalFailedCourses());

        previewArea.setText(sb.toString());
        // Scroll to top
        previewArea.setCaretPosition(0);
    }

    // LOAD STUDENTS METHOD from CSV files (For Staff)
    private void loadStudents() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 3) continue;

                String studentID = data[0];
                String firstName = data[1];
                String lastName = data[2];
                String displayText = studentID + " - " + firstName + " " + lastName;

                studentDropdown.addItem(displayText);
            }
            statusLabel.setText("Status: Loaded students for selection");

        } catch (IOException e) {
            statusLabel.setText("Status: Error reading student file!");
        }
    }

    private void generateReport() {
        String targetID;

        if (loggedInUser instanceof Student) {
            // Student generates their own report
            targetID = loggedInUser.getUserID();
        } else {
            // Staff generates selected student report
            if (studentDropdown.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a student first!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String selectedItem = (String) studentDropdown.getSelectedItem();
            targetID = selectedItem.split(" - ")[0];
        }

        try {
            statusLabel.setText("Status: Generating report for " + targetID + "...");
            reportGen.generatePDF(targetID);
            statusLabel.setText("Status: Report generated successfully!");

            JOptionPane.showMessageDialog(this,
                    "PDF Report saved to: data/report/" + targetID + "_Report.pdf",
                    "Success!", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            statusLabel.setText("Status: Error generating report!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ReportGUI(null).setVisible(true);
        });
    }
}