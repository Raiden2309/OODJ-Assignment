package resources;

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
import java.util.Comparator;

public class CheckRecoveryEligibility extends JFrame {

    private JPanel mainPanel;
    private JComboBox<String> idCombobox;
    private JButton checkEligibilityButton;
    private JButton btnBack;

    // Dynamic Labels
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel majorLabel;
    private JLabel yearLabel;

    // Result Labels (Updated to show details inline)
    private JLabel eligibilityLabel;
    private JLabel cgpaLabel;
    private JLabel failedCountLabel;

    // Styling Constants
    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color ELIGIBLE_COLOR = new Color(40, 167, 69);
    private final Color INELIGIBLE_COLOR = new Color(220, 53, 69);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Backend Access
    DataAccess data = new DataAccess();
    List<String[]> students = data.getStudents();
    private List<String> allItems = new ArrayList<>();
    private User loggedInUser;

    public CheckRecoveryEligibility(User user) {
        this.loggedInUser = user;

        setTitle("Check Student Eligibility");
        setSize(800, 700); // Increased height slightly
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Eligibility Checker");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- CENTER CONTENT ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Search Section
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel searchLbl = new JLabel("Search Student (ID - Name):");
        searchLbl.setFont(LABEL_FONT);

        idCombobox = new JComboBox<>();
        idCombobox.setEditable(true);
        idCombobox.setFont(VALUE_FONT);
        idCombobox.setPreferredSize(new Dimension(300, 35));

        searchPanel.add(searchLbl);
        searchPanel.add(idCombobox);

        // Info Section
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        firstNameLabel = createInfoLabel(infoPanel, "First Name:");
        lastNameLabel = createInfoLabel(infoPanel, "Last Name:");
        majorLabel = createInfoLabel(infoPanel, "Major:");
        yearLabel = createInfoLabel(infoPanel, "Year:");

        // Result Section (Updated to show more details)
        JPanel resultPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 rows
        resultPanel.setBackground(new Color(245, 247, 250)); // Light background for results
        resultPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Row 1: Status
        JLabel resLbl = new JLabel("Eligibility Status:");
        resLbl.setFont(LABEL_FONT);
        eligibilityLabel = new JLabel("-");
        eligibilityLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        eligibilityLabel.setForeground(Color.GRAY);
        resultPanel.add(resLbl);
        resultPanel.add(eligibilityLabel);

        // Row 2: CGPA
        JLabel cgpaTitle = new JLabel("Current CGPA:");
        cgpaTitle.setFont(LABEL_FONT);
        cgpaLabel = new JLabel("-");
        cgpaLabel.setFont(VALUE_FONT);
        resultPanel.add(cgpaTitle);
        resultPanel.add(cgpaLabel);

        // Row 3: Failed Count
        JLabel failTitle = new JLabel("Failed Courses:");
        failTitle.setFont(LABEL_FONT);
        failedCountLabel = new JLabel("-");
        failedCountLabel.setFont(VALUE_FONT);
        resultPanel.add(failTitle);
        resultPanel.add(failedCountLabel);

        contentPanel.add(searchPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(resultPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // --- BOTTOM BUTTONS ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        btnBack = createRoundedButton("Back to Dashboard", new Color(108, 117, 125), Color.WHITE);
        checkEligibilityButton = createRoundedButton("Check Eligibility", ACCENT_COLOR, Color.WHITE);

        buttonPanel.add(btnBack);
        buttonPanel.add(checkEligibilityButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // --- LOGIC ---
        loadAllItems();
        addComboboxItems(allItems);

        JTextField editor = (JTextField) idCombobox.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (loggedInUser instanceof Student) return;
                SwingUtilities.invokeLater(() -> filterList(editor.getText()));
            }
        });

        idCombobox.addActionListener(e -> {
            if ("comboBoxChanged".equals(e.getActionCommand())) {
                Object selected = idCombobox.getSelectedItem();
                if (selected != null) updateStudentDetails(selected.toString());
            }
        });

        checkEligibilityButton.addActionListener(e -> {
            if (idCombobox.getItemCount() == 0 || idCombobox.getSelectedItem() == null) return;
            String sel = idCombobox.getSelectedItem().toString();
            if (sel.contains(" - ")) performEligibilityCheck();
        });

        btnBack.addActionListener(e -> {
            new Dashboard(loggedInUser).setVisible(true);
            dispose();
        });

        if (loggedInUser instanceof Student) {
            setupStudentView((Student) loggedInUser);
        }

        setVisible(true);
    }

    private JLabel createInfoLabel(JPanel panel, String title) {
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(LABEL_FONT);
        titleLbl.setForeground(Color.GRAY);

        JLabel valueLbl = new JLabel("-");
        valueLbl.setFont(VALUE_FONT);
        valueLbl.setForeground(Color.BLACK);

        panel.add(titleLbl);
        panel.add(valueLbl);
        return valueLbl;
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
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadAllItems() {
        Collections.sort(students, (s1, s2) -> s1[0].compareToIgnoreCase(s2[0]));
        for (String[] s : students) {
            allItems.add(String.format("%s - %s %s", s[0], s[1], s[2]));
        }
    }

    private void addComboboxItems(List<String> items) {
        idCombobox.setModel(new DefaultComboBoxModel<>(items.toArray(new String[0])));
        idCombobox.setSelectedItem(null);
    }

    private void filterList(String text) {
        if (text.isEmpty()) {
            if (idCombobox.getItemCount() != allItems.size()) addComboboxItems(allItems);
            return;
        }
        List<String> filtered = new ArrayList<>();
        for (String item : allItems) {
            if (item.toLowerCase().contains(text.toLowerCase())) filtered.add(item);
        }
        if (filtered.size() != idCombobox.getItemCount()) {
            idCombobox.setModel(new DefaultComboBoxModel<>(filtered.toArray(new String[0])));
            idCombobox.setSelectedItem(text);
            if (!filtered.isEmpty()) idCombobox.showPopup();
            else idCombobox.hidePopup();
        }
        ((JTextField) idCombobox.getEditor().getEditorComponent()).setText(text);
    }

    private void updateStudentDetails(String selectedItem) {
        if (selectedItem == null || selectedItem.isEmpty() || !selectedItem.contains(" - ")) {
            resetLabels();
            return;
        }
        try {
            String id = selectedItem.split(" - ")[0].trim();
            for (String[] s : students) {
                if (s[0].equals(id)) {
                    firstNameLabel.setText(s[1]);
                    lastNameLabel.setText(s[2]);
                    majorLabel.setText(s[3]);
                    yearLabel.setText(s[4]);

                    // Reset result labels when student changes (until button clicked)
                    eligibilityLabel.setText("Unknown");
                    eligibilityLabel.setForeground(Color.GRAY);
                    cgpaLabel.setText("-");
                    failedCountLabel.setText("-");
                    return;
                }
            }
        } catch (Exception e) {}
        resetLabels();
    }

    private void resetLabels() {
        firstNameLabel.setText("-");
        lastNameLabel.setText("-");
        majorLabel.setText("-");
        yearLabel.setText("-");
        eligibilityLabel.setText("Unknown");
        eligibilityLabel.setForeground(Color.GRAY);
        cgpaLabel.setText("-");
        failedCountLabel.setText("-");
    }

    private void performEligibilityCheck() {
        Object selectedObj = idCombobox.getSelectedItem();
        if (selectedObj == null) return;
        String selectedText = selectedObj.toString();
        if (!selectedText.contains(" - ")) return;

        String studentID = selectedText.split(" - ")[0].trim();

        StudentDAO studentDAO = new StudentDAO();
        AcademicRecordDAO recordDAO = new AcademicRecordDAO();
        List<Student> allStudents = studentDAO.loadAllStudents();
        recordDAO.loadRecords(allStudents);

        Student targetStudent = allStudents.stream()
                .filter(s -> s.getUserID().equals(studentID))
                .findFirst().orElse(null);

        if (targetStudent == null) {
            // Using a non-modal message label or status bar would be better than popup,
            // but for errors, a popup is still standard.
            // I'll update status label instead.
            eligibilityLabel.setText("Data Error");
            return;
        }

        EligibilityCheck checker = targetStudent.checkEligibility();

        // Update Labels (No Popup!)
        cgpaLabel.setText(String.format("%.2f", targetStudent.getAcademicProfile().getCGPA()));
        failedCountLabel.setText(String.valueOf(targetStudent.getAcademicProfile().getTotalFailedCourses()));

        if (checker.isEligible()) {
            eligibilityLabel.setText("ELIGIBLE");
            eligibilityLabel.setForeground(ELIGIBLE_COLOR);
        } else {
            eligibilityLabel.setText("NOT ELIGIBLE");
            eligibilityLabel.setForeground(INELIGIBLE_COLOR);
        }
    }

    private void setupStudentView(Student student) {
        String targetID = student.getUserID();
        for (int i = 0; i < idCombobox.getItemCount(); i++) {
            String item = idCombobox.getItemAt(i);
            if (item.startsWith(targetID + " - ")) {
                idCombobox.setSelectedIndex(i);
                break;
            }
        }
        idCombobox.setEnabled(false);
        idCombobox.setEditable(false);
        SwingUtilities.invokeLater(() -> performEligibilityCheck());
        checkEligibilityButton.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CheckRecoveryEligibility(null));
    }
}