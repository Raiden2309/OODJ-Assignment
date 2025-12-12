package resources;

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

public class CheckRecoveryEligibility extends JFrame {
    private JComboBox<String> idCombobox;
    private JButton checkEligibilityButton;

    // Static Labels
    private JLabel labelTitle;
    private JLabel label1; // Select Student
    private JLabel label2; // First Name
    private JLabel label3; // Last Name
    private JLabel label4; // Major
    private JLabel label5; // Year
    private JLabel label6; // Eligibility

    // Dynamic Labels (Data Display)
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel majorLabel;
    private JLabel yearLabel;

    private JLabel eligibilityLabel;

    final Font txtFont = new Font("Arial", Font.PLAIN, 14);
    final Font categoryFont = new Font("Arial", Font.BOLD, 14);
    final Color eligibleColour = new Color(0, 188, 0);
    final Color ineligibleColour = Color.RED;

    // Backend Access
    StudentDAO studentDAO = new StudentDAO();
    List<Student> students = studentDAO.loadAllStudents();

    // List to hold the full data for filtering
    private List<String> allItems = new ArrayList<>();

    public CheckRecoveryEligibility() {
        // 1. INITIALIZE COMPONENTS
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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
        label6 = new JLabel("Eligibility Result:");

        label1.setFont(categoryFont);
        label2.setFont(categoryFont);
        label3.setFont(categoryFont);
        label4.setFont(categoryFont);
        label5.setFont(categoryFont);
        label6.setFont(categoryFont);

        // Dynamic Labels
        firstNameLabel = new JLabel("-");
        lastNameLabel = new JLabel("-");
        majorLabel = new JLabel("-");
        yearLabel = new JLabel("-");
        eligibilityLabel = new JLabel("Unknown");

        firstNameLabel.setFont(txtFont);
        lastNameLabel.setFont(txtFont);
        majorLabel.setFont(txtFont);
        yearLabel.setFont(txtFont);
        eligibilityLabel.setFont(txtFont);

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
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        JLabel lblLogo = new JLabel(logoIcon);

        labelTitle = new JLabel("Check Student CRP Eligibility", SwingConstants.CENTER);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel logoTitlePanel = new JPanel(new BorderLayout());
        logoTitlePanel.setBackground(new Color(229, 215, 139));
        logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        logoTitlePanel.add(lblLogo, BorderLayout.WEST);
        logoTitlePanel.add(labelTitle, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(229, 215, 139));
        topPanel.add(logoTitlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        inputPanel.add(label1);
        inputPanel.add(idCombobox);

        inputPanel.add(label2);
        inputPanel.add(firstNameLabel);

        inputPanel.add(label3);
        inputPanel.add(lastNameLabel);

        inputPanel.add(label4);
        inputPanel.add(majorLabel);

        inputPanel.add(label5);
        inputPanel.add(yearLabel);

        inputPanel.add(label6);
        inputPanel.add(eligibilityLabel);

        // Empty placeholder for spacing
        inputPanel.add(new JLabel(""));

        add(inputPanel, BorderLayout.CENTER);

        checkEligibilityButton.setBorder(BorderFactory.createEmptyBorder(20,10,10,10));
        add(checkEligibilityButton,BorderLayout.SOUTH);

        getContentPane().setBackground(new Color(229, 215, 139));
        inputPanel.setBackground(new Color(229, 215, 139));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CRS - Student Eligibility Check");
        setSize(1000, 700);
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
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getStudentId().compareToIgnoreCase(s2.getStudentId());
            }
        });

        // Add formatted items to memory list
        for (Student student : students) {
            String s = String.format("%s - %s", student.getStudentId(), student.getFullName());
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
            eligibilityLabel.setText("Unknown");
            eligibilityLabel.setForeground(Color.BLACK);
            return;
        }

        // PARSE CHANGE: Extract ID from "ID - Name" format
        if (selectedItem.contains(" - ")) {
            try {
                String id = selectedItem.split(" - ")[0].trim();

                for (Student student : students) {
                    if (student.getStudentId().equals(id)) {
                        firstNameLabel.setText(student.getFirstName());
                        lastNameLabel.setText(student.getLastName());
                        majorLabel.setText(student.getMajor());
                        yearLabel.setText(student.getAcademicYear());

                        eligibilityLabel.setText("Unknown");
                        eligibilityLabel.setForeground(Color.BLACK);
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error:" + e);
                e.printStackTrace();
            }
        }

        // Reset
        firstNameLabel.setText("-");
        lastNameLabel.setText("-");
        majorLabel.setText("-");
        yearLabel.setText("-");
        eligibilityLabel.setText("Unknown");
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
        EnrolledCourseDAO enrolledCourseDAO = new EnrolledCourseDAO();

        // 2. Load Data
        List<Student> allStudents = studentDAO.loadAllStudents();
        enrolledCourseDAO.loadRecords(allStudents);

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
        String eligibilityStatus;

        // 5. Update UI
        if (checker.isEligible())
        {
            eligibilityLabel.setText("ELIGIBLE FOR RECOVERY");
            eligibilityLabel.setForeground(eligibleColour);
            eligibilityStatus = "ELIGIBLE";
        }
        else
        {
            eligibilityLabel.setText("NOT ELIGIBLE");
            eligibilityLabel.setForeground(ineligibleColour);
            eligibilityStatus = "NOT ELIGIBLE";
        }
        String msg = String.format("Student: %s\nCGPA: %.2f\nFailed Courses: %d\n\nStatus: %s",
                targetStudent.getFullName(),
                targetStudent.getAcademicProfile().getCGPA(),
                targetStudent.getAcademicProfile().getTotalFailedCourse(),
                eligibilityStatus);

        JOptionPane.showMessageDialog(null, msg, "Check Result", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main (String[]args){
        SwingUtilities.invokeLater(() -> new CheckRecoveryEligibility());
    }
}