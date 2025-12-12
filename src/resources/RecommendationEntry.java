package resources;

import academic.Recommendation;
import domain.User;
import service.CourseCatalog;
import service.RecommendationDAO;
import service.StudentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
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
    private JLabel lblRecommendation;
    private JComboBox<String> cmbDescription;
    private JComboBox<String> cmbStatus;
    private JTextField txtRecID;
    private JLabel lblRecID;
    private JButton btnMilestone;
    private JButton btnFailed;
    private JButton btnRecovery;
    private DefaultTableModel tableModel;
    private RecommendationDAO recommendationDAO;
    private User loggedInUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public RecommendationEntry(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.recommendationDAO = new RecommendationDAO();

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

        setTitle("Recommendation Entry");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.setOpaque(false);
        inputPanel.add(lblRecID = new JLabel("Recommendation ID:"));
        txtRecID = new JTextField();
        inputPanel.add(txtRecID);

        inputPanel.add(lblStudentID = new JLabel("Student ID:"));
        txtStudentID = new JTextField();
        inputPanel.add(txtStudentID);

        inputPanel.add(lblCourseID = new JLabel("Course ID:"));
        txtCourseID = new JTextField();
        inputPanel.add(txtCourseID);

        inputPanel.add(lblTimeline = new JLabel("Timeline (String):"));
        txtTimeLine = new JTextField();
        inputPanel.add(txtTimeLine);

        inputPanel.add(lblDeadline = new JLabel("Deadline (YYYY-MM-DD):"));
        txtDeadline = new JTextField();
        inputPanel.add(txtDeadline);

        inputPanel.add(lblDescription = new JLabel("Description:"));
        cmbDescription = new JComboBox<>(new String[]{"Retake Exam", "Attend Tutorial", "Submit Assignment", "Review Materials"});
        inputPanel.add(cmbDescription);

        inputPanel.add(lblStatus = new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Pending", "Completed", "In Progress"});
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

        tableModel = new DefaultTableModel(new String[]{"RecID", "StudentID", "CourseID", "Timeline", "Deadline", "Description", "Status"}, 0);
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
                        txtTimeLine.setText((String) tableModel.getValueAt(selectedRow, 3));
                        txtDeadline.setText((String) tableModel.getValueAt(selectedRow, 4));
                        cmbDescription.setSelectedItem((String) tableModel.getValueAt(selectedRow, 5));
                        cmbStatus.setSelectedItem((String) tableModel.getValueAt(selectedRow, 6));
                    }
                }
            }
        });

        btnAdd.addActionListener(e -> addRecommendation());
        btnUpdate.addActionListener(e -> updateRecommendation());
        btnRemove.addActionListener(e -> removeRecommendation());
        btnList.addActionListener(e -> listRecommendation());

        // FIX: Pass loggedInUser to CRPHomePage
        btnBack.addActionListener(e -> {
            new Dashboard(loggedInUser).setVisible(true);
            dispose();
        });

        // Sidebar
        btnFailed = new JButton("Failed Component");
        btnMilestone = new JButton("Milestone");
        btnRecovery = new JButton("Recovery");

        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(229, 93, 138));

        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS));
        leftButtonPanel.setBackground(new Color(229, 205, 103));

        Dimension size = new Dimension(200, 40);
        btnFailed.setMaximumSize(size);
        btnMilestone.setMaximumSize(size);
        btnRecovery.setMaximumSize(size);

        leftButtonPanel.add(btnFailed);
        leftButtonPanel.add(Box.createVerticalStrut(100));
        leftButtonPanel.add(btnMilestone);
        leftButtonPanel.add(Box.createVerticalStrut(100));
        leftButtonPanel.add(btnRecovery);
        leftButtonPanel.add(Box.createVerticalGlue());

        lblRecommendation = new JLabel("RECOMMENDATION ENTRY", SwingConstants.CENTER);
        lblRecommendation.setFont(new Font("Comic Sans MS", Font.BOLD, 35));
        lblRecommendation.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        JLabel lblLogo = new JLabel(logoIcon);

        JPanel logoTitlePanel = new JPanel(new BorderLayout());
        logoTitlePanel.setOpaque(false);
        logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        logoTitlePanel.add(lblLogo, BorderLayout.WEST);
        logoTitlePanel.add(lblRecommendation, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(logoTitlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(leftButtonPanel, BorderLayout.WEST);

        btnFailed.addActionListener(e -> {
            new FailedComponentOverview().setVisible(true);
            dispose();
        });

        btnMilestone.addActionListener(e -> {
            new MilestoneActionPlan(null).setVisible(true);
            dispose();
        });

        btnRecovery.addActionListener(e -> {
            new RecoveryProgress(null).setVisible(true);
            dispose();
        });

        listRecommendation();
    }

    private void addRecommendation() {
        if (txtRecID.getText().isEmpty() || txtStudentID.getText().isEmpty() || txtCourseID.getText().isEmpty() || txtTimeLine.getText().isEmpty() || txtDeadline.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: All fields must be filled.");
            return;
        }

        if (isDuplicateRecID(txtRecID.getText())) {
            JOptionPane.showMessageDialog(this, "Error: Duplicate Recommendation ID.");
            return;
        }

        if (!validateDateLine()) return;
        // validateTimeLine() removed or simplified since timeline is String
        if (!validateStudentID()) return;
        if (!validateCourseID()) return;

        try {
            // FIX: timeline is String, deadline is Date
            String timeline = txtTimeLine.getText();
            Date deadline = dateFormat.parse(txtDeadline.getText());

            // FIX: Constructor order (ID, Student, Course, Desc, Timeline, Deadline, Status)
            Recommendation recommendation = new Recommendation(
                    txtRecID.getText(),
                    txtStudentID.getText(),
                    txtCourseID.getText(),
                    (String) cmbDescription.getSelectedItem(), // Description comes before timeline in Rec.java
                    timeline,
                    deadline,
                    (String) cmbStatus.getSelectedItem()
            );
            recommendationDAO.saveRecommendation(recommendation); // Updated call to saveRecommendation (was addRecommendation)
            JOptionPane.showMessageDialog(this, "Recommendation added successfully!");
            listRecommendation();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding recommendation: " + e.getMessage());
        }
    }

    private void updateRecommendation() {
        if (tblRecommendation.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Error: Please select a recommendation to update.");
            return;
        }

        if (!validateDateLine()) return;

        try {
            // FIX: timeline is String, deadline is Date
            String timeline = txtTimeLine.getText();
            Date deadline = dateFormat.parse(txtDeadline.getText());

            // FIX: Constructor order to match Recommendation.java
            Recommendation recommendation = new Recommendation(
                    txtRecID.getText(),
                    txtStudentID.getText(),
                    txtCourseID.getText(),
                    (String) cmbDescription.getSelectedItem(),
                    timeline,
                    deadline,
                    (String) cmbStatus.getSelectedItem()
            );
            // FIX: Pass all required arguments (RecID, StudentID, CourseID, Recommendation)
            recommendationDAO.updateRecommendation(
                    txtRecID.getText(),
                    txtStudentID.getText(),
                    txtCourseID.getText(),
                    recommendation
            );

            JOptionPane.showMessageDialog(this, "Recommendation updated successfully!");
            listRecommendation();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating recommendation: " + e.getMessage());
        }
    }

    private void removeRecommendation() {
        if (tblRecommendation.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Error: Please select a recommendation to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this recommendation?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // FIX: Pass all required arguments (RecID, StudentID, CourseID)
            recommendationDAO.removeRecommendation(
                    txtRecID.getText(),
                    txtStudentID.getText(),
                    txtCourseID.getText()
            );

            JOptionPane.showMessageDialog(this, "Recommendation removed successfully!");
            listRecommendation();
            clearFields();
        }
    }

    private void listRecommendation() {
        tableModel.setRowCount(0);
        List<Recommendation> recommendations = recommendationDAO.loadRecommendations();
        for (Recommendation r : recommendations) {
            tableModel.addRow(new Object[]{
                    r.getRecID(),
                    r.getStudentID(),
                    r.getCourseID(),
                    r.getTimeline(), // String, no format needed
                    dateFormat.format(r.getDeadline()),
                    r.getDescription(),
                    r.getStatus()
            });
        }
    }

    private void clearFields() {
        txtRecID.setText("");
        txtStudentID.setText("");
        txtCourseID.setText("");
        txtTimeLine.setText("");
        txtDeadline.setText("");
        cmbDescription.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
    }

    // Removed or relaxed validation since timeline is just a String now
    private boolean validateTimeLine() {
        if (txtTimeLine.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: Timeline cannot be empty.");
            return false;
        }
        return true;
    }

    private boolean validateDateLine() {
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
        SwingUtilities.invokeLater(() -> {
            new RecommendationEntry(null).setVisible(true);
        });
    }
}