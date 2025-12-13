package resources;

import academic.AcademicProfile;
import academic.EligibilityCheck;
import domain.Student;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import service.EnrolledCourseDAO;
import service.StudentDAO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckRecoveryEligibility extends JFrame
{
    private JComboBox<String> idCombobox;

    private JLabel titleLabel;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel cgpaLabel;
    private JLabel totalFailedLabel;
    private JLabel resultLabel;

    private ButtonGroup radioButtons = new ButtonGroup();
    private JRadioButton filterAllRb;
    private JRadioButton filterEligibleRb;
    private JRadioButton filterIneligibleRb;
    private JRadioButton filterAlmostRb;

    private JTable studentsTable;
    private DefaultTableModel tableModel;

    private StudentDAO studentDAO = new StudentDAO();
    List<Student> students = studentDAO.loadAllStudents();
    private EnrolledCourseDAO enrolledCourseDAO = new EnrolledCourseDAO();

    final String defaultChar = "-";
    private Map<Student, Integer> studentEligibilityResults = new HashMap<>();

    public CheckRecoveryEligibility()
    {
        enrolledCourseDAO.loadRecords(students);
        getStudentEligibility();

        setTitle("CRS - Student Recovery Eligibility Checking");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4));

        idCombobox = new JComboBox<>();
        idCombobox.addItem("-- Select student --");
        for (Student student : students)
        {
            String s = String.format("%s - %s", student.getStudentId(), student.getFullName());
            idCombobox.addItem(s);
        }
        AutoCompleteDecorator.decorate(idCombobox);

        inputPanel.add(new JLabel("Select Student:"));
        inputPanel.add(idCombobox);

        inputPanel.add(new JLabel("Student ID:"));
        idLabel = new JLabel("-");
        inputPanel.add(idLabel);

        inputPanel.add(new JLabel("Student Name:"));
        nameLabel = new JLabel("-");
        inputPanel.add(nameLabel);

        inputPanel.add(new JLabel("CGPA Requirement Met?:"));
        cgpaLabel = new JLabel("-");
        inputPanel.add(cgpaLabel);

        inputPanel.add(new JLabel("Total Failed Courses Within Maximum Allowed?:"));
        totalFailedLabel = new JLabel("-");
        inputPanel.add(totalFailedLabel);

        inputPanel.add(new JLabel("Eligibility Result:"));
        resultLabel = new JLabel("-");
        inputPanel.add(resultLabel);

        buttonPanel.add(new JLabel("FILTER BY:"));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));

        filterAllRb = new JRadioButton("All");
        radioButtons.add(filterAllRb);
        buttonPanel.add(filterAllRb);

        filterEligibleRb = new JRadioButton("Eligible");
        radioButtons.add(filterEligibleRb);
        buttonPanel.add(filterEligibleRb);

        filterIneligibleRb = new JRadioButton("Not Eligible");
        radioButtons.add(filterIneligibleRb);
        buttonPanel.add(filterIneligibleRb);

        filterAlmostRb = new JRadioButton("Almost Eligible");
        radioButtons.add(filterAlmostRb);
        buttonPanel.add(filterAlmostRb);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{
                "Student ID", "Student Name", "Major", "Year", "Email"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        studentsTable = new JTable(tableModel);
        add(new JScrollPane(studentsTable), BorderLayout.CENTER);

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
                            }
                        }
                        selectStudentFromTable();
                        displayEligibilityResults();
                    }
                    else
                    {
                        nameLabel.setText(defaultChar);
                        idLabel.setText(defaultChar);
                        cgpaLabel.setText(defaultChar);
                        totalFailedLabel.setText(defaultChar);
                        resultLabel.setText(defaultChar);
                        resultLabel.setForeground(Color.BLACK);
                    }
                }
            }
        });

        studentsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (!e.getValueIsAdjusting())
                {
                    int selectedRow = studentsTable.getSelectedRow();
                    if (selectedRow != -1)
                    {
                        idLabel.setText((String) tableModel.getValueAt(selectedRow, 0));
                        nameLabel.setText((String) tableModel.getValueAt(selectedRow, 1));
                        idCombobox.setSelectedItem(
                                String.format("%s - %s", idLabel.getText(), nameLabel.getText())
                        );

                        displayEligibilityResults();
                    }
                }
            }
        });

        ActionListener radioButtonSelected = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                idCombobox.setSelectedIndex(0);

                nameLabel.setText(defaultChar);
                idLabel.setText(defaultChar);
                cgpaLabel.setText(defaultChar);
                totalFailedLabel.setText(defaultChar);
                resultLabel.setText(defaultChar);
                resultLabel.setForeground(Color.BLACK);

                JRadioButton selectedButton = (JRadioButton) e.getSource();

                if (selectedButton == filterAllRb) listStudents(filterAllRb.getText());
                else if (selectedButton == filterEligibleRb) listStudents(filterEligibleRb.getText());
                else if (selectedButton == filterIneligibleRb) listStudents(filterIneligibleRb.getText());
                else if (selectedButton == filterAlmostRb) listStudents(filterAlmostRb.getText());
            }
        };

        filterAllRb.addActionListener(radioButtonSelected);
        filterEligibleRb.addActionListener(radioButtonSelected);
        filterIneligibleRb.addActionListener(radioButtonSelected);
        filterAlmostRb.addActionListener(radioButtonSelected);

        getContentPane().setBackground(new Color(229, 215, 139));
        inputPanel.setBackground(new Color(229, 215, 139));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));
        buttonPanel.setBackground(new Color(229, 215, 139));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));

        titleLabel = new JLabel("Check Student CRP Eligibility", SwingConstants.CENTER);
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

        JScrollPane tableScroll = new JScrollPane(studentsTable);
        add(tableScroll, BorderLayout.CENTER);

        filterAllRb.setSelected(true);
        listStudents("All");
    }

    private void listStudents(String filterCondition)
    {
        try
        {
            tableModel.setRowCount(0);

            for (Student s : students)
            {
                Object[] row = {
                        s.getStudentId(),
                        s.getFullName(),
                        s.getMajor(),
                        s.getAcademicYear(),
                        s.getEmail()
                };
                int code = studentEligibilityResults.get(s);

                if (filterCondition.equals(filterAllRb.getText()))
                {
                    tableModel.addRow(row);
                }
                else if (filterCondition.equals(filterEligibleRb.getText()))
                {
                    if (code == 2) tableModel.addRow(row);
                }
                else if (filterCondition.equals(filterAlmostRb.getText()))
                {
                    if (code == 1) tableModel.addRow(row);
                }
                else if (filterCondition.equals(filterIneligibleRb.getText()))
                {
                    if (code == 0) tableModel.addRow(row);
                }
            }
            studentsTable.setModel(tableModel);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error: " + e);
        }
    }

    private void selectStudentFromTable()
    {
        int rowIndex = -1;

        for (int i = 0; i < tableModel.getRowCount(); i++)
        {
            Object studentId = tableModel.getValueAt(i, 0);

            if (studentId != null && studentId.equals(idLabel.getText()))
            {
                rowIndex = i;
                break;
            }
        }

        if (rowIndex != -1)
        {
            studentsTable.setRowSelectionInterval(rowIndex, rowIndex);
            studentsTable.scrollRectToVisible(studentsTable.getCellRect(rowIndex, 0, true));
        }
    }

    private void getStudentEligibility()
    {
        for (Student student : students)
        {
            AcademicProfile ap = student.getAcademicProfile();
            EligibilityCheck ec = student.checkEligibility();
            final double NEAR_FAIL_THRESHOLD = ec.getMinCgpa() + 0.1;
            int result;

            if (ec.checkCGPA(ap) && ec.checkFailedCourseLimit(ap)) result = 0;

            else if (ec.getMinCgpa() < ap.getCGPA() && ap.getCGPA() <= NEAR_FAIL_THRESHOLD
                    || ap.getTotalFailedCourse() == ec.getMaxFailedCourses()) result = 1;

            else result = 2;

            studentEligibilityResults.put(student, result);
        }
    }

    private void displayEligibilityResults()
    {
        Student targetStudent = null;

        for (Student s : students)
        {
            if (s.getUserID().equals(idLabel.getText()))
            {
                targetStudent = s;
                break;
            }
        }

        if (targetStudent != null)
        {
            String cgpaResult;
            String totalFailedCoursesResult;
            EligibilityCheck ec = targetStudent.checkEligibility();

            if (ec.checkCGPA(targetStudent.getAcademicProfile()))
            {
                cgpaResult = String.format("YES   [ CGPA: %.2f >= Minimum Requirement: %.2f ]",
                        targetStudent.getAcademicProfile().getCGPA(),
                        ec.getMinCgpa()
                );
            }
            else
            {
                cgpaResult = String.format("NO   [ CGPA: %.2f < Minimum Requirement: %.2f ]",
                        targetStudent.getAcademicProfile().getCGPA(),
                        ec.getMinCgpa()
                );
            }
            cgpaLabel.setText(cgpaResult);

            if (ec.checkFailedCourseLimit(targetStudent.getAcademicProfile()))
            {
                totalFailedCoursesResult = String.format("YES   [ Total Failed Courses: %d <= Maximum Allowed: %d ]",
                        targetStudent.getAcademicProfile().getTotalFailedCourse(),
                        ec.getMaxFailedCourses()
                );
            }
            else
            {
                totalFailedCoursesResult = String.format("NO   [ Total Failed Courses: %d > Maximum Allowed: %d ]",
                        targetStudent.getAcademicProfile().getTotalFailedCourse(),
                        ec.getMaxFailedCourses()
                );
            }
            totalFailedLabel.setText(totalFailedCoursesResult);

            final Color eligibleColour = new Color(0, 188, 0);
            final Color ineligibleColour = Color.RED;

            if (cgpaLabel.getText().contains("NO") || totalFailedLabel.getText().contains("NO"))
            {
                resultLabel.setText("ELIGIBLE FOR COURSE RECOVERY PROGRAM");
                resultLabel.setForeground(eligibleColour);
            }
            else
            {
                resultLabel.setText("NOT ELIGIBLE FOR COURSE RECOVERY PROGRAM");
                resultLabel.setForeground(ineligibleColour);
            }
        }
    }

    public static void main(String[] args)
    {
        new CheckRecoveryEligibility().setVisible(true);
    }
}