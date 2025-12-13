package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import domain.Student;
import domain.Enrollment;
import academic.CourseRecoveryPlan;
import service.CourseRecoveryPlanDAO;

public class EnrollmentView extends JFrame {

    private Student currentStudent;
    private JComboBox<String> planDropdown;
    private JTextArea planDetailsArea;
    private JButton enrollButton;
    private JButton cancelButton;

    // Store the actual plan objects that correspond to the dropdown items
    private List<CourseRecoveryPlan> eligiblePlans;

    // Modern Colors
    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private final Color CANCEL_COLOR = new Color(108, 117, 125);
    private final Color CONTENT_BG = new Color(245, 247, 250);

    public EnrollmentView(Student student) {
        this.currentStudent = student;

        // 1. Setup UI Window
        setTitle("Course Recovery Enrollment - " + student.getFullName());
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(CONTENT_BG);

        // 2. Load Data
        loadEligiblePlans();

        // 3. Create Panels
        createTopPanel();
        createCenterPanel();
        createBottomPanel();

        // 4. Listeners
        setupListeners();

        setVisible(true);
    }

    private void loadEligiblePlans() {
        CourseRecoveryPlanDAO planDAO = new CourseRecoveryPlanDAO();
        List<CourseRecoveryPlan> allPlans = planDAO.loadAllPlans();

        // --- FIX: Remove Filtering Logic ---
        // We bypass the check against failed courses and load ALL available plans.
        eligiblePlans = allPlans;
        // --- END FIX ---
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        topPanel.setBackground(CONTENT_BG);
        topPanel.setBorder(new EmptyBorder(15, 15, 5, 15));

        JLabel nameLabel = new JLabel("Enroll Student: " + currentStudent.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(ACCENT_COLOR);

        topPanel.add(nameLabel);
        topPanel.add(new JLabel("Select the specific course recovery plan you wish to enroll in:"));
        add(topPanel, BorderLayout.NORTH);
    }

    private void createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(CONTENT_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        planDropdown = new JComboBox<>();
        planDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        planDropdown.setBackground(Color.WHITE);
        planDropdown.setFocusable(true);


        // Populate Dropdown
        if (eligiblePlans.isEmpty()) {
            planDropdown.addItem("No recovery plans available.");
            planDropdown.setEnabled(false);

            planDetailsArea = new JTextArea();
            planDetailsArea.setText("This student has no failed courses requiring an active recovery plan.");

        } else {
            // Dropdown is enabled since plans are available
            planDropdown.setEnabled(true);

            for (CourseRecoveryPlan plan : eligiblePlans) {
                String courseName = getCourseNameFromID(plan.getCourseID());
                String label = String.format("[%s] %s - Plan ID: %s", plan.getCourseID(), courseName, plan.getPlanID());
                planDropdown.addItem(label);
            }
            planDropdown.setSelectedIndex(0);

            planDetailsArea = new JTextArea();
            planDetailsArea.setEditable(false);
            planDetailsArea.setLineWrap(true);
            planDetailsArea.setWrapStyleWord(true);
            planDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            planDetailsArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            planDetailsArea.setBackground(Color.WHITE);

            updateDetails(0);

            SwingUtilities.invokeLater(() -> planDropdown.requestFocusInWindow());
        }

        if (planDetailsArea == null) {
            planDetailsArea = new JTextArea("Initialization Error.");
        }


        centerPanel.add(planDropdown, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(planDetailsArea), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomPanel.setBackground(CONTENT_BG);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 10, 10));

        // Rounded Cancel Button
        cancelButton = createRoundedButton("Cancel", CANCEL_COLOR, Color.WHITE);

        // Rounded Enroll Button (Green)
        enrollButton = createRoundedButton("Confirm Enrollment", SUCCESS_COLOR, Color.WHITE);

        if (eligiblePlans.isEmpty()) {
            enrollButton.setEnabled(false);
        }

        bottomPanel.add(cancelButton);
        bottomPanel.add(enrollButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateDetails(int index) {
        if (index >= 0 && index < eligiblePlans.size()) {
            CourseRecoveryPlan plan = eligiblePlans.get(index);

            String courseName = getCourseNameFromID(plan.getCourseID());

            String info = "Course: " + courseName + " (" + plan.getCourseID() + ")\n" +
                    "Plan ID: " + plan.getPlanID() + "\n" +
                    "Current Status: " + plan.getStatus() + "\n" +
                    "----------------------------------\n" +
                    "Recovery Recommendation:\n" +
                    plan.getRecommendation();

            planDetailsArea.setText(info);
        }
    }

    // Placeholder method to get course name (Assuming CourseResult holds the name)
    private String getCourseNameFromID(String courseID) {
        return currentStudent.getAcademicProfile().getCourseResults().stream()
                .filter(cr -> cr.getCourse().getCourseId().equals(courseID))
                .findFirst()
                .map(cr -> cr.getCourse().getName())
                .orElse("Course Name Unknown");
    }

    private void setupListeners() {
        planDropdown.addActionListener(e -> {
            updateDetails(planDropdown.getSelectedIndex());
        });

        cancelButton.addActionListener(e -> dispose());

        enrollButton.addActionListener(e -> {
            int index = planDropdown.getSelectedIndex();
            if (index >= 0 && index < eligiblePlans.size()) {
                CourseRecoveryPlan selectedPlan = eligiblePlans.get(index);

                // CORE LOGIC CALL
                Enrollment result = currentStudent.enroll(selectedPlan);

                if (result != null) {
                    JOptionPane.showMessageDialog(this,
                            "Enrollment request submitted!\n" +
                                    "Status: Active (Immediate Enrollment)",
                            "Enrollment Confirmed", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Enrollment Failed. Please check eligibility status.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // --- Helper for Rounded Button with Shadow & Hover ---
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

        btn.setPreferredSize(new Dimension(180, 40)); // Standard size for actions
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
}