package ui;

import academic.Recommendation;
import domain.User;
import domain.Student;
import service.CourseCatalog;
import service.RecommendationDAO;
import service.StudentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class RecommendationEntry extends JFrame{
    private JTextField txtStudentID;
    private JTable tblRecommendation;
    private JTextField txtCourseID;
    private JTextField txtTimeLine;
    private JTextField txtDeadline;
    private JLabel lblCourseID;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnRemove;
    private JButton btnList;
    private JButton btnBack;
    private JLabel lblStudentID;
    private JLabel lblTimeline;
    private JLabel lblDeadline;
    private JLabel lblDescription;
    private JLabel lblStatus;
    private JLabel lblRecommendation;
    private JComboBox<String> cmbDescription;
    private JComboBox<String> cmbStatus;
    private JTextField txtRecID;
    private JLabel lblRecID;
    private JButton btnMilestone;
    private JButton btnFailed;
    private JButton btnRecovery;

    private JLabel statusMessageLabel;

    private DefaultTableModel tableModel;
    private RecommendationDAO recommendationDAO;
    private User loggedInUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color RED_COLOR = new Color(220, 53, 69);
    private final Color TEXT_COLOR = Color.WHITE;

    public RecommendationEntry(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.recommendationDAO = new RecommendationDAO();

        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon(getClass().getResource("/resources/bg3.png")).getImage();
                } catch (Exception e) {
                    setBackground(Color.WHITE);
                }
                setOpaque(false);
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        setTitle("Recommendation Entry");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        ImageIcon logoIcon = null;
        try {
            Image logoImage = new ImageIcon(getClass().getResource("/resources/apulogo.png")).getImage();
            Image scaledLogo = logoImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(scaledLogo);
        } catch (Exception e) {
            System.out.println("Logo image not found at /resources/apulogo.png");
        }

        JLabel logoLabel = new JLabel(logoIcon);
        headerPanel.add(logoLabel);

        JLabel titleLabel = new JLabel("RECOMMENDATION ENTRY");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 35));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(lblRecID = new JLabel("Recommendation ID:"));
        txtRecID = new JTextField();
        inputPanel.add(txtRecID);

        inputPanel.add(lblStudentID = new JLabel("Student ID:"));
        txtStudentID = new JTextField();
        inputPanel.add(txtStudentID);

        inputPanel.add(lblCourseID = new JLabel("Course ID:"));
        txtCourseID = new JTextField();
        inputPanel.add(txtCourseID);

        inputPanel.add(lblTimeline = new JLabel("Timeline (String):"));
        txtTimeLine = new JTextField();
        inputPanel.add(txtTimeLine);

        inputPanel.add(lblDeadline = new JLabel("Deadline (YYYY-MM-DD):"));
        txtDeadline = new JTextField();
        inputPanel.add(txtDeadline);

        inputPanel.add(lblDescription = new JLabel("Description:"));
        cmbDescription = new JComboBox<>(new String[]{"Exam", "Assignment", "Project", "Overall Fail"});
        inputPanel.add(cmbDescription);

        inputPanel.add(lblStatus = new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Pending", "Approved", "Rejected", "Completed", "Cancelled"});
        inputPanel.add(cmbStatus);

        topPanel.add(inputPanel, BorderLayout.CENTER);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        btnAdd = createRoundedButton("Add", ACCENT_COLOR, TEXT_COLOR);
        btnUpdate = createRoundedButton("Update", ACCENT_COLOR, TEXT_COLOR);
        btnRemove = createRoundedButton("Remove", ACCENT_COLOR, TEXT_COLOR);
        btnList = createRoundedButton("List All", ACCENT_COLOR, TEXT_COLOR);
        btnBack = createRoundedButton("Back", RED_COLOR, TEXT_COLOR);

        if (!(loggedInUser instanceof Student)) {
            buttonPanel.add(btnAdd);
            buttonPanel.add(btnUpdate);
            buttonPanel.add(btnRemove);
            buttonPanel.add(btnList);
        }

        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new CardLayout());
        centerPanel.setOpaque(false);

        tableModel = new DefaultTableModel(new String[]{"RecID", "StudentID", "CourseID", "Timeline", "Deadline", "Description", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblRecommendation = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(tblRecommendation);
        centerPanel.add(tableScroll, "TABLE");

        statusMessageLabel = new JLabel("No recommendations found for you.", SwingConstants.CENTER);
        statusMessageLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        statusMessageLabel.setForeground(Color.DARK_GRAY);
        centerPanel.add(statusMessageLabel, "EMPTY");

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        tblRecommendation.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblRecommendation.getSelectedRow();
                if (selectedRow != -1) {
                    populateFieldsFromTable(selectedRow);
                }
            }
        });

        btnAdd.addActionListener(e -> addRecommendation());
        btnUpdate.addActionListener(e -> updateRecommendation());
        btnRemove.addActionListener(e -> removeRecommendation());
        btnList.addActionListener(e -> listRecommendation());

        btnBack.addActionListener(e -> {
            dispose();
        });

        btnFailed = new JButton("Failed Component");
        btnMilestone = new JButton("Milestone");
        btnRecovery = new JButton("Recovery");

        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS));
        leftButtonPanel.setBackground(new Color(229, 205, 103)); // Yellowish

        if (loggedInUser instanceof Student) {
            setupStudentView();
        } else {
            listRecommendation();
            ((CardLayout) centerPanel.getLayout()).show(centerPanel, "TABLE");
        }

        setVisible(true);
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowGap = 3;
                int arcSize = 30;
                int width = getWidth() - shadowGap;
                int height = getHeight() - shadowGap;
                g2.setColor(new Color(200, 200, 200));
                g2.fillRoundRect(shadowGap, shadowGap, width, height, arcSize, arcSize);
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);
                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (width - fm.stringWidth(getText())) / 2;
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(120, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setupStudentView() {
        txtRecID.setEditable(false);
        txtStudentID.setEditable(false);
        txtCourseID.setEditable(false);
        txtTimeLine.setEditable(false);
        txtDeadline.setEditable(false);
        cmbDescription.setEnabled(false);
        cmbStatus.setEnabled(false);

        txtStudentID.setText(loggedInUser.getUserID());

        List<Recommendation> allRecs = recommendationDAO.loadRecommendations();
        List<Recommendation> myRecs = allRecs.stream()
                .filter(r -> r.getStudentID().equalsIgnoreCase(loggedInUser.getUserID()))
                .collect(Collectors.toList());

        if (myRecs.isEmpty()) {
            Component[] comps = getContentPane().getComponents();
            for (Component c : comps) {
                if (c instanceof JPanel && ((JPanel)c).getLayout() instanceof CardLayout) {
                    ((CardLayout) ((JPanel)c).getLayout()).show((JPanel)c, "EMPTY");
                    break;
                }
            }
        } else {
            updateTable(myRecs);
        }
    }

    private void populateFieldsFromTable(int row) {
        txtRecID.setText((String) tableModel.getValueAt(row, 0));
        txtStudentID.setText((String) tableModel.getValueAt(row, 1));
        txtCourseID.setText((String) tableModel.getValueAt(row, 2));
        txtTimeLine.setText((String) tableModel.getValueAt(row, 3));
        txtDeadline.setText((String) tableModel.getValueAt(row, 4));
        cmbDescription.setSelectedItem((String) tableModel.getValueAt(row, 5));
        cmbStatus.setSelectedItem((String) tableModel.getValueAt(row, 6));
    }

    private void addRecommendation() {
        if (txtRecID.getText().isEmpty() || txtStudentID.getText().isEmpty() || txtCourseID.getText().isEmpty() || txtTimeLine.getText().isEmpty() || txtDeadline.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: All fields must be filled.");
            return;
        }

        if (isDuplicateRecID(txtRecID.getText())) {
            JOptionPane.showMessageDialog(this, "Error: Duplicate Recommendation ID.");
            return;
        }

        if (!validateDateLine()) return;
        if (!validateStudentID()) return;
        if (!validateCourseID()) return;

        try {
            String timeline = txtTimeLine.getText();
            Date deadline = dateFormat.parse(txtDeadline.getText());
            Recommendation recommendation = new Recommendation(
                    txtRecID.getText(), txtStudentID.getText(), txtCourseID.getText(),
                    (String) cmbDescription.getSelectedItem(), timeline, deadline,
                    (String) cmbStatus.getSelectedItem()
            );
            recommendationDAO.saveRecommendation(recommendation);
            JOptionPane.showMessageDialog(this, "Recommendation added successfully!");
            listRecommendation();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding recommendation: " + e.getMessage());
        }
    }

    private void updateRecommendation() {
        if (tblRecommendation.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Error: Please select a recommendation to update.");
            return;
        }
        if (!validateDateLine()) return;
        try {
            String timeline = txtTimeLine.getText();
            Date deadline = dateFormat.parse(txtDeadline.getText());
            Recommendation recommendation = new Recommendation(
                    txtRecID.getText(), txtStudentID.getText(), txtCourseID.getText(),
                    (String) cmbDescription.getSelectedItem(), timeline, deadline,
                    (String) cmbStatus.getSelectedItem()
            );
            recommendationDAO.updateRecommendation(
                    txtRecID.getText(), txtStudentID.getText(), txtCourseID.getText(), recommendation
            );
            JOptionPane.showMessageDialog(this, "Recommendation updated successfully!");
            listRecommendation();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating recommendation: " + e.getMessage());
        }
    }

    private void removeRecommendation() {
        if (tblRecommendation.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Error: Please select a recommendation to remove.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Remove this recommendation?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            recommendationDAO.removeRecommendation(txtRecID.getText(), txtStudentID.getText(), txtCourseID.getText());
            JOptionPane.showMessageDialog(this, "Recommendation removed successfully!");
            listRecommendation();
            clearFields();
        }
    }

    private void listRecommendation() {
        List<Recommendation> recommendations = recommendationDAO.loadRecommendations();
        updateTable(recommendations);
    }

    private void updateTable(List<Recommendation> recommendations) {
        tableModel.setRowCount(0);
        for (Recommendation r : recommendations) {
            tableModel.addRow(new Object[]{
                    r.getRecID(), r.getStudentID(), r.getCourseID(),
                    r.getTimeline(), dateFormat.format(r.getDeadline()),
                    r.getDescription(), r.getStatus()
            });
        }
    }

    private void clearFields() {
        txtRecID.setText(""); txtStudentID.setText(""); txtCourseID.setText("");
        txtTimeLine.setText(""); txtDeadline.setText("");
        cmbDescription.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
    }

    private boolean validateDateLine() {
        try{ dateFormat.parse(txtDeadline.getText()); } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid date format. Use YYYY-MM-DD.");
            return false;
        }
        return true;
    }

    public boolean validateStudentID() {
        StudentDAO studentDAO = new StudentDAO();
        boolean exists = studentDAO.loadAllStudents().stream().anyMatch(s -> s.getUserID().equals(txtStudentID.getText()));
        if (!exists) JOptionPane.showMessageDialog(this, "Error: Student ID does not exist.");
        return exists;
    }

    private boolean validateCourseID() {
        boolean exists = CourseCatalog.getInstance().getCourse(txtCourseID.getText()) != null;
        if (!exists) JOptionPane.showMessageDialog(this, "Error: Course ID does not exist.");
        return exists;
    }

    private boolean isDuplicateRecID(String recID) {
        return recommendationDAO.loadRecommendations().stream().anyMatch(m -> m.getRecID().equals(recID));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RecommendationEntry(null).setVisible(true);
        });
    }
}