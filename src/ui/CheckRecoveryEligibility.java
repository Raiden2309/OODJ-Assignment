package ui;

import data_access.DataAccess;
import service.StudentDAO;
import service.AcademicRecordDAO;
import academic.EligibilityCheck;
import domain.Student;
import domain.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class CheckRecoveryEligibility extends JFrame {

    private JPanel mainPanel;
    private JComboBox<String> idCombobox;
    private JButton btnBack;

    // Dynamic Labels
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel majorLabel;
    private JLabel yearLabel;

    // Result Labels
    private JLabel eligibilityLabel;
    private JLabel cgpaLabel;
    private JLabel failedCountLabel;

    // Enrollment Button
    private JButton btnEnrollmentProceed;

    // Styling Constants
    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color ELIGIBLE_COLOR = new Color(40, 167, 69);
    private final Color INELIGIBLE_COLOR = new Color(220, 53, 69);
    private final Color MUTE_COLOR = new Color(108, 117, 125);
    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // Backend Access
    DataAccess data = new DataAccess();
    List<String[]> students = data.getStudents();

    // Store logged in user if available
    private User loggedInUser;

    // Store the full list of formatted student strings
    private List<String> allStudentItems;

    public CheckRecoveryEligibility() {
        this(null);
    }

    public CheckRecoveryEligibility(User user)
    {
        this.loggedInUser = user;

        // 1. WINDOW SETUP
        setTitle("CRS - Student Eligibility Check");
        setSize(800, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 2. MAIN LAYOUT
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // --- TOP SECTION: Header & Search ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.setBackground(BG_COLOR);

        JLabel titleLabel = new JLabel("Eligibility Verification");
        titleLabel.setFont(HEADER_FONT);
        topPanel.add(titleLabel);

        // Search Bar Setup
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(BG_COLOR);
        searchPanel.add(new JLabel("Search Student:"), BorderLayout.WEST);

        idCombobox = new JComboBox<>();
        idCombobox.setEditable(true);
        idCombobox.setFont(MAIN_FONT);
        searchPanel.add(idCombobox, BorderLayout.CENTER);

        topPanel.add(searchPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- CENTER SECTION: Details Form ---
        JPanel detailsPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Basic Info Rows
        addDetailRow(detailsPanel, "First Name:", firstNameLabel = new JLabel("-"));
        addDetailRow(detailsPanel, "Last Name:", lastNameLabel = new JLabel("-"));
        addDetailRow(detailsPanel, "Major:", majorLabel = new JLabel("-"));
        addDetailRow(detailsPanel, "Year:", yearLabel = new JLabel("-"));

        // Result Rows (CGPA, Failed Count)
        addDetailRow(detailsPanel, "Current CGPA:", cgpaLabel = new JLabel("-"));
        addDetailRow(detailsPanel, "Failed Courses:", failedCountLabel = new JLabel("-"));

        // Eligibility Result Row
        JLabel eligTitle = new JLabel("Final Eligibility:");
        eligTitle.setFont(BOLD_FONT);
        detailsPanel.add(eligTitle);

        eligibilityLabel = new JLabel("Status: Unknown");
        eligibilityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailsPanel.add(eligibilityLabel);

        mainPanel.add(detailsPanel, BorderLayout.CENTER);

        // --- BOTTOM SECTION: Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(BG_COLOR);

        // Back Button
        btnBack = createRoundedButton("Back to Dashboard", MUTE_COLOR, Color.WHITE);

        btnEnrollmentProceed = createRoundedButton("Enroll Student", ELIGIBLE_COLOR, Color.WHITE);
        btnEnrollmentProceed.setEnabled(false);

        buttonPanel.add(btnBack);
        buttonPanel.add(btnEnrollmentProceed);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);

        // 4. LOGIC INITIALIZATION
        addComboboxItems();
        setupListeners();

        if (loggedInUser instanceof Student) {
            setupStudentView((Student) loggedInUser);
        }
    }

    // Helper to create consistent form rows
    private void addDetailRow(JPanel panel, String title, JLabel valueLabel) {
        JLabel label = new JLabel(title);
        label.setFont(BOLD_FONT);
        label.setForeground(Color.DARK_GRAY);

        valueLabel.setFont(MAIN_FONT);

        panel.add(label);
        panel.add(valueLabel);
    }

    // Helper for Rounded Button with Shadow & Hover (Mirroring ManageAccountView)
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

                // Draw Soft Shadow
                g2.setColor(new Color(200, 200, 200));
                g2.fillRoundRect(shadowGap, shadowGap, width, height, arcSize, arcSize);

                // Hover/Press Effect
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                // Draw Button Body
                g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);

                // Paint Text
                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (width - fm.stringWidth(getText())) / 2;
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(180, 45)); // Slightly bigger for rounding effect
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    // FIX: Filtering logic now applies to the actual Combobox model
    private void filterCombobox(String text) {
        // Get the current list of items in the combobox model
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) idCombobox.getModel();

        // 1. Get the current editor text, ensuring it matches the input parameter 'text'
        String currentText = text.toLowerCase();

        // 2. Filter the complete list of student items
        List<String> filteredList = allStudentItems.stream()
                .filter(item -> item.toLowerCase().contains(currentText))
                .collect(Collectors.toList());

        // 3. Temporarily disable listener to prevent action calls during model update
        ActionListener[] listeners = idCombobox.getActionListeners();
        for (ActionListener l : listeners) {
            idCombobox.removeActionListener(l);
        }

        // 4. Update the model with filtered results (keeping the text the user typed)
        idCombobox.setModel(new DefaultComboBoxModel<>(filteredList.toArray(new String[0])));

        // Set the typed text back into the editor (crucial for editable combobox filtering)
        JTextField editor = (JTextField) idCombobox.getEditor().getEditorComponent();
        editor.setText(text);

        // 5. Re-enable listeners
        for (ActionListener l : listeners) {
            idCombobox.addActionListener(l);
        }

        // 6. Show the dropdown popup if results exist
        if (!filteredList.isEmpty()) {
            idCombobox.setPopupVisible(true);
        } else {
            idCombobox.setPopupVisible(false);
        }
    }


    private void handleTextSelection(String text) {
        // Attempt to find a matching item in the full list and select it
        for (int i = 0; i < idCombobox.getItemCount(); i++) {
            String item = (String) idCombobox.getItemAt(i);
            if (item != null && item.toLowerCase().contains(text.toLowerCase()) && item.contains(" - ")) {
                idCombobox.setSelectedItem(item);
                performEligibilityCheck();
                return;
            }
        }
        // If exact match isn't found, keep details cleared
        updateStudentDetails(text);
        btnEnrollmentProceed.setEnabled(false);
    }


    private void setupListeners() {
        // --- Key Listener for Searching/Filtering ---
        JTextField editor = (JTextField) idCombobox.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String currentText = editor.getText();

                // If ENTER is pressed, attempt to select/check the entered text
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleTextSelection(currentText);
                } else if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN) {
                    // Trigger dynamic filtering when typing (excluding navigation keys)
                    filterCombobox(currentText);
                }
            }
        });

        // Back Button Action
        btnBack.addActionListener(e -> {
            if (loggedInUser != null) {
                new Dashboard(loggedInUser).setVisible(true);
            }
            dispose();
        });

        // Dropdown Selection Listener (Triggers when item selected via mouse/arrow keys, or set by code)
        idCombobox.addActionListener(e -> {
            Object selected = idCombobox.getSelectedItem();

            // Check if the action is due to a genuine selection change (not model update)
            if (e.getActionCommand().equals("comboBoxChanged")) {
                if (selected != null) updateStudentDetails(selected.toString());

                // Check if a valid student is selected before running the eligibility check
                if (idCombobox.getSelectedIndex() > 0 && idCombobox.getSelectedItem().toString().contains(" - ")) {
                    performEligibilityCheck();
                } else {
                    btnEnrollmentProceed.setEnabled(false);
                }
            }
        });

        // Enrollment Proceed Button Action (Enabled ONLY if eligible)
        btnEnrollmentProceed.addActionListener(e -> {
            String studentID = idCombobox.getSelectedItem().toString().split(" - ")[0].trim();
            Student targetStudent = findStudent(studentID);

            if (targetStudent != null && targetStudent.checkEligibility().isEligible()) {
                new EnrollmentView(targetStudent).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Student is not eligible to enroll at this time.", "Enrollment Blocked", JOptionPane.ERROR_MESSAGE);
                btnEnrollmentProceed.setEnabled(false);
            }
        });
    }

    private void addComboboxItems() {
        allStudentItems = new ArrayList<>();
        allStudentItems.add(""); // Placeholder for initial state

        Collections.sort(students, (s1, s2) -> s1[0].compareToIgnoreCase(s2[0]));

        for (String[] student : students) {
            String s = String.format("%s - %s %s", student[0], student[1], student[2]);
            allStudentItems.add(s);
        }

        idCombobox.setModel(new DefaultComboBoxModel<>(allStudentItems.toArray(new String[0])));
        idCombobox.setSelectedIndex(0);
    }

    private void updateStudentDetails(String selectedItem) {
        if (selectedItem.isEmpty() || selectedItem.startsWith("-")) { // Check for empty or default start
            resetLabels();
            return;
        }

        if (selectedItem.contains(" - ")) {
            try {
                String id = selectedItem.split(" - ")[0].trim();
                for (String[] student : students) {
                    if (student[0].equals(id)) {
                        firstNameLabel.setText(student[1]);
                        lastNameLabel.setText(student[2]);
                        majorLabel.setText(student[3]);
                        yearLabel.setText(student[4]);

                        // Reset dynamic results
                        cgpaLabel.setText("-");
                        failedCountLabel.setText("-");
                        eligibilityLabel.setText("Status: Ready to Check");
                        eligibilityLabel.setForeground(Color.BLACK);
                        btnEnrollmentProceed.setEnabled(false);
                        return;
                    }
                }
            } catch (Exception e) {}
        }
        resetLabels();
    }

    private void resetLabels() {
        firstNameLabel.setText("-");
        lastNameLabel.setText("-");
        majorLabel.setText("-");
        yearLabel.setText("-");
        cgpaLabel.setText("-");
        failedCountLabel.setText("-");
        eligibilityLabel.setText("Status: Unknown");
        eligibilityLabel.setForeground(Color.BLACK);
    }

    private Student findStudent(String studentID) {
        StudentDAO studentDAO = new StudentDAO();
        AcademicRecordDAO recordDAO = new AcademicRecordDAO();

        List<Student> allStudents = studentDAO.loadAllStudents();
        recordDAO.loadRecords(allStudents);

        return allStudents.stream()
                .filter(s -> s.getUserID().equals(studentID))
                .findFirst()
                .orElse(null);
    }

    private void performEligibilityCheck() {
        Object selectedObj = idCombobox.getSelectedItem();
        if (selectedObj == null) return;
        String selectedText = selectedObj.toString();
        if (!selectedText.contains(" - ")) return;

        String studentID = selectedText.split(" - ")[0].trim();
        Student targetStudent = findStudent(studentID);

        if (targetStudent == null) {
            // Update labels inline instead of popup
            eligibilityLabel.setText("DATA ERROR: Student Not Found");
            eligibilityLabel.setForeground(INELIGIBLE_COLOR);
            btnEnrollmentProceed.setEnabled(false);
            return;
        }

        // Run Check
        EligibilityCheck checker = targetStudent.checkEligibility();

        // Update Labels based on loaded data
        cgpaLabel.setText(String.format("%.2f", targetStudent.getAcademicProfile().getCGPA()));
        failedCountLabel.setText(String.valueOf(targetStudent.getAcademicProfile().getTotalFailedCourses()));

        if (checker.isEligible()) {
            eligibilityLabel.setText("ELIGIBLE FOR RECOVERY");
            eligibilityLabel.setForeground(ELIGIBLE_COLOR);
            btnEnrollmentProceed.setEnabled(true);

        } else {
            eligibilityLabel.setText("NOT ELIGIBLE");
            eligibilityLabel.setForeground(INELIGIBLE_COLOR);
            btnEnrollmentProceed.setEnabled(false);
        }
    }

    private void setupStudentView(Student student) {
        // Find and select the student's own ID
        String targetID = student.getUserID();
        for (int i = 0; i < idCombobox.getItemCount(); i++) {
            String item = idCombobox.getItemAt(i);
            if (item.startsWith(targetID + " - ")) {
                idCombobox.setSelectedIndex(i);
                break;
            }
        }
        // Lock controls for the student
        idCombobox.setEnabled(false);
        // checkEligibilityButton removed from layout entirely

        // Automatically perform check
        SwingUtilities.invokeLater(() -> performEligibilityCheck());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CheckRecoveryEligibility(null));
    }
}