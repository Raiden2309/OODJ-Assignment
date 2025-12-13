package ui;

import service.StudentDAO;
import service.AcademicRecordDAO;
import domain.Student;
import academic.EligibilityCheck;
import data_access.DataAccess;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EnrollmentGUI extends JFrame {
    // UI Components
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel studentIdLabel;
    private JComboBox<String> idCombobox; // Dropdown/Search box
    private JButton checkEligibilityButton;
    private JLabel detailsLabel;
    private JTextArea detailsTextArea;
    private JButton enrollButton;
    private JLabel statusLabel;

    private Student currentStudent;
    private List<String[]> allStudentsData;

    // Modern Colors/Fonts
    private final Color ACCENT_COLOR = new Color(0, 102, 204); // System blue (used for Check)
    private final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private final Color CONTENT_BG = new Color(245, 247, 250);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public EnrollmentGUI() {
        // 1. Window Setup
        setTitle("Student Enrollment Checker");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.allStudentsData = new DataAccess().getStudents();

        // 2. Initialize Main Panel
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(CONTENT_BG);
        mainPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        setContentPane(mainPanel);

        // 3. Setup Layout Constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // -- Row 0: Title --
        titleLabel = new JLabel("Recovery Enrollment Check");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // -- Row 1: Search Input and Button (using ComboBox) --
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        studentIdLabel = new JLabel("Student ID:");
        studentIdLabel.setFont(LABEL_FONT);
        studentIdLabel.setForeground(Color.DARK_GRAY);

        idCombobox = new JComboBox<>();
        idCombobox.setEditable(true);
        idCombobox.setFont(VALUE_FONT);
        idCombobox.setPreferredSize(new Dimension(200, 40));

        // Use styled button for Check (blue)
        checkEligibilityButton = createStyledButton("Check Eligibility", ACCENT_COLOR);

        searchPanel.add(studentIdLabel, BorderLayout.WEST);
        searchPanel.add(idCombobox, BorderLayout.CENTER);
        searchPanel.add(checkEligibilityButton, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(searchPanel, gbc);

        // -- Row 2: Details Label --
        detailsLabel = new JLabel("Academic Details:");
        detailsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailsLabel.setForeground(ACCENT_COLOR);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(detailsLabel, gbc);

        // -- Row 3: Text Area (NON-SCROLLABLE CARD) --
        detailsTextArea = new JTextArea(12, 40);
        detailsTextArea.setEditable(false);
        detailsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        detailsTextArea.setText("Select a Student ID and click 'Check Eligibility' above.");
        detailsTextArea.setBackground(Color.WHITE);
        detailsTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        detailsTextArea.setPreferredSize(new Dimension(600, 250));

        gbc.gridy = 3; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(detailsTextArea, gbc);

        // -- Row 4: Action Button --
        enrollButton = createRoundedButton("Proceed to Enrollment", SUCCESS_COLOR, Color.WHITE);
        enrollButton.setEnabled(false);

        gbc.gridy = 4; gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(enrollButton, gbc);

        // -- Row 5: Status Label --
        statusLabel = new JLabel("Status: Ready.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);
        gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(statusLabel, gbc);

        // 4. Initialization and Listeners
        loadComboboxItems();
        setupComboboxListener();
        checkEligibilityButton.addActionListener(e -> checkStudentEligibility());
        enrollButton.addActionListener(e -> enrollStudent());

        setVisible(true);
    }

    // Simple styled button for the CHECK button (less complex than rounded)
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(250, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int shadowGap = 3;
                int arcSize = 30; // Roundness
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

        btn.setPreferredSize(new Dimension(250, 45)); // Consistent size
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private void loadComboboxItems() {
        List<String> items = new ArrayList<>();
        items.add("-- Select a student --");

        Collections.sort(allStudentsData, Comparator.comparing(s -> s[0]));

        for (String[] s : allStudentsData) {
            items.add(String.format("%s - %s %s", s[0], s[1], s[2]));
        }

        idCombobox.setModel(new DefaultComboBoxModel<>(items.toArray(new String[0])));
        idCombobox.setSelectedIndex(0);
    }

    private void setupComboboxListener() {
        JTextField editor = (JTextField) idCombobox.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = editor.getText();
                filterCombobox(text);
                editor.setText(text);
            }
        });
    }

    private void filterCombobox(String text) {
        if (text.isEmpty() || text.equals("-- Select a student --")) {
            List<String> allItems = new ArrayList<>();
            allItems.add("-- Select a student --");
            allStudentsData.stream()
                    .sorted(Comparator.comparing(s -> s[0]))
                    .map(s -> String.format("%s - %s %s", s[0], s[1], s[2]))
                    .forEach(allItems::add);
            idCombobox.setModel(new DefaultComboBoxModel<>(allItems.toArray(new String[0])));
            return;
        }

        List<String> filtered = new ArrayList<>();
        for (String[] s : allStudentsData) {
            String item = String.format("%s - %s %s", s[0], s[1], s[2]);
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filtered.add(item);
            }
        }

        List<String> finalModel = new ArrayList<>();
        if (!filtered.isEmpty()) {
            finalModel.addAll(filtered);
        }

        if (!finalModel.isEmpty()) {
            idCombobox.setModel(new DefaultComboBoxModel<>(finalModel.toArray(new String[0])));
        }
    }


    private void checkStudentEligibility() {
        Object selected = idCombobox.getSelectedItem();
        if (selected == null || selected.toString().startsWith("-- Select")) {
            statusLabel.setText("Status: Please select a valid Student ID.");
            return;
        }

        String selectedItem = selected.toString();
        String id = selectedItem.split(" - ")[0].trim();

        statusLabel.setText("Status: Checking eligibility for " + id + "...");
        detailsTextArea.setText("Loading...");
        enrollButton.setEnabled(false);

        StudentDAO dao = new StudentDAO();
        List<Student> students = dao.loadAllStudents();
        AcademicRecordDAO recordDAO = new AcademicRecordDAO();
        recordDAO.loadRecords(students);

        currentStudent = students.stream()
                .filter(s -> s.getUserID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        if (currentStudent == null) {
            detailsTextArea.setText("Student ID '" + id + "' not found in the records.");
            statusLabel.setText("Status: Error: Student Not Found.");
            return;
        }

        EligibilityCheck checker = currentStudent.checkEligibility();
        boolean eligible = checker.isEligible();

        StringBuilder info = new StringBuilder();
        info.append("Name:           ").append(currentStudent.getFullName()).append("\n");
        info.append("Major:          ").append(currentStudent.getMajor()).append("\n");
        info.append("---------------------------------------------------\n");
        info.append("CGPA:           ").append(String.format("%.2f", currentStudent.getAcademicProfile().getCGPA())).append("\n");
        info.append("Failed Courses: ").append(currentStudent.getAcademicProfile().getTotalFailedCourses()).append("\n");
        info.append("---------------------------------------------------\n");
        info.append("Recovery Status: ").append(eligible ? "ELIGIBLE" : "NOT ELIGIBLE").append("\n");

        detailsTextArea.setText(info.toString());
        enrollButton.setEnabled(eligible);
        statusLabel.setText(eligible ? "Status: Student is ELIGIBLE. Proceed to enrollment." : "Status: Student is NOT eligible. Cannot enroll.");
    }

    private void enrollStudent() {
        if (currentStudent == null) return;

        new EnrollmentView(currentStudent).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EnrollmentGUI());
    }
}