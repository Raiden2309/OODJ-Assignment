package resources;

import academic.RecoveryMilestone;
import domain.User;
import domain.Student;
import service.StudentDAO;
import service.MilestoneDAO;
import service.CourseCatalog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MilestoneActionPlan extends JFrame {
    private JTable tblMilestone;
    private JTextField txtMilestoneID;
    private JTextField txtStudentID;
    private JTextField txtCourseID;
    private JTextField txtStudyWeek;
    private JTextField txtTaskDes;
    private JTextField txtDeadline;
    private JComboBox<String> cmbStatus;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnRemove;
    private JButton btnList;
    private JButton btnBack;

    // Labels
    private JLabel lblMilestoneID, lblStudentID, lblCourseID, lblStudyWeek, lblTaskDes, lblDeadline, lblStatus;
    private JLabel statusMessageLabel;

    private DefaultTableModel tableModel;
    private MilestoneDAO milestoneDAO;
    private User loggedInUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Colors
    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color RED_COLOR = new Color(220, 53, 69);
    private final Color TEXT_COLOR = Color.WHITE;

    public MilestoneActionPlan(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.milestoneDAO = new MilestoneDAO();

        // Background Setup
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon(getClass().getResource("/resources/bg3.png")).getImage();
                } catch (Exception e) {
                    setBackground(Color.WHITE);
                }
                setOpaque(false);
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        setTitle("Milestone Action Plan");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- INPUT PANEL ---
        JPanel inputPanel = new JPanel(new GridLayout(8, 2));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(lblMilestoneID = new JLabel("Milestone ID:"));
        txtMilestoneID = new JTextField();
        txtMilestoneID.setEditable(false); // Auto-generated
        inputPanel.add(txtMilestoneID);

        inputPanel.add(lblStudentID = new JLabel("Student ID:"));
        txtStudentID = new JTextField();
        inputPanel.add(txtStudentID);

        inputPanel.add(lblCourseID = new JLabel("Course ID:"));
        txtCourseID = new JTextField();
        inputPanel.add(txtCourseID);

        inputPanel.add(lblStudyWeek = new JLabel("Study Week:"));
        txtStudyWeek = new JTextField();
        inputPanel.add(txtStudyWeek);

        inputPanel.add(lblTaskDes = new JLabel("Task Description:"));
        txtTaskDes = new JTextField();
        inputPanel.add(txtTaskDes);

        inputPanel.add(lblDeadline = new JLabel("Deadline (YYYY-MM-DD):"));
        txtDeadline = new JTextField();
        inputPanel.add(txtDeadline);

        inputPanel.add(lblStatus = new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Not Started", "In Progress", "Completed", "Passed", "Failed"});
        inputPanel.add(cmbStatus);

        add(inputPanel, BorderLayout.NORTH);

        // --- BUTTON PANEL ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        btnAdd = createRoundedButton("Add", ACCENT_COLOR, TEXT_COLOR);
        btnUpdate = createRoundedButton("Update", ACCENT_COLOR, TEXT_COLOR);
        btnRemove = createRoundedButton("Remove", ACCENT_COLOR, TEXT_COLOR);
        btnList = createRoundedButton("List All", ACCENT_COLOR, TEXT_COLOR);
        btnBack = createRoundedButton("Back", RED_COLOR, TEXT_COLOR);

        if (!(loggedInUser instanceof Student)) {
            buttonPanel.add(btnAdd);
            buttonPanel.add(btnUpdate);
            buttonPanel.add(btnRemove);
            buttonPanel.add(btnList);
        }

        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- CENTER TABLE / MESSAGE ---
        JPanel centerPanel = new JPanel(new CardLayout());
        centerPanel.setOpaque(false);

        tableModel = new DefaultTableModel(new String[]{"MilestoneID", "StudentID", "CourseID", "StudyWeek", "TaskDescription", "Deadline", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblMilestone = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(tblMilestone);
        centerPanel.add(tableScroll, "TABLE");

        statusMessageLabel = new JLabel("You are currently not on any milestones.", SwingConstants.CENTER);
        statusMessageLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        statusMessageLabel.setForeground(Color.DARK_GRAY);
        centerPanel.add(statusMessageLabel, "EMPTY");

        add(centerPanel, BorderLayout.CENTER);

        // --- LISTENERS ---
        tblMilestone.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblMilestone.getSelectedRow();
                if (selectedRow != -1) {
                    populateFieldsFromTable(selectedRow);
                }
            }
        });

        btnAdd.addActionListener(e -> addMilestone());
        btnUpdate.addActionListener(e -> updateMilestone());
        btnRemove.addActionListener(e -> removeMilestone());
        btnList.addActionListener(e -> listMilestones());

        btnBack.addActionListener(e -> {
            new Dashboard(loggedInUser).setVisible(true);
            dispose();
        });

        // --- LOGIC: CHECK USER ROLE ---
        if (loggedInUser instanceof Student) {
            setupStudentView();
        } else {
            listMilestones();
            generateNewMilestoneID(); // Generate ID for new entry
            ((CardLayout) centerPanel.getLayout()).show(centerPanel, "TABLE");
        }

        setVisible(true);
    }

    // --- Helper for Rounded Button with Shadow & Hover ---
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

        btn.setPreferredSize(new Dimension(120, 45)); // Smaller size for action buttons
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    // --- STUDENT VIEW LOGIC ---
    private void setupStudentView() {
        // Disable inputs (Read-Only)
        txtMilestoneID.setEditable(false);
        txtStudentID.setEditable(false);
        txtCourseID.setEditable(false);
        txtStudyWeek.setEditable(false);
        txtTaskDes.setEditable(false);
        txtDeadline.setEditable(false);
        cmbStatus.setEnabled(false);

        txtStudentID.setText(loggedInUser.getUserID());

        List<RecoveryMilestone> allMilestones = milestoneDAO.loadMilestones();
        List<RecoveryMilestone> myMilestones = allMilestones.stream()
                .filter(m -> m.getStudentID().equalsIgnoreCase(loggedInUser.getUserID()))
                .collect(Collectors.toList());

        if (myMilestones.isEmpty()) {
            JPanel center = (JPanel) getContentPane().getComponent(1); // Careful with index, relying on CardLayout logic
            // Safer way to access center panel if indices change:
            Component[] comps = getContentPane().getComponents();
            for (Component c : comps) {
                if (c instanceof JPanel && ((JPanel)c).getLayout() instanceof CardLayout) {
                    ((CardLayout) ((JPanel)c).getLayout()).show((JPanel)c, "EMPTY");
                    break;
                }
            }
        } else {
            updateTable(myMilestones);
        }
    }

    private void populateFieldsFromTable(int row) {
        txtMilestoneID.setText((String) tableModel.getValueAt(row, 0));
        txtStudentID.setText((String) tableModel.getValueAt(row, 1));
        txtCourseID.setText((String) tableModel.getValueAt(row, 2));
        txtStudyWeek.setText((String) tableModel.getValueAt(row, 3));
        txtTaskDes.setText((String) tableModel.getValueAt(row, 4));
        txtDeadline.setText((String) tableModel.getValueAt(row, 5));
        cmbStatus.setSelectedItem((String) tableModel.getValueAt(row, 6));
    }

    // --- AUTO INCREMENT ID ---
    private void generateNewMilestoneID() {
        List<RecoveryMilestone> all = milestoneDAO.loadMilestones();
        if (all.isEmpty()) {
            txtMilestoneID.setText("M001");
            return;
        }

        int maxId = 0;
        for (RecoveryMilestone m : all) {
            try {
                // Assuming format "Mxxx"
                String idPart = m.getMilestoneID().substring(1);
                int idVal = Integer.parseInt(idPart);
                if (idVal > maxId) maxId = idVal;
            } catch (Exception e) {
                // Ignore weird IDs
            }
        }

        // Generate next
        txtMilestoneID.setText(String.format("M%03d", maxId + 1));
    }

    // --- CRUD OPERATIONS (For Staff) ---

    private void addMilestone() {
        if (txtStudentID.getText().isEmpty() || txtCourseID.getText().isEmpty() || txtStudyWeek.getText().isEmpty() || txtTaskDes.getText().isEmpty() || txtDeadline.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: All fields must be filled.");
            return;
        }

        // Removed duplicate check since we auto-generate ID

        if (!validateDate(txtDeadline.getText())) return;
        if (!validateStudentID()) return;
        if (!validateCourseID()) return;

        try {
            Date deadline = dateFormat.parse(txtDeadline.getText());
            RecoveryMilestone milestone = new RecoveryMilestone(
                    txtMilestoneID.getText(), txtStudentID.getText(), txtCourseID.getText(),
                    txtStudyWeek.getText(), txtTaskDes.getText(), deadline,
                    (String) cmbStatus.getSelectedItem()
            );
            // Pass loggedInUser as the second argument
            milestoneDAO.addMilestone(milestone, loggedInUser);
            JOptionPane.showMessageDialog(this, "Milestone added successfully!");
            listMilestones();
            clearFields();
            generateNewMilestoneID(); // Prep for next add
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding milestone: " + e.getMessage());
        }
    }

    private void updateMilestone() {
        if (tblMilestone.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Error: Please select a milestone to update.");
            return;
        }

        if (!validateDate(txtDeadline.getText())) return;

        try {
            Date deadline = dateFormat.parse(txtDeadline.getText());
            RecoveryMilestone milestone = new RecoveryMilestone(
                    txtMilestoneID.getText(), txtStudentID.getText(), txtCourseID.getText(),
                    txtStudyWeek.getText(), txtTaskDes.getText(), deadline,
                    (String) cmbStatus.getSelectedItem()
            );
            milestoneDAO.updateMilestone(milestone);
            JOptionPane.showMessageDialog(this, "Milestone updated successfully!");
            listMilestones();
            clearFields();
            generateNewMilestoneID();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating milestone: " + e.getMessage());
        }
    }

    private void removeMilestone() {
        if (tblMilestone.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Error: Please select a milestone to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this milestone?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            milestoneDAO.removeMilestone(txtMilestoneID.getText());
            JOptionPane.showMessageDialog(this, "Milestone removed successfully!");
            listMilestones();
            clearFields();
            generateNewMilestoneID();
        }
    }

    private void listMilestones() {
        List<RecoveryMilestone> milestones = milestoneDAO.loadMilestones();
        updateTable(milestones);
    }

    private void updateTable(List<RecoveryMilestone> milestones) {
        tableModel.setRowCount(0);
        for (RecoveryMilestone m : milestones) {
            tableModel.addRow(new Object[]{
                    m.getMilestoneID(), m.getStudentID(), m.getCourseID(),
                    m.getStudyWeek(), m.getTaskDescription(),
                    dateFormat.format(m.getDeadline()), m.getStatus()
            });
        }
    }

    private void clearFields() {
        // Don't clear ID, regenerate it
        txtStudentID.setText(""); txtCourseID.setText("");
        txtStudyWeek.setText(""); txtTaskDes.setText(""); txtDeadline.setText("");
        cmbStatus.setSelectedIndex(0);
        tblMilestone.clearSelection();
    }

    private boolean validateDate(String dateStr) {
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(dateStr);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid date format. Use YYYY-MM-DD.");
            return false;
        }
    }

    private boolean validateStudentID() {
        StudentDAO studentDAO = new StudentDAO();
        boolean exists = studentDAO.loadAllStudents().stream().anyMatch(s -> s.getUserID().equals(txtStudentID.getText()));
        if (!exists) {
            JOptionPane.showMessageDialog(this, "Error: Student ID does not exist.");
        }
        return exists;
    }

    private boolean validateCourseID() {
        boolean exists = CourseCatalog.getInstance().getCourse(txtCourseID.getText()) != null;
        if (!exists) {
            JOptionPane.showMessageDialog(this, "Error: Course ID does not exist.");
        }
        return exists;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MilestoneActionPlan(null).setVisible(true));
    }
}