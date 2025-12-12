package resources;

import academic.RecoveryResult;
import academic.Course;
import domain.User;
import service.RecoveryDAO;
import service.CourseCatalog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RecoveryProgress extends JFrame {
    private JTable tblAttempt;
    private JTextField txtAttemptID;
    private JComboBox<String>  cmbStatus;
    private JLabel lblAttemptID;
    private JLabel lblStatus;
    private JButton btnSave;
    private JLabel lblRecovery;
    private JButton btnBack;
    private JButton btnMilestone;
    private JButton btnRec;
    private JButton btnFailed;
    private DefaultTableModel tableModel;
    private RecoveryDAO recoveryDAO;
    private User loggedInUser; //for login derrr

    public RecoveryProgress(User loggedInUser) {

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
        this.recoveryDAO = new RecoveryDAO();

        setTitle("Recovery Progress");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /*if (!(loggedInUser instanceof CourseAdministrator)) {
            btnSave.setEnabled(false);
        }*/

        //let user input der
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.setOpaque(false);
        inputPanel.add(lblAttemptID = new JLabel("AttemptID:"));
        txtAttemptID = new JTextField();
        inputPanel.add(txtAttemptID);

        inputPanel.add(lblStatus = new JLabel("Update Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Not Started", "In Progress", "Completed", "Passed", "Failed - Process to 2nd Attempt", "Failed - Process to 3rd Attempt", "Failed - No more Attempt"});
        inputPanel.add(cmbStatus);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(229,176,134));
        btnSave = new JButton("Save");
        buttonPanel.add(btnSave);

        btnBack = new JButton("Back");
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        //tbl display
        tableModel = new DefaultTableModel(new String[] {
                "AttemptID", "StudentID", "CourseID", "FailedComponent", "ExamScore", "AssignmentScore", "RecoveryStatus"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        //automatically fill txtAttemptID when user select a specific row
        tblAttempt = new JTable(tableModel);
        add(new JScrollPane(tblAttempt), BorderLayout.CENTER);

        tblAttempt.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblAttempt.getSelectedRow();
                    if (selectedRow != -1) {

                        String selectedAttemptID = (String) tableModel.getValueAt(selectedRow, 0);
                        txtAttemptID.setText(selectedAttemptID);

                    }
                }
            }
        });

        btnSave.addActionListener(e -> saveStatusUpdate());
        btnBack.addActionListener(e -> {
            new CRPHomePage().setVisible(true);
            dispose();
        });

        getContentPane().setBackground(new Color(229, 215, 139));
        inputPanel.setBackground(new Color(229, 215, 139));
        buttonPanel.setBackground(new Color(229,176,134));
        btnFailed = new JButton("Failed Component");
        btnRec = new JButton("Recommendation Entry");
        btnMilestone = new JButton("Milestone");
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(229, 93, 138));

        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS));
        leftButtonPanel.setBackground(new Color(229, 205, 103));

        Dimension size = new Dimension(200, 40);
        btnFailed.setMaximumSize(size);
        btnRec.setMaximumSize(size);
        btnMilestone.setMaximumSize(size);

        leftButtonPanel.add(btnFailed);
        leftButtonPanel.add(Box.createVerticalStrut(100));
        leftButtonPanel.add(btnRec);
        leftButtonPanel.add(Box.createVerticalStrut(100));
        leftButtonPanel.add(btnMilestone);
        leftButtonPanel.add(Box.createVerticalGlue());

        lblRecovery = new JLabel("RECOVERY PROGRESS", SwingConstants.CENTER);
        lblRecovery.setFont(new Font("Comic Sans MS", Font.BOLD, 35));
        lblRecovery.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        JLabel lblLogo = new JLabel(logoIcon);

        JPanel logoTitlePanel = new JPanel(new BorderLayout());
        logoTitlePanel.setOpaque(false);
        logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        logoTitlePanel.add(lblLogo, BorderLayout.WEST);
        logoTitlePanel.add(lblRecovery, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(logoTitlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(229,215,139));

        JScrollPane tableScroll = new JScrollPane(tblAttempt);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        add(tableScroll, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(leftButtonPanel, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnFailed.addActionListener(e -> {
            new FailedComponentOverview().setVisible(true);
            dispose();

        });

        btnRec.addActionListener(e -> {
            new RecommendationEntry(null).setVisible(true);
            dispose();

        });

        btnMilestone.addActionListener(e -> {
            new MilestoneActionPlan(null).setVisible(true);
            dispose();

        });

        loadRecoveryProgress();

    }

    private void loadRecoveryProgress() {
        tableModel.setRowCount(0);
        List<RecoveryResult> results = recoveryDAO.loadRecoveryResults();
        for (RecoveryResult result : results) {
            tableModel.addRow(new Object[]{
                    result.getAttemptID(),
                    result.getStudentID(),
                    result.getCourseID(),
                    result.getFailedComponent(),
                    result.getExamScore(),
                    result.getAssignmentScore(),
                    result.getRecoveryStatus()
            });
        }
    }

    private void saveStatusUpdate() {
        String enteredAttemptID = txtAttemptID.getText().trim();
        if (enteredAttemptID.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Error: Please enter an AttemptID.");
            return;

        }

        boolean attemptExists = false;
        int rowIndex = -1;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (enteredAttemptID.equals(tableModel.getValueAt(i, 0))) {

                attemptExists = true;
                rowIndex = i;
                break;

            }
        }
        if (!attemptExists) {

            JOptionPane.showMessageDialog(this, "Error: AttemptID does not exist. Please enter a valid AttemptID.");
            return;

        }

        int confirmSave = JOptionPane.showConfirmDialog(this, "Are you sure you want to save the update?", "Confirm Save", JOptionPane.OK_CANCEL_OPTION);
        if (confirmSave != JOptionPane.OK_OPTION) {
            return;
        }
        String newStatus = (String) cmbStatus.getSelectedItem();
        String studentID = (String) tableModel.getValueAt(rowIndex, 1);
        String courseID = (String) tableModel.getValueAt(rowIndex, 2);
        String failedComponent = (String) tableModel.getValueAt(rowIndex, 3);
        int examScore = (Integer) tableModel.getValueAt(rowIndex, 4);
        int assignmentScore = (Integer) tableModel.getValueAt(rowIndex, 5);

        Course course = CourseCatalog.getInstance().getCourse(courseID);
        RecoveryResult updatedResult = new RecoveryResult(enteredAttemptID, studentID, courseID, course, failedComponent, examScore, assignmentScore, newStatus);
        recoveryDAO.updateRecoveryResult(studentID, courseID, enteredAttemptID, updatedResult);
        JOptionPane.showMessageDialog(this, "Status updated successfully!");

        loadRecoveryProgress();
        txtAttemptID.setText("");
        cmbStatus.setSelectedIndex(0);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new RecoveryProgress(null).setVisible(true);
        });

    }
}