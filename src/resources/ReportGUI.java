package resources;

import service.ReportGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ReportGUI extends JFrame {

    private JComboBox<String> studentDropdown;
    private JButton generateButton;
    private JLabel titleLabel;
    private JLabel selectLabel;
    private JLabel statusLabel;
    private JPanel mainPanel;

    private ReportGenerator reportGen;
    private static final String STUDENT_FILE = "data/student_information.csv";

    public ReportGUI() {
        // 1. Initialize Frame - RESTORED ORIGINAL SIZE
        setTitle("Academic Performance Report Generator");
        setSize(800, 600); // Changed back to 800x600 to match original
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 2. Initialize Components
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(40, 60, 40, 60)); // Increased padding for spacious look
        mainPanel.setBackground(new Color(245, 247, 250));

        titleLabel = new JLabel("Generate Student Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Increased font size
        titleLabel.setForeground(new Color(50, 50, 50));

        selectLabel = new JLabel("Select Student:");
        selectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Increased font size

        studentDropdown = new JComboBox<>();
        studentDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentDropdown.setPreferredSize(new Dimension(300, 40)); // Force wider dropdown
        studentDropdown.addItem("-- Select a student --");

        generateButton = new JButton("Generate Report");
        generateButton.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Increased font size
        generateButton.setBackground(new Color(0, 102, 204));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateButton.setPreferredSize(new Dimension(200, 45)); // Bigger button

        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(Color.GRAY);

        // 3. Layout Components (GridBagLayout)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Label
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(selectLabel, gbc);

        // Dropdown
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(studentDropdown, gbc);

        // Button
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(generateButton, gbc);

        // Status
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(statusLabel, gbc);

        // 4. Final Setup
        setContentPane(mainPanel);
        reportGen = new ReportGenerator();

        loadStudents();

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
    }

    // LOAD STUDENTS METHOD from CSV files
    private void loadStudents() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length < 3) {
                    continue;
                }

                String studentID = data[0];
                String firstName = data[1];
                String lastName = data[2];
                String fullName = firstName + " " + lastName;
                String displayText = studentID + " - " + fullName;

                studentDropdown.addItem(displayText);
            }

            int studentCount = studentDropdown.getItemCount() - 1;
            statusLabel.setText("Status: Loaded " + studentCount + " students");

        } catch (FileNotFoundException e) {
            statusLabel.setText("Status: Error - Student file not found!");
            JOptionPane.showMessageDialog(this,
                    "Could not find student_information.csv file.\n" +
                            "Please make sure the data folder exists.",
                    "File Not Found",
                    JOptionPane.ERROR_MESSAGE);

        } catch (IOException e) {
            statusLabel.setText("Status: Error - Could not read file!");
            JOptionPane.showMessageDialog(this,
                    "Error reading student file: " + e.getMessage(),
                    "Read Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReport() {
        int selectedIndex = studentDropdown.getSelectedIndex();

        if (selectedIndex == 0) {
            statusLabel.setText("Status: Please select a student first!");
            JOptionPane.showMessageDialog(this,
                    "Please select a student from the dropdown list before generating a report.",
                    "No Student Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedItem = (String) studentDropdown.getSelectedItem();

        if (selectedItem == null || !selectedItem.contains(" - ")) {
            statusLabel.setText("Status: Invalid selection!");
            JOptionPane.showMessageDialog(this,
                    "Invalid selection. Please select a valid student.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] parts = selectedItem.split(" - ");
        String studentID = parts[0];

        try {
            statusLabel.setText("Status: Generating report for " + studentID + "...");

            reportGen.generatePDF(studentID);

            statusLabel.setText("Status: Report generated successfully!");

            String successMessage = "PDF Report generated successfully!\n\n" +
                    "Student: " + selectedItem + "\n" +
                    "Saved to: data/report/" + studentID + "_Report.pdf";

            JOptionPane.showMessageDialog(this,
                    successMessage,
                    "Success!",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            statusLabel.setText("Status: Error generating report!");

            String errorMessage = "Sorry, there was a problem generating the report.\n\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Please try again or contact support.";

            JOptionPane.showMessageDialog(this,
                    errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ReportGUI gui = new ReportGUI();
                gui.setVisible(true);
            }
        });
    }
}