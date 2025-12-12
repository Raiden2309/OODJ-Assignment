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


public class RequestEnrollCRP extends JFrame
{
    private JTable failedComponentsTable;
    private JLabel courseIdLabel;
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private DefaultTableModel tableModel;
    private JButton requestButton;
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
        courseIdLabel = new JLabel("-");
        inputPanel.add(courseIdLabel);

        inputPanel.add(new JLabel("Failure Description:"));
        descriptionLabel = new JLabel("-");
        inputPanel.add(descriptionLabel);

        add(inputPanel, BorderLayout.NORTH);
        tableModel = new DefaultTableModel(new String[]{"Course ID", "Failure Description", "Request Status"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        failedComponentsTable = new JTable(tableModel);
        add(new JScrollPane(failedComponentsTable), BorderLayout.CENTER);

        requestButton = new JButton("Submit Enrollment Request");
        requestButton.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));
        add(requestButton, BorderLayout.SOUTH);

        requestButton.addActionListener(e -> submitRequest());

        failedComponentsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (!e.getValueIsAdjusting())
                {
                    int selectedRow = failedComponentsTable.getSelectedRow();
                    if (selectedRow != -1)
                    {
                        courseIdLabel.setText((String) tableModel.getValueAt(selectedRow, 0));
                        descriptionLabel.setText((String) tableModel.getValueAt(selectedRow, 1));
                    }
                }
            }
        });

        getContentPane().setBackground(new Color(229, 215, 139));
        inputPanel.setBackground(new Color(229, 215, 139));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));

        titleLabel = new JLabel("Request to Enroll in CRP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        JLabel lblLogo = new JLabel(logoIcon);

        JPanel logoTitlePanel = new JPanel(new BorderLayout());
        logoTitlePanel.setBackground(new Color(229, 215, 139));
        logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        logoTitlePanel.add(lblLogo, BorderLayout.WEST);
        logoTitlePanel.add(titleLabel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(229, 215, 139));
        topPanel.add(logoTitlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JScrollPane tableScroll = new JScrollPane(failedComponentsTable);
        add(tableScroll, BorderLayout.CENTER);

        listFailedCourses();
    }

    private void submitRequest() {
        if (!validateSubmission()) return;
        
        if (courseIdLabel.getText().equals("-") || descriptionLabel.getText().equals("-"))
        {
            JOptionPane.showMessageDialog(null, "Please select an academic record first.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Submit an enrolment request?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        try
        {
            String courseID = courseIdLabel.getText();
            String description = descriptionLabel.getText();

            String lastRecID = recommendationDAO.loadRecommendations().getLast().getRecID();
            lastRecID = lastRecID.replaceAll("\\D+", "");

            String newRecID = String.format("%03d", Integer.parseInt(lastRecID) + 1);
            newRecID = "R" + newRecID;

            Recommendation recommendations = new Recommendation(newRecID, "S003", courseID, description, "To be set", new Date(), "Pending");
            recommendationDAO.saveRecommendation(recommendations);
            JOptionPane.showMessageDialog(this, "Request submitted successfully!");
            listFailedCourses();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error: " + e);
        }
    }

    private boolean validateSubmission()
    {
        for (Recommendation rec : recommendationDAO.loadRecommendations())
        {
            if (rec.getStudentID().trim().equals("S003") && rec.getCourseID().trim().equals(courseIdLabel.getText()))
            {
                JOptionPane.showMessageDialog(null, "Already submitted recovery request for this course", "Duplicate Submission", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void listFailedCourses()
    {
        try
        {
            tableModel.setRowCount(0);
            EnrolledCourseDAO dao = new EnrolledCourseDAO();
            List<EnrolledCourse> enrolledCourses = dao.loadAllEnrolledCourses();

            List<Recommendation> recommendations = recommendationDAO.loadRecommendations();

            final String studentId = "S003";

            for (EnrolledCourse ec : enrolledCourses)
            {
                if (!ec.getFailedComponent().equals("None") && ec.getStudentID().equals(studentId))
                {
                    String status = "Not submitted";

                    for (Recommendation rec : recommendations)
                    {
                        if (rec.getStudentID().trim().equals(studentId)
                                && rec.getCourseID().trim().equals(ec.getCourseID()))
                        {
                            status = rec.getStatus();
                            break;
                        }
                    }

                    Object[] row = {
                            ec.getCourseID(),
                            ec.getFailedComponent(),
                            status
                    };
                    
                    if (!status.equals("Approved") && !status.equals("Completed"))
                    {
                        tableModel.addRow(row);
                    }
                }
            }
            failedComponentsTable.setModel(tableModel);;
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error: " + e);
        }
    }

    public static void main(String[] args)
    {
        new RequestEnrollCRP(null).setVisible(true);
    }
}