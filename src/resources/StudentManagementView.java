package resources;

import domain.User;
import domain.Student;
import service.StudentDAO;
import service.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentManagementView extends JFrame {

    private User loggedInUser;
    private StudentDAO studentDAO;
    private UserDAO userDAO;
    private JTable table;
    private DefaultTableModel model;

    public StudentManagementView(User user) {
        this.loggedInUser = user;
        this.studentDAO = new StudentDAO();
        this.userDAO = new UserDAO();

        setTitle("Student Account Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Manage Student Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(title);

        // Back Button
        JButton backBtn = createRoundedButton("Back to Dashboard", new Color(108, 117, 125), Color.WHITE);
        backBtn.addActionListener(e -> {
            new Dashboard(loggedInUser).setVisible(true);
            dispose();
        });

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.WEST);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.setBackground(new Color(245, 247, 250));
        backPanel.setBorder(new EmptyBorder(20, 0, 0, 20));
        backPanel.add(backBtn);
        topContainer.add(backPanel, BorderLayout.EAST);

        add(topContainer, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Major", "Email", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        actionPanel.setBackground(Color.WHITE);

        JButton activateBtn = createRoundedButton("Activate Account", new Color(40, 167, 69), Color.WHITE);
        JButton deactivateBtn = createRoundedButton("Deactivate Account", new Color(220, 53, 69), Color.WHITE);

        actionPanel.add(activateBtn);
        actionPanel.add(deactivateBtn);
        add(actionPanel, BorderLayout.SOUTH);

        // Load Data
        loadStudentData();

        // Listeners
        activateBtn.addActionListener(e -> updateStatus(true));
        deactivateBtn.addActionListener(e -> updateStatus(false));

        setVisible(true);
    }

    private void loadStudentData() {
        model.setRowCount(0);
        List<Student> students = studentDAO.loadAllStudents();
        for (Student s : students) {
            model.addRow(new Object[]{
                    s.getUserID(),
                    s.getFullName(),
                    s.getMajor(),
                    s.getEmail(),
                    s.isActive() ? "Active" : "Inactive"
            });
        }
    }

    private void updateStatus(boolean isActive) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first.");
            return;
        }

        String studentID = (String) model.getValueAt(selectedRow, 0);
        String action = isActive ? "Activate" : "Deactivate";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + action.toLowerCase() + " user " + studentID + "?",
                "Confirm " + action, JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            List<Student> all = studentDAO.loadAllStudents();
            Student target = null;
            for(Student s : all) {
                if(s.getUserID().equals(studentID)) {
                    target = s;
                    break;
                }
            }

            if(target != null) {
                target.setActive(isActive);
                if(userDAO.saveUserCredentials(target)) {
                    JOptionPane.showMessageDialog(this, "Success! User " + studentID + " is now " + (isActive ? "Active" : "Inactive"));
                    loadStudentData();
                } else {
                    JOptionPane.showMessageDialog(this, "Error saving status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
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