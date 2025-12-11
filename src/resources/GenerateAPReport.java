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
    private JLabel labelTitle;
    private JLabel label1; // "Select Student:"
    private JLabel label2; // "Student ID:"
    private JLabel idLabel; // Shows Student ID
    private JLabel label3; // "Student Name:"
    private JLabel nameLabel; // Shows Name
    private JLabel label4; // "Major:"
    private JLabel majorLabel; // Shows Major
    private JLabel label5; // "Year:"
    private JLabel yearLabel; // Shows Academic Year

    final Font txtFont = new Font("Arial", Font.PLAIN, 14);
    final Font categoryFont = new Font("Arial", Font.BOLD, 14);

    public GenerateAPReport()
    {
        // 1. INITIALIZE ALL COMPONENTS (Fixes NullPointerExceptions)
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        idCombobox = new JComboBox();
        selectButton = new JButton("Generate PDF Report"); // Initialize the button!

        // Initialize Labels
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

        idLabel = new JLabel("-"); // Placeholder text
        nameLabel = new JLabel("-");
        majorLabel = new JLabel("-");
        yearLabel = new JLabel("-");

        idLabel.setFont(txtFont);
        nameLabel.setFont(txtFont);
        majorLabel.setFont(txtFont);
        yearLabel.setFont(txtFont);

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

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        JLabel lblLogo = new JLabel(logoIcon);

        labelTitle = new JLabel("Generate Academic Progress Report PDFs", SwingConstants.CENTER);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel logoTitlePanel = new JPanel(new BorderLayout());
        logoTitlePanel.setBackground(new Color(229, 215, 139));
        logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        logoTitlePanel.add(lblLogo, BorderLayout.WEST);
        logoTitlePanel.add(labelTitle, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(229, 215, 139));
        topPanel.add(logoTitlePanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        inputPanel.add(label1);
        inputPanel.add(idCombobox);

        inputPanel.add(label2);
        inputPanel.add(idLabel);   // Shows dynamic ID

        inputPanel.add(label3);
        inputPanel.add(nameLabel); // Shows dynamic name

        inputPanel.add(label4);
        inputPanel.add(majorLabel); // Shows dynamic major

        inputPanel.add(label5);
        inputPanel.add(yearLabel); // Shows dynamic academic year

        inputPanel.add(new JLabel("")); // Empty placeholder

        add(selectButton, BorderLayout.SOUTH);

        // 4. FRAME SETTINGS
        getContentPane().setBackground(new Color(229, 215, 139));
        inputPanel.setBackground(new Color(229, 215, 139));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CRS - Generate Academic Performance Report");
        setSize(1000, 700); // Made slightly wider
        setLocationRelativeTo(null); // Centers window on screen
        setResizable(false);
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

                        for (String[] student : students)
                        {
                            if (student[0].equals(id))
                            {
                                // Update Labels from real data
                                idLabel.setText(student[0]);
                                nameLabel.setText(student[1] + " " + student[2]);
                                majorLabel.setText(student[3]);
                                yearLabel.setText(student[4]);
                            }
                        }
                    }
                    else
                    {
                        nameLabel.setText("-");
                        idLabel.setText("-");
                        majorLabel.setText("-");
                        yearLabel.setText("-");
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