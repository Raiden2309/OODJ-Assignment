package resources;

import academic.RecoveryMilestone;
import domain.User;
import service.StudentDAO;
import service.MilestoneDAO;
import service.CourseCatalog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private JLabel lblMilestones;
    private JLabel lblMilestoneID;
    private JLabel lblStudentID;
    private JLabel lblCourseID;
    private JLabel lblStudyWeek;
    private JLabel lblTaskDes;
    private JLabel lblDeadline;
    private JLabel lblStatus;
    private JButton btnFailed;
    private JButton btnRec;
    private JButton btnRecovery;
    private DefaultTableModel tableModel;
    private MilestoneDAO milestoneDAO;
    private User loggedInUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public MilestoneActionPlan(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.milestoneDAO = new MilestoneDAO();

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
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(8, 2));
        inputPanel.setOpaque(false);
        inputPanel.add(lblMilestoneID = new JLabel("Milestone ID:"));
        txtMilestoneID = new JTextField();
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

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(229,176,134));
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnRemove = new JButton("Remove");
        btnList = new JButton("List");
        btnBack = new JButton("Back");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnList);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{"MilestoneID", "StudentID", "CourseID", "StudyWeek", "TaskDescription", "Deadline", "Status"}, 0);
        tblMilestone = new JTable(tableModel);
        add(new JScrollPane(tblMilestone), BorderLayout.CENTER);

        tblMilestone.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblMilestone.getSelectedRow();
                    if (selectedRow != -1) {
                        txtMilestoneID.setText((String) tableModel.getValueAt(selectedRow, 0));
                        txtStudentID.setText((String) tableModel.getValueAt(selectedRow, 1));
                        txtCourseID.setText((String) tableModel.getValueAt(selectedRow, 2));
                        txtStudyWeek.setText((String) tableModel.getValueAt(selectedRow, 3));
                        txtTaskDes.setText((String) tableModel.getValueAt(selectedRow, 4));
                        txtDeadline.setText((String) tableModel.getValueAt(selectedRow, 5));
                        cmbStatus.setSelectedItem((String) tableModel.getValueAt(selectedRow, 6));
                    }
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

        // --- Sidebar ---
        btnFailed = new JButton("Failed Component");
        btnRec = new JButton("Recommendation Entry");
        btnRecovery = new JButton("Recovery");
        btnSave = new JButton("Save"); // Assuming btnSave exists or this line is leftover, handled safely

        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(229, 93, 138));

        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS));
        leftButtonPanel.setBackground(new Color(229, 205, 103));

        Dimension size = new Dimension(200, 40);
        btnFailed.setMaximumSize(size);
        btnRec.setMaximumSize(size);
        btnRecovery.setMaximumSize(size);

        leftButtonPanel.add(btnFailed);
        leftButtonPanel.add(Box.createVerticalStrut(100));
        leftButtonPanel.add(btnRec);
        leftButtonPanel.add(Box.createVerticalStrut(100));
        leftButtonPanel.add(btnRecovery);
        leftButtonPanel.add(Box.createVerticalGlue());

        add(leftButtonPanel, BorderLayout.WEST);

        btnFailed.addActionListener(e -> {
            new FailedComponentOverview().setVisible(true);
            dispose();
        });

        btnRec.addActionListener(e -> {
            new RecommendationEntry(null).setVisible(true);
            dispose();
        });

        btnRecovery.addActionListener(e -> {
            new RecoveryProgress(null).setVisible(true);
            dispose();
        });

        // Load initial data
        listMilestones();
    }

    // Stub for btnSave to avoid compilation error if referenced
    private JButton btnSave;

    private void addMilestone() {
        if (txtMilestoneID.getText().isEmpty() || txtStudentID.getText().isEmpty() || txtCourseID.getText().isEmpty() || txtStudyWeek.getText().isEmpty() || txtTaskDes.getText().isEmpty() || txtDeadline.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: All fields must be filled.");
            return;
        }

        if (isDuplicateMilestoneID(txtMilestoneID.getText())) {
            JOptionPane.showMessageDialog(this, "Error: Duplicate Milestone ID.");
            return;
        }

        if (!validateDate(txtDeadline.getText())) return;
        if (!validateStudentID()) return;
        if (!validateCourseID()) return;

        try {
            Date deadline = dateFormat.parse(txtDeadline.getText());
            RecoveryMilestone milestone = new RecoveryMilestone(
                    txtMilestoneID.getText(),
                    txtStudentID.getText(),
                    txtCourseID.getText(),
                    txtStudyWeek.getText(),
                    txtTaskDes.getText(),
                    deadline,
                    (String) cmbStatus.getSelectedItem()
            );
            // FIX: Pass loggedInUser as the second argument
            milestoneDAO.addMilestone(milestone, loggedInUser);
            JOptionPane.showMessageDialog(this, "Milestone added successfully!");
            listMilestones();
            clearFields();
        } catch (Exception e) {
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
                    txtMilestoneID.getText(),
                    txtStudentID.getText(),
                    txtCourseID.getText(),
                    txtStudyWeek.getText(),
                    txtTaskDes.getText(),
                    deadline,
                    (String) cmbStatus.getSelectedItem()
            );
            // Assuming updateMilestone might also require user context based on your DAO pattern
            // If it only takes one argument, revert to milestoneDAO.updateMilestone(milestone);
            milestoneDAO.updateMilestone(milestone);
            JOptionPane.showMessageDialog(this, "Milestone updated successfully!");
            listMilestones();
            clearFields();
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
        }
    }

    private void listMilestones() {
        tableModel.setRowCount(0);
        List<RecoveryMilestone> milestones = milestoneDAO.loadMilestones();
        for (RecoveryMilestone m : milestones) {
            tableModel.addRow(new Object[]{
                    m.getMilestoneID(),
                    m.getStudentID(),
                    m.getCourseID(),
                    m.getStudyWeek(),
                    m.getTaskDescription(),
                    dateFormat.format(m.getDeadline()),
                    m.getStatus()
            });
        }
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

    private boolean isDuplicateMilestoneID(String milestoneID) {
        return milestoneDAO.loadMilestones().stream().anyMatch(m -> m.getMilestoneID().equals(milestoneID));
    }

    private void clearFields() {
        txtMilestoneID.setText("");
        txtStudentID.setText("");
        txtCourseID.setText("");
        txtStudyWeek.setText("");
        txtTaskDes.setText("");
        txtDeadline.setText("");
        cmbStatus.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MilestoneActionPlan(null).setVisible(true));
    }
}