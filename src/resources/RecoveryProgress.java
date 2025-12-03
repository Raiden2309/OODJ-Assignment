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
    private DefaultTableModel tableModel;
    private RecoveryDAO recoveryDAO;
    private User loggedInUser; //for login derrr

    public RecoveryProgress(User loggedInUser) {

        this.loggedInUser = loggedInUser;
        this.recoveryDAO = new RecoveryDAO();

        setTitle("Recovery Progress");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /*if (!(loggedInUser instanceof CourseAdministrator)) {
            btnSave.setEnabled(false);
        }*/

        //let user input der
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(lblAttemptID = new JLabel("AttemptID:"));
        txtAttemptID = new JTextField();
        inputPanel.add(txtAttemptID);

        inputPanel.add(lblStatus = new JLabel("Update Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Not Started", "In Progress", "Completed", "Passed", "Failed - Process to 2nd Attempt", "Failed - Process to 3rd Attempt", "Failed - No more Attempt"});
        inputPanel.add(cmbStatus);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
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
