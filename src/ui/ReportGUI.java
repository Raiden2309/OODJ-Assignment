package ui;

import service.NotificationService;
import service.ReportGenerator;
import domain.User;
import domain.Student;
import service.AcademicRecordDAO;
import academic.CourseResult;
import academic.Course;
import service.StudentDAO;
import service.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Collections;
import java.util.List;

public class ReportGUI extends JFrame {

    private JButton generateButton;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JPanel mainPanel;
    private JTextArea previewArea;

    private ReportGenerator reportGen;
    private Student targetStudent;

    // Constructor now expects the specific Student whose report needs generating
    public ReportGUI(Student student) {
        this.targetStudent = student;
        this.reportGen = new ReportGenerator();

        // Ensure student data is fully loaded and CGPA is calculated before setting up the view
        AcademicRecordDAO recordDAO = new AcademicRecordDAO();
        recordDAO.loadRecords(Collections.singletonList(this.targetStudent));
        this.targetStudent.getAcademicProfile().calculateCGPA();

        setTitle("Academic Performance Report Generator");
        setSize(800, 650); // Adjusted size to fit the preview area
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 2. Initialize Components
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(new Color(245, 247, 250));
        add(mainPanel);

        // 3. Setup Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        // Title
        titleLabel = new JLabel(targetStudent.getFullName() + "'s Academic Records");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // --- ACADEMIC RECORD PREVIEW (Always Visible) ---
        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        previewArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane previewScroll = new JScrollPane(previewArea);
        previewScroll.setPreferredSize(new Dimension(650, 350));

        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(previewScroll, gbc);

        // Load the preview text
        loadStudentPreview(this.targetStudent);

        // Button Setup (Always says "Generate Report (PDF)")
        generateButton = new JButton("Generate Report (PDF)");
        generateButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        generateButton.setBackground(new Color(0, 102, 204));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateButton.setPreferredSize(new Dimension(250, 50));

        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(generateButton, gbc);

        statusLabel = new JLabel("Status: Ready to export.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(Color.GRAY);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(statusLabel, gbc);

        generateButton.addActionListener(e -> generateReport());

        setVisible(true);
    }

    // Default constructor should initialize with null or throw error if not used for testing
    public ReportGUI() {
        this(null);
    }

    // This method now runs on instantiation (for student-specific view)
    private void loadStudentPreview(Student student) {
        // Data is already loaded/calculated in the constructor

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
        previewArea.setCaretPosition(0);
    }


    private void generateReport() {
        String targetID = targetStudent.getUserID();

        try {
            statusLabel.setText("Status: Generating report for " + targetID + "...");

            ReportGenerator reportGen = new ReportGenerator(); // Initialize report gen locally if needed
            reportGen.generatePDF(targetID);
            statusLabel.setText("Status: Report generated successfully!");

            JOptionPane.showMessageDialog(this,
                    "PDF Report saved to: data/report/" + targetID + "_Report.pdf",
                    "Success!", JOptionPane.INFORMATION_MESSAGE);

            // Notification Logic (Assuming targetStudent is valid)
            final String fName = targetStudent.getFirstName();
            final String lName = targetStudent.getLastName();
            final String email = targetStudent.getEmail();
            final double cgpa = targetStudent.getAcademicProfile().getCGPA();

            new Thread(() -> {
                try {
                    NotificationService notify = new NotificationService(fName, lName, email);
                    notify.sendAcademicReportEmail(1, cgpa);
                    System.out.println("Report notification sent.");
                } catch (Exception ex) {
                    System.err.println("Notification failed: " + ex.getMessage());
                }
            }).start();

        } catch (Exception e) {
            statusLabel.setText("Status: Error generating report!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Note: Launching without a student will throw NullPointer, used for basic testing only
            new ReportGUI(null).setVisible(true);
        });
    }
}