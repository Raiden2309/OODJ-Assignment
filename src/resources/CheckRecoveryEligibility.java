package resources;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import service.EnrolledCourseDAO;
import service.StudentDAO;
import academic.EligibilityCheck;
import domain.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CheckRecoveryEligibility extends JFrame
{
    private JComboBox<String> idCombobox;
    private JButton checkEligibilityButton;

    private JLabel labelTitle;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;

    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel majorLabel;
    private JLabel yearLabel;

    private JLabel eligibilityLabel;

    final Font txtFont = new Font("Arial", Font.PLAIN, 14);
    final Font categoryFont = new Font("Arial", Font.BOLD, 14);
    final Color eligibleColour = new Color(0, 188, 0);
    final Color ineligibleColour = Color.RED;
    final String defaultChar = "-";

    StudentDAO studentDAO = new StudentDAO();
    List<Student> students = studentDAO.loadAllStudents();

    public CheckRecoveryEligibility() {
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        idCombobox = new JComboBox<>();
        checkEligibilityButton = new JButton("Check Eligibility");

        label1 = new JLabel("Select Student:");
        label2 = new JLabel("Student ID:");
        label3 = new JLabel("Name:");
        label4 = new JLabel("Major:");
        label5 = new JLabel("Year:");
        label6 = new JLabel("Eligibility Result:");

        label1.setFont(categoryFont);
        label2.setFont(categoryFont);
        label3.setFont(categoryFont);
        label4.setFont(categoryFont);
        label5.setFont(categoryFont);
        label6.setFont(categoryFont);

        idLabel = new JLabel(defaultChar);
        nameLabel = new JLabel(defaultChar);
        majorLabel = new JLabel(defaultChar);
        yearLabel = new JLabel(defaultChar);
        eligibilityLabel = new JLabel("Unknown");

        idLabel.setFont(txtFont);
        nameLabel.setFont(txtFont);
        majorLabel.setFont(txtFont);
        yearLabel.setFont(txtFont);
        eligibilityLabel.setFont(txtFont);

        idCombobox.addItem("-- Select student --");

        for (Student student : students)
        {
            String s = String.format("%s - %s", student.getStudentId(), student.getFullName());
            idCombobox.addItem(s);
        }
        AutoCompleteDecorator.decorate(idCombobox);

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(logoIcon);

        labelTitle = new JLabel("Check Student CRP Eligibility", SwingConstants.CENTER);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel logoTitlePanel = new JPanel(new BorderLayout());
        logoTitlePanel.setBackground(new Color(229, 215, 139));
        logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        logoTitlePanel.add(logoLabel, BorderLayout.WEST);
        logoTitlePanel.add(labelTitle, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(229, 215, 139));
        topPanel.add(logoTitlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        inputPanel.add(label1);
        inputPanel.add(idCombobox);

        inputPanel.add(label2);
        inputPanel.add(idLabel);

        inputPanel.add(label3);
        inputPanel.add(nameLabel);

        inputPanel.add(label4);
        inputPanel.add(majorLabel);

        inputPanel.add(label5);
        inputPanel.add(yearLabel);

        inputPanel.add(label6);
        inputPanel.add(eligibilityLabel);

        inputPanel.add(new JLabel(""));

        add(inputPanel, BorderLayout.CENTER);

        checkEligibilityButton.setBorder(BorderFactory.createEmptyBorder(20,10,10,10));
        add(checkEligibilityButton,BorderLayout.SOUTH);

        getContentPane().setBackground(new Color(229, 215, 139));
        inputPanel.setBackground(new Color(229, 215, 139));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CRS - Student Recovery Eligibility Checking");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        idCombobox.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if (e.getStateChange() == ItemEvent.SELECTED)
                {
                    String selectedItem = e.getItem().toString();

                    if (idCombobox.getSelectedIndex() != 0)
                    {
                        String[] parts = selectedItem.split(" - ");
                        String id = parts[0].trim();

                        for (Student student : students)
                        {
                            if (student.getStudentId().equals(id))
                            {
                                idLabel.setText(student.getStudentId());
                                nameLabel.setText(student.getFullName());
                                majorLabel.setText(student.getMajor());
                                yearLabel.setText(student.getAcademicYear());
                            }
                        }
                    }
                    else
                    {
                        nameLabel.setText(defaultChar);
                        idLabel.setText(defaultChar);
                        majorLabel.setText(defaultChar);
                        yearLabel.setText(defaultChar);
                        eligibilityLabel.setText("Unknown");
                        eligibilityLabel.setForeground(Color.BLACK);
                    }
                }
            }
        });

        checkEligibilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idCombobox.getSelectedIndex() != 0)
                {
                    performEligibilityCheck();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Please select a student first.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void performEligibilityCheck () {
        String selectedItem = idCombobox.getSelectedItem().toString();
        String studentID = selectedItem.split(" - ")[0].trim();

        StudentDAO studentDAO = new StudentDAO();
        EnrolledCourseDAO enrolledCourseDAO = new EnrolledCourseDAO();

        List<Student> allStudents = studentDAO.loadAllStudents();
        enrolledCourseDAO.loadRecords(allStudents);

        Student targetStudent = null;
        for (Student s : allStudents)
        {
            if (s.getUserID().equals(studentID))
            {
                targetStudent = s;
                break;
            }
        }

        EligibilityCheck ec = targetStudent.checkEligibility();

        if (ec.isEligible())
        {
            eligibilityLabel.setText("ELIGIBLE FOR RECOVERY");
            eligibilityLabel.setForeground(eligibleColour);
        }
        else
        {
            eligibilityLabel.setText("NOT ELIGIBLE FOR RECOVERY");
            eligibilityLabel.setForeground(ineligibleColour);
        }
        String msg = String.format("Student: %s\nCGPA: %.2f\nFailed Courses: %d",
                targetStudent.getFullName(),
                targetStudent.getAcademicProfile().getCGPA(),
                targetStudent.getAcademicProfile().getTotalFailedCourse()
        );
        JOptionPane.showMessageDialog(null, msg, "Eligibility Result", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main (String[] args)
    {
        new CheckRecoveryEligibility();
    }
}