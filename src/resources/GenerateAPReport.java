package resources;

import javax.swing.*;
import data_access.DataAccess;
import org.jdesktop.swingx.autocomplete.*;
import report.GenerateReportPDF;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GenerateAPReport extends JFrame {
    private JPanel panel1;
    private JComboBox idCombobox;
    private JButton selectButton;
    private JLabel label1; // "Select Student:"
    private JLabel idLabel; // Shows ID
    private JLabel label2; // "Student Name:"
    private JLabel nameLabel; // Shows Name
    private JLabel label3; // "Program:"
    private JLabel programLabel; // Shows Program

    public GenerateAPReport()
    {
        // 1. INITIALIZE ALL COMPONENTS (Fixes NullPointerExceptions)
        // We use a GridLayout (5 rows, 2 columns) to organize them nicely.
        panel1 = new JPanel(new GridLayout(5, 2, 10, 10));
        panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        idCombobox = new JComboBox();
        selectButton = new JButton("Generate PDF Report"); // Initialize the button!

        // Initialize Labels
        label1 = new JLabel("Select Student:");
        label2 = new JLabel("Student Name:");
        label3 = new JLabel("Enrolled Program:");

        idLabel = new JLabel("-"); // Placeholder text
        nameLabel = new JLabel("-");
        programLabel = new JLabel("-");

        // 2. LOAD DATA
        DataAccess data = new DataAccess();
        java.util.List<String[]> students = data.getStudents();
        idCombobox.addItem("-- Select student --");

        for (String[] student : students)
        {
            String s = String.format("%s - %s %s", student[0], student[1], student[2]);
            idCombobox.addItem(s);
        }

        // Make sure you have the swingx library added for this line:
        AutoCompleteDecorator.decorate(idCombobox);

        // 3. ADD COMPONENTS TO PANEL (Fixes Blank Window)
        // The order matters in GridLayout!
        panel1.add(label1);
        panel1.add(idCombobox);

        panel1.add(label2);
        panel1.add(nameLabel); // Shows dynamic name

        panel1.add(new JLabel("Student ID:")); // Extra label for clarity
        panel1.add(idLabel);   // Shows dynamic ID

        panel1.add(label3);
        panel1.add(programLabel); // Shows dynamic program

        panel1.add(new JLabel("")); // Empty placeholder
        panel1.add(selectButton);

        // 4. FRAME SETTINGS
        setContentPane(panel1);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Academic Performance Report");
        setSize(600, 400); // Made slightly wider
        setLocationRelativeTo(null); // Centers window on screen
        setVisible(true);

        // 5. EVENT LISTENERS
        idCombobox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                {
                    String selectedItem = (String) e.getItem();

                    if (selectedItem != null && selectedItem.contains("-"))
                    {
                        // Use " - " to split safely just in case a name has a hyphen
                        String[] parts = selectedItem.split(" - ");
                        if(parts.length < 2) return; // Safety check

                        String id = parts[0].trim();
                        // The rest is the name (we don't strictly need to parse name from string since we lookup by ID)

                        String enrolled_program = "";

                        for (String[] student : students)
                        {
                            if (student[0].equals(id))
                            {
                                // Update Labels from real data
                                idLabel.setText(student[0]);
                                nameLabel.setText(student[1] + " " + student[2]);

                                // Get Program
                                List<String[]> programs = data.getPrograms(student);
                                if (!programs.isEmpty()) {
                                    String[] program = programs.get(0);
                                    enrolled_program = program[1] + " (" + program[2] + ")";
                                } else {
                                    enrolled_program = "No Program Found";
                                }
                                break;
                            }
                        }
                        programLabel.setText(enrolled_program);
                    }
                    else
                    {
                        nameLabel.setText("-");
                        idLabel.setText("-");
                        programLabel.setText("-");
                    }
                }
            }
        });

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idCombobox.getSelectedIndex() != 0)
                {
                    try {
                        GenerateReportPDF pdf = new GenerateReportPDF();

                        // Safely get ID
                        String selectedText = idCombobox.getSelectedItem().toString();
                        String studentId = selectedText.split(" - ")[0].trim();

                        // Call your PDF generator
                        pdf.createDocument(studentId);

                        String message = String.format("Report for %s successfully generated!\nSaved to Downloads folder.", studentId);
                        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception ex) {
                        ex.printStackTrace();
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
        // Run on Event Dispatch Thread for thread safety (best practice)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GenerateAPReport();
            }
        });
    }
}