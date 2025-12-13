package ui;

import domain.User;
import domain.Student;
import service.EnrolledCourseDAO;
import academic.EnrolledCourse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class FailedComponentOverview extends JFrame {

    private JTable tblFailed;
    private JButton btnBack;
    private DefaultTableModel tableModel;
    private EnrolledCourseDAO enrolledCoursesDAO;
    private User loggedInUser;

    // Modern UI Colors/Fonts
    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color RED_COLOR = new Color(220, 53, 69);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public FailedComponentOverview(User user) {
        this.loggedInUser = user;
        this.enrolledCoursesDAO = new EnrolledCourseDAO();

        // Window Setup
        setTitle("Failed Components Overview");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("Failed Components List");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- TABLE ---
        JPanel centerPanel = new JPanel(new CardLayout());
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        centerPanel.setBackground(Color.WHITE);

        String[] columns = {"StudentID", "CourseID", "ExamScore", "AssignmentScore", "ExamWeight", "AssignmentWeight", "FailedComponent"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Read-only
        };

        tblFailed = new JTable(tableModel);
        tblFailed.setRowHeight(25);
        tblFailed.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(tblFailed);

        // Empty State Message
        JLabel emptyLabel = new JLabel("No failed components found.", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        emptyLabel.setForeground(Color.GRAY);

        centerPanel.add(scrollPane, "TABLE");
        centerPanel.add(emptyLabel, "EMPTY");

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- BUTTONS ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        btnBack = createRoundedButton("Back to Dashboard", RED_COLOR, TEXT_COLOR);
        btnBack.addActionListener(e -> {
            dispose();
        });

        buttonPanel.add(btnBack);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- LOAD DATA ---
        loadData(centerPanel);

        setVisible(true);
    }

    private void loadData(JPanel centerPanel) {
        try {
            List<EnrolledCourse> allCourses = enrolledCoursesDAO.loadAllEnrolledCourses();
            List<EnrolledCourse> failedCourses;

            // Filter logic
            if (loggedInUser instanceof Student) {
                // Students only see their own failures
                failedCourses = allCourses.stream()
                        .filter(ec -> ec.getStudentID().equalsIgnoreCase(loggedInUser.getUserID()))
                        .filter(ec -> !ec.getFailedComponent().equalsIgnoreCase("None"))
                        .collect(Collectors.toList());
            } else {
                // Staff see all failures
                failedCourses = allCourses.stream()
                        .filter(ec -> !ec.getFailedComponent().equalsIgnoreCase("None"))
                        .collect(Collectors.toList());
            }

            // Populate Table
            tableModel.setRowCount(0);
            for (EnrolledCourse ec : failedCourses) {
                tableModel.addRow(new Object[]{
                        ec.getStudentID(),
                        ec.getCourseID(),
                        ec.getExamScore(),
                        ec.getAssignmentScore(),
                        ec.getExamWeight(),
                        ec.getAssignmentWeight(),
                        ec.getFailedComponent()
                });
            }

            // Show correct view
            CardLayout cl = (CardLayout) centerPanel.getLayout();
            if (failedCourses.isEmpty()) {
                cl.show(centerPanel, "EMPTY");
            } else {
                cl.show(centerPanel, "TABLE");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    // --- Helper for Rounded Button ---
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

    // Default constructor for compatibility if needed
    public FailedComponentOverview() {
        this(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FailedComponentOverview(null).setVisible(true));
    }
}