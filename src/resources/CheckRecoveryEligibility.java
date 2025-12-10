package resources;

import data_access.DataAccess;
import service.StudentDAO;
import service.AcademicRecordDAO;
import academic.EligibilityCheck;
import domain.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CheckRecoveryEligibility extends JFrame {
    private JPanel panel1;
    private JComboBox<String> idCombobox;
    private JButton checkEligibilityButton;

    // Static Labels
    private JLabel label1; // Select Student
    private JLabel label2; // First Name
    private JLabel label3; // Last Name
    private JLabel label4; // Major
    private JLabel label5; // Year

    // Dynamic Labels (Data Display)
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel majorLabel;
    private JLabel yearLabel;

    private JLabel eligibilityLabel;

    final Color eligibleColour = new Color(0, 188, 0);
    final Color ineligibleColour = Color.RED;

    // Backend Access
    DataAccess data = new DataAccess();
    List<String[]> students = data.getStudents();

    // List to hold the full data for filtering
    private List<String> allItems = new ArrayList<>();

    public CheckRecoveryEligibility() {
        // 1. INITIALIZE COMPONENTS
        panel1 = new JPanel(new GridLayout(9, 2, 10, 10));
        panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Setup Dropdown
        idCombobox = new JComboBox<>();
        idCombobox.setEditable(true);

        checkEligibilityButton = new JButton("Check Eligibility");

        // Static Labels
        label1 = new JLabel("Search Student (ID - Name):");
        label2 = new JLabel("First Name:");
        label3 = new JLabel("Last Name:");
        label4 = new JLabel("Major:");
        label5 = new JLabel("Year:");

        // Dynamic Labels
        firstNameLabel = new JLabel("-");
        lastNameLabel = new JLabel("-");
        majorLabel = new JLabel("-");
        yearLabel = new JLabel("-");
        eligibilityLabel = new JLabel("Status: Unknown");
        eligibilityLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // 2. POPULATE & ENABLE SEARCH
        loadAllItems(); // Load data into memory
        addComboboxItems(allItems); // Show all initially

        // CUSTOM FILTER LOGIC
        // This makes the dropdown actually SHRINK to show only matches
        JTextField editor = (JTextField) idCombobox.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> filterList(editor.getText()));
            }
        });

        // 3. BUILD LAYOUT
        panel1.add(label1);
        panel1.add(idCombobox);

        panel1.add(label2);
        panel1.add(firstNameLabel);

        panel1.add(label3);
        panel1.add(lastNameLabel);

        panel1.add(label4);
        panel1.add(majorLabel);

        panel1.add(label5);
        panel1.add(yearLabel);

        panel1.add(new JLabel("Eligibility Result:"));
        panel1.add(eligibilityLabel);

        // Empty placeholder for spacing
        panel1.add(new JLabel(""));
        panel1.add(new JLabel(""));

        panel1.add(checkEligibilityButton);

        setContentPane(panel1);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CRS - Student Eligibility Check");
        setSize(650, 500);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        setVisible(true);

        // 4. LISTENERS

        // Update labels only when a valid item is SELECTED
        idCombobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Only update if the action command is 'comboBoxChanged' (selection)
                if ("comboBoxChanged".equals(e.getActionCommand())) {
                    Object selected = idCombobox.getSelectedItem();
                    if (selected != null) {
                        updateStudentDetails(selected.toString());
                    }
                }
            }
        });

        // Backend Connection
        checkEligibilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idCombobox.getItemCount() == 0 || idCombobox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "Please select a valid student first.");
                    return;
                }

                String currentSelection = idCombobox.getSelectedItem().toString();
                if (!currentSelection.contains(" - ")) {
                    JOptionPane.showMessageDialog(null, "Please select a valid student from the list.");
                    return;
                }

                performEligibilityCheck();
            }
        });
    }

    private void loadAllItems () {
        // SORTING: Sort by ID
        Collections.sort(students, new Comparator<String[]>() {
            @Override
            public int compare(String[] s1, String[] s2) {
                return s1[0].compareToIgnoreCase(s2[0]);
            }
        });

        // Add formatted items to memory list
        for (String[] student : students) {
            String s = String.format("%s - %s %s", student[0], student[1], student[2]);
            allItems.add(s);
        }
    }

    private void addComboboxItems (List < String > itemsToShow) {
        idCombobox.setModel(new DefaultComboBoxModel<>(itemsToShow.toArray(new String[0])));
        idCombobox.setSelectedItem(null); // Clear selection initially
    }

    // The Filtering Logic
    private void filterList (String text){
        if (text.isEmpty()) {
            // If empty, allow showing full list on dropdown click (optional)
            // Or just keep current view.
            // For now, let's allow refilling the list if user clears text.
            if (idCombobox.getItemCount() != allItems.size()) {
                addComboboxItems(allItems);
            }
            return;
        }

        List<String> filteredItems = new ArrayList<>();
        for (String item : allItems) {
            // Case-insensitive filtering.
            // "Starts With" logic for ID (S00...) or Name searching
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filteredItems.add(item);
            }
        }

        // Only update if the list changed to prevent flickering
        if (filteredItems.size() != idCombobox.getItemCount()) {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(filteredItems.toArray(new String[0]));
            idCombobox.setModel(model);
            idCombobox.setSelectedItem(text); // Keep user's text

            // Show popup if we have matches
            if (!filteredItems.isEmpty() && !idCombobox.isPopupVisible()) {
                idCombobox.showPopup();
            } else if (filteredItems.isEmpty()) {
                idCombobox.hidePopup();
            }
        }

        // Restore text cursor
        JTextField editor = (JTextField) idCombobox.getEditor().getEditorComponent();
        editor.setText(text);
    }

    private void updateStudentDetails (String selectedItem){
        if (selectedItem == null || selectedItem.isEmpty()) {
            firstNameLabel.setText("-");
            lastNameLabel.setText("-");
            majorLabel.setText("-");
            yearLabel.setText("-");
            eligibilityLabel.setText("Status: Unknown");
            eligibilityLabel.setForeground(Color.BLACK);
            return;
        }

        // PARSE CHANGE: Extract ID from "ID - Name" format
        if (selectedItem.contains(" - ")) {
            try {
                String id = selectedItem.split(" - ")[0].trim();

                for (String[] student : students) {
                    if (student[0].equals(id)) {
                        firstNameLabel.setText(student[1]);
                        lastNameLabel.setText(student[2]);
                        majorLabel.setText(student[3]);
                        yearLabel.setText(student[4]);

                        eligibilityLabel.setText("Status: Unknown");
                        eligibilityLabel.setForeground(Color.BLACK);
                        return;
                    }
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Reset
        firstNameLabel.setText("-");
        lastNameLabel.setText("-");
        majorLabel.setText("-");
        yearLabel.setText("-");
        eligibilityLabel.setText("Status: Unknown");
        eligibilityLabel.setForeground(Color.BLACK);
    }

    // --- BACKEND LOGIC ---
    private void performEligibilityCheck () {
        Object selectedObj = idCombobox.getSelectedItem();
        if (selectedObj == null) return;

        String selectedText = selectedObj.toString();
        if (!selectedText.contains(" - ")) return;

        String studentID = selectedText.split(" - ")[0].trim();

        // 1. Initialize DAOs
        StudentDAO studentDAO = new StudentDAO();
        AcademicRecordDAO recordDAO = new AcademicRecordDAO();

        // 2. Load Data
        List<Student> allStudents = studentDAO.loadAllStudents();
        recordDAO.loadRecords(allStudents);

        recordDAO.loadRecords(allStudents);

        // 3. Find target
        Student targetStudent = null;
        for (Student s : allStudents) {
            if (s.getUserID().equals(studentID)) {
                targetStudent = s;
                break;
            }
        }

        if (targetStudent == null) {
            JOptionPane.showMessageDialog(null, "Error: Student data not found in backend records.");
            return;
        }

        // 4. Run Check
        EligibilityCheck checker = targetStudent.checkEligibility();

        // 5. Update UI
        if (checker.isEligible()) {
            eligibilityLabel.setText("ELIGIBLE FOR RECOVERY");
            eligibilityLabel.setForeground(eligibleColour);

            String msg = String.format("Student: %s\nCGPA: %.2f\nFailed Courses: %d\n\nStatus: ELIGIBLE",
                    targetStudent.getFullName(),
                    targetStudent.getAcademicProfile().getCGPA(),
                    targetStudent.getAcademicProfile().getTotalFailedCourse());

            JOptionPane.showMessageDialog(null, msg, "Check Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            eligibilityLabel.setText("NOT ELIGIBLE");
            eligibilityLabel.setForeground(ineligibleColour);

            String msg = String.format("Student: %s\nCGPA: %.2f\nFailed Courses: %d\n\nStatus: NOT ELIGIBLE",
                    targetStudent.getFullName(),
                    targetStudent.getAcademicProfile().getCGPA(),
                    targetStudent.getAcademicProfile().getTotalFailedCourse());

            JOptionPane.showMessageDialog(null, msg, "Check Result", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main (String[]args){
        SwingUtilities.invokeLater(() -> new CheckRecoveryEligibility());
    }
}