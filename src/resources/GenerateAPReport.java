package resources;

import javax.swing.*;
import domain.Student;
import org.jdesktop.swingx.autocomplete.*;
import report.GenerateReportPDF;
import service.StudentDAO;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GenerateAPReport extends JFrame
{
    private JComboBox idCombobox;
    private JButton selectButton;

    private JLabel labelTitle;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;

    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel majorLabel;
    private JLabel yearLabel;

    final Font txtFont = new Font("Arial", Font.PLAIN, 14);
    final Font categoryFont = new Font("Arial", Font.BOLD, 14);
    final String defaultChar = "-";

    StudentDAO studentDAO = new StudentDAO();
    List<Student> students = studentDAO.loadAllStudents();

    public GenerateAPReport()
    {
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        idCombobox = new JComboBox();
        selectButton = new JButton("Generate PDF Report");

        label1 = new JLabel("Select Student:");
        label2 = new JLabel("Student ID:");
        label3 = new JLabel("Student Name:");
        label4 = new JLabel("Major:");
        label5 = new JLabel("Year");

        label1.setFont(categoryFont);
        label2.setFont(categoryFont);
        label3.setFont(categoryFont);
        label4.setFont(categoryFont);
        label5.setFont(categoryFont);

        idLabel = new JLabel(defaultChar);
        nameLabel = new JLabel(defaultChar);
        majorLabel = new JLabel(defaultChar);
        yearLabel = new JLabel(defaultChar);

        idLabel.setFont(txtFont);
        nameLabel.setFont(txtFont);
        majorLabel.setFont(txtFont);
        yearLabel.setFont(txtFont);

        selectButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

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

        labelTitle = new JLabel("Generate Academic Performance Report", SwingConstants.CENTER);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel logoTitlePanel = new JPanel(new BorderLayout());
        logoTitlePanel.setBackground(new Color(229, 215, 139));
        logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        logoTitlePanel.add(logoLabel, BorderLayout.WEST);
        logoTitlePanel.add(labelTitle, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(229, 215, 139));
        topPanel.add(logoTitlePanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);

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

        inputPanel.add(new JLabel(""));

        add(selectButton, BorderLayout.SOUTH);

        getContentPane().setBackground(new Color(229, 215, 139));
        inputPanel.setBackground(new Color(229, 215, 139));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CRS - Generate Academic Performance Report");
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
                    }
                }
            }
        });

        selectButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (idCombobox.getSelectedIndex() != 0)
                {
                    try
                    {
                        GenerateReportPDF pdf = new GenerateReportPDF();
                        pdf.createDocument(idLabel.getText());
                        String message = String.format("Report for %s successfully generated!\nSaved to Downloads folder.", idLabel.getText());
                        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);

                    }
                    catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(null, "Error generating PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Please select a student first.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args)
    {
        new GenerateAPReport();
    }
}