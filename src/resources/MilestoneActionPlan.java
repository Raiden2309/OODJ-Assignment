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

        this.loggedInUser = loggedInUser;
        this.milestoneDAO = new MilestoneDAO();

        setTitle("Milestone Action Plan");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("Milestone ID:"));
        txtMilestoneID = new JTextField();
        inputPanel.add(txtMilestoneID);

        inputPanel.add(new JLabel("Student ID:"));
        txtStudentID = new JTextField();
        inputPanel.add(txtStudentID);

        inputPanel.add(new JLabel("Course ID:"));
        txtCourseID = new JTextField();
        inputPanel.add(txtCourseID);

        inputPanel.add(new JLabel("Study Week:"));
        txtStudyWeek = new JTextField();
        inputPanel.add(txtStudyWeek);

        inputPanel.add(new JLabel("Task Description:"));
        txtTaskDes = new JTextField();
        inputPanel.add(txtTaskDes);

        inputPanel.add(new JLabel("Deadline (YYYY-MM-DD):"));
        txtDeadline = new JTextField();
        inputPanel.add(txtDeadline);

        inputPanel.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Not Started", "In Progress", "Completed"});
        inputPanel.add(cmbStatus);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(120,172,229));
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnRemove = new JButton("Remove");
        btnList = new JButton("List All");
        btnBack = new JButton("Back");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnList);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        inputPanel.setBackground(new Color(229, 215, 139));
        buttonPanel.setBackground(new Color(120,172,229));
        btnFailed = new JButton("Failed Component");
        btnRec = new JButton("Recommendation Entry");
        btnRecovery = new JButton("Recovery Progress");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        btnUpdate.setFont(new Font("Arial", Font.BOLD, 14));
        btnRemove.setFont(new Font("Arial", Font.BOLD, 14));
        btnList.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(229, 93, 138));

        tableModel = new DefaultTableModel(new String[]{"MilestoneID", "StudentID", "CourseID", "StudyWeek", "TaskDescription", "Deadline", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 && !"Student".equalsIgnoreCase(loggedInUser.getRole().getRoleName());
            }
        };

        tblMilestone = new JTable(tableModel);

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
                        cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 6));
                    }
                }
            }
        });

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

        lblMilestones = new JLabel("MILESTONE ACTION PLAN", SwingConstants.CENTER);
        lblMilestones.setFont(new Font("Comic Sans MS", Font.BOLD, 35));
        lblMilestones.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        JLabel lblLogo = new JLabel(logoIcon);

        JPanel logoTitlePanel = new JPanel(new BorderLayout());
        logoTitlePanel.setOpaque(false);
        logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        logoTitlePanel.add(lblLogo, BorderLayout.WEST);
        logoTitlePanel.add(lblMilestones, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(logoTitlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(229,215,139));

        JScrollPane tableScroll = new JScrollPane(tblMilestone);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);

        add(topPanel, BorderLayout.NORTH);
        add(leftButtonPanel, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Lock edit if role is Student
        if ("Student".equalsIgnoreCase(loggedInUser.getRole().getRoleName())) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnRemove.setEnabled(false);
            txtMilestoneID.setEditable(false);
            txtStudentID.setEditable(false);
            txtCourseID.setEditable(false);
            txtStudyWeek.setEditable(false);
            txtTaskDes.setEditable(false);
            txtDeadline.setEditable(false);
            cmbStatus.setEnabled(false);
        }

        btnAdd.addActionListener(e -> addMilestone());
        btnUpdate.addActionListener(e -> updateMilestone());
        btnRemove.addActionListener(e -> removeMilestone());
        btnList.addActionListener(e -> listMilestones());

        btnBack.addActionListener(e -> {
            new CRPHomePage(loggedInUser).setVisible(true);
            dispose();
        });

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

        listMilestones();
    }

    private void addMilestone() {
        if (!validateEmptyFields()) return;
        if (!validateDate()) return;
        if (!validateStudentID()) return;
        if (!validateCourseID()) return;
        if (isDuplicateMilestoneID(txtMilestoneID.getText())) {
            JOptionPane.showMessageDialog(this, "Error: Milestone ID already exists. Please use a unique ID.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to add this milestone?", "Confirm Add", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        try {
            String milestoneID = txtMilestoneID.getText();
            String studentID = txtStudentID.getText();
            String courseID = txtCourseID.getText();
            String studyWeek = txtStudyWeek.getText();
            String taskDescription = txtTaskDes.getText();
            Date deadline = dateFormat.parse(txtDeadline.getText());
            String status = (String) cmbStatus.getSelectedItem();

            RecoveryMilestone milestone = new RecoveryMilestone(milestoneID, studentID, courseID, studyWeek, taskDescription, deadline, status);
            milestoneDAO.saveMilestone(milestone);
            JOptionPane.showMessageDialog(this, "Milestone added!");
            listMilestones();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private void updateMilestone() {
        if (!validateEmptyFields()) return;
        if (!validateDate()) return;
        if (!validateStudentID()) return;
        if (!validateCourseID()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to update this milestone?", "Confirm Update", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        try {
            String milestoneID = txtMilestoneID.getText();
            String studentID = txtStudentID.getText();
            String courseID = txtCourseID.getText();
            String studyWeek = txtStudyWeek.getText();
            String taskDescription = txtTaskDes.getText();
            Date deadline = dateFormat.parse(txtDeadline.getText());
            String status = (String) cmbStatus.getSelectedItem();

            RecoveryMilestone updatedMilestone = new RecoveryMilestone(milestoneID, studentID, courseID, studyWeek, taskDescription, deadline, status);
            milestoneDAO.updateMilestone(milestoneID, updatedMilestone);
            JOptionPane.showMessageDialog(this, "Milestone updated!");
            listMilestones();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Unexpected error during update.");
        }
    }

    private void removeMilestone() {
        int selectedRow = tblMilestone.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Error: Select a row to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this milestone?", "Confirm Remove", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        String milestoneID = (String) tableModel.getValueAt(selectedRow, 0);
        milestoneDAO.removeMilestone(milestoneID);
        JOptionPane.showMessageDialog(this, "Milestone removed!");
        listMilestones();
        clearFields();
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
                    m.getStatusString()
            });
        }
    }

    private boolean validateEmptyFields() {
        if (txtMilestoneID.getText().trim().isEmpty() || txtStudentID.getText().trim().isEmpty() ||
                txtCourseID.getText().trim().isEmpty() || txtStudyWeek.getText().trim().isEmpty() ||
                txtTaskDes.getText().trim().isEmpty() || txtDeadline.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: All fields must be filled. Cannot leave any field empty.");
            return false;
        }
        return true;
    }

    private boolean validateDate() {
        try {
            dateFormat.parse(txtDeadline.getText());
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
        SwingUtilities.invokeLater(() -> {
            new MilestoneActionPlan(null).setVisible(true);
        });
    }
}