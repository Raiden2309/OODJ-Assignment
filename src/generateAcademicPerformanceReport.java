import javax.swing.*;
import data_access.DataAccess;
import org.jdesktop.swingx.autocomplete.*;
import service.GenerateReportPDF;

import java.awt.event.*;
import java.util.List;

public class generateAcademicPerformanceReport extends JFrame {
    private JPanel panel1;
    private JComboBox idCombobox;
    private JButton selectButton;
    private JLabel label1;
    private JLabel idLabel;
    private JLabel label2;
    private JLabel nameLabel;
    private JLabel label3;
    private JLabel programLabel;

    public generateAcademicPerformanceReport()
    {
        DataAccess data = new DataAccess();
        List<String[]> students = data.getStudents();
        idCombobox.addItem("-- Select student --");

        for (String[] student : students)
        {
            String s = String.format("%s - %s %s", student[0], student[1], student[2]);
            idCombobox.addItem(s);
        }
        AutoCompleteDecorator.decorate(idCombobox);

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
                    String selectedItem = (String) e.getItem();

                    if (selectedItem != null && selectedItem.contains("-"))
                    {
                        String id = selectedItem.split("-")[0].trim();
                        String name = selectedItem.split("-")[1].trim();
                        String enrolled_program = "";
                        nameLabel.setText(id);
                        idLabel.setText(name);

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
                    else
                    {
                        nameLabel.setText(null);
                        idLabel.setText(null);
                        programLabel.setText(null);
                    }
                }
            }
        });

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dialog;
                String title;
                int pane;
                if (idCombobox.getSelectedIndex() != 0)
                {
                    GenerateReportPDF pdf = new GenerateReportPDF();
                    String studentId = idCombobox.getSelectedItem().toString().split("-")[0].trim();
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
    }

    public static void main(String[] args)
    {
        new generateAcademicPerformanceReport();
    }
}
