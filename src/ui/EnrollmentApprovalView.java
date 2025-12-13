package ui;

import domain.User;
import domain.Enrollment;
import service.EnrollmentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentApprovalView extends JFrame {

    private User loggedInUser;
    private EnrollmentDAO enrollmentDAO;
    private JTable tblEnrollments;
    private DefaultTableModel model;
    private Dashboard dashboard;

    // Status constants
    private final String STATUS_PENDING = "Pending Approval";
    private final String STATUS_ACTIVE = "Active";

    // Colors
    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private final Color ERROR_COLOR = new Color(220, 53, 69);


    public EnrollmentApprovalView(User user, Dashboard dashboard) {
        this.loggedInUser = user;
        this.dashboard = dashboard;
        this.enrollmentDAO = new EnrollmentDAO();

        setTitle("Enrollment Approval Queue");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("Enrollment Review Queue (" + STATUS_PENDING + ")");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Student ID", "Plan ID", "Course ID", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tblEnrollments = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tblEnrollments);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        actionPanel.setBackground(Color.WHITE);

        JButton approveBtn = createRoundedButton("Approve Selected", SUCCESS_COLOR, Color.WHITE);
        JButton rejectBtn = createRoundedButton("Reject Selected", ERROR_COLOR, Color.WHITE);
        JButton refreshBtn = createRoundedButton("Refresh", ACCENT_COLOR, Color.WHITE);

        actionPanel.add(approveBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(refreshBtn);
        add(actionPanel, BorderLayout.SOUTH);

        // Listeners
        refreshBtn.addActionListener(e -> loadPendingEnrollments());
        approveBtn.addActionListener(e -> updateSelectedStatus(STATUS_ACTIVE));
        rejectBtn.addActionListener(e -> updateSelectedStatus("Rejected"));

        loadPendingEnrollments();
        setVisible(true);
    }

    private void loadPendingEnrollments() {
        model.setRowCount(0);
        List<Enrollment> all = enrollmentDAO.loadEnrollments();

        // Filter only Pending items
        List<Enrollment> pending = all.stream()
                .filter(e -> STATUS_PENDING.equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());

        for (Enrollment e : pending) {
            // Ensure Plan and Course are not null (from DAO load)
            String courseID = (e.getPlan() != null) ? e.getPlan().getCourseID() : "N/A";
            String planID = (e.getPlan() != null) ? e.getPlan().getPlanID() : "N/A";

            model.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getStudentId(),
                    planID,
                    courseID,
                    e.getStatus()
            });
        }

        if (pending.isEmpty()) {
            // Use system print instead of JOptionPane if queue is empty, to avoid spam
            System.out.println("Approval Queue is empty.");
        }
    }

    private void updateSelectedStatus(String newStatus) {
        int selectedRow = tblEnrollments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment to modify.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String enrollmentId = (String) model.getValueAt(selectedRow, 0);
        String action = newStatus.equals(STATUS_ACTIVE) ? "Approve" : "Reject";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + action + " enrollment " + enrollmentId + "?",
                "Confirm Action", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (enrollmentDAO.updateEnrollmentStatus(enrollmentId, newStatus)) {
                JOptionPane.showMessageDialog(this, "Enrollment " + enrollmentId + " has been " + newStatus + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingEnrollments(); // Refresh view

                if (dashboard != null) {
                    dashboard.refreshStaffContent();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error updating status in DAO.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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

        btn.setPreferredSize(new Dimension(180, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
}