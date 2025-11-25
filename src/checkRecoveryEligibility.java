import data_access.DataAccess;
import domain.StudentPerformance;
import service.EligibilityCheck;
import service.GenerateReportPDF;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class checkRecoveryEligibility extends JFrame {

    private JPanel panel1;
    private JComboBox idCombobox;
    private JButton checkEligibilityButton;
    private JButton generateReportButton;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel programLabel;
    private JLabel eligibilityLabel;

    final Color eligibleColour = new Color(0, 188, 0);
    final Color eligibleHover = new Color(0, 133, 0);
    final Color ineligibleColour = Color.RED;

    DataAccess data = new DataAccess();
    List<String[]> students = data.getStudents();

    public checkRecoveryEligibility()
    {
        AutoCompleteDecorator.decorate(idCombobox);
        addComboboxItems();

        setContentPane(panel1);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 400);
        setResizable(false);
        setVisible(true);

        idCombobox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                {
                    String selectedItem = e.getItem().toString();

                    if (selectedItem != null && selectedItem.contains("-"))
                    {
                        fillDetails(selectedItem);
                    }
                    else
                    {
                        nameLabel.setText(null);
                        idLabel.setText(null);
                        programLabel.setText(null);
                        eligibilityLabel.setText(null);
                    }
                }
            }
        });

        checkEligibilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentPerformance performance = new StudentPerformance(idLabel.getText());
                performance.getPerformance(data);
                EligibilityCheck check = new EligibilityCheck();
                boolean[] eligibilityResult = check.isEligible(performance);

                if (idCombobox.getSelectedIndex() != 0)
                {
                    String eligibilityMsg;

                    if (eligibilityResult[0])
                    {
                        eligibilityLabel.setForeground(eligibleColour);
                        eligibilityMsg = "ELIGIBLE FOR COURSE RECOVERY PROGRAM<br>(CLICK FOR FURTHER ACTION)<br>";
                    }
                    else
                    {
                        eligibilityLabel.setForeground(ineligibleColour);
                        eligibilityMsg = "NOT ELIGIBLE FOR COURSE RECOVERY PROGRAM<br>";
                    }
                    eligibilityLabel.setText(String.format("<html><u>%s</u></html>", eligibilityMsg));
                }
            }
        });

        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dialog;
                String title;
                int pane;
                if (idCombobox.getSelectedIndex() != 0)
                {
                    GenerateReportPDF pdf = new GenerateReportPDF();
                    String studentId = idLabel.getText();
                    dialog = String.format("Academic performance report for student %s%ndownloaded to Downloads folder.", idCombobox.getSelectedItem().toString());
                    title = "Success!";
                    pane = JOptionPane.INFORMATION_MESSAGE;

                    pdf.createDocument(studentId);
                }
                else
                {
                    dialog = "Please select a student to continue.";
                    title = "Error";
                    pane = JOptionPane.ERROR_MESSAGE;
                }
                JOptionPane.showMessageDialog(null, dialog, title, pane);
            }
        });

        generateReportButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                generateReportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                generateReportButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        eligibilityLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (eligibilityLabel.getForeground() == eligibleHover)
                {
                    StudentPerformance performance = new StudentPerformance(idLabel.getText());
                    performance.getPerformance(data);
                    EligibilityCheck check = new EligibilityCheck();
                    boolean[] eligibilityResult = check.isEligible(performance);

                    String[] reasonsForEligibility = {"", ""};
                    String dialogMsg;

                    if (eligibilityResult[1])
                    {
                        reasonsForEligibility[0] = String.format("- No. of failed courses > 3 ( %d )<br>", performance.getFailedCourses());
                    }
                    if (eligibilityResult[2])
                    {
                        reasonsForEligibility[1] = String.format("- CGPA &lt; 2.00 ( %.2f )<br>", performance.getCgpa());
                    }
                    dialogMsg = "<html>Reason(s) for eligibility:<br>";

                    for (String reason : reasonsForEligibility)
                    {
                        dialogMsg += reason;
                    }
                    int confirmation = JOptionPane.showConfirmDialog(null, dialogMsg + "<br>Allow student to register for Course Recovery Program?</html>");

                    String dialog;
                    String title;
                    int pane;

                    if (confirmation == JOptionPane.YES_OPTION)
                    {
                        dialog = "Student is now able to register for Course Recovery Program.";
                        title = "Success!";
                        updateEligibility();
                    }
                    else
                    {
                        dialog = "Action cancelled.";
                        title = "Cancelled";
                    }
                    pane = JOptionPane.INFORMATION_MESSAGE;
                    JOptionPane.showMessageDialog(null, dialog, title, pane);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (eligibilityLabel.getForeground() == eligibleColour || eligibilityLabel.getForeground() == eligibleHover)
                {
                    eligibilityLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    eligibilityLabel.setForeground(eligibleHover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (eligibilityLabel.getForeground() == eligibleHover)
                {
                    eligibilityLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    eligibilityLabel.setForeground(eligibleColour);
                }
            }
        });
    }

    public void addComboboxItems()
    {
        idCombobox.addItem("-- Select student --");

        for (String[] student : students)
        {
            String s = String.format("%s - %s %s", student[0], student[1], student[2]);
            idCombobox.addItem(s);
        }
    }

    public void fillDetails(String selectedItem)
    {
        String id = selectedItem.split("-")[0].trim();
        String name = selectedItem.split("-")[1].trim();
        String enrolled_program = "";
        idLabel.setText(id);
        nameLabel.setText(name);

        for (String[] student : students)
        {
            if (student[0].equals(id))
            {
                String[] program = data.getPrograms(student).getFirst();
                enrolled_program = program[2] + " in " + program[1];
                break;
            }
        }
        programLabel.setText(enrolled_program);
    }

    public void updateEligibility()
    {
        for (String[] student : students)
        {
            if (student[0].equals(idLabel.getText()))
            {
                student[5] = String.valueOf(1);
                break;
            }
        }
        updateCsvFile();
    }

    public void updateCsvFile()
    {
        final String STUDENT_INFO = "data/student_information.csv";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENT_INFO)))
        {
            bw.write("StudentID, FirstName, LastName, ProgramID, Email, RecoveryEligibility\n");
            for (String[] student : students)
            {
                String line = String.join(",", student);
                bw.write(line);
                bw.newLine();
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
    }

    public static void main(String[] args)
    {
        new checkRecoveryEligibility();
    }
}

