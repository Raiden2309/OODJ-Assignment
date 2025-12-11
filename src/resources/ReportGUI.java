package resources;

// - Displays a window for generating PDF reports
// - Lets user select a student from dropdown
// - Calls ReportGenerator to create the PDF

import com.intellij.uiDesigner.core. GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import service.ReportGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt. event.*;
import java.io.*;

public class ReportGUI extends JFrame {

    // GUI COMPONENTS

    private JComboBox<String> studentDropdown;

    private JButton generateButton;

    private JLabel titleLabel;
    private JLabel selectLabel;
    private JLabel statusLabel;

    private JPanel mainPanel;

    private ReportGenerator reportGen;

    private static final String STUDENT_FILE = "data/student_information.csv";

    public ReportGUI() {
        $$$setupUI$$$();

        setTitle("Academic Performance Report Generator");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);

        reportGen = new ReportGenerator();
        studentDropdown. addItem("-- Select a student --");

        replaceWithRoundedButton();

        loadStudents();

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
    }

// rounded button code
    private void replaceWithRoundedButton() {
        Container parent = generateButton. getParent();

        if (parent != null && parent.getLayout() instanceof GridLayoutManager) {
            GridLayoutManager layout = (GridLayoutManager) parent.getLayout();
            GridConstraints constraints = layout.getConstraintsForComponent(generateButton);

            // Remove the original button
            parent.remove(generateButton);

            // Create the new rounded button
            generateButton = createRoundedButton("Generate PDF Report", new Color(0, 102, 204), Color.WHITE);

            // Add the rounded button with the same constraints
            parent.add(generateButton, constraints);

            // Refresh the layout
            parent.revalidate();
            parent.repaint();
        }
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int shadowGap = 5;
                int arcSize = 50;
                int width = getWidth() - shadowGap;
                int height = getHeight() - shadowGap;

                // Handle button states (pressed, hover, normal)
                if (getModel().isPressed()) {
                    g2.translate(2, 2);
                    g2.setColor(bgColor. darker());
                } else if (getModel().isRollover()) {
                    g2.translate(-1, -1);
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                // Draw the button background
                g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);

                // Draw the button text
                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (width - fm.stringWidth(getText())) / 2;
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };

        // Button styling
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel. setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), 0, 20));
        mainPanel.setBorder(BorderFactory. createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(new Color(240, 240, 250));
        titleLabel = new JLabel();
        titleLabel. setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel. setForeground(new Color(50, 50, 100));
        titleLabel.setText("Academic Performance Report Generator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints. SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), 10, 20));
        panel1.setBackground(new Color(240, 240, 250));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints. ANCHOR_CENTER, GridConstraints. FILL_BOTH, GridConstraints. SIZEPOLICY_CAN_SHRINK | GridConstraints. SIZEPOLICY_CAN_GROW, GridConstraints. SIZEPOLICY_CAN_SHRINK | GridConstraints. SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectLabel = new JLabel();
        selectLabel.setText("Select Student:");
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel1.add(selectLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        studentDropdown = new JComboBox<>();
        studentDropdown. setFont(new Font("Arial", Font. PLAIN, 14));
        studentDropdown.setPreferredSize(new Dimension(400, 35));
        panel1.add(studentDropdown, new GridConstraints(1, 0, 1, 1, GridConstraints. ANCHOR_WEST, GridConstraints. FILL_HORIZONTAL, GridConstraints. SIZEPOLICY_CAN_SHRINK | GridConstraints. SIZEPOLICY_CAN_GROW, GridConstraints. SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateButton = new JButton();
        generateButton.setText("Generate PDF Report");
        generateButton. setFont(new Font("Arial", Font. BOLD, 16));
        generateButton.setBackground(new Color(70, 130, 180));
        generateButton.setForeground(Color.WHITE);
        generateButton.setPreferredSize(new Dimension(200, 45));
        generateButton.setFocusPainted(false);
        panel1.add(generateButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints. SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusLabel = new JLabel();
        statusLabel. setText("Status: Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        statusLabel.setForeground(new Color(100, 100, 100));
        panel1.add(statusLabel, new GridConstraints(3, 0, 1, 1, GridConstraints. ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    // LOAD STUDENTS METHOD from CSV files
    private void loadStudents() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_FILE))) {

            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {

                // Split the line by comma to get individual values
                String[] data = line.split(",");

                // Check for 3 columns minimum
                if (data.length < 3) {
                    continue;
                }

                // Get the student ID (first column, index 0)
                String studentID = data[0];

                // Get the student name (first name + last name)
                String firstName = data[1];
                String lastName = data[2];
                String fullName = firstName + " " + lastName;

                String displayText = studentID + " - " + fullName;

                studentDropdown. addItem(displayText);
            }

            int studentCount = studentDropdown.getItemCount() - 1;  // Exclude the default option
            statusLabel.setText("Status:  Loaded " + studentCount + " students");

        } catch (FileNotFoundException e) {
            statusLabel.setText("Status: Error - Student file not found!");
            JOptionPane.showMessageDialog(this,
                    "Could not find student_information.csv file.\n" +
                            "Please make sure the data folder exists.",
                    "File Not Found",
                    JOptionPane.ERROR_MESSAGE);

        } catch (IOException e) {
            statusLabel. setText("Status: Error - Could not read file!");
            JOptionPane.showMessageDialog(this,
                    "Error reading student file: " + e.getMessage(),
                    "Read Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReport() {

        // CHECK IF STUDENT IS SELECTED

        int selectedIndex = studentDropdown.getSelectedIndex();

        if (selectedIndex == 0) {
            // User didn't select a student - show warning
            statusLabel. setText("Status: Please select a student first!");

            JOptionPane.showMessageDialog(this,
                    "Please select a student from the dropdown list before generating a report.",
                    "No Student Selected",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }


        // GET SELECTED STUDENT
        String selectedItem = (String) studentDropdown.getSelectedItem();

        // check
        if (selectedItem == null || ! selectedItem.contains(" - ")) {
            statusLabel.setText("Status: Invalid selection!");
            JOptionPane.showMessageDialog(this,
                    "Invalid selection.  Please select a valid student.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // EXTRACT STUDENT ID
        String[] parts = selectedItem. split(" - ");
        String studentID = parts[0];

        // GENERATE THE PDF
        try {
            statusLabel. setText("Status:  Generating report for " + studentID + "...");

            // Call the ReportGenerator to create the PDF
            reportGen. generatePDF(studentID);

            statusLabel. setText("Status: Report generated successfully!");

            // The report is saved in /data/report/ folder
            String successMessage = "PDF Report generated successfully!\n\n" +
                    "Student: " + selectedItem + "\n" +
                    "Saved to: data/report/" + studentID + "_Report.pdf";

            JOptionPane.showMessageDialog(this,
                    successMessage,
                    "Success! ",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            // HANDLE ERRORS

            statusLabel.setText("Status: Error generating report!");

            String errorMessage = "Sorry, there was a problem generating the report.\n\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Please try again or contact support. ";

            JOptionPane.showMessageDialog(this,
                    errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }
    }

    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }


    // MAIN METHOD
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the GUI window
                ReportGUI gui = new ReportGUI();

                gui. setVisible(true);
            }
        });
    }

}
