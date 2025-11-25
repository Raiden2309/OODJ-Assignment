package GUI;


import academic.Recommendation;
import domain.User;
import service.CourseCatalog;
import service.RecommendationDAO;
import service.StudentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class RecommendationEntry extends JFrame{
    private JTextField txtStudentID;
    private JTable tblRecommendation;
    private JTextField txtCourseID;
    private JTextField txtTimeLine;
    private JTextField txtDeadline;
    private JLabel lblCourseID;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnRemove;
    private JButton btnList;
    private JButton btnBack;
    private JLabel lblStudentID;
    private JLabel lblTimeline;
    private JLabel lblDeadline;
    private JLabel lblDescription;
    private JLabel lblStatus;
    private JLabel frmRecommendation;
    private JComboBox<String> cmbDescription;
    private JComboBox<String> cmbStatus;
    private JTextField txtRecID;
    private JLabel lblRecID;
    private DefaultTableModel tableModel;
    private RecommendationDAO recommendationDAO;
    private User loggedInUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public RecommendationEntry(User loggedInUser) {

        this.loggedInUser = loggedInUser;
        this.recommendationDAO = new RecommendationDAO();

        setTitle("Course Recovery Recommendation Entry");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(8, 2));
        inputPanel.add(new JLabel("RecID:"));
        txtRecID = new JTextField();
        inputPanel.add(txtRecID);

        inputPanel.add(new JLabel("Student ID:"));
        txtStudentID = new JTextField();
        inputPanel.add(txtStudentID);

        inputPanel.add(new JLabel("Course ID:"));
        txtCourseID = new JTextField();
        inputPanel.add(txtCourseID);

        inputPanel.add(new JLabel("Description:"));
        cmbDescription = new JComboBox<>(new String[]{"Exam", "Assignment", "Project", "Overall Fail"});
        inputPanel.add(cmbDescription);

        inputPanel.add(new JLabel("Timeline:"));
        txtTimeLine = new JTextField();
        inputPanel.add(txtTimeLine);

        inputPanel.add(new JLabel("Deadline:"));
        txtDeadline = new JTextField();
        inputPanel.add(txtDeadline);

        inputPanel.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Pending", "Approved", "Rejected", "Completed", "Cancelled"});
        inputPanel.add(cmbStatus);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
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

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(true);
        btnRemove.setEnabled(true);
        btnBack.setEnabled(true);//may delete after hehe :D

        /*if (!(loggedInUser instanceof CourseAdministrator)) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnRemove.setEnabled(false);
        }*/

        tableModel = new DefaultTableModel(new String[]{"RecID", "Student ID", "Course ID", "Description", "Timeline", "Deadline", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return column == 6;

            }
        };

        tblRecommendation = new JTable(tableModel);
        add(new JScrollPane(tblRecommendation), BorderLayout.CENTER);

        tblRecommendation.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblRecommendation.getSelectedRow();
                    if (selectedRow != -1) {
                        txtRecID.setText((String) tableModel.getValueAt(selectedRow, 0));
                        txtStudentID.setText((String) tableModel.getValueAt(selectedRow, 1));
                        txtCourseID.setText((String) tableModel.getValueAt(selectedRow, 2));
                        cmbDescription.setSelectedItem((String) tableModel.getValueAt(selectedRow, 3));
                        txtTimeLine.setText((String) tableModel.getValueAt(selectedRow, 4));
                        txtDeadline.setText((String) tableModel.getValueAt(selectedRow, 5));
                        cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 6));
                    }
                }
            }
        });

        btnAdd.addActionListener(e -> addRecommendation());
        btnUpdate.addActionListener(e -> updateRecommendation());
        btnRemove.addActionListener(e -> removeRecommendation());
        btnList.addActionListener(e -> listRecommendations());
        btnBack.addActionListener(e -> {
            new CRPHomePage().setVisible(true);
            dispose();
        });

        listRecommendations();
    }

    private void clearFields() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Clear all fields?",
                "Confirm Clear",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            txtRecID.setText("");
            txtStudentID.setText("");
            txtCourseID.setText("");
            txtTimeLine.setText("");
            txtDeadline.setText("");
            cmbDescription.setSelectedIndex(0);
            cmbStatus.setSelectedIndex(0);
            tblRecommendation.clearSelection();
        }
    }

    private void addRecommendation() {
        if (!validateEmptyFields()) return;
        if (!validateDateLine()) return;
        if (!validateStudentID()) return;
        if (!validateCourseID()) return;
        if (isDuplicateRecID(txtRecID.getText())) {
            JOptionPane.showMessageDialog(this, "Error: RecID already exists. Please use a unique ID.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to add this recommendation?", "Confirm Add", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        try {
            String recID = txtRecID.getText();
            String studentID = txtStudentID.getText();
            String courseID = txtCourseID.getText();
            String timeline = txtTimeLine.getText();
            Date deadline = dateFormat.parse(txtDeadline.getText());
            String description = (String) cmbDescription.getSelectedItem();
            String status = (String) cmbStatus.getSelectedItem();

            Recommendation recommendations = new Recommendation(recID, studentID, courseID, description, timeline, deadline, status);
            recommendationDAO.saveRecommendation(recommendations);
            JOptionPane.showMessageDialog(this, "Recommendation added!");
            listRecommendations();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid date format. Use YYYY-MM-DD.");
        }

    }

    private void updateRecommendation() {
        if (!validateEmptyFields()) return;
        if (!validateDateLine()) return;
        if (!validateStudentID()) return;
        if (!validateCourseID()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to update this recommendation?", "Confirm Update", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        try {
            String recID = txtRecID.getText();
            String studentID = txtStudentID.getText();
            String courseID = txtCourseID.getText();
            String timeline = txtTimeLine.getText();
            Date deadline = dateFormat.parse(txtDeadline.getText());
            String description = (String) cmbDescription.getSelectedItem();
            String status = (String) cmbStatus.getSelectedItem();

            Recommendation updatedRecommendation = new Recommendation(recID, studentID, courseID, description, timeline, deadline, status);
            recommendationDAO.updateRecommendation(recID, studentID, courseID, updatedRecommendation);
            JOptionPane.showMessageDialog(this, "Recommendation added!");
            listRecommendations();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Unexpected error during update.");
        }
    }

    private void removeRecommendation() {
        int selectedRow = tblRecommendation.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Error: Select a row to remove!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this recommendation?", "Confirm Remove", JOptionPane.OK_CANCEL_OPTION);

        if (confirm != JOptionPane.OK_OPTION) return;

        String recID = (String) tableModel.getValueAt(selectedRow, 0);
        String studentID = (String) tableModel.getValueAt(selectedRow, 1);
        String courseID = (String) tableModel.getValueAt(selectedRow, 2);
        recommendationDAO.removeRecommendation(recID, studentID, courseID);
        JOptionPane.showMessageDialog(this, "Milestone removed!");
        listRecommendations();
        clearFields();
    }

    private void listRecommendations() {
        tableModel.setRowCount(0);
        List<Recommendation> recommendations = recommendationDAO.loadRecommendations();
        for (Recommendation rec : recommendations) {
            tableModel.addRow(new Object[] {
                    rec.getRecID(),
                    rec.getStudentID(),
                    rec.getCourseID(),
                    rec.getDescription(),
                    rec.getTimeline(),
                    dateFormat.format(rec.getDeadline()),
                    rec.getStatus()
            });
        }
    }

    private boolean validateEmptyFields() {
        if (txtRecID.getText().trim().isEmpty() || txtStudentID.getText().trim().isEmpty() ||
                txtCourseID.getText().trim().isEmpty() || txtTimeLine.getText().trim().isEmpty() ||
                txtDeadline.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: All fields must be filled. Cannot leave any field empty.");
            return false;
        }
        return true;
    }

    public boolean validateDateLine() {
        try{
            dateFormat.parse(txtDeadline.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid date format. Use YYYY-MM-DD.");
            return false;
        }
        return true;
    }

    public boolean validateStudentID() {
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

    private boolean isDuplicateRecID(String recID) {
        return recommendationDAO.loadRecommendations().stream().anyMatch(m -> m.getRecID().equals(recID));
    }

    public static void main(String[] args) {
        /*User admin = new CourseAdministrator("adminID", "Admin Name");
        SwingUtilities.invokeLater(() -> {
            new RecommendationEntry(admin).setVisible(true);
        });*/

        SwingUtilities.invokeLater(() -> {
            new RecommendationEntry(null).setVisible(true);
        });
    }


}
