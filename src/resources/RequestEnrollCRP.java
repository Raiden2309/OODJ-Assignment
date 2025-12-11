package resources;

import academic.EnrolledCourse;
import academic.Recommendation;
import domain.User;
import service.EnrolledCourseDAO;
import service.RecommendationDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Date;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class RequestEnrollCRP extends JFrame{
    private JTable tblRecommendation;
    private JLabel lblCourseID;
    private JLabel lblRecommendation;
    private JLabel lblDescription;
    private DefaultTableModel tableModel;
    private JButton btnSubmit;
    private RecommendationDAO recommendationDAO;
    private User loggedInUser;

    public RequestEnrollCRP(User loggedInUser)
    {
        this.loggedInUser = loggedInUser;
        this.recommendationDAO = new RecommendationDAO();

        setTitle("CRS - Request to Enroll in Course Recovery Program");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));

        inputPanel.add(new JLabel("Course ID:"));
        lblCourseID = new JLabel("-");
        inputPanel.add(lblCourseID);

        inputPanel.add(new JLabel("Description:"));
        lblDescription = new JLabel("-");
        inputPanel.add(lblDescription);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Course ID", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        tblRecommendation = new JTable(tableModel);
        add(new JScrollPane(tblRecommendation), BorderLayout.CENTER);

        btnSubmit = new JButton("Submit Request");
        add(btnSubmit, BorderLayout.SOUTH);

        btnSubmit.addActionListener(e -> submitRequest());

        tblRecommendation.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblRecommendation.getSelectedRow();
                    if (selectedRow != -1) {
                        lblCourseID.setText((String) tableModel.getValueAt(selectedRow, 0));
                        lblDescription.setText((String) tableModel.getValueAt(selectedRow, 1));
                    }
                }
            }
        });

        getContentPane().setBackground(new Color(229, 215, 139));
        inputPanel.setBackground(new Color(229, 215, 139));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));

        lblRecommendation = new JLabel("Request to Enroll in CRP", SwingConstants.CENTER);
        lblRecommendation.setFont(new Font("Arial", Font.BOLD, 22));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        JLabel lblLogo = new JLabel(logoIcon);

        JPanel logoTitlePanel = new JPanel(new BorderLayout());
        logoTitlePanel.setBackground(new Color(229, 215, 139));
        logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        logoTitlePanel.add(lblLogo, BorderLayout.WEST);
        logoTitlePanel.add(lblRecommendation, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(229, 215, 139));
        topPanel.add(logoTitlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JScrollPane tableScroll = new JScrollPane(tblRecommendation);
        add(tableScroll, BorderLayout.CENTER);

        listFailedCourses();
    }

    private void submitRequest() {
        if (!validateSubmission()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Submit an enrolment request?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        try
        {
            String courseID = lblCourseID.getText();
            String description = lblDescription.getText();

            String lastRecID = recommendationDAO.loadRecommendations().getLast().getRecID();
            lastRecID = lastRecID.replaceAll("\\D+", "");

            String newRecID = String.format("%03d", Integer.parseInt(lastRecID) + 1);
            newRecID = "R" + newRecID;

            Recommendation recommendations = new Recommendation(newRecID, "S003", courseID, description, "To be set", new Date(), "Pending");
            recommendationDAO.saveRecommendation(recommendations);
            JOptionPane.showMessageDialog(this, "Request submitted successfully!");
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error: " + e);
            e.printStackTrace();
        }
    }

    private boolean validateSubmission()
    {
        for (Recommendation rec : recommendationDAO.loadRecommendations())
        {
            if (rec.getStudentID().trim().equals("S003") && rec.getCourseID().trim().equals(lblCourseID.getText()))
            {
                JOptionPane.showMessageDialog(null, "Already submitted recovery request for this course", "Duplicate Submission", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void listFailedCourses() {
        try {
            tableModel.setRowCount(0);
            EnrolledCourseDAO dao = new EnrolledCourseDAO();
            List<EnrolledCourse> enrolledCourses = dao.loadAllEnrolledCourses();
            String[] columns = {"CourseID", "Failed Component"};

            for (EnrolledCourse ec : enrolledCourses) {
                if (!ec.getFailedComponent().equals("None") && ec.getStudentID().equals("S003")) {
                    Object[] row = {
                            ec.getCourseID(),
                            ec.getFailedComponent()
                    };
                    tableModel.addRow(row);
                }
            }
            tblRecommendation.setModel(tableModel);;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading failed components: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /*User admin = new CourseAdministrator("adminID", "Admin Name");
        SwingUtilities.invokeLater(() -> {
            new RecommendationEntry(admin).setVisible(true);
        });*/

        SwingUtilities.invokeLater(() -> {
            new RequestEnrollCRP(null).setVisible(true);
        });
    }
}